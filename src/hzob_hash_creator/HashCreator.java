package hzob_hash_creator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

import org.json.JSONObject;

import board.BoardCreator;
import board.ChessBoard;
import obj.PieceColor;
import obj.PieceType;

public class HashCreator {

	HashSquare[][] hashBoard;

	long turnKey = 3084300197685471715L;

	long wLongKey = 2445656173058035222L;
	long wShortKey = 393037475346970491L;
	long bLongKey = 2298865284889636499L;
	long bShortKey = 1458222261398550821L;

	public HashBoard createHashBoard() {
		hashBoard = new HashSquare[ChessBoard.BOARD_SIZE][ChessBoard.BOARD_SIZE];

		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				hashBoard[i][j] = new HashSquare(i, j);
			}
		}
		String json = BoardCreator.cBoard.boardTool.lastJSON;

		JSONObject jsonObject = new JSONObject(json);

		String en_pass = jsonObject.getString("en_passant");

		String strTurn = jsonObject.getString("turn");

		PieceColor turn = strTurn.trim().equals("white") ? PieceColor.white : PieceColor.black;

		JSONObject castling = jsonObject.getJSONObject("castling");
		JSONObject whiteCastling = castling.getJSONObject("white");
		JSONObject blackCastling = castling.getJSONObject("black");

		boolean wLongCastling = whiteCastling.getBoolean("long");
		boolean wShortCastling = whiteCastling.getBoolean("short");

		boolean bLongCastling = blackCastling.getBoolean("long");
		boolean bShortCastling = blackCastling.getBoolean("short");

		JSONObject pos = jsonObject.getJSONObject("position");

		Iterator<String> keys = pos.keys();

		HashSquare sq = null;
		PieceColor color = null;

		while (keys.hasNext()) {
			String strPos = keys.next();
			JSONObject piece = pos.getJSONObject(strPos);

			String strColor = piece.getString("color");
			String strType = piece.getString("type");

			color = strColor.trim().equals("white") ? PieceColor.white : PieceColor.black;

			sq = readAlgNot(strPos);

			hashBoard[sq.x][sq.y].piece = new HashPiece(getSquare(sq.x, sq.y), findType(strType), color);
		}

		return new HashBoard(hashBoard, readAlgNot(en_pass), turn, wLongCastling, wShortCastling, bLongCastling,
				bShortCastling);
	}

	public long calculatePosHash() {
		HashBoard tempBoard = createHashBoard();
		long hash = 0;

		for (int i = 0; i < ChessBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				if (getSquare(tempBoard.tHashBoard, i, j).piece != null) {
					HashPiece piece = getSquare(tempBoard.tHashBoard, i, j).piece;
					String strId = (piece.color.getValue() + 1) + "" + piece.type.getValue() + "" + piece.square.x + ""
							+ piece.square.y;
					int id = Integer.parseInt(strId);
					hash ^= readZobNum(id);
				}
			}
		}

		if (tempBoard.enPassSquare != null) {
			String strId = 10 + "" + tempBoard.enPassSquare.x + "" + tempBoard.enPassSquare.y;
			int id = Integer.parseInt(strId);
			hash ^= readZobNum(id);
		}

		if (tempBoard.turn.equals(PieceColor.black)) {
			hash ^= turnKey;
		}

		if (tempBoard.wLongCastling) {
			hash^= wLongKey;
		}

		if (tempBoard.wShortCastling) {
			hash^= wShortKey;
		}
		if (tempBoard.bLongCastling) {
			hash^= bLongKey;
		}
		if (tempBoard.bShortCastling) {
			hash^= bShortKey;
		}

		System.out.println(hash);

		return hash;
	}

	public long readZobNum(int id) {
		long zob = 0;
		try {
			String moveInsertQuery = "select zobristNum,id from zobrist_num where id = ?";
			PreparedStatement preparedStatement = BoardCreator.cBoard.sqlConnection.conn
					.prepareStatement(moveInsertQuery);
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			zob = 0;
			while (resultSet.next()) {
				zob = resultSet.getLong("zobristNum");
			}

			resultSet.close();
			preparedStatement.close();

		} catch (Exception e) {
			System.err.println(e);
			;
		}
		return zob;
	}

	public static String formatLine(String line) {
		String marker = "|";
		return String.format("%-39s%s", line, marker);
	}

	public static String formatLine7(String line) {
		String marker = "|";
		return String.format("%-31s%s", line, marker);
	}

	PieceType findType(String strType) {
		switch (strType.trim()) {
		case "pawn":
			return PieceType.pawn;
		case "rook":
			return PieceType.rook;
		case "knight":
			return PieceType.knight;
		case "bishop":
			return PieceType.bishop;
		case "king":
			return PieceType.king;
		case "queen":
			return PieceType.queen;
		default:
			throw new IllegalArgumentException("Unexpected value: " + strType);
		}
	}

	HashSquare getSquare(int i, int j) {
		return hashBoard[i][j];
	}

	HashSquare getSquare(HashSquare[][] board, int i, int j) {
		return board[i][j];
	}

	public void printBoard() {
		for (int i = ChessBoard.BOARD_SIZE - 1; i >= 0; i--) {
			for (int j = 0; j < ChessBoard.BOARD_SIZE; j++) {
				String strSq = getSquare(j, i).piece != null ? getSquare(j, i).piece.toStr() : ".";
				System.out.print(strSq);
			}
			System.out.println(formatLine7(" "));
		}
		System.out.println("----------------------------------------");
	}

	HashSquare readAlgNot(String position) {
		if (!position.equals("-")) {
			int x = -1;
			int y = Character.getNumericValue((position.trim().charAt(1))) - 1;
			switch (position.trim().charAt(0)) {
			case 'a':
				x = 0;
				break;
			case 'b':
				x = 1;
				break;
			case 'c':
				x = 2;
				break;
			case 'd':
				x = 3;
				break;
			case 'e':
				x = 4;
				break;
			case 'f':
				x = 5;
				break;
			case 'g':
				x = 6;
				break;
			case 'h':
				x = 7;
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + position.trim().charAt(0));
			}
			return getSquare(x, y);
		}
		return null;
	}

	String readAlgNotToSquare(HashSquare square) {
		if (square != null) {
			String x = "";
			int y = square.y + 1;
			switch (square.x) {
			case 0:
				x = "a";
				break;
			case 1:
				x = "b";
				break;
			case 2:
				x = "c";
				break;
			case 3:
				x = "d";
				break;
			case 4:
				x = "e";
				break;
			case 5:
				x = "f";
				break;
			case 6:
				x = "g";
				break;
			case 7:
				x = "h";
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + x);
			}
			return x + "" + y;
		}
		return "-";
	}

}