/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2014 David R. Smith
 *
 */
package asia.redact.bracket.properties.mgmt;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import asia.redact.bracket.properties.ExternalizedLocaleStringBuilder;
import asia.redact.bracket.properties.LocaleStringBuilder;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.Properties.Mode;
import asia.redact.bracket.properties.PropertiesImpl;
import asia.redact.bracket.properties.mgmt.ReferenceType;

/**
 * <pre>
 * Our list of references from which to load properties. The list can contain
 * externalized files, internal files within the classpath (such as in a jar
 * file) and even direct name value pairs.
 * 
 * Localization is supported, just set attributes.locale to the appropriate locale.
 * 
 * </pre>
 * 
 * @author Dave
 * 
 */
public class LoadList {

	protected final List<PropertiesReference> list;
	protected final Properties props;
	protected final Attributes attribs;

	public LoadList() {
		list = new ArrayList<PropertiesReference>();
		props = new PropertiesImpl();
		attribs = new Attributes();
	}

	public LoadList(Attributes attribs) {
		list = new ArrayList<PropertiesReference>();
		props = new PropertiesImpl();
		this.attribs = attribs;
	}

	/**
	 * This is understood as a shortcut for
	 * PropertiesReference(ReferenceType.EXTERNAL,fileloc);
	 * 
	 * @param file
	 */
	public void addReference(File file) {
		try {
			list.add(new PropertiesReference(ReferenceType.EXTERNAL, file.getCanonicalPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addReference(ReferenceType type, String path) {
		list.add(new PropertiesReference(type, path));
	}

	public void addReference(PropertiesReference ref) {
		list.add(ref);
	}

	public void load() {

		if (attribs.useCompatibilityMode) {
			Properties.Factory.mode = Mode.Compatibility;
		} else if (attribs.useLineMode) {
			Properties.Factory.mode = Mode.Line;
		} else if (attribs.useCompatibilityMode) {
			Properties.Factory.mode = Mode.Compatibility;
		} else if (attribs.useBasicTokenMode) {
			Properties.Factory.mode = Mode.BasicToken;
		}

		for (PropertiesReference ref : list) {
			switch (ref.type) {
			case CLASSLOADED:
				if(attribs.locale!= null){
					LocaleStringBuilder builder = new LocaleStringBuilder(ref.data,attribs.locale);
					List<String> list = builder.getSearchStrings();
					for(String localizedPath :list){
						try {
						loadFromClasspath(localizedPath);
						}catch(Exception x) {}
					}
				}else{
					loadFromClasspath(ref.data);
				}
				break;
			case DIRECT:
				loadFromDirect(ref.data);
				break;
			case EXTERNAL:
				if(attribs.locale!= null){
					ExternalizedLocaleStringBuilder builder = new ExternalizedLocaleStringBuilder(ref.data,attribs.locale);
					List<String> list = builder.getValidPaths();
					for(String localizedPath :list){
						try {
						loadFromFile(localizedPath);
						}catch(Exception x) {}
					}
				}else{
					loadFromFile(ref.data);
				}
				break;
			case OBFUSCATED:
				if(attribs.locale!= null){
					ExternalizedLocaleStringBuilder builder = new ExternalizedLocaleStringBuilder(ref.data,attribs.locale);
					List<String> list = builder.getValidPaths();
					for(String localizedPath :list){
						try {
						loadFromFile(localizedPath,true);
						}catch(Exception x) {}
					}
				}else{
					loadFromFile(ref.data,true);
				}
				break;
			case COMMANDLINE_OVERRIDE:
				loadFromSystemProps(ref.data);
				break;
			default:
				throw new RuntimeException("Unknown type: " + ref);
			}
		}

		if (attribs.insertResultsIntoSystemProperties) {
			props.mergeIntoSystemProperties();
		}

	}

	private void loadFromSystemProps(String key) {
		String val = System.getProperty(key);
		if (val != null) {
			props.put(key, val);
		}
	}

	private void loadFromClasspath(String path) {

		InputStream in = getClass().getResourceAsStream(path);

		if (in == null) {
			in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(path);
			if (in == null) {
				throw new RuntimeException("path " + path
						+ " was not found as a ResourceStream");
			}
		}

		props.merge(Properties.Factory.getInstance(in));

	}

	private void loadFromDirect(String data) {
		StringReader reader = new StringReader(data);
		props.merge(Properties.Factory.getInstance(reader));
	}
	
	private void loadFromFile(String path){
		loadFromFile(path,false);
	}

	private void loadFromFile(String path, boolean deobfuscate) {

		File file = new File(path);

		if (attribs.scanForPropertiesFiles) {
			if (file.isDirectory()) {
				File[] mylist = file.listFiles(new FilenameFilter() {

					public boolean accept(File dir, String name) {
						return name.contains(".properties");
					}

				});

				for (File f : mylist) {
					// TODO - add explicit mode support here
					if (Properties.Factory.mode == Mode.Compatibility) {
						Properties ext = Properties.Factory.getInstance(f, Charset.forName("ISO-8859-1"));
						if(deobfuscate) {
							props.merge(ext,false,true);
						}else{
							props.merge(ext);
						}
						
					} else {
						Properties ext = Properties.Factory.getInstance(f, Charset.forName("UTF-8"));
						if(deobfuscate) {
							props.merge(ext,false,true);
						}else{
							props.merge(ext);
						}
					}
				}

			} else {
				// file is not a directory, load it
				// TODO - add explicit mode support here
				if (Properties.Factory.mode == Mode.Compatibility) {
					Properties ext = Properties.Factory.getInstance(file, Charset.forName("ISO-8859-1"));
					if(deobfuscate) {
						props.merge(ext,false,true);
					}else{
						props.merge(ext);
					}
				} else {
					Properties ext = Properties.Factory.getInstance(file, Charset.forName("UTF-8"));
					if(deobfuscate) {
						props.merge(ext,false,true);
					}else{
						props.merge(ext);
					}
				}
			}
		} else {

			// not scanning, believe this file is a properties file regardless
			// of extension
			// but we'll test sanity anyway
			if (!file.isFile()) {
				try {
					throw new RuntimeException("File "
							+ file.getCanonicalPath()
							+ " is not a file we can read. Bailing.");
				} catch (IOException e) {
				}
			}
			// file is not a directory, load it
			// TODO - add explicit mode support here
			if (Properties.Factory.mode == Mode.Compatibility) {
				Properties ext = Properties.Factory.getInstance(file, Charset.forName("ISO-8859-1"));
				if(deobfuscate) {
					props.merge(ext,false,true);
				}else{
					props.merge(ext);
				}
			} else {
				Properties ext = Properties.Factory.getInstance(file, Charset.forName("UTF-8"));
				if(deobfuscate) {
					props.merge(ext,false,true);
				}else{
					props.merge(ext);
				}
			}
		}

	}

	public List<PropertiesReference> getList() {
		return list;
	}

	public Properties getProps() {
		return props;
	}

	public Attributes getAttribs() {
		return attribs;
	}

}
