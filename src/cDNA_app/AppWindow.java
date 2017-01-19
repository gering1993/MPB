package cDNA_app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;

public class Test extends JFrame implements ActionListener
{
	// Pola
	private PasekMenu Menu;
	private JPanel PanelGlowny, PanelPozycja;
	private GroupLayout layout, pozycjaLayout;
	private JButton bPlusV, bMinusV, bPlusH, bMinusH, bOK;
	private JTextField VvalueText, HvalueText;
	private JLabel lX, lY;
	private int VerticalOffset, HorizontalOffset;
	private ImagePanel ImageAndGrid;
	
	private String filename = "input/img2.tif";
	
	// Funkcja main
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				Test MyApp = new Test();
				//MyApp.setController();
				MyApp.setVisible(true);
				MyApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});	
	}
		
	/*
	 * Konstruktory
	 * */
	public Test() {
		this.zrobGUI();
	}
	
	/*
	 * Tu robi sie GUI
	 */
	private void zrobGUI() {
		// ustaw rozmiar okna, nazwij okno
		this.setSize(1200, 675);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setTitle("cDNA analyser");
		this.pack();
		this.setResizable(true);
		
		PanelGlowny = new JPanel();
		this.add(PanelGlowny);
				
		/* 
		 * tworzy paske menu. Menu jest obiektem klasy PasekMenu, ktora
		 * rozszerza klase JMenuBar, stad dalej metoda klasy nadrzednej
		 */
		Menu = new PasekMenu(this);
		setJMenuBar(Menu);
		
		PanelPozycja = new JPanel();
		PanelPozycja.setBorder(BorderFactory.createTitledBorder("Pozycja siatki"));
		
        try {
        	ImageAndGrid = new ImagePanel(filename);
        	ImageAndGrid.setBorder(BorderFactory.createTitledBorder("cDNA TIFF"));
        } catch (java.io.IOException ioe) {
            System.out.println(ioe);
        }
		
		VerticalOffset = 0;
		HorizontalOffset = 0;
		
		bPlusV = new JButton("+");
		bMinusV = new JButton("-");
		bPlusH = new JButton("+");
		bMinusH = new JButton("-");
		bOK = new JButton("OK");
		
		VvalueText = new JTextField(Integer.toString(VerticalOffset));
		HvalueText = new JTextField(Integer.toString(HorizontalOffset));
		
		lX = new JLabel("X: ");
		lY = new JLabel("Y: ");
		
		PanelPozycja.add(lX);
		PanelPozycja.add(bPlusH);
		PanelPozycja.add(bMinusH);
		PanelPozycja.add(HvalueText);
		PanelPozycja.add(lY);
		PanelPozycja.add(bPlusV);
		PanelPozycja.add(bMinusV);
		PanelPozycja.add(VvalueText);
		PanelPozycja.add(bOK);
		
		// dodanie słuchacza dla elementów panelu
		bPlusH.addActionListener(this);
		bMinusH.addActionListener(this);
		bPlusV.addActionListener(this);
		bMinusV.addActionListener(this);
		bOK.addActionListener(this);
		
		HvalueText.addActionListener(this);
		VvalueText.addActionListener(this);
		
		pozycjaLayout = new GroupLayout(PanelPozycja);
		pozycjaLayout.setAutoCreateGaps(true);
		pozycjaLayout.setAutoCreateContainerGaps(true);

		pozycjaLayout.setHorizontalGroup(pozycjaLayout.createSequentialGroup()
				.addComponent(lX)
        		.addComponent(bPlusH)
        		.addComponent(bMinusH)
        		.addComponent(HvalueText)
        		.addGap(30)
        		.addComponent(lY)
        		.addComponent(bPlusV)
        		.addComponent(bMinusV)
        		.addComponent(VvalueText)
        		.addGap(30)
        		.addComponent(bOK)      
		);

		pozycjaLayout.setVerticalGroup(pozycjaLayout.createSequentialGroup()
				.addGroup(pozycjaLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lX)
		        		.addComponent(bPlusH)
		        		.addComponent(bMinusH)
		        		.addComponent(HvalueText)
		        		//.addGap(75)
		        		.addComponent(lY)
		        		.addComponent(bPlusV)
		        		.addComponent(bMinusV)
		        		.addComponent(VvalueText)
		        		//.addGap(75)
		        		.addComponent(bOK)
	            )
		);
		
		//PanelGlowny.setLayout(layout);
		PanelGlowny.setLayout(new BorderLayout());
		PanelGlowny.add(PanelPozycja, BorderLayout.NORTH);
		PanelGlowny.add(ImageAndGrid, BorderLayout.CENTER);
		PanelPozycja.setLayout(pozycjaLayout);
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.bPlusH) {
			HorizontalOffset++;
		}
		else if(e.getSource() == this.bMinusH) {
			HorizontalOffset--;
		}
		else if(e.getSource() == this.bPlusV) {
			VerticalOffset++;
		}
		else if(e.getSource() == this.bMinusV) {
			VerticalOffset--;
		}
		else if(e.getSource() == this.HvalueText) {
			HorizontalOffset = Integer.parseInt(HvalueText.getText());
		}
		else if(e.getSource() == this.VvalueText) {
			VerticalOffset = Integer.parseInt(VvalueText.getText());
		}
		else if(e.getSource() == this.bOK) {
			System.out.println("Wciśnięto " + e.getActionCommand().toString());
			System.out.println("Położenie siatki:\nX: " + Integer.toString(HorizontalOffset) + "\tY: " + Integer.toString(VerticalOffset));
		}
		
		HvalueText.setText(Integer.toString(HorizontalOffset));
		VvalueText.setText(Integer.toString(VerticalOffset));
	}
}