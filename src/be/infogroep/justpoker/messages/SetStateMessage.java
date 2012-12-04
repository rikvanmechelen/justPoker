package be.infogroep.justpoker.messages;

import edu.vub.at.commlib.PlayerState;

public class SetStateMessage  extends Message {
	private static final long serialVersionUID = 4067126053792155852L;
	private String client_id;
	private PlayerState state;

	public SetStateMessage(PlayerState s, String id) {
		this.setState(s);
		this.setClient_id(id);
	}

	public SetStateMessage() {
	}

	public PlayerState getState() {
		return state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}



}
