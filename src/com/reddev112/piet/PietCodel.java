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

public class PietCodel {
    
    static public final String BLACK = "BLACK";
    static public final String WHITE = "WHITE";
    
    static private final int[][] DEF_PALLETE = {
        { 0xFFFFC0C0, 0xFFFF0000, 0xFFC00000 }, // Red
        { 0xFFFFFFC0, 0xFFFFFF00, 0xFFC0C000 }, // Yellow
        { 0xFFC0FFC0, 0xFF00FF00, 0xFF00C000 }, // Green
        { 0xFFC0FFFF, 0xFF00FFFF, 0xFF00C0C0 }, // Cyan
        { 0xFFC0C0FF, 0xFF0000FF, 0xFF0000C0 }, // Blue
        { 0xFFFFC0FF, 0xFFFF00FF, 0xFFC000C0 }, // Magenta
        { 0xFFFFFF, 0x000000 } // White and black
    };
    
    static public final String[] HUES = { "R", "Y", "G", "C", "B", "M" };
    static public final String[] LIGHTNESS = { "l", "n", "d" };
    static public final int DEFAULT = -1;
    static public final int CHECKED = 0;
    
    private String color;
    private int value;
    private int col;
    private int row;
    
    public PietCodel() {
        value = DEFAULT;
    }
    
    public PietCodel(int x, int y) {
        this.col = x;
        this.row = y;
        value = DEFAULT;
    }
    
    public PietCodel(String color) {
        this.color = color;
        value = DEFAULT;
    }
    
    public PietCodel(int color) {
        this.color = getColor(color);
        value = DEFAULT;
    }
    
    public PietCodel(int x, int y, String color) {
        this.col = x;
        this.row = y;
        this.color = color;
        value = DEFAULT;
    }
    
    public PietCodel(int x, int y, int color) {
        this.col = x;
        this.row = y;
        this.color = Utility.getColor(color);
        value = DEFAULT;
    }
    
    // ###### ----- Methods ----- ######
    
    public int getValue() {
        return value;
    }
    
    public void incrValue(int incr) {
        value += incr;
    }
    
    public void setValue(int value) {
//        System.out.println(this + ": value " + this.value + " -> " + value);
        this.value = value;
    }
    
    public String getColor() {
        return color;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getRow() {
        return row;
    }
    
    /**
     * This equals is a little unconventional in how it works. If you use a PietCodel, it compares the color and coordinates. Since colors are stored as Strings, you can use a String to compare just the colors, and ignore the coordinates.
     * @param obj Can be a PietCodel or a String. PietCodel compares color and coordinates. String compares just the color (because colors are stored as Strings), to ignore the coordinates.
     * @return Returns true if the color and/or (based on whether a PietCodel or String was used) coordinates are the same. Returns false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        
        if (obj instanceof PietCodel) {
            PietCodel codel = (PietCodel) obj;
            if (!getColor().equals(codel.getColor())) return false;
            if (getCol() != codel.getCol() || getRow() != codel.getRow()) return false;
            return true;
        } else if (obj instanceof String) {
            return getColor().equals(obj);
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return getColor() + ", " + value + " : ( " + getCol() + ", " + getRow() + " )";
    }
    
    // ####### ----- Utility ----- #####
    
    static public String getColor(int color) {
        if (color == DEF_PALLETE[6][0]) return WHITE;
        if (color == DEF_PALLETE[6][1]) return BLACK;
        
        int hueIndex = 0;
        int lightIndex = 0;
        
        for (int y = 0; y < DEF_PALLETE[0].length; y++) {
            for (int x = 0; x < DEF_PALLETE.length - 1; x++) {
                if (color == DEF_PALLETE[x][y]) {
                    hueIndex = x;
                    lightIndex = y;
                }
            }
        }
        
        return HUES[hueIndex] + LIGHTNESS[lightIndex];
    }
    
    static public int getColor(PietCodel codel) {
        if (codel == null) {
            return DEF_PALLETE[6][0];
        }
        
        if (codel.equals(WHITE)) return DEF_PALLETE[6][0];
        if (codel.equals(BLACK)) return DEF_PALLETE[6][1];
        
        String hue = getHue(codel);
        String lightness = getLightness(codel);
        int hueVal = -1;
        int lightVal = -1;
        
        for (int i = 0; i < 6; i++) {
            if (hue.equals(HUES[i])) hueVal = i;
            if (i < 3 && lightness.equals(LIGHTNESS[i])) lightVal = i;
        }
        
        if (hueVal == -1 || lightVal == -1) {
            return DEF_PALLETE[6][0];
        }
        
        return DEF_PALLETE[hueVal][lightVal];
    }
    
    static public String getHue(PietCodel codel) {
        return (codel.getColor()).substring(0, 1); // Get the first character.
    }
    
    static public String getLightness(PietCodel codel) {
        return (codel.getColor()).substring(1); // Get the second character.
    }
}
