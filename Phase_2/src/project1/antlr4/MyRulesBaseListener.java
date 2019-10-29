/**************************************************************
 ** File   : MyRulesBaseListener.java
 ** Project: CSCE 315 - Project 1: Databases
 ** Author : Team Five
 ** Date   : 10/4/19
 ** Section: 910
 ** E-mail : rwilson@tamu.edu
 ** Members: Reid Wilson, Jonathan Yang, Will Morland, Trey Shaffer, Israel Blevins
 **
 ** Description: This is the MyRulesBaseListener class. It consists only of methods that
 ** override methods in the generated RulesBaseListener class. The goal of this class is
 ** to listen for events related to the input SQL file and then provide the DBMS class with
 ** necessary information to complete the file's requests.
 **
 **************************************************************/

package project1.antlr4;

import org.antlr.v4.runtime.tree.ParseTree;
import project1.Dbms;

import java.util.*;
import java.io.IOException;

public class MyRulesBaseListener extends RulesBaseListener {

    // the famous DBMS
    private Dbms myDbms;

    // MyRulesBaseListenr constructor creates the DBMS
    public MyRulesBaseListener() {
        myDbms = new Dbms();
    }
    public MyRulesBaseListener(Dbms a) {
        myDbms = a;
    }

    public Dbms getMyDbms(){
        return myDbms;
    }
    // getLeafNodes is a recursive method which updates the list of leaves
    private void getLeafNodes(List<String> leaves, ParseTree node) {

        int numChildren = node.getChildCount();

        // we are dealing with a leaf, if the node has no children (this is the base case)
        if(numChildren == 0) {
            leaves.add(node.getText());
        }
        else { // we are dealing with a tree if the node has children (this is the recursive case)
            for(int i=0; i<numChildren; i++) {

                ParseTree child = node.getChild(i);
                if(child.getText().startsWith("\"") && child.getChildCount() < 1) {
                    leaves.add(node.getText());
                    return;
                }
                getLeafNodes(leaves, child);
            }
        }

    }

    // define the acceptable operators
    private ArrayList<String> OPS = new ArrayList<>(Arrays.asList(
            "+", "-", "*", "==", "&&", "||", "!=", ">",
            ">=", "<", "<=", "(", ")", "&"));

    // check if a given String is an operator
    private boolean isOperator(String value) {
        return OPS.contains(value);
    }

    // define the acceptable comparison operators
    private ArrayList<String> CONDITION_OPS = new ArrayList<>(Arrays.asList(
            "==", "<", "<=", ">", ">=", "!="));

    // check if a give String is a comparison operator
    private boolean isConditionOp(String value) { return CONDITION_OPS.contains(value); }

    // listen for show command and call DBMS show method
    public void exitShow_cmd(RulesParser.Show_cmdContext ctx) {

        List<ParseTree> children = ctx.children;
        ParseTree second_child = children.get(1);
        String tableName = second_child.getText();
        myDbms.showTable(tableName);

    }

    // listen for create command and call DBMS create method
    public void exitCreate_cmd (RulesParser.Create_cmdContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // obtain the table name
        ParseTree second_child = children.get(1);
        String tableName = second_child.getText();

        ArrayList<ArrayList<String>> secondParam = new ArrayList<>();
        ArrayList<String> tempQuality = new ArrayList<>();

        // fourth_child represents the qualities of the entries
        ParseTree fourth_child = children.get(3);

        for(int i=0; i<fourth_child.getChildCount(); i++) {
            String b = fourth_child.getChild(i).getText();
            tempQuality.add(b);
        }
        for(int i=2; i<fourth_child.getChildCount(); i+=3) {
            String b = fourth_child.getChild(i).getText();
            tempQuality.remove(b);
        }

        for(int i=1; i<tempQuality.size(); i+=2) {
            if(tempQuality.get(i).contains("(")) {
                tempQuality.add(i+1, tempQuality.get(i).substring(tempQuality.get(i).indexOf("(") + 1, tempQuality.get(i).indexOf(")")));
                tempQuality.set(i, tempQuality.get(i).substring(0, tempQuality.get(i).indexOf("(")));
                i += 1;
            }
            else {
                tempQuality.add(i+1, null);
                i+=1;
            }
        }

        // a represents the required qualities
        ParseTree eighth_child = children.get(7);
        String a = eighth_child.getText();
        String[] reqQual = a.split(",");
        ArrayList<String> requiredQualities= new ArrayList<String>();
        for(int i=0; i<reqQual.length; i++) {
            requiredQualities.add(reqQual[i]);
        }

        for(int i=0; i<tempQuality.size(); i+=3) {
            ArrayList<String> quality = new ArrayList<String>();
            quality.add(tempQuality.get(i));
            quality.add(tempQuality.get(i+1));
            quality.add(tempQuality.get(i+2));
            secondParam.add(quality);
        }

        myDbms.createTable(tableName, secondParam, requiredQualities);

    }

    // listen for select command and call DBMS tableSubset method plus any others needed from conditions
    public void exitSelection (RulesParser.SelectionContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // get the condition and expression nodes
        ParseTree conditionNode = children.get(2);
        ParseTree expressionNode = children.get(4);

        // create operator stack and elements queue for condition node
        List<String> conditionLeaves = new ArrayList<>(); // parse tree leaves
        Deque<String> conditionOpStack = new ArrayDeque<>(); // operator stack
        Queue<String> conditionQueue = new LinkedList<>(); // elements queue

        // get condition node's leaf nodes
        getLeafNodes(conditionLeaves, conditionNode);

        // build operator stack and elements queue for condition node
        for (String leaf : conditionLeaves) {
            if(isOperator(leaf)) {
                if(conditionOpStack.isEmpty()) {
                    conditionOpStack.addLast(leaf);
                    continue;
                }
                if(isConditionOp(conditionOpStack.getLast()) && (!isConditionOp(leaf)) && (leaf!="(") && (leaf!=")")) {
                    conditionQueue.add(conditionOpStack.removeLast());
                    conditionOpStack.addLast(leaf);
                }
                else {
                    conditionOpStack.addLast(leaf);
                }
            }
            else { // text is an element
                conditionQueue.add(leaf);
            }
        }

        // pop rest of operator stack into elements queue for condition node
        while (!conditionOpStack.isEmpty()) {
            if(conditionOpStack.getLast().equals("(") || conditionOpStack.getLast().equals(")")) {
                conditionOpStack.removeLast();
            }
            else {
                conditionQueue.add(conditionOpStack.removeLast());
            }
        }

        // create operator stack and elements queue for expression node
        List<String> expressionLeaves = new ArrayList<>(); // parse tree leaves
        Deque<String> expressionOpStack = new ArrayDeque<>(); // operator stack
        Queue<String> expressionQueue = new LinkedList<>(); // elements queue

        // get expression node's leaf nodes
        getLeafNodes(expressionLeaves, expressionNode);

        // build operator stack and elements queue for expression node
        for(String leaf: expressionLeaves) {
            if(isOperator(leaf)) {
                expressionOpStack.addLast(leaf);
            }
            else { // text is an element
                expressionQueue.add(leaf);
            }
        }

        // pop rest of operator stack into elements queue for expression node
        while (!expressionOpStack.isEmpty()) {
            if(expressionOpStack.getLast().equals("(") || expressionOpStack.getLast().equals(")")) {
                expressionOpStack.removeLast();
            }
            else {
                expressionQueue.add(expressionOpStack.removeLast());
            }
        }

        // we have the condition queue and the expression queue now

        Deque<String> expressionStack = new ArrayDeque<>();
        while(!expressionQueue.isEmpty()) {
            String currentElement = expressionQueue.peek();
            expressionStack.add(expressionQueue.remove());
            if(isOperator(currentElement)) {
                expressionStack.removeLast();
                String tableName1 = expressionStack.removeLast();
                String tableName2 = expressionStack.removeLast();
                expressionStack.add(tableName2);
                expressionStack.add(tableName1);
                switch(currentElement) {
                    case "+":
                        myDbms.union(tableName1, tableName2);
                        break;
                    case "-":
                        myDbms.difference(tableName1, tableName2);
                        break;
                    case "&":
                        myDbms.naturalJoin(tableName1, tableName2);
                        break;
                    case "*":
                        myDbms.product(tableName1, tableName2);
                        break;
                    default:
                        System.out.println("Error: There is an invalid symbol.");
                }
                expressionStack.removeLast();
                expressionStack.removeLast();
                expressionStack.add("#");
            }
        }

        // there is only 1 item on expressionStack after algorithm so that is the simplified expression
        String simplifiedExpression = expressionStack.removeLast();
        myDbms.setSpecialRelation(simplifiedExpression);

        while(!conditionQueue.isEmpty()) {
            if(!isOperator(conditionQueue.peek())) {
                String attribute = conditionQueue.remove();
                String value = conditionQueue.remove();
                String operator = conditionQueue.remove();
                myDbms.tableSubset(attribute, value, operator);
            }
            else if (conditionQueue.peek().equals("&&")){
                conditionQueue.remove();
                myDbms.tableAnd();
            }
            else if (conditionQueue.peek().equals("||")) {
                conditionQueue.remove();
                myDbms.tableOr();
            }
            else {
                System.out.println("Error: There is an invalid symbol.");
            }
        }

        // the temp stack in dbms should have 1 element left and that is our answer

    }

    // listen for close command and call DBMS close method
    public void exitClose_cmd (RulesParser.Close_cmdContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grabs the table name
        ParseTree second_child = children.get(1);
        String tableName = second_child.getText();

        // calls for the specified table to be closed
        myDbms.close(tableName);

    }

    // listen for exit command and call DBMS exit method
    public void exitExit_cmd (RulesParser.Exit_cmdContext ctx) {

        myDbms.exit();

    }

    // listen for delete command and call corresponding DBMS delete method (delete or delete2)
    public void exitDelete_cmd (RulesParser.Delete_cmdContext ctx) {

        // grabs the children nodes and then the table name
        List<ParseTree> children = ctx.children;
        ParseTree tableNameTree = children.get(1);
        String tableName = tableNameTree.getText();

        // we are dealing with any easy delete  (no condition)
        if(children.size() == 2) {

            // deletes a whole table
            myDbms.delete(tableName);

        }
        else { // handle delete with a WHERE condition

            // isolate the condition and put it into conditionCluster
            ParseTree conditionTree = children.get(3);
            ParseTree conditionTree2 = conditionTree.getChild(0).getChild(0).getChild(1);
            String conditionCluster = conditionTree2.getText();

            // parse the conditionCluster and call delete 2 with appropriate arguments
            String[] condition = conditionCluster.split("&&");
            if(condition.length != 2) {
                condition = conditionCluster.split("==");
                if(condition.length != 2) {
                    condition = conditionCluster.split("<=");
                    if(condition.length != 2) {
                        condition = conditionCluster.split("<");
                        if(condition.length != 2) {
                            condition = conditionCluster.split(">=");
                            if(condition.length != 2) {
                                condition = conditionCluster.split(">");
                                if(condition.length != 2) {
                                    condition = conditionCluster.split("!=");
                                    myDbms.delete2(tableName, condition[0], condition[1], "!=");
                                }
                                else {
                                    myDbms.delete2(tableName, condition[0], condition[1], ">");
                                }
                            }
                            else {
                                myDbms.delete2(tableName, condition[0], condition[1], ">=");
                            }
                        }
                        else {
                            myDbms.delete2(tableName, condition[0], condition[1], "<");
                        }
                    }
                    else {
                        myDbms.delete2(tableName, condition[0], condition[1], "<=");
                    }
                }
                else {
                    myDbms.delete2(tableName, condition[0], condition[1], "==");
                }
            }
            else {
                myDbms.delete2(tableName, condition[0], condition[1], "&&");
            }

        }

    }

    // listen for union command and call corresponding DBMS union method
    public void exitUnion (RulesParser.UnionContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grab names for the two tables
        ParseTree first_child = children.get(0);
        String tableName1 = first_child.getText();
        if(tableName1.charAt(0) == '(') {
            first_child = children.get(0).getChild(1);
            tableName1 = first_child.getText();
        }
        ParseTree third_child = children.get(2);
        String tableName2 = third_child.getText();

        // perform the operation
        myDbms.union(tableName1, tableName2);

    }

    // listen for product command and call corresponding DBMS product method
    public void exitProduct (RulesParser.ProductContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grab names for the two tables
        ParseTree first_child = children.get(0);
        String tableName1 = first_child.getText();
        ParseTree third_child = children.get(2);
        String tableName2 = third_child.getText();

        // perform the operation
        myDbms.product(tableName1, tableName2);

    }

    // listen for natural join command and call corresponding DBMS natural join method
    public void exitNatural_join (RulesParser.Natural_joinContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grab names for the two tables
        ParseTree first_child = children.get(0);
        String tableName1 = first_child.getText();
        ParseTree third_child = children.get(2);
        String tableName2 = third_child.getText();

        // perform the operation
        myDbms.naturalJoin(tableName1, tableName2);

    }

    // listen for write command and call corresponding DBMS write method
    public void exitWrite_cmd (RulesParser.Write_cmdContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grab the table name
        ParseTree second_child = children.get(1);
        String tableName = second_child.getText();

        // tell DBMS to write the table to a file
        try {
            myDbms.write(tableName);
        }
        catch(IOException e)
        {
            System.out.println("This should never happen. "+e);
        }

    }

    // listen for every query and call DBMS tableReal method to finalize new table
    public void exitQuery(RulesParser.QueryContext ctx) {

        // grabs the children nodes and table names
        List<ParseTree> children = ctx.children;
        ParseTree first_child = children.get(0);
        ParseTree second_child = children.get(2);
        String newTable = first_child.getText();
        String oldTable = second_child.getText();

        // tell DBMS to use oldTable to create newTable
        myDbms.tableReal(newTable, oldTable);

    }

    // listen for insert command and call DBMS insert method to load tables
    public void exitInsert_cmd (RulesParser.Insert_cmdContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grab table name to insert to
        ParseTree second_child = children.get(1);
        String tableName = second_child.getText();

        ArrayList<String> newEntry = new ArrayList<>();

        // branch off in 2 directions depending on if we are inserting from existing table or not
        ParseTree third_child = children.get(2);
        String operationType = third_child.getText();
        if(operationType.equals("VALUES FROM")) { // we are not inserting from existing table

            for(int i=4; i<children.size()-1; i+=2) {
                ParseTree current_child = children.get(i);
                String newQuality = current_child.getText();
                //System.out.println(newQuality);
                if(newQuality.contains("\"")) {
                    newQuality = newQuality.substring(1, newQuality.length()-1);
                }
                newEntry.add(newQuality);
            }

            myDbms.insertInto(tableName, newEntry);

        }
        else if(operationType.equals("VALUES FROM RELATION")) { // we are inserting from existing table
            ParseTree fourth_child = children.get(3);
            String conditionCluster = fourth_child.getText();
            myDbms.insertInto2(tableName, conditionCluster);
        }
        else { // this should never happen with valid SQL code
            System.out.println("Error: invalid insert operation");
        }

    }

    // listen for open command and call DBMS open method to load table from file
    public void exitOpen_cmd (RulesParser.Open_cmdContext ctx) {

        // grabs the children nodes and table name
        List<ParseTree> children = ctx.children;
        ParseTree second_child = children.get(1);
        String tableName = second_child.getText();

        // tell DBMS to load table from file
        try {
            myDbms.open(tableName, this);
        }
        catch(IOException e)
        {
            System.out.println("This should never happen. "+e);
        }

    }

    // listen for project query and call DBMS project method
    public void exitProjection(RulesParser.ProjectionContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grabs the attribute
        ParseTree third_child = children.get(2);
        String attributeText = third_child.getText();

        // grabs the expression
        ParseTree fifth_child = children.get(4);
        String expressionText = fifth_child.getText();

        // simplifies the attributes
        String[] attributesArray = attributeText.split(",");
        ArrayList<String> attributes = new ArrayList<>();
        for(int i=0; i<attributesArray.length; i++) {
            attributes.add(attributesArray[i]);
        }

        // tells the DBMS to perform a projection
        myDbms.project(attributes, expressionText);

    }

    // listen for difference operation and call DBMS difference method
    public void exitDifference(RulesParser.DifferenceContext ctx) {

        // grabs the children nodes
        List<ParseTree> children = ctx.children;

        // grab the table names
        ParseTree first_child = children.get(0);
        String tableName1 = first_child.getText();
        ParseTree third_child = children.get(2);
        String tableName2 = third_child.getText();

        // perform the operation
        myDbms.difference(tableName1, tableName2);

    }

    // listen for rename query and call DBMS rename method
    public void exitRenaming(RulesParser.RenamingContext ctx) {

        // grabs the children nodes and what to rename the attributes
        List<ParseTree> children = ctx.children;
        ParseTree third_child = children.get(2);
        String renameText = third_child.getText();

        // grabs the expression
        ParseTree fifth_child = children.get(4);
        String expressionText = fifth_child.getText();

        // simplifies the new names
        String[] newNamesArray = renameText.split(",");
        ArrayList<String> newNames = new ArrayList<>();
        for(int i=0; i<newNamesArray.length; i++) {
            newNames.add(newNamesArray[i]);
        }

        // tell the DBMS to rename the attributes of a certain table
        myDbms.rename(newNames, expressionText);

    }

    // listen for update command and call DBMS update method
    public void exitUpdate_cmd(RulesParser.Update_cmdContext ctx) {

        // grabs the children nodes and table name
        List<ParseTree> children = ctx.children;
        ParseTree tableNameTree = children.get(1);
        String tableName = tableNameTree.getText();

        // in SET condition, grab the set attribute
        ParseTree setAttributeTree = children.get(3);
        ParseTree setAttributeTree2 = setAttributeTree.getChild(0);
        String setAttribute = setAttributeTree2.getText();

        // we know the set operator will be "=" so no need to parse it

        // in SET condition, grab the set value
        String setValue = "";
        ParseTree setValueTree = children.get(5);
        // we need 2 cases to deal with the value being having quotation marks or not
        if(setValueTree.getChildCount() > 1) {
            ParseTree setValueTree2 = setValueTree.getChild(1);
            setValue = setValueTree2.getText();
        }
        else {
            ParseTree setValueTree2 = setValueTree.getChild(0);
            setValue = setValueTree2.getText();
        }

        // we now have the 2 Strings we need for the set condition

        // handle WHERE condition
        ParseTree whereConditionTree = children.get(7);
        ParseTree whereConditionTree2 = whereConditionTree.getChild(0).getChild(0);
        String whereConditionCluster = whereConditionTree2.getText();

        // in WHERE condition, grab the where attribute and where value, then tell DBMS to update the table
        String[] whereCondition = whereConditionCluster.split("==");
        if(whereCondition.length != 2) {
            whereCondition = whereConditionCluster.split("<=");
            if(whereCondition.length != 2) {
                whereCondition = whereConditionCluster.split("<");
                if(whereCondition.length != 2) {
                    whereCondition = whereConditionCluster.split(">=");
                    if(whereCondition.length != 2) {
                        whereCondition = whereConditionCluster.split(">");
                        if(whereCondition.length != 2) {
                            whereCondition = whereConditionCluster.split("!=");
                            myDbms.update(tableName, setAttribute, setValue, whereCondition[0], "!=", whereCondition[1]);
                        }
                        else {
                            myDbms.update(tableName, setAttribute, setValue, whereCondition[0], ">", whereCondition[1]);
                        }
                    }
                    else {
                        myDbms.update(tableName, setAttribute, setValue, whereCondition[0], ">=", whereCondition[1]);
                    }
                }
                else {
                    myDbms.update(tableName, setAttribute, setValue, whereCondition[0], "<", whereCondition[1]);
                }
            }
            else {
                myDbms.update(tableName, setAttribute, setValue, whereCondition[0], "<=", whereCondition[1]);
            }
        }
        else {
            myDbms.update(tableName, setAttribute, setValue, whereCondition[0], "==", whereCondition[1]);
        }

    }

}
