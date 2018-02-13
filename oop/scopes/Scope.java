package oop.ex6.scopes;

import oop.ex6.lines.lineException.LineException;
import oop.ex6.main.Patterns;
import oop.ex6.main.Sjavac;
import oop.ex6.types.Type;
import oop.ex6.variables.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class Represents the scope class, this abstract class . he is super class of GlobalScope,
 * IfOrWhileScope and MethodScope.
 */
public abstract class Scope {

    public static final String COMMA_PATTERN = "\\s*,\\s*";
    public static final String OPEN_BRACKETS = ".*\\{\\s*";
    public static final String CLOSE_BRACKETS = "\\s*}\\s*";
    public static final String TRUE_STRING = "true";
    public static final String FALSE_STRING = "false";
    protected ArrayList<Scope> innerScopes = new ArrayList<Scope>();
    protected Scope father = null;
    protected HashMap<String,Variable> localVariableHashMap = new HashMap<String,Variable>();

    /**
     * abstract method.
     */
    abstract public String getTypeOfScope();

    /** this method handle with the line in the method scope and build the variable and other scope inside
     *  her. the mysjavac points at the line of the subject of the method.
     * @param mySjavac the sjava file we handle with.
     * @throws LineException if there are illegal line in the scope.
     */
    public void treatScope(Sjavac mySjavac) throws LineException{
        try {
            String tempLine;
            while ((tempLine = mySjavac.nextLine()) != null) {
                if(Patterns.END_SCOPE_PATTERN.matcher(tempLine).matches()){
                    if (this.getTypeOfScope().equals(MethodScope.TYPE_NAME)){
                        MethodScope tempMethodScope = (MethodScope)this;
                        if (!tempMethodScope.getIsLastLineReturn())
                            throw new LineException();
                        }
                    return;
                }
                int indicator = this.scopeCases(mySjavac, tempLine);
                if (this.getTypeOfScope().equals(MethodScope.TYPE_NAME)){
                    MethodScope tempMethodScope = (MethodScope)this;
                    tempMethodScope.changeIsLastLineReturn(indicator);
                }
            }
            throw new LineException();
        }
        catch (LineException e){
            throw new LineException();
        }
    }

    /** check the cases of the line in the scope.
     * @param mySjavac  the sjava file we handle with.
     * @param tempLine the first line of the scope.
     * @return the number of the case.
     * @throws LineException if none of the case is happen.
     */
    public int scopeCases(Sjavac mySjavac, String tempLine) throws LineException{
        try {
            ArrayList<Pattern> patternsList = mySjavac.getPatternsList();
            int forLoopCounter = -1;
            Matcher m;
            for (Pattern tempPattern : patternsList) {
                forLoopCounter++;
                m = tempPattern.matcher(tempLine);
                if (m.find() && m.start() == 0) {
                    if (forLoopCounter >= Patterns.FIRST_INDEX_OF_TYPE_PATTERNS) {
                        this.dealWithDeclarationPrefix(tempLine.substring(m.end()), m.group(1),
                                false);
                        return forLoopCounter;
                    }
                    switch (forLoopCounter){
                        case Patterns.INDEX_OF_FINAL_PATTERN:
                            this.dealWithFinalPrefix(tempLine.substring(m.end()), patternsList);
                            return forLoopCounter;
                        case Patterns.INDEX_OF_COMMENT_PATTERN:
                            return forLoopCounter;
                        case Patterns.INDEX_OF_METHOD_PATTERN:
                            throw new LineException();
                        case Patterns.INDEX_OF_IF_PATTERN:
                            this.innerScopes.add(new IfOrWhileScope(mySjavac,tempLine,this));
                            return forLoopCounter;
                        case Patterns.INDEX_OF_WHILE_PATTERN:
                            this.innerScopes.add(new IfOrWhileScope(mySjavac,tempLine,this));
                            return forLoopCounter;
                        case Patterns.INDEX_OF_RETURN_PATTERN:
                            if (!m.matches())
                                throw new LineException();
                            return forLoopCounter;
                        case Patterns.INDEX_OF_VARIABLE_NAME_PATTERN_WITHOUT_DECLARATION:
                            this.dealWithAssignmentPrefix(tempLine.substring(m.end()-1), m.group(1),
                                    null, false);
                            return forLoopCounter;
                        case Patterns.INDEX_OF_METHOD_CALL_PATTERN:
                            this.dealWithMethodCallPrefix(tempLine.substring(m.end()),m.group(1));
                            return forLoopCounter;
                        case Patterns.INDEX_OF_EMPTY_LIST_PATTEN:
                            if (m.matches())
                                return forLoopCounter;
                    }
                }
            }
            throw new LineException();
        }
        catch (LineException e){
            throw new LineException();
        }
    }


    /**
     *
     * @param line - the line starts from the first char after the "("
     * @param methodName the name of the method
     * @throws LineException if the call is illegal.
     * assumption : it happens only in method/while/of scops
     */
    public void dealWithMethodCallPrefix(String line,String methodName) throws LineException{
        try {
            MethodScope methodScope = this.getTheMethod(methodName);
            ArrayList<Variable> methodParameters = methodScope.getMethodParametersVariable();
            Matcher m = Patterns.METHOD_CALL_PARAMETERS_PATTERN.matcher(line);
            if (!m.matches())
                throw new LineException();
            Pattern p3 = Patterns.EMPTY_LIST_PATTERN;
            Matcher m3 = p3.matcher(m.group(1));
            String[] paramsOfTheCall;
            if (!m3.matches())
                paramsOfTheCall = m.group(1).split(COMMA_PATTERN);
            else{
                if (methodParameters.size()==0)
                    return;
                else
                    throw new LineException();
            }
            if (paramsOfTheCall.length != methodParameters.size()){
                /*means the call hasn't suitable amount of parameters, like the method*/
                throw new LineException();
            }
            for (int i = 0; i<paramsOfTheCall.length; i++){
                Matcher m1 = Patterns.VARIABLE_NAME_PATTERN.matcher(paramsOfTheCall[i]);
                if(m1.find()) {
                    if (m1.group(1).equals(TRUE_STRING) || m1.group(1).equals(FALSE_STRING))
                        return;
                }
                m1 = Patterns.VARIABLE_NAME_PATTERN.matcher(paramsOfTheCall[i]);
                if (m1.matches()){
                    Variable tempVar = this.getTheMostSpecificVariableWIthGivenName(m1.group(1));
                    String type1 = methodParameters.get(i).getVariableType();
                    String type2 = tempVar.getVariableType();
                    if (!tempVar.getIsVariableInitialized() || !Type.isType1ContainType2(type1,type2))
                        throw new LineException();
                }
                else{
                    String tempRejx = Type.getRelevantRegex(methodParameters.get(i).getVariableType());
                    Pattern p2 = Pattern.compile(tempRejx);
                    Matcher m2 = p2.matcher(paramsOfTheCall[i]);
                    if (!m2.matches())
                        throw new LineException();
                }
            }
        }
        catch (LineException e){
            throw new LineException();
        }
    }

    /**
     * @param methodName the method name.
     * @return the method object.
     * @throws LineException if the method is not there.
     */
    /*assumption: the operated scope is method/if/while scope (and not the global) */
    public MethodScope getTheMethod(String methodName) throws LineException{
        Scope tempScope = this;
        while (!tempScope.getTypeOfScope().equals(GlobalScope.TYPE_NAME))
            tempScope = tempScope.father;
        /*here the method is the global. now we look if the method is exist in the program*/
        for (Scope tempMethod : tempScope.innerScopes){
            MethodScope tempMethod1 = (MethodScope)tempMethod;
            if (tempMethod1.getMethodName().equals(methodName))
                return tempMethod1;
        }
        throw new LineException(); /*the method doesn't exist*/
    }


    /**
     * deal With Final Prefix at variable.
     * @param line the line with the final write there.
     * @param patternList the list of the patterns.
     * @throws LineException if the line after the final word is worng.
     */
    public void dealWithFinalPrefix(String line, ArrayList<Pattern> patternList) throws LineException{
        try {
            Pattern tempPattern;
            Matcher m;
            for (int i = Patterns.FIRST_INDEX_OF_TYPE_PATTERNS; i < patternList.size(); i++) {
                tempPattern = patternList.get(i);
                m = tempPattern.matcher(line);
                if (m.find() && m.start() == 0) {
                    String typeNAME = m.group(1);
                    this.dealWithDeclarationPrefix(line.substring(m.end()), typeNAME, true);
                    return;
                }
            }
            throw new LineException();
        }
        catch (LineException e){
            throw new LineException();
        }
    }

    /**
     * deal With Declaration Prefix.
     * @param line  the line with the final write there.
     * @param typeName the type of the variable.
     * @param isThereFinalBefore is the variable is final.
     * @throws LineException if there is something wrong with the variable.
     */
    public void dealWithDeclarationPrefix(String line,String typeName, boolean isThereFinalBefore) throws
            LineException{
        try {
            Pattern variableNamePattern = Patterns.VARIABLE_NAME_PATTERN;
            Matcher m = variableNamePattern.matcher(line);
            String variableName;
            if (m.find() && m.start() == 0) {
                variableName = m.group(1);
                if (localVariableHashMap.containsKey(variableName))
                    throw new LineException(); /*the variable already declared in the scope*/
                localVariableHashMap.put(variableName, new Variable(typeName, variableName,
                        false, isThereFinalBefore));
                line = line.substring(m.end());
                /*check if the line is in the end*/
                m = Patterns.END_OF_LINE_PATTERN.matcher(line);
                if (m.matches()) {
                    if (isThereFinalBefore)
                        throw new LineException();
                    return; /*the line is finished correctly so the treatment is over*/
                }
            /*check if there is "," and there is another declaration of the same type*/
                m = Patterns.SEPERATE_PATTERN.matcher(line);
                if (m.find() && m.start() == 0) {
                    if (isThereFinalBefore)
                        throw new LineException();
                    line = line.substring(m.end());
                    this.dealWithDeclarationPrefix(line, typeName, false);
                    return;
                }
            /*check if there is "=" sign, and there is an assignment*/
                m = Patterns.ASSIGNMENT_PATTERN.matcher(line);
                if (m.find() && m.start() == 0) {
                    line = line.substring(m.end());
                    this.dealWithAssignmentPrefix(line, variableName, typeName, isThereFinalBefore);
                    return;
                }
            }
            throw new LineException();
        }
        catch (LineException e){
            throw new LineException();
        }
    }

    /**
     *  deal With Assignment Prefix.
     * @param line the line with the final write there.
     * @param variableName the name of the variable.
     * @param typeOfVariable the type of the variable.
     * @param isThereFinalBefore is the variable is final.
     * @throws LineException if there is something wrong with the variable.
     */
    /*type of variable maybe null only if there is here only assignment without declaration*/
    public void dealWithAssignmentPrefix(String line, String variableName,String typeOfVariable, boolean
            isThereFinalBefore) throws LineException{
        try {
            boolean isThereDeclaration;
            Variable specificVariable;
            if (typeOfVariable == null) { /*means that there is only assignment without declaration*/
            /* and we have to look for the most specific variable*/
                /*if there weren't declaration, so the method was operated from the globlScope's treatment,
                so the "=" still exist (if it indeed exist) and we have to look for it. it is exists, we
                want to remove it. it it desn't exist, so there is error*/
                Matcher m1 = Patterns.ASSIGNMENT_PATTERN.matcher(line);
                if (m1.find() && m1.start()==0)
                    line = line.substring(m1.end());
                else
                    throw new LineException();
                specificVariable = this.getTheMostSpecificVariableWIthGivenName(variableName);
                typeOfVariable = specificVariable.getVariableType();
                isThereDeclaration =false;
                if (specificVariable.getIsFinal())
                    throw new LineException();
            }
            else{
                specificVariable = this.localVariableHashMap.get(variableName);
                isThereDeclaration = true;
            }
            String relevantRegex = Type.getRelevantRegex(typeOfVariable);
            Pattern p = Pattern.compile(relevantRegex);
            Matcher m = p.matcher(line);
            if (m.find() && m.start() == 0){
                /*means that the value after the = sign is a valid value to the variable*/
                specificVariable.assignValue();
            }
            else{
                /*means that the value after the = sign isnt a valid value to the variable, but it may be
                name of variable that may do reassignment to the temporary variable.*/
                p = Patterns.VARIABLE_NAME_PATTERN;
                m = p.matcher(line);
                if (m.find() && m.start()==0){
                    /*it it is may be variable name. now we have to check if the variable is exist*/
                    Variable assigningVariable = this.getTheMostSpecificVariableWIthGivenName(m.group(1));
                    String type1 = specificVariable.getVariableType();
                    String type2 = assigningVariable.getVariableType();
                    if (Type.isType1ContainType2(type1,type2) && assigningVariable.getIsVariableInitialized())
                        specificVariable.assignValue();
                    else
                        throw new LineException();
                }
            }
            line = line.substring(m.end());
            /*after the assignment there are be the followings:
            * 1. the line is over with ";"
            * 2. there is another declaration after "," sign.(valid only if the lines start with declaration
            * others things are errors*/

            /*check if the line is in the end*/
            m = Patterns.END_OF_LINE_PATTERN.matcher(line);
            if (m.matches())
                /*means that the line is over correctly*/
                return;
            /*check if there is "," so there is another declaration of the same type*/
            m = Patterns.SEPERATE_PATTERN.matcher(line);
            if (m.find() && m.start()==0 ){
                if (isThereDeclaration){
                    line = line.substring(m.end());
                    this.dealWithDeclarationPrefix(line,typeOfVariable,isThereFinalBefore);
                    return;
                }
            }
            throw new LineException();
        }
        catch (Exception e){
            /* the variable doesnt exist*/
            throw new LineException();
        }
    }

    /**
     * search after the variable name at the line before our line and check at all variable at the fathers
     * scope and at the global scope.
     * @param variableName the name of variable.
     * @return the variable we search for.
     * @throws LineException if there is something wrong with the variable.
     */
    public Variable getTheMostSpecificVariableWIthGivenName(String variableName) throws LineException{
        /*if the temporary scope is the global one */
        if (this.getTypeOfScope().equals(GlobalScope.TYPE_NAME)){
            if (this.localVariableHashMap.containsKey(variableName))
                return this.localVariableHashMap.get(variableName);
            throw new LineException();
        }
        /*if the program is here, the temporary scope that is treated isnt the global*/
        Scope tempScope = this;
        while (!tempScope.getTypeOfScope().equals(MethodScope.TYPE_NAME)){
            if (this.localVariableHashMap.containsKey(variableName))
                return this.localVariableHashMap.get(variableName);
            tempScope = tempScope.father;
        }
        /*means that the variable may be only in method scope (excluding the method's inners scopes) or in
        the global scope*/
        /*check if it is local variable of the method (the relevant one)*/
        if (tempScope.localVariableHashMap.containsKey(variableName)){
                Variable varToReturn = tempScope.localVariableHashMap.get(variableName);
                return varToReturn;
        }
        /*check if it is global variable*/
        /*We perform here DOWNCASTING. we make object of Scope to MethodScope because by the flag
        type_of_scope i know what the specific type of each scope*/
        MethodScope methodScope = (MethodScope)tempScope;
        HashMap<String,Variable> globalVariableSet = methodScope.getCopiedGlobalVariableHashMap();
        if (globalVariableSet.containsKey(variableName))
            return globalVariableSet.get(variableName);
        /*if the program is here, it mens that the variable doesn't exist*/
        throw new LineException();
    }


    /**
     *
     * @param mySjavac the file we handle with.
     * @param tempLine  the line with the final write there.
     * @throws LineException if there is something wrong with the variable.
     */
    public void skipTemporaryScope(Sjavac mySjavac, String tempLine) throws LineException{
        int CurlyBracketsCounter = 1;
        Pattern OpenBrackets = Pattern.compile(OPEN_BRACKETS);
        Pattern CloseBrackets = Pattern.compile(CLOSE_BRACKETS);
        while ((tempLine = mySjavac.nextLine()) != null &&(CurlyBracketsCounter >= 0)) {

            if (OpenBrackets.matcher(tempLine).matches()){
                CurlyBracketsCounter++;
                continue;
            }
            if (CloseBrackets.matcher(tempLine).matches())
                CurlyBracketsCounter--;
            if (CurlyBracketsCounter==0){
                return;
            }
        }
        throw new LineException();
    }
}
