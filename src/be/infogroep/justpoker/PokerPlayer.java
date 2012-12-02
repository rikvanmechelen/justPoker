package be.infogroep.justpoker;

import be.infogroep.justpoker.GameElements.Card;

import com.esotericsoftware.kryonet.Connection;

import edu.vub.at.commlib.PlayerState;

public class PokerPlayer {
	private String name;
	private Connection connection;
	private int id;
	private volatile PlayerState state;
	private volatile Card[] cards;
	private volatile Boolean myTurn = false;
	
	public PlayerState getState() {
		return state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}
	
	public void resetState() {
		this.state = PlayerState.Unknown;
	}

	public PokerPlayer(int i, Connection c){
		this.id = i;
		this.connection = c;
		this.state = PlayerState.Unknown;
		this.cards = new Card[2];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Connection getConnection() {
		return connection;
	}

	public int getId() {
		return id;
	}

	public Card[] getCards() {
		return cards;
	}

	public void setCards(Card card1, Card card2) {
		this.cards[0] = card1;
		this.cards[1] = card2;
	}

	public Boolean getMyTurn() {
		return myTurn;
	}

	public void setMyTurn(Boolean myTurn) {
		this.myTurn = myTurn;
	}
	
	public void endMyTurn() {
		this.myTurn = false;
	}
	
}
