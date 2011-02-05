package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

// クラス内クラスでスレッドオブジェクトを実装
// Thread オブジェクトを継承するか Runnable インターフェイスを実装する
class ReceiverThread extends Thread {
	Handler handler;
	volatile boolean toBeContinued;
	DatagramSocket datagramSocket;
	ArrayList<String> receivedLines;
	static final int MAX_LINES = 100;
	TextView textViewReceivedLines;

	synchronized public void StopThread() throws InterruptedException {
		toBeContinued = false;
		Thread.sleep(1000);
		if (datagramSocket != null) {
			datagramSocket.close();
		}
	}

	public ReceiverThread(Handler handler_, int port_, TextView text_view) {
		super();
		this.handler = handler_;
		this.toBeContinued = true;
		this.textViewReceivedLines = text_view;
		try {
			datagramSocket = new DatagramSocket(port_);
		} catch (SocketException e) {
			Log.e("ReceiverThread", "failed to open datagram socket.");
			return;
		}
	}

	public void run() {
		// 受け付けるデータバッファとUDPパケットを作成
		byte buffer[] = new byte[1024];
		DatagramPacket datagram_packet = new DatagramPacket(buffer,
				buffer.length);

		while (toBeContinued == true) {
			// UDPパケットを受信 ノンブロッキング処理にしたいが今はブロッキング処理
			try {
				datagramSocket.receive(datagram_packet);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("ReceiverThread", "failed to receive datagram packet.");
				return;
			}
			InetAddress inet_address = datagram_packet.getAddress();
			String sender_address = inet_address.getHostAddress();
			int sender_port = datagram_packet.getPort();
			String received_data = new String(datagram_packet.getData(), 0,
					datagram_packet.getLength());

			// 受信したデータをアレイリストに追加
			if (receivedLines.size() >= MAX_LINES) {
				receivedLines.remove(0);
			}
			receivedLines.add("[" + sender_address + ":" + sender_port + "]"
					+ received_data);

			// 匿名オブジェクトを使って擬似的なクロージャを実現するテクニック。
			handler.post(new Runnable() {
				public void run() {
					// テキストビューを更新
					String s = new String(); // ストリームを使うほうがスマート
					for (int i = receivedLines.size() - 1; i >= 0; --i) {
						s = s + receivedLines.get(i) + "\n";
					}
					textViewReceivedLines.setText(s);
				}
			});
		}

	}
}