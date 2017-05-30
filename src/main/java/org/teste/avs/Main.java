package org.teste.avs;

import javax.swing.UIManager;

import org.teste.avs.view.MonitorView;

public class Main {
	
    public static void main( String[] args ) throws Exception{
    	
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	new MonitorView().setVisible(true);
    }
    
}
