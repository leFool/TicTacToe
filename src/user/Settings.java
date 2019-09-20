package user;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import enums.GameDifficulty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Settings implements User, Serializable {

	private static final long serialVersionUID = 5674337691876672432L;

	private static final String FILE_NAME = "jtttsettings.ser";
	private static final String DEFAULT_PLAYER_1_NAME = "Player 1";
	private static final String DEFAULT_PLAYER_2_NAME = "Player 2";
	private static final String DEFAULT_COMPUTER_NAME = "Computer";
	private static final String DEFAULT_ONLINE_NAME = "Player";
	private static final GameDifficulty DEFAULT_GAME_DIFFICULTY = GameDifficulty.NORMAL;

	private transient File savedSettings;
	private transient Alert error;

	protected String player1Name, player2Name, compName, onlineName;
	protected GameDifficulty gd;

	public Settings(String player1Name, String player2Name, String compName, String onlineName, GameDifficulty gd) {
		set(player1Name, player2Name, compName, onlineName, gd);
	}
	
	public Settings(String player1Name, String player2Name) {
		this(player1Name, player2Name, null, null, null);
	}

	public Settings() {
		savedSettings = new File(FILE_NAME);
		error = new Alert(AlertType.ERROR);
		error.setTitle("Error");
		loadFromFile();
	}

	public void setToDefault() {
		set(DEFAULT_PLAYER_1_NAME, DEFAULT_PLAYER_2_NAME, DEFAULT_COMPUTER_NAME, DEFAULT_ONLINE_NAME,
				DEFAULT_GAME_DIFFICULTY);
	}

	public void set(String player1Name, String player2Name, String compName, String onlineName, GameDifficulty gd) {
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.compName = compName;
		this.onlineName = onlineName;
		this.gd = gd;
	}

	public void loadFromFile() {
		if (!savedSettings.exists()) {
			setToDefault();
			saveToFile();
			return;
		}
		int tries = 0;
		ObjectInputStream in = null;
		BufferedInputStream bi = null;
		FileInputStream fi = null;
		while (tries++ <= 3) {
			try {
				fi = new FileInputStream(savedSettings);
				bi = new BufferedInputStream(fi);
				in = new ObjectInputStream(bi);
				Settings temp = (Settings) in.readObject();
				player1Name = temp.player1Name;
				player2Name = temp.player2Name;
				compName = temp.compName;
				onlineName = temp.onlineName;
				gd = temp.gd;
			} catch (Exception e) {
				if (tries == 3) {
					error.setHeaderText("Could not load settings");
					error.setContentText("Using default settings!");
					setToDefault();
					error.showAndWait();
				}
			}
		}
		if (in != null)
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void saveToFile() {
		ObjectOutputStream out = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(savedSettings);
			bos = new BufferedOutputStream(fos);
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
		} catch (Exception e) {
			error.setHeaderText("Could not save settings");
			error.setHeaderText("Please try again!");
			error.showAndWait();
		} finally {
			try {
				if (out != null)
					out.close();
				if (fos != null)
					fos.close();
			} catch (IOException ce) {
				ce.printStackTrace();
			}
		}
	}

	@Override
	public String getPlayer1Name() {
		return player1Name;
	}

	@Override
	public String getPlayer2Name() {
		return player2Name;
	}

	@Override
	public String getComputerName() {
		return compName;
	}

	@Override
	public String getOnlineName() {
		return onlineName;
	}

	@Override
	public GameDifficulty getGameDifficulty() {
		return gd;
	}

	@Override
	public String toString() {
		return String.format("p1 = %s, p2 = %s, c = %s, gd = %s", player1Name, player2Name, compName, gd.toString());
	}

}
