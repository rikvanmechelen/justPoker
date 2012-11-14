package be.infogroep.justpoker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ServerActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_server, menu);
        return true;
    }
}
