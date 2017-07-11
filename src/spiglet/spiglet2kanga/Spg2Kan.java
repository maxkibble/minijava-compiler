package spiglet.spiglet2kanga;

import spiglet.ParseException;
import spiglet.SpigletParser;
import spiglet.syntaxtree.NodeToken;
import spiglet.syntaxtree.Node;
import spiglet.visitor.LivenessAnalysisVisitor;
import spiglet.visitor.Spiglet2KangaVisitor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by maxkibble on 2015/12/15.
 */
public class Spg2Kan {
    public static String spg2kan(String s) {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Node root = new NodeToken("rr");
        try {
            root = new SpigletParser(in).Goal();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LivenessAnalysisVisitor v1 = new LivenessAnalysisVisitor();
        Context context = new Context();
        root.accept(v1,context);
        context.alloc();
        Spiglet2KangaVisitor v2 = new Spiglet2KangaVisitor();
        root.accept(v2,context);
        return context.kangaCode.toString();
    }
}
