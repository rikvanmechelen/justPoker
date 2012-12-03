package be.infogroep.justpoker;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Profile;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.Color;
import be.infogroep.justpoker.Validators.IPAddressValidator;
import be.infogroep.justpoker.messages.RegisterMessage;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.vub.at.commlib.CommLib;
import edu.vub.at.commlib.CommLibConnectionInfo;

public class ClientActivity extends Activity {

	private static Connection serverConnection;
	public static final String connectionID = "connectionID";
	private static int myClientID;
	private static String name = "Rik";

	//private PokerClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		final EditText editTextNickname = (EditText) findViewById(R.id.nickname);

		AccountManager manager = AccountManager.get(this); 
		Account[] accounts = manager.getAccountsByType("com.google"); 

		List<String> l = new LinkedList<String>();
		if (Build.VERSION.SDK_INT >= 14){
			l = getName();
		}
		
		editTextNickname.setText("Guest_"+randomNr());
		if (accounts.length != 0) {
			editTextNickname.setText(accounts[0].name);
		}
		if (l.size() != 0) {
			editTextNickname.setText(l.get(0));
		}
		//client = PokerClient.getInstance();
		//client.setName("Rik");
	}

	@TargetApi(14)
	private List<String> getName(){
		Cursor c = getContentResolver().query(
				ContactsContract.Profile.CONTENT_URI, null,  null, null, null);
		int count = c.getCount();

		String[] columnNames = c.getColumnNames();
		List<String> profileList = new LinkedList<String>();
		boolean b = c.moveToFirst();
		int position = c.getPosition();
		if (count == 1 && position == 0) {
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < columnNames.length; j++) {
					String columnName = columnNames[j];
					profileList.add(c.getString(c.getColumnIndex(Profile.DISPLAY_NAME)));
				}
				boolean b2 = c.moveToNext();
			}
		}
		c.close();
		return profileList;
	}     

	private int randomNr() {
		Time t = new Time();
		t.setToNow();
		long s = t.toMillis(false);
		Random r = new Random(s);
		return r.nextInt(100);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_client, menu);
		return true;
	}

	public void connectToServer(View view) {
		final EditText editTextIP = (EditText) findViewById(R.id.server_ip);
		final EditText editTextNickname = (EditText) findViewById(R.id.nickname);
		String ip = editTextIP.getText().toString();
		String name = editTextNickname.getText().toString();
		if (handleNameValidation(name, editTextNickname) && handelIpAddressValidation(editTextIP)) {
			//Intent intent = new Intent(this, PokerServer.class);
			//startService(intent);
			Intent intent = new Intent(this, TapTestActivity.class);
			intent.putExtra("ip", ip);
			intent.putExtra("name", name);
			startActivity(intent);
		}
	}

	private boolean handleNameValidation(String n, final EditText e) {
		if (n.length() == 0) {
			Log.d("justPoker - client", "nickname is: "+n.length());
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Please give a Nickname")
			.setTitle("Nickname not correct")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					e.setBackgroundColor(android.graphics.Color.RED);
					e.requestFocus();
				}});
			AlertDialog dialog = builder.create();
			dialog.show();
			return false;
		}
		return true;
	}

	public Boolean handelIpAddressValidation(final EditText e) {
		String ip = e.getText().toString();
		IPAddressValidator ip_val = new IPAddressValidator();
		if (!ip_val.validate(ip)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Please give a valid ip address.")
			.setTitle("Not an ip address")
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int id) {
					e.setBackgroundColor(android.graphics.Color.RED);
					e.requestFocus();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			return false;
		}
		return true;
	}

	private void setServerConnection(Connection c) {
		serverConnection = c;
	}

}