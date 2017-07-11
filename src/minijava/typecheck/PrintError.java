package minijava.typecheck;

/**
 * Created by maxkibble on 2015/10/18.
 */

public class PrintError {
    public static PrintError instance = new PrintError();
    public static boolean hasOut = false;
    public void printError(int line,int column,String content) {
        System.out.println("line:" + line + " column:" + column + '\n' + content);
        hasOut = true;
    }
}
