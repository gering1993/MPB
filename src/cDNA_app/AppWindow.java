package cDNA_app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.scene.input.KeyCode;

import javax.imageio.ImageIO;
import javax.swing.*;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;

public class AppWindow extends JFrame implements KeyListener, ActionListener 
{
	// Okno programu
	private PasekMenu Menu;
	private JPanel mainPanel, controlPanel;
	private GroupLayout pozycjaLayout;
	private JButton b_PlusV, b_MinusV, b_PlusH, b_MinusH, b_Image, b_Sizer ,b_x10, b_x1;
	private JTextField f_horizontalValue, f_verticalValue;
	private JLabel l_X, l_Y;
	private int VerticalOffset, HorizontalOffset;
	private ImagePanel tiffPanel;
	
	//private JLayeredPane T_imageAndGrid;
	private JPanel T_imageAndGrid;
	private Grid T_grid;
	//private JPanel tiffPanel;
	
	// Zmienne u¿ywane w programie - zast¹piæ inputem od u¿ytkownika
	private String filename = "input/img2.tif";
	
	// Ustawienia suwaka
	private int mode; // 0 - przesuwanie TIFFa, 1 - przesuwanie ramki
	private int speed;
		
	/**
	 * Konstruktor
	 * @throws IOException
	 */
	public AppWindow()  {
		try {
			this.showGUI();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		addKeyListener(this);
		this.mode=0;
		this.speed=10;
	}
	
	/**
	 * Generuje GUI
	 * @throws IOException
	 */
	private void showGUI() throws IOException {
		this.setFocusable(true);							// sprawia ¿e dzia³a nas³uchiwanie klawiszy
		this.setVisible(true);								// w³¹cza wyœwietlanie okna
		this.setSize(1220, 750); 							//usatwia rozmiar na "sztywno"
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH); 		//maksymalizuje domyœlnie g³ówne okno
		this.setTitle("Program do ekstrakcji wartoœci liczbowych z obrazów mikromacierzy cDNA");
		//this.pack(); 										// powinien sprawiaæ ¿e ka¿da czêœæ ma swoj¹ preferowan¹ wartoœæ, lub wieksz¹.
															// Alternatywnie mo¿na rozmiar JFrame ustawiæ na sztywno
		this.setResizable(true);							// ¯eby nie mo¿na by³o zmieniaæ rozmiaru okna
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		// ¯eby X zamyka³ program a nie tylko okno
		
		mainPanel = new JPanel();							// Dodaje panel g³ówny
		this.add(mainPanel);								// Dodaje pane³ g³ówny do JFrame
				
		// Pasek menu
		this.Menu = new PasekMenu();						// Stworzenie paska menu
		this.setJMenuBar(Menu);								// Dodanie paska do naszego okna
		this.Menu.CloseButton.addActionListener(this);		// Dodanie s³uchacza dla przycisku 
		
		// Panel z kontrolkami siatki
		controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createTitledBorder("Pozycja siatki"));
		
		// Panel z obrazem i siatk¹
		tiffPanel = new ImagePanel(filename);
		tiffPanel.setOpaque(false);
		tiffPanel.setBounds(10,15,1200,605);

		
		// Panel opakowuj¹cy TIFF i siatkê
		T_imageAndGrid = new JPanel();
		T_imageAndGrid.setBorder(BorderFactory.createTitledBorder("cDNA TIFF"));
		

		// Pocz¹tkowa pozycja siatki
		VerticalOffset = 0;
		HorizontalOffset = 0;
		
		// Przyciski panelu sterowania
		b_PlusV = new JButton("+");
		b_MinusV = new JButton("-");
		b_PlusH = new JButton("+");
		b_MinusH = new JButton("-");
		b_Image = new JButton("Obraz");
		b_Sizer = new JButton("Ramka");
		b_x1 = new JButton("x1");
		b_x10 = new JButton("x10");
		
		
		// Pola na wartoœci przesuniêcia siatki
		f_horizontalValue = new JTextField(Integer.toString(VerticalOffset));
		f_verticalValue = new JTextField(Integer.toString(HorizontalOffset));
		
		// Label 'X' i 'Y'
		l_X = new JLabel("X: ");
		l_Y = new JLabel("Y: ");
		
		// Dodanie przycisków do panelu sterowania
		controlPanel.add(l_X);
		controlPanel.add(b_PlusH);
		controlPanel.add(b_MinusH);
		controlPanel.add(f_verticalValue);
		controlPanel.add(l_Y);
		controlPanel.add(b_PlusV);
		controlPanel.add(b_MinusV);
		controlPanel.add(f_horizontalValue);
		controlPanel.add(b_Image);
		controlPanel.add(b_Sizer);
		controlPanel.add(b_x1);
		controlPanel.add(b_x10);
		
		// Dodanie s³uchacza do przycisków panelu kontrolnego
		b_PlusH.addActionListener(this);
		b_MinusH.addActionListener(this);
		b_PlusV.addActionListener(this);
		b_MinusV.addActionListener(this);
		b_Image.addActionListener(this);
		b_Sizer.addActionListener(this);
		b_x1.addActionListener(this);
		b_x10.addActionListener(this);
		
		// Dodanie s³uchacza pól na wartoœci przesuniêcia siatki
		f_verticalValue.addActionListener(this);
		f_horizontalValue.addActionListener(this);
		
		// Layout panelu sterowania
		pozycjaLayout = new GroupLayout(controlPanel);
		pozycjaLayout.setAutoCreateGaps(true);
		pozycjaLayout.setAutoCreateContainerGaps(true);

		// Rozk³ad horyzontalny elementow panelu kontrolnego
		pozycjaLayout.setHorizontalGroup(pozycjaLayout.createSequentialGroup()
				.addComponent(l_X)
        		.addComponent(b_PlusH)
        		.addComponent(b_MinusH)
        		.addComponent(f_verticalValue)
        		.addGap(30)
        		.addComponent(l_Y)
        		.addComponent(b_PlusV)
        		.addComponent(b_MinusV)
        		.addComponent(f_horizontalValue)
        		.addGap(30)
        		.addComponent(b_Image)
        		.addComponent(b_Sizer)
        		.addComponent(b_x1)
        		.addComponent(b_x10)
        		
		);
		
		// Rozk³ad wertykalny elementow panelu kontrolnego
		pozycjaLayout.setVerticalGroup(pozycjaLayout.createSequentialGroup()
				.addGroup(pozycjaLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(l_X)
		        		.addComponent(b_PlusH)
		        		.addComponent(b_MinusH)
		        		.addComponent(f_verticalValue)
		        		//.addGap(75)
		        		.addComponent(l_Y)
		        		.addComponent(b_PlusV)
		        		.addComponent(b_MinusV)
		        		.addComponent(f_horizontalValue)
		        		//.addGap(75)
		        		.addComponent(b_Image)
		        		.addComponent(b_Sizer)
		        		.addComponent(b_x1)
		        		.addComponent(b_x10)
	            )
		);
		
		mainPanel.setLayout(new BorderLayout());			// Rozk³ad dla okna g³ownego
		
		// Dodanie elementów do panelu warstwowego
		T_imageAndGrid.setLayout(new OverlayLayout(T_imageAndGrid));
		T_imageAndGrid.add(tiffPanel,BorderLayout.CENTER);
		
		
		// Dodanie elementów do panelu g³ównego
		mainPanel.add(controlPanel, BorderLayout.NORTH);	
		mainPanel.add(T_imageAndGrid, BorderLayout.CENTER);
		
		controlPanel.setLayout(pozycjaLayout);				// Zastosowanie rozk³adu elementów panelu kontrolnego
	}
	
	private void changeModeToSizer(){
		this.mode=1;
	}
	private void changeModeToImage(){
		this.mode=0;
	}
	private void setSpeed(int speed){
		this.speed=speed;
	}

	/**
	 * Obs³uguje zdarzenia okna programu
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.Menu.CloseButton){
			this.dispose();
		}
		if (e.getSource() == this.b_x1){
			this.setSpeed(1);
			this.requestFocus();
		}
		if (e.getSource() == this.b_x10){
			this.setSpeed(10);
			this.requestFocus();
		}
		if (e.getSource() == this.b_Sizer){
			this.changeModeToSizer();
			this.requestFocus();
		}
		if (e.getSource() == this.b_Image){
			this.changeModeToImage();
			this.requestFocus();
		}
		if(e.getSource() == this.b_PlusH) {
			HorizontalOffset++;
		}
		else if(e.getSource() == this.b_MinusH) {
			HorizontalOffset--;
		}
		else if(e.getSource() == this.b_PlusV) {
			VerticalOffset++;
		}
		else if(e.getSource() == this.b_MinusV) {
			VerticalOffset--;
		}
		else if(e.getSource() == this.f_verticalValue) {
			HorizontalOffset = Integer.parseInt(f_verticalValue.getText());
		}
		else if(e.getSource() == this.f_horizontalValue) {
			VerticalOffset = Integer.parseInt(f_horizontalValue.getText());
		}
		
		f_verticalValue.setText(Integer.toString(HorizontalOffset));
		f_horizontalValue.setText(Integer.toString(VerticalOffset));
		
		
	}
	

	

	@Override
	public void keyReleased(KeyEvent e) { // Kiedy puœci siê klawisz
		if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==0  && speed ==1){
			this.tiffPanel.moveTiff(e, this.speed);
		}
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==1 && speed ==1){
			this.tiffPanel.moveSizer(e,this.speed);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) { // Kiedy symbol znaku na klawiaturze zostanie wys³any do systemu - nie dzia³a dla strza³ek!
		
	}

	@Override
	public void keyPressed(KeyEvent e) { // Kiedy klawisz siê wciska lub jest wciskany
		if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==0  && speed !=1){
			this.tiffPanel.moveTiff(e, this.speed);
		}
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==1 && speed !=1){
			this.tiffPanel.moveSizer(e,this.speed);
		}
	}
	
}