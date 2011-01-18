package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import jp.ac.ehime_u.cite.sasaki.ReceiveUdp.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class SensorUdp extends Activity implements OnClickListener,
		SensorListener {
	private String destination_host;
	private int destination_port;
	// 本物のセンターを使う場合
	private SensorManager sensorManager;
	// センサーシミュレータを使う場合
	// private SensorManagerSimulator sensorManager;
	private TextView textViewAccelerometer;
	private TextView textViewMagneticField;
	private TextView textViewOrientation;
	private DatagramSocket datagramSocket;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button_send_udp = (Button) this
				.findViewById(R.id.ButtonSendToggle);
		button_send_udp.setOnClickListener(this);

		// スピナーの設定

		try {
			Inet4Addresses inet4_addresses = new Inet4Addresses();
			ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(this,
					R.layout.spinner, inet4_addresses.getBroadcastAddresses());
			//Spinner spinner = (Spinner) findViewById(R.id.SpinnerBroadcastAddress);
			//spinner.setAdapter(array_adapter);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Log.v("SensorUdp#onCreate", "SocketException");
		} catch (VerifyError e) {
			Log.v("SensorUdp#onCreate", e.toString());
		}

		// センサー情報表示用ビューの取得
		textViewAccelerometer = (TextView) findViewById(R.id.TextViewAccelerometer);
		textViewMagneticField = (TextView) findViewById(R.id.TextViewMagneticField);
		textViewOrientation = (TextView) findViewById(R.id.TextViewOrientation);

		// センサーの設定
		InitSensorManager();

		// ソケットを用意
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			// e.printStackTrace();
			Log.v("SensorUdp#onCreate", e.toString());
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonSendToggle:
			SendDebugMessageByUdp();
			Log.v("SensorUdp#onClick", "ButtonSendDebugMessage");
			break;
		}
	}

	private void SendDebugMessageByUdp() {
		EditText edit_text_host = (EditText) this
				.findViewById(R.id.EditTextHost);
		Editable editable_host = edit_text_host.getEditableText();
		this.destination_host = editable_host.toString();
		EditText edit_text_port = (EditText) this
				.findViewById(R.id.EditTextPort);
		Editable editable_port = edit_text_port.getEditableText();
		String string_port = editable_port.toString();
		this.destination_port = Integer.parseInt(string_port);
		EditText edit_text_debug_message = (EditText) this
				.findViewById(R.id.EditTextDebugMessage);
		Editable editable = edit_text_debug_message.getText();
		String string_to_be_sent = editable.toString() + "\n";
		SendMessageByUdp(string_to_be_sent);
	}

	private void SendMessageByUdp(String string_to_be_sent){
		try {
			byte[] byte_array = string_to_be_sent.getBytes();
			InetAddress inet_address = InetAddress.getByName(destination_host);
			DatagramPacket datagram_packet = new DatagramPacket(byte_array,
					byte_array.length, inet_address, destination_port);
			// DatagramSocket datagram_socket = new DatagramSocket();
			if (null == datagramSocket) {
				// datagramSocket.close();
				//datagramSocket = null;
				datagramSocket = new DatagramSocket();
			}
			datagramSocket.send(datagram_packet);
		} catch (IOException io_exception) {
			datagramSocket = null;
			Log.v("SensorUdp#SendDebugMessageByUdp", io_exception.toString());
		}
	}

	private void InitSensorManager() {
		// 本物のセンターを使う場合
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// センサーシミュレータを使う場合
		// sensorManager =
		// SensorManagerSimulator.getSystemService(context,Context.SENSOR_SERVICE);
		// sensorManager.connectSimulator();

		sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER
				| SensorManager.SENSOR_MAGNETIC_FIELD
				| SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_NORMAL);
		// SensorManager.SENSOR_DELAY_FASTEST 最高速度
		// SensorManager.SENSOR_DELAY_GAME ゲーム速度
		// SensorManager.SENSOR_DELAY_NORMAL 通常速度
		// SensorManager.SENSOR_DELAY_UI UI速度
	}

	public void onAccuracyChanged(int i, int j) {
		// TODO Auto-generated method stub
	}

	// 10進数固定小数点表示するためのフォーマットを行うクラス DecimalFormat
	private static final DecimalFormat decimal_format = new DecimalFormat(
			"000.0000000");

	public void onSensorChanged(int sensor, float[] values) {
		switch (sensor) {
		case SensorManager.SENSOR_ACCELEROMETER: {
			// 加速度センサーの値を表示
			Date date = new Date();
			String accelerometer_cvs_line = "A, " + date.getTime() + ", "
					+ decimal_format.format(values[0]) + ", "
					+ decimal_format.format(values[1]) + ", "
					+ decimal_format.format(values[2]);
			textViewAccelerometer.setText(accelerometer_cvs_line);
			SendMessageByUdp(accelerometer_cvs_line + "\n");
		}
			break;
		case SensorManager.SENSOR_MAGNETIC_FIELD: {
			// 磁気センサーの値を表示
			Date date = new Date();
			String magnetic_field_cvs_line = "M, " + date.getTime() + ", "
					+ decimal_format.format(values[0]) + ", "
					+ decimal_format.format(values[1]) + ", "
					+ decimal_format.format(values[2]);
			textViewMagneticField.setText(magnetic_field_cvs_line);
			SendMessageByUdp(magnetic_field_cvs_line + "\n");
		}
			break;
		case SensorManager.SENSOR_ORIENTATION: {
			Date date = new Date();
			String orientation_cvs_line = "O, " + date.getTime() + ", "
					+ decimal_format.format(values[0]) + ", "
					+ decimal_format.format(values[1]) + ", "
					+ decimal_format.format(values[2]);
			textViewOrientation.setText(orientation_cvs_line);
			SendMessageByUdp(orientation_cvs_line + "\n");
		}
			break;
		}// switchの終わり
	}
}