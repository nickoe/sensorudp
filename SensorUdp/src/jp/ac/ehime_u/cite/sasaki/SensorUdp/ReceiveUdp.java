// システムデザイン 第14回 2011年1月31日

package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private ReceiverThread receiverThread; // 受信スレッド
	private static final int PORT = 12345; // 受信するUDPポート番号（定数）

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receiveudp);

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
					byte[] byte_array = "test message from ReceiveUdp activity".getBytes();
					InetAddress inet_address = InetAddress
							.getByName("127.0.0.1");
					DatagramPacket datagram_packet = new DatagramPacket(
							byte_array, byte_array.length, inet_address, 12345);
					// DatagramSocket datagram_socket = new DatagramSocket();
					datagramSocket.send(datagram_packet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// スレッドオブジェクトを生成しスタート
		receiverThread = ReceiverThread.GetSingleton();
		receiverThread.start(new Handler(), PORT, textViewReceivedLines);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		receiverThread.interrupt();
		receiverThread = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menu_infrator = getMenuInflater();
		menu_infrator.inflate(R.menu.receiveudp, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.itemReceiveUdpToSensorUdp: {
			Intent intent = new Intent(this, SensorUdp.class);
			startActivity(intent);
			return true;
		}
		}
		return false;
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
