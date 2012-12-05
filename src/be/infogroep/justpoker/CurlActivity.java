package be.infogroep.justpoker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import fi.harism.curl.CurlPage;
import fi.harism.curl.CurlView;

public class CurlActivity extends Activity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

	private CurlView mCurlView;

	private Button mbtSpeak;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curl);

		int index = 0;
		mCurlView = (CurlView) findViewById(R.id.curl);
		mCurlView.setPageProvider(new PageProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);

		mbtSpeak = (Button) findViewById(R.id.btnSpeak);




		checkVoiceRecognition();

		// mCurlView.setBackgroundColor(0xFF202830);

		// This is something somewhat experimental. Before uncommenting next
		// line, please see method comments in CurlView.
		// mCurlView.setEnableTouchPressure(true);
	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
				mCurlView.setMargins(.1f, .05f, .1f, .05f);
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				mCurlView.setMargins(.1f, .1f, .1f, .1f);
			}
		}
	}

	private class PageProvider implements CurlView.PageProvider {

		// Bitmap resources.
		private int[] mBitmapIds = { R.drawable.card_backside,
				R.drawable.spades_ace };

		private Bitmap loadBitmap(int width, int height, int index) {
			Bitmap b = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			Drawable d = getResources().getDrawable(mBitmapIds[index]);

			int margin = 0;
			int border = 0;
			Rect r = new Rect(margin, margin, width - margin, height - margin);

			int imageWidth = r.width() - (border * 2);
			int imageHeight = imageWidth * d.getIntrinsicHeight()
					/ d.getIntrinsicWidth();
			if (imageHeight > r.height() - (border * 2)) {
				imageHeight = r.height() - (border * 2);
				imageWidth = imageHeight * d.getIntrinsicWidth()
						/ d.getIntrinsicHeight();
			}

			r.left += ((r.width() - imageWidth) / 2) - border;
			r.right = r.left + imageWidth + border + border;
			r.top += ((r.height() - imageHeight) / 2) - border;
			r.bottom = r.top + imageHeight + border + border;

			Paint p = new Paint();
			p.setColor(0xFFC0C0C0);
			c.drawRect(r, p);
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;

			d.setBounds(r);
			d.draw(c);

			return b;
		}

		public void updatePage(CurlPage page, int width, int height, int index) {

			switch (index) {
			// First case is image on front side, solid colored back.
			case 0:
				Bitmap front = loadBitmap(width, height, 0);
				Bitmap back = loadBitmap(width, height, 1);
				page.setTexture(back, CurlPage.SIDE_BACK);
				page.setTexture(front, CurlPage.SIDE_FRONT);
				break;
			case 1:
				Bitmap front1 = loadBitmap(width, height, 0);
				Bitmap back1 = loadBitmap(width, height, 1);
				page.setTexture(back1, CurlPage.SIDE_BACK);
				page.setTexture(front1, CurlPage.SIDE_FRONT);
				break;
			}
		}

		public int getPageCount() {
			return 2;
		}
	}

	private void vibrate(){
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(300);
	}

	public void createVibrate(View view) {
		vibrate();
	}

	public void fadeBackground(View view) {
		(new Thread(){
			Handler handler = new Handler();
			RelativeLayout screen = (RelativeLayout) findViewById(R.id.screen);
			@Override
			public void run(){
				for(int i=0; i<255; i++){
					final int a = i;
					handler.post(new Runnable(){
						public void run(){
							screen.setBackgroundColor(Color.argb(255, 255, 255-a, 255-a));
							if (a==254){
								vibrate();
								screen.setBackgroundColor(Color.argb(255, 255, 255, 255));
							}
						}
					});
					// next will pause the thread for some time
					try{ sleep(50); }
					catch(Exception e) { break; }
				}
			}
		}).start();
	}


	public void checkVoiceRecognition() {
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			mbtSpeak.setEnabled(false);
			mbtSpeak.setText("Voice recognizer not present");
			Toast.makeText(this, "Voice recognizer not present",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void speak(View view) {
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
							Toast.makeText(this, iter.next(), Toast.LENGTH_SHORT).show();
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



}
