package tool;

import java.security.SecureRandom;
import java.util.ArrayList;

import board.ChessBoard;
import hash_creator.JsonHasher;
import board.BoardCreator;
import obj.PieceColor;
import obj.PieceType;
import obj.Square;
import option.OpType;
import option.Option;
import piece.King;
import piece.Pawn;
import piece.Piece;
import piece.Rook;

public class BoardTool {

	public static Piece currentPiece;

	SecureRandom randomColor;
	public PieceColor userColor;
	public PieceColor queue;
	public int halfMoveClock;
	public boolean isActiveDB = false;
	public PieceType promotionType = PieceType.knight;
	public Square lastMovePiece = null;
	public PieceType lastMoveType = null;
	public PieceColor lastMoveColor = null;
	public Square lastMoveSquare = null;
	public String lastJSON;
//	public Move lastMove = null;
	public PieceType pawnPromotionDB = null;

	public BoardTool() {
		randomColor = new SecureRandom();
		userColor = randomColor.nextInt(1, 5) == 1 ? PieceColor.white : PieceColor.black;
		queue = PieceColor.white;
		halfMoveClock = 1;
	}

	public void placePiece(Piece piece) {
		BoardCreator.cBoard.getSquare(piece.square.x, piece.square.y).piece = piece;
	}

	public void resetValues() {
		queue = PieceColor.white;
		halfMoveClock = 1;
		isActiveDB = false;
		promotionType = PieceType.knight;
		lastMovePiece = null;
		lastMoveType = null;
		lastMoveColor = null;
		lastMoveSquare = null;
		pawnPromotionDB = null;
	}

	boolean isValidMove = false;
	Square tempSquare = null;
//	HashCreator hashing = new HashCreator();
	JsonHasher hasher = new JsonHasher();

	public void move(Piece piece, int newX, int newY) {
		tempSquare = BoardCreator.cBoard.getSquare(piece.square.x, piece.square.y);
		for (Option opt : selectedPieceMove(piece)) {
			if (opt.opType == OpType.movedTo || opt.opType == OpType.take) {
				if (opt.xsqu.x == newX && opt.xsqu.y == newY) {
					isValidMove = true;
				}
			}
		}
		if (isValidMove) {
			if (piece instanceof King) {
				if (((King) piece).isFirstMove)
					((King) piece).isCastling(piece, BoardCreator.cBoard.getSquare(newX, newY));
				((King) piece).isFirstMove = false;
			} else if (piece instanceof Rook) {
				((Rook) piece).isFirstMove = false;
			} else if (piece instanceof Pawn) {
				((Pawn) piece).eatEnPassant(BoardCreator.cBoard.getSquare(newX, newY));
			}
			pawnPromotionDB = null;
			lastMovePiece = tempSquare;
			lastMoveSquare = BoardCreator.cBoard.getSquare(newX, newY);
			lastMoveType = piece.type;
			lastMoveColor = piece.color;
//			lastMove = new Move(BoardCreator.cBoard.sqlConnection.getCurrentDBMove().move, queue);
			BoardCreator.cBoard.setSquare(piece.square, newX, newY);
			if (piece instanceof Pawn) {
				if (piece.color == PieceColor.white) {
					if (newY == 7) {
						pawnPromotionDB = ((Pawn) piece).pawnChange(piece, promotionType).type;
					}
				} else {
					if (newY == 0) {
						pawnPromotionDB = ((Pawn) piece).pawnChange(piece, promotionType).type;
					}
				}
			}
			lastJSON = BoardCreator.cBoard.jsonCreator.generateJSON();
			pawnPromotionDB = null;
//			hashing.calculatePosHash();
//			hasher.calculateJsonHash();
			halfMoveClock++;
			queue = queue == PieceColor.white ? PieceColor.black : PieceColor.white;
			isValidMove = false;
		}
	}

	public ArrayList<Option> selectedPieceMove(Piece piece) {
		currentPiece = piece;
		int checkersSize = BoardCreator.cBoard.ruleTool.kingAttacker(piece.color).size();
		ArrayList<Option> copyOptions = new ArrayList<Option>(piece.getOptions());
		copyOptions.removeIf(option -> (BoardCreator.cBoard.ruleTool.isKingInCheck(piece.color)
				&& BoardCreator.cBoard.ruleTool.canKingEscape(piece.color)
				&& BoardCreator.cBoard.ruleTool.escMovesSquare.stream()
						.noneMatch(escSquare -> option.xsqu.equals(escSquare.square) && piece.equals(escSquare.piece)))
				|| checkersSize > 1);

		int pSquareSize = BoardCreator.cBoard.ruleTool.checkPathSquare(piece).size();

		if (piece != null) {
			if (BoardCreator.cBoard.ruleTool.ifMoveKingInCheck(piece) != null) {
				if (pSquareSize < 1) {
					copyOptions.removeIf(option -> !((option.opType == OpType.take
							&& option.xsqu.piece == BoardCreator.cBoard.ruleTool.eatablePiece)
							|| BoardCreator.cBoard.ruleTool.moveableSquares.contains(option.xsqu)));
				}
			}
		}

		return copyOptions;
	}

	public ArrayList<Square> getValidMoves(Piece validPiece) {
		ArrayList<Square> validMoves = new ArrayList<Square>();
		ArrayList<Square> otherValidMoves = new ArrayList<Square>();
		ArrayList<Square> inThreadValidMoves = new ArrayList<Square>();
		if (validPiece != null) {
			for (Option opt : selectedPieceMove(validPiece)) {
				if (opt.opType == OpType.movedTo || opt.opType == OpType.take) {
					validMoves.add(BoardCreator.cBoard.getSquare(opt.xsqu.x, opt.xsqu.y));
				}
			}
			for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
				for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
					if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
						if (BoardCreator.cBoard.getSquare(i, j).piece.color != validPiece.color) {
							Pawn.isValidMoveOn = true;
							King.isValidMoveOn = true;
							for (Option opt : BoardCreator.cBoard.getSquare(i, j).piece.getOptions()) {
								if (opt.opType == OpType.movedTo || opt.opType == OpType.notTake) {
									if (BoardCreator.cBoard.getSquare(i, j).piece.type == PieceType.pawn) {
										if (BoardCreator.cBoard.getSquare(i, j).piece.square.x != opt.xsqu.x) {
											otherValidMoves.add(BoardCreator.cBoard.getSquare(opt.xsqu.x, opt.xsqu.y));
										}
									} else {
										otherValidMoves.add(BoardCreator.cBoard.getSquare(opt.xsqu.x, opt.xsqu.y));
									}
									Pawn.isValidMoveOn = false;
									King.isValidMoveOn = false;
								} else if (opt.opType == OpType.take) {
									if (BoardCreator.cBoard.getSquare(i, j).piece.type == PieceType.queen
											|| BoardCreator.cBoard.getSquare(i, j).piece.type == PieceType.rook
											|| BoardCreator.cBoard.getSquare(i, j).piece.type == PieceType.bishop)
										if (opt.xsqu.piece == validPiece) {
											int rx = (BoardCreator.cBoard.getSquare(i, j).piece.square.x - opt.xsqu.x)
													* -1;
											int ry = (BoardCreator.cBoard.getSquare(i, j).piece.square.y - opt.xsqu.y)
													* -1;
											int directionX = rx == 0 ? 0 : rx < 0 ? -1 : +1;
											int directionY = ry == 0 ? 0 : ry < 0 ? -1 : +1;

											for (int kx = 1; kx < ChessBoard.BOARD_SIZE; kx++) {
												for (int ky = 1; ky < ChessBoard.BOARD_SIZE; ky++) {
													if (opt.xsqu.x + kx * directionX < ChessBoard.BOARD_SIZE
															&& opt.xsqu.y + ky * directionY < ChessBoard.BOARD_SIZE
															&& opt.xsqu.x + kx * directionX >= 0
															&& opt.xsqu.y + ky * directionY >= 0) {
														if (validPiece.type != PieceType.knight) {
															otherValidMoves.add(BoardCreator.cBoard.getSquare(
																	opt.xsqu.x + kx * directionX,
																	opt.xsqu.y + ky * directionY));
														}
													}
												}
											}
										}
								}
							}
						}
					}
				}
			}
			for (Square validMove : validMoves) {
				for (Square otherValidMove : otherValidMoves) {
					if (validMove.equals(otherValidMove) && !inThreadValidMoves.contains(validMove)) {
						inThreadValidMoves.add(otherValidMove);
					}
				}

			}
		}
		return inThreadValidMoves;
	}

	public ArrayList<Piece> getThreateningPiece(Piece xpiece) {
		ArrayList<Piece> threateningPieces = new ArrayList<Piece>();
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
					if (BoardCreator.cBoard.getSquare(i, j).piece.color != xpiece.color) {
						for (Option opt : BoardCreator.cBoard.getSquare(i, j).piece.getOptions()) {
							if (opt.opType == OpType.take) {
								if (BoardCreator.cBoard.getSquare(opt.xsqu.x, opt.xsqu.y).piece == xpiece) {
									threateningPieces.add(BoardCreator.cBoard.getSquare(
											BoardCreator.cBoard.getSquare(i, j).piece.square.x,
											BoardCreator.cBoard.getSquare(i, j).piece.square.y).piece);
								}
							}
						}
					}
				}
			}
		}
		return threateningPieces;
	}

	public ArrayList<Piece> getProtectingPiece(Piece xpiece) {
		ArrayList<Piece> protectingPieces = new ArrayList<Piece>();
		Pawn.isValidMoveOn = true;
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
					if (BoardCreator.cBoard.getSquare(i, j).piece.color == xpiece.color) {
						for (Option opt : BoardCreator.cBoard.getSquare(i, j).piece.getOptions()) {
							if (opt.opType == OpType.notTake) {
								if (BoardCreator.cBoard.getSquare(opt.xsqu.x, opt.xsqu.y).piece == xpiece) {
									protectingPieces.add(BoardCreator.cBoard.getSquare(
											BoardCreator.cBoard.getSquare(i, j).piece.square.x,
											BoardCreator.cBoard.getSquare(i, j).piece.square.y).piece);
								}
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
