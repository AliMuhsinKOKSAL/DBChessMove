package hzob_hash_creator;

import obj.PieceColor;
import obj.PieceType;

public class HashPiece {
	
	public HashSquare square;
	public PieceType type;
	public PieceColor color;
	
	public HashPiece(HashSquare square, PieceType type, PieceColor color) {
		this.square = square;
		this.type = type;
		this.color = color;
	}
	
	public String toStr() {
		switch (type) {
		case pawn:
			if(color == PieceColor.white) {
				return "P";
			}
			return "p";
		case rook:
			if(color == PieceColor.white) {
				return "R";
			}
			return "r";
		case knight:
			if(color == PieceColor.white) {
				return "N";
			}
			return "n";
		case bishop:
			if(color == PieceColor.white) {
				return "B";
			}
			return "b";
		case king:
			if(color == PieceColor.white) {
				return "K";
			}
			return "k";
		case queen:
			if(color == PieceColor.white) {
				return "Q";
			}
			return "q";
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}
}
