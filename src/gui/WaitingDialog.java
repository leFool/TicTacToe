package gui;

import core.Network;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WaitingDialog extends Stage {

	private Network net;
	private Label l;
	private ProgressIndicator indicator;
	private Button cancel;
	private VBox root;
	
	public WaitingDialog(Network net) {
		super(StageStyle.UNDECORATED);
		initModality(Modality.APPLICATION_MODAL);
		setTitle("Wait for Opponent Dialog");
		setX(860);
		setY(443);
		this.net = net;
		l = new Label("Waiting for opponent...");
		indicator = new ProgressIndicator(-1);
		indicator.setPrefSize(30, 30);
		cancel = new Button("Cancel");
		cancel.setOnAction(e -> this.hide(false));
		root = new VBox(10, l, indicator, cancel);
		root.setAlignment(Pos.CENTER);
		setScene(new Scene(root, 200, 110));
	}
	
	public void hide(boolean success) {	
		if (!success)
			net.closeServer();
		super.hide();
	}

}
