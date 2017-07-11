package spiglet.spiglet2kanga;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by maxkibble on 11/26/15.
 */
public class Context {
    protected HashMap<String,SMethod> methodHashMap;
    public SMethod currentMethod;
    public SStat currentStat;
    public StringBuilder kangaCode;
    public boolean insideStat = false;
    public int vReg = 0;
    public int passingParam = -1,movetoReg = -1;

    public Context() {
        methodHashMap = new HashMap<String,SMethod>();
        kangaCode = new StringBuilder();
    }

    public void addMethod() {
        methodHashMap.put(currentMethod.name,currentMethod);
    }

    public void alloc() {
        for(SMethod sMethod: methodHashMap.values()) {
            sMethod.alloc();
        }
    }

    public void appendKangeCode(String s) {
        kangaCode.append(s + "\n");
    }

    public void setMethod(String name) {
        currentMethod = methodHashMap.get(name);
    }
}
