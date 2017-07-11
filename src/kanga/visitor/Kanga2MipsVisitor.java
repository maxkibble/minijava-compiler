package kanga.visitor;

import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_0;
import com.sun.org.apache.xpath.internal.Arg;
import kanga.kanga2mips.*;
import kanga.syntaxtree.*;

import java.security.spec.EncodedKeySpec;
import java.util.Enumeration;

/**
 * Created by maxkibble on 2015/12/12.
 */
public class Kanga2MipsVisitor extends GJDepthFirst<ReturnType,Environment> {
    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public ReturnType visit(NodeList n, Environment argu) {
        ReturnType _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public ReturnType visit(NodeListOptional n, Environment argu) {
        if ( n.present() ) {
            ReturnType _ret=null;
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

    public ReturnType visit(NodeOptional n, Environment argu) {
        if ( n.present() )
            return n.node.accept(this,argu);
        else
            return null;
    }

    public ReturnType visit(NodeSequence n, Environment argu) {
        ReturnType _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public ReturnType visit(NodeToken n, Environment argu) { return null; }

    public void initProc(String name,int paramNum,Environment argu) {
        argu.appendMipsCode(".text");
        argu.appendMipsCode(".globl " + name);
        argu.appendMipsCode(name + ":");
        argu.appendMipsCode("sw $fp, -8($sp)");
        argu.appendMipsCode("sw $ra, -4($sp)");
        argu.appendMipsCode("move $fp, $sp");
        argu.appendMipsCode("subu $sp, $sp, " + (paramNum+3)*4);
    }

    public void endProc(int paramNum, Environment argu) {
        argu.appendMipsCode("lw $ra, -4($fp)");
        argu.appendMipsCode("lw $fp, -8($fp)");
        argu.appendMipsCode("addu $sp, $sp, " + (paramNum+3)*4);
        argu.appendMipsCode("jr $ra");
        argu.appendMipsCode("");
    }
    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> "MAIN"
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> "["
     * f5 -> IntegerLiteral()
     * f6 -> "]"
     * f7 -> "["
     * f8 -> IntegerLiteral()
     * f9 -> "]"
     * f10 -> StmtList()
     * f11 -> "END"
     * f12 -> ( Procedure() )*
     * f13 -> <EOF>
     */
    public ReturnType visit(Goal n, Environment argu) {
        ReturnType _ret=null;

        argu.appendMipsCode(".data");
        argu.appendMipsCode(".align 0");
        argu.appendMipsCode("endl: .asciiz \"\\n\"");
        argu.appendMipsCode("");
        argu.appendMipsCode(".data");
        argu.appendMipsCode(".align 0");
        argu.appendMipsCode("error: .asciiz \"ERROR: abnormal termination\"");
        argu.appendMipsCode("");

        initProc("main", Integer.valueOf(n.f5.f0.tokenImage), argu);
        n.f10.accept(this, argu);
        endProc(Integer.valueOf(n.f5.f0.tokenImage),argu);

        n.f12.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    public ReturnType visit(StmtList n, Environment argu) {
        ReturnType _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> "["
     * f5 -> IntegerLiteral()
     * f6 -> "]"
     * f7 -> "["
     * f8 -> IntegerLiteral()
     * f9 -> "]"
     * f10 -> StmtList()
     * f11 -> "END"
     */
    public ReturnType visit(Procedure n, Environment argu) {
        ReturnType _ret=null;
        int paramNum  = Integer.valueOf(n.f5.f0.tokenImage);
        initProc(n.f0.f0.toString(), paramNum, argu);
        n.f10.accept(this, argu);
        endProc(paramNum, argu);
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
     *       | ALoadStmt()
     *       | AStoreStmt()
     *       | PassArgStmt()
     *       | CallStmt()
     */
    public ReturnType visit(Stmt n, Environment argu) {
        ReturnType _ret=null;
        argu.inStmt = true;
        n.f0.accept(this, argu);
        argu.inStmt = false;
        return _ret;
    }

    /**
     * f0 -> "NOOP"
     */
    public ReturnType visit(NoOpStmt n, Environment argu) {
        ReturnType _ret=null;
        argu.appendMipsCode("nop");
        return _ret;
    }

    /**
     * f0 -> "ERROR"
     */
    public ReturnType visit(ErrorStmt n, Environment argu) {
        ReturnType _ret=null;
        argu.appendMipsCode("la $a0, error");
        argu.appendMipsCode("li $v0, 4");
        argu.appendMipsCode("syscall");
        argu.appendMipsCode("li $v0, 10");
        argu.appendMipsCode("syscall");
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Reg()
     * f2 -> Label()
     */
    public ReturnType visit(CJumpStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        ReturnType r2 = n.f2.accept(this, argu);
        argu.appendMipsCode("beqz " + r1.value + " " + r2.value);
        return _ret;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    public ReturnType visit(JumpStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r = n.f1.accept(this, argu);
        argu.appendMipsCode("j " + r.value);
        return _ret;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Reg()
     * f2 -> IntegerLiteral()
     * f3 -> Reg()
     */
    public ReturnType visit(HStoreStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        int num = Integer.valueOf(n.f2.f0.tokenImage);
        ReturnType r2 = n.f3.accept(this, argu);
        argu.appendMipsCode("sw " + r2.value + ", " + num + "(" + r1.value + ")");
        return _ret;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Reg()
     * f2 -> Reg()
     * f3 -> IntegerLiteral()
     */
    public ReturnType visit(HLoadStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        ReturnType r2 = n.f2.accept(this, argu);
        int num = Integer.valueOf(n.f3.f0.tokenImage);
        argu.appendMipsCode("lw " + r1.value + ", " + num + "(" + r2.value + ")");
        return _ret;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Reg()
     * f2 -> Exp()
     */
    public ReturnType visit(MoveStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        ReturnType r2 = n.f2.accept(this, argu);
        if(n.f2.f0.choice instanceof SimpleExp) {
            Node choice = ((SimpleExp) n.f2.f0.choice).f0.choice;
            String op = "";
            if(choice instanceof IntegerLiteral) op = "li ";
            if(choice instanceof Label) op = "la ";
            if(choice instanceof Reg) op = "move ";
            argu.appendMipsCode(op + r1.value + ", " + r2.value);
        }
        else {
            argu.appendMipsCode("move " + r1.value + ", $v0");
        }
        return _ret;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    public ReturnType visit(PrintStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        if(n.f1.f0.choice instanceof IntegerLiteral) {
            argu.appendMipsCode("li $a0, " + r1.value);
        }
        else if(n.f1.f0.choice instanceof Label) {
            argu.appendMipsCode("la $a0, " + r1.value);
        }
        else {
            argu.appendMipsCode("move $a0, " + r1.value);
        }
        argu.appendMipsCode("li $v0, 1");
        argu.appendMipsCode("syscall");
        argu.appendMipsCode("la $a0, endl");
        argu.appendMipsCode("li $v0, 4");
        argu.appendMipsCode("syscall");
        return _ret;
    }

    /**
     * f0 -> "ALOAD"
     * f1 -> Reg()
     * f2 -> SpilledArg()
     */
    public ReturnType visit(ALoadStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        ReturnType r2 = n.f2.accept(this, argu);
        argu.appendMipsCode("lw " + r1.value + ", " + r2.value);
        return _ret;
    }

    /**
     * f0 -> "ASTORE"
     * f1 -> SpilledArg()
     * f2 -> Reg()
     */
    public ReturnType visit(AStoreStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        ReturnType r2 = n.f2.accept(this, argu);
        argu.appendMipsCode("sw " + r2.value + ", " + r1.value);
        return _ret;
    }

    /**
     * f0 -> "PASSARG"
     * f1 -> IntegerLiteral()
     * f2 -> Reg()
     */
    public ReturnType visit(PassArgStmt n, Environment argu) {
        ReturnType _ret=null;
        int num = (Integer.valueOf(n.f1.f0.tokenImage) + 2) * (-4);
        ReturnType r = n.f2.accept(this, argu);
        argu.appendMipsCode("sw " + r.value + ", " + num + "($sp)");
        return _ret;
    }

    /**
     * f0 -> "CALL"
     * f1 -> SimpleExp()
     */
    public ReturnType visit(CallStmt n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r1 = n.f1.accept(this, argu);
        Node choice = n.f1.f0.choice;
        if(choice instanceof  IntegerLiteral) {
            argu.appendMipsCode("li $v0, " + r1.value);
            argu.appendMipsCode("jalr $v0");
        }
        else {
            if(choice instanceof Label) {
                argu.appendMipsCode("jal " + r1.value);
            }
            else {
                argu.appendMipsCode("jalr " + r1.value);
            }
        }
        return _ret;
    }

    /**
     * f0 -> HAllocate()
     *       | BinOp()
     *       | SimpleExp()
     */
    public ReturnType visit(Exp n, Environment argu) {
        ReturnType _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    public ReturnType visit(HAllocate n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r = n.f1.accept(this, argu);
        if(n.f1.f0.choice instanceof IntegerLiteral) {
            argu.appendMipsCode("li $a0, " + r.value);
        }
        else if(n.f1.f0.choice instanceof Label) {
            argu.appendMipsCode("la $a0, " + r.value);
        }
        else {
            argu.appendMipsCode("move $a0, " + r.value);
        }
        argu.appendMipsCode("li $v0, 9");
        argu.appendMipsCode("syscall");
        return _ret;
    }

    /**
     * f0 -> Operator()
     * f1 -> Reg()
     * f2 -> SimpleExp()
     */
    public ReturnType visit(BinOp n, Environment argu) {
        ReturnType _ret=null;
        ReturnType r0 = n.f0.accept(this, argu);
        ReturnType r1 = n.f1.accept(this, argu);
        ReturnType r2 = n.f2.accept(this, argu);
        Node choice = n.f2.f0.choice;

        if(choice instanceof IntegerLiteral) {
            argu.appendMipsCode("li $v1, " + r2.value);
        }
        else if(choice instanceof Label) {
            argu.appendMipsCode("la $v1, " + r2.value);
        }
        else {
            argu.appendMipsCode("move $v1, " + r2.value);
        }
        String op = "";
        if(r0.value.equals("LT")) op = "slt";
        else if(r0.value.equals("PLUS")) op = "add";
        else if(r0.value.equals("MINUS")) op = "sub";
        else if(r0.value.equals("TIMES")) op = "mul";
        argu.appendMipsCode(op + " $v0, " + r1.value + ", $v1");
        return _ret;
    }

    /**
     * f0 -> "LT"
     *       | "PLUS"
     *       | "MINUS"
     *       | "TIMES"
     */
    public ReturnType visit(Operator n, Environment argu) {
        ReturnType _ret = new ReturnType("Operator", ((NodeToken)n.f0.choice).tokenImage);
        return _ret;
    }

    /**
     * f0 -> "SPILLEDARG"
     * f1 -> IntegerLiteral()
     */
    public ReturnType visit(SpilledArg n, Environment argu) {
        ReturnType _ret=null;
        int num = (3 + Integer.valueOf(n.f1.f0.tokenImage)) * (-4);
        _ret = new ReturnType("SpilledArg", num + "($fp)");
        return _ret;
    }

    /**
     * f0 -> Reg()
     *       | IntegerLiteral()
     *       | Label()
     */
    public ReturnType visit(SimpleExp n, Environment argu) {
        ReturnType _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "a0"
     *       | "a1"
     *       | "a2"
     *       | "a3"
     *       | "t0"
     *       | "t1"
     *       | "t2"
     *       | "t3"
     *       | "t4"
     *       | "t5"
     *       | "t6"
     *       | "t7"
     *       | "s0"
     *       | "s1"
     *       | "s2"
     *       | "s3"
     *       | "s4"
     *       | "s5"
     *       | "s6"
     *       | "s7"
     *       | "t8"
     *       | "t9"
     *       | "v0"
     *       | "v1"
     */
    public ReturnType visit(Reg n, Environment argu) {
        ReturnType _ret = new ReturnType("Reg","$" + ((NodeToken)n.f0.choice).tokenImage);
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public ReturnType visit(IntegerLiteral n, Environment argu) {
        ReturnType _ret = new ReturnType("IntegerLiteral", n.f0.tokenImage);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public ReturnType visit(Label n, Environment argu) {
        if(!argu.inStmt) {
            argu.appendMipsCode(n.f0.tokenImage + ": ");
        }
        ReturnType _ret = new ReturnType("Label", n.f0.tokenImage);
        return _ret;
    }

}
