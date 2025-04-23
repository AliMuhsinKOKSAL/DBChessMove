package score;

import board.BoardCreator;
import obj.PieceColor;
import piece.Piece;

public class ScoreWhitePiece {
	
	public Piece bestWhitePiece;
	public ScoredSquare bestWhiteSquare;
	public double whitePiecesPoint = 0.0;
	public boolean isGameOver = false;
	
	public ScoreWhitePiece() {
		try {
			ScoredSquare bestMove = BoardCreator.cBoard.scoreTool.findBestMove(BoardCreator.cBoard.scoreTool.computePieceScore(BoardCreator.cBoard.scoreTool.findPieces(PieceColor.white)));
			bestWhitePiece = bestMove.piece;
			bestWhiteSquare = bestMove;
		} catch (Exception e) {
			System.out.println("Game Over White");
			isGameOver = true;
		}
	}
}
