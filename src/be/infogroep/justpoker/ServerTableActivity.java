package be.infogroep.justpoker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class ServerTableActivity extends Activity {
	
	private PokerServer cps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_table);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        cps = new PokerServer();
		cps.start();
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
    
    public void stopServer(MenuItem  m){
		Intent intent = new Intent(this, ServerActivity.class);
    	cps.stop();
		startActivity(intent);
    }
}
