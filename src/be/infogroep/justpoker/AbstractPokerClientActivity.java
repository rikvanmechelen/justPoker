package be.infogroep.justpoker;

import be.infogroep.justpoker.GameElements.Card;


public interface AbstractPokerClientActivity {
	public void displayLoggingInfo(final Object m);

	public void setCards(Card[] cards);
}
