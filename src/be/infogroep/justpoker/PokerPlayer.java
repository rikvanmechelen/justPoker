package be.infogroep.justpoker;

import com.esotericsoftware.kryonet.Connection;

public class PokerPlayer {
	private String name;
	private Connection connection;
	private int id;
	
	public PokerPlayer(int i, Connection c){
		this.id = i;
		this.connection = c;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Connection getConnection() {
		return connection;
	}

	public int getId() {
		return id;
	}
	
}
