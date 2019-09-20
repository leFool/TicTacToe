package core;

import enums.TileType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import vlad.MatrixIndex;

public class Tile extends Canvas {

	private static int filledTiles = 0;

	private double width, height;
	private TileType type;
	private MatrixIndex mindex;
	private final Color defColor, hintColor, winColor;

	public Tile(double width, double height, MatrixIndex mindex) {
		super(width, height);
		this.width = width;
		this.height = height;
		this.mindex = mindex;
		type = TileType.EMPTY;
		defColor = Color.WHITE;
		hintColor = Color.MOCCASIN;
		winColor = Color.CHARTREUSE;
		drawTile(getGraphicsContext2D());
	}

	public Tile(MatrixIndex mindex) {
		this(100, 100, mindex);
	}

	/**
	 * Creates a tile with size of 100 x 100
	 */
	public Tile(int i, int j) {
		this(100, 100, new MatrixIndex(i, j));
	}

	public static int getFilledTilesCounter() {
		return filledTiles;
	}

	public static void resetFilledTilesCounter() {
		filledTiles = 0;
	}

	public MatrixIndex getIndex() {
		return mindex;
	}

	public boolean isEmpty() {
		return type == TileType.EMPTY;
	}

	public boolean setType(TileType type) {
		if (!isEmpty())
			return false;
		this.type = type;
		drawTile(getGraphicsContext2D());
		filledTiles++;
		return true;
	}

	public void reset() {
		if (isEmpty())
			return;
		type = TileType.EMPTY;
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
		drawTile(gc);
		filledTiles--;
	}

	public TileType getTileType() {
		return type;
	}

	private void drawTile(GraphicsContext gc) {
		switch (type) {
			case EMPTY:
				gc.setFill(defColor);
				gc.fillRect(0, 0, width, height);
				break;
			case HINT:
				gc.setFill(defColor);
				gc.fillRect(0, 0, width, height);
			case X:
				gc.strokeLine(5, 5, width - 5, height - 5);
				gc.strokeLine(height - 5, 5, 5, width - 5);
				break;
			case O:
				gc.strokeOval(5, 5, width - 10, height - 10);
		}
	}

	public void drawHint() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(hintColor);
		gc.fillRect(0, 0, width, height);
	}

	public void clearHint() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
		if (!isEmpty()) {
			gc.setFill(defColor);
			gc.fillRect(0, 0, width, height);
		}
		drawTile(gc);
	}

	public void drawWin() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
		gc.setFill(winColor);
		gc.fillRect(0, 0, width, height);
		drawTile(gc);
	}

	@Override
	public String toString() {
		return String.format("[%d, %d, %s]", mindex.i, mindex.j, type.toString());
	}
}
