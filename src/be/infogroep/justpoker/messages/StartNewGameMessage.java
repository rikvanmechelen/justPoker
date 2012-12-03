package be.infogroep.justpoker.messages;


public class StartNewGameMessage  extends Message {
	private Integer client_id;


	public StartNewGameMessage(int id) {
		this.setClient_id(id);
	}

	public StartNewGameMessage() {
	}

	public Integer getClient_id() {
		return client_id;
	}

	public void setClient_id(Integer client_id) {
		this.client_id = client_id;
	}



}
