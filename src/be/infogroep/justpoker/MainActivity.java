package be.infogroep.justpoker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    
    public void sendMessage(View v){
    	((Button) v).setText("clicked");
    	//Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message); 
    	String message = editText.getText().toString();
    	//System.out.println(findViewById(R.id.edit_message));
    	TextView tv = (TextView) findViewById(R.id.textView1);
    	tv.append(message+"\n");
    	//intent.putExtra(EXTRA_MESSAGE, message);
    	//startActivity(intent);
    	
    }
}
