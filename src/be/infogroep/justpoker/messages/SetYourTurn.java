package be.infogroep.justpoker.messages;

import edu.vub.at.commlib.PlayerState;

public class SetYourTurn  extends Message {
	private Integer client_id;
	private Boolean turn;

	public Boolean getTurn() {
		return turn;
	}

	public void setTurn(Boolean turn) {
		this.turn = turn;
	}

	public SetYourTurn(Boolean t, int id) {
		this.setClient_id(id);
		this.setTurn(t);
	}

	public SetYourTurn() {
	}

	

	public Integer getClient_id() {
		return client_id;
	}

	public void setClient_id(Integer client_id) {
		this.client_id = client_id;
	}



}
