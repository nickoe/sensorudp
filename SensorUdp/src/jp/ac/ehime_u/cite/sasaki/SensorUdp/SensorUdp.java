package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

public class SensorUdp extends Activity implements OnClickListener {
	private String destination_host;
	private int destination_port;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button_send_udp = (Button) this
				.findViewById(R.id.ButtonSendDebugMessage);
		button_send_udp.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonSendDebugMessage:
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
		try {
			Editable editable = edit_text_debug_message.getText();
			String string = editable.toString();
			byte[] byte_array = string.getBytes();
			InetAddress inet_address = InetAddress.getByName(destination_host);
			DatagramPacket datagram_packet = new DatagramPacket(byte_array,
					byte_array.length, inet_address, destination_port);
			DatagramSocket datagram_socket = new DatagramSocket();
			datagram_socket.send(datagram_packet);
			datagram_socket.close();
		} catch (IOException io_exception) {
			Log.v("SensorUdp#SendDebugMessageByUdp", io_exception.toString());
		}
	}
}