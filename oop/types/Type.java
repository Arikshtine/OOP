package oop.ex6.types;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 11/06/2017.
 * This class is an assistance class that doesn't produce instance.
 * this class's goal is to manage easily the control with all the kinds of types.
 * this class holds for each type: 1)constant String variable that represent the name of the type, as it
 * appears in the code. 2) a regex that represent a valid value of the type.
 * the class also has 2 assistance static method, as detailed continued.
 */
public class Type{

    /*
    * INSTRUMENT HOW ADD NEW TYPE: (follow the followings steps):
    * 1) create here Constant of the name of the new type
    * 2) create here Constant of the regex String of the new type (the convert to new Pattern happens in the
    * class "Pattens" and the new pattern are part od the arrayList that returned from the static method
    * createPatternList().
    * 3) add the Constant of the name of the new type to the list : typeNamesList. in the end.
    * 4) Add new case to the method getRelevantRegex()
    * */

    /*Constants that represent for each type its represented name*/
    public static final String INT_TYPE_NAME = "int";
    public static final String DOUBLE_TYPE_NAME = "double";
    public static final String BOOLEAN_TYPE_NAME = "boolean";
    public static final String CHAR_TYPE_NAME = "char";
    public static final String STRING_TYPE_NAME = "String";

    /*Constant that represent for each type a regex that represented a valid value of the type. (it is of
    that there is spaces around the value. they are ignored as we was asked)*/
    public static final String INT_REGEX = "\\s*-?\\d+\\s*";
    public static final String DOUBLE_REGEX = "\\s*-?\\d+(\\.\\d+|\\d*)\\s*";
    public static final String BOOLEAN_REGEX = "\\s*(true|false|-?(\\d+(\\.\\d+|\\d*)))\\s*";
    public static final String CHAR_REGEX = "\\s*'.'\\s*";
    public static final String STRING_REGEX = "\\s*\".*\"\\s*";

    /*Constant that represents a list that contains all the types names.
    * if is useful because if we want to add new variable, by add the name to this list, the new type will
    * be added automatically to the list of pattens as valid situation of declaration of the new type*/
    public static final List<String> typeNamesList = Arrays.asList(INT_TYPE_NAME, DOUBLE_TYPE_NAME,
            BOOLEAN_TYPE_NAME, CHAR_TYPE_NAME, STRING_TYPE_NAME);


    /**
     * thiis method is a kind of factory of regex. the method returns suitable regex to some given type
     * @param typeName - name of some type
     * @return regexString - regex that suitable to the given type name
     */
    public static String getRelevantRegex(String typeName){
        switch (typeName){
            case INT_TYPE_NAME:
                return INT_REGEX;
            case DOUBLE_TYPE_NAME:
                return DOUBLE_REGEX;
            case BOOLEAN_TYPE_NAME:
                return BOOLEAN_REGEX;
            case CHAR_TYPE_NAME:
                return CHAR_REGEX;
            case STRING_TYPE_NAME:
                return STRING_REGEX;

        }
        return null;
    }

    /**
     *
     * @param type1
     * @param type2
     * @return - true - if type1 contains type2 (means that type1 can holds each value of type2)
     *           false - otherwise
     */
    public static boolean isType1ContainType2(String type1, String type2){
        switch (type1){
            case INT_TYPE_NAME:
                return (INT_TYPE_NAME.equals(type2)); /*int contains only int value*/
            case DOUBLE_TYPE_NAME:

                /*double contains int and double value*/
                return (INT_TYPE_NAME.equals(type2)||DOUBLE_TYPE_NAME.equals(type2));

            case BOOLEAN_TYPE_NAME:

                /*boolean contains int, double and boolean value*/
                return (INT_TYPE_NAME.equals(type2)||DOUBLE_TYPE_NAME.equals(type2)||BOOLEAN_TYPE_NAME.equals
                        (type2));

            case CHAR_TYPE_NAME:
                return (CHAR_TYPE_NAME.equals(type2)); /*char contains only char value*/
            case STRING_TYPE_NAME:
                return (STRING_TYPE_NAME.equals(type2)); /*String contains only String value*/
        }

        /*if type1 isn't in the cases, the method returns false .*/
        return false;
    }


}
