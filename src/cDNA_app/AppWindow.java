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
	final int numbCol = 18;
	final int numbRow = 20;
	
	
	// Okno programu
	private PasekMenu Menu;
	private JPanel mainPanel, controlPanel;
	private GroupLayout pozycjaLayout;
	private JButton b_PlusV, b_MinusV, b_PlusH, b_MinusH, b_Image, b_Sizer ,b_x10, b_x1, b_OK, b_Reset;
	private JTextField f_tiffXPosition, f_tiffYPosition, f_sizerXPosition, f_sizerYPosition;
	private JLabel l_tiffX, l_tiffY, l_sizerX, l_sizerY;
	private int tiffXOffset, tiffYOffset, sizerXOffset, sizerYOffset;
	private ImagePanel tiffPanel;
	
	//private JLayeredPane T_imageAndGrid;
	private JPanel T_imageAndGrid;
	private Grid T_grid;
	//private JPanel tiffPanel;
	
	// Zmienne u�ywane w programie - zast�pi� inputem od u�ytkownika
	private String filename = "input/img2.tif";
	
	// Ustawienia suwaka
	private int mode; // 0 - przesuwanie TIFFa, 1 - przesuwanie ramki
	private int speed;
	
	//podobraz
	private BufferedImage img;
	private BufferedImage[][] arrayOfImages;
		
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
		this.setFocusable(true);							// sprawia �e dzia�a nas�uchiwanie klawiszy
		this.setVisible(true);								// w��cza wy�wietlanie okna
		this.setSize(1220, 750); 							//usatwia rozmiar na "sztywno"
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH); 		//maksymalizuje domy�lnie g��wne okno
		this.setTitle("Program do ekstrakcji warto�ci liczbowych z obraz�w mikromacierzy cDNA");
		//this.pack(); 										// powinien sprawia� �e ka�da cz�� ma swoj� preferowan� warto��, lub wieksz�.
															// Alternatywnie mo�na rozmiar JFrame ustawi� na sztywno
		this.setResizable(true);							// �eby nie mo�na by�o zmienia� rozmiaru okna
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		// �eby X zamyka� program a nie tylko okno
		
		mainPanel = new JPanel();							// Dodaje panel g��wny
		this.add(mainPanel);								// Dodaje pane� g��wny do JFrame
				
		// Pasek menu
		this.Menu = new PasekMenu();						// Stworzenie paska menu
		this.setJMenuBar(Menu);								// Dodanie paska do naszego okna
		this.Menu.CloseButton.addActionListener(this);		// Dodanie s�uchacza dla przycisku 
		
		// Panel z kontrolkami siatki
		controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createTitledBorder("Pozycja siatki"));
		
		// Panel z obrazem i siatk�
		tiffPanel = new ImagePanel(filename);
		tiffPanel.setOpaque(false);
		tiffPanel.setBounds(10,15,1200,605);

		
		// Panel opakowuj�cy TIFF i siatk�
		T_imageAndGrid = new JPanel();
		T_imageAndGrid.setBorder(BorderFactory.createTitledBorder("cDNA TIFF"));
		

		// Pocz�tkowa pozycja siatki
		tiffXOffset = 0;
		tiffYOffset = 0;
		
		// Przyciski panelu sterowania
		//b_PlusV = new JButton("+");
		//b_MinusV = new JButton("-");
		//b_PlusH = new JButton("+");
		//b_MinusH = new JButton("-");
		b_Image = new JButton("Obraz");
		b_Sizer = new JButton("Ramka");
		b_x1 = new JButton("x1");
		b_x10 = new JButton("x10");
		b_OK = new JButton("OK");
		b_Reset = new JButton("RESET");
		
		
		// Pola na warto�ci przesuni�cia siatki
		f_tiffXPosition = new JTextField(Integer.toString(tiffXOffset));
		f_tiffYPosition = new JTextField(Integer.toString(tiffYOffset));
		f_sizerXPosition = new JTextField(Integer.toString(tiffXOffset));
		f_sizerYPosition = new JTextField(Integer.toString(tiffYOffset));
		
		// Label 'X' i 'Y'
		l_tiffX = new JLabel("Obraz   X: ");
		l_tiffY = new JLabel("Y: ");
		l_sizerX = new JLabel("Ramka   X: ");
		l_sizerY = new JLabel("Y: ");
		
		// Dodanie przycisk�w do panelu sterowania
		controlPanel.add(l_tiffX);
		//controlPanel.add(b_PlusH);
		//controlPanel.add(b_MinusH);
		controlPanel.add(f_tiffYPosition);
		controlPanel.add(l_tiffY);
		//controlPanel.add(b_PlusV);
		//controlPanel.add(b_MinusV);
		controlPanel.add(f_tiffXPosition);
		controlPanel.add(l_sizerX);
		//controlPanel.add(b_PlusH);
		//controlPanel.add(b_MinusH);
		controlPanel.add(f_sizerXPosition);
		controlPanel.add(l_sizerY);
		//controlPanel.add(b_PlusV);
		//controlPanel.add(b_MinusV);
		controlPanel.add(f_sizerYPosition);
		controlPanel.add(b_Image);
		controlPanel.add(b_Sizer);
		controlPanel.add(b_x1);
		controlPanel.add(b_x10);
		controlPanel.add(b_OK);
		controlPanel.add(b_Reset);
		
		// Dodanie s�uchacza do przycisk�w panelu kontrolnego
		//b_PlusH.addActionListener(this);
		//b_MinusH.addActionListener(this);
		//b_PlusV.addActionListener(this);
		//b_MinusV.addActionListener(this);
		b_Image.addActionListener(this);
		b_Sizer.addActionListener(this);
		b_x1.addActionListener(this);
		b_x10.addActionListener(this);
		b_OK.addActionListener(this);
		b_Reset.addActionListener(this);
		
		// Dodanie s�uchacza p�l na warto�ci przesuni�cia siatki
		//f_tiffYPosition.addActionListener(this);
		//f_tiffXPosition.addActionListener(this);
		
		// Layout panelu sterowania
		pozycjaLayout = new GroupLayout(controlPanel);
		pozycjaLayout.setAutoCreateGaps(true);
		pozycjaLayout.setAutoCreateContainerGaps(true);

		// Rozk�ad horyzontalny elementow panelu kontrolnego
		pozycjaLayout.setHorizontalGroup(pozycjaLayout.createSequentialGroup()
				.addComponent(l_tiffX)
        		//.addComponent(b_PlusH)
        		//.addComponent(b_MinusH)
        		.addComponent(f_tiffXPosition)
        		.addGap(20)
        		.addComponent(l_tiffY)
        		//.addComponent(b_PlusV)
        		//.addComponent(b_MinusV)
        		.addComponent(f_tiffYPosition)
        		.addGap(40)
        		.addComponent(l_sizerX)
        		.addComponent(f_sizerXPosition)
        		.addGap(20)
        		.addComponent(l_sizerY)
        		.addComponent(f_sizerYPosition)
        		.addGap(40)
        		.addComponent(b_Image)
        		.addComponent(b_Sizer)
        		.addComponent(b_x1)
        		.addComponent(b_x10)
        		.addGap(50)
        		.addComponent(b_OK)
        		.addGap(10)
        		.addComponent(b_Reset)
        		
		);
		
		// Rozk�ad wertykalny elementow panelu kontrolnego
		pozycjaLayout.setVerticalGroup(pozycjaLayout.createSequentialGroup()
				.addGroup(pozycjaLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(l_tiffX)
		        		//.addComponent(b_PlusH)
		        		//.addComponent(b_MinusH)
		        		.addComponent(f_tiffXPosition)
		        		//.addGap(75)
		        		.addComponent(l_tiffY)
		        		//.addComponent(b_PlusV)
		        		//.addComponent(b_MinusV)
		        		.addComponent(f_tiffYPosition)
		        		.addComponent(l_sizerX)
		        		.addComponent(f_sizerXPosition)
		        		.addGap(30)
		        		.addComponent(l_sizerY)
		        		.addComponent(f_sizerYPosition)
		        		//.addGap(75)
		        		.addComponent(b_Image)
		        		.addComponent(b_Sizer)
		        		.addComponent(b_x1)
		        		.addComponent(b_x10)
		        		.addComponent(b_OK)
		        		.addComponent(b_Reset)
	            )
		);
		
		mainPanel.setLayout(new BorderLayout());			// Rozk�ad dla okna g�ownego
		
		// Dodanie element�w do panelu warstwowego
		T_imageAndGrid.setLayout(new OverlayLayout(T_imageAndGrid));
		T_imageAndGrid.add(tiffPanel,BorderLayout.CENTER);
		
		
		// Dodanie element�w do panelu g��wnego
		mainPanel.add(controlPanel, BorderLayout.NORTH);	
		mainPanel.add(T_imageAndGrid, BorderLayout.CENTER);
		
		controlPanel.setLayout(pozycjaLayout);				// Zastosowanie rozk�adu element�w panelu kontrolnego
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
	
	//dzieli wybrany fragment obrazu na małe obrazy - zapisuje je w tablicy 'array'
	private BufferedImage[][] divideImage(BufferedImage image, int numbeorOfColumns, int numberOfRows){
		BufferedImage[][] array = new BufferedImage[numbeorOfColumns][numberOfRows];
		int deltaX = image.getWidth() / numbeorOfColumns;
		int deltaY = image.getHeight() / numberOfRows;
		
		for(int row = 0; row < numberOfRows; row++){
			for(int column = 0; column < numbeorOfColumns; column++){
				array[column][row] = image.getSubimage(column * deltaX, row * deltaY, deltaX, deltaY);
			}
		}
		//System.out.println(new Color(array[1][4].getRGB(10, 10)).getGreen());
		return array;
	}
	
	//liczy wartość średnią pikseli dla każdego obrazu
	private double computeAvgValueOfPixels(BufferedImage image){
		int sum = 0, tempValue = 0, pixelCounter = 0;
		double avg = 0.0;
		
		for(int row = 0; row < image.getHeight(); row++){
			for(int column = 0; column < image.getWidth(); column++){
				tempValue = new Color(image.getRGB(column, row)).getGreen();
				if(tempValue > 30){
					sum += tempValue;
					pixelCounter++;
				}
			}
		}
		
		avg = ((double)sum) / ((double)pixelCounter);
		
		return avg;
	}
	
	private void printAvgValues(BufferedImage[][] array, int numberOfColumns, int numberOfRows){
		for(int row = 0; row < numberOfRows; row++){
			for(int column = 0; column < numberOfColumns; column++){
				System.out.print((int)computeAvgValueOfPixels(array[column][row]) + "\t");
			}
			System.out.println();
		}
	}

	/**
	 * Obs�uguje zdarzenia okna programu
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.Menu.CloseButton){
			this.dispose();
		}
		else if(e.getSource() == this.b_x1){
			this.setSpeed(1);
			this.requestFocus();
		}
		else if(e.getSource() == this.b_x10){
			this.setSpeed(10);
			this.requestFocus();
		}
		else if(e.getSource() == this.b_Sizer){
			this.changeModeToSizer();
			this.requestFocus();
		}
		else if(e.getSource() == this.b_Image){
			this.changeModeToImage();
			this.requestFocus();
		}
		else if(e.getSource() == this.b_OK){
			img = tiffPanel.getSubimage(
						tiffPanel.getSizerXPosition(),
						tiffPanel.getSizerYPosition(),
						tiffPanel.getSizerWidth(),
						tiffPanel.getSizerHeight());
			
			arrayOfImages = divideImage(img, numbCol, numbRow);
			printAvgValues(arrayOfImages, numbCol, numbRow);
			tiffPanel.drawSubimage(img);
			this.requestFocus();
		}
		else if(e.getSource() == this.b_Reset){
			tiffPanel.drawOriginalImage();
			tiffPanel.resetTiffPosition();
			tiffPanel.resetSizerPosition();
			this.requestFocus();
		}
//		if(e.getSource() == this.b_PlusH) {
//			tiffYOffset++;
//		}
//		else if(e.getSource() == this.b_MinusH) {
//			tiffYOffset--;
//		}
//		else if(e.getSource() == this.b_PlusV) {
//			tiffXOffset++;
//		}
//		else if(e.getSource() == this.b_MinusV) {
//			tiffXOffset--;
//		}
//		else if(e.getSource() == this.f_tiffYPosition) {
//			tiffYOffset = Integer.parseInt(f_tiffYPosition.getText());
//		}
//		else if(e.getSource() == this.f_tiffXPosition) {
//			tiffXOffset = Integer.parseInt(f_tiffXPosition.getText());
//		}
		
		f_sizerXPosition.setText(Integer.toString(tiffPanel.getSizerXPosition()));
		f_sizerYPosition.setText(Integer.toString(tiffPanel.getSizerYPosition()));
		f_tiffYPosition.setText(Integer.toString(tiffPanel.getTiffYPosition()));
		f_tiffXPosition.setText(Integer.toString(tiffPanel.getTiffXPosition()));
	}
	

	

	@Override
	public void keyReleased(KeyEvent e) { // Kiedy pu�ci si� klawisz
//		if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==0  && speed ==1){
//			this.tiffPanel.moveTiff(e, this.speed);
//		}
//		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==1 && speed ==1){
//			this.tiffPanel.moveSizer(e,this.speed);
//		}
	}

	@Override
	public void keyTyped(KeyEvent e) { // Kiedy symbol znaku na klawiaturze zostanie wys�any do systemu - nie dzia�a dla strza�ek!
		
	}

	@Override
	public void keyPressed(KeyEvent e) { // Kiedy klawisz si� wciska lub jest wciskany
		if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==0  && speed == 1){
			this.tiffPanel.moveTiff(e, this.speed);
		}
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==1 && speed == 1){
			this.tiffPanel.moveSizer(e,this.speed);
		}
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==0  && speed !=1){
			this.tiffPanel.moveTiff(e, this.speed);
		}
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==1 && speed !=1){
			this.tiffPanel.moveSizer(e,this.speed);
		}
		
		f_sizerXPosition.setText(Integer.toString(tiffPanel.getSizerXPosition()));
		f_sizerYPosition.setText(Integer.toString(tiffPanel.getSizerYPosition()));
		f_tiffYPosition.setText(Integer.toString(tiffPanel.getTiffYPosition()));
		f_tiffXPosition.setText(Integer.toString(tiffPanel.getTiffXPosition()));
	}
	
}