package be.infogroep.justpoker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class TapTestActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tap_test);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		TextView textView = (TextView) findViewById(R.id.tap_test_text);
		textView.setOnTouchListener(new OnFlingGestureListener() {

			@Override
			public void onTopToBottom() {
				printMessage("TopToBottom");
			}

			@Override
			public void onRightToLeft() {
				printMessage("RightToLeft");
			}

			@Override
			public void onLeftToRight() {
				printMessage("LeftToRight");
			}

			@Override
			public void onBottomToTop() {
				printMessage("BottomToTop");
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

	public void printMessage(String s) {
		TextView textView = (TextView) findViewById(R.id.tap_test_text);
		textView.append("\n");
		textView.append(s);
	}
}
