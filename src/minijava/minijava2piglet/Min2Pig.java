package minijava.minijava2piglet;

import minijava.MiniJavaParser;
import minijava.symboltable.MClassList;
import minijava.symboltable.MPiglet;
import minijava.syntaxtree.Node;
import minijava.visitor.MinijavaToPigletVisitor;
import minijava.visitor.SymbolTableVisitor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by maxkibble on 2015/12/15.
 */
public class Min2Pig {
    public static String mini2pig(String s) {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Node root = new minijava.syntaxtree.NodeToken("rr");
        try {
            root = new MiniJavaParser(in).Goal();
        } catch (minijava.ParseException e) {
            e.printStackTrace();
        }
        root.accept(new SymbolTableVisitor(), MClassList.instance);
        MClassList.instance.completeClass();
        MClassList.instance.allocTemp(20);
        MPiglet ans = (MPiglet) root.accept(new MinijavaToPigletVisitor(), MClassList.instance);
        return ans.getCode().toString();
    }
}