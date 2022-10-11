/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.packages;

import java.awt.BorderLayout; 
import java.awt.Color;
import javax.swing.BoxLayout; 
import javax.swing.JFrame;
import javax.swing.JButton; 
import javax.swing.JPanel; 
import javax.swing.JTextField; 
import javax.swing.JPasswordField;
import javax.swing.JLabel; 
import javax.swing.JOptionPane; 
import javax.swing.Box; 
import javax.swing.JFileChooser; 
import javax.swing.JOptionPane; 
import java.awt.event.ActionListener;
import java.awt.Dimension; 
import java.awt.Component; 
import java.awt.Font; 
import java.awt.GraphicsEnvironment; 
import java.awt.event.ActionEvent;
import java.io.File; 
import java.io.FileNotFoundException; 
import java.io.PrintWriter;
import java.util.Scanner; 
/**
 *
 * @author jlarator
 * Entry menu for selecting txt files with keys, or typed keys. 
 */
public class EntryMenu extends Window{
    
    private JButton publicBrowse;
    private JPasswordField APIPublicField;
    
    private JButton secretBrowse;
    private JPasswordField APISecretField;
    
    private JButton passBrowse;
    private JPasswordField passField;
    
    private final Dimension TXFdimension = new Dimension(4, 4); 
    
    public EntryMenu(Window arg){
        /**
         * Constructor. Every window in the program shares a root Window. 
         * This window holds any methods and variables needed throughout all 
         * the whole program. 
         */
        
        super(); 
        print("\nEntry Menu Constructor"); 
        root = arg; 
        arg.setTitle("Main Menu");
        arg.getContentPane().removeAll(); 
        
        pyCaller = root.pyCaller; 
        
        root.setSize(900, 400); 
        root.setLayout(new BorderLayout()); 
        
        JPanel northPanel = new JPanel(); 
        northPanel.setBackground(Gray);
        northPanel.add(Box.createGlue()); 
        
        JPanel southPanel = new JPanel();
        southPanel.setBackground(Gray);
        southPanel.add(Box.createGlue());  
        
        JPanel leftPanel = new JPanel(); 
        leftPanel.setBackground(Gray);
        leftPanel.add(Box.createGlue());  
        
        JPanel rightPanel = new JPanel(); 
        rightPanel.setBackground(Gray);
        rightPanel.add(Box.createGlue());  
        
        JPanel centerPanel = new JPanel(); 
        
        BoxLayout centerBoxLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS); 
        centerPanel.setBorder(grayBorder);
        centerPanel.setSize(new Dimension(900, 600));
        centerPanel.setMaximumSize(new Dimension(900, 700));
        centerPanel.setLayout(centerBoxLayout); 
        
        // main label at the top 
        JPanel labelPanel = new JPanel(); 
        JLabel coinManagerLabel = new JLabel("CoinManager"); 
        coinManagerLabel.setFont(new Font("CO59",Font.PLAIN, 36 ));
        labelPanel.add(coinManagerLabel);
   
        //Public API key 
        JPanel publicPanel = new JPanel(); 
      
        JLabel APIPublicLabel = (JLabel) addVisual(publicPanel, "label", "API public key"); 
        APIPublicField = new JPasswordField(30); 
        APIPublicField.enableInputMethods(rootPaneCheckingEnabled);
        
        publicPanel.add(APIPublicField); 
        publicBrowse = (JButton) addVisual(publicPanel, "button", " Browse "); 
        publicBrowse.setActionCommand("Browse public");
        publicBrowse.addActionListener(root);
        
        //Secret API key 
        JPanel secretPanel = new JPanel(); 
        secretPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        JLabel APIsecretLabel = (JLabel) addVisual(secretPanel, "Label", "API secret key"); 
        APIsecretLabel.setSize(TXFdimension); 
        APISecretField = new JPasswordField(30);
        APISecretField.enableInputMethods(rootPaneCheckingEnabled);
        
        secretPanel.add(APISecretField); 
        secretBrowse = (JButton) addVisual(secretPanel, "button", " Browse "); 
        secretBrowse.setActionCommand("Browse secret");
        secretBrowse.addActionListener(root);
        
        //Secret Pass Phrase
        JPanel passPhrasePanel = new JPanel(); 
        
        JLabel passLabel = (JLabel) addVisual(passPhrasePanel, "label", "    Passphrase"); 
        passLabel.setSize(TXFdimension);
        passField = new JPasswordField(30); 
        passField.enableInputMethods(rootPaneCheckingEnabled);
        
        
        passPhrasePanel.add(passField); 
        passBrowse = (JButton) addVisual(passPhrasePanel, "button", " Browse ");
        passBrowse.setActionCommand("Browse pass");
        passBrowse.addActionListener(root);
        
        //Buttons
        JPanel buttonsPanel = new JPanel(); 
        
        JButton marketButton = (JButton) addVisual(buttonsPanel, "Button", "Start Connection");
        marketButton.addActionListener(root); 
        
        //center box layout. 
        centerPanel.add(labelPanel);
        centerPanel.add(Box.createGlue()); 
        
        centerPanel.add(publicPanel); 
        centerPanel.add(Box.createGlue()); 
        
        centerPanel.add(secretPanel);  
        centerPanel.add(Box.createGlue()); 
        
        centerPanel.add(passPhrasePanel); 
        centerPanel.add(Box.createGlue()); 
        
        centerPanel.add(buttonsPanel); 
        centerPanel.add(Box.createGlue()); 
        
        centerPanel.add(new JPanel()); 
        
        JPanel centerBack = new JPanel(); 
        
        centerBack.setLayout(new BoxLayout(centerBack, BoxLayout.Y_AXIS)); 
        centerBack.add(new JPanel()); 
        centerBack.add(centerPanel); 
        centerBack.add(new JPanel()); 
        
        root.add(northPanel, BorderLayout.NORTH); 
        root.add(leftPanel ,  BorderLayout.WEST); 
        root.add(centerBack, BorderLayout.CENTER); 
        root.add(rightPanel,  BorderLayout.EAST); 
        root.add(southPanel, BorderLayout.SOUTH);
      
        root.setVisible(rootPaneCheckingEnabled);
    }
    
    
    public String readFileLine(String filePath){
       String output = "";  
       
       if(filePath!= null){
       
            try{

                File file = new File(filePath);
                Scanner fileReader = new Scanner(file);
                output = fileReader.next(); 
                fileReader.close();

            }catch (FileNotFoundException e){
               JOptionPane.showConfirmDialog(root, e); 
            }

           return output; 
        }  
       
       return output; 
    }
    
    @Override
    public  void actionPerformed(ActionEvent event){
        
        switch(event.getActionCommand()){
            case("Browse public"):
                String publicKey = readFileLine(getFilePath()); 
                APIPublicField.setText(publicKey);
                break;
                
            case("Browse secret"):
                String secretKey = readFileLine(getFilePath()); 
                APISecretField.setText(secretKey);
                break;
                
            case("Browse pass"):
                String passKey = readFileLine(getFilePath()); 
                passField.setText(passKey); 
                break;
            case("Start Connection"):
                
                String pub, sec, pass; 
                
                pub = new String(APIPublicField.getPassword());
                sec = new String(APISecretField.getPassword()); 
                pass = new String(passField.getPassword()); 
                
                root.pyCaller = new PyCaller(pub,sec,pass); 
                
                String code = root.pyCaller.checkCredentials("NONE");
                
                if(code.equals("200")){
                    Market market = new Market(root); 
                    market.validateRoot();
                }else{
                    System.out.println("It didn't work: |" + code + "|"); 
                }
                
                break; 
                
        }
        
    }
}
