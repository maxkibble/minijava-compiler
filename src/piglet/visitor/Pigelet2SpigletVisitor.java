package piglet.visitor;

import piglet.piglet2spiglet.MSpiglet;
import piglet.syntaxtree.*;

import java.util.Enumeration;

/**
 * Created by maxkibble on 11/7/15.
 */
public class Pigelet2SpigletVisitor extends GJNoArguDepthFirst<Object> {
    private int tempNum;

    public Pigelet2SpigletVisitor(int num) {
        tempNum = num;
    }

    public String getNextTemp() {
        return "TEMP " + (++tempNum);
    }
    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public MSpiglet visit(NodeList n) {
        MSpiglet _ret = new MSpiglet("");
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            _ret.appendCode((MSpiglet) e.nextElement().accept(this));
            _count++;
        }
        return _ret;
    }

    public MSpiglet visit(NodeListOptional n) {
        if ( n.present() ) {
            MSpiglet _ret = new MSpiglet("");
            int _count=0;
            for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                Node n1 = e.nextElement();

                //System.out.println((++_count) + " " + n1.toString());

                MSpiglet r1 = (MSpiglet)n1.accept(this);
                _ret.appendCode(r1);
                if (r1 != null && r1.getTemp() != null)
                    _ret.addTemp(r1.getTemp());
            }
            return _ret;
        }
        else
            return null;
    }

    public MSpiglet visit(NodeOptional n) {
        if ( n.present() ) {
            if(n.node instanceof Label) {
                return new MSpiglet(((Label) n.node).f0.tokenImage);
            }
            return (MSpiglet)n.node.accept(this);
        }
        else
            return null;
    }

    public MSpiglet visit(NodeSequence n) {
        MSpiglet _ret = new MSpiglet("");
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            _ret.appendCode((MSpiglet) e.nextElement().accept(this));
            _count++;
        }
        return _ret;
    }

    public MSpiglet visit(NodeToken n) { return null; }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> "MAIN"
     * f1 -> StmtList()
     * f2 -> "END"
     * f3 -> ( Procedure() )*
     * f4 -> <EOF>
     */
    public MSpiglet visit(Goal n) {
        MSpiglet _ret = new MSpiglet("");
        _ret.appendCode(new MSpiglet("MAIN"));
        n.f0.accept(this);
        _ret.appendCode((MSpiglet) n.f1.accept(this));
        n.f2.accept(this);
        _ret.appendCode(new MSpiglet("END"));
        _ret.appendCode((MSpiglet) n.f3.accept(this));
        n.f4.accept(this);
        return _ret;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    public MSpiglet visit(StmtList n) {
        MSpiglet _ret = (MSpiglet)n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> StmtExp()
     */
    public MSpiglet visit(Procedure n) {
        MSpiglet _ret = new MSpiglet(n.f0.f0.tokenImage + " [" + n.f2.f0.tokenImage + "]");
        _ret.appendCode(new MSpiglet("BEGIN"));
        n.f0.accept(this);
        n.f1.accept(this);
        n.f2.accept(this);
        n.f3.accept(this);
        MSpiglet exp = (MSpiglet) n.f4.accept(this);
        _ret.appendCode(exp);
        _ret.appendCode(new MSpiglet("RETURN " + exp.getTemp()));
        _ret.appendCode(new MSpiglet("END"));
        return _ret;
    }

    /**
     * f0 -> NoOpStmt()
     *       | ErrorStmt()
     *       | CJumpStmt()
     *       | JumpStmt()
     *       | HStoreStmt()
     *       | HLoadStmt()
     *       | MoveStmt()
     *       | PrintStmt()
     */
    public MSpiglet visit(Stmt n) {
        MSpiglet _ret = (MSpiglet)n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "NOOP"
     */
    public MSpiglet visit(NoOpStmt n) {
        MSpiglet _ret = new MSpiglet("NOOP");
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    public MSpiglet visit(ErrorStmt n) {
        MSpiglet _ret = new MSpiglet("ERROR");
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Exp()
     * f2 -> Label()
     */
    public MSpiglet visit(CJumpStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp = (MSpiglet)n.f1.accept(this);
        n.f2.accept(this);
        _ret.appendCode(exp);
        _ret.appendCode(new MSpiglet("CJUMP " + exp.getTemp() + " " + n.f2.f0.tokenImage));
        return _ret;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    public MSpiglet visit(JumpStmt n) {
        MSpiglet _ret = new MSpiglet("JUMP " + n.f1.f0.tokenImage);
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Exp()
     * f2 -> IntegerLiteral()
     * f3 -> Exp()
     */
    public MSpiglet visit(HStoreStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = (MSpiglet)n.f1.accept(this);
        n.f2.accept(this);
        MSpiglet exp2 = (MSpiglet)n.f3.accept(this);
        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        _ret.appendCode(new MSpiglet("HSTORE " + exp1.getTemp() + " " + n.f2.f0.tokenImage + " " + exp2.getTemp()));
        return _ret;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Exp()
     * f3 -> IntegerLiteral()
     */
    public MSpiglet visit(HLoadStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = (MSpiglet)n.f1.accept(this);
        MSpiglet exp2 = (MSpiglet)n.f2.accept(this);
        n.f3.accept(this);
        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        _ret.appendCode(new MSpiglet("HLOAD " + exp1.getTemp() + " " + exp2.getTemp() + " " + n.f3.f0.tokenImage));
        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    public MSpiglet visit(MoveStmt n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = (MSpiglet)n.f1.accept(this);
        MSpiglet exp2 = (MSpiglet)n.f2.accept(this);
        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        _ret.appendCode(new MSpiglet("MOVE " + exp1.getTemp() + " " + exp2.getTemp()));
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> Exp()
     */
    public MSpiglet visit(PrintStmt n) {
        n.f0.accept(this);
        MSpiglet _ret = new MSpiglet("");
        MSpiglet exp = (MSpiglet) n.f1.accept(this);
        _ret.appendCode(exp);
        _ret.appendCode(new MSpiglet("PRINT " + exp.getTemp()));
        return _ret;
    }

    /**
     * f0 -> StmtExp()
     *       | Call()
     *       | HAllocate()
     *       | BinOp()
     *       | Temp()
     *       | IntegerLiteral()
     *       | Label()
     */
    public MSpiglet visit(Exp n) {
        MSpiglet _ret = (MSpiglet)n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> Exp()
     * f4 -> "END"
     */
    public MSpiglet visit(StmtExp n) {
        n.f0.accept(this);
        MSpiglet _ret = new MSpiglet("");
        _ret.appendCode((MSpiglet) n.f1.accept(this));
        n.f2.accept(this);
        MSpiglet exp = (MSpiglet) n.f3.accept(this);
        n.f4.accept(this);
        _ret.appendCode(exp);
        _ret.setTemp(exp.getTemp());
        return _ret;
    }

    /**
     * f0 -> "CALL"
     * f1 -> Exp()
     * f2 -> "("
     * f3 -> ( Exp() )*
     * f4 -> ")"
     */
    public MSpiglet visit(Call n) {
        MSpiglet _ret = new MSpiglet("");
        n.f0.accept(this);
        MSpiglet exp1 = (MSpiglet)n.f1.accept(this);
        n.f2.accept(this);
        MSpiglet exp2 = (MSpiglet)n.f3.accept(this);
        n.f4.accept(this);

        String name = getNextTemp();
        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        String callCode = "CALL " + exp1.getTemp() + "(";
        for(String temp : exp2.getTempList()) {
            callCode += temp + " ";
        }
        callCode += ')';
        _ret.appendCode(new MSpiglet("MOVE " + name + "\n" + callCode));
        _ret.setTemp(name);
        return _ret;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> Exp()
     */
    public MSpiglet visit(HAllocate n) {
        MSpiglet exp = (MSpiglet)n.f1.accept(this);
        MSpiglet _ret = exp;
        String name = getNextTemp();
        _ret.appendCode(new MSpiglet("MOVE " + name + " HALLOCATE " + exp.getTemp()));
        _ret.setTemp(name);
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Exp()
     * f2 -> Exp()
     */
    public MSpiglet visit(BinOp n) {
        MSpiglet _ret = new MSpiglet("");
        MSpiglet op = (MSpiglet)n.f0.accept(this);
        MSpiglet exp1 = (MSpiglet)n.f1.accept(this);
        MSpiglet exp2 = (MSpiglet)n.f2.accept(this);
        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        String name = getNextTemp();
        _ret.appendCode(new MSpiglet("MOVE " + name + " " + op.getOp() + " " + exp1.getTemp() + " " + exp2.getTemp()));
        _ret.setTemp(name);
        return _ret;
    }

    /**
     * f0 -> "LT"
     *       | "PLUS"
     *       | "MINUS"
     *       | "TIMES"
     */
    public MSpiglet visit(Operator n) {
        MSpiglet _ret = new MSpiglet("");
        _ret.setOpType(n.f0.which);
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    public MSpiglet visit(Temp n) {
        MSpiglet _ret = new MSpiglet("");
        String name = "TEMP " + n.f1.f0.tokenImage;
        _ret.setTemp(name);
        n.f0.accept(this);
        n.f1.accept(this);
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public MSpiglet visit(IntegerLiteral n) {
        MSpiglet _ret = new MSpiglet("");
        String name = getNextTemp();
        _ret.appendCode(new MSpiglet("MOVE " + name + " " + n.f0.tokenImage));
        _ret.setTemp(name);
        n.f0.accept(this);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public MSpiglet visit(Label n) {
        MSpiglet _ret=new MSpiglet("");
        String name = getNextTemp();
        _ret.appendCode(new MSpiglet("MOVE " + name + " " + n.f0.tokenImage));
        _ret.setTemp(name);
        n.f0.accept(this);
        return _ret;
    }
}
