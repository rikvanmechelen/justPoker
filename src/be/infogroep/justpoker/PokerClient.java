package be.infogroep.justpoker;

import java.io.IOException;

import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.ImageView;
import be.infogroep.justpoker.GameElements.Card;
import be.infogroep.justpoker.messages.ReceiveCardsMessage;
import be.infogroep.justpoker.messages.RegisterMessage;
import be.infogroep.justpoker.messages.SetButtonMessage;
import be.infogroep.justpoker.messages.SetStateMessage;
import be.infogroep.justpoker.messages.SetYourTurn;
import be.infogroep.justpoker.messages.StartNewGameMessage;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

import edu.vub.at.commlib.CommLib;
import edu.vub.at.commlib.CommLibConnectionInfo;
import edu.vub.at.commlib.PlayerState;
import edu.vub.at.commlib.PokerButton;

public class PokerClient {
	public static final String BROADCAST_ACTION = "be.infogroep.justpoker.pokerclient.displayevent";

	private static PokerClient SingletonPokerClient;

	private Client serverConnection;
	private String connectionID = "connectionID";
	private int myClientID;
	private String android_id;
	private String name;
	private String serverIP;
	private AbstractPokerClientActivity gui;
	private volatile PlayerState state;
	private volatile Boolean dealer = false;
	private volatile Boolean smallBlind = false;
	private volatile Boolean bigBlind = false;
	private volatile Boolean myTurn = false;
	private volatile Boolean inGame = false;
	private volatile Card card1;
	private volatile Card card2;

	public PlayerState getState() {
		return state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public PokerClient() {

	}

	public PokerClient(AbstractPokerClientActivity c, String n, String aid, String ip) {
		this.name = n;
		this.serverIP = ip;
		this.gui = c;
		this.state = PlayerState.Unknown;
		this.android_id = aid;
		connectToServer(ip);
	}


	public static PokerClient getInstance() {
		if (SingletonPokerClient == null) {
			SingletonPokerClient = new PokerClient();
		}
		return SingletonPokerClient;
	}
	
	public static PokerClient getInstance(AbstractPokerClientActivity c, String n, String aid, String ip) {
		//if (SingletonPokerClient == null) {
		SingletonPokerClient = new PokerClient(c, n, aid, ip);
		//}
		return SingletonPokerClient;
	}

	public Connection getServerConnection() {
		return serverConnection;
	}

	public void setServerConnection(Client c) {
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

	public Boolean getMyTurn() {
		return myTurn;
	}

	public void setMyTurn(Boolean myTurn) {
		this.myTurn = myTurn;
		gui.startTurn();
	}

	public void endMyTurn() {
		this.myTurn = false;
		gui.endTurn();
	}

	public void sendHello() {
		//new EnsureConnection().execute();
		new SendAsyncMessage(serverConnection, "Owh Yah, Duffman is pounding in the direction!").execute();
	}

	public void sendState(PlayerState s){
		new EnsureConnection().execute();
		new SendAsyncMessage(serverConnection, new SetStateMessage(s, android_id)).execute();
	}

	public Listener listener = new Listener() {

		@Override
		public void connected(Connection c) {
			super.connected(c);
			setServerConnection((Client) c);
			Log.d("justPoker - Client", "Connected to server!");
		}

		@Override
		public void received(Connection c, Object m) {
			super.received(c, m);

			Log.v("justPoker - Client", "Received message " + m.toString());

			if (!(m instanceof KeepAlive)){
				messageParser(c, m);
			}
		}
	};

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
		private Client c;
		private Object o;
		public SendAsyncMessage(Client co, Object obj) {
			this.c = co;
			this.o = obj;
		}
		@Override
		protected Client doInBackground(Void... params) {
			c.sendTCP(o);
			return null;
		}	
	}

	public class EnsureConnection extends AsyncTask<Void, Void, Client> {		
		public EnsureConnection() {
		}
		@Override
		protected Client doInBackground(Void... params) {
			if (! serverConnection.isConnected()){
				try {
					serverConnection.reconnect();
					Log.d("justPoker - Client", "RECONNECT Succeded");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Log.d("justPoker - Client", "RECONNECT FAILED");
				}
			}
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
	
	public Boolean inGame(){
		return inGame;
	}

	public void fold(ImageView cardContainer1, ImageView cardContainer2) {
		if (getMyTurn()){
			sendState(PlayerState.Fold);
			gui.fold(cardContainer1, cardContainer2);
			//endMyTurn();
		} else {
			gui.displayLoggingInfo("It is not your turn yet!");
		}		
	}

	public void bet() {
		if (getMyTurn()) {
			sendState(PlayerState.Bet);
			gui.bet();
			//endMyTurn();
		} else {
			gui.displayLoggingInfo("It is not your turn yet!");
		}
	}

	public void check(ImageView cardContainer1, ImageView cardContainer2) {
		if (getMyTurn()){
			sendState(PlayerState.Check);
			gui.check(cardContainer1, cardContainer2);
			//endMyTurn();
		} else {
			gui.displayLoggingInfo("It is not your turn yet!");
		}

	}


	private void messageParser(Connection c, Object m){
		//DisplayLoggingInfo(msg);
		//handler.postDelayed(test, 2000);
		if (m instanceof RegisterMessage) {
			myClientID = ((RegisterMessage) m).getClient_id();
			serverConnection.sendTCP(new RegisterMessage(myClientID, name, android_id));
			gui.displayLoggingInfo(m);
		}
		if (m instanceof ReceiveCardsMessage){
			inGame = true;
			Card[] cards = ((ReceiveCardsMessage) m).getCards();
			gui.setCards(cards);
		}
		if (m instanceof SetButtonMessage){
			PokerButton b = ((SetButtonMessage) m).getButton();
			switch(b) {
			case BigBlind:
				bigBlind = true;
				gui.setBigBlind();
				break;
			case SmallBlind:
				smallBlind = true;
				gui.setSmallBlind();
				break;
			case Dealer:
				dealer = true;
				gui.setDealer();
				break;
			}
			gui.setBlind(b);
		}
		if (m instanceof SetYourTurn){
			setMyTurn(((SetYourTurn) m).getTurn());
			gui.displayLoggingInfo("It is your turn!");
		}
		if (m instanceof SetStateMessage) {
			endMyTurn();
			state = ((SetStateMessage) m).getState();
			gui.displayLoggingInfo(m);
			switch(state){
			case Fold:
				gui.setFold();
				break;
			case Check:
				gui.setCheck();
				break;
			case Bet:
				gui.setBet();
				break;
			case Call:
				gui.setCall();
				break;
			case Raise:
				gui.setRaise();
				break;
			case ReRaise:
				gui.setReRaise();
				break;
			}
			//gui.setState(state);
		}
		if (m instanceof StartNewGameMessage) {
			inGame = false;
			state = PlayerState.Unknown;
			dealer = false;
			smallBlind = false;
			bigBlind = false;
			myTurn = false;
			card1 = null;
			card2 = null;
			gui.resetPlayerAction();
			gui.resetButton();
			gui.displayLoggingInfo(m);
		}
		if (m instanceof String) {
			gui.displayLoggingInfo(m);
		}
	}

	public Card getCard1() {
		return card1;
	}

	public void setCard1(Card card1) {
		this.card1 = card1;
	}

	public Card getCard2() {
		return card2;
	}

	public void setCard2(Card card2) {
		this.card2 = card2;
	}
}
