package kanga.kanga2mips;

import kanga.KangaParser;
import kanga.syntaxtree.Node;
import kanga.syntaxtree.NodeToken;
import kanga.visitor.Kanga2MipsVisitor;
import kanga.ParseException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by maxkibble on 2015/12/15.
 */
public class Kan2Mips {
    public static String kan2mips(String s) {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Node root = new NodeToken("rr");
        try {
            root = new KangaParser(in).Goal();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        Kanga2MipsVisitor v = new Kanga2MipsVisitor();
        Environment env = new Environment();
        // Traverse the Abstract Grammar Tree
        root.accept(v, env);
        return env.mipsCode.toString();
    }
}
