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

/**
 *
 * @author Tyler
 */
public class Utility {
    
    static public final String[] HUES = { "R", "Y", "G", "C", "B", "M" };
    static public final String[] LIGHTNESS = { "l", "n", "d" };
    static public final String[] DP = { "R", "D", "L", "U" };
    static public final String[] CC = { "l", "r" };
    static public final String BLACK = "BLACK";
    static public final String WHITE = "WHITE";
    
    static private final int[][] PALLETE = {
        { 0xFFFFC0C0, 0xFFFF0000, 0xFFC00000 }, // Red
        { 0xFFFFFFC0, 0xFFFFFF00, 0xFFC0C000 }, // Yellow
        { 0xFFC0FFC0, 0xFF00FF00, 0xFF00C000 }, // Green
        { 0xFFC0FFFF, 0xFF00FFFF, 0xFF00C0C0 }, // Cyan
        { 0xFFC0C0FF, 0xFF0000FF, 0xFF0000C0 }, // Blue
        { 0xFFFFC0FF, 0xFFFF00FF, 0xFFC000C0 }, // Magenta
        { 0xFFFFFFFF, 0xFF000000 } // White and black
    };
    
    static public String turnDirecPointer(String currDC, int turns) {
        int currValue = 0;
        
        // Match up the direction String, with a number value.
        for (int i = 0; i < 4; i++) if (currDC.equals(DP[i])) currValue = i;
        
        // Loop around
        int newValue = turns + currValue;
        newValue %= 4;
        
        return DP[newValue];
    }
    
    static public String toggleCodelChooser(String currCC, int toggle) {
        int newValue;
        
        if (currCC.equals(CC[0]))
            newValue = toggle % 2;
        else
            newValue = (toggle + 1) % 2;
        
        return CC[newValue];
    }
    
    static public int getColor(PietCodel codel) {
        if (codel == null) {
            return PALLETE[6][0];
        }
        
        if (codel.equals(WHITE)) return PALLETE[6][0];
        if (codel.equals(BLACK)) return PALLETE[6][1];
        
        String hue = getHue(codel);
        String lightness = getLightness(codel);
        int hueVal = -1;
        int lightVal = -1;
        
        for (int i = 0; i < 6; i++) {
            if (hue.equals(HUES[i])) hueVal = i;
            if (i < 3 && lightness.equals(LIGHTNESS[i])) lightVal = i;
        }
        
        if (hueVal == -1 || lightVal == -1) {
            return PALLETE[6][0];
        }
        
        return PALLETE[hueVal][lightVal];
    }
    
    static public String getColor(int color) {
        if (color == PALLETE[6][0]) return WHITE;
        if (color == PALLETE[6][1]) return BLACK;
        
        int hueIndex = 0;
        int lightIndex = 0;
        
        for (int y = 0; y < PALLETE[0].length; y++) {
            for (int x = 0; x < PALLETE.length - 1; x++) {
                if (color == PALLETE[x][y]) {
                    hueIndex = x;
                    lightIndex = y;
                }
            }
        }
        
        return HUES[hueIndex] + LIGHTNESS[lightIndex];
    }
    
    static public int getHueChange(PietCodel first, PietCodel second) {
        return getDifference(getHue(first), getHue(second), HUES);
    }
    
    static public int getLightnessChange(PietCodel first, PietCodel second) {
        return getDifference(getLightness(first), getLightness(second), LIGHTNESS);
    }
    
    static private int getDifference(String first, String second, String[] pallete) {
        int firstIndex = 0;
        int secondIndex = 0;
        
        // Use the String arrays to match a number value to the hue position.
        for (int i = 0; i < pallete.length; i++) {
            if (first.equals(pallete[i])) firstIndex = i;
            if (second.equals(pallete[i])) secondIndex = i;
        }
        
        // Ensures that the values loop around.
        if (secondIndex < firstIndex) {
            secondIndex += pallete.length;
        }
        
        return secondIndex - firstIndex;
    }
    
    static private String getHue(PietCodel codel) {
        return (codel.getColor()).substring(0, 1); // Get the first character.
    }
    
    static private String getLightness(PietCodel codel) {
        return (codel.getColor()).substring(1); // Get the second character.
    }
    
    static public Graphics drawPietProgram(PietProgram program, Graphics g, int codelWidth, boolean showValue, boolean outlineCurrent) {
        int width = program.getColCount() * codelWidth;
        int height = program.getRowCount() * codelWidth;
        
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, width, height);
        
        for (int y = 0; y < program.getRowCount(); y++) {
            for (int x = 0; x < program.getColCount(); x++) {
                PietCodel codel = program.get(x, y);
//                if (codel.getColor().equals(Utility.WHITE)) continue;
                
                int color = Utility.getColor(codel);
//                System.out.println("Color, white: " + Integer.toHexString((new Color(color)).getRGB()) + ", " + Integer.toHexString(Color.WHITE.getRGB()));
                
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
        
        return g;
    }
}
