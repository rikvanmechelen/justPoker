package be.infogroep.justpoker;

import edu.vub.at.commlib.PokerButton;
import be.infogroep.justpoker.GameElements.Card;


public interface AbstractPokerClientActivity {
	public void displayLoggingInfo(final Object m);

	public void setCards(Card[] cards);

	public void setBlind(PokerButton b);
}
