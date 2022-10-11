/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.packages;

import java.util.HashMap;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author jlarator
 */
public class PyCaller {
    
    private static String publicKey = null; 
    private static String secretKey = null;
    private static String passKey = null;
    
    private static ProcessBuilder terminal = new ProcessBuilder(); 
    private static final String pyDirectory =  System.getProperty("user.dir") + "/pyFolder";
    private static final String py3 = "python3"; 
    private static BufferedReader pyOutput;
    
    private static HashMap<String, String[]> prefixRates; 
    private static HashMap<String, String[]> nameRates; 
    
    private static HashMap<String, String> lowerToUpper;
    
    /**
     * Used to execute and retrieve the output from python scripts. 
     * @param pub public key from Coinbase's API
     * @param secret secret Key from Coinbase's API
     * @param pass password form Coinbase's API
     */
    public PyCaller(String  pub, String secret, String pass){
        System.out.println("pyFolder Path: " + pyDirectory); 
        publicKey = pub; 
        secretKey = secret; 
        passKey = pass; 
        prefixRates = new HashMap(); 
        nameRates = new HashMap(); 
        
    }
    
    public PyCaller(){
        
        System.out.println("\nPyCaller constructor called"); 
        prefixRates = new HashMap(); 
        nameRates = new HashMap(); 
    }
    
    public void setPublic(String key){
        publicKey = key;
    }
    
    public void setSecret(String key){
        secretKey = key; 
    }
    
    public void setPass(String key){
        passKey = key; 
    }
    
    public String getPublic(){
        return publicKey; 
    }
    
    public String getSecret(){
        return secretKey; 
    }
    
    public String getPass(){
        return passKey;
    }
    
    /**
     * Runs a python script that takes one argument. 
     * @param script Script path
     * @param argOne Argument for the script
     * @return script output
     */
    public String pyRun(String script, String argOne){
        String scriptPath = pyDirectory + script; 
        terminal.command(py3, scriptPath, publicKey, secretKey, passKey, argOne);
        
        return pyOutput(); 
    }
    /**
     * Runs python script
     * @param script script path
     * @return script output 
     */
    public String pyRun(String script){
        String scriptPath = pyDirectory + script;
        System.out.println("Running: " + scriptPath); 
        terminal.command(py3, scriptPath, publicKey, secretKey, passKey); 
        
        return pyOutput();  
        
    }
    
    /**
     * captures a python scripts' output from the console. 
     * @return 
     */
    public String pyOutput(){
        
        String output =""; 
        try{
        
            Process process = terminal.start(); 

            pyOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String data; 

            while((data = pyOutput.readLine()) != null){
                output +=  data + "\n"; 
            }
            return output; 
            
        }catch (Exception e){
            System.out.println(e.toString());
        }
        
        return output;
    }
    
    public String getCoinOrders(String coin){
        return pyRun("/getCoinOrders.py", coin); 
    }
    
    public String getFills(String coin){
        
        return pyRun("/getFills.py", coin); 
    }
    
    public String placeBuyOrder(String OrderInfo){
        return pyRun("/placeBuyOrder.py", OrderInfo); 
    }
    
    public String placeSellOrder(String OrderInfo){
        return pyRun("/placeSellOrder.py", OrderInfo); 
    }
    
    public String cancelOrder(String orderId){
        return pyRun("/cancelOrder.py", orderId); 
    }
    
    public String getRates(){
        /**
         * Uses coinbase pro API
         */
        return pyRun("/CoinbasePriceIndex.py");
    }
    
    /**
     * gets Portfolio and puts it into a hashmap. 
     * @return 
     */
    public HashMap<String, String> getPortfolioMap(){
        
        String[] portfolio = getPortfolio().split("\n"); 
        
        HashMap<String, String> output = new HashMap<>(); 
        
        for(int i=0; i < portfolio.length ; i++){
            String[] info = portfolio[i].split("#"); 
            String prefix = info[0]; 
            String balance = info[1]; 
            
            output.put(prefix, balance); 
        }
        
        return output; 
    }
    
    public String getPortfolio(){
        /*
        Uses coinbase Authentication
        */
        return pyRun("/getPortFolio.py"); 
    }
    
    public String updatedRates(){
        return pyRun("/updatedRates.py"); 
    }
    
    public String getCoinCost(String coinID){
        return pyRun("/getCoinCost.py", coinID); 
    }
    
    public String getProductRate(String coinID){
        return pyRun("/getProductRate.py", coinID); 
    }
    
    public String checkCredentials(String nana){
	System.out.println("checking creds: " + nana); 
        return pyRun("/CheckAuthCode.py").strip(); 
    }
    
    public final HashMap<String, String[]> marketMap(){
        return prefixRates; 
    }
   
    public final HashMap<String, String[]> nameMap(){
        return nameRates; 
    }
    
    public final void makeMaps(){
        /**
         * makes a symbol table
         * 
         * Key: Coin prefix
         * Value: {Name, Cost}
         * 
         * Key: Name
         * Value: {Coin prefix, Cost}
         */
        System.out.println("Making maps"); 
        String[] market = getRates().split("\n"); 

        String[] fields; 
        for (String item : market) {
            fields = item.split("#"); 
            
            String[] prefixValues = {fields[1], fields[2]}; 
            prefixRates.put(fields[0], prefixValues); 
            
            String[] nameValues = {fields[0], fields[2]}; 
            nameRates.put(fields[1], nameValues); 
        }

    }
    
    public final String[] getMarket(){
        return getRates().split("\n"); 
    }
    
}
