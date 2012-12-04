package be.infogroep.justpoker;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import be.infogroep.justpoker.GameElements.Card;
import edu.vub.at.commlib.CommLib;
import edu.vub.at.commlib.PlayerState;
import edu.vub.at.commlib.PokerButton;

public class TapTestActivity extends Activity implements
		AbstractPokerClientActivity {
	boolean flippedCard1;
	boolean flippedCard2;

	PokerClient client;
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	private boolean stopFadeThread = false;
	private String android_id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// client = PokerClient.getInstance();
		// client.sendHello();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"be.infogroep.justpoker.ServerTableActivity");
		wl.acquire();

		android_id = Secure.getString(getBaseContext().getContentResolver(),
				Secure.ANDROID_ID);

		Intent incomingIntent = getIntent();
		String ip = incomingIntent.getStringExtra("ip");
		String name = incomingIntent.getStringExtra("name");
		setTitle("justPoker - " + name);

		flippedCard1 = flippedCard2 = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tap_test);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		final ImageView cardContainer1 = (ImageView) findViewById(R.id.card1);
		final ImageView cardContainer2 = (ImageView) findViewById(R.id.card2);
		initialCards(cardContainer1, cardContainer2);
		final ImageView betChip = (ImageView) findViewById(R.id.betChip);
		// final TextView betChipText = (TextView)
		// findViewById(R.id.betChipText);

		client = PokerClient.getInstance(TapTestActivity.this, name,
				android_id, ip, cardContainer1, cardContainer2);

		OnFlingGestureListener cardListener = new OnFlingGestureListener() {
			private boolean longPressed = false;

			@Override
			public void onBottomToTop() {
				client.fold(cardContainer1, cardContainer2);
			}

			@Override
			public void onDoubletap() {
				client.check(cardContainer1, cardContainer2);
			}

			@Override
			public void onLongpress() {
				if (client.inGame()) {
					cardContainer1.setImageDrawable(getDrawable(client
							.getCard1().toString()));
					cardContainer2.setImageDrawable(getDrawable(client
							.getCard2().toString()));
					longPressed = true;
				}
			}

			public void onTouchevent(MotionEvent e) {
				if (longPressed && e.getAction() == MotionEvent.ACTION_UP) {
					cardContainer1.setImageResource(R.drawable.card_backside);
					cardContainer2.setImageResource(R.drawable.card_backside);
					longPressed = false;
				}
			}
		};
		OnFlingGestureListener chipListener = new OnFlingGestureListener() {
			@Override
			public void onBottomToTop() {
				client.bet();
			}
		};

		cardContainer2.setOnTouchListener(cardListener);
		cardContainer1.setOnTouchListener(cardListener);
		betChip.setOnTouchListener(chipListener);
		// betChipText.setOnTouchListener(chipListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_tap_test, menu);
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

	public void printMessage(String s, TextView t) {
		t.setText(s);
	}

	private void doFold(final ImageView card) {
		Animation myFadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.foldcards);
		myFadeInAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		myFadeInAnimation.setFillAfter(true);
		card.startAnimation(myFadeInAnimation);
	}

	private void doInitialCards(final ImageView card) {
		Animation myFadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.initialcards);
		myFadeInAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		myFadeInAnimation.setFillAfter(true);
		card.startAnimation(myFadeInAnimation);
	}

	private void doDeal(final ImageView card, final ImageView card2) {
		final Animation myFadeInAnimation2 = AnimationUtils.loadAnimation(this,
				R.anim.dealcards);
		myFadeInAnimation2.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
				card2.setImageDrawable(getDrawable("card_backside"));
			}
		});
		// myFadeInAnimation2.setFillAfter(true);

		final Animation myFadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.dealcards);
		myFadeInAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				card2.startAnimation(myFadeInAnimation2);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
				card.setImageDrawable(getDrawable("card_backside"));
			}
		});
		Animation myFoldInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.foldcards);
		final Animation myFoldInAnimation2 = AnimationUtils.loadAnimation(this,
				R.anim.foldcards);
		myFoldInAnimation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				card.setImageDrawable(null);
				card2.startAnimation(myFoldInAnimation2);
			}
		});
		myFoldInAnimation2.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				card2.setImageDrawable(null);
				card.startAnimation(myFadeInAnimation);
			}
		});

		if (client.getState() == PlayerState.Fold) {
			card.startAnimation(myFadeInAnimation);
		} else {
			card.startAnimation(myFoldInAnimation);
		}
		// myFadeInAnimation.setFillAfter(true);
	}

	private void doCheck() {
		Toast.makeText(getApplicationContext(), "You Checked!",
				Toast.LENGTH_LONG).show();
	}

	private void doBet() {
		final ImageView button = (ImageView) findViewById(R.id.betChip);
		Animation myFadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.moveup);
		myFadeInAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		button.startAnimation(myFadeInAnimation);
		Toast.makeText(getApplicationContext(), "You Bet!", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		wl.release();
	}

	public void displayLoggingInfo(final Object m) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), "received: " + m,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	protected void runOnNotUiThread(Runnable runnable) {
		new Thread(runnable).start();
	}

	public void setCards(Card[] cards) {
		client.setCard1(cards[0]);
		client.setCard2(cards[1]);
		final ImageView cardContainer1 = (ImageView) findViewById(R.id.card1);
		final ImageView cardContainer2 = (ImageView) findViewById(R.id.card2);
		deal(cardContainer1, cardContainer2);
	}

	private Drawable getDrawable(String s) {
		String s2 = "drawable/" + s;
		int imageResource = getResources().getIdentifier(s2, null,
				getPackageName());
		return getResources().getDrawable(imageResource);
	}

	public void setBlind(final PokerButton b) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), "You are the " + b,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public void fold(final ImageView card1, final ImageView card2) {
		runOnUiThread(new Runnable() {
			public void run() {
				doFold(card1);
				doFold(card2);
			}
		});
	}

	public void initialCards(final ImageView card1, final ImageView card2) {
		// runOnUiThread(new Runnable() {
		// public void run() {
		// doInitialCards(card1);
		// doInitialCards(card2);
		// }
		// });
	}

	public void deal(final ImageView card1, final ImageView card2) {
		runOnUiThread(new Runnable() {
			public void run() {
				doDeal(card2, card1);
				// doDeal(card2);
			}
		});
	}

	public void check(ImageView cardContainer1, ImageView cardContainer2) {
		runOnUiThread(new Runnable() {
			public void run() {
				doCheck();
			}
		});
	}

	public void bet() {
		runOnUiThread(new Runnable() {
			public void run() {
				doBet();
			}
		});
	}

	private ImageView cloneImageView(ImageView view) {
		ImageView result = new ImageView(this);
		result.setX(view.getX());
		result.setY(view.getY());
		result.setScaleType(view.getScaleType());
		result.setScaleX(view.getScaleX());
		result.setScaleY(view.getScaleY());
		result.setImageDrawable(view.getDrawable());
		return result;
	}

	private void vibrate(int len) {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(len);
	}

	public void fadeBackground() {
		final RelativeLayout screen = (RelativeLayout) findViewById(R.id.tap_test_layout);
		for (int i = 0; i < 255; i++) {
			final int a = i;
			if (stopFadeThread) {
				runOnUiThread(new Runnable() {

					public void run() {

						screen.setBackgroundColor(Color.GREEN);

					}
				});
				stopFadeThread = false;
				break;
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						screen.setBackgroundColor(Color
								.argb(255, a, 255 - a, 0));
						if (a == 254) {
							vibrate(300);
							screen.setBackgroundColor(Color.GREEN);
						}
					}
				});
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					break;
				}
			}
		}
	}

	public void startTurn() {
		vibrate(500);
		// fadeBackground();

	}

	public void endTurn() {
		stopFadeThread = true;
	}

	public void setBet() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_bet);
			}
		});
	}

	public void setCall() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_call);
			}
		});
	}

	public void setCheck() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_check);
			}
		});
	}

	public void setFold() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_fold);
			}
		});
	}

	public void setRaise() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_raise);
			}
		});
	}

	public void setReRaise() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_raise);
			}
		});
	}

	public void resetPlayerAction() {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView seat = (ImageView) findViewById(R.id.action);
				seat.setImageDrawable(null);
			}
		});
	}

	private void setPlayerAction(int drawable) {
		ImageView seat = (ImageView) findViewById(R.id.action);
		seat.setImageResource(drawable);
	}

	public void setBigBlind() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerButton(R.drawable.bigblind_button);
			}
		});
	}

	public void setSmallBlind() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerButton(R.drawable.smallblind_button);
			}
		});
	}

	public void setDealer() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerButton(R.drawable.dealer_button);
			}
		});
	}

	public void resetButton() {
		runOnUiThread(new Runnable() {
			public void run() {
				ImageView seat = (ImageView) findViewById(R.id.button);
				seat.setImageDrawable(null);
			}
		});
	}

	private void setPlayerButton(int drawable) {
		ImageView seat = (ImageView) findViewById(R.id.button);
		seat.setImageResource(drawable);
	}
}
