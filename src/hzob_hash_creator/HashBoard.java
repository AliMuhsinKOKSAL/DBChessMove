package hzob_hash_creator;

import obj.PieceColor;

public class HashBoard {

	public HashSquare[][] tHashBoard;
	public HashSquare enPassSquare;
	public PieceColor turn;
	public boolean wLongCastling;
	public boolean wShortCastling;
	public boolean bLongCastling;
	public boolean bShortCastling;

	public HashBoard(HashSquare[][] tempBoard, HashSquare enPassSquare,PieceColor turn, boolean wLongCastling, boolean wShortCastling,
			boolean bLongCastling, boolean bShortCastling) {
		this.tHashBoard = tempBoard;
		this.enPassSquare = enPassSquare;
		this.turn = turn;
		this.wLongCastling = wLongCastling;
		this.wShortCastling = wShortCastling;
		this.bLongCastling = bLongCastling;
		this.bShortCastling = bShortCastling;
	}

}
