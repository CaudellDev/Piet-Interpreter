/*
 * The MIT License
 *
 * Copyright 2016 Tyler.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.reddev112.piet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

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
    public void repaint() {
        setSize(500, 500);
        super.repaint(); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        if (program == null) return;
        
        Utility.drawPietProgram(program, g, codelWidth, showValue, outlineCurrent);
    }
}
