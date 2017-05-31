package org.teste.avs;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.teste.avs.view.MonitorView;

public class Main {
	
    public static void main( String[] args ) throws Exception{
    	
    	if( args!=null && args.length>0){
    		ConsoleMode.run(args);
    	}else{
    		initInterface();
    	}
    }

	private static void initInterface() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new MonitorView().setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error! \n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
    
}
