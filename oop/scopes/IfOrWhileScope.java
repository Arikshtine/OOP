package oop.ex6.scopes;

import oop.ex6.lines.lineException.LineException;
import oop.ex6.main.Patterns;
import oop.ex6.main.Sjavac;
import oop.ex6.types.Type;
import oop.ex6.variables.Variable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class Represents a if/while scope that keeps all other scoops inside.
 * we also keep the "father" of the scope that is the scoop he is nested.
 */
public class IfOrWhileScope extends Scope {

    public static final String TYPE_NAME = "if or while";
    public static final String OR_AND_SPLIT_IN_CONDITION = "\\s*((\\|\\|)|(&&))\\s*";

    /**
     * constructor that make a new scoop, we keep the father of this scoop, the local Variable of this scope.
     * @param mySjavac the sjava file we handle with.
     * @param tempLine the first line of the if/while scope (the signature).
     * @param father the scope that this scoop nested in.
     * @throws LineException if thar are line that illegal at this scope.
     */
    public IfOrWhileScope(Sjavac mySjavac, String tempLine, Scope father) throws LineException {
        try {
            this.father = father;
            this.localVariableHashMap = new HashMap<String, Variable>();
            this.checkTheCondition(tempLine);
            this.treatScope(mySjavac);
        }
        catch (LineException e){
            throw new LineException();
        }
    }

    /**
     * @return the type of the scoop.
     */
    public String getTypeOfScope() {
        return TYPE_NAME;
    }


    /**
     *  we check the condition in the signature of the if/while scope.
     * @param tempLine the line were the signature of the scope.
     * @throws LineException if the condition is illegal.
     */
    /*assumption - tempLine is all the line that start with if/while*/
    public void checkTheCondition(String tempLine) throws LineException {
        try {
            Matcher m1 = Patterns.IF_OR_WHILE_FIRST_LINE_PATTERN.matcher(tempLine);
            if (!m1.matches()) {
                throw new LineException();
            }
            String theCondition = m1.group(Patterns.IF_OR_WHILE_FIRST_LINE_INDEX_OF_CONDITION);
            theCondition = Patterns.cutSpacesInEdges(theCondition);
            String[] conditionsList = theCondition.split(OR_AND_SPLIT_IN_CONDITION);
            for (String tempCond : conditionsList){
                if (!this.isCondValidValue(tempCond) && !this.isCondValidVariable(tempCond))
                    throw new LineException();
            }
        }
        catch (LineException e){
            throw new LineException();
        }
    }

    /**
     * check if the value in the condition is legal and valid.
     * @param tempCond the string of the value.
     * @return if the condition is okay.
     */
    public boolean isCondValidValue(String tempCond){
        if (tempCond.equals("true")||tempCond.equals("false"))
            return true;
        Pattern booleanValuePattern = Pattern.compile(Type.BOOLEAN_REGEX);
        return booleanValuePattern.matcher(tempCond).matches();
    }

    /**
     * check if the variable in the condition is legal and valid.
     * @param tempCond the string of the variable.
     * @return if the condition is okay.
     * @throws LineException if the variable make problem.
     */
    public boolean isCondValidVariable(String tempCond) throws LineException{
        try {
            Variable tempVariable = this.getTheMostSpecificVariableWIthGivenName(tempCond);
            if (!tempVariable.getIsVariableInitialized())
                return false;
            return (Type.isType1ContainType2(Type.BOOLEAN_TYPE_NAME,tempVariable.getVariableType()));
        }
        catch (LineException e){
            throw new LineException();
        }
    }
}
