package be.infogroep.justpoker.messages;

import edu.vub.at.commlib.PlayerState;

public class SetYourTurn  extends Message {
	private String client_id;
	private Boolean turn;

	public Boolean getTurn() {
		return turn;
	}

	public void setTurn(Boolean turn) {
		this.turn = turn;
	}

	public SetYourTurn(Boolean t, String id) {
		this.setClient_id(id);
		this.setTurn(t);
	}

	public SetYourTurn() {
	}

	

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}



}
