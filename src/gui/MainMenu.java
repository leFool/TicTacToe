package gui;

import core.StyledLabel;
import enums.GameMode;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import main.TicTacToe;

public class MainMenu extends VBox {

	private TicTacToe main;
	private VBox btns;
	private StyledLabel wm;
	private Button pvc, pvp, og, settings, exit;

	public MainMenu(TicTacToe main) {
		super(30);
		this.main = main;
		setAlignment(Pos.CENTER);
		wm = new StyledLabel("Main Menu");
		pvc = new Button("Player vs. Computer");
		pvc.setOnAction(e -> nextRoot(GameMode.VS_COMPUTER));
		pvp = new Button("Player vs. Player");
		pvp.setOnAction(e -> nextRoot(GameMode.VS_PLAYER));
		setOnlineGameButton();
		exit = new Button("Quit Game");
		exit.setOnAction(e -> main.exit());
		settings = new Button("Settings");
		settings.setOnAction(e -> main.getSettingsChooser().openDialog());
		btns = new VBox(10, pvc, pvp, og, settings, exit);
		btns.setAlignment(Pos.CENTER);
		getChildren().addAll(wm, btns);
		setBackground(TicTacToe.BACKGROUND);
	}

	private void setOnlineGameButton() {
		og = new Button("Online Game");
		String[] args = TicTacToe.getArgs();
		boolean isOnline = true;
		for (int i = 0; (i < args.length) && isOnline; i++)
			if (args[i].equals("-offline")) {
				og.setDisable(true);
				isOnline = false;
			}
		if (isOnline)
			og.setOnAction(e -> nextRoot(GameMode.ONLINE));
	}

	private void nextRoot(GameMode mode) {
		if (mode == GameMode.ONLINE)
			main.setLayout(new OnlineGame(main, this));
		else
			main.setLayout(new Game(main, mode).getLayout());
	}

}