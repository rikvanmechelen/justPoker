package be.infogroep.justpoker;

import edu.vub.at.commlib.CommLib;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ServerTableActivity extends Activity {

	private PokerServer cps;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_table);
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		//intent = new Intent(this, PokerServer.class);
		//startService(intent);
		//cps = PokerServer.getInstance();
		String ipAddress = CommLib.getIpAddress(this);
		cps = PokerServer.getInstance(ServerTableActivity.this, ipAddress);
		cps.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_server_table, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		//startService(intent);
		//registerReceiver(broadcastReceiver, new IntentFilter(
		//		PokerServer.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		//unregisterReceiver(broadcastReceiver);
		//stopService(intent);
	}

	public void stopServer(MenuItem m) {
		//Intent intent = new Intent(this, ServerActivity.class);
		//cps.stop();
		//startActivity(intent);
	}
	
	public void startGame(View v){
		runOnNotUiThread(new Runnable() {
			public void run() {
				PokerServer.getInstance().startGame();
			}
		});
	}

	public void displayLogginInfo(final String string) {
		runOnUiThread(new Runnable() {
			public void run() {
				TextView log = (TextView) findViewById(R.id.serverLog);
				log.append(string + "\n");
			}
		});
		
	}
	
	protected void runOnNotUiThread(Runnable runnable) {
		new Thread(runnable).start();
	}

}
