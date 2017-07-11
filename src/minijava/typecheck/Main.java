package minijava.typecheck;

import minijava.MiniJavaParser;
import minijava.ParseException;
import minijava.TokenMgrError;
import minijava.symboltable.MClassList;
import minijava.syntaxtree.Node;
import minijava.visitor.GJDepthFirst;
import minijava.visitor.SymbolTableVisitor;
import minijava.visitor.TypeCheckVisitor;
import sun.security.jca.GetInstance;
import minijava.symboltable.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Main { 
 
    public static void main(String[] args) {
    	try {
			//InputStream is = System.in;
			InputStream is = new FileInputStream("./tests/check/test100.java");
			Node root = new MiniJavaParser(is).Goal();
			root.accept(new SymbolTableVisitor(),MClassList.instance);
			//System.out.println("Symbol table built");
			root.accept(new TypeCheckVisitor(), MClassList.instance);
			//System.out.println("Type checking finished");
			if(!PrintError.hasOut) System.out.println("Program type checked successfully");
			else System.out.println("Type error");
		}
    	catch(TokenMgrError e){
    		//Handle Lexical Errors
    		e.printStackTrace();
    	}
    	catch (ParseException e){
    		//Handle Grammar Errors
    		e.printStackTrace();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
}