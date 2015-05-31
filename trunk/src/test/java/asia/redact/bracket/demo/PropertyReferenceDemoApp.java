package asia.redact.bracket.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import asia.redact.bracket.properties.OutputAdapter;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.mgmt.PropertiesReference;
import asia.redact.bracket.properties.mgmt.ReferenceType;

/**
 * Demo to show how bracket property references work
 * 
 * @author Dave
 *
 */
public class PropertyReferenceDemoApp implements ActionListener {

	JTextPane textPane;
	Properties props;
	
	String templateProps;
	String overrideExternalProps;
	String passwordExternalProps;
	
	public PropertyReferenceDemoApp(JTextPane textPane) {
		super();
		this.textPane = textPane;
		templateProps = "/demo/template.properties";
		String home = System.getProperty("user.home");
		overrideExternalProps = home+File.separator+"ext.properties";
	    passwordExternalProps = home+File.separator+"password.properties";
	}


	private static void createAndShowGUI() {
		final JFrame frame = new JFrame("Property Reference Demo");
		frame.setPreferredSize(new Dimension(500,400));
		// Create and set up the window.
		WindowListener exitListener = new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
		};

		frame.addWindowListener(exitListener);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		JTextPane text = new JTextPane();
		PropertyReferenceDemoApp app = new PropertyReferenceDemoApp(text);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(text,BorderLayout.CENTER);
		
		JPanel controls = new JPanel();
		JButton load = new JButton("Load/Reload");
		load.setActionCommand("LOAD");
		load.addActionListener(app);
		JButton close = new JButton("Close");
		close.setActionCommand("CLOSE");
		close.addActionListener(app);
		controls.setLayout(new FlowLayout());
		controls.add(load);
		controls.add(close);
		frame.getContentPane().add(controls,BorderLayout.SOUTH);
		
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(action.equals("LOAD")){
			load();
		}else if(action.equals("CLOSE")){
			System.exit(0);
		}
	}
	
	private void load() {
		List<PropertiesReference> refs = new ArrayList<PropertiesReference>();
	    refs.add(new PropertiesReference(ReferenceType.CLASSLOADED,templateProps));
	    refs.add(new PropertiesReference(ReferenceType.EXTERNAL,overrideExternalProps));
	//    refs.add(new PropertiesReference(ReferenceType.EXTERNAL,passwordExternalProps));
	    refs.add(new PropertiesReference(ReferenceType.OBFUSCATED,passwordExternalProps));
	    props = Properties.Factory.loadReferences(refs);
	    textPane.setText(OutputAdapter.toString(props));
	}
	
	
	public static void main(String [] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
