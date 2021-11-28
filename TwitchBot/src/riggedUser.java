
public class riggedUser {

	String name;
	Boolean rigged;


	public  riggedUser(String userName,Boolean riggedStatus) {
		this.rigged = riggedStatus;
		this.name = userName;
	}

	
	public String getName() {
		return this.name;
	}
	
	
	public boolean getRigged() {
		return this.rigged;
	}
	
}
