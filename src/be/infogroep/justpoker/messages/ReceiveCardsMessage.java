package be.infogroep.justpoker.messages;

import be.infogroep.justpoker.GameElements.Card;

public class ReceiveCardsMessage extends Message {
	private static final long serialVersionUID = -100116881386445548L;
	private Integer client_id;
	private Card[] cards;

	public ReceiveCardsMessage(Card card1, Card card2) {
		cards = new Card[2];
		cards[0]=card1;
		cards[1]=card2;
	}

	public ReceiveCardsMessage() {
	}

	public Card[] getCards() {
		return cards;
	}

	public int getClient_id() {
		return client_id;
	}

}
