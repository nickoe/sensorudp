// システムデザイン 第14回 2011年1月31日

package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import android.R.bool;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

// 前回の課題
//  このアクティビティで、
//  受信したUDPパケットの内容を逐次表示するようにして下さい。
//  最新100行程度表示出来ればOKです。
//  他のアクティビティの表示中にも受信できるよう、
//  メインスレッドとは別のスレッドで受信するようにしましょう。
//  UDPパケットの受信とUDPデータグラムからのデータの取り出しは
//  ReceiveUdp プロジェクトの RecieveUdp.java を参考にしてください。


// 解答例
//  実はまだ途中。マルチスレッドを使ったコードを解説します。
//  授業中に解説しながら一緒に仕上げて行こうと思います。

public class ReceiveUdp extends Activity {

	private TextView textViewReceivedLines; // 受信した行を表示するビュー
	private Thread receiverThread; // 受信スレッド
	private boolean receiving; // 受信スレッドではここを見ながらループ
	private DatagramSocket datagram_socket; // 受信UDPポート用ソケット
	private static final int PORT = 12345; // 受信するUDPポート番号（定数）
	private static final int MAX_LINES = 50; // 保存する行の上限
	private ArrayList<String> receivedLines; // 受信した行を格納するアレイリスト

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receiveudp);
		try {
			datagram_socket = new DatagramSocket(PORT);
		} catch (SocketException e) {
			Log.e("ReceiverThread", "failed to open datagram socket.");
			return;
		}

		receivedLines = new ArrayList<String>();

		// テキストビューを取得しておく
		textViewReceivedLines = (TextView) findViewById(R.id.textViewReceivedLines);

		// 匿名オブジェクトを使ってクロージャっぽくイベントハンドラを実装
		Button b = (Button) findViewById(R.id.ButtonReceiveUdp);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// finish();
				DatagramSocket datagramSocket;
				try {
					datagramSocket = new DatagramSocket();
					byte[] byte_array = "aaaa".getBytes();
					InetAddress inet_address = InetAddress
							.getByName("127.0.0.1");
					DatagramPacket datagram_packet = new DatagramPacket(
							byte_array, byte_array.length, inet_address, 12345);
					// DatagramSocket datagram_socket = new DatagramSocket();
					datagramSocket.send(datagram_packet);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// クラス内クラスでスレッドオブジェクトを実装
		// Thread オブジェクトを継承するか Runnable インターフェイスを実装する
		class ReceiverThread extends Thread {
			Handler handler;

			public ReceiverThread(Handler handler){
				super();
				this.handler = handler;
			}

			public void run() {
				// 受け付けるデータバッファとUDPパケットを作成
				byte buffer[] = new byte[1024];
				DatagramPacket datagram_packet = new DatagramPacket(buffer,
						buffer.length);

				while (receiving == true) {
					// UDPパケットを受信 ノンブロッキング処理にしたいが今はブロッキング処理
					try {
						datagram_socket.receive(datagram_packet);
					} catch (IOException e) {
						e.printStackTrace();
						Log.e("ReceiverThread",
								"failed to receive datagram packet.");
						return;
					}
					InetAddress inet_address = datagram_packet.getAddress();
					String sender_address = inet_address.getHostAddress();
					int sender_port = datagram_packet.getPort();
					String received_data = new String(
							datagram_packet.getData(), 0,
							datagram_packet.getLength());

					// 受信したデータをアレイリストに追加
					if (receivedLines.size() >= MAX_LINES) {
						receivedLines.remove(0);
					}
					receivedLines.add("[" + sender_address + ":" + sender_port
							+ "]" + received_data);

					//匿名オブジェクトを使って擬似的なクロージャを実現するテクニック。
					handler.post(new Runnable(){
						public void run(){
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

		// スレッドオブジェクトを生成しスタート
		receiving = true;
		receiverThread = new ReceiverThread(new Handler());
		receiverThread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		receiving = false;
		if (datagram_socket != null) {
			datagram_socket.close();
		}
		if (receiverThread != null) {
			if (receiverThread.isAlive()) {
				receiverThread.interrupt();
			}
		}
	}
}

// 次回 1人5分程度のプレゼンテーション（PowerPoint）
// プレゼンテーションに含める内容
//
// 1. Android の構成（カーネル、ミドルウェア、DalvikVMなど）
// 2. 今回作った SensorUdp の作り方と動作の解説
// 3. 発展させて何に使えそうか？
// 4. 改良するとすればどんな点？
// 5. 授業で興味を持てた点、持てなかった点
// 6. 今後作ってみたいアプリケーションがあればアイディアを
//
