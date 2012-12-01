package be.infogroep.justpoker;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TapTestActivity extends Activity implements AbstractPokerClientActivity {
	boolean flippedCard1;
	boolean flippedCard2;
	PokerClient client;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//client = PokerClient.getInstance();
		//client.sendHello();
		Intent incomingIntent = getIntent();
		String ip = incomingIntent.getStringExtra("ip");
		String name = incomingIntent.getStringExtra("name");
		client = PokerClient.getInstance(TapTestActivity.this ,name, ip);
				
		flippedCard1 = flippedCard2 = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tap_test);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		final ImageView card1 = (ImageView) findViewById(R.id.card1);
		final ImageView card2 = (ImageView) findViewById(R.id.card2);

		card2.setOnTouchListener(new OnFlingGestureListener() {
			private boolean longPressed = false;
			@Override
			public void onBottomToTop() {
				fold(card1);
				fold(card2);
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
				card1.setImageResource(R.drawable.spades_ace);
				card2.setImageResource(R.drawable.spades_king);
				longPressed = true;
			}
			
			public void onTouchevent(MotionEvent e){
				if (longPressed && e.getAction() == MotionEvent.ACTION_UP) {
					card1.setImageResource(R.drawable.card_backside);
					card2.setImageResource(R.drawable.card_backside);
					longPressed = false;
				}
			}
		});
		card1.setOnTouchListener(new OnFlingGestureListener() {
			
			@Override
			public void onBottomToTop() {
				fold(card1);
				fold(card2);
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
				card2.setImageResource(R.drawable.card_backside);
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

}
