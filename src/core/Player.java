package core;

import enums.TileType;

public class Player {

	public static final int COMPUTER = -1;
	public static final int PLAYER_1 = 0;
	public static final int PLAYER_2 = 1;

	private int index;
	private boolean isComp;
	private TileType playerTileType;

	public Player(int index) {
		this.index = index;
		isComp = (index == COMPUTER);
		setType();
	}
	
	public int getIndex() {
		return index;
	}
	
	public TileType getPlayerTileType() {
		return playerTileType;
	}

	private void setType() {
		playerTileType = (index == PLAYER_1) ? TileType.X : TileType.O;
	}
	
	public boolean isComp() {
		return isComp;
	}

	public void switchPlayerType() {
		playerTileType = (playerTileType == TileType.X) ? TileType.O : TileType.X;
	}
	
}
