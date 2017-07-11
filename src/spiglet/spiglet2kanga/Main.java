package spiglet.spiglet2kanga;

import spiglet.ParseException;
import spiglet.SpigletParser;
import spiglet.TokenMgrError;
import spiglet.syntaxtree.Node;
import spiglet.visitor.GJDepthFirst;
import spiglet.visitor.LivenessAnalysisVisitor;
import spiglet.visitor.Spiglet2KangaVisitor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;


public class Main {
 
    public static void main(String[] args) {
    	try {
			//InputStream in = System.in;
			InputStream in = new FileInputStream("QuickSort.spg");
			PrintStream out = System.out;
    		//PrintStream out = new PrintStream("QuickSort.kg");
			Scanner sc = new Scanner(in);
			String code = "";
			while (sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}
			out.println(Spg2Kan.spg2kan(code));
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