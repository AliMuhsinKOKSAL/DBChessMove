package obj;

public enum PieceType {
	pawn(0), knight(1), rook(2), bishop(3), queen(4), king(5);
	
	  private int value;

    PieceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
