package core;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class StyledLabel extends Label {
	
	public StyledLabel(String title) {
		super(title);
		setStyle();
	}

	private void setStyle() {
		setFont(new Font("Comic Sans MS", 36));
		setStyle("-fx-text-fill: linear-gradient(aqua, blue);");
	}

}
