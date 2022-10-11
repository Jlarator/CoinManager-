/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.packages;


import java.awt.*;
import javax.swing.*;
import javax.swing.JScrollBar; 
import javax.swing.JScrollPane; 
import javax.swing.BorderFactory;
import javax.swing.border.*; 

import javax.swing.table.*; 
import javax.swing.event.*;
import java.awt.event.*; 
import java.io.*; 
import java.util.*; 


/**
 *
 * @author jlarator
 */
public class Portfolio extends Window implements ActionListener, DocumentListener{
    
    private JPanel mBackground = new JPanel();
    private JPanel mTopFields = new JPanel();
    
    private JPanel mMenuButtons = new JPanel();
    private Dimension cellSize;
    private Dimension contentSize; 
    private JScrollPane mContents; 
    
    private String baseCurrency = "-USD"; 
    private JButton[] baseButtons;
    private Entry[] mPortfolio = null; 
    private HashMap<String, RowEntry> rowEntryMap; 
    
    private JTextField searchField; 
    private HashMap<String, String[]> nameMap; 
    private JPanel portfolioContents;
    private JViewport viewport; 
    
    private Thread thread; 
    
    private boolean running; 
    
    /**
     * Screen displaying items in portfolio. 
     * @param arg Parent Window. 
     */
    public Portfolio(Window arg){
        super(arg); 
        print("\nPortfolio constructor called"); 
        root.getContentPane().removeAll();
        root.repaint();
        root.setTitle("Portfolio");
        
        pyCaller.makeMaps();
        ratesMap = pyCaller.marketMap(); 
        nameMap = pyCaller.nameMap(); 
        searchSuggestion = makeSearchTree();
        
        print("Done Making map"); 
        
        mBackground.setLayout(new BorderLayout());
        
        viewport = makeViewport(); 
        portfolioContents = makePortfolioContents(); 
        mContents = makeContents(portfolioContents);
        mTopFields = makeTopFields(); 
        
        JPanel westPanel = makeWestPanel(); 
        
        mBackground.add(mTopFields, BorderLayout.NORTH); 
        
        mBackground.add(new JPanel(), BorderLayout.EAST);
        mBackground.add(westPanel, BorderLayout.WEST);
        mBackground.add(new JPanel(), BorderLayout.SOUTH);
        mBackground.add(mContents, BorderLayout.CENTER); 
        
        root.add(mBackground); 
        startThread(); 
        
    }
    
    /**
     * Each entry in the portfolio. 
     */
    protected class RowEntry extends JPanel{
        
        private JButton coinButton; 
        private String prefix, name, rate, balance, assetValue; 
        private JLabel  nameLabel, rateLabel, balanceLabel, assetLabel; 
        
        /**
         * each entry in the portfolio. 
         * @param args 
         */
        public RowEntry(String[] args){
//            String[] fields = { all[1], all[2], all[4], all[7]}; 
//        // 0-prefix | 1-name | 2-rate | 3-balance | 4-assetValue 
            super();
            
            prefix = args[0]; 
            name = args[1]; 
            rate = args[2]; 
            balance = args[3]; 
            assetValue = args[4]; 
            
            nameLabel = new JLabel(name); 
            rateLabel = new JLabel(rate); 
            balanceLabel = new JLabel(balance); 
            assetLabel = new JLabel(assetValue); 
            
            setBorder(lightBlueBorder);
            setLayout(new GridLayout(0, 5));
            setPreferredSize(cellSize);

    //        // Make Button
            coinButton = new JButton(prefix); 
            coinButton.setFont(DejaVu);
            coinButton.setName(prefix); 
            coinButton.setActionCommand("CoinButton " + prefix); 
            add(coinButton); 

            //String[] fields = {name, rate, balance, assetValue}; 
            JLabel[] labels = {nameLabel, rateLabel, balanceLabel, assetLabel}; 
            for(int i = 0; i < labels.length; i++){
                labels[i].setHorizontalAlignment(SwingConstants.CENTER); 
                labels[i].setFont(DejaVu); 
                add(labels[i]); 
            }
            
        }
        
        public void addActionListener(ActionListener arg){
            coinButton.addActionListener(arg);
        }
        
        /**
         * used to update portfolio information. 
         * @param pRate
         * @param pBalance 
         */
        public void update(String pRate, String pBalance){
            rate = pRate; 
            balance = pBalance; 
            Double assVal = Double.parseDouble(rate) * Double.parseDouble(balance); 
            assetValue = assVal.toString(); 
            
            String[] fields = {rate, balance, assetValue}; 
            JLabel[] labels = {rateLabel, balanceLabel, assetLabel}; 
            
            for(int i = 0; i < fields.length; i++){
                labels[i].setText(round(fields[i], 4)); 
                labels[i].validate();
            }
            validate(); 
            
        }
    }
    
    /**
     * Makes the widgets for the JPanel located at the top of the screen. 
     * @return 
     */
    public JPanel makeTopFields(){
         
        JPanel output = new JPanel(); 
        mBackground.setLayout(new BorderLayout());
        output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS)); 
        
        mMenuButtons.setBackground(LightBlue); 
        mMenuButtons.setLayout(new BoxLayout(mMenuButtons, BoxLayout.X_AXIS)); 
        
        JButton mainButton = (JButton) addVisual(mMenuButtons, "Button", "Main Menu"); 
        mainButton.addActionListener(this);
        
        String[] baseCurrencies = {" -USD ", " -USDC ", " -BTC "}; 
        
        baseButtons = new JButton[3]; 
        
        for(int i = 0; i < baseButtons.length; i++){
            baseButtons[i] = new JButton(baseCurrencies[i]); 
            baseButtons[i].setBackground(Gray);
            baseButtons[i].setActionCommand(baseCurrencies[i].strip());
            baseButtons[i].addActionListener(this);
            mMenuButtons.add(baseButtons[i]); 
        }
        baseButtons[0].setBackground(LightGreen);
        
        JButton marketButton = (JButton) addVisual(mMenuButtons, "Button", " Market ");
        marketButton.setActionCommand("Market");
        
        mMenuButtons.add(Box.createHorizontalGlue());
        
        mMenuButtons.add(Box.createRigidArea(new Dimension(45, 45)));
        
        output.add(mMenuButtons); 
        
        // Search Bar. 
        JPanel searchPanel = new JPanel(); 
        searchField = new JTextField(60); 
        searchField.getDocument().addDocumentListener(this); 
        searchField.setActionCommand("TextField");
        
        JButton searchButton = new JButton("Search"); 
        searchButton.addActionListener(this);
        
        searchPanel.add(searchButton); 
        searchPanel.add(searchField); 
        
        String infoText = "Click on the button for the coin that you want to " + 
                "see trades for"; 
        
        JLabel infoLabel = new JLabel(infoText); 
        output.setBackground(LightBlue);
        
        output.add(infoLabel); 
        output.add(searchPanel); 
        
        return output; 
    }
    
    /**
     * Headers for the table displaying portfolio information. 
     * @return 
     */
    public JViewport makeViewport(){
        
        String[] headerNames = {"Button", "Name", "Rate", "Balance", "Asset value"};
        
        JPanel header = new JPanel(); 
        header.setLayout(new GridLayout(1, 5));
        
        for(var name: headerNames){
            JPanel panel = new JPanel();
            panel.setBorder(grayBorder);
            JLabel label = new JLabel(name); 
            label.setFont(DejaVu);
            panel.add(label);
            header.add(panel); 
        }
        
        cellSize = header.getPreferredSize();
        
        JViewport viewPort = new JViewport(); 
        viewPort.setView(header); 
        viewPort.setVisible(true); 
        
        return viewPort; 
    }
    
    /**
     * Makes a panel with a scroll bar. 
     * @param contents JPanel to encapsulate. 
     * @return JScrollPane. 
     */
    public JScrollPane makeContents(JPanel contents){
        
        JScrollPane output = new JScrollPane(contents); 
        
        JScrollBar verticalScrollbar = new JScrollBar(); 
        int defaultUnitIncrement = verticalScrollbar.getUnitIncrement();
        verticalScrollbar.setUnitIncrement(defaultUnitIncrement * scrollSpeed);
        
        output.setVerticalScrollBar(verticalScrollbar);
        output.setColumnHeaderView(viewport);
        
        return output; 
    }
    
    public JPanel makePortfolioContents(){
        
        JPanel contents = new JPanel();
        BoxLayout centerLayout = new BoxLayout(contents, BoxLayout.Y_AXIS);
        contents.setLayout(centerLayout);
        mPortfolio = getPortfolio(); 
        Entry[] sortedPort = mergeSort(mPortfolio); 
        rowEntryMap = new HashMap<>();  
        
        for(int i = 0; i < sortedPort.length; i++){
            RowEntry row = new RowEntry(sortedPort[i].getPortfolioRowFields());
            row.addActionListener(this); 
            rowEntryMap.put(row.prefix, row);
            contents.add(row); 
        }
        
        return contents; 
    }
    
    /**
     * Makes a JPanel including all the results retrieved from a search query
     * @param results ArrayList of strings to turn into a JPanel. 
     * @return JPanel
     */
    public JPanel makeSearchContents(ArrayList<String> results){
        
        JPanel output = new JPanel(); 
        output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));
        
        ArrayList<Entry> entries = new ArrayList<>(); 
        ArrayList<String> prefixes = new ArrayList<>(); 
        
        for(String result: results){
            String[] info = ratesMap.get(result);
            Entry searchResult; 
            if(info != null){
                searchResult = searchEntry(result); 
                String pre = searchResult.getPrefix(); 
                if(!prefixes.contains(pre)) prefixes.add(pre); 
                if(!entries.contains(searchResult)) entries.add(searchResult); 
            }else{
                info = nameMap.get(result); 
                if(info != null){
                    searchResult = searchEntry(info[0]);
                    String pre = searchResult.getPrefix(); 
                    if(!prefixes.contains(pre)) prefixes.add(pre); 
                    if(!entries.contains(searchResult)) entries.add(searchResult);
                }
            } 
            
        }
        
//        for(Entry entry: entries){
//            print("\nEntry: " + entry + " Pre: " + entry.getPrefix()); 
//            addEntryToPanel(entry, output); 
//        }
        for(String pre: prefixes){
            output.add(rowEntryMap.get(pre)); 
        }
        for(int i = 0; i < 36; i++){
            output.add(new JPanel());
        }
        return output; 
    }
    
    private void addEntryToPanel(Entry entry, JPanel panel){
        //print("\nAdding entry"); 
        String[] all = entry.getFields(); 

        RowEntry rEn = rowEntryMap.get(all[0]); 
        print("Prefix: " + all[0]); 
        print("SortedPort got: " + rEn.name ); 
        //panel.add(row); 
        panel.add(rEn); 
    }
    
    public JPanel makeWestPanel(){
        JPanel output = new JPanel(); 
        
        return output; 
    }
   
    /**
     * Updates contents on the center panel based on search queries. 
     */
    public void updateContents(){
        String query = searchField.getText(); 
        mContents.removeAll();
        mBackground.remove(mContents); 
        mContents = null; 
        
        if(query.isEmpty()){
            mContents = makeContents(portfolioContents);
        }else{
            ArrayList<String> results = searchSuggestion.searchResults(query); 
            JPanel resultsPanel = makeSearchContents(results); 
            mContents = makeContents(resultsPanel);
        }
        
        mBackground.add(mContents, BorderLayout.CENTER);
        root.validate();
    }
    
    @Override
    public void insertUpdate(DocumentEvent event){
        running = false; 
        updateContents(); 
        running = true; 
    }
    
    @Override 
     public void removeUpdate(DocumentEvent event){ 
        running = false; 
        updateContents(); 
        running = true; 
    }
    
    @Override
    public void changedUpdate(DocumentEvent event){
        System.out.println("Event changed: " + event); 
    }
    
    public void startThread(){
          thread = new Thread(this, "Updating rows Thread service"); 
          
          running = true; 
          thread.start();
    }
    
    @Override 
    public void run(){
        
        String[] portfolio, updatedRates; 
        while(running){
            
            updatedRates = pyCaller.updatedRates().split("\n"); 
            
            HashMap<String, String> balanceMap = pyCaller.getPortfolioMap(); 
            for(int i=0; i < updatedRates.length; i++){
                
                String[] info = updatedRates[i].split("#"); 
                String prefix, rate, balance; 
                prefix = info[0]; 
                rate = info[1]; 
                balance = balanceMap.get(prefix); 
                
                RowEntry row = rowEntryMap.get(prefix); 
                if(row != null)  row.update(rate, balance);
            }
        }
        
    }
    @Override
    public  void actionPerformed(ActionEvent event){
        
        running = false; 
        
        
        String command = event.getActionCommand(); 
        System.out.println("Command: " + command); 
        
        String[] ls = command.split(" ");
        
        if(ls.length > 1){
            if(ls[0]. equals("CoinButton")){
                String coin = ls[1] + baseCurrency; 
                String stuff = pyCaller.getFills(coin); 
                
                CoinTrades coinTrades = new CoinTrades(root, coin); 
                coinTrades.validateRoot();
            }
        }else{
            System.out.println("H__" + command + "___H"); 
            switch (command){
                
                case("Market"):
                    System.out.println("Market button pressed"); 
                    Market market = new Market(root); 
                    market.validateRoot();
                    break; 
                    
                case("-USD"):
                    
                    baseCurrency = "-USD"; 
                    for(int i = 0; i < baseButtons.length; i++){
                        if(baseButtons[i].getActionCommand().equals("-USD")){
                            baseButtons[i].setBackground(LightGreen);
                        }else{
                            baseButtons[i].setBackground(Gray);
                        }
                    }
                    running = true; 
                    break;
                case("-USDC"):
                    
                    baseCurrency = "-USDC"; 
                    for(int i = 0; i < baseButtons.length; i++){
                        if(baseButtons[i].getActionCommand().equals("-USDC")){
                            baseButtons[i].setBackground(LightGreen);
                        }else{
                            baseButtons[i].setBackground(Gray);
                        }
                    }
                    running = true; 
                    break;
                case("-BTC"):
                    
                    baseCurrency = "-BTC"; 
                    for(int i = 0; i < baseButtons.length; i++){
                        if(baseButtons[i].getActionCommand().equals("-BTC")){
                            baseButtons[i].setBackground(LightGreen);
                        }else{
                            baseButtons[i].setBackground(Gray);
                        }
                    }
                    running = true; 
                    break;
            }
        }
        
    }

    private Entry searchEntry(String target){
        int hi = mPortfolio.length - 1; 
        return searchHelp(0, hi, target); 
    }
    
    private Entry searchHelp(int lo, int hi, String target){
        
        if(mPortfolio[lo].preComp(target) == 0) return mPortfolio[lo]; 
        if(mPortfolio[hi].preComp(target) == 0) return mPortfolio[hi]; 
        
        int mid = lo + ((hi - lo) / 2); 
        
        if(mPortfolio[mid].preComp(target) == 0) return mPortfolio[mid]; 
        if(lo == hi) return null; 
        
        if(mPortfolio[mid].preComp(target) > 0){
            return searchHelp(lo,mid,target); 
        }else if(mPortfolio[mid].preComp(target) < 0){
            return searchHelp(mid + 1,hi,target); 
        }
        
        return null; 
        
    }
    
}
