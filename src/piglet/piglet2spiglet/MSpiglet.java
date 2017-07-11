package piglet.piglet2spiglet;

import java.util.ArrayList;

/**
 * Created by maxkibble on 11/7/15.
 */
public class MSpiglet {
    private StringBuilder code;
    private boolean isNewLine = false;
    private String temp;
    private int opType;
    private ArrayList<String> tempList = new ArrayList<String>();

    public StringBuilder getCode() {
        return code;
    }

    public MSpiglet(String code) {
        this.code = new StringBuilder(code);
        isNewLine = true;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }

    public ArrayList<String> getTempList() {
        return tempList;
    }

    public void addTemp(String temp) {
        tempList.add(temp);
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getOp() {
        if(opType == 0) return "LT";
        else if(opType == 1) return "PLUS";
        else if(opType == 2) return "MINUS";
        else return "TIMES";
    }

    public void appendCode(MSpiglet mSpiglet) {
        if(mSpiglet == null) return;
        if(mSpiglet.isNewLine) code.append("\n");
        else code.append(" ");
        code.append(mSpiglet.code.toString());
    }
}
