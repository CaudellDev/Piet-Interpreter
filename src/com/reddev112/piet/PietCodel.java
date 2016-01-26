package com.reddev112.piet;

public class PietCodel {
    
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
        this.color = Utility.getColor(color);
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
}
