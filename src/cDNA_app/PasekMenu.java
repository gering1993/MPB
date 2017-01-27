package cDNA_app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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
	public JMenuItem b_CloseButton, b_OpenFileToLoad, b_SaveToTxt;

		
	public PasekMenu()
	
	{		
		// Lista menu "Aplikacja"
		MenuSegment1 = new JMenu("Plik");
		this.add(MenuSegment1);
		
		// Przycisk "Otworz"
		b_OpenFileToLoad = new JMenuItem("Otwórz");
		KeyStroke ctrlO = KeyStroke.getKeyStroke("control O");
		b_OpenFileToLoad.setAccelerator(ctrlO);
		MenuSegment1.add(b_OpenFileToLoad);
		
		// Przycisk "Zapisz"
		b_SaveToTxt = new JMenuItem("Zapisz");
		KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
		b_SaveToTxt.setAccelerator(ctrlS);
		MenuSegment1.add(b_SaveToTxt);
		
		// Przycisk "Zamknij"
		b_CloseButton = new JMenuItem("Zamknij");
		KeyStroke altF4 = KeyStroke.getKeyStroke("alt F4");
		b_CloseButton.setAccelerator(altF4);
		MenuSegment1.add(b_CloseButton);


		
	}
}
