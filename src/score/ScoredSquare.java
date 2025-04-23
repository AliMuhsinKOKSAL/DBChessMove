package score;

import java.security.SecureRandom;
import java.util.ArrayList;

import board.BoardCreator;
import board.ChessBoard;
import obj.PieceColor;
import obj.PieceType;
import obj.Square;
import option.Option;
import piece.King;
import piece.Pawn;
import piece.Piece;

public class ScoredSquare {

	Piece piece;
	ScoredPiece scoredPiece;
	public double result;
	public Square square;

	public ScoredSquare(ScoredPiece scoredPiece, Square square) {
		this.piece = scoredPiece.piece;
		this.scoredPiece = scoredPiece;
		this.result = distanceOfMiddleSquareX(square) + distanceOfMiddleSquareY(square) + rateEatingOption(square)
				+ rateEscThreadsSafeOption(square) + isCastlingAvaliable(square);
		this.square = square;
	}

	public int distanceOfMiddleSquareX(Square square) {
		int x1 = 3 - square.x;
		int x2 = 4 - square.x;
		int x = Math.abs(x1) < Math.abs(x2) ? Math.abs(x1) : Math.abs(x2);

		SecureRandom sRandom = new SecureRandom();
		int sInt = sRandom.nextInt(1, 3);
		return (8 - x) / sInt;
	}

	public int distanceOfMiddleSquareY(Square square) {
		int y1 = 3 - square.y;
		int y2 = 4 - square.y;
		int y = Math.abs(y1) < Math.abs(y2) ? Math.abs(y1) : Math.abs(y2);

		SecureRandom sRandom = new SecureRandom();
		int sInt = sRandom.nextInt(1, 3);
		return (8 - y) / sInt;
	}

	public double rateEatingOption(Square square) {
		if (square.piece != null) {
			if (square.piece.color != piece.color) {
				SecureRandom sRandom = new SecureRandom();
				int sInt = sRandom.nextInt(1, 4);
				if (BoardCreator.cBoard.boardTool.getValidMoves(piece).contains(square)) {
					if (BoardCreator.cBoard.scoreTool.pieceFactor(piece) > BoardCreator.cBoard.scoreTool
							.pieceFactor(square.piece)) {
						System.out.println("tehlikeli4");
						return (sInt * -4) + -4 * BoardCreator.cBoard.scoreTool.pieceFactor(piece);
					} else {
						System.out.println("tehlikeli2");
						return (sInt * 2) + 5 * BoardCreator.cBoard.scoreTool.pieceFactor(piece);
					}
				} else {
					return (sInt * 16) + 5 * BoardCreator.cBoard.scoreTool.pieceFactor(square.piece);
				}
			}
		}
		return 0.0;
	}

	public double rateEscThreadsSafeOption(Square square) {
		if (getThreateningInSquare(square, piece.color).size() > 0) {
			boolean allGreaterThanPiecePoint = getThreateningInSquare(square, piece.color).stream()
					.allMatch(enemypiece -> BoardCreator.cBoard.scoreTool
							.pieceFactor(enemypiece) > BoardCreator.cBoard.scoreTool.pieceFactor(piece));
			if (getProtectingInSquare(square, piece.color).size() < 1) {
				if (allGreaterThanPiecePoint) {
					return 16 * BoardCreator.cBoard.scoreTool.pieceFactor(piece);
				} else {
					return -20 * BoardCreator.cBoard.scoreTool.pieceFactor(piece);
				}
			} else {
				return -12 * BoardCreator.cBoard.scoreTool.pieceFactor(piece);
			}
		}
		return 0.0;
	}

	public ArrayList<Piece> getThreateningInSquare(Square controlledSquare, PieceColor pColor) {
		ArrayList<Piece> threateningPieces = new ArrayList<Piece>();
		Pawn.isValidMoveOn = true;
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
					if (BoardCreator.cBoard.getSquare(i, j).piece.color != pColor) {
						for (Option opt : BoardCreator.cBoard.getSquare(i, j).piece.getOptions()) {
							if (BoardCreator.cBoard.getSquare(opt.xsqu.x, opt.xsqu.y) == controlledSquare) {
								threateningPieces.add(BoardCreator.cBoard.getSquare(
										BoardCreator.cBoard.getSquare(i, j).piece.square.x,
										BoardCreator.cBoard.getSquare(i, j).piece.square.y).piece);
							}
						}
					}
				}
			}
		}
		Pawn.isValidMoveOn = false;
		return threateningPieces;
	}

	private double isCastlingAvaliable(Square square) {
		if (piece.type == PieceType.king) {
			boolean isFirstMove = ((King)piece).isFirstMove==true;
			if(isFirstMove) {
			if (piece.color == PieceColor.white) {
						if (square.equals(BoardCreator.cBoard.getSquare(2, 0))
								|| square.equals(BoardCreator.cBoard.getSquare(6, 0))) {
							return 1000;
				} else {
					return -1000;
				}
			} else if (piece.color == PieceColor.black) {
				if (square.equals(BoardCreator.cBoard.getSquare(2, 7))
						|| square.equals(BoardCreator.cBoard.getSquare(6, 7))) {
							return 1000;
				} else {
					return -1000;
				}
			}
			}else {
				return -1000;
			}
		}
		return 0.0;
	}

	public ArrayList<Piece> getProtectingInSquare(Square controlledSquare, PieceColor pColor) {
		ArrayList<Piece> protectingPieces = new ArrayList<Piece>();
		Pawn.isValidMoveOn = true;
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
					if (BoardCreator.cBoard.getSquare(i, j).piece.color == pColor) {
						for (Option opt : BoardCreator.cBoard.getSquare(i, j).piece.getOptions()) {
							if (BoardCreator.cBoard.getSquare(opt.xsqu.x, opt.xsqu.y) == controlledSquare) {
								protectingPieces.add(BoardCreator.cBoard.getSquare(
										BoardCreator.cBoard.getSquare(i, j).piece.square.x,
										BoardCreator.cBoard.getSquare(i, j).piece.square.y).piece);
							}
						}
					}
				}
			}
		}
		Pawn.isValidMoveOn = false;
		return protectingPieces;
	}
}
