package be.infogroep.justpoker;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ClientActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_client, menu);
        return true;
    }
}
