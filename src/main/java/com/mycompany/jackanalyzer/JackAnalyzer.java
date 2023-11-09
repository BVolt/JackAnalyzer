package com.mycompany.jackanalyzer;
import java.io.*;

public class JackAnalyzer {

    public static void main(String[] args) {
        String path = (args.length == 0) ? "tests/ExpressionLessSquare": args[0];
        if(path.split("\\.").length<=1){
            File dir = new File(path);
            File[] directory = dir.listFiles();
            for(File file: directory){
                if(file.getName().split("\\.")[1].equals("jack")){
                    translateFile(path+"/"+file.getName());
                }
            }
        }
        else
            translateFile(path);
    }
    
    public static void translateFile(String path){
        JackTokenizer tokenizer = new JackTokenizer(openInFile(path), openOutFile(path.split("\\.")[0]+"T.xml"));
        
        tokenizer.InitializeOut();
        tokenizer.advance();
        while(tokenizer.hasMoreTokens()){
//            System.out.println(tokenizer.token);
            tokenizer.writeToken();
            tokenizer.advance();
        }
        tokenizer.Finalize();
        
//        CompilationEngine engine = new CompilationEngine(openInFile(path.split("\\.")[0]+"T.xml"),openOutFile(path.split("\\.")[0]+".xml"));
    }
    
    public static BufferedReader openInFile(String path){
        try{
            return new BufferedReader(new FileReader(path));
        }catch(IOException e){}
        return null;
    }
    
    public static BufferedWriter openOutFile(String path){
        try{
            return new BufferedWriter(new FileWriter(path));
        }catch(IOException e){}
        return null;
    }
}
