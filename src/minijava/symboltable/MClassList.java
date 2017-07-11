package minijava.symboltable;

import minijava.typecheck.PrintError;

import java.util.ArrayList;

/**
 * Created by maxkibble on 2015/10/15.
 */
public class MClassList extends MType {
    public static MClassList instance = new MClassList();

    private static int tempNumber = 20,labelNumber = 0;
    private ArrayList<MClass> mClassArrayList = new ArrayList<MClass>();

    private MClassList(){
        mClassArrayList.add(new MClass("int",0,0));
        mClassArrayList.add(new MClass("boolean",0,0));
        mClassArrayList.add(new MClass("int[]",0,0));
    }

    public ArrayList<MClass> getmClassArrayList() {
        return mClassArrayList;
    }

    public void setLabelNumber(int labelNumber) {
        MClassList.labelNumber = labelNumber;
    }

    public int getLabelNumber() {
        return labelNumber;
    }

    public int getTempNum() {
        return this.tempNumber;
    }

    public void setTempNum(int tempNum) {
        this.tempNumber = tempNum;
    }

    //judge repeated class
    public boolean repeatedClass(String className) {
        int s = mClassArrayList.size();
        for(int i = 0; i < s; i++) {
            String ithName = mClassArrayList.get(i).getName();
            if(className.equals(ithName)) return true;
        }
        return false;
    }

    //insert a new class in the list
    public boolean addClass(MClass nClass) {
        if(repeatedClass(nClass.getName())) {
            PrintError.instance.printError(nClass.getLine(),nClass.getColumn(),"Class " + nClass.getName() + " repeated declared");
            return false;
        }
        mClassArrayList.add(nClass);
        return true;
    }

    //search a class according to name
    public MClass findClass(String name) {
        int s = mClassArrayList.size();
        for(int i = 0; i < s; i++) {
            String ithName = mClassArrayList.get(i).getName();
            if(name.equals(ithName)) return mClassArrayList.get(i);
        }
        return null;
    }

    //judge whether class b is the father class of class a
    public boolean judgeParentClass(String a,String b) {
        MClass aa = MClassList.instance.findClass(a);
        while(aa != null) {
            if(aa.getName().equals(b)) return true;
            String parent = aa.getParentClass();
            aa = MClassList.instance.findClass(parent);
        }
        return false;
    }

    public void completeClass() {
        for(MClass mClass : mClassArrayList) {
            mClass.completeClass();
        }
    }

    public int allocTemp(int currentTemp) {
        for(MClass mClass : MClassList.instance.mClassArrayList) {
           currentTemp =  mClass.allocTemp(currentTemp);
        }
        return currentTemp;
    }
}
