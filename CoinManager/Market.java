/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.packages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JButton; 
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout; 
import java.awt.Dimension;
import java.awt.Font; 
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener; 
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.Box;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap; 
import java.util.ArrayList; 
import javax.swing.JScrollPane;
import javax.swing.JScrollBar; 
import javax.swing.JViewport;
//import org.pyCaller.core.PyObject;
//import org.pyCaller.util.PythonInterpreter;

/**
 *
 * @author jlarator
 */
public final class Market extends Window implements ActionListener{
    
    private JPanel mBackground = new JPanel();
    private JPanel mTopFields = new JPanel();
    private JScrollPane scroll; 
    
    private JPanel mMenuButtons = new JPanel();
    private JPanel mContents;
    
    private String[] coinProfiles = null;
    private String baseCurrency = "-USD"; 
    private JButton[] baseButtons; 
    private HashMap<String, RowEntry> marketEntries; 
    private Dimension cellSize; 
    private boolean running; 
    
    /**
     * Displayes Available currencies to purchase. 
     * @param arg 
     */
    public Market(Window arg){
        
        super(arg); 
        print("\nMarket Constructor Called"); 
        root.getContentPane().removeAll();
        root.repaint();
        root.setTitle("Market");
        
        running = true; 
         
        //mBackground.setLayout(new BoxLayout(mBackground, BoxLayout.Y_AXIS)); 
        mBackground.setLayout(new BorderLayout());
        mTopFields.setLayout(new BoxLayout(mTopFields, BoxLayout.Y_AXIS)); 
        
        mMenuButtons.setBackground(LightBlue); 
        mMenuButtons.setLayout(new BoxLayout(mMenuButtons, BoxLayout.X_AXIS));
        mMenuButtons.add(Box.createRigidArea(new Dimension(45, 45)));
        
        ratesMap = pyCaller.marketMap(); 
        
        getPortfolio(); 
        
        JButton mainButton = (JButton) addVisual(mMenuButtons, "Button", "Main Menu"); 
        
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
        
        JButton testButton = (JButton) addVisual(mMenuButtons, "Button", "Portfolio"); 
        
        scroll = makeCenterScrollPane(); 

        mTopFields.add(mMenuButtons);
        
        String infoText = "Click on the button for the coin that you want to " + 
                "see trades for"; 
        
        JLabel infoLabel = new JLabel(infoText); 
        mTopFields.setBackground(LightBlue);
        
        mTopFields.add(infoLabel); 
        
        mBackground.add(mTopFields, BorderLayout.NORTH);
        
        mBackground.add(new JPanel(), BorderLayout.EAST);
        mBackground.add(new JPanel(), BorderLayout.WEST);
        mBackground.add(new JPanel(), BorderLayout.SOUTH);
        
        mBackground.add(scroll, BorderLayout.CENTER); 
        
        root.add(mBackground);
        
        startThread(); 
    }
     
    /**
     * Row entry in the JPanel displaying market options. 
     */
    private class RowEntry extends JPanel{
        
        private JButton button; 
        private JLabel nameLabel, rateLabel; 
        private String prefix; 
        private String rate; 
        
        private RowEntry(String info){
            super(); 
            String[] infoList = info.split("#"); 
            
            String name; 
            name = infoList[1]; 
            rate = infoList[2]; 
            
            setBorder(lightBlueBorder); 
            setLayout(new GridLayout(1, 3)); 
            
            prefix = infoList[0]; 
            button = new JButton(prefix); 
            button.setFont(DejaVu); 
            button.setName(prefix); 
            button.setActionCommand("CoinButton " + prefix); 
            
            
            nameLabel = new JLabel(name); 
            rateLabel = new JLabel(rate); 
            
            JLabel[] labels = {nameLabel, rateLabel}; 
            
            for(int i = 0; i < labels.length; i++){
                labels[i].setHorizontalAlignment(SwingConstants.CENTER);
                labels[i].setFont(DejaVu);
            }
            
            add(button); 
            add(nameLabel); 
            add(rateLabel); 
            
        }
        
        private void addActionListener(ActionListener listener){
            button.addActionListener(listener);
        }
        
        public void updateRate(String rate){
            rateLabel.setText(rate);
            rateLabel.validate();
        }
        
    }
    
    /**
     * Makes headers for the table showing market options. 
     * @return 
     */
    private JPanel makeHeader(){
        String[] headerNames = {"Button", "Name", "Rate"}; 
        JPanel header = new JPanel(); 
        
        header.setLayout(new GridLayout(1, 5)); 
        
        for(String name: headerNames){
            JPanel panel = new JPanel(); 
            panel.setBorder(grayBorder); 
            JLabel label = new JLabel(name); 
            label.setFont(DejaVu);
            panel.add(label); 
            header.add(panel); 
        }
        
        return header; 
    }
    
    /**
     * Makes a JPanel containing all the market options. 
     */
    private void makeContents(){
        mContents = new JPanel(); 
        BoxLayout centerLayout = new BoxLayout(mContents, BoxLayout.Y_AXIS) ;
        mContents.setLayout(centerLayout);
        
        coinProfiles = pyCaller.getRates().strip().split("\n"); 
        
        marketEntries = new HashMap<>(); 
        
        for(int i = 0; i < coinProfiles.length; i++){   
            
            RowEntry row = new RowEntry(coinProfiles[i]); 
            
            marketEntries.put(row.prefix, row);
            
            row.setPreferredSize(cellSize);
            
            row.addActionListener(this);
            
            mContents.add(row); 
        }
    }
    
    /**
     * Makes a scroll pane in which to put the JPanel containing market options. 
     * @return 
     */
    public JScrollPane makeCenterScrollPane(){
        
        JPanel header = makeHeader(); 
        cellSize = header.getPreferredSize(); 
        makeContents(); 
        
        scroll = new JScrollPane(mContents); 
        
        JScrollBar verticalScrollbar = new JScrollBar(); 
        int defaultUnitIncrement = verticalScrollbar.getUnitIncrement();
        verticalScrollbar.setUnitIncrement(defaultUnitIncrement * scrollSpeed);
        scroll.setVerticalScrollBar(verticalScrollbar);
        
        JViewport viewPort = new JViewport(); 
        viewPort.setView(header); 
        viewPort.setVisible(true); 
        scroll.setColumnHeader(viewPort);
        
        return scroll; 
        
    }
    
    public void startThread(){
        Thread thread = new Thread(this, "Contents Update"); 
        thread.start();
    }
    @Override
    public void run(){
        try{
            print("Market Thread running: " + Thread.currentThread().getId()); 
            
            while(running){ 
                coinProfiles = pyCaller.updatedRates().strip().split("\n"); 
                
                for(int i = 0; i < coinProfiles.length; i++){
                    String[] infoList = coinProfiles[i].split("#"); 
                    
                    String prefix, rate; 
                    prefix = infoList[0]; 
                    rate = infoList[1]; 
                    //System.out.println("Updating rate: " + prefix); 
                    marketEntries.get(prefix).updateRate(rate); 
                }
                
                Thread.sleep(6);
            }
            
        }catch(Exception e){
            print("Threading exception: " + e); 
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        
        running = false; 
        
        String command = e.getActionCommand();
        System.out.println(command);
        
        String[] ls = command.split(" "); 
        if(ls.length > 1){
            if(ls[0].equals("CoinButton")){
                String coin = ls[1] + baseCurrency; 
                
                CoinTrades coinTrades = new CoinTrades(root, coin); 
                coinTrades.validateRoot();
                
            }
        }else{
            
            switch (command){
                
                case("Main Menu"):
                    
                    break;
                
                case("Portfolio"):
                    
                    Portfolio portfolio = new Portfolio(root); 
                    portfolio.validateRoot(); 
                    
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
                    break;
            }
        }
        
    }
     
}
