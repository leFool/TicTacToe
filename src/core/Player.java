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

	/**
	 * Returns the TileType of the player
	 * 
	 * @return the TileType of the player
	 */
	public TileType getPlayerTileType() {
		return playerTileType;
	}

	private void setType() {
		playerTileType = (index == PLAYER_1) ? TileType.X : TileType.O;
	}

	/**
	 * Checks if the this player instance if a Computer
	 * 
	 * @return {@code true} if this player instance represents a computer
	 */
	public boolean isComp() {
		return isComp;
	}

	public void switchPlayerType() {
		playerTileType = (playerTileType == TileType.X) ? TileType.O : TileType.X;
	}

}
