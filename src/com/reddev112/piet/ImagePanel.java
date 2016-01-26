/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reddev112.piet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Tyler
 */
public class ImagePanel extends JPanel {
    private BufferedImage image;
    
    public ImagePanel() {
        image = null;
    }
    
    public void loadImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null);
            
//            int codelWidth = image.getWidth() / 6;
//            int codelHeight = image.getHeight() / 4;
//            
//            for (int y = 0; y < 3; y++) {
//                for (int x = 0; x < 6; x++) {
//                    String codelColor = Utility.HUES[x] + Utility.LIGHTNESS[y];
//                    g.setColor(new Color(Utility.getColor(new PietCodel(codelColor))));
//                    g.fillRect(x * codelWidth, y * codelHeight, codelWidth, codelHeight);
//                }
//            }
//            
//            g.setColor(new Color(Utility.getColor(new PietCodel(Utility.WHITE))));
//            g.fillRect(0, codelHeight * 3, codelWidth * 3, codelHeight);
//            
//            g.setColor(new Color(Utility.getColor(new PietCodel(Utility.BLACK))));
//            g.fillRect(codelWidth * 3, codelHeight * 3, codelWidth * 3, codelHeight);
        }
    }
    
    
}
