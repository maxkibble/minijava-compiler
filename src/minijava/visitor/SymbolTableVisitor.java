package minijava.visitor;


import com.sun.org.apache.xpath.internal.SourceTree;
import minijava.symboltable.*;
import minijava.syntaxtree.*;

import java.lang.Object;
import java.util.Enumeration;

/**
 * Created by maxkibble on 2015/10/15.
 */
public class SymbolTableVisitor extends GJDepthFirst<Object, Object> {
    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public Object visit(NodeList n, Object argu) {
        Object _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public Object visit(NodeListOptional n, Object argu) {
        if ( n.present() ) {
            Object _ret=null;
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

    public Object visit(NodeOptional n, Object argu) {
        if ( n.present() )
            return n.node.accept(this,argu);
        else
            return null;
    }

    public Object visit(NodeSequence n, Object argu) {
        Object _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public Object visit(NodeToken n, Object argu) { return null; }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public Object visit(Goal n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> PrintStatement()
     * f15 -> "}"
     * f16 -> "}"
     */
    public Object visit(MainClass n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);

        // add main class into class list
        MClass nClass = new MClass(n.f1.f0.tokenImage,n.f1.f0.beginLine,n.f1.f0.beginColumn);
        if(!MClassList.instance.addClass(nClass)) return null;

        n.f1.accept(this, nClass);
        n.f2.accept(this, nClass);
        n.f3.accept(this, nClass);
        n.f4.accept(this, nClass);
        n.f5.accept(this, nClass);

        // add main method into method list of main class
        MMethod nMethod = new MMethod("main","void",n.f1.f0.tokenImage,n.f1.f0.beginLine+1,n.f1.f0.beginColumn+1);
        if(!nClass.addMethod(nMethod)) return null;

        n.f6.accept(this, nClass);
        n.f7.accept(this, nMethod);
        n.f8.accept(this, nMethod);
        n.f9.accept(this, nMethod);
        n.f10.accept(this, nMethod);

        // add parameter for main method
        MVar nVar = new MVar(n.f11.f0.tokenImage,"String[]",true,"main",n.f1.f0.tokenImage,n.f11.f0.beginLine,n.f11.f0.beginColumn);
        if(!nMethod.addParam(nVar)) return null;
        n.f11.accept(this, nMethod);

        n.f12.accept(this, nMethod);
        n.f13.accept(this, nMethod);
        n.f14.accept(this, nMethod);
        n.f15.accept(this, nMethod);
        n.f16.accept(this, nClass);
        return _ret;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public Object visit(TypeDeclaration n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public Object visit(ClassDeclaration n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);

        MClass nClass = new MClass(n.f1.f0.tokenImage,n.f1.f0.beginLine,n.f1.f0.beginColumn);
        if(!MClassList.instance.addClass(nClass)) return null;
        n.f1.accept(this, argu);

        n.f2.accept(this, argu);
        n.f3.accept(this, nClass);
        n.f4.accept(this, nClass);
        n.f5.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public Object visit(ClassExtendsDeclaration n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);

        MClass nClass = new MClass(n.f1.f0.tokenImage,n.f1.f0.beginLine,n.f1.f0.beginColumn);
        if(!MClassList.instance.addClass(nClass)) return null;
        n.f1.accept(this, argu);

        n.f2.accept(this, argu);

        nClass.setParentClass(n.f3.f0.tokenImage);
        n.f3.accept(this, argu);

        n.f4.accept(this, argu);
        n.f5.accept(this, nClass);
        n.f6.accept(this, nClass);
        n.f7.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public Object visit(VarDeclaration n, Object argu) {
        Object _ret=null;
        String type = (String)n.f0.accept(this, argu);

        if(argu instanceof MClass) {
            MVar nVar = new MVar(n.f1.f0.tokenImage,type,false,null,((MClass) argu).getName(),n.f1.f0.beginLine,n.f1.f0.beginColumn);
            if(!((MClass) argu).addVar(nVar)) return null;
        }
        else if(argu instanceof MMethod) {
            MVar nVar = new MVar(n.f1.f0.tokenImage,type,false,((MMethod) argu).getName(),((MMethod) argu).getClassName(),n.f1.f0.beginLine,n.f1.f0.beginColumn);
            if(!((MMethod) argu).addVar(nVar)) return null;
        }
        n.f1.accept(this, argu);

        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public Object visit(MethodDeclaration n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        String type = (String)n.f1.accept(this, argu);

        MMethod nMethod = new MMethod(n.f2.f0.tokenImage, type, ((MClass) argu).getName(), n.f2.f0.beginLine, n.f2.f0.beginColumn);
        n.f2.accept(this, argu);

        n.f3.accept(this, argu);
        n.f4.accept(this, nMethod);
        if(!((MClass) argu).addMethod(nMethod)) {
            return null;
        }
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, nMethod);
        n.f8.accept(this, nMethod);
        n.f9.accept(this, argu);
        n.f10.accept(this, nMethod);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public Object visit(FormalParameterList n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public Object visit(FormalParameter n, Object argu) {
        Object _ret=null;
        String type = (String)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        if(argu instanceof MMethod) {
            MVar nVar = new MVar(n.f1.f0.tokenImage,type,true,((MMethod) argu).getName(),((MMethod) argu).getClassName(),n.f1.f0.beginLine,n.f1.f0.beginColumn);
            if(!((MMethod) argu).addParam(nVar)) return null;
        }
        else {
            System.out.println("add a param to a non-method object");
        }
        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public Object visit(FormalParameterRest n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public Object visit(Type n, Object argu) {
        Object _ret= n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public Object visit(ArrayType n, Object argu) {
        String _ret="int[]";
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    public Object visit(BooleanType n, Object argu) {
        String _ret="boolean";
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    public Object visit(IntegerType n, Object argu) {
        String _ret="int";
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public Object visit(Statement n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public Object visit(Block n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public Object visit(AssignmentStatement n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public Object visit(ArrayAssignmentStatement n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public Object visit(IfStatement n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public Object visit(WhileStatement n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public Object visit(PrintStatement n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    public Object visit(Expression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public Object visit(AndExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public Object visit(CompareExpression n,Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f2 -> PrimaryExpression()
     * f1 -> "+"
     */
    public Object visit(PlusExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public Object visit(MinusExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public Object visit(TimesExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public Object visit(ArrayLookup n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public Object visit(ArrayLength n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public Object visit(MessageSend n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public Object visit(ExpressionList n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public Object visit(ExpressionRest n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public Object visit(PrimaryExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public Object visit(IntegerLiteral n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "true"
     */
    public Object visit(TrueLiteral n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "false"
     */
    public Object visit(FalseLiteral n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public Object visit(Identifier n, Object argu) {
        Object _ret = n.f0.tokenImage;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "this"
     */
    public Object visit(ThisExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public Object visit(ArrayAllocationExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public Object visit(AllocationExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public Object visit(NotExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public Object visit(BracketExpression n, Object argu) {
        Object _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }
}
