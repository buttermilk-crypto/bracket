package asia.redact.bracket.exampleclasses;

public class Name extends ExampleBase  {

	String first;
	String middle;
	String last;
	
	public Name() {}

	public Name(String first, String middle, String last) {
		super();
		this.first = first;
		this.middle = middle;
		this.last = last;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getMiddle() {
		return middle;
	}

	public void setMiddle(String middle) {
		this.middle = middle;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

}
