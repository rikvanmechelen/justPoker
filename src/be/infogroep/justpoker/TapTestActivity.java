package be.infogroep.justpoker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.speech.RecognizerIntent;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
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
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private static final String[] CHECK_OPTIONS = new String[]{"check", "tech"};
	private static final String[] FOLD_OPTIONS = new String[]{"fold", "bold", "old"};
	private static final String[] BET_OPTIONS = new String[]{"bet", "Bet", "bed"};
	private static final String[] RAISE_OPTIONS = new String[]{"race", "racist", "grace"};
	private static final String[] CALL_OPTIONS = new String[]{"call", "call me"};
	private static final String[] ALLIN_OPTIONS = new String[]{"all in", "allin", "allen", "all inn"};
	
	private static Boolean voiceCheck(String s, String[] items){
		String lowerS = s.toLowerCase();
		for(int i =0; i < items.length; i++)
	    {
	        if(items[i].contains(lowerS) || lowerS.contains(items[i]))
	        {
	            return true;
	        }
	    }
	    return false;
	}


	boolean flippedCard1;
	boolean flippedCard2;

	PokerClient client;
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	private boolean stopFadeThread = false;
	private String android_id;
	private ImageView cardContainer1;
	private ImageView cardContainer2;

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

		((TextView) findViewById(R.id.info_area))
				.setMovementMethod(new ScrollingMovementMethod());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		cardContainer1 = (ImageView) findViewById(R.id.card1);
		cardContainer2 = (ImageView) findViewById(R.id.card2);
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

			@Override
			public void onDoubletap() {
				client.bet();
			}
		};

		cardContainer2.setOnTouchListener(cardListener);
		cardContainer1.setOnTouchListener(cardListener);
		betChip.setOnTouchListener(chipListener);
		checkVoiceRecognition();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

			//If Voice recognition is successful then it returns RESULT_OK
			if(resultCode == RESULT_OK) {

				ArrayList<String> textMatchList = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				if (!textMatchList.isEmpty()) {
					// If first Match contains the 'search' word
					// Then start web search.
					if (textMatchList.get(0).contains("search")) {

						String searchQuery = textMatchList.get(0);
						searchQuery = searchQuery.replace("search","");
						Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
						search.putExtra(SearchManager.QUERY, searchQuery);
						startActivity(search);
					} else {
						Iterator<String> iter = textMatchList.iterator();
						while(iter.hasNext()){
							//Toast.makeText(this, iter.next(), Toast.LENGTH_SHORT).show();
							String result = iter.next();
							if (voiceCheck(result, CHECK_OPTIONS)){
								client.check(cardContainer1, cardContainer2);
								break;
							} else if (voiceCheck(result, FOLD_OPTIONS)){
								client.fold(cardContainer1, cardContainer2);
								break;
							} else if (voiceCheck(result, BET_OPTIONS)){
								client.bet();
								break;
							} else if (voiceCheck(result, CALL_OPTIONS)){
								client.check(cardContainer1, cardContainer2);
								break;
							} else if (voiceCheck(result, RAISE_OPTIONS)){
								client.bet();
								break;
							} else if (voiceCheck(result, ALLIN_OPTIONS)){
								client.bet();
								break;
							}
							
						}
					}

				}
				//Result code for various error.
			}else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
				Toast.makeText(this, "Audio Error", Toast.LENGTH_SHORT).show();
			}else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
				Toast.makeText(this, "Client Error", Toast.LENGTH_SHORT).show();
			}else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
				Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
			}else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
				Toast.makeText(this, "No Match", Toast.LENGTH_SHORT).show();
			}else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
				Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
			}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void checkVoiceRecognition() {
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			ImageView speak_button = (ImageView) (ImageView) findViewById(R.id.voiceButton);
			speak_button.setEnabled(false);
			speak_button.setImageDrawable(null);
		}
	}
	
	public void speak(View v){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Your options are: \nFold, Check, Bet, Raise, Call, All In");

		// Given an hint to the recognizer about what the user is going to say
		//There are two form of language model available
		//1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
		//2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		// Specify how many results you want to receive. The results will be
		// sorted where the first result is the one with higher confidence.
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		//Start the Voice recognizer activity for the result.
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
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

		if (client.getState() == PlayerState.Fold
				|| client.getState() == PlayerState.Unknown) {
			card.startAnimation(myFadeInAnimation);
		} else {
			card.startAnimation(myFoldInAnimation);
		}
		// myFadeInAnimation.setFillAfter(true);
	}

	private void doCheck() {
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

	public void displayLoggingInfo(final String m) {
		runOnUiThread(new Runnable() {
			public void run() {
				writeLog(m);
			}
		});
	}

	private void writeLog(String s) {
		TextView loginfo = (TextView) findViewById(R.id.info_area);
		loginfo.append(s + "\n");

		// find the amount we need to scroll. This works by
		// asking the TextView's internal layout for the position
		// of the final line and then subtracting the TextView's height
		final int scrollAmount = loginfo.getLayout().getLineTop(
				loginfo.getLineCount())
				- loginfo.getHeight();
		// if there is no need to scroll, scrollAmount will be <=0
		if (scrollAmount > 0)
			loginfo.scrollTo(0, scrollAmount);
		else
			loginfo.scrollTo(0, 0);
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
				writeLog("You are the " + b);
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
				writeLog("You Bet");
			}
		});
	}

	public void setCall() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_call);
				writeLog("You Called");
			}
		});
	}

	public void setCheck() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_check);
				writeLog("You Checked");
			}
		});
	}

	public void setFold() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_fold);
				writeLog("You Folded");
			}
		});
	}

	public void setRaise() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_raise);
				writeLog("You Raised");
			}
		});
	}

	public void setReRaise() {
		runOnUiThread(new Runnable() {
			public void run() {
				setPlayerAction(R.drawable.action_raise);
				writeLog("You ReRaised");
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
