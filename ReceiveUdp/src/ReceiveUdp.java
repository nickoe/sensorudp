import java.net.*;

public class ReceiveUdp {
	private static final int PORT = 12345;

	public static void main(String[] argv) throws Exception {
		// ワイルドカードアドレスで待ち受け
		DatagramSocket datagram_socket = new DatagramSocket(PORT);

		// 受け付けるデータバッファとUDPパケットを作成
		byte buffer[] = new byte[1024];
		DatagramPacket datagram_packet = new DatagramPacket(buffer,
				buffer.length);

		while (true) {
			// UDPパケットを受信
			datagram_socket.receive(datagram_packet);
			InetAddress inet_address = datagram_packet.getAddress();
			String host_address = inet_address.getHostAddress();
			String received_data = new String(datagram_packet.getData(),0, datagram_packet.getLength());
			// 受信したデータを標準出力へ出力
			System.out.println("["+host_address+"]"+received_data);
		}
	}
}
