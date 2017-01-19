package cDNA_app;
import java.io.File;
import java.io.IOException;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
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

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.prism.paint.ImagePattern;

public class ImagePanel extends JPanel {

  	private RenderedImage op;
  	
  	BufferedImage sizer;
  	BufferedImage tiff;
  	
  	private int tiffXPosition;
  	private int tiffYPosition;
  	private int sizerXPosition;
  	private int sizerYPosition;

    public ImagePanel(String filename) throws IOException {
    	tiffXPosition=0;
    	tiffYPosition=0;
    	sizerXPosition=0;
    	sizerYPosition=0;
    	sizer = ImageIO.read(new File("lib_files/ramka2.png"));
		FileSeekableStream stream = new FileSeekableStream(filename);
		TIFFDecodeParam decodeParam = new TIFFDecodeParam();
		decodeParam.setDecodePaletteAsShorts(true);
		ParameterBlock params = new ParameterBlock();
		params.add(stream);
		RenderedOp image1 = JAI.create("tiff", params);
		tiff = image1.getAsBufferedImage();
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
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			this.tiffXPosition+=speed;
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP){
			this.tiffYPosition-=speed;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			this.tiffYPosition+=speed;
		}
		repaint();
	}

	public void moveSizer(KeyEvent e, int speed) {
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			this.sizerXPosition-=speed;
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			this.sizerXPosition+=speed;
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP){
			this.sizerYPosition-=speed;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			this.sizerYPosition+=speed;
		}
		repaint();
	}
}

