package com.mycompany.jackanalyzer;

import java.io.*;

public class JackTokenizer {
    private BufferedReader inFile;
    private BufferedWriter outFile;
    private String line = "";
    private String token;
    private String type;
    private boolean withinComment = false;
    private int currIndex = 0;
    
    public JackTokenizer(BufferedReader file, BufferedWriter oFile){
        inFile = file;
        outFile = oFile;
        token = null;
    }
    
    public boolean hasMoreTokens(){
        return line != null;
    }
    
    public boolean hasMoreLines(){
        return line != null;
    }
    
    public Boolean skipLine(){
        if(line.length()==0) return true;
        return line.charAt(0) == '/' && line.charAt(1) == '/';
    }
    
    public void advance(){
        if(currIndex >= line.length()){
            currIndex = 0;
            try {
                line = inFile.readLine();            
                while (hasMoreLines() && skipLine()) {
                    line = inFile.readLine();
                }
                if(hasMoreLines()){
                    line = line.split("//")[0];
                    getNextTokenInLine();
                }
            } catch (IOException e) {
            }
        }
        else{
            getNextTokenInLine();
        }
    }
    
    public void getNextTokenInLine(){
        char sym = line.charAt(currIndex);
        int poo = sym;
        int notp = ' ';
//        System.out.print("Line = ");
//        System.out.println(line);
        if(withinComment){
            while(line.charAt(currIndex)!= '*' || line.charAt(currIndex+1) != '/'){
                currIndex++;
                if(currIndex==line.length()-1)break;
            }
            currIndex++;
            if(currIndex==line.length())advance();
            else{ 
                currIndex++;
                withinComment = false;
            }
        }
        else{
            switch(sym){
                case ' ', '\t' ->{
                        while(line.charAt(currIndex) == ' '| line.charAt(currIndex) == '\t'){
                            currIndex++;
                            if(currIndex == line.length()) break;
                        }
                        advance();
                    break;
                }
                case '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~' -> {
                    if(sym == '/' && line.charAt(currIndex+1) == '*') {
                        withinComment = true;
                        token = null;
                    }
                    else{
                        token = Character.toString(sym);
                        type = "SYMBOL";
                    }
                    currIndex++;
                    break;
                }
                case '"' -> {
                    currIndex++;
                    token = "";
                    while(line.charAt(currIndex) != '"'){
                        token += Character.toString(line.charAt(currIndex));
                        currIndex++;
                    }
                    type = "STRING_CONST";
                    currIndex++;
                    break;
                }
                default  -> {
                    token = "";
                    if(Character.isDigit(sym)){
                        while(line.charAt(currIndex) != ' ' && !isSymbol(line.charAt(currIndex))){
                            token += Character.toString(line.charAt(currIndex));
                            if (!isSymbol(line.charAt(currIndex)))currIndex++;
                        }
                        type = "INT_CONST";
                        break;
                    }
                    while(line.charAt(currIndex) != ' ' && !isSymbol(line.charAt(currIndex))){
                        token += Character.toString(line.charAt(currIndex));
                        currIndex++;
                    }
                    if(isSymbol(line.charAt(currIndex))){currIndex--;}
                    if(isKeyWord()){type = "KEYWORD";}
                    else {type = "IDENTIFIER";}
                    currIndex++;
                    break;
                }
            }
        }
    if(token == null) advance();
    }
    
    public String tokenType(){
        return type;
    }
    
    public String keyWord(){
        return token;
    }
    
    public char symbol(){
        return token.charAt(0);
    }
    
    public String identifier(){
        return token;
    }
    
    public int intVal(){
        return Integer.parseInt(token);
    }
    
    public String stringVal(){
        return token;
    }
    
    public boolean isInt(){
        try{
            int temp = Integer.parseInt(token);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
    
    public boolean isKeyWord(){
        return switch(token){
            case "class", "constructor", "function", 
                "method", "field", "static", "var", "int", 
                "char", "boolean", "void", "true", "false", "null", 
                "this", "let", "do", "if", "else", "while", "return" -> true;
            default -> false;
        }; 
    }
    
    public boolean isSymbol(char c) {
        return switch(c){
            case '{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~' -> true;
            default -> false;
        };
    }
    
    public void writeToken(){
        String label = "";
        String toke = "";
        switch(tokenType()){
            case "KEYWORD" -> {label="keyword"; toke = token;break;}
            case "IDENTIFIER" -> {label="identifier"; toke = token;break;}
            case "INT_CONST" -> {label="integerConstant"; toke = token; break;}
            case "STRING_CONST" -> {label="stringConstant"; toke = token; break;}
            case "SYMBOL"->{
                label = "symbol";
                switch(token.charAt(0)){
                    case '<'-> toke = "&lt;";
                    case '>'-> toke = "&gt;";
                    case '"'-> toke = "&quot;";
                    case '&'-> toke = "&amp;";
                    default -> toke = token;
                }
                break;
            }
            default ->{break;}
        }
        String fstring = String.format("<%s> %s </%s>", label, toke, label);
        writeLine(fstring);
    }
    
    public void InitializeOut(){
        writeLine("<tokens>");
    }
    
    public void Finalize(){
        writeLine("</tokens>");
        closeFiles();
    }
    
    public void writeLine(String line){
        try{
            outFile.write(line+"\n");
        }catch(IOException e){
        
        }
    }
    
    public void closeFiles(){
        try{
            inFile.close();
            outFile.close();
        }catch(IOException e){
        
        }
    }
}
