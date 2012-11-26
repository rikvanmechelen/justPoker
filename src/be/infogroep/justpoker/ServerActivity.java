package be.infogroep.justpoker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class ServerActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        if (PokerServer.getInstance() != null) {
        	Intent intent = new Intent(this, ServerTableActivity.class);
    		startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_server, menu);
        return true;
    }
    
    public void startServer(View view) {
		Intent intent = new Intent(this, ServerTableActivity.class);
		startActivity(intent);
	}
    
}
