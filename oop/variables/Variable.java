package oop.ex6.variables;



/**
 * this class Represents an instances of variable for the program.
 * there is 5 kinds of types: int,double,String,char and boolean.
 */
public class Variable {

    /*fields*/
    private String variableType; /*the type of the variable*/
    private String variableName; /*the name of the variable*/
    private boolean isVariableInitialized; /*true if the variable has a value. false if there isn't*/
    private boolean isFinal; /*true it the variable is final. false otherwise*/

    /**
     * the only constructor of variable
     * @param type - type of the new variable
     * @param name - name of the new variable
     * @param isVariableInitialized - true if the new variable has a value. false otherwise.
     * @param isFinal - true it the new variable is final. false otherwise
     */
    public Variable(String type, String name, boolean isVariableInitialized, boolean isFinal){
        this.variableType = type;
        this.variableName = name;
        this.isVariableInitialized = isVariableInitialized;
        this.isFinal = isFinal;
    }

    /**
     * the method happens after successful assignment.
     * the method set the 'isVariableInitialized' fields as true.
     */
    public void assignValue(){
        this.isVariableInitialized = true;
    }

    /*getters to the fields of the class*/
    public String getVariableType(){return variableType;}
    public boolean getIsVariableInitialized(){return isVariableInitialized;}
    public boolean getIsFinal(){return isFinal;}
    public String getVariableName(){return  variableName;}
}
