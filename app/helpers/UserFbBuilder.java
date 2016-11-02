package helpers;

public class UserFbBuilder {
	
	private String fname;	
	private String email;
	private String id;
	private String bdate;
	private String image_url;
	
	public UserFbBuilder(String name, String email, String id, String bdate) {
		super();
		this.fname = name;
		this.email = email;
		this.id = id;
		this.bdate = bdate;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBdate() {
		return bdate;
	}

	public void setBdate(String bdate) {
		this.bdate = bdate;
	}	

	
	
}
