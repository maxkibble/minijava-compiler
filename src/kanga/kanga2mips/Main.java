package kanga.kanga2mips;

import kanga.KangaParser;
import kanga.ParseException;
import kanga.TokenMgrError;
import kanga.syntaxtree.Node;
import kanga.visitor.GJDepthFirst;
import kanga.visitor.Kanga2MipsVisitor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		try {
			InputStream in = new FileInputStream("TreeVisitor.kg");
			//PrintStream out = System.out;
			PrintStream out = new PrintStream("TreeVisitor.asm");
			Scanner sc = new Scanner(in);
			String code = "";
			while(sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}
			out.print(Kan2Mips.kan2mips(code));
		} catch (TokenMgrError e) {
			// Handle Lexical Errors
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}