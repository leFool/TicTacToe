package gui;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Optional;

import core.Message;
import core.Network;
import core.StyledLabel;
import enums.GameMode;
import enums.MessageType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import main.TicTacToe;
import vlad.Ip;

public class OnlineGame extends VBox {

	private TicTacToe main;
	private StyledLabel sl;
	private String lip, eip;
	private Label lipl, eipl;
	private Button connectButton, waitButton, cancel;
	private Alert error;
	private TextInputDialog in;
	private WaitingDialog waitingDialog;
	private Network net;

	public OnlineGame(TicTacToe main, MainMenu mainMenu) {
		super(10);
		this.main = main;
		net = main.getNetwork();
		sl = new StyledLabel("Online Game");
		waitingDialog = new WaitingDialog(net);
		error = new Alert(AlertType.ERROR);
		error.setTitle("Error");
		error.setHeaderText("Connection failed");
		in = new TextInputDialog();
		in.setTitle("Connect");
		in.setHeaderText("Enter your opponent's IP");
		in.setContentText("IP:");
		lipl = new Label();
		eipl = new Label();
		connectButton = new Button("Connect to Opponent");
		connectButton.setOnAction(e -> connect());
		waitButton = new Button("Wait for Opponent");
		waitButton.setOnAction(e -> startWaiting());
		cancel = new Button("Return to Main Menu");
		cancel.setOnAction(e -> main.setLayout(mainMenu));
		Separator s = new Separator();
		s.setMaxWidth(175);
		Separator s2 = new Separator();
		s2.setMaxWidth(175);
		VBox ips = new VBox(s, lipl, eipl, s2);
		ips.setAlignment(Pos.CENTER);
		setBackground(TicTacToe.BACKGROUND);
		setAlignment(Pos.CENTER);
		setIpLabels();
		getChildren().addAll(sl, ips, connectButton, waitButton, cancel);
	}

	private void setIpLabels() {
		try {
			lip = Ip.getLocalIp();
			lipl.setText("Local IP: " + lip);
		} catch (IOException e) {
			lipl.setText("could not retrive local ip");
		}
		try {
			eip = Ip.getExternalIp();
			eipl.setText("External IP: " + eip);
		} catch (IOException ex) {
			eipl.setText("could not retrieve external ip");
		}
	}

	private void play() {
		if (net.checkConnection())
			main.setLayout(new Game(main, GameMode.ONLINE).getLayout());
	}

	private void connect() {
		Optional<String> ip = in.showAndWait();
		if (!ip.isPresent())
			return;
		String sip = ip.get().trim();
		try {
			if (sip.equals("") || sip == null)
				throw new UnknownHostException();
			boolean local = Ip.isLocal(sip);
			if (local && sip.equals(lip))
				throw new IllegalArgumentException();
			if (!local && sip.equals(eip))
				throw new IllegalArgumentException();
			net.connectToServer(sip);
			net.openStreams();
			String onlineName = main.getSettingsChooser().getOnlineName();
			net.send(new Message(MessageType.NAME, onlineName));
			play();
		} catch (IllegalArgumentException e1) {
			error.setContentText("You cannot connect to yourself, silly");
			error.showAndWait();
		} catch (UnknownHostException e2) {
			error.setContentText("IP could not be resolved");
			error.showAndWait();
		} catch (IOException e3) {
			e3.printStackTrace();
			error.setContentText("An unknown error occured, please try again!");
			error.showAndWait();
		} finally {
			if (!net.checkConnection())
				net.closeAll();
		}
	}

	private void startWaiting() {
		new Thread(() -> waitForOpponent()).start();
		waitingDialog.show();
	}

	private void waitForOpponent() {
		try {
			net.openServer();
			net.accept();
			net.openStreams();
			String onlineName = main.getSettingsChooser().getOnlineName();
			net.send(new Message(MessageType.NAME, onlineName));
			Platform.runLater(() -> {
				waitingDialog.hide(true);
				play();
			});
		} catch (SocketException se) {
			// user cancelled waiting
		} catch (IOException e) {
			error.setContentText("An unknown error occured, please try again!");
			error.showAndWait();
		}
	}

}
