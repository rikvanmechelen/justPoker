package be.infogroep.justpoker;

import edu.vub.at.commlib.PokerButton;
import android.widget.ImageView;
import be.infogroep.justpoker.GameElements.Card;


public interface AbstractPokerClientActivity {
	public void displayLoggingInfo(final Object m);

	public void setCards(Card[] cards);

	public void setBlind(PokerButton b);

	public void fold(ImageView cardContainer1, ImageView cardContainer2);

	public void check(ImageView cardContainer1, ImageView cardContainer2);

	public void bet();

	public void startTurn();
}
