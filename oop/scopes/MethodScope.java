package oop.ex6.scopes;

import oop.ex6.lines.lineException.LineException;
import oop.ex6.main.Patterns;
import oop.ex6.main.Sjavac;
import oop.ex6.variables.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * this class Represents a Method scope that keeps all other scoops inside.
 * we also keep the "father" of the scope that is the global scoop.
 */
public class MethodScope extends Scope {

    public static final String TYPE_NAME = "method";
    public static final String NAME_PATTERN = "([a-zA-Z][a-zA-Z_\\d]*)\\s*\\(\\s*";
    public static final String NAME_VARIABLE_PATTERN = "\\s*[a-zA-Z][a-zA-Z_\\d]*\\s*";
    public static final String COMMA_PATTERN = "\\s*,\\s*";
    public static final String BRACKET_PATTERN = "\\s*\\)\\s*\\{\\s*";
    public static final String ALL_TYPE = "\\s*(int|boolean|char|double|String)\\s*";
    public static final String END_LINE_PATTERN = "\\s*\\)\\s*\\{\\s*";
    private HashMap<String,Variable> copiedGlobalVariableHashMap;
    private String methodName;
    private ArrayList<Variable> methodParametersVariable = new ArrayList<Variable>();
    private boolean isLastLineReturn = false;

    /**
     * constructor that make a new method scoop, we keep the father of this scoop, the local Variable of this
     * scope, the name of the scope and the parameters .
     * @param mySjavac the sjava file we handle with.
     * @param tempLine the first line of the scope.
     * @param globalScope the scope that this scope nested in.
     * @throws LineException if one of the lines in the scope are illegal.
     */
    public MethodScope(Sjavac mySjavac,String tempLine, GlobalScope globalScope) throws LineException {
        /*here we put the method treatsignature and treatscope*/
        methodParametersVariable = new ArrayList<Variable>();
        methodName = TYPE_NAME;
        this.father = globalScope;
        this.dealWithMethodScopeFirstLine(tempLine);
        //todo put the check of the subject
    }

    /**
     * this method handle with the line in the method scope and build the variable and other scope inside her.
     * the mysjavac points at the line of the subject of the method.
     * @param mySjavac the sjava file we handle with.
     * @throws LineException if thar line that illegal.
     */
    public void treatScope(Sjavac mySjavac) throws LineException{
        try {
            this.addVariableToSetFromParamsList();
            GlobalScope globalScope = (GlobalScope)this.father;
            this.copiedGlobalVariableHashMap = globalScope.copyGlobalVariableHahMap();
            super.treatScope(mySjavac);
        }
        catch (LineException e){
            throw new LineException();
        }

    }

    /** add variable to the set of parameters list.
     * @throws LineException if the name of the new variable is already exists.
     */
    public void addVariableToSetFromParamsList() throws LineException {
        for (Variable tempVar : this.methodParametersVariable){
            if (this.localVariableHashMap.containsKey(tempVar.getVariableName()))
                throw new LineException();
            this.localVariableHashMap.put(tempVar.getVariableName(),tempVar);
        }
    }

    /**
     * @return  the Type Of Scope.
     */
    public String getTypeOfScope(){
        return TYPE_NAME;
    }

    /**
     * @return the Copied Global Variable HashMap.
     */
    public HashMap<String,Variable> getCopiedGlobalVariableHashMap(){return copiedGlobalVariableHashMap;}

    /**
     * @return the Method Name.
     */
    public String getMethodName(){return methodName;}

    /**
     * @return the Method Parameters Variable.
     */
    public ArrayList<Variable> getMethodParametersVariable(){return methodParametersVariable;}

    public boolean getIsLastLineReturn(){return isLastLineReturn;}

    public void changeIsLastLineReturn(int indicator){
        if (indicator == Patterns.INDEX_OF_EMPTY_LIST_PATTEN){
            return;
        }
        if (indicator == Patterns.INDEX_OF_RETURN_PATTERN){
            isLastLineReturn = true;
            return;
        }
        isLastLineReturn = false;
    }

    /**
     * deal With Method Scope First Line.
     * @param tempLine the line of the first scope after cuting the start.
     * @throws LineException if the first line is illegal.
     */
    public void dealWithMethodScopeFirstLine(String tempLine) throws LineException {
        Pattern METHOD_START_LINE  = Pattern.compile(NAME_PATTERN);
        Matcher m = METHOD_START_LINE.matcher(tempLine);
        if (m.find() && m.start() == 0) {
            this.methodName = m.group(1);
            this.makeMethodParametersVariableIsFinalPart(tempLine.substring(m.end()));
        }
        else
            throw new LineException();
    }

    /** make Method Parameters Variable and check if them Final.
     * @param tempLine the first line of the method after cuting the start.
     * @throws LineException if the first line is illegal.
     */
    public void makeMethodParametersVariableIsFinalPart(String tempLine) throws LineException {
        Matcher caseOfNoneParmetars = Pattern.compile(BRACKET_PATTERN).matcher(tempLine);
        Matcher m = Pattern.compile("Patterns.FINAL_PATTERN").matcher(tempLine);
        if(caseOfNoneParmetars.matches())
            return;
        if (m.find() && m.start() == 0)
            this.makeMethodParametersVariableTypePart(tempLine.substring(m.end()), true);
        else {
            this.makeMethodParametersVariableTypePart(tempLine, false);
        }
    }

    /**
     * make Method Parameters Variable and check what the Type.
     * @param tempLine the first line of the method after cuting the start.
     * @param isFinal if the variable is final.
     * @throws LineException if the type is illegal.
     */
    public void makeMethodParametersVariableTypePart(String tempLine, boolean isFinal) throws LineException {
        Matcher m = Pattern.compile(ALL_TYPE).matcher(tempLine);
        if (m.find() && m.start() == 0)
            this.makeMethodParametersVariableNamePart(tempLine.substring(m.end()), isFinal, m.group(1));
        else
            throw new LineException();
    }

    /**
     * make Method Parameters Variable and take the variable Name.
     * @param tempLine  the first line of the method after cuting the start.
     * @param isFinal if the variable is final.
     * @param variableType the type of the variable.
     * @throws LineException if the name is illegal or already exists.
     */
    public void makeMethodParametersVariableNamePart(String tempLine, boolean isFinal, String variableType)
            throws LineException {
        Matcher m = Pattern.compile(NAME_VARIABLE_PATTERN).matcher(tempLine);
        if (m.find()&& m.start() == 0) {
            this.methodParametersVariable.add(new Variable(variableType, tempLine.substring(0, m.end()),
                    true, isFinal));
            this.makeMethodParametersVariableIsMore(tempLine.substring(m.end()));
        }
        else
            throw new LineException();
    }

    /**
     * make Method Parameters Variable and check if thar are more variable.
     * @param tempLine the first line of the method after cuting the start.
     * @throws LineException if the line is illegal.
     */
    public void makeMethodParametersVariableIsMore(String tempLine) throws LineException {

        Matcher moreVarible = Pattern.compile(COMMA_PATTERN).matcher(tempLine);
        Matcher endLine = Pattern.compile(END_LINE_PATTERN).matcher(tempLine);
        if (moreVarible.find() && moreVarible.start() == 0) {
            this.makeMethodParametersVariableIsFinalPart(tempLine.substring(moreVarible.end()));
            return;
        }
        if (!(endLine.matches() && endLine.start() == 0)) {
            throw new LineException();
        }
    }
}
