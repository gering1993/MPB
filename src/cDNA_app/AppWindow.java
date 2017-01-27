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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

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
	private JButton /*b_PlusV, b_MinusV, b_PlusH, b_MinusH, b_Image, b_Sizer ,b_x10, b_x1,*/ b_OK, b_Reset,
					/*b_gridOn, b_gridOff,*/ b_setTreshld;
	private JToggleButton b_Image, b_Sizer, /*b_x10, b_x1,*/ b_gridOn, b_gridOff;
	private JComboBox speedList;
	private JTextField f_tiffXPosition, f_tiffYPosition, f_sizerXPosition, f_sizerYPosition;
	private JLabel l_tiffX, l_tiffY, l_sizerX, l_sizerY, l_grid;
	private int tiffXOffset, tiffYOffset, sizerXOffset, sizerYOffset;
	private ImagePanel tiffPanel;
	
	//private JLayeredPane T_imageAndGrid;
	private JPanel T_imageAndGrid;
	private Grid T_grid;
	//private JPanel tiffPanel;
	
	// Zmienne uzywane w programie - zastapic inputem od uzytkownika
	private String filename = "input/img2.tif";
	
	// Ustawienia suwaka
	private int mode; // 0 - przesuwanie TIFFa, 1 - przesuwanie ramki
	private int speed;
	
	//podobraz
	private BufferedImage img;
	private BufferedImage[][] arrayOfImages;
	
	// Próg wybierania pikseli
	private int treshHld=30;
	
	// lista etykiet do listy szybkości
	String[] speedStrings = { "x1", "x10"};
		
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
		this.mode=1;
		this.speed=10;
	}
	
	/**
	 * Generuje GUI
	 * @throws IOException
	 */
	private void showGUI() throws IOException {
		this.setFocusable(true);							// sprawia ze dziala nasluchiwanie klawiszy
		this.setVisible(true);								// wlacza wyswietlanie okna
		this.setSize(1220, 750); 							//usatwia rozmiar na "sztywno"
		//this.setExtendedState(JFrame.MAXIMIZED_BOTH); 		//maksymalizuje domyslnie glowne okno
		this.setTitle("Program do ekstrakcji wartosci liczbowych z obrazow mikromacierzy cDNA");
		//this.pack(); 										// powinien sprawiac ze kazda czesc ma swoja preferowana wartosc, lub wieksza.
															// Alternatywnie mozna rozmiar JFrame ustawic na sztywno
		this.setResizable(true);							// zeby nie mozna bylo zmieniac rozmiaru okna
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		// zeby X zamykal program a nie tylko okno
		
		mainPanel = new JPanel();							// Dodaje panel glowny
		this.add(mainPanel);								// Dodaje panel glowny do JFrame
				
		// Pasek menu
		this.Menu = new PasekMenu();						// Stworzenie paska menu
		this.setJMenuBar(Menu);								// Dodanie paska do naszego okna
		this.Menu.b_SaveToTxt.setEnabled(false);
		this.Menu.b_CloseButton.addActionListener(this);	// Dodanie sluchacza dla przycisku 
		this.Menu.b_OpenFileToLoad.addActionListener(this);
		this.Menu.b_SaveToTxt.addActionListener(this);
		
		
		// Panel z kontrolkami siatki
		controlPanel = new JPanel();
		controlPanel.setBorder(BorderFactory.createTitledBorder("Pozycja siatki"));
		
		// Panel z obrazem i siatka
		tiffPanel = new ImagePanel(filename);
		tiffPanel.setOpaque(false);
		tiffPanel.setBounds(10,15,1200,605);

		
		// Panel opakowujacy TIFF i siatke
		T_imageAndGrid = new JPanel();
		T_imageAndGrid.setBorder(BorderFactory.createTitledBorder("cDNA TIFF"));
		

		// Poczatkowa pozycja siatki
		tiffXOffset = 0;
		tiffYOffset = 0;
		
		// Przyciski panelu sterowania
		//b_PlusV = new JButton("+");
		//b_MinusV = new JButton("-");
		//b_PlusH = new JButton("+");
		//b_MinusH = new JButton("-");
		b_Image = new JToggleButton("Obraz");
		b_Sizer = new JToggleButton("Ramka");
		//b_x1 = new JToggleButton("x1");
		//b_x10 = new JToggleButton("x10");
		b_OK = new JButton("OK");
		b_Reset = new JButton("RESET");
		b_gridOn = new JToggleButton("Wł.");
		b_gridOff = new JToggleButton("Wył.");
		b_setTreshld= new JButton("Wybór progu");
		
		speedList = new JComboBox(speedStrings);
		
		//Domysle wylaczenie niektorych przyciskow
		b_OK.setEnabled(false);
		b_Reset.setEnabled(false);
		b_gridOff.setEnabled(false);
		b_gridOn.setEnabled(false);
		b_setTreshld.setEnabled(false);
		// Domyslny wybor niektorych 
		b_Sizer.setSelected(true);
		//b_x10.setSelected(true);
		b_gridOff.setSelected(true);
		
		speedList.setSelectedIndex(1);
		
		// Pola na wartosci przesuniecia siatki
		f_tiffXPosition = new JTextField(Integer.toString(tiffXOffset));
		f_tiffYPosition = new JTextField(Integer.toString(tiffYOffset));
		f_sizerXPosition = new JTextField(Integer.toString(tiffXOffset));
		f_sizerYPosition = new JTextField(Integer.toString(tiffYOffset));
		
		// Label 'X' i 'Y'
		l_tiffX = new JLabel("Obraz   X: ");
		l_tiffY = new JLabel("Y: ");
		l_sizerX = new JLabel("Ramka   X: ");
		l_sizerY = new JLabel("Y: ");
		l_grid = new JLabel("Siatka");
		
		// Dodanie przyciskow do panelu sterowania
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
		//controlPanel.add(b_x1);
		//controlPanel.add(b_x10);
		controlPanel.add(speedList);
		controlPanel.add(b_OK);
		controlPanel.add(b_Reset);
		controlPanel.add(l_grid);
		controlPanel.add(b_gridOn);
		controlPanel.add(b_gridOff);
		controlPanel.add(b_setTreshld);
		
		// Dodanie sluchacza do przyciskow panelu kontrolnego
		//b_PlusH.addActionListener(this);
		//b_MinusH.addActionListener(this);
		//b_PlusV.addActionListener(this);
		//b_MinusV.addActionListener(this);
		b_Image.addActionListener(this);
		b_Sizer.addActionListener(this);
		speedList.addActionListener(this);
		//b_x1.addActionListener(this);
		//b_x10.addActionListener(this);
		b_OK.addActionListener(this);
		b_Reset.addActionListener(this);
		b_gridOn.addActionListener(this);
		b_gridOff.addActionListener(this);
		b_setTreshld.addActionListener(this);
		
		// Dodanie sluchacza pol na wartosci przesuniecia siatki
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
        		.addGap(10)
        		.addComponent(l_tiffY)
        		//.addComponent(b_PlusV)
        		//.addComponent(b_MinusV)
        		.addComponent(f_tiffYPosition)
        		.addGap(30)
        		.addComponent(l_sizerX)
        		.addComponent(f_sizerXPosition)
        		.addGap(10)
        		.addComponent(l_sizerY)
        		.addComponent(f_sizerYPosition)
        		.addGap(30)
        		.addComponent(b_Image)
        		.addComponent(b_Sizer)
        		.addGap(20)
        		.addComponent(speedList)
        		//.addComponent(b_x1)
        		//.addComponent(b_x10)
        		.addGap(30)
        		.addComponent(l_grid)
        		.addComponent(b_gridOn)
        		.addComponent(b_gridOff)
        		.addGap(30)
        		.addComponent(b_setTreshld)
        		.addComponent(b_OK)
        		//.addGap(10)
        		.addComponent(b_Reset)
        		
		);
		
		// Rozklad wertykalny elementow panelu kontrolnego
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
		        		//.addGap(30)
		        		.addComponent(l_sizerY)
		        		.addComponent(f_sizerYPosition)
		        		//.addGap(75)
		        		.addComponent(b_Image)
		        		.addComponent(b_Sizer)
		        		.addComponent(l_grid)
		        		.addComponent(b_gridOn)
		        		.addComponent(b_gridOff)
		        		//.addComponent(b_x1)
		        		//.addComponent(b_x10)
		        		.addComponent(speedList)
		        		.addComponent(b_setTreshld)
		        		.addComponent(b_OK)
		        		.addComponent(b_Reset)
	            )
		);
		
		mainPanel.setLayout(new BorderLayout());			// Rozklad dla okna glownego
		
		// Dodanie elementow do panelu warstwowego
		T_imageAndGrid.setLayout(new OverlayLayout(T_imageAndGrid));
		T_imageAndGrid.add(tiffPanel,BorderLayout.CENTER);
		
		
		// Dodanie elementow do panelu glownego
		mainPanel.add(controlPanel, BorderLayout.NORTH);	
		mainPanel.add(T_imageAndGrid, BorderLayout.CENTER);
		
		controlPanel.setLayout(pozycjaLayout);				// Zastosowanie rozkladu elementow panelu kontrolnego
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
	
	private void gridOn(){
		tiffPanel.drawGrid();
	}
	
	private void gridOff(){
		tiffPanel.drawFrame();
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
		return array;
	}
	
	//liczy wartość średnią pikseli dla każdego obrazu
	private double computeAvgValueOfPixels(BufferedImage image){
		int sum = 0, tempValue = 0, pixelCounter = 0;
		double avg = 0.0;
		
		for(int row = 0; row < image.getHeight(); row++){
			for(int column = 0; column < image.getWidth(); column++){
				tempValue = new Color(image.getRGB(column, row)).getGreen();
				if(tempValue > treshHld){
					sum += tempValue;
					pixelCounter++;
				}
			}
		}
		avg = ((double)sum) / ((double)pixelCounter);
		return avg;
	}
	
// FUNKCJA ZAKOMENTOWANA BO NIE JEST NIGDZIE UŻYWANA	
//	private void printAvgValues(BufferedImage[][] array, int numberOfColumns, int numberOfRows){ 
//		for(int row = 0; row < numberOfRows; row++){
//			for(int column = 0; column < numberOfColumns; column++){
//				System.out.print((int)computeAvgValueOfPixels(array[column][row]) + "\t");
//			}
//			System.out.println();
//		}
//	}
	
	private void saveAvgValues (String directoryPath, BufferedImage firstTiffSubimage, BufferedImage secondTiffSubimage) throws FileNotFoundException{
		BufferedImage[][] dividedFirstTiffSumbimage = divideImage(firstTiffSubimage, numbCol, numbRow);
		BufferedImage[][] dividedSecondTiffSumbimage = divideImage(secondTiffSubimage, numbCol, numbRow);
		
		PrintWriter firstOut = new PrintWriter(directoryPath+File.separator+this.tiffPanel.getFirstTiffFilename().replace(".tif", ".txt"));
		for(int row = 0; row < numbRow; row++){
			for(int column = 0; column < numbCol; column++){
				//System.out.print((int)computeAvgValueOfPixels(dividedFirstTiffSumbimage[column][row]) + "\t");
				firstOut.write((int)computeAvgValueOfPixels(dividedFirstTiffSumbimage[column][row]) + "\t");
			}
			firstOut.write("\n");
		}
		firstOut.close();
		
		PrintWriter secondOut = new PrintWriter(directoryPath+File.separator+this.tiffPanel.getSecondTiffFilename().replace(".tif", ".txt"));
		for(int row = 0; row < numbRow; row++){
			for(int column = 0; column < numbCol; column++){
				//System.out.print((int)computeAvgValueOfPixels(dividedSecondTiffSumbimage[column][row]) + "\t");
				secondOut.write((int)computeAvgValueOfPixels(dividedSecondTiffSumbimage[column][row]) + "\t");
			}
			secondOut.write("\n");
		}
		secondOut.close();
	}
	
	private void setTreshHld(int median){
		this.treshHld=4;
	}

	/**
	 * Obsluguje zdarzenia okna programu
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.Menu.b_CloseButton){
			this.dispose();
		}
		else if(e.getSource() == this.speedList){
			//System.out.println(this.speedList.getSelectedItem().toString());
			if(this.speedList.getSelectedItem().toString().equals("x1")){
				this.setSpeed(1);
			}
			else if(this.speedList.getSelectedItem().toString().equals("x10")){
				this.setSpeed(10);
			}
			/*this.b_x1.setSelected(true);
			this.b_x10.setSelected(false);
			this.setSpeed(1);*/
			//this.speedList.setS
			this.requestFocus();
		}
		/*else if(e.getSource() == this.b_x1){
			this.b_x1.setSelected(true);
			this.b_x10.setSelected(false);
			this.setSpeed(1);
			this.requestFocus();
		}
		else if(e.getSource() == this.b_x10){
			this.b_x10.setSelected(true);
			this.b_x1.setSelected(false);
			this.setSpeed(10);
			this.requestFocus();
		}*/
		else if(e.getSource() == this.b_Sizer){
			this.b_Sizer.setSelected(true);
			this.b_Image.setSelected(false);
			this.changeModeToSizer();
			this.requestFocus();
		}
		else if(e.getSource() == this.b_Image){
			this.b_Image.setSelected(true);
			this.b_Sizer.setSelected(false);
			this.changeModeToImage();
			this.requestFocus();
		}
		else if(e.getSource() == this.b_OK){
			img = tiffPanel.getFirstTiffSubimage(
						tiffPanel.getSizerXPosition()-tiffPanel.getTiffXPosition(),
						tiffPanel.getSizerYPosition()-tiffPanel.getTiffYPosition(),
						tiffPanel.getSizerWidth(),
						tiffPanel.getSizerHeight());
			
			this.tiffPanel.setRawFirstSubimage(img);
			
//			arrayOfImages = divideImage(img, numbCol, numbRow);
//			printAvgValues(arrayOfImages, numbCol, numbRow);
			
			BufferedImage imgSecond = tiffPanel.getSecondTiffSubimage(
						tiffPanel.getSizerXPosition()-tiffPanel.getTiffXPosition(),
						tiffPanel.getSizerYPosition()-tiffPanel.getTiffYPosition(),
						tiffPanel.getSizerWidth(),
						tiffPanel.getSizerHeight());
			
			this.tiffPanel.setRawSecondSubimage(imgSecond);
			
//			System.out.println("=======================");
//			
//			BufferedImage[][] arrayOfImagesSecondTiff = divideImage(imgSecond, numbCol, numbRow);
//			printAvgValues(arrayOfImagesSecondTiff, numbCol, numbRow);
			
			BufferedImage colorizedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			colorizedImg.getGraphics().drawImage(img, 0, 0,null);
			
			for (int i=0;i<colorizedImg.getWidth(); i++){
				for (int j=0;j<colorizedImg.getHeight(); j++){
					Color pixelColor = new Color (colorizedImg.getRGB(i, j));
					if (pixelColor.getRed() > treshHld){
						Color redColor = new Color (pixelColor.getRed(),0,0);
						colorizedImg.setRGB(i, j, redColor.getRGB());
					}
				}
			}
			tiffPanel.drawSubimage(colorizedImg);
			this.Menu.b_SaveToTxt.setEnabled(true);
			this.b_OK.setEnabled(false);
			this.b_setTreshld.setEnabled(false);
			this.requestFocus();
		}
		else if(e.getSource() == this.b_Reset){
			tiffPanel.drawOriginalImage();
			tiffPanel.resetTiffPosition();
			tiffPanel.resetSizerPosition();
			this.Menu.b_SaveToTxt.setEnabled(false);
			this.b_OK.setEnabled(false);
			this.b_setTreshld.setEnabled(true);
			this.requestFocus();
		}
		else if(e.getSource() == this.b_gridOn){
			this.b_gridOn.setSelected(true);
			this.b_gridOff.setSelected(false);
			gridOn();
			this.requestFocus();
		}
		else if(e.getSource() == this.b_gridOff){
			this.b_gridOff.setSelected(true);
			this.b_gridOn.setSelected(false);
			gridOff();
			this.requestFocus();
		}
		else if(e.getSource() == this.Menu.b_OpenFileToLoad){
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Otwórz plik TIFF");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION && Pattern.matches("^.+\\.tif", fc.getSelectedFile().getAbsolutePath())){
				try {
					this.tiffPanel.loadTiff(fc.getSelectedFile().getAbsolutePath());
					this.tiffPanel.setFirstTiffFilename(fc.getSelectedFile().getName());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}else if (returnVal == JFileChooser.APPROVE_OPTION && !Pattern.matches("^.+\\.tif", fc.getSelectedFile().getAbsolutePath())){
				throw new Error(); 
			}
			this.Menu.b_SaveToTxt.setEnabled(false);
			//this.b_OK.setEnabled(true);
			//this.b_OK.setEnabled(true);
			this.b_Reset.setEnabled(true);
			this.b_gridOff.setEnabled(true);
			this.b_gridOn.setEnabled(true);
			this.b_setTreshld.setEnabled(true);
			this.b_setTreshld.setEnabled(true);
			this.requestFocus();
		}
		else if(e.getSource()==this.Menu.b_SaveToTxt){
			System.out.println("Wybierz folder");
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Wybierz folder do zapisu");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//int returnVal = fc.showOpenDialog(this);
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			
				try {
					saveAvgValues(fc.getSelectedFile().getAbsolutePath(), this.tiffPanel.getRawFirstSubimage(), this.tiffPanel.getRawSecondSubimage());
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		else if (e.getSource()==this.b_setTreshld){
			System.out.println("Set treshhold");
			HashMap<Integer, Integer> statisticsHashMap = new HashMap<Integer, Integer>();
			for (int i=0;i<this.tiffPanel.tiff.getWidth();i++){
				for (int j=0;j<this.tiffPanel.tiff.getHeight();j++){
					//System.out.println("i:"+i+" j:"+j+" width:"+this.tiffPanel.tiff.getWidth()+" height:"+this.tiffPanel.tiff.getHeight());
					int value = new Color(this.tiffPanel.tiff.getRGB(i, j)).getGreen();
					if (!statisticsHashMap.containsKey(value)){
						statisticsHashMap.put(value, 1);
					}
					else{
						statisticsHashMap.replace(value, statisticsHashMap.get(value)+1);
					}
				}
			}
//			System.out.println("Statistic:");
//			System.out.println("=======================");
//			for (Map.Entry<Integer, Integer> entry : statisticsHashMap.entrySet()){
//				System.out.println("Key:"+entry.getKey()+" , value:"+entry.getValue());
//			}
			
//			ArrayList<Integer> statistics = new ArrayList<Integer>();
//			for (Map.Entry<Integer, Integer> entry : statisticsHashMap.entrySet()){
//				statistics.add(entry.getValue());
//			}
//			
//			int medianCount;
//			if (statistics.size() %2 == 1){
//				medianCount=statistics.get((int) Math.floor(statistics.size()/2));
//			}else{
//				medianCount=statistics.get(statistics.size()/2);
//			}
//			
//			for (Map.Entry<Integer, Integer> entry : statisticsHashMap.entrySet()){
//				if (entry.getValue()==medianCount){
//					System.out.println("Mediana: "+entry.getKey());
//					setTreshHld(entry.getKey());
//					break;
//				}
//			}
			
			int actualDominant=0;
			int actualDominantCount=0;
			for (Map.Entry<Integer, Integer> entry : statisticsHashMap.entrySet()){
				if (entry.getValue() > actualDominantCount){
					actualDominantCount=entry.getValue();
					actualDominant=entry.getKey();
				}
			}
			setTreshHld(actualDominant);
			System.out.println("Dominant:"+actualDominant);
			this.b_OK.setEnabled(true);
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
	public void keyReleased(KeyEvent e) { // Kiedy pusci sie klawisz
//		if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==0  && speed ==1){
//			this.tiffPanel.moveTiff(e, this.speed);
//		}
//		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) && mode==1 && speed ==1){
//			this.tiffPanel.moveSizer(e,this.speed);
//		}
	}

	@Override
	public void keyTyped(KeyEvent e) { // Kiedy symbol znaku na klawiaturze zostanie wys�any do systemu - nie dziala dla strzalek!
		
	}

	@Override
	public void keyPressed(KeyEvent e) { // Kiedy klawisz sie wciska lub jest wciskany
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