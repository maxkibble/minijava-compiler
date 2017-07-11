package spiglet.spiglet2kanga;

import com.sun.org.apache.xpath.internal.operations.Bool;
import kanga.syntaxtree.IntegerLiteral;

import java.util.*;

/**
 * Created by maxkibble on 11/29/15.
 */
public class SMethod {
    public String name;
    protected ArrayList<SStat> statArrayList;
    protected HashSet<Integer> tempSet;
    protected TreeMap<Integer,Integer> regForTemp;
    public int paramNum,spilledNum,spilledSize;

    class Pair implements Comparable<Pair> {
        public int t1, t2;

        public Pair(int _t1, int _t2) {
            this.t1 = _t1;
            this.t2 = _t2;
        }

        @Override
        public int compareTo(Pair o) {
            // TODO Auto-generated method stub
            if (this.t1 < o.t1) return -1;
            if (this.t1 > o.t1) return 1;
            if (this.t2 < o.t2) return -1;
            if (this.t2 > o.t2) return 1;
            return 0;
        }
    }

    protected TreeMap<Pair,Boolean> edge;

    public SMethod(String name) {
        this.name = name;
        statArrayList = new ArrayList<SStat>();
    }

    public void addStat(SStat sStat) {
        statArrayList.add(sStat);
    }

    public boolean update(SStat sStat1,SStat sStat2) {
        if(sStat2 == null) return false;
        boolean _ret = false;
        for(int temp : sStat2.usedTempList) {
            if(!sStat1.outSet.contains(temp)) {
                _ret = true;
                sStat1.outSet.add(temp);
            }
        }
        for(int temp :  sStat2.outSet) {
            if(temp != sStat1.genTemp && !sStat1.outSet.contains(temp)) {
                _ret = true;
                sStat1.outSet.add(temp);
            }
        }
        return _ret;
    }

    private void buildGraph() {
        HashMap<String,SStat> labelStmt = new HashMap<String,SStat>();
        for(SStat sStat : statArrayList) {
            String label = sStat.entryLabel;
            if(label != null) labelStmt.put(label,sStat);
        }
        for(int i = 0; i < statArrayList.size(); i++) {
            SStat sStat = statArrayList.get(i);
            if(!sStat.isUnconditionalJump && i+1 < statArrayList.size()) {
                sStat.nextStmt1 = statArrayList.get(i+1);
            }
            if(sStat.jmupLabel != null) {
                sStat.nextStmt2 = labelStmt.get(sStat.jmupLabel);
            }
        }
        while(true) {
            boolean flag = false;
            for(SStat sStat : statArrayList) {
                flag |= update(sStat,sStat.nextStmt1);
                flag |= update(sStat,sStat.nextStmt2);
            }
            if(!flag) break;
        }
        tempSet = new HashSet<Integer>();
        edge = new TreeMap<Pair,Boolean>();
        for(SStat sStat : statArrayList) {
            tempSet.addAll(sStat.outSet);
        }
        for(int temp1 : tempSet) {
            for(int temp2 : tempSet) {
                if(temp1 < temp2) {
                    boolean flag = false;
                    for(SStat sStat : statArrayList) {
                        if(sStat.outSet.contains(temp1) && sStat.outSet.contains(temp2)) {
                            flag = true;
                            break;
                        }
                    }
                    edge.put(new Pair(temp1,temp2),flag);
                    edge.put(new Pair(temp2,temp1),flag);
                }
            }
        }
    }

    public void alloc() {
        buildGraph();
        spilledNum = Math.max(0,paramNum-4);
        regForTemp = new TreeMap<Integer,Integer>();
        spilledSize = 0;
        for(int temp1 : tempSet) {
            regForTemp.put(temp1,spilledSize++);
        }
    }

    public int getReg(int t) {
        if(tempSet.contains(t) == false) return -1;
        else return regForTemp.get(t);
    }
}
