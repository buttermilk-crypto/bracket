/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Adds sorted map capability, but with the overhead of a TreeMap. As you add entries 
 * they will be sorted by key in natural sort order or by your comparator
 * 
 * @author Dave
 *
 */
public class SortedPropertiesImpl extends PropertiesImpl {
	
	private static final long serialVersionUID = 1L;
	private Comparator<String> comparator;

	public SortedPropertiesImpl() {
		super();
	}

	public SortedPropertiesImpl(Comparator<String> comparator) {
		super();
		this.comparator = comparator;
	}

	protected void initMap() {
		if(comparator == null) {
			map = new TreeMap<String,ValueModel>();
		}else{
			map = new TreeMap<String,ValueModel>(comparator);
		}
	}
}
