/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.packages;
import javax.swing.JFrame; 

/**
 *
 * @author jlarator
 */
public class CoinMngrFrontEnd {
    
    public static void main(String[] args) {
        
        CoinMngrFrontEnd main = new CoinMngrFrontEnd(); 
        main.execute();
    }
    
    public void execute(){
        System.out.println("Starting execute"); 
        
        Window window = new Window(); 
        EntryMenu startMenu = new EntryMenu(window); 
    }
    
    
}
