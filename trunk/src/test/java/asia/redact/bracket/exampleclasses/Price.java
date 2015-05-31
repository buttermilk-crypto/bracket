package asia.redact.bracket.exampleclasses;

public class Price extends ExampleBase {

	String amount;
	String currencyCode;
	
	public Price() {}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

}
