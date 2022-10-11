/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.packages;

import java.util.*; 

/**
 *
 * @author jlarator
 */
public class searchTree {
    
    char[] alphabet;
    private static HashMap<Character, Node> keystone;
    private static HashMap<String, String> lowToUp; 
    
    private final static String FAILURETOKEN = "|"; 

    static class Node implements Comparable<Node>{
        /**
         * this  represents a token in the tree. Each node/token can have as many
         * as 52
         *
         * Data is the character this node represents.
         * Kids will hold the kids to this node.
         */
        Character data;
        HashMap<Character, Node> kids;
        int size;
        public  Node(Character c){
            size = 0;
            data = c;
            kids = new HashMap<>();
        }

        public void displayKids(){
            Set<Character> keys  = kids.keySet();
            for(Character key: keys){
            }
        }

        /**
         * WIll return every possible combination of words that are made from each brach of itself and
         * its sub-trees.
         * @param output a list with all the words.
         * @param cumulative The first characters of the word that you are searching for.
         * @return list of words that can be made from each brach of itself and its
         * sub-trees. 
         */
        public ArrayList<String> getChildrenStrings(ArrayList<String> output, String cumulative){
            
            Set<Character> keys = kids.keySet();

            String original = cumulative;
            for(Character key: keys){
                Node kid = kids.get(key);
                cumulative = original;

                if(kid.size < 1){
                    String value = lowToUp.get(cumulative); 
                    output.add(value);
                }else{
                    if(!kid.equals(FAILURETOKEN)){
                        cumulative += kid;
                        kid.getChildrenStrings(output, cumulative);
                    }
                }
            }
            return output;
        }
        
        /**
         * Adds character by character to three
         * @param kid character to add
         * @return false if character is already a child of parent node. 
         */
        public boolean addKid(Node kid){
            if(!kids.containsKey(kid.data)){
                kids.put(kid.data, kid);
                size++;
                return true;
            }
            return false;
        }

        public Node getKid(Character c){
            return kids.get(c);
        }

        /**
         * Will add a branch representing a word to this tree. It will not have duplicate branches.
         * @param arg
         */
        public void addWord(String arg){
           // will get a substring of original example: [o]riginal -> [r]iginal -> [i]ginal
            if(arg.isEmpty()) return;
            
            char firstChar = arg.charAt(0);
            Node firstChild = new Node(firstChar);

            boolean added = addKid(firstChild);

            if(arg.length() > 1){
                String trailing = arg.substring(1);
                if(added){
                    addDescendants(firstChild, trailing);
                }else{
                    getKid(firstChar).addWord(trailing);
                }
            }
        }
        /**
         * The adds and entire word to as a branch (character by character), where
         * each character is a child to the previous character. 
         * @param father
         * @param subArg 
         */
        public void addDescendants(Node father, String subArg){

            if(subArg.length() < 1) return ;
            
            char firstchar = subArg.charAt(0);
            Node newNode = new Node(firstchar);

            String sub = subArg.substring(1);
            father.addKid(newNode);
            addDescendants(newNode, sub);

        }

        @Override
        public int compareTo(Node kid){
            return data.compareTo(kid.data);
        }

        @Override
        public String toString(){
            return String.valueOf(data);
        }

    }

    /**
     * Constructor
     */
    public searchTree(){
        
        System.out.println("Search Tree constructor Called"); 
        alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        keystone = new HashMap<>(); //Rootlist 
        lowToUp = new HashMap<>(); 
        
        for(char c: alphabet){
            keystone.put((Character) c, new Node(c));
        }
    }

    /**
     * Returns words associated with a search query
     * @param arg query
     * @return String array containing each word associated with a query
     */
    public ArrayList<String> searchResults(String arg){
        arg = arg.toLowerCase(); 
        Character key = arg.charAt(0);
        if(!keystone.containsKey(key)) return new ArrayList<>();
        Node father = keystone.get(key);
        return search(father, arg);
    }

    public ArrayList<String> search(Node firstChild, String arg){
        return searchChildren(firstChild, arg.substring(1), firstChild.toString());
    }

    public ArrayList<String> searchChildren(Node father, String arg, String accumulative){
        
        ArrayList<String> output = new ArrayList<>();

        father.displayKids();

        if(arg.isEmpty()){
            father.getChildrenStrings(output, accumulative);

        }else{
            char firstChar = arg.charAt(0);
            Node newFather = father.getKid(firstChar);
            if(newFather == null) return output;
            accumulative += newFather;
            output = searchChildren(newFather, arg.substring(1), accumulative);
        }
        return output;
    }

    public void addWord(String word){
        if(word.isEmpty()) return;
        lowToUp.put(word.toLowerCase(), word);
        
        word += FAILURETOKEN;
        word = word.toLowerCase();
        
        char indexChar = word.charAt(0);

        String sub = word.substring(1);
        keystone.get(indexChar).addWord(sub);
    }

    static void print(String arg){
        System.out.println(arg);
    }

}
