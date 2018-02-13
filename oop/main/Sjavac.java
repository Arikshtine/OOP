package oop.ex6.main;

import oop.ex6.lines.lineException.LineException;
import oop.ex6.scopes.GlobalScope;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * this class Represents the main class that run the check on the sjava file we want to check that okay.
 * this class also used the other class.
 * if the sjava file pass the check and he is legal we print "0", otherwise we print "1". if the file not
 * javac or the argument not good we throw Exception and print "2".
 */
public class Sjavac {
    public static final int DIGIT_FOR_GOOD_CODE = 0;
    public static final int DIGIT_FOR_WRONG_CODE = 1;
    public static final int DIGIT_FOR_IO_EXCEPTION = 2;
    public static final String MSG_FOR_IO_EXCEPTION = "Warning: Running on a non s-java file";

    private ArrayList<Pattern> patternsList;
    private GlobalScope globalScope;
    private FileReader myFileReader;
    private File myFile;
    private BufferedReader myBufferedReader;
    private String lineValue;
    private String filePath;
    private HashSet<Integer> setOfUnsupportedCasesForGlobalScope;


    public static void main(String[] args) {
        Sjavac mySjavac;
        try {
            mySjavac = new Sjavac();
            String filePath = args[0];
            mySjavac.setOfUnsupportedCasesForGlobalScope = Patterns.createSetOfUnsupportedCasesForGlobal();
            mySjavac.patternsList = Patterns.createPatternList();
            mySjavac.filePath = filePath;
            mySjavac.myFile = new File(filePath);
            mySjavac.myFileReader = new FileReader(mySjavac.myFile);
            mySjavac.myBufferedReader = new BufferedReader(mySjavac.myFileReader);
            mySjavac.globalScope = new GlobalScope(mySjavac);
            /*if every thing is okay so far*/
            System.out.println(DIGIT_FOR_GOOD_CODE);
        } catch (IOException | ArrayIndexOutOfBoundsException e1) {
            System.err.println(MSG_FOR_IO_EXCEPTION);
            System.out.println(DIGIT_FOR_IO_EXCEPTION);
        } catch (LineException e2) {
            System.out.println(DIGIT_FOR_WRONG_CODE);
        }
        }



    /** move to the next line at the file sjava
     * @return the next line. otherwise throw exception.
     */
    public String nextLine(){
        try {
            this.lineValue = this.myBufferedReader.readLine();
            return this.lineValue;
        }
        catch (IOException e){
            return null;
        }
    }

    /**
     * Back to top of file. we used this after we keep all the global variable.
     * @throws FileNotFoundException - if the file not exists.
     */
    public void rereadTheFile() throws FileNotFoundException {
        myFile = new File(filePath);
        this.myFileReader = new FileReader(myFile);
        this.myBufferedReader = new BufferedReader(myFileReader);
        this.myBufferedReader = new BufferedReader(this.myFileReader);
    }

    /**
     * @return the hashSet of all cases of the illegal lines at the global scope.
     */
    public HashSet<Integer> getSetOfUnsupportedCasesForGlobalScope(){
        return setOfUnsupportedCasesForGlobalScope;
    }

    /**
     * @return the pattern list of all kind of pattern we used at this code.
     */
    public ArrayList<Pattern> getPatternsList(){return patternsList;}
}
