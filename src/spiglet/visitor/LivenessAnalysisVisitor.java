package spiglet.visitor;

import com.sun.jndi.cosnaming.CNCtx;
import spiglet.syntaxtree.*;

import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import spiglet.spiglet2kanga.*;

/**
 * Created by maxkibble on 11/26/15.
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */

public class LivenessAnalysisVisitor extends GJDepthFirst<String ,Context> {
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

    /**
     * f0 -> "MAIN"
     * f1 -> StmtList()
     * f2 -> "END"
     * f3 -> ( Procedure() )*
     * f4 -> <EOF>
     */
    public String visit(Goal n, Context argu) {
        String _ret=null;
        argu.currentMethod = new SMethod("MAIN");
        argu.currentMethod.paramNum = 0;
        argu.currentMethod.addStat(new SStat("empty"));
        //n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        argu.addMethod();
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
        argu.currentMethod = new SMethod(n.f0.f0.tokenImage);
        argu.currentMethod.paramNum = Integer.valueOf(n.f2.f0.tokenImage);
        argu.currentMethod.addStat(new SStat("empty"));
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        //n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        argu.addMethod();
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
        n.f0.accept(this, argu);
        argu.insideStat = false;
        return _ret;
    }

    /**
     * f0 -> "NOOP"
     */
    public String visit(NoOpStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        SStat sStat = new SStat("NoOpStmt");
        argu.currentMethod.addStat(sStat);
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    public String visit(ErrorStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        SStat sStat = new SStat("ErrorStmt");
        argu.currentMethod.addStat(sStat);
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Temp()
     * f2 -> Label()
     */
    public String visit(CJumpStmt n, Context argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        SStat sStat = new SStat("CJumpStmt");
        sStat.jmupLabel = n.f2.f0.tokenImage;
        sStat.addUsedTemp(n.f1.f1.f0.tokenImage);
        argu.currentMethod.addStat(sStat);
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
        SStat sStat = new SStat("JumpStmt");
        sStat.jmupLabel = n.f1.f0.tokenImage;
        sStat.isUnconditionalJump = true;
        argu.currentMethod.addStat(sStat);
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Temp()
     * f2 -> IntegerLiteral()
     * f3 -> Temp()
     */
    public String visit(HStoreStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        //n.f3.accept(this, argu);
        SStat sStat = new SStat("HStoreStmt");
        sStat.addUsedTemp(n.f1.f1.f0.tokenImage);
        sStat.addUsedTemp(n.f3.f1.f0.tokenImage);
        argu.currentMethod.addStat(sStat);
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
        //n.f2.accept(this, argu);
        //n.f3.accept(this, argu);
        SStat sStat = new SStat("HLoadStmt");
        sStat.addUsedTemp(n.f1.f1.f0.tokenImage);
        sStat.addUsedTemp(n.f2.f1.f0.tokenImage);
        argu.currentMethod.addStat(sStat);
        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    public String visit(MoveStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        argu.currentStat = new SStat("MoveStmt");
        argu.currentStat.genTemp = Integer.valueOf(n.f1.f1.f0.tokenImage);
        n.f2.accept(this, argu);
        argu.currentMethod.addStat(argu.currentStat);
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    public String visit(PrintStmt n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        argu.currentStat = new SStat("PrintStmt");
        n.f1.accept(this, argu);
        argu.currentMethod.addStat(argu.currentStat);
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
        argu.currentStat = new SStat("Return");
        //n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        argu.currentMethod.addStat(argu.currentStat);
        //n.f4.accept(this, argu);
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
        argu.currentStat = new SStat("Call");
        //n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        //n.f4.accept(this, argu);
        argu.currentMethod.addStat(argu.currentStat);
        return _ret;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    public String visit(HAllocate n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Temp()
     * f2 -> SimpleExp()
     */
    public String visit(BinOp n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "LT"
     *       | "PLUS"
     *       | "MINUS"
     *       | "TIMES"
     */
    public String visit(Operator n, Context argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Temp()
     *       | IntegerLiteral()
     *       | Label()
     */
    public String visit(SimpleExp n, Context argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    public String visit(Temp n, Context argu) {
        String _ret=null;
        //n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        //if(argu.currentStat == null) System.out.println("null here");
        argu.currentStat.addUsedTemp(n.f1.f0.tokenImage);
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, Context argu) {
        String _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Label n, Context argu) {
        String _ret=null;
        if(argu.insideStat) {
            argu.currentStat.jmupLabel = n.f0.tokenImage;
        }
        else {
            SStat sStat = new SStat("Entry Label");
            sStat.entryLabel = n.f0.tokenImage;
            argu.currentMethod.addStat(sStat);
        }
        //n.f0.accept(this, argu);
        return _ret;
    }

}