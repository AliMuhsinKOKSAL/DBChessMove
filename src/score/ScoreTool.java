package score;

import java.util.ArrayList;
import java.util.Collections;

import board.BoardCreator;
import board.ChessBoard;
import obj.PieceColor;
import obj.PieceType;
import obj.Square;
import option.OpType;
import option.Option;
import piece.Piece;

public class ScoreTool {
	
	public ScoredSquare findBestMove(ArrayList<ScoredPiece> scoredPieces) {
		Double bestMove = Collections.max(BoardCreator.cBoard.scoreTool.saveScoredSquare(scoredPieces));

		ScoredSquare bestScoredSquare = null;

		for (ScoredPiece scoredPiece : scoredPieces) {
			for (ScoredSquare scoredSquare : scoredPiece.scoredResults) {
				if (scoredSquare != null && scoredSquare.result == bestMove) {
					bestScoredSquare = scoredSquare;
					break;
				}
			}
		}

//		System.out.println("bestPiece: " + new ScoredPiece(bestScoredSquare.piece).point + " bestSquare: x:" + bestScoredSquare.square.x + " y:"
//				+ bestScoredSquare.square.y + " bestResult: " + bestScoredSquare.result);

		return bestScoredSquare;
	}
	
	public ArrayList<Piece> findPieces(PieceColor color) {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		ArrayList<Option> copyOptions = new ArrayList<Option>();
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
					if (BoardCreator.cBoard.getSquare(i, j).piece.color == color) {
						copyOptions.clear();
						for (Option opt : BoardCreator.cBoard.boardTool
								.selectedPieceMove(BoardCreator.cBoard.getSquare(i, j).piece)) {
							if (opt.opType == OpType.take || opt.opType == OpType.movedTo) {
								copyOptions.add(opt);
							}
						}
						if (BoardCreator.cBoard.boardTool.selectedPieceMove(BoardCreator.cBoard.getSquare(i, j).piece).size() != 0) {
							pieces.add(BoardCreator.cBoard.getSquare(i, j).piece);
						}
					}
				}
			}
		}
		return pieces;
	}

	boolean isGameOver(ArrayList<Square> pieceSquare) {
		if (pieceSquare.isEmpty())
			System.out.println("game Over White");
		return pieceSquare.isEmpty();
	}

	public ArrayList<ScoredPiece> computePieceScore(ArrayList<Piece> pieces) {
		ArrayList<ScoredPiece> sPiece = new ArrayList<ScoredPiece>();
		for (Piece pies : pieces) {
			sPiece.add(new ScoredPiece(pies));
		}
		return sPiece;
	}

	public ArrayList<Double> saveScoredSquare(ArrayList<ScoredPiece> scoredPieces) {
		ArrayList<Double> nArray = new ArrayList<Double>();

		for (ScoredPiece sq : scoredPieces) {
			int q = 1;
			for (ScoredSquare ssq : sq.scoredResults) {
				nArray.add(ssq.result);
				//System.out.println(q + ". kare " + ssq.result);
				q = q + 1;
			}
			//System.out.println("----------------------------------");
		}
		return nArray;
	}
	
	public double pieceFactor(Piece piece) {
		double i = 0;
		if (piece.type == PieceType.pawn) {
			i = 1;
		} else if (piece.type == PieceType.knight) {
			i = 3.05;
		} else if (piece.type == PieceType.bishop) {
			i = 3.33;
		} else if (piece.type == PieceType.rook) {
			i = 5.63;
		} else if (piece.type == PieceType.queen) {
			i = 9.5;
		}else if(piece.type == PieceType.king) {
			i = 0.0;
		}
		return i;
	}
	

	
	public double calculatePiecePoint(Piece piece) {
		return new ScoredPiece(piece).point;
	}
	
}
