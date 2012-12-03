package be.infogroep.justpoker;

import be.infogroep.justpoker.GameElements.Card;
import edu.vub.at.commlib.CommLib;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
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
	private PowerManager pm;
	private PowerManager.WakeLock wl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_table);
		//getActionBar().setDisplayHomeAsUpEnabled(true);

		//intent = new Intent(this, PokerServer.class);
		//startService(intent);
		//cps = PokerServer.getInstance();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				 "be.infogroep.justpoker.ServerTableActivity");
		wl.acquire();
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
	
	@Override
	public void onStop() {
		super.onStop();
		cps.stop();
		wl.release();
	}

	public void stopServer(MenuItem m) {
		//Intent intent = new Intent(this, ServerActivity.class);
		cps.stop();
		//startActivity(intent);
	}

	public void startMatch(View v){
		runOnNotUiThread(new Runnable() {
			public void run() {
				PokerServer.getInstance().startMatch();
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

	// State
	public void setTurn(final PokerPlayer p, final int index) {
		setPlayerStatusUIThread(p, index, R.drawable.avatar_turn);
	}
	public void setPlaying(final PokerPlayer p, final int index) {
		setPlayerStatusUIThread(p, index, R.drawable.avatar_playing);
	}
	public void setFolded(final PokerPlayer p, final int index) {
		setPlayerStatusUIThread(p, index, R.drawable.avatar_folded);
	}

	// Buttons
	public void setBigBlind(final PokerPlayer p, final int index) {
		setPlayerButtonUIThread(p, index, R.drawable.bigblind_button);
	}
	public void setSmallBlind(final PokerPlayer p, final int index) {
		setPlayerButtonUIThread(p, index, R.drawable.smallblind_button);
	}
	public void setDealer(final PokerPlayer p, final int index) {
		setPlayerButtonUIThread(p, index, R.drawable.dealer_button);
	}

	// Actions
	public void setCall(final PokerPlayer p, final int index) {
		setPlayerActionUIThread(p, index, R.drawable.action_call);
	}
	public void setRaise(final PokerPlayer p, final int index) {
		setPlayerActionUIThread(p, index, R.drawable.action_raise);
	}
	public void setBet(final PokerPlayer p, final int index) {
		setPlayerActionUIThread(p, index, R.drawable.action_bet);
	}

	public void setFold(final PokerPlayer p, final int index) {
		setPlayerActionUIThread(p, index, R.drawable.action_fold);
	}
	public void setCheck(final PokerPlayer p, final int index) {
		setPlayerActionUIThread(p, index, R.drawable.action_check);
	}

	public void resetPlayer(PokerPlayer player, int index) {
		setPlayerStatusUIThread(player, index, R.drawable.avatar_playing);
		setPlayerButtonUIThread(player, index, -1);
		setPlayerActionUIThread(player, index, -1);
	}

	// Run Set content on UI thread
	private void setPlayerStatusUIThread(final PokerPlayer p, final int index, final int drawable){
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAvater(index, drawable);
			}
		});
	}

	private void setPlayerActionUIThread(final PokerPlayer p, final int index, final int drawable){
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(index, drawable);
			}
		});
	}

	private void setPlayerButtonUIThread(final PokerPlayer p, final int index, final int drawable){
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerButton(index, drawable);
			}
		});
	}

	// Helper function
	private void setPlayerAvater(int index, int drawable){
		ImageView seat = (ImageView) findViewById(CommLib.getViewID("player"+Integer.toString(index)));
		seat.setImageResource(drawable);
	}

	private void setPlayerAction(int index, int drawable){
		ImageView seat = (ImageView) findViewById(CommLib.getViewID("player"+Integer.toString(index)+"_action"));
		seat.setImageResource(drawable);
	}

	private void setPlayerButton(int index, int drawable){
		ImageView seat = (ImageView) findViewById(CommLib.getViewID("player"+Integer.toString(index)+"_button"));
		seat.setImageResource(drawable);
	}

	private void setPlayerName(int index, String name){
		TextView name_field = (TextView) findViewById(CommLib.getViewID("player"+Integer.toString(index)+"_name"));
		name_field.setText(name);
	}

	public void showFlop(final Card[] card) {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView card0 = (ImageView) findViewById(CommLib.getViewID("card0"));
				ImageView card1 = (ImageView) findViewById(CommLib.getViewID("card1"));
				ImageView card2 = (ImageView) findViewById(CommLib.getViewID("card2"));
				card0.setImageDrawable(getDrawable(card[0].toString()));
				card1.setImageDrawable(getDrawable(card[1].toString()));
				card2.setImageDrawable(getDrawable(card[2].toString()));
			}
		});
	}

	public void showTurn(final Card card) {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView card3 = (ImageView) findViewById(CommLib.getViewID("card3"));
				card3.setImageDrawable(getDrawable(card.toString()));
			}
		});
	}

	public void showRiver(final Card card) {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView card4 = (ImageView) findViewById(CommLib.getViewID("card4"));
				card4.setImageDrawable(getDrawable(card.toString()));
			}
		});
	}
	
	private Drawable getDrawable(String s){
		String s2 = "drawable/"+s;
		int imageResource = getResources().getIdentifier(s2, null, getPackageName());
		return getResources().getDrawable(imageResource);
	}

	public void resetCards() {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView card0 = (ImageView) findViewById(CommLib.getViewID("card0"));
				ImageView card1 = (ImageView) findViewById(CommLib.getViewID("card1"));
				ImageView card2 = (ImageView) findViewById(CommLib.getViewID("card2"));
				ImageView card3 = (ImageView) findViewById(CommLib.getViewID("card3"));
				ImageView card4 = (ImageView) findViewById(CommLib.getViewID("card4"));		
				card0.setImageResource(-1);
				card1.setImageResource(-1);
				card2.setImageResource(-1);
				card4.setImageResource(-1);
				card3.setImageResource(-1);
			}
		});
	}
	

}
