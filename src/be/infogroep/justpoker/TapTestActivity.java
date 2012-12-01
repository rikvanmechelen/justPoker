package be.infogroep.justpoker;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import be.infogroep.justpoker.GameElements.Card;

public class TapTestActivity extends Activity implements AbstractPokerClientActivity {
	boolean flippedCard1;
	boolean flippedCard2;
	Card card1;
	Card card2;
	PokerClient client;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//client = PokerClient.getInstance();
		//client.sendHello();
		Intent incomingIntent = getIntent();
		String ip = incomingIntent.getStringExtra("ip");
		client = PokerClient.getInstance(TapTestActivity.this ,"Rik", ip);
				
		flippedCard1 = flippedCard2 = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tap_test);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		final ImageView cardContainer1 = (ImageView) findViewById(R.id.card1);
		final ImageView cardContainer2 = (ImageView) findViewById(R.id.card2);

		cardContainer2.setOnTouchListener(new OnFlingGestureListener() {
			private boolean longPressed = false;
			@Override
			public void onBottomToTop() {
				fold(cardContainer1);
				fold(cardContainer2);
			}

//			@Override
//			public void onDoubletap() {
//				if (flippedCard1) {
//					card1.setImageResource(R.drawable.spades_ace);
//					flippedCard1 = false;
//				} else {
//					card1.setImageResource(R.drawable.card_backside);
//					flippedCard1 = true;
//				}
//			}
			@Override
			public void onLongpress() {
				cardContainer1.setImageDrawable(getDrawable(card1.toString()));
				cardContainer2.setImageDrawable(getDrawable(card2.toString()));
				longPressed = true;
			}
			
			public void onTouchevent(MotionEvent e){
				if (longPressed && e.getAction() == MotionEvent.ACTION_UP) {
					cardContainer1.setImageResource(R.drawable.card_backside);
					cardContainer2.setImageResource(R.drawable.card_backside);
					longPressed = false;
				}
			}
		});
		cardContainer1.setOnTouchListener(new OnFlingGestureListener() {
			
			@Override
			public void onBottomToTop() {
				fold(cardContainer1);
				fold(cardContainer2);
			}

//			@Override
//			public void onDoubletap() {
//				if (flippedCard2) {
//					card2.setImageResource(R.drawable.spades_king);
//					flippedCard2 = false;
//				} else {
//					card2.setImageResource(R.drawable.card_backside);
//					flippedCard2 = true;
//				}
//			}
			
			@Override
			public void onLongpress() {
				cardContainer2.setImageResource(R.drawable.card_backside);
			}
		});

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

	private void fold(ImageView card) {
		ObjectAnimator move = ObjectAnimator.ofFloat(card, "y", -225);
		ObjectAnimator fade = ObjectAnimator.ofFloat(card, "alpha", 0);
		ObjectAnimator spin = ObjectAnimator.ofFloat(card, "rotation", 180);
		move.setDuration(300);
		fade.setDuration(300);
		spin.setDuration(300);
		move.start();
		//fade.start();
		spin.start();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	public void displayLoggingInfo(final Object m) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), "received: "+ m,
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	protected void runOnNotUiThread(Runnable runnable) {
		new Thread(runnable).start();
	}

	@Override
	public void setCards(Card[] cards) {
		card1 = cards[0];
		card2 = cards[1];
	}
	
	private Drawable getDrawable(String s){
		String s2 = "drawable/"+s;
		int imageResource = getResources().getIdentifier(s2, null, getPackageName());
		return getResources().getDrawable(imageResource);
	}

}
