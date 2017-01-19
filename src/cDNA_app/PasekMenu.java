package cDNA_app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/*
 * Pasek menu
 * */
public class PasekMenu extends JMenuBar
{
	public JMenu MenuSegment1;
	public JMenuItem CloseButton;
	public JMenuItem Option1;
		
	public PasekMenu()
	
	{		
		// Lista menu "Aplikacja"
		MenuSegment1 = new JMenu("Aplikacja");
		this.add(MenuSegment1);
		
		// Przycisk "Zamknij"
		CloseButton = new JMenuItem("Zamknij");
		KeyStroke altF4 = KeyStroke.getKeyStroke("alt F4");
		CloseButton.setAccelerator(altF4);
		MenuSegment1.add(CloseButton);

		// Przycisk "Opcja 1"
		Option1 = new JMenuItem("Opcja 1");
		MenuSegment1.add(Option1);
		
	}
}
