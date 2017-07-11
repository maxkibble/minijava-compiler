package piglet.piglet2spiglet;


import minijava.symboltable.MPiglet;
import piglet.ParseException;
import piglet.PigletParser;
import piglet.TokenMgrError;
import piglet.syntaxtree.Node;
import piglet.visitor.GJDepthFirst;
import piglet.visitor.Pigelet2SpigletVisitor;
import piglet.visitor.PigletTempNumVisitor;

import java.io.*;
import java.util.Scanner;


public class Main { 
 
    public static void main(String[] args) {
    	try {
			//InputStream in = System.in;
    		InputStream in = new FileInputStream("TreeVisitor.pg");
			PrintStream out = new PrintStream("TreeVisitor.spg");
			//PrintStream out = System.out;
			Scanner sc = new Scanner(in);
			String SpigletCode = "";
			while(sc.hasNext()) {
				SpigletCode += sc.nextLine() + "\n";
			}
			out.println(Pig2Spig.pig2spig(SpigletCode));
		}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
}