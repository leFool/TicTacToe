package core;

import gui.Game;
import javafx.application.Platform;
import main.TicTacToe;
import user.Settings;
import vlad.MatrixIndex;

public class Opponent extends Thread {

	private static boolean isPlaying;

	private Game g;
	private Network net;
	private TicTacToe main;

	public Opponent(TicTacToe main, Game g) {
		super();
		setName("Tic-Tac-Toe Online Opponent Thread");
		this.main = main;
		this.g = g;
		net = main.getNetwork();
		start();
	}

	public static void setPlaying(boolean isPlaying) {
		Opponent.isPlaying = isPlaying;
	}

	@Override
	public void run() {
		Message oppMsg;
		isPlaying = true;
		while (isPlaying) {
			// skip null messages
			while ((oppMsg = net.read()) == null)
				if (!isPlaying)
					return;
			switch (oppMsg.getType()) {
				case DISCONNECT:
					isPlaying = false;
					Platform.runLater(() -> net.error("Opponent Disconnected!"));
					break;
				case END:
					synchronized (g) {
						try {
							g.notify();
							g.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							net.error("...");
						}
					}
					break;
				case MOVE:
					MatrixIndex mi = (MatrixIndex) oppMsg.getMessage();
					Tile t = g.getTileByIndex(mi);
					Platform.runLater(() -> g.drawPlayer(t, true));
					break;
				case NAME:
					String n1 = main.getSettingsChooser().getOnlineName();
					String n2 = (String) oppMsg.getMessage();
					Settings user = (net.isHost()) ? new Settings(n1, n2) : new Settings(n2, n1);
					Platform.runLater(() -> g.updateInfo(user));
					break;
				default:
					break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
