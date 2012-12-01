package be.infogroep.justpoker;

import com.esotericsoftware.kryonet.Connection;

import edu.vub.at.commlib.PlayerState;

public class PokerPlayer {
	private String name;
	private Connection connection;
	private int id;
	private volatile PlayerState state;
	
	public PlayerState getState() {
		return state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public PokerPlayer(int i, Connection c){
		this.id = i;
		this.connection = c;
		this.state = PlayerState.Unknown;
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
