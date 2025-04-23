package database;

import board.BoardCreator;
import obj.Move;
import obj.PieceColor;

public class DBMove {

	public int move_id;
	public int game_id;
	public int move_number;
	public int half_move_number;
	public String move;
	public boolean is_white;

	public DBMove(int move_id, int game_id, int move_number, String move, boolean is_white) {
		this.move_id = move_id;
		this.game_id = game_id;
		this.move_number = move_number;
		this.half_move_number = ((is_white == true) ? (2 * move_number) - 1 : 2 * move_number);
		this.move = move;
		this.is_white = is_white;
	}

	public String algToCoords() {
//		String strPromotion = (BoardCreator.cBoard.boardTool.pawnPromotionDB!=null)?((BoardCreator.cBoard.boardTool.pawnPromotionDB != PieceType.knight)?(BoardCreator.cBoard.boardTool.pawnPromotionDB.toString().substring(0,1)):"N"):"";
		return chessXNot(BoardCreator.cBoard.boardTool.lastMovePiece.x) + ""
				+ (BoardCreator.cBoard.boardTool.lastMovePiece.y + 1)
				+ chessXNot(BoardCreator.cBoard.boardTool.lastMoveSquare.x) + ""
				+ (BoardCreator.cBoard.boardTool.lastMoveSquare.y + 1);
	}

	public char chessXNot(int intX) {
		char x;
		switch (intX) {
		case 0:
			x = 'a';
			break;
		case 1:
			x = 'b';
			break;
		case 2:
			x = 'c';
			break;
		case 3:
			x = 'd';
			break;
		case 4:
			x = 'e';
			break;
		case 5:
			x = 'f';
			break;
		case 6:
			x = 'g';
			break;
		case 7:
			x = 'h';
			break;
		default:
			throw new IllegalStateException();
		}
		return x;
	}

	public Move getMove() {
		return new Move(move, is_white ? PieceColor.white : PieceColor.black);
	}
}
