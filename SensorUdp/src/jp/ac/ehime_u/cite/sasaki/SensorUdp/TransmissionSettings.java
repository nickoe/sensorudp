package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.net.SocketException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;

public class TransmissionSettings extends Activity {

	TextView textViewReceivedLines; // 受信した行を表示するビュー
	ReceiverThread receiverThread; // 受信スレッド
	EditText editTextLiteral;
	EditText editTextHost;
	EditText editTextPort;
	int counterLiteral;
	Button buttonLiteral;
	SenderThread senderThread;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transmission_settings);

		// テキストビューを取得しておく
		textViewReceivedLines = (TextView) findViewById(R.id.textViewReceivedLines);
		editTextLiteral = (EditText) this.findViewById(R.id.EditTextLiteral);

		// 任意文字列の送信ボタンが押下された場合
		buttonLiteral = (Button) this.findViewById(R.id.ButtonLiteral);
		buttonLiteral.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				++counterLiteral;
				Date date = new Date();
				senderThread.SendMessageByUdp("D, " + counterLiteral + ", "
						+ date.getTime() + ", "
						+ editTextLiteral.getText().toString());
			}
		});

		// 送信先IPアドレスが変更された時にはパケット送出を停止
		editTextHost = (EditText) findViewById(R.id.EditTextHost);
		editTextHost.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView textview, int i,
					KeyEvent keyevent) {
				MySensorEventListener.GetSingleton().UncheckAll();
				MyLocationListener.GetSingleton().UncheckAll();
				try {
					SenderThread.GetSingleton(editTextHost.getEditableText()
							.toString(), Integer.parseInt(editTextPort
							.getEditableText().toString()));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});
		// 送信先ポートが変更された時にはパケット送出を停止
		editTextPort = (EditText) findViewById(R.id.EditTextPort);
		editTextPort.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView textview, int i,
					KeyEvent keyevent) {
				MySensorEventListener.GetSingleton().UncheckAll();
				MyLocationListener.GetSingleton().UncheckAll();
				try {
					SenderThread.GetSingleton(editTextHost.getEditableText()
							.toString(), Integer.parseInt(editTextPort
							.getEditableText().toString()));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});

		try {
			senderThread = SenderThread.GetSingleton(editTextHost
					.getEditableText().toString(), Integer
					.parseInt(editTextPort.getEditableText().toString()));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// スレッドオブジェクトを生成しスタート
		receiverThread = ReceiverThread.GetSingleton(this);
		receiverThread.start(new Handler());
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
		menu_infrator.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.itemSensorSettings: {
			Intent intent = new Intent(this, SensorUdp.class);
			startActivity(intent);
			return true;
		}
		case R.id.itemTransmissionSettings:{
			Intent intent = new Intent(this, TransmissionSettings.class);
			startActivity(intent);
			return true;
		}
		case R.id.itemAbout:{
			Intent intent = new Intent(this, About.class);
			startActivity(intent);
			return true;
		}
		}
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (outState == null)
			return;
		outState.putString(editTextHost.toString(), editTextHost.getText()
				.toString());
		outState.putString(editTextPort.toString(), editTextPort.getText()
				.toString());
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState == null)
			return;
		String edit_text_host = savedInstanceState.getString(editTextHost
				.toString());
		if (edit_text_host != null) {
			editTextHost.setText(edit_text_host, BufferType.EDITABLE);
		}
		String edit_text_port = savedInstanceState.getString(editTextPort
				.toString());
		if (edit_text_port != null) {
			editTextPort.setText(edit_text_port, BufferType.EDITABLE);
		}
	}
}
