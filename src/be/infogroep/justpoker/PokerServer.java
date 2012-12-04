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
import be.infogroep.justpoker.messages.NewRoundMessage;
import be.infogroep.justpoker.messages.ReceiveCardsMessage;
import be.infogroep.justpoker.messages.RegisterMessage;
import be.infogroep.justpoker.messages.SetButtonMessage;
import be.infogroep.justpoker.messages.SetStateMessage;
import be.infogroep.justpoker.messages.SetYourTurn;
import be.infogroep.justpoker.messages.StartNewGameMessage;

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
	private PokerPlayerMap<String, PokerPlayer> connections = new PokerPlayerMap<String, PokerPlayer>();
	private volatile Thread serverThread;
	private Server server;
	// private final Handler handler = new Handler();
	// Intent intent;
	private ServerTableActivity gui;
	private String ipAddress;
	
	//game specific stuff
	private PokerGame match;

	public PokerServer() {}

	public PokerServer(ServerTableActivity serverTableActivity, String ip) {
		this.gui = serverTableActivity;
		this.ipAddress = ip;
		this.match = new PokerGame();
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
		//connections.put(nextClientID, new PokerPlayer(nextClientID,c));
		RegisterMessage m = new RegisterMessage(nextClientID);
		c.sendTCP(m);
		nextClientID++;
	}

	public void registerClient(Connection c, String nickname, int avatar,
			int money) {
		for (String i : connections.keySet()) {
			if (connections.get(i).getConnection() == c) {
				// gameLoop.addPlayer(c, i, nickname, avatar, money);
				return;
			}
		}
	}

	public void removeClient(Connection c) {
		// Log.d("wePoker - Server", "Client removed: " + c);
		for (String i : connections.keySet()) {
			if (connections.get(i).getConnection() == c) {
				// gameLoop.removePlayer(i);
				//connections.remove(i);
				return;
			}
		}
	}
	
	public void showFlop() {
		gui.showFlop(match.getFlop());
	}
	
	public void showTurn() {
		gui.showTurn(match.getTurn());
	}
	
	public void showRiver() {
		gui.showRiver(match.getRiver());
	}

	public void dealCards() {
		Log.d("justPoker - server", "amount of players in values " + connections.values().size());
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			Connection c = player.getConnection();
			if (c.isConnected()) {
				Log.d("justPoker - server", "Dealing cards to " + c.toString());
				Card card1 = match.getDeck().drawFromDeck();
				Card card2 = match.getDeck().drawFromDeck();
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
			if (state == PlayerState.Playing ) {
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
		match.setDealer(dealer.getId());
		PokerPlayer smallBlind = connections.nextFrom(dealer.getId());
		match.setSmallBlind(smallBlind.getId());
		PokerPlayer bigBlind = connections.nextFrom(smallBlind.getId());
		match.setBigBlind(bigBlind.getId());

		bigBlind.getConnection().sendTCP(new SetButtonMessage(PokerButton.BigBlind, bigBlind.getId()));
		gui.setBigBlind(bigBlind, connections.indexOfKey(bigBlind.getId()));
		smallBlind.getConnection().sendTCP(new SetButtonMessage(PokerButton.SmallBlind, smallBlind.getId()));
		gui.setSmallBlind(smallBlind, connections.indexOfKey(smallBlind.getId()));
		dealer.getConnection().sendTCP(new SetButtonMessage(PokerButton.Dealer, dealer.getId()));
		gui.setDealer(dealer, connections.indexOfKey(dealer.getId()));
	}
	
	private void roundCheck(String client_id) {
		// TODO Auto-generated method stub
		if (roundFinished()){
			endRoundCleanup();
			switch(match.getRound()) {
			case PreFlopBet:
				setTurn(connections.nextUnfoldedFrom(match.getDealer()));
				gui.displayLogginInfo("Pre flop bet is over, going to flop bet");
				showFlop();
				match.nextRound();
				break;
			case FlopBet:
				setTurn(connections.nextUnfoldedFrom(match.getDealer()));
				gui.displayLogginInfo("flop bet is over, going to turn bet");
				showTurn();
				match.nextRound();
				break;
			case TurnBet:
				setTurn(connections.nextUnfoldedFrom(match.getDealer()));
				gui.displayLogginInfo("turn bet is over, going to river bet");				
				showRiver();
				match.nextRound();
				break;
			case RiverBet:
				gui.displayLogginInfo("Game ended, get ready for the next game :)");
				//startNewGame();
				break;
			}
		} else {
			setTurn(connections.nextUnfoldedFrom(client_id));
		}
	}
	
	
	private void endRoundCleanup(){
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			if (! (player.getState() == PlayerState.Fold || player.getState() == PlayerState.Unknown)) {
				player.resetState();
				player.getConnection().sendTCP(new NewRoundMessage());
				gui.resetAction(player, connections.indexOfKey(player.getId()));
			}
		}
		match.resetCurrentState();
	}
	
	public void clearTable(){
		gui.clearTable();
	}
	
	public void startNewGame() {
		clearTable();
		match.newRound();
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			Connection c = player.getConnection();
			int index = connections.indexOfKey(player.getId());
			if (c.isConnected()) {
				player.resetState();
				c.sendTCP(new StartNewGameMessage());
				gui.resetPlayer(player, index);
			}
		}
		//gui.resetCards();
		roundSetup(connections.nextFrom(match.getDealer()));
		dealCards();
		setTurn(connections.nextFrom(match.getBigBlind()));
	}

	public void startMatch() {
		// DisplayLoggingInfo("Starting a game!!!!");
		Log.d("justPoker - server", "starting game");
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			Connection c = player.getConnection();
			if (c.isConnected()) {
				Log.d("justPoker - server", "sending to " + c.toString());
				c.sendTCP("Starting the game!");
				player.resetState();
				gui.setPlaying(player, connections.indexOfKey(player.getId()));
			}
		}
		match = new PokerGame();
		roundSetup(connections.getFirst());
		dealCards();
		setTurn(connections.nextFrom(match.getBigBlind()));
	}
	
	private void messageParser(Connection c, Object msg, Runnable r) {
		// DisplayLoggingInfo(msg);
		// handler.postDelayed(test, 2000);
		connections.logKeys();
		if (msg instanceof RegisterMessage) {
			
			RegisterMessage rm = (RegisterMessage) msg;
			PokerPlayer p = connections.get(rm.getAndroid_id());
			if (p == null) {
				p = new PokerPlayer(rm.getAndroid_id(),c);
				connections.put(rm.getAndroid_id(), p);
				connections.logKeys();
			} else {
				p.setConnection(c);
			}
			p.setName(rm.getName());
			gui.displayLogginInfo(rm.getName() + " connected");
			gui.addPlayer(p, connections.indexOfKey(rm.getAndroid_id()));
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

	private void notifyOtherPlayers(String pl_id, String message){
		for (Iterator<PokerPlayer> iterator = connections.values().iterator(); iterator
				.hasNext();) {
			PokerPlayer player = iterator.next();
			Log.d("justPoker - server", "player id = "+player.getId()+" pl_id = "+pl_id);
			if (! player.getId().equals(pl_id)){
				player.getConnection().sendTCP(message);
			}
		}
	}

	private void parseState(SetStateMessage st) {
		PlayerState state = st.getState();
		String client_id = st.getClient_id();
		PokerPlayer player = connections.get(client_id);
		int index = connections.indexOfKey(client_id);
		player.endMyTurn();
		switch(state){
		case Fold:
			player.getConnection().sendTCP(new SetStateMessage(PlayerState.Fold, client_id));
			notifyOtherPlayers(client_id, player.getName()+" Folded");
			player.setState(state);
			gui.displayLogginInfo(player.getName()+" Folded");
			gui.setFolded(player, index);
			gui.setFold(player, index);
			break;
		case Check:		
			switch(match.getCurrentState().getState()){
			case Bet:
				player.setState(PlayerState.Call);
				player.getConnection().sendTCP(new SetStateMessage(PlayerState.Call, client_id));
				notifyOtherPlayers(client_id, player.getName()+" Called");
				match.raise(client_id);
				gui.displayLogginInfo(player.getName()+" Called");
				gui.setPlaying(player, index);
				gui.setCall(player, index);
				break;
			case Raise:
				player.setState(PlayerState.Call);
				player.getConnection().sendTCP(new SetStateMessage(PlayerState.Call, client_id));
				notifyOtherPlayers(client_id, player.getName()+" Called");
				match.raise(client_id);
				gui.displayLogginInfo(player.getName()+" Called");
				gui.setPlaying(player, index);
				gui.setCall(player, index);
				break;
			default:
				player.setState(state);
				player.getConnection().sendTCP(new SetStateMessage(PlayerState.Check, client_id));
				notifyOtherPlayers(client_id, player.getName()+" Checked");
				//match.bet(client_id);
				gui.displayLogginInfo(player.getName()+" Checked");
				gui.setPlaying(player, index);
				gui.setCheck(player, index);
				break;
			}
			break;
		case Bet:
			player.setState(state);
			switch(match.getCurrentState().getState()){
			case Bet:
				player.getConnection().sendTCP(new SetStateMessage(PlayerState.Raise, client_id));
				notifyOtherPlayers(client_id, player.getName()+" Raised");
				match.raise(client_id);
				gui.displayLogginInfo(player.getName()+" Raised");
				gui.setPlaying(player, index);
				gui.setRaise(player, index);
				break;
			case Check:
				player.getConnection().sendTCP(new SetStateMessage(PlayerState.Bet, client_id));
				notifyOtherPlayers(client_id, player.getName()+" Bet");
				match.bet(client_id);
				gui.displayLogginInfo(player.getName()+" Bet");
				gui.setPlaying(player, index);
				gui.setBet(player, index);
				break;
			case Raise:
				player.getConnection().sendTCP(new SetStateMessage(PlayerState.ReRaise, client_id));
				notifyOtherPlayers(client_id, player.getName()+" Re-Raised "+connections.get(match.getCurrentState().getPlayer()).getName());
				match.raise(client_id);
				gui.displayLogginInfo(player.getName()+" Re-Raised "+connections.get(match.getCurrentState().getPlayer()).getName());
				gui.setPlaying(player, index);
				gui.setRaise(player, index);
				break;
			default:
				player.getConnection().sendTCP(new SetStateMessage(PlayerState.Bet, client_id));
				notifyOtherPlayers(client_id, player.getName()+" Bet");
				match.bet(client_id);
				gui.displayLogginInfo(player.getName()+" Bet");
				gui.setPlaying(player, index);
				gui.setBet(player, index);
				break;
			}
			break;
		}
	}
}
