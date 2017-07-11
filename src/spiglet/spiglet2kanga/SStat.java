package spiglet.spiglet2kanga;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by maxkibble on 11/29/15.
 */
public class SStat {
    public ArrayList<Integer> usedTempList;
    public String type,jmupLabel,entryLabel;
    public boolean isUnconditionalJump = false;
    public int genTemp;
    public SStat nextStmt1,nextStmt2;
    public HashSet<Integer> outSet;

    public SStat(String type) {
        this.type = type;
        genTemp = -1;
        usedTempList = new ArrayList<Integer>();
        outSet = new HashSet<Integer>();
    }

    public void addUsedTemp(String tempId) {
        usedTempList.add(Integer.valueOf(tempId));
    }
}
