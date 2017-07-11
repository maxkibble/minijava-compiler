package minijava.symboltable;

/**
 * Created by maxkibble on 2015/10/15.
 */
public class MType {
    protected String name;
    protected int line = 0;
    protected int column = 0;
    protected int tempNum = 0,offset;

    public MType() {}

    public MType(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setTempNum(int tempNum) {
        this.tempNum = tempNum;
    }

    public int getTempNum() {
        return tempNum;
    }

    public boolean judgeTemp() {
        return tempNum > 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
