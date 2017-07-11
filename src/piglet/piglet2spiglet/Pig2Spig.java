package piglet.piglet2spiglet;

import piglet.syntaxtree.*;
import piglet.ParseException;
import piglet.PigletParser;
import piglet.visitor.Pigelet2SpigletVisitor;
import piglet.visitor.PigletTempNumVisitor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by maxkibble on 2015/12/15.
 */
public class Pig2Spig {
    public static String pig2spig(String s) {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Node root = new NodeToken("rr");
        try {
            root = new PigletParser(in).Goal();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PigletTempNumVisitor v = new PigletTempNumVisitor();
        root.accept(v);
        Pigelet2SpigletVisitor t = new Pigelet2SpigletVisitor(v.getTempNum());
        return  ((MSpiglet) root.accept(t)).getCode().toString();
    }
}
