package json_creator;

import board.BoardCreator;
import board.ChessBoard;
import database.DBMove;
import obj.PieceColor;
import obj.Square;
import piece.King;
import piece.Rook;

public class JSONCreator {

	public String generateJSON() {
		StringBuilder json = new StringBuilder();
		
		DBMove dbMove = BoardCreator.cBoard.sqlConnection.getCurrentDBMove();
		
		String strQueue = (BoardCreator.cBoard.boardTool.queue == PieceColor.white) ? "white" : "black";
		boolean isCheck = (BoardCreator.cBoard.ruleTool.isKingInCheck(PieceColor.white)
				|| BoardCreator.cBoard.ruleTool.isKingInCheck(PieceColor.black));
		boolean castling[] = castling();
		json.append("{\"game_id\":" + dbMove.game_id + "," + "\"move_id\":" + dbMove.move_id + "," + "\"turn\":\"" + strQueue + "\","
				+ "\"status\":{" + "\"check\":" + (isCheck) + "," + "\"check_mate\":" + BoardCreator.cBoard.ruleTool.isCheckMate() + "},"
				+ "\"castling\":" 
				+ "{\"white\":{\"short\":" + castling[0] + ",\"long\":" + castling[1] + "},"
				+ "\"black\":{\"short\":" + castling[2] + ",\"long\":" + castling[3] + "}}," + "\"en_passant\":"
				+ "\"" + strEnPass() + "\",");

		json.append("\"position\":{");

		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (BoardCreator.cBoard.getSquare(i, j).piece != null) {
					String pos = chessXNot(i) + "" + (j + 1);
					String prop = "{\"type\":\"" + BoardCreator.cBoard.getSquare(i, j).piece.type + "\",\"color\":\""
							+ BoardCreator.cBoard.getSquare(i, j).piece.color + "\"},";
					json.append("\"" + pos + "\":" + prop);
				}
			}
		}

		if (json.charAt(json.length() - 1) == ',') {
			json.deleteCharAt(json.length() - 1);
		}

		json.append("},");
		json.append("\"half_move_number\":"+dbMove.half_move_number+",");
		json.append("\"full_move_number\":"+dbMove.move_number+",");
		json.append("\"last_move\":" + "\"" + dbMove.algToCoords() +"\",");
		String pPType = (BoardCreator.cBoard.boardTool.pawnPromotionDB!=null)?"\""+BoardCreator.cBoard.boardTool.pawnPromotionDB.toString()+"\"":null;
		json.append("\"promotion\":"+pPType+"}");
//		System.out.println(json);
		return json.toString();
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

	public String strEnPass() {
		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (BoardCreator.cBoard.getSquare(j, i).piece instanceof piece.Pawn) {
					Square target = ((piece.Pawn) BoardCreator.cBoard.getSquare(j, i).piece).enPassantTarget();
					if (target != null) {
						return chessXNot(target.x) + "" + (target.y + 1);
					}
				}
			}
		}
		return "-";
	}

	// 0: w short| 1: w long| 2: b short| 3: b long
	boolean[] castling() {
		boolean[] castling = { false, false, false, false };
		if (BoardCreator.cBoard.getSquare(4, 0).piece != null) {
			if (BoardCreator.cBoard.getSquare(4, 0).piece.color == PieceColor.white) {
				if (BoardCreator.cBoard.getSquare(4, 0).piece instanceof King) {
					if (((King) BoardCreator.cBoard.getSquare(4, 0).piece).isFirstMove) {
						if (BoardCreator.cBoard.getSquare(7, 0).piece != null) {
							if (BoardCreator.cBoard.getSquare(7, 0).piece.color == PieceColor.white) {
								if (BoardCreator.cBoard.getSquare(7, 0).piece instanceof Rook) {
									if (((Rook) BoardCreator.cBoard.getSquare(7, 0).piece).isFirstMove) {
										castling[0] = true;
									}
								}
							}
						}
						if (BoardCreator.cBoard.getSquare(0, 0).piece != null) {
							if (BoardCreator.cBoard.getSquare(0, 0).piece.color == PieceColor.white) {
								if (BoardCreator.cBoard.getSquare(0, 0).piece instanceof Rook) {
									if (((Rook) BoardCreator.cBoard.getSquare(0, 0).piece).isFirstMove) {
										castling[1] = true;
									}
								}
							}
						}
					}
				}
			}
		}
		if (BoardCreator.cBoard.getSquare(4, 7).piece != null) {
			if (BoardCreator.cBoard.getSquare(4, 7).piece.color == PieceColor.black) {
				if (BoardCreator.cBoard.getSquare(4, 7).piece instanceof King) {
					if (((King) BoardCreator.cBoard.getSquare(4, 7).piece).isFirstMove) {
						if (BoardCreator.cBoard.getSquare(7, 7).piece != null) {
							if (BoardCreator.cBoard.getSquare(7, 7).piece.color == PieceColor.black) {
								if (BoardCreator.cBoard.getSquare(7, 7).piece instanceof Rook) {
									if (((Rook) BoardCreator.cBoard.getSquare(7, 7).piece).isFirstMove) {
										castling[2] = true;
									}
								}
							}
						}
						if (BoardCreator.cBoard.getSquare(0, 7).piece != null) {
							if (BoardCreator.cBoard.getSquare(0, 7).piece.color == PieceColor.black) {
								if (BoardCreator.cBoard.getSquare(0, 7).piece instanceof Rook) {
									if (((piece.Rook) BoardCreator.cBoard.getSquare(0, 7).piece).isFirstMove) {
										castling[3] = true;
									}
								}
							}
						}
					}
				}
			}
		}

		return castling;
	}
}
