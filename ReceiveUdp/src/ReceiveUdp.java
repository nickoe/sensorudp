import java.net.*;

public class ReceiveUdp {
	private static final int PORT = 12345;

	public static void main(String[] argv) throws Exception {
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
			String host_address = inet_address.getHostAddress();
			String received_data = new String(datagram_packet.getData(),0, datagram_packet.getLength());
			// ��M�����f�[�^��W���o�͂֏o��
			System.out.println("["+host_address+"]"+received_data);
		}
	}
}