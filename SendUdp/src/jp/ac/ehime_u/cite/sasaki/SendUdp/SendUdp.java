package jp.ac.ehime_u.cite.sasaki.SendUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendUdp extends Activity implements OnClickListener{
	private String destination_host;
	private int destination_port;
	private DatagramSocket datagramSocket;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button button_send_udp = (Button) this
				.findViewById(R.id.ButtonSendToggle);
		button_send_udp.setOnClickListener(this);

		// �\�P�b�g��p��
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			//e.printStackTrace();
			Log.v("SendUdp#onCreate", e.toString());
		}
	}

	//���M�{�^���������ꂽ�Ƃ��ɌĂяo����郁�\�b�h
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonSendToggle:
			SendDebugMessageByUdp();
			Log.v("SensorUdp#onClick", "ButtonSendDebugMessage");
			break;
		}
	}
	
	// ���ۂ�UDP�f�[�^�O�����𑗐M���郁�\�b�h
	// onClick ����Ăяo����邾���ŁA�N���X�̊O����Ăяo����邱�Ƃ͖������߁A
	// private �A�N�Z�X�w��q�ŃA�N�Z�X�𐧌����Ă���B
	private void SendDebugMessageByUdp() {		
		//EditText�r���[����̈���z�X�g�A�h���X�̎擾
		EditText edit_text_host = (EditText) this
				.findViewById(R.id.EditTextHost);
				Editable editable_host = edit_text_host.getEditableText();		
		this.destination_host = editable_host.toString();
		
		//EditText�r���[����̈���UDP�|�[�g�ԍ��̎擾
		EditText edit_text_port = (EditText) this
				.findViewById(R.id.EditTextPort);
		Editable editable_port = edit_text_port.getEditableText();
		String string_port = editable_port.toString();
		this.destination_port = Integer.parseInt(string_port);
		
		//EditText�r���[����̃��b�Z�[�W�̎擾
		EditText edit_text_debug_message = (EditText) this
				.findViewById(R.id.EditTextDebugMessage);
		Editable editable = edit_text_debug_message.getText();
		String string = editable.toString();
		byte[] byte_array = string.getBytes();

		try {
			//IP�A�h���X�� InetAddress �N���X�ŕ\������
			InetAddress inet_address = InetAddress.getByName(destination_host);
			
			//UDP�f�[�^�O������ DatagramPacket �N���X�ŕ\������
			DatagramPacket datagram_packet = new DatagramPacket(byte_array,
					byte_array.length, inet_address, destination_port);
			
			//DatagramSocket datagram_socket = new DatagramSocket();
			//�l�b�g���[�N���o�͌��̓\�P�b�g�Ƃ��Ē��ۉ������
			if(null != datagramSocket){
				//datagramSocket.close();
				datagramSocket = null;
				datagramSocket = new DatagramSocket();
			}
			//DatagramSocket �� DatagramPacket ��n������
			//�w�肳�ꂽ����A�h���X�� UDP �f�[�^�O�����Ƃ��đ��o����B
			datagramSocket.send(datagram_packet);
		} catch (IOException io_exception) {
			//��肪�N�������O�𑨂��ă��O�ɏo��
			Log.v("SensorUdp#SendDebugMessageByUdp", io_exception.toString());
		}
	}
}
