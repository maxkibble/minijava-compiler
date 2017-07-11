package spiglet.visitor;

import com.sun.org.apache.xalan.internal.xsltc.dom.SimpleResultTreeImpl;
import com.sun.prism.image.CompoundTexture;
import spiglet.spiglet2kanga.Context;
import spiglet.syntaxtree.*;

import java.lang.ref.SoftReference;
import java.util.Enumeration;

/**
 * Created by maxkibble on 11/30/15.
 */
public class Spiglet2KangaVisitor extends GJDepthFirst<String,Context> {
    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public String visit(NodeList n, Context argu) {
        String _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public String visit(NodeListOptional n, Context argu) {
        if ( n.present() ) {
            String _ret=null;
            int _count=0;
            for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                e.nextElement().accept(this,argu);
                _count++;
            }
            return _ret;
        }
        else
            return null;
    }

    public String visit(NodeOptional n, Context argu) {
        if ( n.present() )
            return n.node.accept(this,argu);
        else
            return null;
    }

    public String visit(NodeSequence n, Context argu) {
        String _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public String visit(NodeToken n, Context argu) { return null; }

    //
    // User-generated visitor methods below
    //

    public String getReg(int t) {
        if(t < 8) return "s"+t;
        return "t"+(t-8);
    }

    public void move(String str, Context argu) {
        if (argu.movetoReg == -1) return;
        int t = argu.movetoReg;
        argu.movetoReg = -1;

        String _ret;
        if (t < 18) {
            _ret = getReg(t);
            argu.appendKangeCode("MOVE " + _ret + " " + str);
        } else {
            argu.appendKangeCode("MOVE v0 " + str);
            argu.appendKangeCode("ASTORE SPILLEDARG " + (t+argu.currentMethod.spilledNum) + " v0");
        }
    }
    /**
     * f0 -> "MAIN"
     * f1 -> StmtList()
     * f2 -> "END"
     * f3 -> ( Procedure() )*
     * f4 -> <EOF>
     */
    public String visit(Goal n, Context argu) {
        String _ret=null;
        argu.appendKangeCode("MAIN[0][0][20]");
        argu.setMethod("MAIN");
        //n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        argu.appendKangeCode("END\n");
        //n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        //n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    public String visit(StmtList n, Context argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> StmtExp()
     */
    public String visit(Procedure n, Context argu) {
        String _ret=null;
        argu.setMethod(n.f0.f0.tokenImage);
        //String labelName = n.f0.accept(this,argu);
        String stmt = n.f0.f0.tokenImage + "[" + argu.currentMethod.paramNum + "]";
        stmt += "[";
        stmt += (argu.currentMethod.spilledNum + argu.currentMethod.spilledSize);
        stmt += "][20]";
        argu.appendKangeCode(stmt);
        for(int i = 0; i < Math.min(18,argu.currentMethod.spilledSize); i++) {
            argu.appendKangeCode("ASTORE SPILLEDARG " + (i+argu.currentMethod.spilledNum) + " " + getReg(i));
        }
        for(int i = 0; i < argu.currentMethod.paramNum; i++) {
            if(argu.currentMethod.getReg(i) == -1) continue;
            if(i < 4) {
                argu.appendKangeCode("MOVE " + getReg(argu.currentMethod.getReg(i)) + " a" + i);
            }
            else {
                argu.appendKangeCode("ALOAD " + getReg(argu.currentMethod.getReg(i)) + " SPILLEDARG " + (i-4));
            }
        }
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        //n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        for(int i = 0; i < Math.min(18,argu.currentMethod.spilledSize); i++) {
            argu.appendKangeCode("ALOAD " + getReg(i) + " SPILLEDARG " + (i+argu.currentMethod.spilledNum));
        }
        argu.appendKangeCode("END\n");
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
    public String visit(Stmt n, Context argu) {
        String _ret=null;
        argu.insideStat = true;
        argu.vReg = 0;
        argu.passingParam = -1;
        n.f0.accept(this, argu);
        argu.insideStat = false;
        return _ret;
    }

    /**
     * f0 -> "NOOP"
     */
    public String visit(NoOpStmt n, Context argu) {
        String _ret=null;
        argu.appendKangeCode("NOOP");
        //n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    public String visit(ErrorStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        argu.appendKangeCode("ERROR");
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Temp()
     * f2 -> Label()
     */
    public String visit(CJumpStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        String r1 = n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        argu.appendKangeCode("CJUMP " + r1 + " " + n .f2.f0.tokenImage);
        return _ret;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    public String visit(JumpStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        argu.appendKangeCode("JUMP " + n.f1.f0.tokenImage);
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Temp()
     * f2 -> IntegerLiteral()
     * f3 -> Temp()
     */
    public String visit(HStoreStmt n, Context argu) {
        String  _ret=null;
        //n.f0.accept(this, argu);
        String r1 = n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        String r3 = n.f3.accept(this, argu);
        argu.appendKangeCode("HSTORE " + r1 + " " + n.f2.f0.tokenImage + " " + r3);
        return _ret;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Temp()
     * f3 -> IntegerLiteral()
     */
    public String visit(HLoadStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        String r2 = n.f2.accept(this, argu);
        int t = Integer.valueOf(n.f1.f1.f0.tokenImage);
        t = argu.currentMethod.getReg(t);
        if(t < 18) {
            argu.appendKangeCode("HLOAD " + this.getReg(t) + " " + r2 + " " + n.f3.f0.tokenImage);
        }
        else {
            String rv = "v" + (argu.vReg++);
            argu.appendKangeCode("HLOAD " + rv + " " + r2 + " " + n.f3.f0.tokenImage);
            argu.appendKangeCode("ASTORE SPILLEDARG " + (t+argu.currentMethod.spilledNum) + " " + rv);
        }
        //n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    public String visit(MoveStmt n, Context argu) {
        String _ret=null;
        int t = Integer.valueOf(n.f1.f1.f0.tokenImage);
        t = argu.currentMethod.getReg(t);
        if(t == -1) return _ret;
        argu.movetoReg = t;
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    public String visit(PrintStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        String e1 = n.f1.accept(this, argu);
        argu.appendKangeCode("PRINT " + e1);
        return _ret;
    }

    /**
     * f0 -> Call()
     *       | HAllocate()
     *       | BinOp()
     *       | SimpleExp()
     */
    public String visit(Exp n, Context argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> SimpleExp()
     * f4 -> "END"
     */
    public String visit(StmtExp n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        String e3 = n.f3.accept(this, argu);
        //n.f4.accept(this, argu);
        argu.appendKangeCode("MOVE v0 " + e3);
        return _ret;
    }

    /**
     * f0 -> "CALL"
     * f1 -> SimpleExp()
     * f2 -> "("
     * f3 -> ( Temp() )*
     * f4 -> ")"
     */
    public String visit(Call n, Context argu) {
        String _ret=null;
        argu.passingParam = 0;
        n.f3.accept(this, argu);
        int tmp = argu.movetoReg;
        argu.movetoReg = -1;
        String e1 = n.f1.accept(this, argu);
        argu.movetoReg = tmp;
        argu.appendKangeCode("CALL " + e1);
        move("v0",argu);
        return _ret;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    public String visit(HAllocate n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        int tmp = argu.movetoReg;
        argu.movetoReg = -1;
        String e1 = n.f1.accept(this, argu);
        argu.movetoReg = tmp;
        move("HALLOCATE " + e1,argu);
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Temp()
     * f2 -> SimpleExp()
     */
    public String visit(BinOp n, Context argu) {
        String _ret=null;
        String r1 = n.f1.accept(this, argu);
        int tmp = argu.movetoReg;
        argu.movetoReg = -1;
        String e2 = n.f2.accept(this, argu);
        argu.movetoReg = tmp;
        String code = n.f0.accept(this,argu) + " " + r1 + " " + e2;
        if(argu.movetoReg < 18) {
            move(code,argu);
        }
        else {
            code = "MOVE v0 " + code;
            argu.appendKangeCode(code);
            move("v0",argu);
        }
        return _ret;
    }

    /**
     * f0 -> "LT"
     *       | "PLUS"
     *       | "MINUS"
     *       | "TIMES"
     */
    public String visit(Operator n, Context argu) {
        String _ret = ((NodeToken)n.f0.choice).tokenImage;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Temp()
     *       | IntegerLiteral()
     *       | Label()
     */
    public String visit(SimpleExp n, Context argu) {
        String _ret = n.f0.accept(this, argu);
        if(argu.movetoReg >= 0) move(_ret,argu);
        return _ret;
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    public String visit(Temp n, Context argu) {
        String _ret=null;
        int t = Integer.valueOf(n.f1.f0.tokenImage);
        t = argu.currentMethod.getReg(t);
        if(t < 18) {
            if(t < 8) _ret = "s" + t;
            else _ret = "t" + (t-8);
        }
        else {
            if(argu.passingParam >= 0) _ret = "v0";
            else _ret = "v" + (argu.vReg++);
            argu.appendKangeCode("ALOAD " + _ret + " SPILLEDARG " + (t+argu.currentMethod.spilledNum));
        }
        if(argu.passingParam >= 0) {
            if (argu.passingParam <= 3) argu.appendKangeCode("MOVE a" + argu.passingParam + " " + _ret);
            else argu.appendKangeCode("PASSARG " + (argu.passingParam-3) + " " + _ret);
            argu.passingParam++;
        }
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, Context argu) {
        String _ret=n.f0.tokenImage;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Label n, Context argu) {
        String _ret = n.f0.tokenImage;
        n.f0.accept(this, argu);
        if(argu.insideStat == false) {
            argu.appendKangeCode(_ret);
        }
        return _ret;
    }
}
