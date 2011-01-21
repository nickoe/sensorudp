package jp.ac.ehime_u.cite.sasaki.SensorUdp;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;

@SuppressWarnings("deprecation")
public class SensorUdp extends Activity implements SensorListener,
		LocationListener {
	private String destination_host;
	private int destination_port;
	// 本物のセンターを使う場合
	private SensorManager sensorManager;
	// センサーシミュレータを使う場合
	// private SensorManagerSimulator sensorManager;
	private EditText editTextHost;
	private EditText editTextPort;
	private TextView textViewAccelerometer;
	private TextView textViewMagneticField;
	private TextView textViewOrientation;
	private CheckBox checkBoxAccelerometer;
	private CheckBox checkBoxMagneticField;
	private CheckBox checkBoxOrientation;
	private RadioButton radioButtonFastest;
	private RadioButton radioButtonGame;
	private RadioButton radioButtonNormal;
	private RadioButton radioButtonUi;
	private RadioGroup radioGroup;
	private DatagramSocket datagramSocket;
	private CheckBox checkBoxGps;
	private EditText editTextGpsMinInterval;
	private EditText editTextGpsMinDistance;
	private TextView textViewGps;
	private CheckBox checkBoxNetwork;
	private EditText editTextNetworkMinInterval;
	private EditText editTextNetworkMinDistance;
	private TextView textViewNetwork;
	private EditText editTextLiteral;
	private Button buttonLiteral;

	// センサー情報取得カウンター
	private int counterAccelerometer;
	private int counterMagneticField;
	private int counterOrientation;
	private int counterNetwork;
	private int counterGps;
	private int counterLiteral;

	private LocationManager locationManager;

	private void FindViews() {
		editTextHost = (EditText) this.findViewById(R.id.EditTextHost);
		editTextPort = (EditText) this.findViewById(R.id.EditTextPort);
		textViewAccelerometer = (TextView) findViewById(R.id.TextViewAccelerometer);
		textViewMagneticField = (TextView) findViewById(R.id.TextViewMagneticField);
		textViewOrientation = (TextView) findViewById(R.id.TextViewOrientation);
		checkBoxAccelerometer = (CheckBox) findViewById(R.id.CheckBoxAccelerometer);
		checkBoxMagneticField = (CheckBox) findViewById(R.id.CheckBoxMagneticField);
		checkBoxOrientation = (CheckBox) findViewById(R.id.CheckBoxOrientation);
		radioButtonFastest = (RadioButton) findViewById(R.id.RadioButtonFastest);
		radioButtonGame = (RadioButton) findViewById(R.id.RadioButtonGame);
		radioButtonNormal = (RadioButton) findViewById(R.id.RadioButtonNormal);
		radioButtonUi = (RadioButton) findViewById(R.id.RadioButtonUi);
		radioGroup = (RadioGroup) findViewById(R.id.RadioGroupDelay);
		checkBoxGps = (CheckBox) findViewById(R.id.CheckBoxGps);
		editTextGpsMinDistance = (EditText) findViewById(R.id.EditTextGpsMinDistance);
		editTextGpsMinInterval = (EditText) findViewById(R.id.EditTextGpsMinInterval);
		textViewGps = (TextView) findViewById(R.id.TextViewGps);
		checkBoxNetwork = (CheckBox) findViewById(R.id.CheckBoxNetwork);
		editTextNetworkMinDistance = (EditText) findViewById(R.id.EditTextNetworkMinDistance);
		editTextNetworkMinInterval = (EditText) findViewById(R.id.EditTextNetworkMinInterval);
		textViewNetwork = (TextView) findViewById(R.id.TextViewNetwork);
		editTextLiteral = (EditText) this.findViewById(R.id.EditTextLiteral);
		buttonLiteral = (Button) this.findViewById(R.id.ButtonLiteral);
	}

	private void SetListeners() {
		// 送信先IPアドレスが変更された時にはパケット送出を停止
		editTextHost.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
				checkBoxAccelerometer.setChecked(false);
				checkBoxMagneticField.setChecked(false);
				checkBoxOrientation.setChecked(false);
				checkBoxGps.setChecked(false);
				checkBoxNetwork.setChecked(false);
				ChangeLocationProvider();
				ChangeDestination();
				return true;
			}
		});
		// 送信先ポートが変更された時にはパケット送出を停止
		editTextPort.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView textview, int i, KeyEvent keyevent) {
				checkBoxAccelerometer.setChecked(false);
				checkBoxMagneticField.setChecked(false);
				checkBoxOrientation.setChecked(false);
				checkBoxGps.setChecked(false);
				checkBoxNetwork.setChecked(false);
				ChangeLocationProvider();
				return true;
			}
		});
		// 加速度センサーの使用可否が変更された場合
		checkBoxAccelerometer
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						counterAccelerometer = 0;
					}
				});
		// 磁気センサーの使用可否が変更された場合
		checkBoxMagneticField
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						counterMagneticField = 0;
					}
				});
		// 方向センサーの使用可否が変更された場合
		checkBoxOrientation
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						counterOrientation = 0;
					}
				});
		// 六軸センサー情報の取得頻度が変更された場合
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						UnregisterSensorListener();
						RegisterSensorListener();
						Log.v("SensorUdp", "Delay changed");
					}
				});
		// GPSによる測位の可否が変更された場合
		checkBoxGps.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				counterGps = 0;
				ChangeLocationProvider();
			}
		});
		// GPSによる測位の最短移動距離が変更された場合
		editTextGpsMinDistance
				.setOnEditorActionListener(new OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						counterGps = 0;
						checkBoxGps.setChecked(false);
						ChangeLocationProvider();
						return true;
					}
				});
		// GPSによる測位の最短時間間隔が変更された場合
		editTextGpsMinInterval
				.setOnEditorActionListener(new OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						counterGps = 0;
						checkBoxGps.setChecked(false);
						ChangeLocationProvider();
						return true;
					}
				});
		// Networkを使った測位の可否が変更された場合
		checkBoxNetwork
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						counterNetwork = 0;
						ChangeLocationProvider();
					}
				});
		// Networkによる測位の最短移動距離が変更された場合
		editTextNetworkMinDistance
				.setOnEditorActionListener(new OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						counterNetwork = 0;
						checkBoxNetwork.setChecked(false);
						ChangeLocationProvider();
						return true;
					}
				});
		// Networkによる測位の最短時間間隔が変更された場合
		editTextNetworkMinInterval
				.setOnEditorActionListener(new OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						counterNetwork = 0;
						checkBoxNetwork.setChecked(false);
						ChangeLocationProvider();
						return true;
					}
				});
		// 任意文字列の送信ボタンが押下された場合
		buttonLiteral.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SendLiteralByUdp();
				Log.v("SensorUdp#onClick", "ButtonSendDebugMessage");
			}
		});
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		onRestoreInstanceState(savedInstanceState);
		// ビューの取得
		FindViews();

		// ロケーションマネージャ生成
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// センサーマネージャーの生成
		// 本物のセンターを使う場合
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// センサーシミュレータを使う場合
		// sensorManager =
		// SensorManagerSimulator.getSystemService(context,Context.SENSOR_SERVICE);
		// sensorManager.connectSimulator();
		RegisterSensorListener();

		// ソケットを用意
		ChangeDestination();
		// ビューへのイベントハンドラの設定
		SetListeners();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		Log.d("SensorUdp", "onSaveInstanceState");
		outState.putString("editTextHost", editTextHost.getEditableText().toString());
		outState.putString("editTextPort", editTextPort.getEditableText().toString());
		outState.putString("editTextGpsMinDistance", editTextGpsMinDistance.getEditableText().toString());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		Log.d("SensorUdp", "onRestoreInstanceState");
		String edit_text_host = savedInstanceState.getString("editTextHost");
		if (edit_text_host != null) {
			editTextHost.setText(edit_text_host, BufferType.EDITABLE);
		}
		String edit_text_port = savedInstanceState.getString("editTextPort");
		if (edit_text_port != null) {
			editTextPort.setText(edit_text_port, BufferType.EDITABLE);
		}
	}

	void ChangeDestination(){
		try {
			datagramSocket = null;
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			// e.printStackTrace();
			Log.v("SensorUdp#onCreate", e.toString());
		}
	}

	void ChangeLocationProvider() {
		locationManager.removeUpdates(this);
		if (checkBoxGps.isChecked()) {
			try {
				int min_distance = Integer.parseInt(editTextGpsMinDistance
						.getEditableText().toString());
				int min_interval = Integer.parseInt(editTextGpsMinInterval
						.getEditableText().toString());
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, min_interval,
						min_distance, this);
			} catch (NumberFormatException e) {
				checkBoxGps.setChecked(false);
				Log.d("SensorUdp",e.toString());
			} catch(IllegalArgumentException e){
				checkBoxGps.setChecked(false);
				Log.d("SensorUdp",e.toString());
			}
		}
		if (checkBoxNetwork.isChecked()) {
			try {
				int min_distance = Integer.parseInt(editTextNetworkMinDistance
						.getEditableText().toString());
				int min_interval = Integer.parseInt(editTextNetworkMinInterval
						.getEditableText().toString());
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, min_interval,
						min_distance, this);
			} catch (NumberFormatException e) {
				checkBoxNetwork.setChecked(false);
				Log.d("SensorUdp",e.toString());
			} catch (IllegalArgumentException e){
				checkBoxNetwork.setChecked(false);
				Log.d("SensorUdp",e.toString());
			}
		}
	}

	private void SendLiteralByUdp() {
		Editable editable_host = editTextHost.getEditableText();
		this.destination_host = editable_host.toString();
		Editable editable_port = editTextPort.getEditableText();
		String string_port = editable_port.toString();
		this.destination_port = Integer.parseInt(string_port);
		Editable editable = editTextLiteral.getText();
		String string_to_be_sent = editable.toString() + "\n";
		++counterLiteral;
		Date date = new Date();
		SendMessageByUdp("D, " + counterLiteral + ", " + date.getTime() + ", "
				+ string_to_be_sent);
	}

	private void SendMessageByUdp(String string_to_be_sent) {
		try {
			byte[] byte_array = string_to_be_sent.getBytes();
			InetAddress inet_address = InetAddress.getByName(destination_host);
			DatagramPacket datagram_packet = new DatagramPacket(byte_array,
					byte_array.length, inet_address, destination_port);
			// DatagramSocket datagram_socket = new DatagramSocket();
			if (null == datagramSocket) {
				// datagramSocket.close();
				// datagramSocket = null;
				datagramSocket = new DatagramSocket();
			}
			datagramSocket.send(datagram_packet);
		} catch (IOException io_exception) {
			datagramSocket = null;
			Log.v("SensorUdp#SendDebugMessageByUdp", io_exception.toString());
		}
	}

	private void RegisterSensorListener() {
		int sensor_delay;
		if (radioButtonFastest.isChecked()) {
			sensor_delay = SensorManager.SENSOR_DELAY_FASTEST;
		} else if (radioButtonGame.isChecked()) {
			sensor_delay = SensorManager.SENSOR_DELAY_GAME;
		} else if (radioButtonNormal.isChecked()) {
			sensor_delay = SensorManager.SENSOR_DELAY_NORMAL;
		} else if (radioButtonUi.isChecked()) {
			sensor_delay = SensorManager.SENSOR_DELAY_UI;
		} else {
			sensor_delay = SensorManager.SENSOR_DELAY_UI;
		}
		sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER
				| SensorManager.SENSOR_MAGNETIC_FIELD
				| SensorManager.SENSOR_ORIENTATION, sensor_delay);
		// SensorManager.SENSOR_DELAY_FASTEST 最高速度
		// SensorManager.SENSOR_DELAY_GAME ゲーム速度
		// SensorManager.SENSOR_DELAY_NORMAL 通常速度
		// SensorManager.SENSOR_DELAY_UI UI速度
	}

	private void UnregisterSensorListener() {
		sensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(int i, int j) {
	}

	// 10進数固定小数点表示するためのフォーマットを行うクラス DecimalFormat
	private static final DecimalFormat decimal_format = new DecimalFormat(
			"000.0000000");

	public void onSensorChanged(int sensor, float[] values) {
		switch (sensor) {
		case SensorManager.SENSOR_ACCELEROMETER: {
			if (checkBoxAccelerometer.isChecked()) {
				// 加速度センサーの値を表示
				Date date = new Date();
				String accelerometer_cvs_line = "A, " + ++counterAccelerometer
						+ ", " + date.getTime() + ", "
						+ decimal_format.format(values[0]) + ", "
						+ decimal_format.format(values[1]) + ", "
						+ decimal_format.format(values[2]);
				textViewAccelerometer.setText(accelerometer_cvs_line);
				SendMessageByUdp(accelerometer_cvs_line + "\n");
			}
		}
			break;
		case SensorManager.SENSOR_MAGNETIC_FIELD: {
			if (checkBoxMagneticField.isChecked()) {
				// 磁気センサーの値を表示
				Date date = new Date();
				String magnetic_field_cvs_line = "M, " + ++counterMagneticField
						+ ", " + date.getTime() + ", "
						+ decimal_format.format(values[0]) + ", "
						+ decimal_format.format(values[1]) + ", "
						+ decimal_format.format(values[2]);
				textViewMagneticField.setText(magnetic_field_cvs_line);
				SendMessageByUdp(magnetic_field_cvs_line + "\n");
			}
		}
			break;
		case SensorManager.SENSOR_ORIENTATION: {
			if (checkBoxOrientation.isChecked()) {
				Date date = new Date();
				String orientation_cvs_line = "O, " + ++counterOrientation
						+ ", " + date.getTime() + ", "
						+ decimal_format.format(values[0]) + ", "
						+ decimal_format.format(values[1]) + ", "
						+ decimal_format.format(values[2]);
				textViewOrientation.setText(orientation_cvs_line);
				SendMessageByUdp(orientation_cvs_line + "\n");
			}
		}
			break;
		}// switchの終わり
	}

	public void onLocationChanged(Location location) {
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			Date date = new Date();
			String location_by_gps_cvs_string = "G, " + ++counterGps + ", "
					+ date.getTime() + ", "
					+ decimal_format.format(location.getAltitude()) + ", "
					+ decimal_format.format(location.getLatitude()) + ", "
					+ decimal_format.format(location.getLongitude()) + ", "
					+ location.getTime() + ", "
					+ decimal_format.format(location.getAccuracy()) + ", "
					+ decimal_format.format(location.getSpeed()) +"\n";
			textViewGps.setText(location_by_gps_cvs_string);
			SendMessageByUdp(location_by_gps_cvs_string);
		} else if (location.getProvider().equals(
				LocationManager.NETWORK_PROVIDER)) {
			Date date = new Date();
			String location_by_network_cvs_string = "N, " + ++counterNetwork
					+ ", " + date.getTime() + ", "
					+ decimal_format.format(location.getAltitude()) + ", "
					+ decimal_format.format(location.getLatitude()) + ", "
					+ decimal_format.format(location.getLongitude()) + ", "
					+ location.getTime() + ", "
					+ decimal_format.format(location.getAccuracy()) + ", "
					+ decimal_format.format(location.getSpeed()) + "\n";
			textViewNetwork.setText(location_by_network_cvs_string);
			SendMessageByUdp(location_by_network_cvs_string);
		} else {
			Log.v("SensorUdp", "Unknown provider " + location.getProvider());
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
