package org.teste.avs;

import java.io.File;

import javax.swing.JFileChooser;

import org.junit.Test;

public class AppTest{
    
	@Test
	public void teste(){

		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.showOpenDialog(null);

		File f = chooser.getSelectedFile();
		if(f==null){
			return;
		}
		System.out.println(f.getAbsolutePath());
		System.out.println(f.getPath());
		System.out.println(f.getName());
		System.out.println(f.getParent());
	}
}
