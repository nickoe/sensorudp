package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import java.net.SocketException;

import jp.ac.ehime_u.cite.sasaki.ReceiveUdp.Inet4Addresses;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Interfaces extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interfaces);
		Button b = (Button) findViewById(R.id.ButtonInterfaces);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		try {
			Inet4Addresses inet4_addresses = new Inet4Addresses();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
