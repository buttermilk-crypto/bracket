package asia.redact.bracket.exampleclasses;

public class PostalCode extends ExampleBase  {

	String code;
	
	public PostalCode() {}

	public PostalCode(String code) {
		super();
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
