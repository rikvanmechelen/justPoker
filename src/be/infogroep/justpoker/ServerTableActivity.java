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
import android.widget.ImageView;
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

	public void addPlayer(final PokerPlayer p, final int index) {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAvater(index, R.drawable.avatar_folded);
				setPlayerName(index, p.getName());
			}
		});
		
	}
	
	public void setTurn(final PokerPlayer p, final int index) {
		setPlayerStatusUIThread(p, index, R.drawable.avatar_turn);
	}
	public void setPlaying(final PokerPlayer p, final int index) {
		setPlayerStatusUIThread(p, index, R.drawable.avatar_playing);
	}
	public void setFolded(final PokerPlayer p, final int index) {
		setPlayerStatusUIThread(p, index, R.drawable.avatar_folded);
	}
	
	private void setPlayerStatusUIThread(final PokerPlayer p, final int index, final int drawable){
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAvater(index, drawable);
			}
		});
	}
	
	private void setPlayerAvater(int index, int drawable){
		java.lang.reflect.Field f;
		try {
			Log.d("justPoker - server", "setting atavar player"+index+" on the table");
			f = R.id.class.getField("player"+Integer.toString(index));
			int id = f == null ? -1 : (Integer)f.get(null);
			ImageView seat = (ImageView) findViewById(id);
			seat.setImageResource(drawable);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	private void setPlayerName(int index, String name){
		java.lang.reflect.Field f;
		try {
			f = R.id.class.getField("player"+Integer.toString(index)+"_name");
			int id = f == null ? -1 : (Integer)f.get(null);
			TextView name_field = (TextView) findViewById(id);
			name_field.setText(name);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

}
