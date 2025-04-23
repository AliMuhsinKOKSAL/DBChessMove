package obj;

public enum PieceColor {
	white(0), black(1);
	
	private int value;

    PieceColor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
