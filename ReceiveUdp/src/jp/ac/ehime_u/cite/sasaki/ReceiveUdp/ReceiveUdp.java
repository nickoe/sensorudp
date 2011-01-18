package jp.ac.ehime_u.cite.sasaki.ReceiveUdp;

import java.awt.FlowLayout;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ReceiveUdp extends JFrame {
	private static final int PORT = 12345;

	private final static String TITLE = "ReceiveUdp by Takashi SASAKI";
	private final static String OPENING = "ReceiveUdp v1.0 by Takashi SASAKI, 2011\n"
			+ "Listening all interfaces for " +PORT+"/udp.\n"
			+ "-------------------------------------------\n";
	private JTextArea jTextArea;
	private JLabel jLabel;

	public ReceiveUdp() {
		// �t���[���Ƀ��C�A�E�g��ݒ�
		getContentPane().setLayout(new FlowLayout());

		// �e�L�X�g�G���A���쐬
		jTextArea = new JTextArea(OPENING, 30, 40) {
			// append���\�b�h���I�[�o�[���C�h���ď�ɍŏI�s���\�������悤�ɂ���
			@Override
			public void append(String str) {
				super.append(str);
				jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
			}
		};
		jTextArea.setLineWrap(true);

		// �t���[���ɃX�N���[���y�C����ݒ�
		JScrollPane j_scroll_pane = new JScrollPane(jTextArea);

		getContentPane().add(j_scroll_pane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(TITLE);
		// setSize(300, 400);
		pack();
		setVisible(true);
		jLabel = new JLabel();
		getContentPane().add(jLabel);
		jLabel.setText("" + PORT);
	}

	public static void main(String[] argv) throws Exception {
		// Swing�̃t���[����\��
		ReceiveUdp receive_udp = new ReceiveUdp();

		// ���C���h�J�[�h�A�h���X�ő҂���
		DatagramSocket datagram_socket = new DatagramSocket(PORT);

		// �󂯕t����f�[�^�o�b�t�@��UDP�p�P�b�g���쐬
		byte buffer[] = new byte[1024];
		DatagramPacket datagram_packet = new DatagramPacket(buffer,
				buffer.length);

		while (true) {
			// UDP�p�P�b�g����M
			datagram_socket.receive(datagram_packet);
			InetAddress inet_address = datagram_packet.getAddress();
			String sender_address = inet_address.getHostAddress();
			int sender_port = datagram_packet.getPort();
			String received_data = new String(datagram_packet.getData(), 0,
					datagram_packet.getLength());
			// �\�����镶������쐬
			String s = "[" + sender_address + ":" + sender_port + "]"
					+ received_data;
			// ��M�����f�[�^��W���o�͂֏o��
			System.out.println(s);
			// JTextArea�ɂ��\��
			receive_udp.jTextArea.append(s);
		}
	}
}
