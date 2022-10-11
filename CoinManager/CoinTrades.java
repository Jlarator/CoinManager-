/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.packages;

import java.awt.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.*; 
import javax.swing.table.*; 
import javax.swing.SwingConstants; 
import javax.swing.event.*;
import java.awt.event.*; 
import java.io.*; 
import java.util.*; 

/**
 *
 * @author jlarator
 */
public class CoinTrades extends Window implements ItemListener, DocumentListener, FocusListener{
    
    private JPanel mBackground = new JPanel();
    private JPanel mTopFields = new JPanel();
    
    private JPanel westPanel;
    private JPanel eastPanel = new JPanel(); 
    private JPanel southPanel; 
    
    // South fields
    private numberTextField buyLimitField;
    private numberTextField buySizeField; 
    private numberTextField buyStopPriceField; 
    private JLabel quoteBuyEstimateLabel; 
    private JPanel buyMessagePanel; 
    private JLabel buyTextMessage; 
            
    private numberTextField sellSizeField; 
    private numberTextField sellLimitField; 
    private numberTextField sellStopPriceField; 
    private JLabel quoteSellEstimateLabel; 
    private JPanel sellMessagePanel; 
    private JLabel sellTextMessage; 
    
    private String stop = "loss"; 
    private final String currDir = System.getProperty("user.dir"); 
        
    //Other Stuff
    private JPanel mMenuButtons = new JPanel();
    private JScrollPane mContents; 
    private JPanel ordersPanel; 
    private Dimension cellsize;
    
    private String[] mPortfolio = null; 
    private String baseCurrency; 
    private JButton[] baseButtons;
    private HashMap<String,Order> orders; 
    private CheckBox[] checkBoxes; 
    
    private final String coinNameID; // ETH-USD
    private final String prefix;    // 
    private final String coinName; // Ether
    private final String coinID; // cf9985dc-7491-432d-bb51-1cd96bfcfb69
    private final String USDID; // 3fe34c2f-bc24-49f4-8cf2-cc64b294d7dc
    
    private JLabel valueLabel; 
    private JLabel availCoinLabel;
    private JLabel availUSDLabel;
    private String rate; 
    
    private Double averageSell; 
    private Double averageBuy; 
    
    private Double coinsSold; 
    private Double runningSellValue; 
    
    private Double coinsBought; 
    private Double runningBuyValue; 
    
    private Double totalFees; 
    private Double gains; 
    
    private TitledBorder sellBorder = BorderFactory.createTitledBorder("Sell"); 
    private TitledBorder buyBorder = BorderFactory.createTitledBorder("Buy");
   
    private MatteBorder redMattBorder = BorderFactory.createMatteBorder(1, 5, 5, 1, LightRed); 
    private MatteBorder greenMattBorder = BorderFactory.createMatteBorder(1, 5, 5, 1, LightGreen); 
    
    private Dimension dim = new Dimension(230, 30); 
    private boolean running; 
    private Thread thread;
    
    
    public CoinTrades(Window arg, String name){
        
        super(arg); 
        print("\nCoin trades constructor Called"); 
        sellBorder.setTitleJustification(TitledBorder.CENTER);
        sellBorder.setTitleColor(LightGreen);
        buyBorder.setTitleJustification(TitledBorder.CENTER); 
        buyBorder.setTitleColor(LightRed);
                
        running = false; 
        coinNameID = name;  
        baseCurrency = name.split("-")[1]; 
        
        prefix = name.split("-")[0]; // ex. CGLD
        print("looking for " + prefix + " in rates map"); 
        print(ratesMap.get(prefix)); 
        coinName = ratesMap.get(prefix)[0]; // ex. Celo
        rate = ratesMap.get(prefix)[1]; 
        coinID = root.globalPortfolio.get(prefix)[4];  // ex. 334kdd0dk3
        USDID = root.globalPortfolio.get("USD")[4]; 
        
        print(format("prefix:[l5] coinName:[l7] coinID:[l10]", prefix+","+coinName
        +","+coinID)); 
        orders = new HashMap<>(); 
        
        root.getContentPane().removeAll(); 
        root.repaint();
        root.setTitle(name);
        
        mBackground.setLayout(new BorderLayout()); 
        mTopFields.setLayout(new BoxLayout(mTopFields, BoxLayout.Y_AXIS)); 
        
        mMenuButtons.setBackground(LightBlue); 
        mMenuButtons.setLayout(new BoxLayout(mMenuButtons, BoxLayout.X_AXIS));
        mMenuButtons.add(Box.createRigidArea(new Dimension(45, 45)));
        
        JButton marketButton = (JButton) addVisual(mMenuButtons, "Button", " Market "); 
        
        String[] baseCurrencies = {" -USD ", " -USDC ", " -BTC "}; 
        
        baseButtons = new JButton[3]; 
        
        for(int i = 0; i < baseButtons.length; i++){
            baseButtons[i] = new JButton(baseCurrencies[i]); 
            
            if(baseCurrencies[i].strip().substring(1).equals(baseCurrency)){
                baseButtons[i].setBackground(this.LightGreen);
            }else{
              baseButtons[i].setBackground(Gray);  
            }
            baseButtons[i].setActionCommand(baseCurrencies[i].strip());
            baseButtons[i].addActionListener(this); 
            mMenuButtons.add(baseButtons[i]); 
        }
        
        JButton testButton = (JButton) addVisual(mMenuButtons, "Button", "Portfolio"); 
       
        mTopFields.add(mMenuButtons);
        
        mTopFields.add(mMenuButtons); 
        
        String infoText = "Click on the button for the coin that you want to " + 
                "see trades for"; 
        
        JLabel infoLabel = new JLabel(infoText); 
        mTopFields.setBackground(LightBlue);
        
        mTopFields.add(infoLabel); 
        
        mBackground.add(mTopFields, BorderLayout.NORTH);
        
        mBackground.add(eastPanel, BorderLayout.EAST);
        
        mContents = makeContents(); 
        if(mContents == null){
            mBackground.add(new JPanel(), BorderLayout.WEST); 
            mBackground.add(baseCurrencyNotSupported(), BorderLayout.CENTER); 
            
        }else{
            westPanel = makeWestPanel(); 
            southPanel = makeSouthPanel(); 
            
            mBackground.add(westPanel, BorderLayout.WEST);
            mBackground.add(mContents, BorderLayout.CENTER); 
            mBackground.add(southPanel, BorderLayout.SOUTH);
        }
        
        root.add(mBackground);
        startThread(); 
    }
    
    public class numberTextField extends JTextField implements KeyListener{
        
        private String preRelease = this.getText(); 
        private boolean pass = false; 
        
        
        public numberTextField(String arg, int num,FocusListener focusListener){
            super(arg, num); 
            this.addFocusListener(focusListener);
            this.addKeyListener(this);
        }
        
        public numberTextField(){
            super("No args super"); 
        }
        
        @Override 
        public void keyTyped(KeyEvent event){
            char typed = event.getKeyChar(); 
            if(typed >= '0' && typed <= '9' || typed == '.'){
                
                if(typed == '.'){
                    if(!preRelease.contains(".")){
                         pass = true; 
                    }
                }else{
                    pass = true; 
                }
            }
           
        }
        
        @Override
        public void keyPressed(KeyEvent event){
            
        }
        @Override
        public void keyReleased(KeyEvent event){
            String name = this.getName();
            
            if(pass){
                preRelease = this.getText(); 
                pass = false; 
            }else{
                if(preRelease.length() > this.getText().length()){
                    preRelease = this.getText(); 
                }else{
                    this.setText(preRelease); 
                }
            }
            
        }
        
        public String text(){
            return this.getText(); 
        }
    }
    
    // npa
    private final void startThread(){
        running = true; 
        thread = new Thread(this, "Coin rate Update"); 
        thread.start();
    }
    @Override
    public void run(){
        
        String[] coinProfiles; 
        
        try{
            print("CoinTrades Thread running: " + Thread.currentThread().getId()); 
            
            while(running){
                  String coinInfo = pyCaller.getCoinCost(this.coinNameID); 
                  
                  rate = coinInfo.split("#")[3]; 
                  
                  valueLabel.setText(format("[l21][l10]", "Coin Value:,"+ round(rate, 6)));
        
                Thread.sleep(20);
                
            }
        }catch (Exception e){
            print("Thread error: " + e); 
        }
    }
    
    public class Order implements Comparable{
        
        String side; 
        Double price; 
        Double size; 
        String id; 
        String orderInfoText; 
        
        public Order(String pSide, String pPrice, String pSize, String pId){
            side = pSide; 
            price = Double.parseDouble(pPrice); 
            size = Double.parseDouble(pSize); 
            id = pId; 
            
            orderInfoText = format("Price:[l6] size:[l6]", price + "," + size); 
        }
        
        @Override
        public int compareTo(Object obj){
            Order other = (Order) obj; 
            return id.compareTo(other.id); 
        }
        
        @Override
        public String toString(){
            return orderInfoText; 
        }
        
    }
     //
    public class CheckBox extends JCheckBox{
        boolean checked; 
        
        private final ImageIcon checkedIcon = new ImageIcon(currDir + "/checkedBox.png"); 
        private final ImageIcon uncheckedIcon = new ImageIcon(currDir + "/uncheckedBox.png"); 
     
        public CheckBox(){
            super();
            String descript = uncheckedIcon.getDescription(); 
            Image im = uncheckedIcon.getImage();
            setIcon(uncheckedIcon); 
            setSelectedIcon(checkedIcon); 
            
        }
        
    }
    
    public final JPanel baseCurrencyNotSupported(){
        JPanel panel = new JPanel(); 
        String info = format("[l10] is not a supported currency for this coin",
                baseCurrency); 
        
        JLabel label = new JLabel(info); 
        label.setFont(Mono);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(label); 
        
        return panel; 
    }
    
    public final JScrollPane makeContents(){
        print("Make Contents"); 
        JPanel contents = new JPanel(); 
        JScrollPane output;
        //Fill the center 
        BoxLayout centerLayout = new BoxLayout(contents, BoxLayout.Y_AXIS) ;
        contents.setLayout(centerLayout);
        
        String[] headerNames = {"Date", "Price", "Size", "Fee", "Side", "Volume",
            "Post-Fee Volume", "Revalued Cost", "Balanced Volume", "Gain/Loss"};
        
        JPanel header = new JPanel(); 
        header.setLayout(new GridLayout(1, 10)); 
        
        for(var column: headerNames){
            
            JPanel panel = new JPanel(); 
            panel.setBorder(grayBorder); 
            JLabel label = new JLabel(column); 
            label.setFont(mediumDejaVu); 
            panel.add(label); 
            header.add(panel);  
        }
        
        averageSell = 0.0; 
        averageBuy = 0.0;
    
        coinsSold = 0.0;
        runningSellValue = 0.0;  
    
        coinsBought = 0.0; 
        runningBuyValue = 0.0; 
    
        totalFees = 0.0; 
        gains = 0.0; 
        
        cellsize = header.getPreferredSize(); 
        
        String stuff = pyCaller.getFills(coinNameID); 
        // Date
        //Price
        //Size
        //Fee
        //Side
        //Volume
        //PostFee volume
        //Revaluated cost
        //Balance Volume 
        //Gain
        String[] ledger = pyCaller.getFills(coinNameID).strip().split("\n"); 
        
        if(ledger[0].startsWith("Error")){
            print("there was an error in Make contensts: "); 
            return null;
        } 
        
        int cellIndex; 
        for(int in = 0; in < ledger.length; in++){
            String[] fields = ledger[in].split("#"); 
            
            JPanel row = new JPanel(); 
            row.setBorder(lightBlueBorder); 
            row.setLayout(new GridLayout(1, 10)); 
            row.setPreferredSize(cellsize);
                
                
            if(fields[0].equals("")){
                for(int i = 0; i < 10; i++){
                    row.add(new JLabel(" ")); 
                }
            }else{
                Double size = Double.parseDouble(fields[2].split("=")[1]);
                Double volume = Double.parseDouble(fields[5].split("=")[1]);
                Double fee = Double.parseDouble(fields[3].split("=")[1]);
                totalFees += fee; 
                
                cellIndex = 0; 
                for(var field:fields){
                    String[] cell = field.split("="); 
                    
                    JLabel label = null;  
                    if(cell.length > 1){
                        String cellData = cell[1]; 
                        if((cellIndex == 0) || (cellIndex == 4)){
                            label = new JLabel(cellData); 
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                            label.setVerticalAlignment(SwingConstants.CENTER);
                            if(cellIndex ==4){
                                JPanel panel = new JPanel();
                                panel.setLayout(new GridBagLayout());
                                panel.setPreferredSize(cellsize);
                                panel.add(label);
                                
                                if(cellData.equals("buy")){
                                    coinsBought += size;  
                                    runningBuyValue += volume + fee; 
                                    panel.setBackground(LightRed);
                                }else{
                                    coinsSold +=  size; 
                                    runningSellValue += volume - fee; 
                                    panel.setBackground(LightGreen);
                                }
                                row.add(panel, cellIndex); 
                            }
                        }else{
                            cellData = round(cellData, 6); 
                            label = new JLabel(cellData); 
                            if(cellIndex == 9 && !cellData.equals("")){
                                gains += Double.parseDouble(cellData); 
                            }
                        }
                    }else{
                        label = new JLabel(" "); 
                    }
                    
                    if(cellIndex != 4){
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setFont(smallDejaVu);
                        row.add(label, cellIndex); 
                    }
                    cellIndex++; 
                }
                
            } 
            
            contents.add(row);
        }
        
        for(int i = 0; i < 35; i++){
            JPanel filler = new JPanel(); 
            filler.setPreferredSize(cellsize);
            contents.add(filler); 
        }
        
        if(coinsSold == 0) averageSell = 0.0; 
        else averageSell = runningSellValue / coinsSold; 
        
        if(coinsBought == 0) averageBuy = 0.0; 
        else averageBuy= runningBuyValue / coinsBought; 
        
        JViewport viewPort = new JViewport(); 
        viewPort.setView(header); 
        viewPort.setVisible(true); 
        
        output = new JScrollPane(contents); 
        JScrollBar  verticalScrollbar = new JScrollBar(); 
        int defaultUnitIncrement = verticalScrollbar.getUnitIncrement();
        verticalScrollbar.setUnitIncrement(defaultUnitIncrement * scrollSpeed);
        
        output.setVerticalScrollBar(verticalScrollbar);
        output.setColumnHeader(viewPort);
        
       
        JPanel filler = new JPanel(); 
        filler.setBackground(Gray);
        contents.add(filler); 
        
        
        
        return output;
    }
    
    public final JPanel makeOrdersPanel(){
        print("Making Orders Panel"); 
        JPanel ordersPanel = new JPanel(); 
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
       
        String[]coinOrders = pyCaller.getCoinOrders(coinNameID).split("\n");
        
        if(coinOrders[0].startsWith("Error")){
            print("Make West Panel will be null"); 
            return null; 
        }
        
        checkBoxes = new CheckBox[coinOrders.length / 2]; 
        int index = 0; 
        
        if(!coinOrders[0].isEmpty()){
            for(String orderInfo: coinOrders){ 
                String[] data = orderInfo.split("#"); 

                if(!data[0].startsWith("_")){
                    String side = data[0].split("=")[1]; 
                    String price = round(data[1].split("=")[1], 6); 
                    String size = round(data[2].split("=")[1], 6); 
                    String id = data[3].split("=")[1]; 
                    
                    Order order = new Order(side, price, size, id); 
                    orders.put(id, order);

                    int ln = order.toString().length(); 
                    
                    Double totalCost = Double.parseDouble(size) * Double.parseDouble(price); 
                    
                    CheckBox checkBox = new CheckBox(); 
                    checkBox.setName(order.id);
                    checkBox.setActionCommand("CheckBox");
                    checkBox.addActionListener(this);
                    checkBox.addItemListener(this);
                    checkBoxes[index] = checkBox; 
                    checkBox.setVisible(false);
                    
                    JLabel priceLabel = new JLabel("$:" + order.price.toString());
                    priceLabel.setFont(this.smallMono);
                    
                    JLabel sizeLabel = new JLabel("#:" + order.size.toString()); 
                    sizeLabel.setFont(this.smallMono);
                    
                    JLabel totalLabel = new JLabel("$" + format("[l9]",round(totalCost,2)));
                    
                    JPanel panel = new JPanel(); 
                    
                    String al = format("$" + order.price.toString() + " #:" + 
                            order.size.toString() + " $[l9]", round(totalCost,2)); 
                     
                    panel.add(checkBox); 
                    panel.add(priceLabel); 
                    panel.add(sizeLabel); 
                    panel.add(totalLabel); 
                    
                    if(side.equals("buy")){
                        panel.setBorder(buyBorder);
                    }else {
                        panel.setBorder(sellBorder);
                    } 
                    
                    panel.setMaximumSize(new Dimension(230, 50));
                    ordersPanel.add(panel); 
                    index++; 
                }
            }
        }
        
        for(int i = checkBoxes.length; i < 6; i++){
            ordersPanel.add(Box.createVerticalStrut(35)); 
        }
         
        return ordersPanel; 
    }
    
    public final void updateAvail(){
        String[] responseCoin = pyCaller.getProductRate(coinID).split("#"); 
                
                String balance, hold, availableCoin; 
                
                balance = responseCoin[0]; 
                hold = responseCoin[1]; 
                availableCoin = responseCoin[2]; 
                
                availCoinLabel.setText(format("Available " + prefix + " [l6]", availableCoin));
                
                String[] responseUSD = pyCaller.getProductRate(USDID).split("#"); 
                
                String availUSD = responseUSD[2]; 
                availUSDLabel.setText(format("Available USD [l6]", availUSD));
                
                availCoinLabel.validate();
                availUSDLabel.validate();
    }
     
    public final JPanel makeSouthPanel(){
        
        JTabbedPane orderPanel = new JTabbedPane(JTabbedPane.LEFT); 
        
        // Sell Panel
        JPanel sellPanel = new JPanel(); 
        sellPanel.setLayout(new BoxLayout(sellPanel, BoxLayout.Y_AXIS)); 
        sellPanel.setBorder(BorderFactory.createTitledBorder(greenMattBorder,
                "Make Sell Order", TitledBorder.CENTER, TitledBorder.CENTER));
        String[] stopTypes = {"loss", "entry"};
        // Top widgets 
         
        JLabel sellStopTypeLabel = new JLabel("Stop type:");
        JComboBox sellStopSelect = new JComboBox(stopTypes); 
        sellStopSelect.setSelectedIndex(0); 
        sellStopSelect.addActionListener(this); 
        JLabel sellStopPriceLabel = new JLabel("Stop price: ");
        sellStopPriceField = new numberTextField("0.00", 15, this); 
        sellStopPriceField.setName("Sell stop price"); 
        
        JPanel topSellWidgets = new JPanel(); 
        //topSellWidgets.add(sellLabel); 
        topSellWidgets.add(sellStopTypeLabel); 
        topSellWidgets.add(sellStopSelect); 
        topSellWidgets.add(sellStopPriceLabel); 
        topSellWidgets.add(sellStopPriceField); 
        
        // Middle Widgets
        JLabel sellSizeLabel = new JLabel("Size"); 
        sellSizeField = new numberTextField("0", 15, this);
        sellSizeField.setActionCommand("sellSizeField"); 
        sellSizeField.setName("Sell:Size Field"); 
        sellSizeField.getDocument().addDocumentListener(this);
        sellSizeField.addActionListener(this);
        
        JLabel sellLimitLabel = new JLabel("Limit cost"); 
        sellLimitField = new numberTextField(rate, 15, this); 
        sellLimitField.setActionCommand("sellLimitField"); 
        sellLimitField.setName("Sell:Limit Field"); 
        sellLimitField.getDocument().addDocumentListener(this); 
        sellLimitField.addActionListener(this);
        
        JLabel quoteLabel = new JLabel("Est. cost"); 
        quoteSellEstimateLabel = new JLabel(format("[l8]", "0.00")); 
        
        JPanel middleSellWidgets = new JPanel();
        middleSellWidgets.add(sellSizeLabel); 
        middleSellWidgets.add(sellSizeField); 
        middleSellWidgets.add(sellLimitLabel); 
        middleSellWidgets.add(sellLimitField); 
        middleSellWidgets.add(quoteLabel);
        middleSellWidgets.add(quoteSellEstimateLabel); 
        
        JPanel bottomSellWidgets = new JPanel();
        bottomSellWidgets.setLayout(new GridLayout(0,2));
        
        sellMessagePanel = new JPanel(); 
        sellMessagePanel.setBorder(BorderFactory.createTitledBorder(grayBorder,
                "Order response",TitledBorder.ABOVE_TOP , ICONIFIED, smallMono));
        
        sellTextMessage = new JLabel(); 
        sellMessagePanel.add(sellTextMessage); 
        
        JButton placeSellOrderButton = new JButton("Place Sell Order"); 
        placeSellOrderButton.addActionListener(this);
        
        bottomSellWidgets.add(placeSellOrderButton);
        bottomSellWidgets.add(sellMessagePanel); 
        
        sellPanel.add(topSellWidgets); 
        sellPanel.add(middleSellWidgets); 
        sellPanel.add(bottomSellWidgets); 
        
        // Buy Panel 
        JPanel buyPanel = new JPanel(); 
        buyPanel.setLayout(new BoxLayout(buyPanel, BoxLayout.Y_AXIS)); 
        buyPanel.setBorder(BorderFactory.createTitledBorder(redMattBorder,"Make Buy Order",
                TitledBorder.CENTER, TitledBorder.CENTER));
        
        //JLabel buyLabel = new JLabel("Make Buy Order");
        JLabel buyStopTypeLabel = new JLabel("Stop type:");
        JComboBox buyStopSelect = new JComboBox(stopTypes); 
        buyStopSelect.setSelectedIndex(0); 
        buyStopSelect.addActionListener(this); 
        JLabel buyStopPriceLabel = new JLabel("Stop price: ");
        buyStopPriceField = new numberTextField("0.00", 15, this);
        buyStopPriceField.setName("Buy stop price");
        
        JPanel topBuyWidgets = new JPanel(); 
        topBuyWidgets.add(buyStopTypeLabel); 
        topBuyWidgets.add(buyStopSelect); 
        topBuyWidgets.add(buyStopPriceLabel); 
        topBuyWidgets.add(buyStopPriceField); 
        topBuyWidgets.add(Box.createHorizontalGlue());
        
        JPanel middleBuyWidgets = new JPanel();  
        
        JLabel buySizeLabel = new JLabel("Size"); 
        buySizeField = new numberTextField("0", 15, this); 
        buySizeField.setActionCommand("buySizefield"); 
        buySizeField.setName("Buy:Size Field");
        buySizeField.getDocument().addDocumentListener(this);
        buySizeField.addActionListener(this);
        
        JLabel buyLimitLabel = new JLabel("Limit cost"); 
        buyLimitField = new numberTextField(rate, 15, this); 
        buyLimitField.setActionCommand("buyLimitField");
        buyLimitField.setName("Buy:Limit Field"); 
        buyLimitField.getDocument().addDocumentListener(this);
        buyLimitField.addActionListener(this);
        
        JLabel buyQuoteLabel = new JLabel("Est. cost");
        quoteBuyEstimateLabel = new JLabel(format("[l8]", "0.00"));
        
        middleBuyWidgets.add(buySizeLabel); 
        middleBuyWidgets.add(buySizeField); 
        middleBuyWidgets.add(buyLimitLabel); 
        middleBuyWidgets.add(buyLimitField); 
        middleBuyWidgets.add(buyQuoteLabel); 
        middleBuyWidgets.add(quoteBuyEstimateLabel);
        
        JPanel bottomBuyWidgets = new JPanel(); 
        bottomBuyWidgets.setLayout( new GridLayout(0, 2));
        
        JButton placeBuyOrderButton = new JButton("Place Buy Order"); 
        placeBuyOrderButton.addActionListener(this);
        
        buyMessagePanel = new JPanel(); 
        buyMessagePanel.setBorder(BorderFactory.createTitledBorder(grayBorder,
                "Order response",TitledBorder.ABOVE_TOP , ICONIFIED, smallMono));
        
        buyTextMessage = new JLabel(); 
        buyMessagePanel.add(buyTextMessage); 
        
        bottomBuyWidgets.add(placeBuyOrderButton); 
        bottomBuyWidgets.add(buyMessagePanel); 
        
        buyPanel.add(topBuyWidgets); 
        buyPanel.add(middleBuyWidgets); 
        buyPanel.add(bottomBuyWidgets); 
        
        // Putting it all together
        orderPanel.add("Sell", sellPanel); 
        orderPanel.add("Buy", buyPanel);
        
        
        JPanel output = new JPanel(); 
        output.add(orderPanel); 
        
        return output; 
    }
   
    public final JPanel makeWestPanel(){
        print("Making West Panel"); 
        JPanel output = new JPanel(); 
        output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));
        
        String valueText, averageSelltext, averageBuytext, coinsBoughttext,
                coinsSoldtext, runSelltext, runBuytext, totalFeetext, gainsText;
        
        JLabel averageSellLabel, averageBuyLabel, coinsBoughtLabel, 
                coinsSoldLabel, runSellLabel, runBuyLabel, totalFeeLabel, gainsLabel;
        
        valueText = format("[l21][l10]","Coin Value:,"+ round(rate, 6)); 
        averageSelltext = format("[l21][l10]", "Average Sell value: ,"+ round(averageSell,6)); 
        averageBuytext = format("[l21][l10]","Average Buy value: ,"+ round(averageBuy,6)); 
        coinsBoughttext = format("[l21][l10]","Coins sold: ," + round(coinsSold,6));
        coinsSoldtext = format("[l21][l10]","Coins bought: ,"+ round(coinsBought,6));
        runSelltext = format("[l21][l10]","Running sell value: ,"+round(runningSellValue,6)); 
        runBuytext = format("[l21][l10]","Running buy value: ,"+round(runningBuyValue,6)); 
        totalFeetext = format("[l21][l10]","Total fees: ,"+ round(totalFees,6)); 
        gainsText = format("[l21][l10]", "Gains: ," + round(gains, 6)); 
        
        String[] textList = {valueText, averageSelltext, averageBuytext, coinsBoughttext,
                coinsSoldtext, runSelltext, runBuytext, totalFeetext, gainsText};
       
        valueLabel = new JLabel(valueText); 
        averageSellLabel = new JLabel(averageSelltext);
        averageBuyLabel = new JLabel(averageBuytext); 
        coinsBoughtLabel = new JLabel(coinsBoughttext); 
        coinsSoldLabel = new JLabel(coinsSoldtext); 
        runSellLabel = new JLabel(runSelltext); 
        runBuyLabel = new JLabel(runBuytext); 
        totalFeeLabel = new JLabel(totalFeetext); 
        gainsLabel = new JLabel(gainsText); 
        
        JLabel[] labelList = {valueLabel, averageSellLabel, averageBuyLabel, coinsBoughtLabel, 
                coinsSoldLabel, runSellLabel, runBuyLabel, totalFeeLabel, gainsLabel};
          
        for(int i = 0; i < labelList.length; i++){
            labelList[i].setFont(lato);
            labelList[i].setHorizontalAlignment(JLabel.CENTER);
            JPanel panel = new JPanel();
            panel.setAlignmentX(CENTER_ALIGNMENT);
            panel.setBorder(grayBorder);
            panel.add(labelList[i]);
            panel.setPreferredSize(dim);
            panel.setMaximumSize(dim);
            output.add(panel); 
        }

        ordersPanel = makeOrdersPanel(); 
        JScrollPane ordersScroll = new JScrollPane(ordersPanel); 
        ordersScroll.setBorder(grayBorder);
        
        JScrollBar verticalScrollbar = new JScrollBar(); 
        int defaultUnitIncrement = verticalScrollbar.getUnitIncrement(); 
        verticalScrollbar.setUnitIncrement(defaultUnitIncrement * scrollSpeed);
        
        ordersScroll.setVerticalScrollBar(verticalScrollbar);
        
        JViewport viewport = new JViewport(); 
        JPanel viewPanel = new JPanel();
        JLabel ordersHeader = new JLabel(format("[c31]", "Open orders")); 
        viewPanel.add(ordersHeader); 
        
        viewport.setView(viewPanel);
        
        ordersScroll.setColumnHeader(viewport);
        
        output.add(ordersScroll); 
        
        output.setBorder(grayBorder);
        
        JPanel cancelPanel = new JPanel(); 
        JButton cancelButton = new JButton("Select orders to cancel");
        cancelButton.setActionCommand("Select orders to cancel");
        cancelButton.addActionListener(this);
        cancelPanel.add(cancelButton); 
        
        JPanel cancelBack = new JPanel(); 
        cancelBack.setLayout(new BoxLayout(cancelBack, BoxLayout.Y_AXIS));
        cancelBack.add(cancelPanel); 
        output.add(cancelBack); 
        
        String availCoin = root.globalPortfolio.get(prefix)[2]; 
        String labelString = format("Available " + prefix + " [l6]", availCoin);  
        availCoinLabel = new JLabel(labelString); 
        JPanel availCoinPanel = new JPanel(); 
        availCoinPanel.add(availCoinLabel); 
        
        String availUSD = root.globalPortfolio.get("USD")[2];
        String labelUSD = format("Available USD: [l6]", availUSD); 
        availUSDLabel = new JLabel(labelUSD); 
        JPanel availUSDPanel = new JPanel(); 
        availUSDPanel.add(availUSDLabel); 
        
        JPanel availablePanel = new JPanel(); 
        availablePanel.setLayout(new BoxLayout(availablePanel, BoxLayout.Y_AXIS));
        
        availablePanel.add(availCoinPanel); 
        availablePanel.add(availUSDPanel); 
        availablePanel.setBorder(loweredBlueBorder); 
        
        JPanel availBack = new JPanel();
        availBack.setLayout(new BoxLayout(availBack, BoxLayout.Y_AXIS));
        
        availBack.add(new JPanel()); 
        availBack.add(availablePanel); 
        
        output.add(availBack); 
        
        return output; 
    }
    
    public void updateCost(String side){
        
        if(side.equals("Sell")){
            String sizeString = sellSizeField.getText(); 
            String limitString = sellLimitField.getText(); 
            
            System.out.println("Size: " + sizeString); 
            System.out.println("Limit: " + limitString); 
            
            Double size = Double.parseDouble(sizeString); 
            Double limit = Double.parseDouble(limitString); 
            
            Double total = size * limit; 
            if(total > 0) total += getFee(total); 
        
            quoteSellEstimateLabel.setText(total.toString());
            quoteSellEstimateLabel.validate();
        }else{ 
            String sizeString = buySizeField.getText(); 
            String limitString = buyLimitField.getText();
            
            System.out.println("Size: " + sizeString); 
            System.out.println("Limit: " + limitString); 
            
            Double size = Double.parseDouble(sizeString); 
            Double limit = Double.parseDouble(limitString);
            
            Double total = size * limit; 
            total += getFee(total); 
            
            String text = format("[l9]", total.toString()); 
            
            quoteBuyEstimateLabel.setText(text); 
            quoteBuyEstimateLabel.validate();
        }
        
    }
    
    public double getFee(Double cost){
        
        if(cost >= 0 && cost <= 10000){ // 10K
            return 0.06;
        }else if(cost > 10000 && cost <= 50000){ // 50K
            return 0.04;
        }else if(cost > 50000 && cost <= 100000){ // 100K
            return 0.025;
        }else if(cost > 100000 && cost <= 1000000){ // 1 M
            return 0.02; 
        }else if(cost > 1000000 && cost <= 20000000){ // 20 M
            return 0.018; 
        }else if(cost > 20000000 && cost <= 100000000){ // 100 M
            return 0.015; 
        }else if(cost > 100000000 && cost <= 300000000){ // 300 M
            return 0.1; 
        }else if(cost > 300000000 && cost <= 500000000){ // 500 M
            return 0.008;
        }else if(cost > 500000000){
            return 0.005; 
        }
        
        return 0.0; 
    }
    
    public String purgeAlpha(String arg){
        
        if(arg.isEmpty()) return "0"; 
        
        String output = "";
        System.out.println("Purging: " + arg);
        System.out.println("Len: " + arg.length()); 
        
        boolean dot = false; 
        
        for(int i = 0; i < arg.length(); i++){
            char current = arg.charAt(i); 
            if(current <= '9' && current >= '0' || current == '.'){
                if(current == '.'){
                    if(!dot) output += String.valueOf(current);
                    else dot = true; 
                }else{
                    output += String.valueOf(current);
                }
                 
            }
        }
        return output; 
    }
    
    @Override
    public void itemStateChanged(ItemEvent e){
        System.out.println("Item State Change"); 
        CheckBox box = (CheckBox) e.getSource(); 
        box.checked = !box.checked; 
    }
    @Override
    public void focusGained(FocusEvent e){
        
        String source = e.getComponent().getName();
        String side = source.split(":")[0]; 
        System.out.println("Source gained: " + source);
        System.out.println("Side: " + side); 
        updateCost(side); 
    }
    
    @Override
    public void focusLost(FocusEvent e) {
            numberTextField sourceField = (numberTextField) e.getSource();
            System.out.println("Class: " + e.getClass());   
            String text = sourceField.text(); 
            
            String source = e.getComponent().getName();
            String side = source.split(":")[0]; 
            String purged = purgeAlpha(text); 
            sourceField.setText(purged); 
            updateCost(side);    
       
    }
    
    @Override
    public void insertUpdate(DocumentEvent event){
        System.out.println("Event insert: " + event); 
        System.out.println("Text: " + event.getDocument().getDefaultRootElement().getName());
    }
    
    @Override 
     public void removeUpdate(DocumentEvent event){ 
        System.out.println("Event remove: " + event);
        System.out.println("Text: " + event.getDocument().getDefaultRootElement().getName());
    }
    
    @Override
    public void changedUpdate(DocumentEvent event){
        System.out.println("Event changed: " + event); 
        System.out.println("Text: " + event.getDocument().getDefaultRootElement().getName()); 
    }
   
    @Override
    public void actionPerformed(ActionEvent event){
        String command = event.getActionCommand(); 
        print("Event: " + event.getActionCommand()); 
        
        running = false;
        
        switch(command){
            case(" Market "):
                Market market = new Market(root); 
                market.validateRoot();
                break; 
                
            case("Portfolio"):
                
                Portfolio portfolio = new Portfolio(root); 
                portfolio.validateRoot();
                break; 
            case("comboBoxChanged"):
                JComboBox combo = (JComboBox) event.getSource(); 
                stop = (String) combo.getSelectedItem();
                print("Combo selected: " + stop); 
                running = true; 
                break;
                
            case("Place Buy Order"):
                
                String buySize = buySizeField.getText(); 
                String buyPrice = buyLimitField.getText(); 
                String buyStopPrice = buyStopPriceField.getText(); 
                String buyOrderInfo = coinNameID + "|" + stop + "|"+ buySize + "|" +
                        buyPrice + "|" + buyStopPrice; 
                
                String buyResult = pyCaller.placeBuyOrder(buyOrderInfo); 
                print("Order info:\n" + buyOrderInfo); 
                print("Buy result:" + buyResult); 
                
                buyTextMessage.setText(buyResult); 
                buyTextMessage.validate();
                southPanel.validate();
                
                westPanel.removeAll();
                mBackground.remove(westPanel); 
                
                westPanel = null; 
                
                westPanel = makeWestPanel(); 
                mBackground.add(westPanel, BorderLayout.WEST); 
                
                root.validate();
                print("Finished Buy order"); 
                updateAvail(); 
                
                running = true; 
                
                break; 
                
            case("Place Sell Order"):
                String sellSize = sellSizeField.getText(); 
                String sellPrice = sellLimitField.getText(); 
                String sellStopPrice = sellStopPriceField.getText(); 
                String sellOrderInfo = coinNameID + "|" + stop + "|"+ sellSize + "|" +
                        sellPrice + "|" + sellStopPrice; 
                
                String sellResult = pyCaller.placeSellOrder(sellOrderInfo); 
                print("Sell result: " + sellResult); 
                
                sellTextMessage.setText(sellResult);
                sellMessagePanel.validate();
                
                southPanel.validate();
                westPanel.removeAll();
                mBackground.remove(westPanel); 
                
                westPanel = null; 
                
                westPanel = makeWestPanel(); 
                mBackground.add(westPanel, BorderLayout.WEST); 
                
                root.validate();
                print("Finished sell order"); 
                updateAvail(); 
                running = true; 
                
                break; 
            
            case("Select orders to cancel"):
                
                for(CheckBox box : checkBoxes){
                    box.setVisible(true);
                }
                
                JButton button = (JButton) event.getSource(); 
                
                button.setText("Cancel orders"); 
                button.setActionCommand("Cancel orders"); 
                running = true; 
                break; 
                
            case("Cancel orders"):
                
                for(CheckBox box: checkBoxes){
                    if(box.checked){
                        print("Order to cancel: " + box.getName());
                        String id = box.getName(); 
                        String out = pyCaller.cancelOrder(id); 
                        print("Out: " + out); 
                    } 
                    
                    box.setVisible(false); 
                }
                
                westPanel.removeAll();
                mBackground.remove(westPanel); 
                
                westPanel = null; 
                
                westPanel = makeWestPanel(); 
                mBackground.add(westPanel, BorderLayout.WEST); 
                
                root.validate(); 
                
                JButton butt = (JButton) event.getSource(); 
                
                butt.setText("Select orders to cancel");
                butt.setActionCommand("Select orders to cancel");
                running = true; 
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
