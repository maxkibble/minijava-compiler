package kanga.kanga2mips;

/**
 * Created by maxkibble on 2015/12/12.
 */
public class Environment {
    protected StringBuilder mipsCode;
    public boolean inStmt;

    public Environment() {
        mipsCode = new StringBuilder();
        inStmt = true;
    }

    public void appendMipsCode(String s) {
        mipsCode.append(s + "\n");
    }
}
