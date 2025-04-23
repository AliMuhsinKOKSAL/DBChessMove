package score;

import board.BoardCreator;
import obj.PieceColor;

public class ScoreMoveTool {

	public void cMove() {
		if(BoardCreator.cBoard.boardTool.userColor != BoardCreator.cBoard.boardTool.queue) {
			if (BoardCreator.cBoard.boardTool.userColor != PieceColor.white) {
				ScoreWhitePiece swPiece = new ScoreWhitePiece();
				if (!swPiece.isGameOver) {
					BoardCreator.cBoard.boardTool.move(swPiece.bestWhitePiece, swPiece.bestWhiteSquare.square.x,
							swPiece.bestWhiteSquare.square.y);
				}
			} else {
				ScoreBlackPiece sbPiece = new ScoreBlackPiece();
				if (!sbPiece.isGameOver) {
					BoardCreator.cBoard.boardTool.move(sbPiece.bestBlackPiece, sbPiece.bestBlackSquare.square.x,
							sbPiece.bestBlackSquare.square.y);
				}
			}
		}
	}
}
