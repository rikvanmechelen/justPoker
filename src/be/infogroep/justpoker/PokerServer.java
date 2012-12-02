/*
 *	A lot of this code is taken from the wePoker, written by the Ambientalk team 
 */

package be.infogroep.justpoker;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import android.util.Log;
import be.infogroep.justpoker.PokerGame.Round;
import be.infogroep.justpoker.GameElements.Card;
import be.infogroep.justpoker.GameElements.Deck;
import be.infogroep.justpoker.messages.ReceiveCardsMessage;
import be.infogroep.justpoker.messages.RegisterMessage;
import be.infogroep.justpoker.messages.SetButtonMessage;
import be.infogroep.justpoker.messages.SetStateMessage;
import be.infogroep.justpoker.messages.SetYourTurn;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import edu.vub.at.commlib.CommLib;
import edu.vub.at.commlib.PlayerState;
import edu.vub.at.commlib.PokerButton;
import edu.vub.at.commlib.UUIDSerializer;

public class PokerServer {
	// private static final String TAG = "PokerServer";
	public static final String BROADCAST_ACTION = "be.infogroep.justpoker.pokerserver.displayevent";
	private static PokerServer SingletonPokerServer;

	int nextClientID = 0;
	private PokerPlayerMap<Integer, PokerPlayer> connections = new PokerPlayerMap<Integer, PokerPlayer>();
	private volatile Thread serverThread;
	private Server server;
	// private final Handler handler = new Handler();
	// Intent intent;
	private ServerTableActivity gui;
	private String ipAddress;
	
	//game specific stuff
	private PokerGame game;

	public PokerServer() {}

	public PokerServer(ServerTableActivity serverTableActivity, String ip) {
		this.gui = serverTableActivity;
		this.ipAddress = ip;
		this.game = new PokerGame();
	}

	public static PokerServer getInstance() {
		if (SingletonPokerServer == null) {
			SingletonPokerServer = new PokerServer();
		}
		return SingletonPokerServer;
	}

	public static PokerServer getInstance(
			ServerTableActivity serverTableActivity, String ipAddress) {
		// TODO Auto-generated method stub
		//if (SingletonPokerServer == null) {
			SingletonPokerServer = new PokerServer(serverTableActivity,
					ipAddress);
		//}
		return SingletonPokerServer;
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
						Log.d("justPoker - server", "the gui is: " + gui);
						if (!(msg instanceof KeepAlive)) {
							messageParser(c, msg, test);
						}
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

	public synchronized void start() {
		Log.d("justPoker - Server", "Starting server thread...");
		if (serverThread == null) {
			serverThread = new Thread(serverR);
			serverThread.start();
			// new Thread(gameLoop).start();
			// SingletonPokerServer = this;
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
		connections.put(nextClientID, new PokerPlayer(nextClientID,c));
		RegisterMessage m = new RegisterMessage(nextClientID);
		c.sendTCP(m);
		nextClientID++;
	}

	public void registerClient(Connection c, String nickname, int avatar,
			int money) {
		for (Integer i : connections.keySet()) {
			if (connections.get(i).getConnection() == c) {
				// gameLoop.addPlayer(c, i, nickname, avatar, money);
				return;
			}
		}
	}

	public void removeClient(Connection c) {
		// Log.d("wePoker - Server", "Client removed: " + c);
		for (Integer i : connections.keySet()) {
			if (connections.get(i).getConnection() == c) {
				// gameLoop.removePlayer(i);
				connections.remove(i);
				return;
			}
		}
	}
	
	public void showFlop() {
		gui.showFlop(game.getFlop());
	}
	
	public void showTurn() {
		gui.showTurn(game.getTurn());
	}
	
	public void showRiver() {
		gui.showRiver(game.getRiver());
	}

	public void dealCards() {
		Log.d("justPoker - server", "amount of players in values " + connections.values().size());
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			Connection c = player.getConnection();
			if (c.isConnected()) {
				Log.d("justPoker - server", "Dealing cards to " + c.toString());
				Card card1 = game.getDeck().drawFromDeck();
				Card card2 = game.getDeck().drawFromDeck();
				c.sendTCP(new ReceiveCardsMessage(card1, card2));
				player.setCards(card1, card2);
				gui.displayLogginInfo("Dealt cards to " + player.getName());
			}
		}
	}
	
	private boolean roundFinished(){
		boolean betted = false;
		boolean checked = false;
		boolean called = false;
		boolean result = true;
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			PlayerState state = player.getState();
			if (state == PlayerState.Unknown) {
				result = false;
				break;
			}
			if (state == PlayerState.Check) {
				if (betted) {
					result = false;
					break;
				}
				checked = true;
			}
			if (state == PlayerState.Bet || state == PlayerState.Raise) {
				if (betted || checked) {
					result = false;
					break;
				}
				betted = true;
			}
			if (state == PlayerState.Call) {
				called = true;
			}
		}
		if (called && !betted){
			result = false;
		}
		return result;
	}
	
	private void setTurn(PokerPlayer p){
		p.setMyTurn(true);
		p.getConnection().sendTCP(new SetYourTurn(true, p.getId()));
		gui.setTurn(p, connections.indexOfKey(p.getId()));
	}
	
	private void roundSetup(PokerPlayer dealer) {
		game.setDealer(dealer.getId());
		PokerPlayer smallBlind = connections.nextFrom(dealer.getId());
		game.setSmallBlind(smallBlind.getId());
		PokerPlayer bigBlind = connections.nextFrom(smallBlind.getId());
		game.setBigBlind(smallBlind.getId());

		smallBlind.getConnection().sendTCP(new SetButtonMessage(PokerButton.BigBlind, smallBlind.getId()));
		gui.setBigBlind(smallBlind, connections.indexOfKey(smallBlind.getId()));
		smallBlind.getConnection().sendTCP(new SetButtonMessage(PokerButton.SmallBlind, smallBlind.getId()));
		gui.setSmallBlind(smallBlind, connections.indexOfKey(smallBlind.getId()));
		dealer.getConnection().sendTCP(new SetButtonMessage(PokerButton.Dealer, dealer.getId()));
		gui.setDealer(dealer, connections.indexOfKey(dealer.getId()));
	}
	
	private void roundCheck(int client_id) {
		// TODO Auto-generated method stub
		if (roundFinished()){
			endRoundCleanup();
			setTurn(connections.nextUnfoldedFrom(game.getDealer()));
			if (game.getRound() == Round.PreFlopBet){	
				showFlop();
			}
			if (game.getRound() == Round.FlopBet){
				showTurn();
			}
			if (game.getRound() == Round.TurnBet){
				showRiver();
			}
			if (game.getRound() == Round.RiverBet){
				//showFlop();
			}
			game.nextRound();
			
		} else{
			setTurn(connections.nextFrom(client_id));
		}
	}
	
	private void endRoundCleanup(){
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			if (player.getState() != PlayerState.Fold) {
				player.resetState();
			}
		}
	}

	public void startGame() {
		// DisplayLoggingInfo("Starting a game!!!!");
		Log.d("justPoker - server", "starting game");
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			Log.d("justPoker - server", "got PokerPlayer from connections "+player);
			Connection c = player.getConnection();
			if (c.isConnected()) {
				Log.d("justPoker - server", "sending to " + c.toString());
				c.sendTCP("Starting the game!");
				gui.setPlaying(player, connections.indexOfKey(player.getId()));
			}
		}
		game = new PokerGame();
		roundSetup(connections.getFirst());
		dealCards();
		setTurn(connections.nextFrom(game.getSmallBlind()));
	}
	
	private void messageParser(Connection c, Object msg, Runnable r) {
		// DisplayLoggingInfo(msg);
		// handler.postDelayed(test, 2000);
		if (msg instanceof RegisterMessage) {
			RegisterMessage rm = (RegisterMessage) msg;
			PokerPlayer p = connections.get(rm.getClient_id());
			p.setName(rm.getName());
			gui.displayLogginInfo(rm.getName() + " connected");
			gui.addPlayer(p, connections.indexOfKey(rm.getClient_id()));
			// gui.displayLogginInfo("someone connected");
		}
		if (msg instanceof SetStateMessage) {
			SetStateMessage st = (SetStateMessage) msg;
			Log.d("justPoker - server", "received a set state: "+st.getState());
			parseState(st);
			roundCheck(st.getClient_id());
		}
		// handler.post(r);
	}

	

	private void parseState(SetStateMessage st) {
		PlayerState state = st.getState();
		Integer client_id = st.getClient_id();
		PokerPlayer player = connections.get(client_id);
		player.endMyTurn();
		if (state == PlayerState.Fold) {
			player.setState(state);
			gui.displayLogginInfo(player.getName()+" folded");
			gui.setFolded(player, connections.indexOfKey(client_id));
		}
		if (state == PlayerState.Check) {
			connections.get(st.getClient_id()).setState(state);
			gui.displayLogginInfo(connections.get(st.getClient_id()).getName()+" checked");
			gui.setPlaying(player, connections.indexOfKey(client_id));
		}
		if (state == PlayerState.Bet) {
			connections.get(st.getClient_id()).setState(state);
			gui.displayLogginInfo(connections.get(st.getClient_id()).getName()+" checked");
			gui.setPlaying(player, connections.indexOfKey(client_id));
		}
	}
}
