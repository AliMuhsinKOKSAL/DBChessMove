package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;

import board.BoardCreator;

public class Main {

	public static void main(String[] args) throws IOException, SQLException, URISyntaxException {
		ArrayList<Integer> gids = BoardCreator.cBoard.sqlConnection.getGIDs();
		File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		String jarDirectory = jarFile.getParent();
		FileWriter fw = new FileWriter(jarDirectory + "/hatali_ids.txt", true);

		try (BufferedWriter writer = new BufferedWriter(fw)) {
			for (int id : gids) {
				try {
					System.out.println("işlem yapılan id : " + id);
					BoardCreator.cBoard.boardTool.resetValues();
					BoardCreator.cBoard.init();
					BoardCreator.cBoard.sqlConnection.id = id;
					BoardCreator.cBoard.sqlConnection.doAllMoves();
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("Hata var: " + id);
					writer.write(String.valueOf(id));
					writer.newLine();
					writer.flush();
					continue;
				}
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						fw.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}
}
