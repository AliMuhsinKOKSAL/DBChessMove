package hash_creator;

public class User {

	String name;
	int age;
	int id;

	public User(String name, int age, int id) {
		this.name = name;
		this.age = age;
		this.id = id;
	}
	
	String createJSON() {
		return "{\"name\":\""+name+"\",\"age\":"+age+",\"id\":"+id+"}";
	}
}
