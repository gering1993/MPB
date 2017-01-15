package cDNA_app;
import java.io.File;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.RenderedImage;
import javax.media.jai.widget.ScrollingImagePanel;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.prism.paint.ImagePattern;

public class ImagePanel extends JPanel {

  	private ScrollingImagePanel panel;
  	private RenderedImage op;

    public ImagePanel(String filename) throws IOException {
    	   	
    	File file = new File(filename);
        SeekableStream s = new FileSeekableStream(file);	        
        TIFFDecodeParam param = null;
        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
        System.out.println("Number of images in this TIFF: " +
                           dec.getNumPages());

        // Which of the multiple images in the TIFF file do we want to load
        // 0 refers to the first, 1 to the second and so on.
        int imageToLoad = 0;

        RenderedImage op = new NullOpImage(dec.decodeAsRenderedImage(imageToLoad),
				                            null,
				                            OpImage.OP_IO_BOUND,
				                            null);

        panel = new ScrollingImagePanel(op, 1200, 600);
        add(panel);
    }

}