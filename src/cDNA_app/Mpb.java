package cDNA_app;

import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Mpb {

	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable(){
		@Override
		public void run()
		{
			
			new AppWindow();

		}
		});
	}

}
