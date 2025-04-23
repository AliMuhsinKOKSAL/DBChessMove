package hzob_hash_creator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class InsertZobrist {

	String db_url = "jdbc:mysql://localhost:3306/bot_data_set";
	String user = "root";
	String pass = "1234";

	Connection conn = DriverManager.getConnection(db_url, user, pass);

	Random random = new Random();

	public InsertZobrist() throws SQLException {
		readZobNum(0, 1, 3, 7);
	}

	public void insertZobNum(int color, int type, int x, int y, long zobristNum) {
		try {

			String moveInsertQuery = "INSERT INTO zobristRands(id, color, type, x,y, zobristNum) VALUES (?,?,?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(moveInsertQuery);

			int id = Integer.parseInt((color + 1) + "" + type + "" + x + "" + y);

			preparedStatement.setInt(1, id);
			preparedStatement.setInt(2, color);
			preparedStatement.setInt(3, type);
			preparedStatement.setInt(4, x);
			preparedStatement.setInt(5, y);
			preparedStatement.setLong(6, zobristNum);

			preparedStatement.executeUpdate();

			preparedStatement.close();
			conn.close();

		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public void readZobNum(int color, int type, int x, int y) {
		try {

			int id = Integer.parseInt((color + 1) + "" + type + "" + x + "" + y);

			String moveInsertQuery = "select * from zobrist_num where id = " + id;
			PreparedStatement preparedStatement = conn.prepareStatement(moveInsertQuery);
			ResultSet resultSet = preparedStatement.executeQuery();

			long zob = 0;
			while (resultSet.next()) {
				zob = resultSet.getLong(6);
			}

			System.out.println(zob);

			resultSet.close();
			preparedStatement.close();
			conn.close();

		} catch (Exception e) {
			System.out.println(e);
			;
		}
	}

	public static void main(String[] args) throws SQLException {
		new InsertZobrist();
	}

}
