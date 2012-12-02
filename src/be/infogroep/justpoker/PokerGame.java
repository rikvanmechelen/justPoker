package be.infogroep.justpoker;

public class PokerGame {

	private Integer dealer;
	private Integer bigBlind;
	private Integer smallBlind;
	
	private int smallBlindAmount;
	
	private Integer PlayerTurn;
	
	public PokerGame() {
		// TODO Auto-generated constructor stub
	}

	public Integer getDealer() {
		return dealer;
	}

	public void setDealer(Integer dealer) {
		this.dealer = dealer;
	}

	public Integer getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(Integer bigBlind) {
		this.bigBlind = bigBlind;
	}

	public Integer getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(Integer smallBlind) {
		this.smallBlind = smallBlind;
	}

	public int getSmallBlindAmount() {
		return smallBlindAmount;
	}

	public void setSmallBlindAmount(int smallBlindAmount) {
		this.smallBlindAmount = smallBlindAmount;
	}

	public Integer getPlayerTurn() {
		return PlayerTurn;
	}

	public void setPlayerTurn(Integer playerTurn) {
		PlayerTurn = playerTurn;
	}

	
}
