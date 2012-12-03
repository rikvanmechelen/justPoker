package be.infogroep.justpoker;

import edu.vub.at.commlib.PlayerState;
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

	public void endTurn();

	public void setBet();
	public void setCall();
	public void setCheck();
	public void setFold();
	public void setRaise();
	public void setReRaise();

	public void resetPlayerAction();

	public void setBigBlind();

	public void setSmallBlind();

	public void setDealer();

	public void resetButton();
}
