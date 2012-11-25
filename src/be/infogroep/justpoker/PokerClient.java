package be.infogroep.justpoker;

import java.io.IOException;
import java.io.Serializable;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import be.infogroep.justpoker.messages.RegisterMessage;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import edu.vub.at.commlib.CommLib;
import edu.vub.at.commlib.CommLibConnectionInfo;

public class PokerClient extends Service implements Serializable {
    public static final String BROADCAST_ACTION = "be.infogroep.justpoker.pokerclient.displayevent";

	private static PokerClient SingletonPokerClient;

	private Connection serverConnection;
	private String connectionID = "connectionID";
	private int myClientID;
	private String name = "Rik";
	
	Intent intent;

	@Override
    public void onCreate() {
        super.onCreate();
        getIntent();	
    }
	
	private Intent getIntent() {
		if (intent == null) {
			Log.d("justPoker - Server", "Creating intent "+BROADCAST_ACTION);
			intent = new Intent(BROADCAST_ACTION);
		}
		return intent;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
        intent.getStringExtra("ip");
        Log.d("justPoker - client", "about to start client conneciton");
        connectToServer(intent.getStringExtra("ip"));
        //handler.postDelayed(serverR, 1000); // 1 second   
        //start();
    }

	public static PokerClient getInstance() {
		if (SingletonPokerClient == null) {
			//SingletonPokerClient = new PokerClient();
		}
		return SingletonPokerClient;
	}

	public Connection getServerConnection() {
		return serverConnection;
	}

	public void setServerConnection(Connection c) {
		serverConnection = c;
	}

	public String getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(String c) {
		connectionID = c;
	}

	public int getMyClientID() {
		return myClientID;
	}

	public void setMyClientID(int c) {
		myClientID = c;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}

	public void sendHello() {
		new SendAsyncMessage(serverConnection, "Owh Yah, Duffman is pounding in the direction!").execute();
	}
	
	public Listener listener = new Listener() {

		@Override
		public void connected(Connection c) {
			super.connected(c);
			setServerConnection(c);
			Log.d("justPoker - Client", "Connected to server!");
		}

		@Override
		public void received(Connection c, Object m) {
			super.received(c, m);

			Log.v("justPoker - Client", "Received message " + m.toString());

			if (m instanceof RegisterMessage) {
				myClientID = ((RegisterMessage) m).getClient_id();
				serverConnection.sendTCP(new RegisterMessage(name));
				DisplayLoggingInfo(m);
			}
			// serverConnection.sendTCP("OMG, this is sooo cool");
			// if (m instanceof String) {
			// // Client view
			// Log.v("wePoker - Client", "Procesing state message " +
			// m.toString());
			// }
		}
	};
	
	private void DisplayLoggingInfo(Object msg) {
    	Log.d("justPoker - client", "entered DisplayLoggingInfo");
    	//getIntent();
    	Log.d("justPoker - client", "entered intend is: "+ intent);
    	getIntent().putExtra("message", msg.toString());
    	//intent.putExtra("time", new Date().toLocaleString());
    	//intent.putExtra("counter", String.valueOf(++counter));
    	sendBroadcast(getIntent());
    }

	public boolean connectToServer(String ip) {
		new ConnectAsyncTask(ip, CommLib.SERVER_PORT, listener).execute();

		// while (serverConnection == null) {}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return (serverConnection != null);
		// new MessageSender(serverConnection,
		// "SENDING CLIENT MESSAGE! Owh Yah :)").execute();
	}

	public class SendAsyncMessage extends AsyncTask<Void, Void, Client> {		
		private Connection c;
		private Object o;
		public SendAsyncMessage(Connection co, Object obj) {
			this.c = co;
			this.o = obj;
		}
		@Override
		protected Client doInBackground(Void... params) {
			c.sendTCP(o);
			return null;
		}	
	}

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
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
