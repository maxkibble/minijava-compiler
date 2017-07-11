package minijava.minijava2piglet;

import com.sun.org.apache.xpath.internal.SourceTree;
import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.symboltable.MClass;
import minijava.symboltable.MClassList;
import minijava.symboltable.MPiglet;
import minijava.syntaxtree.Node;
import minijava.visitor.GJDepthFirst;
import minijava.visitor.MinijavaToPigletVisitor;
import minijava.visitor.SymbolTableVisitor;

import java.awt.color.ICC_ColorSpace;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
    	try {
			//InputStream is = System.in;
			InputStream is = new FileInputStream("./tests/check/TreeVisitor.java");
			PrintStream out = new PrintStream("TreeVisitor.pg");
			String code = "";
			Scanner sc = new Scanner(is);
			while (sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}
			out.println(Min2Pig.mini2pig(code));
			/*
    		 * TODO: Implement your own Visitors and other classes.
    		 * 
    		 */
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