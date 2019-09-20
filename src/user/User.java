package user;

import enums.GameDifficulty;

public interface User {

	public default String getPlayer1Name() {
		return null;
	}

	public default String getPlayer2Name() {
		return null;
	}

	public default String getComputerName() {
		return null;
	}

	public default String getOnlineName() {
		return null;
	}

	public default GameDifficulty getGameDifficulty() {
		return null;
	}

}
