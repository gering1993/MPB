package cDNA_app;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.imageio.ImageIO;
import javax.media.jai.widget.ScrollingImagePanel;
import javax.media.jai.JAI;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.sun.javafx.geom.Dimension2D;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.prism.paint.ImagePattern;

public class ImagePanel extends JPanel {

  	private RenderedImage op;
  	
  	BufferedImage sizer, frame, grid;
  	BufferedImage tiff, tiffOriginal, subimage, secondColorTiff;
  	private BufferedImage rawFirstSubimage, rawSecondSubimage;
  	
  	private String firstTiffFilename, secondTiffFilename;
  	
  	private int tiffXPosition;
  	private int tiffYPosition;
  	private int sizerXPosition;
  	private int sizerYPosition;
  	
	private boolean isImageLoaded;

    public ImagePanel(String filename)  {
    	tiffXPosition = 0;
    	tiffYPosition = 0;
    	sizerXPosition = 0;
    	sizerYPosition = 0;    	
    	isImageLoaded=false;

    }
    
    public void loadTiff(String path) throws IOException{
    	frame = ImageIO.read(new File("lib_files/ramka2.png"));
    	grid = ImageIO.read(new File("lib_files/siatka.png"));
		FileSeekableStream stream = new FileSeekableStream(path);
		TIFFDecodeParam decodeParam = new TIFFDecodeParam();
		decodeParam.setDecodePaletteAsShorts(true);
		ParameterBlock params = new ParameterBlock();
		params.add(stream);
		RenderedOp image1 = JAI.create("tiff", params);
		tiff = image1.getAsBufferedImage();
		tiffOriginal = tiff;
		sizer = frame;
		isImageLoaded=true;
		
		String secondTiffPath;
		if (Pattern.matches(".+Cy5.+", path)){
			 secondTiffPath = path.replaceAll("Cy5","Cy3");
			 System.out.println(secondTiffPath);
		}else{
			 secondTiffPath = path.replaceAll("Cy3","Cy5");
		}
		
		stream = new FileSeekableStream(secondTiffPath);
		decodeParam = new TIFFDecodeParam();
		decodeParam.setDecodePaletteAsShorts(true);
		params = new ParameterBlock();
		params.add(stream);
		image1 = JAI.create("tiff", params);
		secondColorTiff = image1.getAsBufferedImage();
		
		repaint();
    }
    
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(tiff, tiffXPosition, tiffYPosition, null);
		g.drawImage(sizer, sizerXPosition, sizerYPosition, null);
	}
    
	
	public void moveTiff (KeyEvent e, int speed){
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			this.tiffXPosition-=speed;
			if((sizerXPosition + sizer.getWidth()) > (tiffXPosition + tiff.getWidth())){
				sizerXPosition = tiffXPosition + tiff.getWidth() - sizer.getWidth();
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			this.tiffXPosition+=speed;
			if(sizerXPosition < tiffXPosition){
				sizerXPosition = tiffXPosition;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP){
			this.tiffYPosition-=speed;
			if((sizerYPosition + sizer.getHeight()) > (tiffYPosition + tiff.getHeight())){
				sizerYPosition = tiffYPosition + tiff.getHeight() - sizer.getHeight();
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			this.tiffYPosition+=speed;
			if(sizerYPosition < tiffYPosition){
				sizerYPosition = tiffYPosition;
			}
		}
		repaint();
	}
	
	public void setRawFirstSubimage(BufferedImage rawFirstSubimageGenerated){
		this.rawFirstSubimage=rawFirstSubimageGenerated;
	}
	
	public void setRawSecondSubimage(BufferedImage rawSecondSubimageGenerated){
		this.rawSecondSubimage=rawSecondSubimageGenerated;
	}
	
	public BufferedImage getRawFirstSubimage(){
		return this.rawFirstSubimage;
	}
	
	public BufferedImage getRawSecondSubimage(){
		return this.rawSecondSubimage;
	}

	public void moveSizer(KeyEvent e, int speed) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			this.sizerXPosition-=speed;
			if(sizerXPosition < tiffXPosition){
				tiffXPosition = sizerXPosition;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			this.sizerXPosition+=speed;
			if((sizerXPosition + sizer.getWidth()) > (tiffXPosition + tiff.getWidth())){
				tiffXPosition = sizerXPosition + sizer.getWidth() - tiff.getWidth();
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP){
			this.sizerYPosition-=speed;
			if(sizerYPosition < tiffYPosition){
				tiffYPosition = sizerYPosition;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			this.sizerYPosition+=speed;
			if((sizerYPosition + sizer.getHeight()) > (tiffYPosition + tiff.getHeight())){
				tiffYPosition = sizerYPosition + sizer.getHeight() - tiff.getHeight();
			}
		}
		repaint();
	}
	
	//rysuj ramkę - włącz ramkę, wyłącz siatkę
	public void drawFrame(){
		sizer = frame;
		repaint();
	}
	
	//rysuj siatkę - włącz siatkę
	public void drawGrid(){
		sizer = grid;
		repaint();
	}
	
	//rysuje pełny obraz wczytany na początku
	public void drawOriginalImage(){
		tiff = tiffOriginal;
		repaint();
	}
	
	//rysuje wybrany fragment oryginalnego obrazu
	public void drawSubimage(BufferedImage image){
		tiff = image;
		//Color redColor = new Color (255,0,0);
		//int rgbForRed = redColor.getRGB();
		
		//tiff = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		//tiff.getGraphics().drawImage(image, 0, 0,null);
		

		
		
		resetSizerPosition();
		resetTiffPosition();
		repaint();
	}
	
	public void resetTiffPosition(){
		this.tiffXPosition = 0;
		this.tiffYPosition = 0;
		repaint();
	}
	
	public void resetSizerPosition(){
		this.sizerXPosition = 0;
		this.sizerYPosition = 0;
		repaint();
	}
	
	public BufferedImage getFirstTiffSubimage(int x, int y, int w, int h){
		return tiff.getSubimage(x, y, w, h);
	}
	
	public BufferedImage getSecondTiffSubimage(int x, int y, int w, int h){
		return secondColorTiff.getSubimage(x, y, w, h);
	}
	
	public int getTiffXPosition(){
		return tiffXPosition;
	}
	
	public int getTiffYPosition(){
		return tiffYPosition;
	}
	
	public int getSizerXPosition(){
		return sizerXPosition;
	}
	
	public int getSizerYPosition(){
		return sizerYPosition;
	}
	
	public int getSizerWidth(){
		return sizer.getWidth();
	}
	
	public int getSizerHeight(){
		return sizer.getHeight();
	}
	
	public int getTiffWidth(){
		return tiff.getWidth();
	}
	
	public int getTiffHeight(){
		return tiff.getHeight();
	}
	
	public void setTiffXPosition(int x){
		tiffXPosition = x;
	}
	
	public void setTiffYPosition(int y){
		tiffYPosition = y;
	}
	
	public void setSizerXPosition(int x){
		sizerXPosition = x;
	}
	
	public void setSizerYPosition(int y){
		sizerYPosition = y;
	}
	
	public void setFirstTiffFilename(String filename){
		this.firstTiffFilename = filename;
		setSecondTiffFilename(filename);
	}
	
	public void setSecondTiffFilename(String filename){
		if (Pattern.matches(".+Cy5.+", filename)){
			this.secondTiffFilename = filename.replaceAll("Cy5","Cy3");
		}else{
			this.secondTiffFilename = filename.replaceAll("Cy3","Cy5");
		}
	}
	
	public String getFirstTiffFilename(){
		return this.firstTiffFilename;
	}
	
	public String getSecondTiffFilename(){
		return this.secondTiffFilename;
	}
}

