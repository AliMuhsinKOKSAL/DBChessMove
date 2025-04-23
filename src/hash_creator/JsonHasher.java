package hash_creator;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonHasher {

	public String calculateJsonHash(){
		String json = "{\"game_id\":1000429,\"move_id\":1000429097,\"turn\":\"white\",\"status\":{\"check\":false,\"check_mate\":false},\"castling\":{\"white\":{\"short\":false,\"long\":false},\"black\":{\"short\":false,\"long\":false}},\"en_passant\":\"-\",\"position\":{\"c4\":{\"type\":\"pawn\",\"color\":\"black\"},\"d3\":{\"type\":\"pawn\",\"color\":\"black\"},\"d5\":{\"type\":\"pawn\",\"color\":\"white\"},\"d6\":{\"type\":\"pawn\",\"color\":\"black\"},\"e2\":{\"type\":\"knight\",\"color\":\"black\"},\"e3\":{\"type\":\"bishop\",\"color\":\"white\"},\"f1\":{\"type\":\"king\",\"color\":\"white\"},\"f2\":{\"type\":\"pawn\",\"color\":\"white\"},\"f5\":{\"type\":\"bishop\",\"color\":\"white\"},\"f8\":{\"type\":\"bishop\",\"color\":\"black\"},\"g5\":{\"type\":\"pawn\",\"color\":\"white\"},\"h6\":{\"type\":\"pawn\",\"color\":\"white\"},\"h8\":{\"type\":\"king\",\"color\":\"black\"}},\"half_move_number\":97,\"full_move_number\":49,\"last_move\":\"g4g5\"}\r\n";
		JSONObject jsonObject = new JSONObject(json);

		JSONObject pos = jsonObject.getJSONObject("position");

		StringBuilder sb = new StringBuilder();
		Iterator<String> keys = pos.keys();

		while (keys.hasNext()) {
			String key = keys.next();
			Object value = pos.get(key);
			sb.append(key).append(value.toString());
		}

		System.out.println(getSHA256Hash(sb.toString()));
		
		return getSHA256Hash(sb.toString());
	}

	public String getSHA256Hash(String data) {
		StringBuilder hexString = new StringBuilder();;
		try {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hashBytes = digest.digest(data.getBytes());

		
		for (byte b : hashBytes) {
			hexString.append(String.format("%02x", b));
		}
		}catch (Exception e) {
			e.getStackTrace();
		}

		return hexString.toString();
	}
	
	public boolean areAllElementsEqual(ArrayList<String> list) {
        if (list.isEmpty()) return true;

        String first = list.get(0);
        for (String s : list) {
            if (!first.equals(s)) {
                return false;
            }
        }
        return true;
    }
	
	public String calculateUsersJsonHash(String json){
		JSONObject jsonObject = new JSONObject(json);

		StringBuilder sb = new StringBuilder();
		Iterator<String> keys = jsonObject.keys();

		while (keys.hasNext()) {
			String key = keys.next();
			Object value = jsonObject.get(key);
			sb.append(key).append(value.toString());
		}
		
		return getSHA256Hash(sb.toString());
	}
	
	/*
		JsonHasher jHasher = new JsonHasher();
		User user = new User("aa", 2, 521);
		String json = user.createJSON();
		System.out.println(json);
		System.out.println(jHasher.calculateUsersJsonHash(json));
	 * 
	 */
	public static void main(String[] args) {
		JsonHasher jHasher = new JsonHasher();
		User fuser = new User("BB", 2, 521);
		String fjson = fuser.createJSON();
		String first = jHasher.calculateUsersJsonHash(fjson);
		for(int i = 0;i<1000000;i++) {
			User user = new User("aa", 2, 521);
			String json = user.createJSON();
			if(!first.equals(jHasher.calculateUsersJsonHash(json))) {
				System.out.println("aynı olmayan var");
				System.exit(0);
				break;
			}
			System.out.println(i+". işlem "+jHasher.calculateUsersJsonHash(json));
		}
		System.out.println("hepsi aynı");
	}
}
