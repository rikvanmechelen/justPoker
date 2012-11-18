package be.infogroep.justpoker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "be.infogroep.justpoker.MESSAGE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void openServer(View view) {
		Intent intent = new Intent(this, ServerActivity.class);
		startActivity(intent);
	}

	public void openClient(View view) {
		Intent intent = new Intent(this, TapTestActivity.class);
		startActivity(intent);
	}
	public void createConnection(View view) {
		Intent intent = new Intent(this, ClientActivity.class);
		startActivity(intent);
	}
}
