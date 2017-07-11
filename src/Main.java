import kanga.TokenMgrError;
import kanga.kanga2mips.Kan2Mips;
import piglet.piglet2spiglet.Pig2Spig;

import java.io.*;
import java.util.Scanner;
import minijava.minijava2piglet.*;
import spiglet.spiglet2kanga.Spg2Kan;

public class Main {
	public static void main(String[] args) {
		try {
			InputStream in = new FileInputStream("tests/MoreThan4.java");
			//PrintStream out = new PrintStream("MoreThan4.asm");
			//InputStream in = System.in;
			PrintStream out = System.out;
			Scanner sc = new Scanner(in);
			String code = "";
			while(sc.hasNext()) {
				code += sc.nextLine() + "\n";
			}
			code = Min2Pig.mini2pig(code);
			code = Pig2Spig.pig2spig(code);
			code = Spg2Kan.spg2kan(code);
			code = Kan2Mips.kan2mips(code);
			out.println(code);
		}
		catch (TokenMgrError e) {
			// Handle Lexical Errors
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}