package minijava.visitor;

import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.typecheck.PrintError;

import java.util.Enumeration;

/**
 * Created by maxkibble on 2015/10/27.
 */
public class MinijavaToPigletVisitor extends GJDepthFirst<Object,Object>{
    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public MPiglet visit(NodeList n, Object argu) {
        MPiglet _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            if(_ret == null) _ret = (MPiglet)e.nextElement().accept(this,argu);
            else _ret.appendCode((MPiglet)e.nextElement().accept(this,argu));
            _count++;
        }
        return _ret;
    }

    public MPiglet visit(NodeListOptional n, Object argu) {
        if ( n.present() ) {
            MPiglet _ret=null;
            int _count=0;
            for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                if(_ret == null) _ret = (MPiglet)e.nextElement().accept(this,argu);
                else _ret.appendCode((MPiglet)e.nextElement().accept(this,argu));
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

    public MPiglet visit(NodeSequence n, Object argu) {
        MPiglet _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            if(_ret == null) _ret = (MPiglet)e.nextElement().accept(this,argu);
            else _ret.appendCode((MPiglet)e.nextElement().accept(this,argu));
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
    public MPiglet visit(Goal n, Object argu) {
        MPiglet _ret = new MPiglet("");
        for(MClass mClass : MClassList.instance.getmClassArrayList()) {
        }
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        _ret.appendString("\nEND");
        _ret.appendCode((MPiglet) n.f1.accept(this, argu));
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
    public MPiglet visit(MainClass n, Object argu) {
        MPiglet _ret = new MPiglet("MAIN");
        n.f0.accept(this, argu);

        //n.f1.accept(this, argu);
        MClass mClass = MClassList.instance.findClass(n.f1.f0.tokenImage);

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        n.f6.accept(this, argu);
        MMethod mMethod = mClass.getMethod("main");

        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        //n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);

        _ret.appendCode((MPiglet)n.f14.accept(this, mMethod));

        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public MPiglet visit(TypeDeclaration n, Object argu) {
        MPiglet _ret = (MPiglet) n.f0.accept(this, argu);
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
    public MPiglet visit(ClassDeclaration n, Object argu) {
        n.f0.accept(this, argu);

        //n.f1.accept(this, argu);
        MClass mClass = MClassList.instance.findClass(n.f1.f0.tokenImage);

        n.f2.accept(this, argu);
        //n.f3.accept(this, argu);

        MPiglet _ret = (MPiglet)n.f4.accept(this, mClass);

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
    public MPiglet visit(ClassExtendsDeclaration n, Object argu) {
        n.f0.accept(this, argu);

        //n.f1.accept(this, argu);
        MClass mClass = MClassList.instance.findClass(n.f1.f0.tokenImage);

        n.f2.accept(this, argu);
        //n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        //n.f5.accept(this, argu);

        MPiglet _ret = (MPiglet)n.f6.accept(this, mClass);

        n.f7.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public MPiglet visit(VarDeclaration n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
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
    public MPiglet visit(MethodDeclaration n, Object argu) {
        n.f0.accept(this, argu);
        //n.f1.accept(this, argu);

        //System.out.println("a method is declared");
        //n.f2.accept(this, argu);
        MMethod mMethod = ((MClass)argu).getMethod(n.f2.f0.tokenImage);
        MPiglet _ret = new MPiglet(mMethod.getPigletDefineName());
        _ret.appendCode(new MPiglet("BEGIN"));

        n.f3.accept(this, argu);
        //n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        //n.f7.accept(this, argu);

        //System.out.println("statement code:" + ((MPiglet)n.f8.accept(this,mMethod)).getCode());
        //MPiglet exp8 = (MPiglet) n.f8.accept(this,mMethod);
        //if(exp8 == null) System.out.println("empty statement");
        _ret.appendCode((MPiglet) n.f8.accept(this, mMethod));

        n.f9.accept(this, argu);
        _ret.appendString("\nRETURN");

        _ret.appendCode((MPiglet) n.f10.accept(this, mMethod));

        _ret.appendCode(new MPiglet("END"));
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        //System.out.println(_ret.getCode());
        return _ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public MPiglet visit(FormalParameterList n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public MPiglet visit(FormalParameter n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public MPiglet visit(FormalParameterRest n, Object argu) {
        MPiglet _ret = new MPiglet("");
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
    public MPiglet visit(Type n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public MPiglet visit(ArrayType n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    public MPiglet visit(BooleanType n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    public MPiglet visit(IntegerType n, Object argu) {
        MPiglet _ret = new MPiglet("");
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
    public MPiglet visit(Statement n, Object argu) {
        MPiglet _ret = (MPiglet)n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public MPiglet visit(Block n, Object argu) {
        n.f0.accept(this, argu);

        MPiglet _ret = (MPiglet)n.f1.accept(this, argu);

        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public MPiglet visit(AssignmentStatement n, Object argu) {
        MPiglet _ret = new MPiglet("");
        MPiglet id = (MPiglet)n.f0.accept(this, argu);
        MVar mVar = id.getmVar();
        n.f1.accept(this, argu);

        MPiglet exp = (MPiglet)n.f2.accept(this, argu);
        if(mVar.judgeTemp()) {
            _ret.appendCode(new MPiglet("MOVE " + id.getCode()));
        }
        else {
            _ret.appendCode(new MPiglet("HSTORE TEMP 0 " + mVar.getOffset()));
        }
        _ret.appendCode(exp);
        n.f3.accept(this, argu);
        return _ret;
    }

    public String getNextTempName() {
        int num = MClassList.instance.getTempNum();
        String _ret = "TEMP " + num;
        MClassList.instance.setTempNum(num + 1);
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
    public MPiglet visit(ArrayAssignmentStatement n, Object argu) {
        MPiglet _ret = new MPiglet("");
        String s1 = getNextTempName();
        String s2 = getNextTempName();
        _ret.appendString(" MOVE " + s1);
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        _ret.appendString(" MOVE " + s2 + " PLUS 4 TIMES 4");

        n.f1.accept(this, argu);

        _ret.appendCode((MPiglet) n.f2.accept(this, argu));

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        _ret.appendString(" MOVE " + s1 + " PLUS " + s1 + " " + s2);
        _ret.appendString(" HSTORE " + s1 + " 0");
        _ret.appendCode((MPiglet) n.f5.accept(this, argu));
        n.f6.accept(this, argu);
        return _ret;
    }

    public String getNextLabel() {
        int num = MClassList.instance.getLabelNumber();
        String _ret = "label_" + num;
        MClassList.instance.setLabelNumber(num + 1);
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
    public MPiglet visit(IfStatement n, Object argu) {
        MPiglet _ret = new MPiglet("CJUMP");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        String s1 = getNextLabel();
        String s2 = getNextLabel();
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
        _ret.appendCode(new MPiglet(s1));

        n.f3.accept(this, argu);

        _ret.appendCode((MPiglet) n.f4.accept(this, argu));
        _ret.appendCode(new MPiglet( "JUMP " + s2 + "\n" + s1));

        n.f5.accept(this, argu);

        _ret.appendCode((MPiglet) n.f6.accept(this, argu));
        _ret.appendString("\n" + s2 + "\nNOOP");
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public MPiglet visit(WhileStatement n, Object argu) {
        MPiglet _ret = new MPiglet("");
        String s1 = getNextLabel(), s2 = getNextLabel();
        _ret.appendCode(new MPiglet(s1));
        _ret.appendCode(new MPiglet("CJUMP"));

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);

        _ret.appendString(" " + ((MPiglet)n.f2.accept(this, argu)).getCode() + " " + s2);
        n.f3.accept(this, argu);

        _ret.appendCode((MPiglet) n.f4.accept(this, argu));
        _ret.appendCode(new MPiglet("JUMP " + s1));
        _ret.appendString("\n" + s2);
        _ret.appendCode(new MPiglet("NOOP"));
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public MPiglet visit(PrintStatement n, Object argu) {
        MPiglet _ret = new MPiglet("PRINT");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
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
    public MPiglet visit(Expression n, Object argu) {
        MPiglet _ret = (MPiglet)n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public MPiglet visit(AndExpression n, Object argu) {
        MPiglet _ret = new MPiglet("TIMES");
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public MPiglet visit(CompareExpression n, Object argu) {
        MPiglet _ret = new MPiglet("LT");
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f2 -> PrimaryExpression()
     * f1 -> "+"
     */
    public MPiglet visit(PlusExpression n, Object argu) {
        MPiglet _ret = new MPiglet("PLUS");
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public MPiglet visit(MinusExpression n, Object argu) {
        MPiglet _ret = new MPiglet("MINUS");
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public MPiglet visit(TimesExpression n, Object argu) {
        MPiglet _ret = new MPiglet("TIMES");
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        n.f1.accept(this, argu);
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public MPiglet visit(ArrayLookup n, Object argu) {
        MPiglet _ret = new MPiglet("");

        _ret.appendCode(new MPiglet("BEGIN"));
        String s1 = getNextTempName(), s2 = getNextTempName(), s3 = getNextTempName();
        _ret.appendCode(new MPiglet("MOVE " + s1));
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        _ret.appendCode(new MPiglet("MOVE " + s2));
        n.f1.accept(this, argu);
        _ret.appendCode((MPiglet) n.f2.accept(this, argu));
        _ret.appendCode(new MPiglet("MOVE " + s2 + " PLUS 4 TIMES 4 " + s2));
        _ret.appendCode(new MPiglet("MOVE " + s1 + " PLUS " + s1 + " " + s2));
        _ret.appendCode(new MPiglet("HLOAD " + s3 + " " + s1 + " 0"));
        _ret.appendCode(new MPiglet("RETURN " + s3));
        n.f3.accept(this, argu);
        _ret.appendCode(new MPiglet("END"));
        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public MPiglet visit(ArrayLength n, Object argu) {
        MPiglet _ret = new MPiglet("");

        _ret.appendCode(new MPiglet("BEGIN"));
        String s1 = getNextTempName(), s2 = getNextTempName();
        _ret.appendCode(new MPiglet("MOVE " + s1));
        _ret.appendCode((MPiglet) n.f0.accept(this, argu));
        _ret.appendCode(new MPiglet("HLOAD " + s2 + " " + s1 + " 0"));
        _ret.appendCode(new MPiglet("RETURN " + s2));
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        _ret.appendCode(new MPiglet("END"));

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
    public MPiglet visit(MessageSend n, Object argu) {
        MPiglet _ret = new MPiglet("CALL");
        _ret.appendCode(new MPiglet("BEGIN"));
        MPiglet _f0 = (MPiglet)n.f0.accept(this, argu);
        MClass mClass = _f0.getmClass();
        if(mClass == null) System.out.println(n.f2.f0.tokenImage + " " + n.f2.f0.beginLine);
        MMethod mMethod = mClass.getMethod(n.f2.f0.tokenImage);
        if(mMethod != null) {
            _ret.setmClass(MClassList.instance.findClass(mMethod.getReturnType()));
        }
        else System.out.println(n.f2.f0.beginLine + " " + mClass.getName() + " " + n.f2.f0.tokenImage + " a empty method");

        String s1 = getNextTempName(), s2 = getNextTempName(), s3 = getNextTempName();
        _ret.appendCode(new MPiglet("MOVE " + s1 + " " + _f0.getCode().toString()));
        _ret.appendCode(new MPiglet("HLOAD " + s2 + " " + s1 + " 0"));
        _ret.appendCode(new MPiglet("HLOAD " + s3 + " " + s2 + " " + mMethod.getOffset()));
        _ret.appendCode(new MPiglet("RETURN " + s3));
        _ret.appendCode(new MPiglet("END"));

        n.f1.accept(this, argu);
        //n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        _ret.appendString(" (" + s1);
        MPiglet _f4 = (MPiglet)n.f4.accept(this,argu);
        if(_f4 != null) _ret.appendString(" " + _f4.getCode());
        _ret.appendString(")");

        n.f5.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public MPiglet visit(ExpressionList n, Object argu) {
        MPiglet _ret = (MPiglet) n.f0.accept(this, argu);
        _ret.appendCode((MPiglet) n.f1.accept(this, argu));
        return _ret;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public MPiglet visit(ExpressionRest n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
        _ret.appendCode((MPiglet) n.f1.accept(this, argu));
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
    public MPiglet visit(PrimaryExpression n, Object argu) {
        MPiglet _ret = (MPiglet)n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public MPiglet visit(IntegerLiteral n, Object argu) {
        MPiglet _ret = new MPiglet(n.f0.tokenImage);
        return _ret;
    }

    /**
     * f0 -> "true"
     */
    public MPiglet visit(TrueLiteral n, Object argu) {
        MPiglet _ret = new MPiglet("1");
        return _ret;
    }

    /**
     * f0 -> "false"
     */
    public MPiglet visit(FalseLiteral n, Object argu) {
        MPiglet _ret = new MPiglet("0");
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public MPiglet visit(Identifier n, Object argu) {
        MPiglet _ret = new MPiglet("");
        MVar mVar = null;
        if(argu instanceof MMethod) {
            mVar = ((MMethod) argu).getVar(n.f0.tokenImage);
        }
        _ret.setmVar(mVar);
        if(mVar == null) {
            PrintError.instance.printError(n.f0.beginLine,n.f0.beginColumn,"no var named:" + n.f0.tokenImage);
        }
        else {
            MClass mClass = MClassList.instance.findClass(mVar.getType());
            _ret.setmClass(mClass);

            if (mVar.judgeTemp()) {
                _ret.appendString(" TEMP " + mVar.getTempNum());
            } else {
                _ret.appendCode(new MPiglet("BEGIN"));
                String s1 = getNextTempName();
                MPiglet m1 = new MPiglet("HLOAD " + s1);
                m1.appendString(" TEMP 0 " + mVar.getOffset());
                _ret.appendCode(m1);
                _ret.appendCode(new MPiglet("RETURN " + s1));
                _ret.appendCode(new MPiglet("END"));
            }
        }
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "this"
     */
    public MPiglet visit(ThisExpression n, Object argu) {
        MPiglet _ret = new MPiglet("TEMP 0");
        MClass mClass = MClassList.instance.findClass(((MMethod) argu).getClassName());
        _ret.setmClass(mClass);
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
    public MPiglet visit(ArrayAllocationExpression n, Object argu) {
        MPiglet _ret = new MPiglet("BEGIN");
        String s1 = getNextTempName(), s2 = getNextTempName(), s3 = getNextTempName();
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        _ret.appendCode(new MPiglet("MOVE " + s1));
        _ret.appendCode((MPiglet) n.f3.accept(this, argu));

        _ret.appendCode(new MPiglet("MOVE " + s2 + " TIMES 4 " + s1));
        _ret.appendCode(new MPiglet("MOVE " + s2 + " PLUS 4 " + s2));
        _ret.appendCode(new MPiglet("MOVE " + s3 + " HALLOCATE " + s2));
        _ret.appendCode(new MPiglet("HSTORE " + s3 + " 0 " + s1));
        _ret.appendCode(new MPiglet("RETURN " + s3));
        n.f4.accept(this, argu);
        _ret.appendCode(new MPiglet("END"));
        return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public MPiglet visit(AllocationExpression n, Object argu) {
        MPiglet _ret = new MPiglet("");
        MClass mClass = MClassList.instance.findClass(n.f1.f0.tokenImage);
        _ret.setmClass(mClass);
        String s1 = getNextTempName(), s2 = getNextTempName();

        _ret.appendCode(new MPiglet("BEGIN"));
        MPiglet exp1 = new MPiglet("MOVE " + s1 + " HALLOCATE " + mClass.getMethodSize());
        for(MMethod mMethod : mClass.getMethodList()) {
            MPiglet tmp = new MPiglet("HSTORE " + s1 + " " + mMethod.getOffset());
            tmp.appendString(" " + mMethod.getPigketName());
            exp1.appendCode(tmp);
        }
        MPiglet exp2 = new MPiglet("MOVE " + s2 + " HALLOCATE " + (4+mClass.getVarSize()));
        for(MVar mVar : mClass.getmVarArrayList()) {
            MPiglet tmp = new MPiglet("HSTORE " + s2 + " " + mVar.getOffset());
            tmp.appendString(" 0");
            exp2.appendCode(tmp);
        }
        MPiglet exp3 = new MPiglet("HSTORE " + s2 + " 0 " + s1);
        MPiglet exp4 = new MPiglet("RETURN " + s2);
        _ret.appendCode(exp1);
        _ret.appendCode(exp2);
        _ret.appendCode(exp3);
        _ret.appendCode(exp4);
        n.f0.accept(this, argu);
        //n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        _ret.appendCode(new MPiglet("END"));
        return _ret;
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public MPiglet visit(NotExpression n, Object argu) {
        MPiglet _ret = new MPiglet("MINUS 1");
        n.f0.accept(this, argu);
        _ret.appendCode((MPiglet) n.f1.accept(this, argu));
        return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public MPiglet visit(BracketExpression n, Object argu) {
        MPiglet _ret = new MPiglet("");
        n.f0.accept(this, argu);
        _ret.appendCode((MPiglet) n.f1.accept(this, argu));
        n.f2.accept(this, argu);
        _ret.setmClass((((MPiglet) n.f1.accept(this,argu)).getmClass()));
        return _ret;
    }

}
