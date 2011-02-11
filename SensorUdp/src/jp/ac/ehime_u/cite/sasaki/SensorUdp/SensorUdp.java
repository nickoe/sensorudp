package jp.ac.ehime_u.cite.sasaki.SensorUdp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SensorUdp extends Activity {
	MySensorEventListener mySensorEventListener;
	MyLocationListener myLocationListener;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); // ビューの生成

		mySensorEventListener = MySensorEventListener.GetSingleton(this);
		myLocationListener = MyLocationListener.GetSingleton(this);
	}

	@Override
	protected void onPause() {
		mySensorEventListener.UncheckAll();
		myLocationListener.UncheckAll();
		super.onPause();
	}

	@Override
	protected void onStop() {
		mySensorEventListener.UncheckAll();
		myLocationListener.UncheckAll();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		mySensorEventListener.UncheckAll();
		myLocationListener.UncheckAll();
		super.onDestroy();
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
		case R.id.itemAbout: {
			Intent intent_about = new Intent(this, About.class);
			startActivity(intent_about);
			return true;
		}
		case R.id.itemReceiveUdp: {
			Intent intent_receive_udp = new Intent(this, ReceiveUdp.class);
			startActivity(intent_receive_udp);
			return true;
		}
			// case R.id.itemInterfaces: {
			// Intent intent_interfaces = new Intent(this, Interfaces.class);
			// startActivity(intent_interfaces);
			// return true;
			// }
		}
		return false;
	}

}
