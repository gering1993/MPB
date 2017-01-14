package cDNA_app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Grid extends JPanel {
	public Grid() {
		this.setPreferredSize(new Dimension(700,500));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(new Color(0,255,0));
		for(int i = 1; i <= 10; i++) {
			g2d.drawLine(40*i-1, 0, 40*i-1, 400);
			g2d.drawLine(40*i, 0, 40*i, 400);
			g2d.drawLine(40*i+1, 0, 40*i+1, 400);
			
			g2d.drawLine(0, 40*i-1, 400, 40*i-1);
			g2d.drawLine(0, 40*i, 400, 40*i);
			g2d.drawLine(0, 40*i+1, 400, 40*i+1);
		}
	}
}