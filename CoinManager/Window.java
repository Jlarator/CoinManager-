/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.packages;

import java.io.File;
import javax.swing.JFrame; 
import java.awt.Color; 
import java.awt.Toolkit; 
import java.awt.Dimension; 
import java.awt.Font;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.awt.event.MouseListener; 
import java.awt.event.MouseAdapter; 
import java.awt.event.ComponentListener; 
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.JFrame; 
import javax.swing.JButton; 
import javax.swing.JPanel;
import javax.swing.JLabel; 
import javax.swing.Box; 
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.border.Border;

/**
 * Used to hold general layout and style attributes, such as fonts, colors, 
 * and layouts. In general, this provides common functionality and variable access 
 * to all classes in the project. 
 * @author jlarator
 */
public class Window extends JFrame implements Runnable, ActionListener, ComponentListener{
    
    public PyCaller pyCaller; 
    
    private final int FrameWidth = 1200;
    private final int FrameHeight = 1600;
    private final String currentDir = System.getProperty("user.dir"); 
    
    //Fonts
    protected final Font DejaVu = new Font("DejaVu Sans Mono", Font.PLAIN, 20);
    protected final Font mediumDejaVu = new Font("DejaVu Sans Mono", Font.PLAIN, 17);
    protected final Font smallDejaVu = new Font("DejaVu Sans Mono", Font.PLAIN, 15);
    protected final Font lato = new Font("Lato", Font.PLAIN, 15); 
    protected final Font Mono = new Font("Monospaced", Font.PLAIN, 15); 
    protected final Font smallMono = new Font("Monospaced", Font.PLAIN, 10); 
    
    //Colors
    protected final Color LightBlue = new Color(129,169,208);
    protected final Color LightGreen = new Color(105, 230, 145); 
    protected final Color LightRed = new Color(240, 105, 80); 
    protected final Color DarkBlue = new Color(12,12,153);
    protected final Color Gray = new Color(120,119,114);
    
    //Borders
    protected final Border grayBorder = BorderFactory.createMatteBorder(1, 1, 2, 1, Gray);
    protected final Border darkBlueBorder = BorderFactory.createMatteBorder(1, 1, 2, 1, DarkBlue);
    protected final Border lightBlueBorder = BorderFactory.createMatteBorder(1, 1, 2, 1, LightBlue);
    protected final Border lightGreenBorder = BorderFactory.createMatteBorder(1,1,1,1, LightGreen); 
    protected final Border loweredBlueBorder = BorderFactory.createCompoundBorder(lightBlueBorder,
            darkBlueBorder); 
    
    //Variables for consistent feel and look 
    protected final int scrollSpeed = 10; 
    protected JFileChooser fileChooser; 
    protected Window root; 
    
    protected HashMap<String, String[]> ratesMap; 
    protected HashMap<String, String[]> globalPortfolio; 
    protected searchTree searchSuggestion; 
    
    /**
     * Every class in this project will be a JFrame, they will be children of 
     * this class, and they will be using this constructor. The argument is
     * the fist JFrame initialized; the parent window will hold common 
     * attributes needed for all other objects. 
     * @param arg parent Window. 
     */
    public Window(Window arg){
        super(); 
        
        this.setResizable(true);
        root = arg; 
        pyCaller = arg.pyCaller; 
        print("\nWindow Constructor called"); 
        
        refreshRates();
          
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = (int) screen.getWidth(); 
        int height = (int) screen.getHeight(); 
        
        setSize(width, height); 
        
        
        
    }
    
    /**
     * First window to be created. 
     */
    public Window(){
        print("\nEmpty Window constructor called"); 
        fileChooser = new JFileChooser(); 
        fileChooser.setCurrentDirectory(new File(currentDir));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
    }
    
    /**
     * Used to keep an updated list of values for every coin in your portfolio. 
     */
    protected static class Entry implements Comparable{
        /**
         * This object is one way of representing each partition of the 
         * portfolio, or the information for every crypto coin to which 
         * the user owns any amount greater than 0. 
         */
        
        private String prefix;
        private String name; 
        private String rate; 
        private String hold;
        private String balance;
        private String available; 
        private String tradable;
        private String assetValue; 
        
        /**
         * How every portfolio entry will be represented 
         * @param entry Information about the portfolio entry. 
         */
        public Entry(String[] entry){
            prefix = entry[0]; 
            name = entry[1];
            rate = entry[2];
            hold = entry[3]; 
            balance = entry[4]; 
            available = entry[5]; 
            tradable = entry[6]; 
            assetValue = entry[7]; 
        }
        
        public String getPrefix(){
            return prefix; 
        }
        
        public String[] getPortfolioRowFields(){
            String[] output = {prefix, name, rate, balance, assetValue}; 
            
            return output; 
            
        }
        public String[] getFields(){
            String[] output = {prefix, name, rate, hold, balance, available, tradable,
                assetValue}; 
            
            return output; 
        }
        
        /**
         * Entries can be compared by value and prefix(alphabetically); this is
         * why there is two ways to compare them. 
         * @param entry
         * @return 
         */
        public int preComp(String entry){
            return prefix.compareTo(entry); 
        }        
        @Override 
        public int compareTo(Object obj){
            Entry entry = (Entry) obj; 
            
            double localVal = Double.parseDouble(assetValue);
            double comp = Double.parseDouble(entry.assetValue); 
            if(localVal > comp) return 1; 
            else if(localVal == comp) return 0; 
            else return -1; 
        }
        
        @Override
        public String toString(){
            String output = prefix + " " + name + " | Rate: " + rate + " | Value: " + 
                    assetValue;
            return output; 
        }
    }
    
    /**
     * Structure used to search coins based on either prefix, or name. There is 
     * no need to specify full coin name/prefix. This method will return 
     * suggestions based on partially typed entries. 
     * @return 
     */
    protected  searchTree makeSearchTree(){
        
        searchTree output = new searchTree(); 
        String[] portfolio = pyCaller.getPortfolio().strip().split("\n"); 
        
        for(int i = 0; i < portfolio.length; i++){
            String[] fields = portfolio[i].split("#"); 
            
            String prefix = fields[0]; 
            output.addWord(prefix); 
            String[] info = getCoinRate(prefix); 
            
            if(info != null){
                String name = info[0]; 
                output.addWord(name);
            }
        }
        return output; 
    }
    
    
    @Override
    /**
     * Children objects will make better use of this methods. Implemented here
     * to avoid typing the implementation signature in every child class. 
     */
    public void run(){
        try{
            print("Empty window thread running, ID: " + Thread.currentThread().getId()); 
        }catch(Exception e){
            print("Threading Exception: " + e); 
        }
    }
    
    /**
     * 
     * @return portfolion as a an array of Entry.  
     */
    protected Entry[] getPortfolio(){
        
        String[] portfolio = pyCaller.getPortfolio().strip().split("\n");
        print("Portfolio stuff looks like this"); 
        
        var entries = portfolio.length - 1; 
        
        Entry[] output = new Entry[entries];
        
        root.globalPortfolio = new HashMap<>(); 
        
        int index = 0; 
        
        print("Making portfolio"); 
        
        for(int i = 0; i < portfolio.length; i++){
            //print(portfolio[i]);
            String[] fields = portfolio[i].split("#"); 
            String prefix, balance, hold, available, tradable, id; 
            
            prefix = fields[0]; 
            balance = round(fields[1], 4); 
            hold = round(fields[2], 4); 
            available = fields[3]; 
            tradable = fields[4];
            id = fields[5];
            
            String[] globalEntry = {balance, hold, available, tradable, id}; 
            
            root.globalPortfolio.put(prefix, globalEntry); 
            
            String[] info = getCoinRate(prefix); 
            if(info != null){
                String name = info[0]; 
                String rate = round(info[1], 6); 
                Double astValue = Double.parseDouble(rate) * Double.parseDouble(available);
                String assetValue = round(astValue.toString(), 4); 

                String[] entry = {prefix, name, rate, hold, balance, available, tradable, assetValue};
                Entry newEntry = new Entry(entry); 
                //print("Entry: " + newEntry); 
                output[index] = newEntry; 
                index++; 
            }
        }
        return output; 
    }
    
    protected Entry[] mergeSort(Entry[] arr){
        /**
         * Good ole reliable mergesort, as of now it is specialized for objects
         * type Entry. Future implementations will work on Objects types. Sorts
         * array in Descending asset value. 
         */
        
        int hi = arr.length - 1; 
        int lo = 0; 
        arr = mergeHelper(arr, lo, hi); 
        
        return arr; 
    }
    
    protected Entry[] mergeHelper(Entry[] arr, int lo, int hi){
        
        Entry[] output = null; 
        
        if(lo == hi){
           output = new Entry[1]; 
           output[0] = arr[lo]; 
        }else{
            int mid = lo + (hi - lo)/ 2; 
            Entry[] left, right; 
            left = mergeHelper(arr, lo, mid); 
            right = mergeHelper(arr, mid + 1, hi); 
            
            output = merge(left, right); 
            
        }
        return output; 
    }
    
    protected Entry[] merge(Entry[] left, Entry[] right){
        
        int outSize = left.length + right.length;
        Entry[] output = new Entry[outSize]; 
        
        int Lind = 0; 
        int Rind = 0; 
        
        for(int i = 0; i < output.length; i++){
            
            if(Lind == left.length){
                while(Rind < right.length){
                    output[i] = right[Rind]; 
                    Rind++; 
                    i++; 
                }
            }else if(Rind == right.length){
                while(Lind < left.length){
                    output[i] = left[Lind]; 
                    Lind++; 
                    i++; 
                }
            }else{
                if(left[Lind].compareTo(right[Rind]) > 0){
                    output[i] = left[Lind]; 
                    Lind++; 
                }else{
                    output[i] = right[Rind]; 
                    Rind++; 
                }
            }
             
        }
        
        return output; 
    }
    
    /**
     * Updates the gui. 
     */
    public void validateRoot(){
        root.validate();
    }
    
    public JComponent addVisual(JPanel pPanel, String pString, String pText){
        /**
         * Some method I made when i first started this project. Outdated because
         * customization got complicated. 
         */
        pPanel.add(Box.createHorizontalGlue());
        
        if(pString.toLowerCase().equals("label")){
            JLabel label = new JLabel(pText);
            pPanel.add(label);
            pPanel.add(Box.createHorizontalGlue());
            
            return label;
        }else if(pString.toLowerCase().equals("button")){
            JButton button = new JButton(pText);
            int size = (pText.length() * 11) + 12;
            button.setPreferredSize(new Dimension(size,30));
            button.addActionListener(this);
            pPanel.add(button);
            pPanel.add(Box.createHorizontalGlue());
            
            return button; 
        }
        return null; 
    }
    
    public JLabel addLabelToFlow(JPanel pPanel, String pText){
        /**
         * Same as addVisual
         */
        JLabel label = new JLabel(pText);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        pPanel.add(label);
        pPanel.add(Box.createHorizontalGlue());
        
        return label;
    }
    /**
     * If the filechooser is used to select password, secret, and public keys,
     * this method helps grab the filepaths for those files. 
     * @return 
     */
    public String getFilePath(){
        if(fileChooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION){
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            fileChooser.setCurrentDirectory(new File(filePath));
            return filePath;
        }
        
        return null; 
    }
    
    public void refreshRates(){
        System.out.println("Refreshing rates"); 

        ratesMap = new HashMap<>(); 
        
        String[] fields, market; 
        market = pyCaller.getMarket(); 
        
        for(int i = 0; i < market.length; i++){
            //print(market[i]); 
            fields = market[i].split("#"); 
            String[] prefixValues = {fields[1], fields[2]}; 
            ratesMap.put(fields[0], prefixValues); 
            
        }
    }
    
    public String[] getCoinRate(String prefix){
        return ratesMap.get(prefix); 
    }
    
    @Override
    public  void actionPerformed(ActionEvent event){
        print("window event: " + event.getActionCommand()); 
    }
    
    @Override
    public void componentResized(ComponentEvent e){
        print("Component Resized: " + e); 
    }
    
    @Override
    public void componentHidden(ComponentEvent e){
        print("Component Hidden: " + e); 
        
    }
    
    @Override
    public void componentShown(ComponentEvent e){
        print("Component Shown: " + e); 
    }
    
    @Override
    public void componentMoved(ComponentEvent e){
        print("Component Moved: " + e); 
    }
    
    public String format(String format, Double arg){
        String argument = arg.toString();
        return format(format, argument); 
    }
    
    /**
     * Think python string formatting. Instead of {}, placing is decided 
     * by " {A#} " where A can be either L, R, C for left, right or center
     * justified. And # is the number of digits allocated for the word. String 
     * arguments can be passed as a String, where each individual string is 
     * separated by commas: " string1, string2, str2".
     * 
     * @param format Complete string. 
     * @param arg
     * @return 
     */
    public String format(String format, String args){
        String[] glossary = args.split(",");
        return format(format, glossary); 
    }
    
    /**
     * Same as format, but string arguments can be passed as a list of strings. 
     * @param format
     * @param glossary
     * @return 
     */
    public String format(String format, String[] glossary){
        String output = "";
        
        int len = format.length();
        int cursor = 0;
        int leftB = -1;
        int rightB = -1;
        int glossaryIndex = 0;

        while(cursor < len){

            leftB = format.indexOf("[", cursor); // Get left bracket
            rightB = format.indexOf("]", leftB);  // get right output;

            if((leftB >= 0) && (rightB >= 0)){ // if left and right brackets are
                //                              found.
                output += format.substring(cursor, leftB); //Get everything
                // from cursor to left bracket and add it to output

                int charSpaces = Integer.parseInt(format.substring(leftB + 2, rightB));
                char justify = format.charAt(leftB +1);

                String addition = processJustify(justify, charSpaces, glossary[glossaryIndex]);
                output += addition;

                glossaryIndex++;
            }else{
                output += format.substring(cursor, len); // if both brackets
                // not found, add rest of string to output;
                return output;
            }
            cursor = rightB + 1;
        }
        return output;
    }
    
    public String processJustify(char type,int size, String txt){
        String output = "";
        type = Character.toLowerCase(type);

        switch (type){

            case ('c'):
                output = centerJustified(size, txt);
                break;
            case ('l'):
                output = leftJustified(size, txt);
                break;
            case('r'):
                output = rightJustified(size, txt);
                break;
        }
        return output;
    }
    
    public String leftJustified(int size, String txt){

        String output = "";
        int chars = txt.length();
        int txtIndex = 0;

        for(int i= 0; i < size; i++){
            if(txtIndex < chars){
                output += txt.charAt(txtIndex);
                txtIndex++;
            }else {
                output += " ";
            }
        }
        return output;
    }   
    
    public String centerJustified(int size, String txt){

        char[] Field = new char[size];

        int fieldMid = size / 2;
        int txtMid = txt.length() / 2;

        for(int i = fieldMid; i >= 0; i--){
            if(txtMid >= 0) {
                Field[i] = txt.charAt(txtMid);
                txtMid--;
            }else{
                Field[i] = ' ';
            }
        }

        fieldMid = size / 2;
        txtMid = txt.length() / 2;

        for(int i = fieldMid; i < size; i++){
            if(txtMid < txt.length()){
                Field[i] = txt.charAt(txtMid);
                txtMid++;
            }else{
                Field[i] = ' ';
            }
        }
        return String.copyValueOf(Field);
    }
    
    public String rightJustified(int size, String txt){

        char[] output = new char[size];
        int chars = txt.length();
        int charIndex = chars - 1;

        for(int i = size - 1; i  >= 0; i--){
            if(charIndex >= 0) {
                output[i] = txt.charAt(charIndex);
                charIndex--;
            }else{
                output[i] = ' ';
            }
        }
        String out = String.copyValueOf(output);
        return out;
    }
    
    public String sciToDec(String number){
        
        if(number.contains("E")) number = number.replace("E", "e");
        
        
        String[] nums = number.split("e"); 
        String output = ""; 
        
        int shift = Math.abs(Integer.parseInt(nums[1])); 
        
        if(nums[1].startsWith("-")){
            output = "0."; 
            for(int i = 0; i < shift - 1; i++){
                output+= "0"; 
            }
            output += nums[0].replace(".", ""); 
        }
        
        return output; 
    }
    
    public double round(double num, int decimals){
        String output = round(Double.toString(num), decimals); 
        
        return Double.parseDouble(output); 
    }
    
    public String round(String num, int decimals){
        
        if(num == null || num.isEmpty()) return "0.0";
        if(num.contains("e") || num.contains("E")){
            num = sciToDec(num); 
        }
        
        String output = "0.0";  
        
        if(num.contains(".")){
            
            String[] numbers = num.replace(".", "#").split("#"); 
            
            if(numbers.length > 1){
                
                String dec = roundHelp(numbers[1], decimals); 
                
                if(numbers[0].equals("")) numbers[0] = "0"; 
                
                output = numbers[0] + "." + dec; 
                
                
            }else if(numbers.length == 1){
                output = numbers[0] + "." + roundHelp("0", decimals); 
            }
        }else{
                output = num + "." + roundHelp("0", decimals); 
        }
        
        return output; 
        
    }
    
    public void print(String arg){
        System.out.println(arg); 
    }
    
    public void print(String[] arg){
        if(arg == null) return; 
        for(int i = 0; i < arg.length; i++){
            System.out.print(arg[i] + " | "); 
        }
        System.out.print("\n"); 
    }
    public String roundHelp(String num, int decimal){
        
        String[] numbers = num.split(""); 
        
        int numlen = numbers.length; 
        
        if(numlen > decimal){
            // . 0 3 2 3 4 7 numbers
            // . 0 0 0 0 0   decimal 
            // . 0 3 2 3 5
            Integer newNum = Integer.parseInt(numbers[decimal -1]); 
            if(Integer.parseInt(numbers[decimal]) >= 5){
                newNum ++; 
            }
            numbers[decimal - 1] = newNum.toString(); 
        }
        
        
        String output = ""; 
        
        for(int i = 0; i < decimal; i++){
            if(i < numlen){
                output += numbers[i]; 
            }
        }
        
        return output; 
    }
    
    protected Object searchEntry(Comparable target, Comparable[] arr){
        int hi = arr.length - 1; 
        return searchHelp(0, hi, target, arr); 
    }
    
    private Object searchHelp(int lo, int hi, Comparable target, Comparable[] arr){
        
        if(arr[lo].compareTo(target) == 0) return arr[lo]; 
        if(arr[hi].compareTo(target) == 0) return arr[hi]; 
        
        int mid = lo + ((hi - lo) / 2); 
        
        if(arr[mid].compareTo(target) == 0) return arr[mid]; 
        if(lo == hi) return null; 
        
        if(arr[mid].compareTo(target) > 0){
            return searchHelp(lo,mid,target, arr); 
        }else if(arr[mid].compareTo(target) < 0){
            return searchHelp(mid + 1,hi,target, arr); 
        }
        return null; 
    }
    
    
}
