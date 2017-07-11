package minijava.visitor;

import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.typecheck.PrintError;

import javax.xml.ws.soap.MTOM;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.prefs.NodeChangeListener;

/**
 * Created by maxkibble on 2015/10/18.
 */

public class TypeCheckVisitor extends GJDepthFirst<Object, Object> {
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

        String name = ((MType)n.f1.accept(this, argu)).getName();
        MClass nClass = MClassList.instance.findClass(name);

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);

        n.f6.accept(this, argu);
        MMethod nMethod = nClass.getMethod("main");

        n.f7.accept(this, nMethod);
        n.f8.accept(this, nMethod);
        n.f9.accept(this, nMethod);
        n.f10.accept(this, nMethod);
        n.f11.accept(this, nMethod);
        n.f12.accept(this, nMethod);
        n.f13.accept(this, nMethod);
        n.f14.accept(this, nMethod);
        n.f15.accept(this, nMethod);
        n.f16.accept(this, nMethod);
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

        String name = ((MType)n.f1.accept(this, argu)).getName();;
        MClass nClass = MClassList.instance.findClass(name);

        n.f2.accept(this, nClass);
        n.f3.accept(this, nClass);
        n.f4.accept(this, nClass);
        n.f5.accept(this, nClass);
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

        String name = ((MType)n.f1.accept(this, argu)).getName();;
        MClass nClass = MClassList.instance.findClass(name);

        n.f2.accept(this, nClass);
        n.f3.accept(this, nClass);

        String parent = nClass.getParentClass();
        if(MClassList.instance.findClass(parent) == null) {
            PrintError.instance.printError(n.f3.f0.beginLine,n.f3.f0.beginColumn,"no declaration for parent class " + parent);
        }
        else {
            HashSet<String> parentSet = new HashSet<>();
            while(parent != null) {
                if(parent.equals(nClass.getName())) {
                    //if find a circle extension,cut the extension relationship
                    PrintError.instance.printError(n.f3.f0.beginLine,n.f3.f0.beginColumn,"circle extension");
                    nClass.setParentClass(null);
                    break;
                }
                else if(parentSet.contains(parent)) break;
                parentSet.add(parent);
                MClass parentClass = MClassList.instance.findClass(parent);
                if(parentClass != null) parent = parentClass.getParentClass();
                else break;
            }
        }

        n.f4.accept(this, nClass);
        n.f5.accept(this, nClass);
        n.f6.accept(this, nClass);
        n.f7.accept(this, nClass);
        return _ret;
    }

    /**
     * check whether a class has been defined
     * @param name:name of the class to be checked
     * @param line:begin line of the object of this class
     * @param column:begin column of the object of this class
     */
    public boolean classCheck(String name,int line,int column) {
        if(name.equals("int") || name.equals("int[]") || name.equals("boolean")) return true;
        if(MClassList.instance.findClass(name) != null) return true;
        PrintError.instance.printError(line, column, "no class definition:" + name);
        return false;
    }

    /**
     * check whether a method has been defined
     * @param name
     * @param nClass
     * @param line
     * @param column
     */
    public boolean methodCheck(String name,MClass nClass,int line,int column) {
        if(nClass.getMethod(name) == null) {
            PrintError.instance.printError(line,column," no method definition:" + name + " in class " + nClass.getName());
            return false;
        }
        return true;
    }

    /**
     * check whether a var has been defined
     * @param name:name of the target var
     * @param argu:a method or a class
     * @param line:begin line of the var
     * @param column:begin column of the var
     */
    public boolean varCheck(String name,Object argu,int line,int column) {
        MVar nVar = null;
        if(argu instanceof MMethod) {
            nVar = ((MMethod) argu).getVar(name);
        }
        else if (argu instanceof MClass) {
            nVar = ((MClass) argu).getVar(name);
        }
        else if(argu instanceof MCallList) {
            nVar = ((MCallList) argu).getContextMethod().getVar(name);
        }
        else {
            PrintError.instance.printError(line, column, "find a var outside class and method " + name);
        }
        if(nVar == null) {
            PrintError.instance.printError(line,column,"Undefined var " + name);
            return false;
        }
        return true;
    }

    /**
     * check whether a expression is the given type
     * @param exp:a expression
     * @param type:the expression is supposed to be this type
     * @param printContent:content to be printed if not match
     */
    public boolean checkExpType(MType exp,String type,String printContent) {
        if(exp == null || type == null) {
            //PrintError.instance.printError(exp.getLine(),exp.getColumn(),"find null type in expression type check");
            return false;
        }

        //is the same type
        if(exp.getName() == null) return false;
        if(exp.getName().equals(type)) return true;

        // no class for the expression
        MClass nClass = MClassList.instance.findClass(exp.getName());
        if(nClass == null) {
            PrintError.instance.printError(exp.getLine(),exp.getColumn()," no class correspond to the expression");
            return false;
        }
        String parent = nClass.getParentClass();

        //System.out.println(exp.getName() + type);
        if(type.equals("int") || type.equals("int[]") || type.equals("boolean")) {
            PrintError.instance.printError(exp.getLine(),exp.getColumn()," try to give a non-base type value to a base-type var");
            return false;
        }
        //check if type is the parent class of the expression
        boolean findParentClass = false;
        HashSet<String> parentSet = new HashSet<>();
        while (parent != null) {
            if(parent.equals(type)) {
                findParentClass = true;
                break;
            }
            else if(parentSet.contains(parent)) break;
            parentSet.add(parent);
            nClass = MClassList.instance.findClass(parent);
            if(nClass == null) break;
            else parent = nClass.getParentClass();
        }
        if(!findParentClass) {
            PrintError.instance.printError(exp.getLine(),exp.getColumn(),printContent);
            return false;
        }
        return true;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public Object visit(VarDeclaration n, Object argu) {
        Object _ret=null;
        String typename = ((MType)n.f0.accept(this, argu)).getName();;
        classCheck(typename, n.f1.f0.beginLine, n.f1.f0.beginColumn);
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

        MType nMtype = (MType)n.f1.accept(this, argu);;
        classCheck(nMtype.getName(), nMtype.getLine(), nMtype.getColumn());

        MMethod nMethod = null;
        if(argu instanceof MClass) {
            nMethod = ((MClass) argu).getMethod(((MType)n.f2.accept(this, argu)).getName());
            MClass nClass = MClassList.instance.findClass(nMethod.getClassName());
            nClass.repeatedMethod(nMethod.getName(),nMethod.getReturnType(),nMethod.getmParamArrayList());
        }
        else {
            System.out.println("a method not in class");
        }

        n.f3.accept(this, nMethod);
        n.f4.accept(this, nMethod);
        n.f5.accept(this, nMethod);
        n.f6.accept(this, nMethod);
        n.f7.accept(this, nMethod);
        n.f8.accept(this, nMethod);
        n.f9.accept(this, nMethod);

        MType type = (MType)n.f10.accept(this, nMethod);
        checkExpType(type,nMethod.getReturnType()," no match for the return type");
        n.f11.accept(this, nMethod);
        n.f12.accept(this, nMethod);
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
        String type = ((MType)n.f0.accept(this, argu)).getName();;
        classCheck(type, n.f1.f0.beginLine, n.f1.f0.beginColumn);

        n.f1.accept(this, argu);
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
        MType _ret=new MType("int[]",n.f0.beginLine,n.f0.beginColumn);
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    public Object visit(BooleanType n, Object argu) {
        MType _ret=new MType("boolean",n.f0.beginLine,n.f0.beginColumn);
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "int"
     */
    public Object visit(IntegerType n, Object argu) {
        MType _ret=new MType("int",n.f0.beginLine,n.f0.beginColumn);
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

    public String changeIdentifier(MType nType,Object argu) {
        String name = nType.getName();
        MVar nVar = null;
        if(argu instanceof MClass) {
            nVar = ((MClass) argu).getVar(name);
        }
        else if(argu instanceof MMethod) {
            nVar = ((MMethod) argu).getVar(name);
        }
        if(nVar == null) return null;
        return nVar.getType();
    }
    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public Object visit(AssignmentStatement n, Object argu) {
        Object _ret=null;
        MType nType = (MType)n.f0.accept(this, argu);
        varCheck(nType.getName(), argu, nType.getLine(), nType.getColumn());
        nType.setName(changeIdentifier(nType, argu));


        n.f1.accept(this, argu);

        MType exp = (MType)n.f2.accept(this, argu);
        checkExpType(exp, nType.getName(), "type dis-match for expression");

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
        MType arrVar = (MType)n.f0.accept(this, argu);
        varCheck(arrVar.getName(),argu,arrVar.getLine(),arrVar.getColumn());
        arrVar.setName(changeIdentifier(arrVar, argu));
        checkExpType(arrVar, "int[]", "type-mismatch for int[]" + arrVar.getLine() + arrVar.getColumn());

        n.f1.accept(this, argu);

        MType indexExp = (MType)n.f2.accept(this, argu);
        checkExpType(indexExp, "int", "type mis-match for index of an array,should be int" + indexExp.getLine() + indexExp.getColumn());

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        MType exp = (MType)n.f5.accept(this, argu);
        checkExpType(exp, "int", "type mis-match for int[]" + exp.getLine() + exp.getColumn());

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

        MType exp = (MType)n.f2.accept(this, argu);
        checkExpType(exp, "boolean", "not boolean type used for condition");

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

        MType exp = (MType)n.f2.accept(this, argu);
        checkExpType(exp,"boolean","not boolean type used for condition");

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

        MType exp = (MType)n.f2.accept(this, argu);
        checkExpType(exp,"int","print a non-digital expression");

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
        MType _ret = (MType)n.f0.accept(this, argu);

        return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public Object visit(AndExpression n, Object argu) {
        MType exp1 = (MType)n.f0.accept(this, argu);
        checkExpType(exp1,"boolean","left part of '&&' is not boolean");

        n.f1.accept(this, argu);

        MType exp2 = (MType)n.f2.accept(this, argu);
        checkExpType(exp2,"boolean","right part of '&&' is not boolean");
        //System.out.println("***" + exp1.getName() + exp2.getName());
        return new MType("boolean",exp1.getLine(),exp1.getColumn());
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public Object visit(CompareExpression n, Object argu) {
        MType exp1 = (MType)n.f0.accept(this, argu);
        checkExpType(exp1, "int", "left part of '<' is not int");

        n.f1.accept(this, argu);

        MType exp2 = (MType)n.f2.accept(this, argu);
        checkExpType(exp2, "int", "right part of '<' is not int");
        return new MType("boolean",exp1.getLine(),exp1.getColumn());
    }

    /**
     * f0 -> PrimaryExpression()
     * f2 -> PrimaryExpression()
     * f1 -> "+"
     */
    public Object visit(PlusExpression n, Object argu) {
        MType exp1 = (MType)n.f0.accept(this, argu);
        checkExpType(exp1, "int", "left part of '+' is not int");

        n.f1.accept(this, argu);

        MType exp2 = (MType)n.f2.accept(this, argu);
        checkExpType(exp2, "int", "right part of '+' is not int");
        return new MType("int",exp1.getLine(),exp1.getColumn());
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public Object visit(MinusExpression n, Object argu) {
        MType exp1 = (MType)n.f0.accept(this, argu);
        checkExpType(exp1, "int", "left part of '-' is not int");

        n.f1.accept(this, argu);

        MType exp2 = (MType)n.f2.accept(this, argu);
        checkExpType(exp2, "int", "right part of '-' is not int");
        return new MType("int",exp1.getLine(),exp1.getColumn());
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public Object visit(TimesExpression n, Object argu) {
        MType exp1 = (MType)n.f0.accept(this, argu);
        checkExpType(exp1, "int", "left part of '*' is not int");

        n.f1.accept(this, argu);

        MType exp2 = (MType)n.f2.accept(this, argu);
        checkExpType(exp2, "int", "right part of '*' is not int");
        return new MType("int",exp1.getLine(),exp1.getColumn());
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public Object visit(ArrayLookup n, Object argu) {
        MType exp1 = (MType)n.f0.accept(this, argu);
        checkExpType(exp1, "int[]", "left part of '[' is not int[]");

        n.f1.accept(this, argu);

        MType exp2 = (MType)n.f2.accept(this, argu);
        checkExpType(exp2, "int", "index of an array is not int");

        n.f3.accept(this,argu);
        return new MType("int",exp1.getLine(),exp1.getColumn());
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public Object visit(ArrayLength n, Object argu) {
        MType exp = (MType)n.f0.accept(this, argu);
        checkExpType(exp, "int[]", "ask for length of a non-array var");

        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return new MType("int",exp.getLine(),exp.getColumn());
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
        MType caller = (MType)n.f0.accept(this, argu);

        n.f1.accept(this, argu);

        MMethod nMethod = null;
        MType methodCalled = (MType)n.f2.accept(this, argu);

            /**check whether the method contains var f0
            if(!varCheck(caller.getName(),argu,caller.getLine(),caller.getColumn())) {
                return new MType("undefined",methodCalled.getLine(),methodCalled.getColumn());
            }*/
            //check whether the caller's class contains the called method
        MClass nClass = MClassList.instance.findClass(caller.getName());
        if(nClass == null) return null;
        if(!methodCheck(methodCalled.getName(),nClass,methodCalled.getLine(),methodCalled.getColumn())) {
            return new MType("undefined",methodCalled.getLine(),methodCalled.getColumn());
        }
        nMethod = nClass.getMethod(methodCalled.getName());


        MType _ret = new MType(nMethod.getReturnType(),methodCalled.getLine(),methodCalled.getColumn());
        n.f3.accept(this, argu);
        MCallList nCallList = null;
        if(argu instanceof MMethod) {
            nCallList = new MCallList(nMethod,(MMethod)argu);
        }
        else nCallList = new MCallList(nMethod,((MCallList)argu).getContextMethod());
        n.f4.accept(this, nCallList);
        if(!nMethod.judgeParamList(nCallList.getmVarArrayList())) {
            PrintError.instance.printError(methodCalled.getLine(), methodCalled.getColumn(), "function param no match");
        }
        n.f5.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public Object visit(ExpressionList n, Object argu) {
        Object _ret=null;
        MType nType = (MType)n.f0.accept(this, argu);
        if(argu instanceof MCallList) {
            ((MCallList) argu).getmVarArrayList().add(new MVar("", nType.getName(), false, null, null, 0, 0));
        }
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
        MType nType = (MType)n.f1.accept(this, argu);
        if(argu instanceof MCallList) {
            ((MCallList) argu).getmVarArrayList().add(new MVar("", nType.getName(), false, null, null, 0, 0));
        }
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
        MType _ret = (MType)n.f0.accept(this, argu);
        if(n.f0.which == 3) {
            String name = _ret.getName();
            MVar nVar = null;
            if(argu instanceof MClass) {
                nVar = ((MClass) argu).getVar(name);
            }
            else if(argu instanceof MMethod) {
                nVar = ((MMethod) argu).getVar(name);
            }
            else if(argu instanceof MCallList) {
                nVar = ((MCallList) argu).getContextMethod().getVar(name);
            }
            if(nVar == null) {
                PrintError.instance.printError(_ret.getLine(),_ret.getColumn(),"find a var not defined");
            }
            else {
                _ret.setName(nVar.getType());
            }
        }
        return _ret;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public Object visit(IntegerLiteral n, Object argu) {
        n.f0.accept(this, argu);
        return new MType("int",n.f0.beginLine,n.f0.beginColumn);
    }

    /**
     * f0 -> "true"
     */
    public Object visit(TrueLiteral n, Object argu) {
        n.f0.accept(this, argu);
        return new MType("boolean",n.f0.beginLine,n.f0.beginColumn);
    }

    /**
     * f0 -> "false"
     */
    public Object visit(FalseLiteral n, Object argu) {
        n.f0.accept(this, argu);
        return new MType("boolean",n.f0.beginLine,n.f0.beginColumn);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public Object visit(Identifier n, Object argu) {
        MType _ret = new MType(n.f0.tokenImage,n.f0.beginLine,n.f0.beginColumn);
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "this"
     */
    public Object visit(ThisExpression n, Object argu) {
        String nClassName = null;
        if(argu instanceof MMethod) {
            nClassName = ((MMethod) argu).getClassName();
        }
        else if(argu instanceof MCallList) {
            nClassName = ((MCallList) argu).getContextMethod().getClassName();
        }
        else {
            PrintError.instance.printError(n.f0.beginLine, n.f0.beginColumn, "find 'this' outside a method");
        }
        MClass nClass = MClassList.instance.findClass(nClassName);
        n.f0.accept(this, argu);
        return new MType(nClass.getName(),n.f0.beginLine,n.f0.beginColumn);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public Object visit(ArrayAllocationExpression n, Object argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);

        MType exp = (MType)n.f3.accept(this, argu);
        checkExpType(exp,"int","index of an array in definition is not a int");

        n.f4.accept(this, argu);
        return new MType("int[]",exp.getLine(),exp.getColumn());
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public Object visit(AllocationExpression n, Object argu) {
        n.f0.accept(this, argu);

        MType exp = (MType)n.f1.accept(this, argu);
        classCheck(exp.getName(),exp.getLine(),exp.getColumn());

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return new MType(exp.getName(),exp.getLine(),exp.getColumn());
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public Object visit(NotExpression n, Object argu) {
        n.f0.accept(this, argu);
        MType exp = (MType)n.f1.accept(this, argu);
        checkExpType(exp,"boolean","expression followed ! is not boolean");
        return new MType("boolean",exp.getLine(),exp.getColumn());
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public Object visit(BracketExpression n, Object argu) {
        n.f0.accept(this, argu);
        MType _ret = (MType)n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }
}
