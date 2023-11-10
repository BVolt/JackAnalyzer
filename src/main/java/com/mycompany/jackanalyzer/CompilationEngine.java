package com.mycompany.jackanalyzer;
import java.io.*;
import java.util.*;


public class CompilationEngine {
    private BufferedWriter outFile;
    private BufferedReader inFile;
    private String s = "";
    private String currToken;
    private String tokenType;
    private String line;
    private boolean firstLine;
    
    //Create a loop that looks for specific symbol to terminate function
    //recursive
    //think before reaching symbol may have to enact another function
    //Write Line assuming perfect however we should if the else print "expected ..."
    
    public CompilationEngine(BufferedReader iFile, BufferedWriter file){
        outFile = file;
        inFile = iFile;
        firstLine = true;
    }
    
    public void CompileClass(){
        readLine();
        readLine();
        writeLine("<class>");
            writeInputLine();
            writeInputLine();
            writeInputLine();
            while(currToken.equals("static") || currToken.equals("field")){
                CompileClassVarDec();
            }
            while(currToken.equals("constructor") || currToken.equals("function") || currToken.equals("method")){
//                System.out.println(currToken);
                CompileSubroutine();
            }
            writeInputLine();
        writeLine("</class>");
    }
    
    public void CompileClassVarDec(){
        writeLine("<classVarDec>");
            writeInputLine();
            writeInputLine();
            writeInputLine();
            while(currToken.equals(",")){
                writeInputLine();
                writeInputLine();
            }
            writeInputLine();
        writeLine("</classVarDec>");
    }
    
    //Do subs
    public void CompileSubroutine(){
        writeLine("<subroutineDec>");
            writeInputLine();
            writeInputLine();
            writeInputLine();
            writeInputLine();
            CompileParameterList();
            writeInputLine();
                writeLine("<subroutineBody>");
                writeInputLine();
                while(currToken.equals("var") || currToken.equals("let") || currToken.equals("if") || currToken.equals("while")|| currToken.equals("do")|| currToken.equals("return")){
                    if(currToken.equals("var")) CompileVarDec();
                    else CompileStatements();
                }
                writeInputLine();//}
                writeLine("</subroutineBody>");
        writeLine("</subroutineDec>");
    }
    
    public void CompileParameterList(){
        if(currToken.equals(")")){
            writeLine("<parameterList>");
            writeLine("</parameterList>");
        }
        else{
            writeLine("<parameterList>");
                while(currToken.equals("int") || currToken.equals("char") || currToken.equals("boolean") || tokenType.equals("indentifier")){
                    writeInputLine();
                    writeInputLine();
                    if(currToken.equals(",")){
                        writeInputLine();
                    }
                }
            writeLine("</parameterList>");
        }
    }
    
    public void CompileVarDec(){
        writeLine("<varDec>");
        writeInputLine();
        writeInputLine();
        writeInputLine();
        while(currToken.equals(",")){
                writeInputLine();
                writeInputLine();
        }
        writeInputLine();
        writeLine("</varDec>");
    }
    
    public void CompileStatements(){
        if(currToken.equals("}")){
            writeLine("<statements>");
            writeLine("</statements>");
        }
        else{
        writeLine("<statements>");
                while(currToken.equals("let") || currToken.equals("if") || currToken.equals("while")|| currToken.equals("do")|| currToken.equals("return")){
                    switch(currToken){
                        case "let" -> {CompileLet();}
                        case "do" -> {CompileDo();}
                        case "if" -> CompileIf();
                        case "while" -> CompileWhile();
                        case "return" -> CompileReturn();
                        default -> {}
                    }
                }
        writeLine("</statements>");
        }
    }
    
    public void CompileDo(){
        writeLine("<doStatement>");
        writeInputLine(); //do
        writeInputLine(); //Subroutine name or className or varName
        CompileSubroutineCall();
        writeInputLine(); //;
        writeLine("</doStatement>");
    }
    
    public void CompileLet(){
        writeLine("<letStatement>");
        while(!currToken.equals(";")){
            if(currToken.equals("[") || currToken.equals("=")){
                writeInputLine();
                CompileExpression();
            }
            else writeInputLine();
        }
        writeInputLine();
        writeLine("</letStatement>");
    }
    
    public void CompileWhile(){
        writeLine("<whileStatement>");
        writeInputLine();
        writeInputLine();
        CompileExpression();
        writeInputLine();
        writeInputLine();
        CompileStatements();
        writeInputLine();
        writeLine("</whileStatement>");
    }
    
    public void CompileReturn(){
        writeLine("<returnStatement>");
        writeInputLine();
        if(!currToken.equals(";")){
            CompileExpression();
        }
        writeInputLine();
        writeLine("</returnStatement>");
    }
    
    public void CompileIf(){
        writeLine("<ifStatement>");
        writeInputLine(); //if
        writeInputLine();//(
        CompileExpression(); //
        writeInputLine();//)
        writeInputLine();//{
        CompileStatements();
        writeInputLine();//}
        if(currToken.equals("else")){
            writeInputLine();//else
            writeInputLine();//{
            CompileStatements();
            writeInputLine();//}
        }
        writeLine("</ifStatement>");
    }
    
    public void CompileExpression(){
        writeLine("<expression>");
        String temp = currToken;
        CompileTerm();
        while(isOp()){
            writeInputLine();
            CompileTerm();
        }
        writeLine("</expression>");
    }
    
    public void CompileTerm(){
        writeLine("<term>");
            if(currToken.equals("(")){
                writeInputLine();
                CompileExpression();
                writeInputLine();
            }
            else if(currToken.equals("~")||currToken.equals("-")){
                writeInputLine();
                CompileTerm();
            }
            else{
                writeInputLine();
                if(currToken.equals("(") || currToken.equals(".")){
                    CompileSubroutineCall();
                }else if(currToken.equals("[")){
                    writeInputLine();
                    CompileExpression();
                    writeInputLine();
                }
                else{}
                
            }
        writeLine("</term>");
    }
    
    public void CompileSubroutineCall(){
        if(currToken.equals(".")){
            writeInputLine(); //.
            writeInputLine(); //subRoutineName
        }
        writeInputLine(); //(
        if(currToken.equals(")"))writeLine("<expressionList>\n</expressionList>");
        else{
            writeLine("<expressionList>");
            CompileExpression();
            while(currToken.equals(",")){
                writeInputLine();
                CompileExpression();
            }
            writeLine("</expressionList>");
        }
        writeInputLine();//)
    }
    
    
    public void readLine(){
        try{
            line = inFile.readLine();
            if(!firstLine && !line.equals("</tokens>")){
//                System.out.println(line);
                tokenType = line.split("<|>")[1];
                currToken = line.split("<|>")[2];
                currToken = currToken.replaceAll(" ", "");
            }
            else firstLine = false;
        }catch(IOException e){
        }
    }
    
    
    
    public void writeLine(String lin){
        try{
            outFile.write(lin+"\n");
        }catch(IOException e){
        
        }
//        System.out.println(lin);
    }
    
        public void writeInputLine(){
        try{
            outFile.write(line+"\n");
            readLine();
        }catch(IOException e){
        
        }
//        System.out.println(line);
//        readLine();

    }
    
    public void closeFiles(){
        try{
            inFile.close();
            outFile.close();
        }catch(IOException e){
        
        }
    }
    
    public boolean isOp(){
        return switch(currToken){
            case "+", "-", "*", "/", "&amp;", "|", "&lt;", "&gt;", "=" -> true;
            default -> false;
        };
    }
}
