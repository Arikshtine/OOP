package oop.ex6.scopes;

import oop.ex6.lines.lineException.LineException;
import oop.ex6.main.Patterns;
import oop.ex6.main.Sjavac;
import oop.ex6.variables.Variable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class Represents a global scope that keeps all other scoops inside.
 * we also create here all other scopes and we keep the "father" of the scope that is the scoop he is nested.
 */
public class GlobalScope extends Scope {

    public static final String TYPE_NAME = "global";
    public static final String VOID_PATTERN = "\\s*void\\s+([a-zA-Z][a-zA-Z\\d_]*)\\s*\\(";

    /**
     * constructor that make a new scoop, we keep the father of this scoop.
     * @param mySjavac the sjava file we handle with.
     * @throws LineException if the scope make problem.
     */
    public GlobalScope(Sjavac mySjavac) throws LineException{
        try {
            this.father = null;
            this.treatScope(mySjavac);
        }
        catch (Exception e) {
            throw new LineException();
        }
    }

    /**
     * @return the type of the scoop (if/while/method).
     */
    public String getTypeOfScope() {
        return TYPE_NAME;
    }

    /**
     * this method handel with the line in the scoop . check all the legal cases and if none of this cases
     * happens we throw exception.
     * @param mySjavac the sjava file we handle with.
     * @throws LineException if the line is illegal.
     */
    public void treatScope(Sjavac mySjavac) throws LineException {
        String tempLine;
        ArrayList<Pattern> patternsList = mySjavac.getPatternsList();
        Matcher m;
        try {
            while ((tempLine = mySjavac.nextLine()) != null) {
                int forLoopCounter = -1;
                boolean isSomeCaseHappen = false;
                for (Pattern tempPattern : patternsList) {
                    forLoopCounter++;
                    m = tempPattern.matcher(tempLine);
                    if (m.find() && m.start() == 0) {
                        if (mySjavac.getSetOfUnsupportedCasesForGlobalScope().contains(forLoopCounter))
                            throw new LineException();
                        else if (forLoopCounter >= Patterns.FIRST_INDEX_OF_TYPE_PATTERNS) {
                            this.dealWithDeclarationPrefix
                                    (tempLine.substring(m.end()), m.group(1), false);
                            isSomeCaseHappen =true;
                            break;
                        } else {
                            switch (forLoopCounter) {
                                case Patterns.INDEX_OF_FINAL_PATTERN:
                                    isSomeCaseHappen =true;
                                    this.dealWithFinalPrefix(tempLine.substring(m.end()), patternsList);
                                    break;
                                case Patterns.INDEX_OF_COMMENT_PATTERN:
                                    isSomeCaseHappen =true;
                                    break;
                                case Patterns.INDEX_OF_METHOD_PATTERN:
                                    if (checkIfMethodNameAlreadyExist(tempLine,this.innerScopes))
                                        throw new LineException();
                                    this.innerScopes.add(new MethodScope(mySjavac,tempLine.substring(m.end
                                            ()),this));
                                    this.skipTemporaryScope(mySjavac, tempLine);
                                    isSomeCaseHappen =true;
                                    break;
                                case Patterns.INDEX_OF_VARIABLE_NAME_PATTERN_WITHOUT_DECLARATION:
                                    this.dealWithAssignmentPrefix(tempLine.substring(m.end()-1), m.group(1),
                                            null, false);
                                    isSomeCaseHappen =true;
                                    break;
                                case Patterns.INDEX_OF_EMPTY_LIST_PATTEN:
                                    if (m.matches())
                                        isSomeCaseHappen =true;
                                    break;
                            }
                        }
                    }
                }
                if (!isSomeCaseHappen)
                    throw new LineException();
            }
            mySjavac.rereadTheFile();
            int counter = -1;
            while ((tempLine = mySjavac.nextLine()) != null) {
                m = Patterns.METHOD_PATTERN.matcher(tempLine);
                if (m.find() && m.start() == 0) {
                    counter++;
                    MethodScope tempMethod = (MethodScope)this.innerScopes.get(counter);
                    tempMethod.treatScope(mySjavac);
                }
            }
        }
        catch (Exception e){
            throw new LineException();
        }
    }

    /** check If Method Name Already Exist.
     * @param tempLine the line of the signature method.
     * @param scopeList the list of the scope that Written by name.
     * @return true if the name of the method is not exist yet, false otherwise.
     * @throws LineException if the line of the signature method is illegal.
     */
    public boolean checkIfMethodNameAlreadyExist(String tempLine, ArrayList<Scope> scopeList) throws
            LineException {
        Pattern p = Pattern.compile(VOID_PATTERN);
        Matcher m = p.matcher(tempLine);
        if (!(m.find() && m.start()==0))
            throw new LineException();
        String name = m.group(1);
        for (Scope tempScope : scopeList){
            MethodScope tempScope2 = (MethodScope)tempScope;
            if (tempScope2.getMethodName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * copy the Global variable and change them if them not initialize yet.
     * @return the new copy hashMap
     */
    public HashMap<String,Variable> copyGlobalVariableHahMap(){
        HashMap<String,Variable> copiedGlobalVariableHashMap = new HashMap<String,Variable>();
        for (Map.Entry<String , Variable> item : this.localVariableHashMap.entrySet()){
            String key = item.getKey();
            Variable value = item.getValue();
            String name = value.getVariableName();
            String type = value.getVariableType();
            boolean isFinal = value.getIsFinal();
            boolean isInitializied = value.getIsVariableInitialized();
            copiedGlobalVariableHashMap.put(name,new Variable(type,name,isInitializied,isFinal));
        }
        return copiedGlobalVariableHashMap;
    }
}
