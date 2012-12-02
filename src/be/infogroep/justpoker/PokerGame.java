package be.infogroep.justpoker;

import be.infogroep.justpoker.GameElements.Card;
import be.infogroep.justpoker.GameElements.Deck;

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
	
	public PokerGame() {
		deck = new Deck();
		deck.shuffle();
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
	
	
}
