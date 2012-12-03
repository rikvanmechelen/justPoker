package be.infogroep.justpoker;

import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import be.infogroep.justpoker.GameElements.Card;
import edu.vub.at.commlib.CommLib;

public class ServerTableActivity extends Activity {

	private PokerServer cps;
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	private OnFlingGestureListener cardListener;
	private ArrayList<ImageView> cards;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_table);
		// getActionBar().setDisplayHomeAsUpEnabled(true);

		// intent = new Intent(this, PokerServer.class);
		// startService(intent);
		// cps = PokerServer.getInstance();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"be.infogroep.justpoker.ServerTableActivity");
		wl.acquire();
		String ipAddress = CommLib.getIpAddress(this);
		cps = PokerServer.getInstance(ServerTableActivity.this, ipAddress);
		cps.start();
		cards = new ArrayList<ImageView>();
		cardListener = new OnFlingGestureListener() {
			@Override
			public void onLeftToRight() {
				runOnNotUiThread(new Runnable() {
					public void run() {
						clearTable();
						//cps.startNewGame();
					}
				});

			}

			@Override
			public void onRightToLeft() {
				runOnNotUiThread(new Runnable() {
					public void run() {
						clearTable();
						//cps.startNewGame();
					}
				});
			}
		};
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
		// startService(intent);
		// registerReceiver(broadcastReceiver, new IntentFilter(
		// PokerServer.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregisterReceiver(broadcastReceiver);
		// stopService(intent);
	}

	@Override
	public void onStop() {
		super.onStop();
		cps.stop();
		wl.release();
	}

	public void stopServer(MenuItem m) {
		// Intent intent = new Intent(this, ServerActivity.class);
		cps.stop();
		// startActivity(intent);
	}

	public void startMatch(View v) {
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

	public void resetAction(PokerPlayer player, int index) {
		setPlayerActionUIThread(player, index, -1);
	}

	public void resetPlayer(PokerPlayer player, int index) {
		setPlayerStatusUIThread(player, index, R.drawable.avatar_playing);
		setPlayerButtonUIThread(player, index, -1);
		setPlayerActionUIThread(player, index, -1);
	}

	// Run Set content on UI thread
	private void setPlayerStatusUIThread(final PokerPlayer p, final int index,
			final int drawable) {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAvater(index, drawable);
			}
		});
	}

	private void setPlayerActionUIThread(final PokerPlayer p, final int index,
			final int drawable) {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(index, drawable);
			}
		});
	}

	private void setPlayerButtonUIThread(final PokerPlayer p, final int index,
			final int drawable) {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerButton(index, drawable);
			}
		});
	}

	// Helper function
	private void setPlayerAvater(int index, int drawable) {
		ImageView seat = (ImageView) findViewById(CommLib.getViewID("player"
				+ Integer.toString(index)));
		seat.setImageResource(drawable);
	}

	private void setPlayerAction(int index, int drawable) {
		ImageView seat = (ImageView) findViewById(CommLib.getViewID("player"
				+ Integer.toString(index) + "_action"));
		seat.setImageResource(drawable);
	}

	private void setPlayerButton(int index, int drawable) {
		ImageView seat = (ImageView) findViewById(CommLib.getViewID("player"
				+ Integer.toString(index) + "_button"));
		seat.setImageResource(drawable);
	}

	private void setPlayerName(int index, String name) {
		TextView name_field = (TextView) findViewById(CommLib
				.getViewID("player" + Integer.toString(index) + "_name"));
		name_field.setText(name);
	}

	public void showFlop(final Card[] card) {
		runOnUiThread(new Runnable() {
			public void run() {
				setUpCard("card0", card[0]);
				setUpCard("card1", card[1]);
				setUpCard("card2", card[2]);
			}
		});
	}

	public void showFlop(View view) {
		short x = 2;
		short y = 3;
		setUpCard("card0", new Card(x, y));
		setUpCard("card1", new Card(x, y));
		setUpCard("card2", new Card(x, y));
	}

	public void showTurn(final Card card) {
		runOnUiThread(new Runnable() {
			public void run() {
				setUpCard("card3", card);
			}
		});
	}

	public void showRiver(final Card card) {
		runOnUiThread(new Runnable() {
			public void run() {
				setUpCard("card4", card);
			}
		});
	}

	private Drawable getDrawable(String s) {
		String s2 = "drawable/" + s;
		int imageResource = getResources().getIdentifier(s2, null,
				getPackageName());
		return getResources().getDrawable(imageResource);
	}

	private ImageView cloneClearImageView(ImageView view) {
		ImageView result = new ImageView(this);
		result.setId(view.getId());
		result.setLayoutParams(view.getLayoutParams());
		// result
		return result;
	}

	public void clearTable() {
		runOnUiThread(new Runnable() {
			public void run() {
				// LinearLayout layout = (LinearLayout)
				// findViewById(R.id.cards);
				// ArrayList<ImageView> newCards = new ArrayList<ImageView>();
				Iterator<ImageView> iter = cards.iterator();
				while (iter.hasNext()) {
					ImageView card = iter.next();
					// newCards.add(cloneClearImageView(card));
					doClearCard(card);
				}
				// iter = newCards.iterator();
				// while(iter.hasNext()){
				// layout.addView(iter.next());
				// }
			}
		});
	}

	private void doClearCard(final ImageView card) {
		
		Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
		myFadeInAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				card.setImageDrawable(null);
	            Log.d("justPoker - Client", "---- animation end listener called" );
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
				card.setImageDrawable(getDrawable("card_backside"));
			}
		});
		card.startAnimation(myFadeInAnimation);
	}

	private void setUpCard(String id, Card c) {
		ImageView card = (ImageView) findViewById(CommLib.getViewID(id));
		card.setImageDrawable(getDrawable(c.toString()));
		card.setOnTouchListener(cardListener);
		cards.add(card);
	}

}
