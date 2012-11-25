/*
 *	A lot of this code is taken from the wePoker, written by the Ambientalk team 
 */

package be.infogroep.justpoker;

import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import be.infogroep.justpoker.GameElements.Deck;
import be.infogroep.justpoker.messages.Message;
import be.infogroep.justpoker.messages.RegisterMessage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.vub.at.commlib.CommLib;
import edu.vub.at.commlib.UUIDSerializer;

public class PokerServer extends Service {
	//private static final String TAG = "PokerServer";
    public static final String BROADCAST_ACTION = "be.infogroep.justpoker.servertableactivity.displayevent";
    private static PokerServer SingletonPokerServer;

	int nextClientID = 0;
	private ConcurrentSkipListMap<Integer, Connection> connections = new ConcurrentSkipListMap<Integer, Connection>();
	private volatile Thread serverThread;
	private Server server;
	private Deck deck;
	private final Handler handler = new Handler();
	Intent intent;

	public PokerServer() {
		deck = new Deck();
		deck.shuffle();
		//start();
	}
	
	public static PokerServer getInstance() {
		if (SingletonPokerServer == null) {
			SingletonPokerServer = new PokerServer();
		}
		return SingletonPokerServer;
	}
	
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
        handler.removeCallbacks(serverR);
        handler.postDelayed(serverR, 1000); // 1 second   
    }

	Runnable serverR = new Runnable() {
		public void run() {
			try {
				Log.d("justPoker - Server", "Creating server");
				server = new Server();
				Kryo k = server.getKryo();
				k.setRegistrationRequired(false); // false is the default
				k.register(UUID.class, new UUIDSerializer());
				// k.register(Message.class);
				// k.register(RegisterMessage.class);
				server.bind(CommLib.SERVER_PORT);
				server.start();
				final Runnable test = this;
				server.addListener(new Listener() {
					@Override
					public void connected(Connection c) {
						super.connected(c);
						Log.d("justPoker - Server",
								"Client connected: " + c.getRemoteAddressTCP());
						addClient(c);
					}

					@Override
					public void received(Connection c, Object msg) {
						super.received(c, msg);
						Log.d("justPoker - Server", "Message received " + msg);
						DisplayLoggingInfo(msg);
						handler.postDelayed(test, 2000);
						//activity.printMessage(msg);
						// if (msg instanceof FutureMessage) {
						// FutureMessage fm = (FutureMessage) msg;
						// Log.d("justPoker - Server", "Resolving future " +
						// fm.futureId + "(" + CommLib.futures.get(fm.futureId)
						// + ") with value " + fm.futureValue);
						// CommLib.resolveFuture(fm.futureId, fm.futureValue);
						// }
						// if (msg instanceof SetClientParameterMessage) {
						// SetClientParameterMessage cm =
						// (SetClientParameterMessage) msg;
						// Log.d("wePoker - Server",
						// "Got SetIDReplyMessage: "+cm.toString());
						// registerClient(c, cm.nickname, cm.avatar, cm.money);
						// gameLoop.broadcast(cm);
						// }
					}

					@Override
					public void disconnected(Connection c) {
						super.disconnected(c);
						Log.d("justPoker - Server", "Client disconnected: " + c);
						removeClient(c);
					}
				});
			} catch (IOException e) {
				Log.e("justPoker - Server", "Server thread crashed", e);
			}
		};
	};
	
	private void DisplayLoggingInfo(Object msg) {
    	Log.d("justPoker - server", "entered DisplayLoggingInfo");
    	//getIntent();
    	Log.d("justPoker - server", "entered intend is: "+ intent);
    	getIntent().putExtra("message","lalala");
    	//intent.putExtra("time", new Date().toLocaleString());
    	//intent.putExtra("counter", String.valueOf(++counter));
    	sendBroadcast(getIntent());
    }

	public synchronized void start() {
		Log.d("justPoker - Server", "Starting server thread...");
		if (serverThread == null) {
			serverThread = new Thread(serverR);
			serverThread.start();
			// new Thread(gameLoop).start();
		}

	}

	public synchronized void stop() {
		if (serverThread != null) {
			// Thread tmpThread = serverThread;
			server.stop();
			serverThread.interrupt();
			serverThread = null;
			// tmpThread.interrupt();
			Log.d("justPoker - Server", "Stoped server thread");
		}
	}

	public void addClient(Connection c) {
		Log.d("justPoker - Server", "Adding client " + c.getRemoteAddressTCP());
		connections.put(nextClientID, c);
		RegisterMessage m = new RegisterMessage(nextClientID);
		c.sendTCP(m);
		nextClientID++;
	}

	public void registerClient(Connection c, String nickname, int avatar,
			int money) {
		for (Integer i : connections.keySet()) {
			if (connections.get(i) == c) {
				// gameLoop.addPlayer(c, i, nickname, avatar, money);
				return;
			}
		}
	}

	public void removeClient(Connection c) {
		// Log.d("wePoker - Server", "Client removed: " + c);
		for (Integer i : connections.keySet()) {
			if (connections.get(i) == c) {
				// gameLoop.removePlayer(i);
				connections.remove(i);
				return;
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
