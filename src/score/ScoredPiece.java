package score;

import java.util.ArrayList;
import java.util.Iterator;

import board.BoardCreator;
import obj.PieceType;
import option.OpType;
import option.Option;
import piece.Piece;

public class ScoredPiece {

	Piece piece;
	double point = 0.0;
	ArrayList<ScoredSquare> scoredResults = new ArrayList<ScoredSquare>();

	public ScoredPiece(Piece piece) {
		this.piece = piece;
		scorePiece();
		scoreSquare();
	}

	public void scorePiece() {
		point += 0;
	}

	public double pieceAllMobilite() {
		double COptSize = copyOptions().size();
		if (piece.type.equals(PieceType.rook)) {
			return (COptSize / 14)*BoardCreator.cBoard.scoreTool.pieceFactor(piece);
		} else if (piece.type.equals(PieceType.knight) || piece.type.equals(PieceType.king)) {
			return (COptSize / 8)*BoardCreator.cBoard.scoreTool.pieceFactor(piece);
		} else if (piece.type.equals(PieceType.bishop)) {
			return (COptSize / 13)*BoardCreator.cBoard.scoreTool.pieceFactor(piece);
		} else if (piece.type.equals(PieceType.queen)) {
			return (COptSize / 27)*BoardCreator.cBoard.scoreTool.pieceFactor(piece);
		} else {
			return (COptSize / 4)*BoardCreator.cBoard.scoreTool.pieceFactor(piece);
		}
	}

	public void scoreSquare() {
		for (Option opt : copyOptions()) {
			scoredResults.add(new ScoredSquare(this, opt.xsqu));
		}
	}

	public ArrayList<Option> copyOptions() {
		ArrayList<Option> copyOptionss = new ArrayList<Option>(BoardCreator.cBoard.boardTool.selectedPieceMove(piece));
		Iterator<Option> iterator = copyOptionss.iterator();
		while (iterator.hasNext()) {
			Option opt = iterator.next();
			if (opt.opType == OpType.notTake || opt.opType == OpType.notMovedTo) {
				iterator.remove();
			}
		}
		return copyOptionss;
	}	
}
