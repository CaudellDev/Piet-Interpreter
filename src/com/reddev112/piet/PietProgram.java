package com.reddev112.piet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PietProgram {
    
    // code is an array of columns. code.get() == col
    // Each column, is an array of rows. code.get().get() == row
    // 
    private ArrayList<ArrayList<PietCodel>> code;
    private Stack<Integer> stack;
    private boolean complete;
    
    private long stepCount;
    private int exitCount;
    private String direcPointer;
    private String codelChooser;
    private boolean rotDP; // This helps decide whether to rotate the DP or toggle the CC when needed.
    
    private PietCodel nextCodel;
    private PietCodel currCodel;
    private PietCodel prevCodel;
    
    private InIntCallback inIntCallback;
    private InStrCallback inStrCallback;
    private OutIntCallback outIntCallback;
    private OutStrCallback outStrCallback;
    
    
    static public final String[] DP = { "R", "D", "L", "U" };
    static public final String[] CC = { "l", "r" };
    
    public PietProgram() {
        init();
    }
    
    public PietProgram(BufferedImage image) {
        init();
        parseImage(image);
        
//        System.out.println("Code = {");
//        for (int y = 0; y < code.size(); y++) {
//            System.out.print("    {");
//            for (int x = 0; x < code.get(y).size(); x++) {
//                System.out.print(code.get(y).get(x));
//                
//                // All but the last one.
//                if (x < code.get(y).size() - 1)
//                    System.out.print(", ");
//            }
//            
//            System.out.print("}");
//            if (y < code.size() - 1) {
//                System.out.println(",");
//            } else {
//                System.out.println();
//            }
//        }
//        System.out.println("};");
        
        // Start codel;
        nextCodel = code.get(0).get(0);
        
    }
    
    public void reset() {
        ArrayList<ArrayList<PietCodel>> oldCode = code;
        for (int y = 0; y < oldCode.size(); y++) {
            for (int x = 0; x < oldCode.get(0).size(); x++) {
                PietCodel codel = oldCode.get(y).get(x);
                codel.setValue(PietCodel.DEFAULT);
            }
        }
        
        init();
        
        code = oldCode;
        nextCodel = get(0, 0);
    }
    
    private void init() {
        stack = new Stack<>();
        code = new ArrayList<>();
        currCodel = null;
        nextCodel = null;
        complete = false;
        
        stepCount = 0;
        exitCount = 0;
        direcPointer = Utility.DP[0];
        codelChooser = Utility.CC[0];
        rotDP = false;
    }
    
    public void run() {
        System.out.println("Running Piet Program...");
        while (!complete) {
            step();
        }
    }
    
    public void step() {
        //System.out.println("Start of step() function...");
        
        stepCount++;
        
        // Initial run...
        if (currCodel == null) {
            currCodel = nextCodel;
            nextCodel = setNextCodel();
        }
        
        //System.out.println("currCodel and nextCodel: " + currCodel + ", " + nextCodel);
        
        if (complete) {
            // Terminate program...
            System.out.println("The Piet Program is complete.");
            return;
        }
        
        int hueChange = 0;
        int lightChange = 0;
        if (currCodel.equals(Utility.WHITE) || nextCodel.equals(Utility.WHITE)) {
            nextCodel = setNextCodel();
        } else {
            hueChange = Utility.getHueChange(currCodel, nextCodel);
            lightChange = Utility.getLightnessChange(currCodel, nextCodel);
        }
        
//        System.out.println("Hue change and lightness change: " + hueChange + ", " + lightChange);
//        System.out.println("Curr codel, next codel: " + currCodel + ", " + nextCodel);
        
        try {
            if (hueChange == 0) {
                if (lightChange == 0) {
                    // Do nothing.
                } else if (lightChange == 1) {
                    push();
                } else if (lightChange == 2) {
                    pop();
                } 
            } else if (hueChange == 1) {
                if (lightChange == 0) {
                    add();
                } else if (lightChange == 1) {
                    sub();
                } else if (lightChange == 2) {
                    multi();
                } 
            } else if (hueChange == 2) {
                if (lightChange == 0) {
                    divide();
                } else if (lightChange == 1) {
                    mod();
                } else if (lightChange == 2) {
                    not();
                } 
            } else if (hueChange == 3) {
                if (lightChange == 0) {
                    greater();
                } else if (lightChange == 1) {
                    pointer();
                } else if (lightChange == 2) {
                    pSwitch();
                } 
            } else if (hueChange == 4) {
                if (lightChange == 0) {
                    duplicate();
                } else if (lightChange == 1) {
                    roll();
                } else if (lightChange == 2) {
                    inInt();
                } 
            } else if (hueChange == 5) {
                if (lightChange == 0) {
                    inStr();
                } else if (lightChange == 1) {
                    outInt();
                } else if (lightChange == 2) {
                    outStr();
                } 
            } else {
                // This shouldn't happen....
            }
        } catch (EmptyStackException e) {
//            System.out.println("Stack is empty.");
        }
        
//        stack.add(0);
//        System.out.println("Stack: " + stack);
        
        performMove();
    }
    
    public boolean isComplete() {
        return complete;
    }
    
    public int getColCount() {
        return code.get(0).size();
    }
    
    public int getRowCount() {
        return code.size();
    }
    
    public PietCodel getCurrentCodel() {
        return currCodel;
    }
    
    private void parseImage(BufferedImage image) {
        int smallestWidth = -1;
        int count = 1;
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() - 1; x++) {
                int firstPix = image.getRGB(x, y);
                int secondPix = image.getRGB(x + 1, y);
                
                if (firstPix == secondPix) {
                    count++;
                } else {
                    if (smallestWidth == -1 || count < smallestWidth) {
                        smallestWidth = count;
                        if (smallestWidth == 1) {
                            break;
                        }
                    }
                    count = 1;
                }
            }
            if (smallestWidth == 1) {
                break;
            }
        }
        
//        int horzCodels = image.getWidth() / smallestWidth;
//        int vertCodels = image.getHeight() / smallestWidth;
//        
//        for (int y = 0; y < vertCodels; y++) {
//            ArrayList<PietCodel> codels = new ArrayList<>();
//            for (int x = 0; x < horzCodels; x++) {
//                int color = image.getRGB(x * smallestWidth+horzCodels, y * smallestWidth+vertCodels);
//                PietCodel codel = new PietCodel(color);
//                codels.add(codel);
//            }
//            System.out.println(codels);
//            code.add(codels);
//        }
        
        for (int y = 0; y < image.getHeight(); y+= smallestWidth) {
            ArrayList<PietCodel> codels = new ArrayList<>();
            for (int x = 0; x < image.getWidth(); x+= smallestWidth) {
                int color = image.getRGB(x, y);
                PietCodel codel = new PietCodel(x / smallestWidth, y / smallestWidth, color);
                codels.add(codel);
            }
            code.add(codels);
        }
        
    }
    
    public PietCodel get(int colIndex, int rowIndex) {
        PietCodel result = null;
        if (colIndex >= 0 && rowIndex >= 0) {
            if (rowIndex < code.size() && colIndex < code.get(rowIndex).size()) {
                ArrayList<PietCodel> col = code.get(rowIndex);
                result = col.get(colIndex);
            }
        }
        
        return result;
    }
    
    private int getBlockCount(PietCodel codel) {
        
//        if (codel != null) {
//            System.out.println("getBlockCount codel.value(): " + codel.getValue());
//        } else {
//            System.out.println("getBlockCount codel is null.");
//        }
        
        if (codel.getValue() != PietCodel.DEFAULT && codel.getValue() != PietCodel.CHECKED) {
            return codel.getValue();
        }
        
        ArrayList<PietCodel> list = blockCountHelper(codel);
        list.add(codel);
        
        // Use the array of codels in the block, and
        // use the size to for each value in the array.
        int result = list.size();
        for (PietCodel item : list) item.setValue(result);
        
//        System.out.println("Block count: " + result);
        
        return result;
    }
    
    private ArrayList<PietCodel> blockCountHelper3(PietCodel codel) {
        ArrayList<PietCodel> result = new ArrayList<>();
        codel.setValue(PietCodel.CHECKED);
        int col = codel.getCol();
        int row = codel.getRow();
        
        // Right
        PietCodel ajac = get(col + 1, row);
        if (ajac != null && codel.equals(ajac.getColor()) && ajac.getValue() == PietCodel.DEFAULT) {
//            System.out.println("blockCountHelper going right...");
            ArrayList<PietCodel> nextCodels = blockCountHelper3(ajac);
            result.add(ajac);
            result.addAll(nextCodels);
        }
        
        // Down
        ajac = get(col, row + 1);
        if (ajac != null && codel.equals(ajac.getColor()) && ajac.getValue() == PietCodel.DEFAULT) {
//            System.out.println("blockCountHelper going down...");
            ArrayList<PietCodel> nextCodels = blockCountHelper3(ajac);
            result.add(ajac);
            result.addAll(nextCodels);
        }
        
        // Left
        ajac = get(col - 1, row);
        if (ajac != null && codel.equals(ajac.getColor()) && ajac.getValue() == PietCodel.DEFAULT) {
//            System.out.println("blockCountHelper going left...");
            ArrayList<PietCodel> nextCodels = blockCountHelper3(ajac);
            result.add(ajac);
            result.addAll(nextCodels);
        }
        
        // Up
        ajac = get(col, row - 1);
        if (ajac != null && codel.equals(ajac.getColor()) && ajac.getValue() == PietCodel.DEFAULT) {
//            System.out.println("blockCountHelper going up...");
            ArrayList<PietCodel> nextCodels = blockCountHelper3(ajac);
            result.add(ajac);
            result.addAll(nextCodels);
        }
        
//        System.out.println("blockCountHelper finished...");
        return result;
    }
    
    private ArrayList<PietCodel> blockCountHelper(PietCodel codel) {
    ArrayList<PietCodel> accumulator = new ArrayList<>();
    LinkedList<PietCodel> queue = new LinkedList<>();
    
    int col = codel.getCol();
    int row = codel.getRow();
    queue.add(codel);

    while (!queue.isEmpty()) {
            PietCodel ajac = queue.remove();
            if (ajac != null && codel.equals(ajac.getColor()) && ajac.getValue() != PietCodel.CHECKED ) {
                accumulator.add(ajac);
                ajac.setValue(PietCodel.CHECKED);
                col = ajac.getCol();
                row = ajac.getRow();
            }
            
            if ( get(col + 1, row) != null && get(col + 1, row).getValue() != PietCodel.CHECKED && !queue.contains(get(col + 1, row)) ) { queue.addFirst(get(col + 1, row)); }
            if ( get(col , row + 1) != null && get(col, row + 1).getValue() != PietCodel.CHECKED && !queue.contains(get(col, row + 1)) ) { queue.addFirst(get(col, row + 1)); }
            if ( get(col - 1, row) != null && get(col - 1, row).getValue() != PietCodel.CHECKED && !queue.contains(get(col - 1, row)) ) { queue.addFirst(get(col - 1, row)); }
            if ( get(col , row - 1) != null && get(col, row - 1).getValue() != PietCodel.CHECKED && !queue.contains(get(col, row - 1)) ) { queue.addFirst(get(col, row- 1)); }
            
            System.out.println("Accumilator: " + accumulator);
            System.out.println("Queue: " + queue);
            System.out.println("Adjacent: " + ajac + "\n");
            
    }
    
    return accumulator;
}
    
    private PietCodel[] getCodelBlockCorners(PietCodel codel) {
        ArrayList<PietCodel> border = blockCornerHelper(null, codel);
//        System.out.println("Border: " + border);
        
        int maxRight = -1;
        int maxDown = -1;
        int maxLeft = -1;
        int maxUp = -1;
        
        for (int i = 0; i < border.size(); i++) {
            PietCodel check = border.get(i);
            
            // Right...
            if (maxRight == -1 || check.getCol() > maxRight) {
                maxRight = check.getCol();
            }
            
            if (maxDown == -1 || check.getRow() > maxDown) {
                maxDown = check.getRow();
            }
            
            if (maxLeft == -1 || check.getCol() < maxLeft) {
                maxLeft = check.getCol();
            }
            
            if (maxUp == -1 || check.getRow() < maxUp) {
                maxUp = check.getRow();
            }
        }
        
//        System.out.println("Maxes --> Right: " + maxRight + ", Down: " + maxDown + ", Left: " + maxLeft + ", Up: " + maxUp);
        
        // Indexes --> Right: 0, 1 | Down: 2, 3 | Left: 4, 5 | Up: 6, 7
        ArrayList<PietCodel> resultList = new ArrayList<>();
        for (int i = 0; i < 8; i++) resultList.add(null);
        
        // Iterates through the border. It
        for (int i = 0; i < border.size(); i++) {
            PietCodel check = border.get(i);
            
            // Right - It needs to be the furthest edge to the right.
            PietCodel leftmost = resultList.get(0);
            PietCodel rightmost = resultList.get(1);
            if (check.getCol() == maxRight) {
                if ((leftmost == null) || (check.getRow() <= leftmost.getRow())) resultList.set(0, check);
                if (rightmost == null || check.getRow() >= rightmost.getRow()) resultList.set(1, check);
            }
            
            // Down
            leftmost = resultList.get(2);
            rightmost = resultList.get(3);
            if (check.getRow() == maxDown) {
                if (leftmost == null || check.getCol() >= leftmost.getCol()) resultList.set(2, check);
                if (rightmost == null || check.getCol() <= rightmost.getCol()) resultList.set(3, check);
            }
            
            // Left
            leftmost = resultList.get(4);
            rightmost = resultList.get(5);
            if (check.getCol() == maxLeft) {
                if (leftmost == null || check.getRow() >= leftmost.getRow()) resultList.set(4, check);
                if (rightmost == null || check.getRow() <= rightmost.getRow()) resultList.set(5, check);
            }
            
            // Up
            leftmost = resultList.get(6);
            rightmost = resultList.get(7);
            if (check.getRow() == maxUp) {
                if (leftmost == null || check.getCol() <= leftmost.getCol()) resultList.set(6, check);
                if (rightmost == null || check.getCol() >= rightmost.getCol()) resultList.set(7, check);
            }
        }
        
//        System.out.println("Corner array: " + resultList);
        
        PietCodel[] result = new PietCodel[8];
        return resultList.toArray(result);
    }
    
    private ArrayList<PietCodel> blockCornerHelper(ArrayList<PietCodel> border, PietCodel curr) {
        
        // Initial run...
        if (border == null) border = new ArrayList<>();
        border.add(curr);
        
        int col = curr.getCol();
        int row = curr.getRow();
        
        // Right
        PietCodel right = get(col + 1, row);
//        System.out.println("Curr: " + curr);
//        System.out.println("Border: " + border);
//        System.out.println("Right codel: " + right);
//        System.out.println(
//                  "    right != null = " + (right != null) + ",\n"
//                + "    curr.equals(right.getColor()) = " + curr.equals(right.getColor()) + ",\n"
//                + "    !isCodelSurrounded(right = " + !isCodelSurrounded(right) + ",\n"
//                + "    !border.contains(right) = " + !border.contains(right));
        
        if (right != null && curr.equals(right.getColor()) && !isCodelSurrounded(right) && !border.contains(right)) {
//            System.out.println("blockCornerHelper going right...");
            blockCornerHelper(border, right);
        }
        
        // Down
        PietCodel down = get(col, row + 1);
        if (down != null && curr.equals(down.getColor()) && !isCodelSurrounded(down) && !border.contains(down)) {
//            System.out.println("blockCornerHelper going down...");
            blockCornerHelper(border, down);
        }
        
        // Left
        PietCodel left = get(col - 1, row);
        if (left != null && curr.equals(left.getColor()) && !isCodelSurrounded(left) && !border.contains(left)) {
//            System.out.println("blockCornerHelper going left...");
            blockCornerHelper(border, left);
        }
        
        // Up
        PietCodel up = get(col, row - 1);
        if (up != null && curr.equals(up.getColor()) && !isCodelSurrounded(up) && !border.contains(up)) {
//            System.out.println("blockCornerHelper going up...");
            blockCornerHelper(border, up);
        }
        
//        System.out.println("Ajacent codels: {\n"
//                + "    Right: " + right + ",\n"
//                + "     Down: " + down + ",\n"
//                + "     Left: " + left + ",\n"
//                + "       Up: " + up + ",\n"
//                + "}");
        
//        System.out.println("Border: " + border);
//        System.out.println("blockCornerHelper finished...");
        return border;
    }
    
    private boolean isCodelSurrounded(PietCodel codel) {
        int col = codel.getCol();
        int row = codel.getRow();
        
        // Right...
        PietCodel right = get(col + 1, row);
        if (right == null || !codel.equals(right.getColor())) return false;
        
        // Down...
        PietCodel down = get(col, row + 1);
        if (down == null || !codel.equals(down.getColor())) return false;
        
        // Left...
        PietCodel left = get(col - 1, row);
        if (left == null || !codel.equals(left.getColor())) return false;
        
        // Up...
        PietCodel up = get(col, row - 1);
        if (up == null || !codel.equals(up.getColor())) return false;
        
        // Up Right...
        PietCodel upRight = get(col + 1, row - 1);
        if (upRight == null || !codel.equals(upRight.getColor())) return false;
        
        // Down Right...
        PietCodel downRight = get(col + 1, row + 1);
        if (downRight == null || !codel.equals(downRight.getColor())) return false;
        
        // Up Left...
        PietCodel upLeft = get(col - 1, row - 1);
        if (upLeft == null || !codel.equals(upLeft.getColor())) return false;
        
        // Down Left...
        PietCodel downLeft = get(col - 1, row + 1);
        if (downLeft == null || !codel.equals(downLeft.getColor())) return false;
        
        return true;
    }
    
    private void performMove() {
        PietCodel newNextCodel = setNextCodel();
        if (newNextCodel != null) {
            currCodel = nextCodel;
            nextCodel = newNextCodel;
        }
    }
    
    /**
     * Sets the nextCodel to the next movement. Currently only goes directly right. Movement will be implemented soon.
     * 
     * How this function will work, is it won't worry about any logic. It will just return the codel in the
     * direction of the Direction Pointer and Codel chooser (in codel blocks). It will return the codel
     * (null, if out of bounds). The step function 
     * 
     * Should this function do the movement logic, until if finds a valid codel?
     * 
     * @return Returns true if there was a codel next.
     */
    private PietCodel setNextCodel() {
        return setNextCodel(currCodel, direcPointer, codelChooser);
    }
    
    private PietCodel setNextCodel(PietCodel current, String dp, String cc) {
        PietCodel result = null;
        
        PietCodel[] corners = getCodelBlockCorners(current);
        
        
//        System.out.println("Start the while loop.");
        
        // Keep trying to find the next codel. After 8 tries, there are no more options.
        rotDP = false;
        exitCount = 0;
        while (true) {

//            System.out.println("dp, cc: " + direcPointer + ", " + codelChooser);

            PietCodel newCurrCodel = new PietCodel();

            if (direcPointer.equals(Utility.DP[0])) {
                if (codelChooser.equals(Utility.CC[0])) {
                    newCurrCodel = corners[0];
                } else if (codelChooser.equals(Utility.CC[1])) {
                    newCurrCodel = corners[1];
                }
            } else if (direcPointer.equals(Utility.DP[1])) {
                if (codelChooser.equals(Utility.CC[0])) {
                    newCurrCodel = corners[2];
                } else if (codelChooser.equals(Utility.CC[1])) {
                    newCurrCodel = corners[3];
                }
            } else if (direcPointer.equals(Utility.DP[2])) {
                if (codelChooser.equals(Utility.CC[0])) {
                    newCurrCodel = corners[4];
                } else if (codelChooser.equals(Utility.CC[1])) {
                    newCurrCodel = corners[5];
                }
            } else if (direcPointer.equals(Utility.DP[3])) {
                if (codelChooser.equals(Utility.CC[0])) {
                    newCurrCodel = corners[6];
                } else if (codelChooser.equals(Utility.CC[1])) {
                    newCurrCodel = corners[7];
                }
            }

            int col = newCurrCodel.getCol();
            int row = newCurrCodel.getRow();
        
        
            if (direcPointer.equals(Utility.DP[0])) {
                result = get(col + 1, row);
            } else if (direcPointer.equals(Utility.DP[1])) {
                result = get(col, row + 1);
            } else if (direcPointer.equals(Utility.DP[2])) {
                result = get(col - 1, row);
            } else if (direcPointer.equals(Utility.DP[3])) {
                result = get(col, row - 1);
            }
            
//            System.out.println("Result codel: " + result);
            
            if (result == null || result.equals(Utility.BLACK)) {
//                System.out.println("Hit a black codel or edge. Adjusting direction....");
                if (rotDP) {
//                    System.out.print("Rotating DP: " + direcPointer);
                    direcPointer = Utility.turnDirecPointer(direcPointer, 1);
//                    System.out.println(" --> " + direcPointer);
                    rotDP = false;
                } else {
//                    System.out.print("Toggling CC: " + codelChooser);
                    codelChooser = Utility.toggleCodelChooser(codelChooser, 1);
//                    System.out.println(" --> " + codelChooser);
                    rotDP = true;
                }
                
                exitCount++;
            } else {
//                System.out.println("Did not hit a black codel. Continue program execution....");
                break;
            }
        
            if (exitCount == 8) {
//                System.out.println("No valid next codel. Must terminate program.");
                complete = true;
                return null;
            }
        }
        
        return result;
    }
    
    private void push() {
        int value = getBlockCount(currCodel);
        //System.out.println("getBlockCount value: " + value);
        stack.push(value);
    }
    
    private void pop() {
        if (!stack.isEmpty()) stack.pop();
    }
    
    private void add() {
        int first = stack.pop();
        int second = stack.pop();
        stack.push(second + first); // Order doesn't matter, but for consistancy...
    }
    
    private void sub() {
        int first = stack.pop();
        int second = stack.pop();
        stack.push(second - first);
    }
    
    private void multi() {
        int first = stack.pop();
        int second = stack.pop();
        stack.push(first * second);
    }
    
    private void divide() {
        int first = stack.pop();
        int second = stack.pop();
        stack.push(second / first);
    }
    
    private void mod() {
        int first = stack.pop();
        int second = stack.pop();
        stack.push(second % first);
    }
    
    private void not() {
        int first = stack.pop();
        stack.push((first == 0) ? 1 : 0);
    }
    
    private void greater() {
        int first = stack.pop();
        int second = stack.pop();
        stack.push((second > first) ? 1 : 0);
    }
    
    private void pointer() {
        direcPointer = Utility.turnDirecPointer(direcPointer, stack.pop());
    }
    
    private void pSwitch() {
        codelChooser = Utility.toggleCodelChooser(codelChooser, stack.pop());
    }
    
    private void duplicate() {
        int value = stack.pop();
        stack.push(value);
        stack.push(value);
    }
    
    private void roll() {
        int roll = stack.pop();
        int depth = stack.pop();
        
        List<Integer> subArray = stack.subList(stack.size() - depth, stack.size() - 1);
        
        for (int i = 0; i < roll; i++) {
            int element = subArray.remove(subArray.size() - 1);
            subArray.add(0, element);
        }
        
        int index = 0;
        for (int i = stack.size() - depth; i < stack.size() - 1; i++) {
            stack.set(i, subArray.get(index));
            index++;
        }
    }
    
    private void inInt() {
        if (inIntCallback == null) {
            Scanner reader = new Scanner(System.in);
            int input = reader.nextInt();
            stack.push(input);
            reader.close();
        } else {
            int value = inIntCallback.onInInt();
            stack.push(value);
        }
    }
    
    private void inStr() {
        if (inStrCallback == null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                int input = br.read();
                stack.add(input);
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(PietProgram.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
            }
        } else {
            
        }
    }
    
    private void outInt() {
        if (outIntCallback == null) {
            System.out.print(stack.pop());
        } else {
            outIntCallback.onOutInt(stack.pop());
        }
    }
    
    private void outStr() {
        int character = stack.pop();
        String output = Character.toString((char) character);
        
        if (outStrCallback == null) {
            System.out.print(output);
        } else {
            outStrCallback.onOutStr(output);
        }
    }
    
    public void setInIntCallback(InIntCallback callback) {
        inIntCallback = callback;
    }
    
    public void setInStrCallback(InStrCallback callback) {
        inStrCallback = callback;
    }
    
    public void setOutIntCallback(OutIntCallback callback) {
        outIntCallback = callback;
    }
    
    public void setOutStrCallback(OutStrCallback callback) {
        outStrCallback = callback;
    }
    
    public interface InIntCallback {
        int onInInt();
    }
    
    public interface InStrCallback {
        String onInStr();
    }
    
    public interface OutIntCallback {
        void onOutInt(int value);
    }
    
    public interface OutStrCallback {
        void onOutStr(String value);
    }
    
    public interface InfoOutput {
        void onOutput(String output);
    }
    
    // ############ ----- Utility ----- #############
    
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
    
    static public int getHueChange(PietCodel first, PietCodel second) {
        return getDifference(PietCodel.getHue(first), PietCodel.getHue(second), PietCodel.HUES);
    }
    
    static public int getLightnessChange(PietCodel first, PietCodel second) {
        return getDifference(PietCodel.getLightness(first), PietCodel.getLightness(second), PietCodel.LIGHTNESS);
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
}

