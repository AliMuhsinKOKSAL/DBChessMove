package score;

import board.BoardCreator;
import obj.PieceColor;
import piece.Piece;

public class ScoreBlackPiece {
	
	public Piece bestBlackPiece;
	public ScoredSquare bestBlackSquare;
	public double blackPiecesPoint = 0.0;
	public boolean isGameOver = false;
	
	public ScoreBlackPiece() {
		try {
			ScoredSquare bestMove = BoardCreator.cBoard.scoreTool.findBestMove(BoardCreator.cBoard.scoreTool.computePieceScore(BoardCreator.cBoard.scoreTool.findPieces(PieceColor.black)));
			bestBlackPiece = bestMove.piece;
			bestBlackSquare = bestMove;
		} catch (Exception e) {
			System.out.println("Game Over black");
			isGameOver = true;
		}
	}	
}
