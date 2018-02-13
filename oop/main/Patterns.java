package oop.ex6.main;

import oop.ex6.types.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class Represents the Patterns class. all the kind we used the code we put here. its like class of
 * "magic numbers"
 */
public class Patterns {
    public static final Pattern FINAL_PATTERN = Pattern.compile("\\s*final\\s+");
    public static final Pattern COMMENT_PATTERN = Pattern.compile("//");
    public static final Pattern METHOD_PATTERN = Pattern.compile("\\s*void\\s+");
    public static final Pattern IF_PATTERN = Pattern.compile("\\s*if\\s*");
    public static final Pattern WHILE_PATTERN = Pattern.compile("\\s*while\\s*");
    public static final Pattern END_SCOPE_PATTERN = Pattern.compile("\\s*}\\s*");
    public static final Pattern RETURN_PATTERN = Pattern.compile("\\s*return\\s*;\\s*");
    public static final Pattern VARIABLE_NAME_PATTERN_WITHOUT_DECLARATION = Pattern.compile("\\s*" +
            "(([a-zA-Z][a-zA-Z_\\d]*)|(_[a-zA-Z_\\d]+))\\s*=");
    public static final Pattern METHOD_CALL_PATTERN = Pattern.compile("\\s*([a-zA-Z][a-zA-Z_\\d]*)\\s*\\(");
    public static final Pattern EMPTY_LIST_PATTERN = Pattern.compile("\\s*");
    public static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("\\s*=\\s*");
    public static final Pattern SEPERATE_PATTERN = Pattern.compile("\\s*,\\s*");
    public static final Pattern END_OF_LINE_PATTERN = Pattern.compile("\\s*;\\s*");
    public static final Pattern METHOD_CALL_PARAMETERS_PATTERN = Pattern.compile("([^\\)\\;]*)\\)\\s*\\;");
    public static final Pattern
            VARIABLE_NAME_PATTERN = Pattern.compile("\\s*(([a-zA-Z][a-zA-Z_\\d]*)|(_[a-zA-Z_\\d]+))\\s*");
    /*pattens that check if some line is a valid if/while first line and catch as group 4 the condition
    between the bruckets*/
    public static final Pattern IF_OR_WHILE_FIRST_LINE_PATTERN = Pattern.compile("\\s*((if)|(while))\\s*\\(" +
            "\\s*(.*)\\s*\\)" + "\\s*\\{\\s*");
    public static final Pattern CUT_EDGE = Pattern.compile("\\s*(([^\\s].*[^\\s])|([^\\s]))\\s*");
    public static final String CLOSE_BRACKET = "\\s*(";
    public static final String OPEN_BRACKET = ")\\s+";
    public static final int IF_OR_WHILE_FIRST_LINE_INDEX_OF_CONDITION = 4;


    public static final int INDEX_OF_FINAL_PATTERN = 0;
    public static final int INDEX_OF_COMMENT_PATTERN = 1;
    public static final int INDEX_OF_METHOD_PATTERN = 2;
    public static final int INDEX_OF_IF_PATTERN = 3;
    public static final int INDEX_OF_WHILE_PATTERN =4;
    public static final int INDEX_OF_END_SCOPE_PATTERN = 5;
    public static final int INDEX_OF_RETURN_PATTERN = 6;
    public static final int INDEX_OF_VARIABLE_NAME_PATTERN_WITHOUT_DECLARATION = 7;
    public static final int INDEX_OF_METHOD_CALL_PATTERN = 8;
    public static final int INDEX_OF_EMPTY_LIST_PATTEN = 9;
    public static final int FIRST_INDEX_OF_TYPE_PATTERNS = 10;



    /**
     * create pattern list of the pattern of the line in the scope that we want to check.
     * @return this list.
     */
    public static ArrayList<Pattern> createPatternList(){

        ArrayList<Pattern> patternList = new ArrayList<Pattern>();
        patternList.add(FINAL_PATTERN);
        patternList.add(COMMENT_PATTERN);
        patternList.add(METHOD_PATTERN);
        patternList.add(IF_PATTERN);
        patternList.add(WHILE_PATTERN);
        patternList.add(END_SCOPE_PATTERN);
        patternList.add(RETURN_PATTERN);
        patternList.add(VARIABLE_NAME_PATTERN_WITHOUT_DECLARATION);
        patternList.add(METHOD_CALL_PATTERN);
        patternList.add(EMPTY_LIST_PATTERN);
        for (String typeName : Type.typeNamesList){
            patternList.add(Pattern.compile(CLOSE_BRACKET + typeName + OPEN_BRACKET ));
        }
        return patternList;
    }

    /**
     * here we cut the spaces at the edge in the bracket.
     * @param temp the line we want to handel with.
     * @return the line after we cut the bracket.
     */
    public static String cutSpacesInEdges(String temp){
        Matcher m = CUT_EDGE.matcher(temp);
        m.matches();
        return m.group(1);
    }

    /**
     * create hashSet of the index of all illegal lines at the pattern list.
     * @return this hashSet.
     */
    public static HashSet<Integer> createSetOfUnsupportedCasesForGlobal(){
        HashSet<Integer> setToReturn = new HashSet<Integer>();
        setToReturn.add(INDEX_OF_IF_PATTERN);
        setToReturn.add(INDEX_OF_WHILE_PATTERN);
        setToReturn.add(INDEX_OF_END_SCOPE_PATTERN);
        setToReturn.add(INDEX_OF_RETURN_PATTERN);
        setToReturn.add(INDEX_OF_METHOD_CALL_PATTERN);
        return setToReturn;
    }
}
