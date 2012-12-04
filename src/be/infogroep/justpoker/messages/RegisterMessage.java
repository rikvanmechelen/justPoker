package be.infogroep.justpoker.messages;

public class RegisterMessage extends Message{
	private static final long serialVersionUID = 8725638069945077184L;
	private String name;
	private Integer client_id;
	private String android_id;
	
	public RegisterMessage(String n){
		name = n;
	}
	
	public RegisterMessage(){
	}
	
	public RegisterMessage(Integer c){
		client_id = c;
	}
	
	public RegisterMessage(Integer c, String n, String aid){
		client_id = c;
		name = n;
		android_id = aid;
	}

	public String getName() {
		return name;
	}

	public int getClient_id() {
		return client_id;
	}

	public String getAndroid_id() {
		return android_id;
	}

}
