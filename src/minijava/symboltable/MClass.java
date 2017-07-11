package minijava.symboltable;

import kanga.ParseException;
import kanga.syntaxtree.IntegerLiteral;
import minijava.typecheck.PrintError;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by maxkibble on 2015/10/15.
 */
public class MClass extends MType {

    protected ArrayList<MMethod> mMethodArrayList = new ArrayList<MMethod>();
    protected ArrayList<MVar> mVarArrayList = new ArrayList<MVar>();
    protected String parentClass;

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public MClass(String name,int line,int column) {
        super(name,line,column);
    }

    public boolean repeatedMethod(String methodName,String returnType,ArrayList<MVar> paramList) {
        MClass nClass = this;
        MMethod nMethod = this.getMethod(methodName);
        //there will be no circle extension here
        while(nClass != null) {
            int s = nClass.mMethodArrayList.size();
            for (int i = 0; i < s; i++) {
                String ithName = nClass.mMethodArrayList.get(i).getName();
                if (methodName.equals(ithName)) {
                    if(!nClass.mMethodArrayList.get(i).judgeEqualParamList(paramList) || !returnType.equals(nClass.mMethodArrayList.get(i).getReturnType())) {
                        //method name equal but param not equal
                        PrintError.instance.printError(nMethod.getLine(), nMethod.getColumn(), "Method " + nMethod.getName() + " repeated declared");
                        return true;
                    }
                }
            }
            if(nClass.getParentClass() != null) nClass = MClassList.instance.findClass(nClass.getParentClass());
            else nClass = null;
        }
        return false;
    }

    public ArrayList<Integer> overrideMethod(String methodName) {
        ArrayList<Integer> ans = new ArrayList<Integer>();
        MClass nClass = MClassList.instance.findClass(this.getParentClass());
        //there will be no circle extension here
        while(nClass != null) {
            int s = nClass.mMethodArrayList.size();
            for (int i = 0; i < s; i++) {
                String ithName = nClass.mMethodArrayList.get(i).getName();
                if (methodName.equals(ithName)) {
                    ans.add(nClass.mMethodArrayList.get(i).getOffset());
                }
            }
            if(nClass.getParentClass() != null) nClass = MClassList.instance.findClass(nClass.getParentClass());
            else nClass = null;
        }
        return ans;
    }

    public int getVarSize() {
        int ans = 0;
        if(getParentClass() != null) {
            MClass mClass = MClassList.instance.findClass(getParentClass());
            ans = mClass.getVarSize();
        }
        //System.out.println(getName() + " " + mVarArrayList.size());
        return ans+mVarArrayList.size()*4;
    }

    public int getMethodSize() {
        int ans = 0;
        if(getParentClass() != null) {
            MClass mClass = MClassList.instance.findClass(getParentClass());
            ans = mClass.getMethodSize();
        }
        return ans+mMethodArrayList.size()*4;
    }

    public ArrayList<MMethod> getMethodList() {
        return mMethodArrayList;
    }

    public ArrayList<MVar> getmVarArrayList() {
        return  mVarArrayList;
    }

    public boolean findMethodName(String name) {
        int s = mMethodArrayList.size();
        for(int i = 0;i < s; i++) {
            if(name.equals(mMethodArrayList.get(i).getName())) return true;
        }
        return false;
    }

    public boolean findVarName(String name) {
        int s = mVarArrayList.size();
        for(int i = 0; i < s; i++) {
            if(name.equals(mVarArrayList.get(i).getName())) return true;
        }
        return false;
    }

    public boolean addMethod(MMethod nMethod) {
        if(repeatedMethod(nMethod.getName(),nMethod.getReturnType(),nMethod.getmParamArrayList())) {
            return false;
        }
        mMethodArrayList.add(nMethod);
        return true;
    }

    public boolean repeatedVar(String varName) {
        int s = mVarArrayList.size();
        for (int i = 0; i < s; i++) {
            String ithName = mVarArrayList.get(i).getName();
            if (varName.equals(ithName)) return true;
        }
        return false;
    }

    public boolean addVar(MVar nVar) {
        if(repeatedVar(nVar.getName())) {
            PrintError.instance.printError(nVar.getLine(),nVar.getColumn(),"Var " + nVar.getName() + " repeated declared");
            return false;
        }
        mVarArrayList.add(nVar);
        return true;
    }


    //get a method object according to its name and paramList
    public MMethod getMethod(String name) {
        MClass nClass = this;
        //there will be no circle extension here
        while(nClass != null) {
            int s = nClass.mMethodArrayList.size();
            for (int i = 0; i < s; i++) {
                String ithName = nClass.mMethodArrayList.get(i).getName();
                if (name.equals(ithName)) {
                    return nClass.mMethodArrayList.get(i);
                }
            }
            if(nClass.getParentClass() != null) nClass = MClassList.instance.findClass(nClass.getParentClass());
            else nClass = null;
        }
        return null;
    }

    public MVar getVar(String name) {
        MClass nClass = this;
        //there will be no circle extension here
        while(nClass != null) {
            int s = nClass.mVarArrayList.size();
            for (int i = 0; i < s; i++) {
                String ithName = nClass.mVarArrayList.get(i).getName();
                if (name.equals(ithName)) return nClass.mVarArrayList.get(i);
            }
            if(nClass.getParentClass() != null) nClass = MClassList.instance.findClass(nClass.getParentClass());
            else nClass = null;
        }
        return null;
    }

    /**
     * mark the offset for each var and method in class
     * @return whole size of this class and father class
     */
    public int varPosition() {
        int offset = 0;
        if (getParentClass() != null) {
            MClass mClass = MClassList.instance.findClass(getParentClass());
            offset += mClass.varPosition();
        }
        for (MVar mVar : mVarArrayList) {
            mVar.setOffset(offset);
            offset += 4;
        }
        return offset;
    }

    public int methodPosition() {
        int offset = varPosition();
        if(getParentClass() != null) {
            MClass mClass = MClassList.instance.findClass(getParentClass());
            offset += mClass.methodPosition();
        }
        for(MMethod mMethod : mMethodArrayList) {
            mMethod.setOffset(offset);
            String name = this.getName() + "_" + mMethod.getName();
            mMethod.setPigketName(name);
            offset += 4;
        }
        return offset;
    }

    public void completeClass() {
        if(getParentClass() == null) return;
        MClass parentClass = MClassList.instance.findClass(getParentClass());
        parentClass.completeClass();
        for(MMethod mMethod : parentClass.mMethodArrayList) {
            if(findMethodName(mMethod.getName())) continue;
            mMethodArrayList.add(mMethod);
        }
        for(MVar mVar : parentClass.mVarArrayList) {
            if(findVarName(mVar.getName())) continue;
            mVarArrayList.add(mVar);
        }
    }

    public int allocTemp(int currentTemp) {
        int offset = 4;
        for(MVar mVar : mVarArrayList) {
            mVar.setOffset(offset);
            offset += 4;
        }
        offset = 0;
        for(MMethod mMethod : mMethodArrayList) {
            mMethod.setOffset(offset);
            String name = getName() + "_" + mMethod.getName();
            mMethod.setPigketName(name);
            offset += 4;
            currentTemp = mMethod.allocTemp(currentTemp);
        }
        return currentTemp;
    }
}
