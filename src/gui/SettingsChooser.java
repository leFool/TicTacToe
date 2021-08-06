package gui;

import java.io.IOException;

import core.Message;
import core.Network;
import enums.GameDifficulty;
import enums.MessageType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.TicTacToe;
import user.Settings;

public class SettingsChooser extends Settings {

	private static final long serialVersionUID = -6460486954356247550L;

	private transient Button ok, apply, toDefault, cancel;
	private transient TextField player1TextFiled, player2TextFiled, compTextFiled, onlineTextFiled;
	private transient Label lp1, lp2, lcomp, lo, ldiff;
	private transient ChoiceBox<String> difficulty;
	private transient GridPane root;
	private transient Stage stage;

	private transient TicTacToe main;
	private transient Network net;

	public SettingsChooser(TicTacToe main) {
		super();
		this.main = main;
		this.net = main.getNetwork();
		setLabels();
		setFields();
		setButtons();
		setRoot();
		stage = new Stage();
		stage.close();
		stage.setTitle("Settings");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(new Scene(root, 350, 220));
	}

	private void setRoot() {
		Node[][] childs = { { lp1, player1TextFiled }, { lp2, player2TextFiled }, { lcomp, compTextFiled },
				{ lo, onlineTextFiled }, { ldiff, difficulty } };
		root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setHgap(10);
		root.setVgap(5);
		int i;
		for (i = 0; i < childs.length; i++)
			for (int j = 0; j < childs[0].length; j++)
				root.add(childs[i][j], j, i);
		HBox btns = new HBox(10, ok, apply, toDefault, cancel);
		btns.setAlignment(Pos.CENTER);
		root.getChildren().add(btns);
		GridPane.setConstraints(btns, 0, i, 2, 1);
	}

	private void setLabels() {
		lp1 = new Label("Player 1 name");
		lp2 = new Label("Player 2 name");
		lcomp = new Label("Computer name");
		lo = new Label("Online name");
		ldiff = new Label("Difficulty");
	}

	private void setFields() {
		player1TextFiled = new TextField();
		player2TextFiled = new TextField();
		compTextFiled = new TextField();
		onlineTextFiled = new TextField();
		String[] options = new String[GameDifficulty.values().length];
		int i = 0;
		for (GameDifficulty gd : GameDifficulty.values())
			options[i++] = gd.toString();
		difficulty = new ChoiceBox<String>(FXCollections.observableArrayList(options));
	}

	private void setButtons() {
		ok = new Button("Ok");
		ok.setOnAction(e -> buttonClicked(e));
		apply = new Button("Apply");
		apply.setOnAction(e -> buttonClicked(e));
		toDefault = new Button("Default");
		toDefault.setOnAction(e -> buttonClicked(e));
		cancel = new Button("Cancel");
		cancel.setOnAction(e -> buttonClicked(e));
	}

	private void buttonClicked(ActionEvent e) {
		Button b = (Button) e.getSource();
		if (b == ok) {
			save(true);
			stage.hide();
			Game g = Game.getCurrentGame(main);
			if (g != null)
				Game.getCurrentGame(main).updateInfo(this);
			if (net.checkConnection()) {
				try {
					net.send(new Message(MessageType.NAME, onlineName));
				} catch (IOException e1) {
					net.error("couldn't send online name");
				}
			}
		}
		else if (b == apply)
			save(false);
		else if (b == toDefault) {
			setToDefault();
			load();
		}
		else
			stage.hide();
	}

	public void load() {
		player1TextFiled.setText(player1Name);
		player2TextFiled.setText(player2Name);
		compTextFiled.setText(compName);
		onlineTextFiled.setText(onlineName);
		difficulty.setValue(gd.toString());
	}

	private void save(boolean toFile) {
		set(player1TextFiled.getText(), player2TextFiled.getText(), compTextFiled.getText(), onlineTextFiled.getText(),
				GameDifficulty.valueOf(difficulty.getValue()));
		if (toFile)
			saveToFile();
	}

	public void openDialog() {
		loadFromFile();
		load();
		stage.showAndWait();
	}

}
