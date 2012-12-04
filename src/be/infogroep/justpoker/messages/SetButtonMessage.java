package be.infogroep.justpoker.messages;

import edu.vub.at.commlib.PokerButton;

public class SetButtonMessage  extends Message {
	private static final long serialVersionUID = 1728704905150265306L;
	private String client_id;
	private PokerButton button;

	public SetButtonMessage(PokerButton s, String id) {
		this.setButton(s);
		this.setClient_id(id);
	}

	public SetButtonMessage() {
	}

	public PokerButton getButton() {
		return button;
	}

	public void setButton(PokerButton state) {
		this.button = state;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}



}
