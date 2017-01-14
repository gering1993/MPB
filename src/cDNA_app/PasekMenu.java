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
public class PasekMenu extends JMenuBar implements ActionListener
{
	public static JMenu AppMenu;
	public static JMenuItem Zamknij;
	
	private JFrame App = null;
	
	public PasekMenu(JFrame app)
	{
		App = app;
		
		AppMenu = new JMenu("Aplikacja");
		add(AppMenu);
		
		Zamknij = new JMenuItem("Zamknij");
		KeyStroke altF4 = KeyStroke.getKeyStroke("alt F4");
		Zamknij.setAccelerator(altF4);
		AppMenu.add(Zamknij);
		
		Zamknij.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.Zamknij) {
			App.dispose();
		}
	}
}
