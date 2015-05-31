package asia.redact.bracket.exampleclasses;

public class ShipTo extends ExampleBase {

	String country;
	
	Name name;
	Street street1;
	Street street2;
	City city;
	StateOrTerritory stateOrTerritory;
	PostalCode postalCode;
	
	public ShipTo() {}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public Street getStreet1() {
		return street1;
	}

	public void setStreet1(Street street1) {
		this.street1 = street1;
	}

	public Street getStreet2() {
		return street2;
	}

	public void setStreet2(Street street2) {
		this.street2 = street2;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public StateOrTerritory getStateOrTerritory() {
		return stateOrTerritory;
	}

	public void setStateOrTerritory(StateOrTerritory stateOrTerritory) {
		this.stateOrTerritory = stateOrTerritory;
	}

	public PostalCode getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(PostalCode postalCode) {
		this.postalCode = postalCode;
	}

}
