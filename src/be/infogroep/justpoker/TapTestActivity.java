package be.infogroep.justpoker;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class TapTestActivity extends Activity {
	boolean flippedCard1;
	boolean flippedCard2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		flippedCard1 = flippedCard2 = false;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tap_test);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		final TextView textView2 = (TextView) findViewById(R.id.tap_test_text2);
		final ImageView card1 = (ImageView) findViewById(R.id.card1);
		final ImageView card2 = (ImageView) findViewById(R.id.card2);

		card1.setOnTouchListener(new OnFlingGestureListener() {

			@Override
			public void onBottomToTop() {
				fold(card1);
				fold(card2);
			}

			@Override
			public void onDoubletap() {
				printMessage("Card1 doubletap", textView2);
				if (flippedCard1) {
					card1.setImageResource(R.drawable.spades_ace);
					flippedCard1 = false;
				} else {
					card1.setImageResource(R.drawable.card_backside);
					flippedCard1 = true;
				}
			}
		});
		card2.setOnTouchListener(new OnFlingGestureListener() {
			
			@Override
			public void onBottomToTop() {
				fold(card1);
				fold(card2);
			}

			@Override
			public void onDoubletap() {
				printMessage("Card2 doubletap", textView2);
				if (flippedCard2) {
					card2.setImageResource(R.drawable.spades_king);
					flippedCard2 = false;
				} else {
					card2.setImageResource(R.drawable.card_backside);
					flippedCard2 = true;
				}
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
		ObjectAnimator move = ObjectAnimator.ofFloat(card, "y", 0);
		ObjectAnimator fade = ObjectAnimator.ofFloat(card, "alpha", 0);
		move.setDuration(300);
		fade.setDuration(300);
		move.start();
		fade.start();
	}
}
