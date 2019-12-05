package main;

import core.Network;
import gui.Game;
import gui.MainMenu;
import gui.SettingsChooser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

public class TicTacToe extends Application {

	public static final Background BACKGROUND;
	public static final int PORT = 6336;

	private static String[] args;
	
	static {
		Stop[] stops = new Stop[] { new Stop(0, Color.WHITE), new Stop(1, Color.BLACK) };
		LinearGradient lg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
				stops);
		BackgroundFill bf = new BackgroundFill(lg, CornerRadii.EMPTY, Insets.EMPTY);
		BACKGROUND = new Background(bf);
	}

	private SettingsChooser settingsChooser;
	private BorderPane root;
	private Menu gameMenu;
	private Network net;

	@Override
	public void start(Stage primaryStage) throws Exception {
		net = new Network(this);
		settingsChooser = new SettingsChooser(this);
		root = new BorderPane();
		addMenuBar();
		root.setCenter(new MainMenu(this));
		primaryStage.setTitle("Tic-Tac-Toe");
		primaryStage.setScene(new Scene(root, 400, 380));
		primaryStage.show();
	}

	private void addMenuBar() {
		MenuItem re = new MenuItem("Restart");
		MenuItem exit = new MenuItem("Exit");
		MenuItem settings = new MenuItem("Settings");
		Menu file = new Menu("File", null, re, exit);
		gameMenu = new Menu("Game");
		Menu tools = new Menu("Tools", null, settings);
		MenuBar bar = new MenuBar(file, gameMenu, tools);
		re.setOnAction(e -> restart());
		exit.setOnAction(e -> exit());
		settings.setOnAction(e -> settingsChooser.openDialog());
		gameMenu.setDisable(true);
		root.setTop(bar);
	}

	public void restart() {
		setLayout(new MainMenu(this));
	}

	public void exit() {
		Platform.exit();
	}

	@Override
	public void stop() throws Exception {
		net.closeAll();
		super.stop();
	}

	public void setLayout(Node layout) {
		// checks if a new game has started
		if (layout instanceof BorderPane)
			gameMenu.setDisable(false);
		// checks if a game eneded
		else if (root.getCenter() instanceof BorderPane) {
			Game.end();
			net.closeAll();
			gameMenu.getItems().remove(0);
			gameMenu.setDisable(true);
		}
		root.setCenter(layout);
	}

	public Node getLayout() {
		return root.getCenter();
	}

	public Menu getGameMenu() {
		return gameMenu;
	}

	public Network getNetwork() {
		return net;
	}

	public SettingsChooser getSettingsChooser() {
		return settingsChooser;
	}

	public static void main(String[] args) {
		TicTacToe.args = args;
		launch(args);
		System.exit(0);
	}

	public static String[] getArgs() {
		return args;
	}

}