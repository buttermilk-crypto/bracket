package asia.redact.bracket.exampleclasses;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ExampleBase {

	public ExampleBase() {
		super();
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

}
