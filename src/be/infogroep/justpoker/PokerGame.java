package be.infogroep.justpoker;

import edu.vub.at.commlib.PlayerState;
import be.infogroep.justpoker.GameElements.Card;
import be.infogroep.justpoker.GameElements.Deck;
import be.infogroep.justpoker.messages.SetStateMessage;

public class PokerGame {

	private Integer dealer;
	private Integer bigBlind;
	private Integer smallBlind;
	
	private int smallBlindAmount;
	
	private Integer PlayerTurn;
	
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

	public Integer getDealer() {
		return dealer;
	}

	public void setDealer(Integer dealer) {
		this.dealer = dealer;
	}

	public Integer getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(Integer bigBlind) {
		this.bigBlind = bigBlind;
	}

	public Integer getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(Integer smallBlind) {
		this.smallBlind = smallBlind;
	}

	public int getSmallBlindAmount() {
		return smallBlindAmount;
	}

	public void setSmallBlindAmount(int smallBlindAmount) {
		this.smallBlindAmount = smallBlindAmount;
	}

	public Integer getPlayerTurn() {
		return PlayerTurn;
	}

	public void setPlayerTurn(Integer playerTurn) {
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
	
	public State bet(Integer id){
		currentState = new State(id, PlayerState.Bet);
		return currentState;
	}
	
	public State check(Integer id){
		currentState = new State(id, PlayerState.Check);
		return currentState;
	}
	public State call(Integer id){
		currentState = new State(id, PlayerState.Call);
		return currentState;
	}
	
	public State raise(Integer id){
		currentState = new State(id, PlayerState.Raise);
		return currentState;
	}
	
	
	
	public class State {
		private Integer player;
		private PlayerState state;
		public State(Integer p, PlayerState s){
			this.player = p;
			this.state = s;
		}
		public PlayerState getState() {
			return state;
		}
		public Integer getPlayer() {
			return player;
		}
	}
}
