package obj;

import java.util.ArrayList;

import board.*;
import option.Option;
import piece.Piece;

public class Move {

	public String move;

	public Piece piece;
	public Square moveSquare;
	public PieceType type;
	public PieceColor color;
	
	public Square firstSquare;

	public Move(String move, PieceColor color) {
		this.move = changeMoveFormat(move);
		this.type = findType(move.charAt(0));
		this.color = color;
		this.moveSquare = findMoveSquare(this.move, type, color, move);
		this.piece = findPiece(this.move, type, color, move);
		this.firstSquare = this.piece.square;
		changePawnPromotion();
	}

	CastlingSort cast = CastlingSort.nothing;
	private PieceType findType(char c) {
		PieceType type;
		switch (c) {
		case 'R':
			type = PieceType.rook;
			break;
		case 'N':
			type = PieceType.knight;
			break;
		case 'B':
			type = PieceType.bishop;
			break;
		case 'Q':
			type = PieceType.queen;
			break;
		case 'K':
			type = PieceType.king;
			break;
		case 'O':
			if (move.length() >= 5 && move.substring(0,5).equals("O-O-O")) {
				cast = CastlingSort.long_castling;
			} else {
				cast = CastlingSort.short_castling;
			}
			type = PieceType.king;
			break;
		default:
			type = PieceType.pawn;
			break;
		}
		return type;
	}

	private void changePawnPromotion() {
		int index = move.indexOf("=");
		if (index != -1 && index + 1 < move.length()) {
			char nextChar = move.charAt(index + 1);
			BoardCreator.cBoard.boardTool.promotionType = findType(nextChar);
		}
	}

	private Square findMoveSquare(String moves, PieceType types, PieceColor colors, String notChangeMove) {
		if (types == PieceType.king && cast == CastlingSort.long_castling) {
			if (colors == PieceColor.white) {
				return BoardCreator.cBoard.getSquare(2, 0);
			} else {
				return BoardCreator.cBoard.getSquare(2, 7);
			}
		} else if (types == PieceType.king && cast == CastlingSort.short_castling) {
			if (colors == PieceColor.white) {
				return BoardCreator.cBoard.getSquare(6, 0);
			} else {
				return BoardCreator.cBoard.getSquare(6, 7);
			}
		} else {
			ArrayList<Integer> mave = new ArrayList<Integer>();
			StringBuilder numberBuilder = new StringBuilder();
			String lastValidNumber = "";

			for (int i = 0; i < moves.length(); i++) {
				char currentChar = moves.charAt(i);

				if (Character.isDigit(currentChar)) {
					numberBuilder.append(currentChar);
				} else {
					if (numberBuilder.length() > 0) {
						lastValidNumber = numberBuilder.toString();
					}
					numberBuilder.setLength(0);
				}
			}

			if (numberBuilder.length() > 0) {
				lastValidNumber = numberBuilder.toString();
			}

			if (lastValidNumber.length() >= 2) {
				String lastTwoDigits = lastValidNumber.substring(lastValidNumber.length() - 2);

				for (int j = 0; j < lastTwoDigits.length(); j++) {
					mave.add(Character.getNumericValue(lastTwoDigits.charAt(j)));
				}
			}

			try {
				return BoardCreator.cBoard.getSquare(mave.get(0) - 1, mave.get(1) - 1);
			} catch (Exception e) {
				System.err.println(findPiece(changeMoveFormat(moves), types, colors, notChangeMove));
			}
		}
		return null;
	}

	private Piece findPiece(String moves, PieceType types, PieceColor colors, String notChangeMove) {
		if ((types == PieceType.king && cast == CastlingSort.long_castling)||types == PieceType.king && cast == CastlingSort.short_castling) {
			if (color == PieceColor.white) {
				return BoardCreator.cBoard.getSquare(4, 0).piece;
			}
			if (color == PieceColor.black) {
				return BoardCreator.cBoard.getSquare(4, 7).piece;
			}
		} else {
			ArrayList<Integer> mave = new ArrayList<Integer>();
			ArrayList<String> allNumbers = new ArrayList<String>();
			ArrayList<Integer> separatedDigits = new ArrayList<Integer>();
			StringBuilder numberBuilder = new StringBuilder();
			String lastValidNumber = "";

			for (int i = 0; i < moves.length(); i++) {
				char currentChar = moves.charAt(i);

				if (Character.isDigit(currentChar)) {
					numberBuilder.append(currentChar);
				} else {
					if (numberBuilder.length() > 0) {
						allNumbers.add(numberBuilder.toString());

						for (int j = 0; j < numberBuilder.length(); j++) {
							separatedDigits.add(Character.getNumericValue(numberBuilder.charAt(j)));
						}

						lastValidNumber = numberBuilder.toString();
					}
					numberBuilder.setLength(0);
				}
			}

			if (numberBuilder.length() > 0) {
				allNumbers.add(numberBuilder.toString());
				for (int j = 0; j < numberBuilder.length(); j++) {
					separatedDigits.add(Character.getNumericValue(numberBuilder.charAt(j)));
				}
				lastValidNumber = numberBuilder.toString();
			}

			if (lastValidNumber.length() >= 2) {
				String lastTwoDigits = lastValidNumber.substring(lastValidNumber.length() - 2);

				for (int j = 0; j < lastTwoDigits.length(); j++) {
					mave.add(Character.getNumericValue(lastTwoDigits.charAt(j)));
				}

				separatedDigits.remove(separatedDigits.size() - 1);
				separatedDigits.remove(separatedDigits.size() - 1);
			}

			int x = -1;
			int y = -1;

			for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
				for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
					if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
						for (Option opt : BoardCreator.cBoard.boardTool.selectedPieceMove(BoardCreator.cBoard.getSquare(i, j).piece)) {
							if (opt.xsqu.equals(BoardCreator.cBoard.getSquare(mave.get(0) - 1, mave.get(1) - 1))) {
								if (BoardCreator.cBoard.getSquare(i, j).piece.type == types) {
									if (BoardCreator.cBoard.getSquare(i, j).piece.color == colors) {
										x = i;
										y = j;
									}
								}
							}
						}
					}
				}
			}
			if (separatedDigits.size() == 1) {
				if (notChangeMove.length() > 1) {
					char ch = notChangeMove.charAt(1);
					if (types == PieceType.pawn) {
						ch = notChangeMove.charAt(0);
					}

					if (ch >= 'a' && ch <= 'h') {
						x = separatedDigits.get(0) - 1;
						for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
							if (BoardCreator.cBoard.getSquare(x, j).piece != null) {
								for (Option opt : BoardCreator.cBoard.boardTool.selectedPieceMove(BoardCreator.cBoard.getSquare(x, j).piece)) {
									if (opt.xsqu
											.equals(BoardCreator.cBoard.getSquare(mave.get(0) - 1, mave.get(1) - 1))) {
										if (BoardCreator.cBoard.getSquare(x, j).piece.type == types) {
											if (BoardCreator.cBoard.getSquare(x, j).piece.color == colors) {
												y = j;
											}
										}
									}
								}
							}
						}
					} else {
						y = separatedDigits.get(0) - 1;
						for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
							if (BoardCreator.cBoard.getSquare(i, y).piece != null) {
								for (Option opt : BoardCreator.cBoard.boardTool.selectedPieceMove(BoardCreator.cBoard.getSquare(i, y).piece)) {
									if (opt.xsqu
											.equals(BoardCreator.cBoard.getSquare(mave.get(0) - 1, mave.get(1) - 1))) {
										if (BoardCreator.cBoard.getSquare(i, y).piece.type == types) {
											if (BoardCreator.cBoard.getSquare(i, y).piece.color == colors) {
												x = i;
											}
										}
									}
								}
							}
						}
					}
//					System.out.println("x: " + x + " y: " + y);
				}
			} else if (separatedDigits.size() == 2) {
				x = separatedDigits.get(0) - 1;
				y = separatedDigits.get(1) - 1;
			}
			return BoardCreator.cBoard.getSquare(x, y).piece;
		}
		return null;
	}

	public String changeMoveFormat(String moves) {
		StringBuilder modifiedText = new StringBuilder();

		for (char ch : moves.toCharArray()) {
			switch (ch) {
			case 'a':
				modifiedText.append('1');
				break;
			case 'b':
				modifiedText.append('2');
				break;
			case 'c':
				modifiedText.append('3');
				break;
			case 'd':
				modifiedText.append('4');
				break;
			case 'e':
				modifiedText.append('5');
				break;
			case 'f':
				modifiedText.append('6');
				break;
			case 'g':
				modifiedText.append('7');
				break;
			case 'h':
				modifiedText.append('8');
				break;
			default:
				modifiedText.append(ch);
				break;
			}
		}
		return modifiedText.toString();
	}

	public void addMove(ArrayList<Move> moves) {
		moves.add(new Move(move, color));
	}
}
