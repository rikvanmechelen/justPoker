package be.infogroep.justpoker;

import edu.vub.at.commlib.PlayerState;
import be.infogroep.justpoker.GameElements.Card;
import be.infogroep.justpoker.GameElements.Deck;
import be.infogroep.justpoker.messages.SetStateMessage;

public class PokerGame {

	private String dealer;
	private String bigBlind;
	private String smallBlind;
	
	private int smallBlindAmount;
	
	private String PlayerTurn;
	
	private Deck deck;
	
	private Card[] flop;
	private Card turn;
	private Card river;
	private Round round;
	
	public State currentState;
	
	public PokerGame() {
		this.deck = new Deck();
		deck.shuffle();
		newRound();
	}

	public String getDealer() {
		return dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}

	public String getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(String bigBlind) {
		this.bigBlind = bigBlind;
	}

	public String getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(String smallBlind) {
		this.smallBlind = smallBlind;
	}

	public int getSmallBlindAmount() {
		return smallBlindAmount;
	}

	public void setSmallBlindAmount(int smallBlindAmount) {
		this.smallBlindAmount = smallBlindAmount;
	}

	public String getPlayerTurn() {
		return PlayerTurn;
	}

	public void setPlayerTurn(String playerTurn) {
		PlayerTurn = playerTurn;
	}
	
	public Deck getDeck() {
		return deck;
	}
	
	public Deck resetDeck() {
		this.deck = new Deck();
		deck.shuffle();
		this.flop = null;
		this.turn = null;
		this.river = null;
		return deck;
	}

	public Card[] getFlop() {
		this.flop = deck.drawCards(3);
		return flop;
	}

	public Card getTurn() {
		this.turn = deck.drawFromDeck();
		return turn;
	}

	public Card getRiver() {
		this.river = deck.drawFromDeck();
		return river;
	}
	
	public Round nextRound() {
		round = round.getNext();
		return round;
	}
	
	public Round newRound() {
		round = Round.PreFlopBet;
		resetCurrentState();
		return round;
	}

	public Round getRound() {
		return round;
	}
	
	public enum Round {
		PreFlopBet, FlopBet, TurnBet, RiverBet;
		public Round getNext() {
			return values()[(ordinal()+1) % values().length];
		}

	}
	
	public State resetCurrentState() {
		this.currentState = new State(null, PlayerState.Check);
		return currentState;
	}
	
	public State getCurrentState() {
		// TODO Auto-generated method stub
		return currentState;
	}
	
	public State bet(String id){
		currentState = new State(id, PlayerState.Bet);
		return currentState;
	}
	
	public State check(String id){
		currentState = new State(id, PlayerState.Check);
		return currentState;
	}
	public State call(String id){
		currentState = new State(id, PlayerState.Call);
		return currentState;
	}
	
	public State raise(String id){
		currentState = new State(id, PlayerState.Raise);
		return currentState;
	}
	
	
	
	public class State {
		private String player;
		private PlayerState state;
		public State(String p, PlayerState s){
			this.player = p;
			this.state = s;
		}
		public PlayerState getState() {
			return state;
		}
		public String getPlayer() {
			return player;
		}
	}
}
