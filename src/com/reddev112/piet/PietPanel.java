/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reddev112.piet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Tyler
 */
public class PietPanel extends JPanel {
    public static final int DEFAULT_CODEL_WIDTH = 50;
    private PietProgram program;
    
    private int codelWidth = DEFAULT_CODEL_WIDTH;
    private boolean showValue = false;
    private boolean outlineCurrent = true;
    
    public PietPanel() {
        program = null;
    }
    
    public PietPanel(PietProgram program) {
        this.program = program;
    }
    
    public void setCodelWidth(int codelWidth) {
        this.codelWidth = codelWidth;
    }
    
    public int getCodelWidth() {
        return codelWidth;
    }
    
    public void showValue(boolean showValue) {
        this.showValue = showValue;
    }
    
    public boolean showValue() {
        return showValue;
    }
    
    public void outlineCurrent(boolean outlineCurrent) {
        this.outlineCurrent = outlineCurrent;
    }
    
    public boolean outlineCurrent() {
        return outlineCurrent;
    }
    
    public void loadPietProgram(PietProgram program) {
        this.program = program;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (program == null) return;
        
        int width = program.getColCount() * codelWidth;
        int height = program.getRowCount() * codelWidth;
        
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, width, height);
        
        for (int y = 0; y < program.getRowCount(); y++) {
            for (int x = 0; x < program.getColCount(); x++) {
                PietCodel codel = program.get(x, y);
                if (codel.getColor().equals(Utility.WHITE)) continue;
                
                int color = Utility.getColor(codel);
                
                g.setColor(new Color(color));
                g.fillRect(x * codelWidth, y * codelWidth, codelWidth, codelWidth);
                
                if (showValue) {
                    g.setColor(Color.GRAY);
                    g.setFont(new Font(null, Font.PLAIN, 16));
                    if (codel != null) g.drawString(codel.getValue() + "", x * codelWidth + (codelWidth / 2), y * codelWidth + (codelWidth / 2));
                }
            }
        }
        
        PietCodel current = program.getCurrentCodel();
        
        if (current != null && outlineCurrent) {
            int currX = current.getCol() * codelWidth;
            int currY = current.getRow() * codelWidth;

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(5));
            g2.setColor(Color.GRAY);
            g2.drawRect(currX, currY, codelWidth, codelWidth);
        }
    }
}
