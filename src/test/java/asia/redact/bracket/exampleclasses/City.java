package asia.redact.bracket.exampleclasses;

public class City extends ExampleBase {
	
	String name;

	public City() {
	
	}

	public City(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
