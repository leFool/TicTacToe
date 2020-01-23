package enums;

public enum TileType {

	EMPTY, HINT, X, O;

	public char toChar() {
		switch (this) {
			case EMPTY:
			case HINT:
				return '-';
			case X:
				return 'X';
			case O:
				return 'O';
			default:
				throw new NullPointerException();
		}
	}
	
	public TileType getOpposite() {
		if (this == EMPTY)
			return EMPTY;
		return (this == X) ? O : X;
	}

}
