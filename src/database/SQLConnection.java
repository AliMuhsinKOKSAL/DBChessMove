package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import board.BoardCreator;
import obj.Move;

public class SQLConnection {

	String url = "jdbc:mysql://localhost:3306/bot_data_set";
	String user = "root";
	String pass = "1234";
	
	public int id;

	public Connection conn;

	public SQLConnection() {
		try {
			conn = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Connection kapatılınıyor.");
		}));
	}

	public ArrayList<DBMove> db_moves() {
		ArrayList<DBMove> moves = new ArrayList<DBMove>();
		try {
			String moveInsertQuery = "select * from jmoves where gid = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(moveInsertQuery);
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int move_id = resultSet.getInt(1);
				int game_id = resultSet.getInt(2);
				int move_number = resultSet.getInt(3);
				String move = resultSet.getString(4);
				boolean is_white = resultSet.getBoolean(5);
				moves.add(new DBMove(move_id, game_id, move_number, move, is_white));
			}

			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			System.err.println(e);
		}

		return moves;
	}

	public void updateBoardState(int move_number,boolean isWhiteMove) throws SQLException {
		String updateQuery = "UPDATE jmoves SET board_state = ? WHERE move_number = ? and is_white_move = ? and gid = ?";
		
		PreparedStatement stmt = conn.prepareStatement(updateQuery);
		
		stmt.setString(1, BoardCreator.cBoard.boardTool.lastJSON);
        stmt.setInt(2, move_number); 
        stmt.setBoolean(3, isWhiteMove);
        stmt.setInt(4, id);
        
        stmt.executeUpdate();
        stmt.close();
	}
	
	public void move_in_db(int z) {
		ArrayList<DBMove> moves = db_moves();

		for (int i = 0; i < z; i++) {
				Move piece_move = moves.get(i).getMove();

				if (moves.get(i).half_move_number == BoardCreator.cBoard.boardTool.halfMoveClock) {
					BoardCreator.cBoard.boardTool.move(piece_move.piece, piece_move.moveSquare.x,
							piece_move.moveSquare.y);
				}
			}
	}

	public void doAllMoves() {
		ArrayList<DBMove> moves = db_moves();

		for (int i = 0; i < moves.size(); i++) {
			Move piece_move = moves.get(i).getMove();

			if (moves.get(i).half_move_number == BoardCreator.cBoard.boardTool.halfMoveClock) {
				BoardCreator.cBoard.boardTool.move(piece_move.piece, piece_move.moveSquare.x, piece_move.moveSquare.y);
				try {
					updateBoardState(moves.get(i).move_number, moves.get(i).is_white);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public DBMove getCurrentDBMove() {
		ArrayList<DBMove> moves = db_moves();
		for (int i = 0; i < moves.size(); i++) {
			if (moves.get(i).half_move_number == BoardCreator.cBoard.boardTool.halfMoveClock) {
				return moves.get(i);
			}
		}
		return null;
	}

	public String coverAlgebericToCoords(Move move) {
		return move.piece.square.x + "" + move.piece.square.y;
	}
	
	public ArrayList<Integer> getGIDs() throws SQLException {

		ArrayList<Integer> numbers = new ArrayList<>();

		String query = "SELECT gid FROM matches";
		
		PreparedStatement stmt = conn.prepareStatement(query);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			int gid = rs.getInt("gid");
			numbers.add(gid);
		}

		rs.close();
		stmt.close();;
		
		return numbers;
	}

}
