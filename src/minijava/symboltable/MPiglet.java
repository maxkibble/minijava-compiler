package minijava.symboltable;

/**
 * Created by maxkibble on 2015/10/27.
 */
public class MPiglet {
    protected StringBuilder code;
    protected boolean isNewLine = false;
    protected MClass mClass;
    protected MVar mVar;

    public StringBuilder getCode() {
        return code;
    }

    public MClass getmClass() {
        return mClass;
    }

    public void setmClass(MClass mClass) {
        this.mClass = mClass;
    }

    public MVar getmVar() {
        return mVar;
    }

    public void setmVar(MVar mVar) {
        this.mVar = mVar;
    }

    public MPiglet(String s) {
        isNewLine = true;
        code = new StringBuilder(s);
    }

    public void appendString(String s) {
        code.append(s);
    }

    public void appendCode(MPiglet mPiglet) {
        if(mPiglet == null) return;
        if(mPiglet.isNewLine) code.append("\n");
        else code.append(" ");
        code.append(mPiglet.getCode());
    }
}
