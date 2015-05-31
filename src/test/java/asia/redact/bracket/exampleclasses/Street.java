package asia.redact.bracket.exampleclasses;

public class Street extends ExampleBase {

	String streetName;
	String number;
	
	public Street() {}

	public Street(String number, String streetName) {
		super();
		this.streetName = streetName;
		this.number = number;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
