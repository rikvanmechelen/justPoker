package be.infogroep.justpoker;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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
	
	private PokerClient client;

	public class ConnectAsyncTask extends AsyncTask<Void, Void, Client> {

		private int port;
		private String address;
		private Listener listener;

		public ConnectAsyncTask(String address, int port, Listener listener) {
			this.address = address;
			this.port = port;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.v("justPoker - Client", "Connecting to " + address + " " + port);
		}

		@Override
		protected Client doInBackground(Void... params) {
			try {
				return CommLibConnectionInfo.connect(address, port, listener);
			} catch (IOException e) {
				Log.d("justPoker - Client", "Could not connect to server", e);
			}
			return null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		client = PokerClient.getInstance();
		client.setName("Rik");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_client, menu);
		return true;
	}

	public void connectToServer(View view) {
		final EditText e = (EditText) findViewById(R.id.server_ip);
		String ip = e.getText().toString();
		if (handelIpAddressValidation(e) && client.connectToServer(ip)) {
				Intent intent = new Intent(this, TapTestActivity.class);
				//intent.putExtra(connectionID, client);
				startActivity(intent);
		}
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
									e.setText("");
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