package gui;

import java.io.IOException;
import java.util.Optional;

import core.Message;
import core.Network;
import core.Opponent;
import core.Player;
import core.Tile;
import enums.GameMode;
import enums.MessageType;
import enums.TileType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import main.TicTacToe;
import user.Settings;
import vlad.MatrixIndex;

public class Game extends GridPane {
	
	private final TicTacToe main;
	private final GameMode MODE;
	/**
	 * equals to 5 space chars
	 */
	private final String space = "     ";
	private final int BOARD_SIZE = 3;

	private Network net;
	private Settings user;
	private Tile[][] board;
	private Tile[] winnerTiles;
	private Tile hintTile;
	private Player[] players;
	private ButtonType toLobby, rematch;
	private Alert tieAlert, winAlert;
	private Label info;
	private int currentPlayer;
	private int round;
	private int[] wins;

	public Game(TicTacToe main, GameMode mode) {
		super();
		this.main = main;
		this.MODE = mode;
		winnerTiles = new Tile[BOARD_SIZE];
		wins = new int[2];
		info = new Label();
		hintTile = null;
		setBackground(TicTacToe.BACKGROUND);
		setAlignment(Pos.CENTER);
		setVgap(5);
		setHgap(5);
		setBoard();
		setAlerts();
		setPlayers();
		setUserSettings();
		setGameMenu();
		setOpponent();
	}

	public static void end() {
		Opponent.setPlaying(false);
		Tile.resetFilledTilesCounter();
	}

	public BorderPane getLayout() {
		BorderPane bp = new BorderPane(this);
		bp.setTop(info);
		return bp;
	}

	private void setUserSettings() {
		if (isOnline())
			return;
		updateInfo(main.getSettingsChooser());
	}

	private void setBoard() {
		board = new Tile[BOARD_SIZE][BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				board[i][j] = new Tile(i, j);
				Tile temp = board[i][j];
				temp.setOnMouseClicked(e -> move(temp));
				add(temp, i, j);
			}
		}
	}

	private void setAlerts() {
		toLobby = new ButtonType("Main Menu");
		rematch = new ButtonType("Rematch");
		tieAlert = new Alert(AlertType.INFORMATION, "No one has won the game!", rematch, toLobby);
		tieAlert.setTitle("Game Result");
		tieAlert.setHeaderText("A Tie");
		winAlert = new Alert(AlertType.INFORMATION, "Congratulations!", rematch, toLobby);
		winAlert.setTitle("Game Result");
	}

	private void setPlayers() {
		players = new Player[2];
		players[Player.PLAYER_1] = new Player(Player.PLAYER_1);
		switch (MODE) {
			case VS_COMPUTER:
				players[Player.PLAYER_2] = new Player(Player.COMPUTER);
				break;
			case ONLINE:
			case VS_PLAYER:
				players[Player.PLAYER_2] = new Player(Player.PLAYER_2);
		}
		currentPlayer = Player.PLAYER_1;
		round = 1;
	}

	private void setOpponent() {
		if (!isOnline())
			return;
		this.net = main.getNetwork();
		new Opponent(main, this);
	}

	public void updateInfo(Settings settings) {
		if (settings != null)
			user = settings;
		String si;
		if (MODE == GameMode.VS_COMPUTER) {
			si = String.format(space + "Round: %d | %s vs. %s | Now Playing: %s | %d:%d | %s", round,
					user.getPlayer1Name(), user.getComputerName(), currentPlayerName(), wins[0], wins[1],
					user.getGameDifficulty());
		}
		else {
			si = String.format(space + "Round: %d | %s vs. %s | Now Playing: %s | %d:%d", round, user.getPlayer1Name(),
					user.getPlayer2Name(), currentPlayerName(), wins[0], wins[1]);
		}
		info.setText(si);
	}

	private void setGameMenu() {
		MenuItem hint = new MenuItem("Hint");
		hint.setOnAction(e -> showHint());
		hint.setAccelerator(new KeyCodeCombination(KeyCode.H));
		main.getGameMenu().getItems().add(hint);
	}

	private TileType currentPlayerType() {
		return players[currentPlayer].getPlayerTileType();
	}

	private String currentPlayerName() {
		switch (players[currentPlayer].getIndex()) {
			case Player.COMPUTER:
				return user.getComputerName();
			case Player.PLAYER_1:
				return user.getPlayer1Name();
			case Player.PLAYER_2:
				return user.getPlayer2Name();
			default:
				return null;
		}
	}

	private boolean cantLose() {
		return Tile.getFilledTilesCounter() < 4;
	}

	private boolean isEndOfRound() {
		return Tile.getFilledTilesCounter() == 9;
	}

	private boolean isOnline() {
		return MODE == GameMode.ONLINE;
	}

	private synchronized void checkRound() {
		boolean isWin = checkBoard();
		boolean isEnd = isEndOfRound();
		if (!isWin && !isEnd) {
			switchCurrentPlayer();
			if (MODE == GameMode.VS_COMPUTER)
				Platform.runLater(() -> drawComp());
			return;
		}
		if (isOnline()) {
			try {
				net.send(new Message(MessageType.END));
				wait();
			} catch (Exception e1) {
				e1.printStackTrace();
				net.error("An Unknown Error Occured!");
			}
		}
		Optional<ButtonType> action;
		if (!isWin && isEnd)
			action = tieAlert.showAndWait();
		else
			action = win();
		if (action.get() == rematch) {
			swapAndReset();
			if (isOnline())
				notify();
			return;
		}
		if (isOnline())
			notify();
		main.restart();
	}

	public void drawPlayer(Tile tile, boolean isSent) {
		if (players[currentPlayer].isComp())
			return;
		resetHint();
		if (!tile.setType(currentPlayerType()))
			return;
		if (!isSent)
			sendMove(tile);
		checkRound();
		updateInfo(null);
	}

	private void drawComp() {
		if (!players[currentPlayer].isComp())
			return;
		if (isEndOfRound())
			return;
		Tile tile = getTileByDifficulty();
		tile.setType(currentPlayerType());
		checkRound();
		updateInfo(null);
	}

	private void move(Tile tile) {
		if (isOnline()) {
			if (net.isHost()) {
				if (currentPlayer != Player.PLAYER_1)
					return;
			}
			else if (currentPlayer != Player.PLAYER_2)
				return;
		}
		drawPlayer(tile, false);
	}

	private void sendMove(Tile move) {
		if (!isOnline())
			return;
		try {
			net.send(new Message(MessageType.MOVE, move.getIndex()));
		} catch (IOException e) {
			net.error("Opponent Disconnected");
		}
	}

	private Tile getTileByDifficulty() {
		Tile tile = getWinningTile();
		switch (user.getGameDifficulty()) {
			case EASY:
				if (rndAction())
					tile = (tile == null) ? getThreatedTile() : tile;
				return (tile == null) ? getRandomEmptyTile() : tile;
			case HARD:
				return (tile == null) ? getBestTile() : tile;
			case NORMAL:
			default:
				tile = (tile == null) ? getThreatedTile() : tile;
				return (tile == null) ? getRandomEmptyTile() : tile;
		}
	}

	public Tile getTileByIndex(MatrixIndex mi) {
		return board[mi.i][mi.j];
	}

	private void showHint() {
		hintTile = getWinningTile();
		if (hintTile == null)
			hintTile = getBestTile();
		hintTile.drawHint();
	}

	private void resetHint() {
		if (hintTile == null)
			return;
		hintTile.clearHint();
		hintTile = null;
	}

	private boolean rndAction() {
		int isBlock = (int) (Math.random() * 2);
		return (isBlock == 0) ? false : true;
	}

	/**
	 * Checks the board if the current player has won
	 * 
	 * @return {@code true} if the a tie or current player has won and false if
	 *         not
	 */
	private boolean checkBoard() {
		if (cantLose())
			return false;
		int len = winnerTiles.length;
		// check rows
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++)
				winnerTiles[j] = board[j][i];
			if (checkTrio(winnerTiles))
				return true;
		}
		// check columns
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++)
				winnerTiles[j] = board[i][j];
			if (checkTrio(winnerTiles))
				return true;
		}
		// check main diagonal
		for (int i = 0; i < len; i++)
			winnerTiles[i] = board[i][i];
		if (checkTrio(winnerTiles))
			return true;
		// check second diagonal
		for (int i = 0; i < len; i++)
			winnerTiles[i] = board[i][len - 1 - i];
		return checkTrio(winnerTiles);
	}

	private boolean checkTrio(Tile[] trio) {
		for (Tile tile : trio) {
			if (tile.getTileType() != currentPlayerType())
				return false;
		}
		return true;
	}

	private Optional<ButtonType> win() {
		wins[currentPlayer]++;
		for (Tile win : winnerTiles)
			win.drawWin();
		winAlert.setHeaderText(currentPlayerName() + " has won");
		return winAlert.showAndWait();
	}

	private void swapAndReset() {
		for (Tile[] tiles : board)
			for (Tile tile : tiles)
				tile.reset();
		for (Player player : players)
			player.switchPlayerType();
		currentPlayer = round++ % 2;
		updateInfo(null);
		if (players[currentPlayer].isComp())
			Platform.runLater(() -> drawComp());
	}

	private void switchCurrentPlayer() {
		currentPlayer = (currentPlayer == Player.PLAYER_1) ? Player.PLAYER_2 : Player.PLAYER_1;
	}

	private Tile getRandomEmptyTile() {
		Tile tile;
		MatrixIndex ind;
		do {
			ind = MatrixIndex.getRandomMatrixIndex(BOARD_SIZE, BOARD_SIZE);
			tile = board[ind.i][ind.j];
		} while (!tile.isEmpty());
		return tile;
	}

	private Tile getWinningTile() {
		Tile winner = null;
		TileType opponent = currentPlayerType();
		CHECK: for (Tile[] tiles : board) {
			for (Tile tile : tiles)
				if (tile.setType(opponent)) {
					if (checkBoard())
						winner = tile;
					tile.reset();
					if (winner != null)
						break CHECK;
				}
		}
		return winner;
	}

	private Tile getThreatedTile() {
		switchCurrentPlayer();
		Tile threated = null;
		TileType opponent = currentPlayerType();
		CHECK: for (Tile[] tiles : board) {
			for (Tile tile : tiles)
				if (tile.setType(opponent)) {
					Boolean b = checkBoard();
					if (b) {
						threated = tile;
					}
					tile.reset();
					if (threated != null)
						break CHECK;
				}
		}
		switchCurrentPlayer();
		return threated;
	}

	private Tile getBestTile() {
		Tile best = getThreatedTile();
		if (best != null)
			return best;
		// middle for outmost block
		best = board[1][1];
		if (best.isEmpty())
			return best;
		if (Tile.getFilledTilesCounter() != 1)
			return getRandomEmptyTile();
		Tile[] ts = new Tile[] { board[0][0], board[2][2], board[0][2], board[2][0] };
		int i;
		do {
			i = (int) (Math.random() * 4);
		} while (!ts[i].isEmpty());
		return ts[i];
	}

}