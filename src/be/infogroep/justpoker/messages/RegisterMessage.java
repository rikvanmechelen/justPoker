package be.infogroep.justpoker.messages;

public class RegisterMessage extends Message{
	private String name;
	private Integer client_id;
	
	public RegisterMessage(String n){
		name = n;
	}
	
	public RegisterMessage(){
	}
	
	public RegisterMessage(Integer c){
		client_id = c;
	}
	
	public RegisterMessage(Integer c, String n){
		client_id = c;
		name = n;
	}

	public String getName() {
		return name;
	}

	public int getClient_id() {
		return client_id;
	}
	
	

}
