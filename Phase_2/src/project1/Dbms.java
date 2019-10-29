/**************************************************************
 ** File   : Dbms.java
 ** Project: CSCE 315 - Project 1: Databases
 ** Author : Team Five
 ** Date   : 10/4/19
 ** Section: 910
 ** E-mail : rwilson@tamu.edu
 ** Members: Reid Wilson, Jonathan Yang, Will Morland, Trey Shaffer, Israel Blevins
 **
 ** Description: This is the Dbms class (database management system). The goal of this class
 ** is to store data which was read in from a SQL file, output said data, and write said data
 ** to a file.
 **
 **************************************************************/

package project1;

import java.sql.SQLOutput;
import java.util.*;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

import project1.antlr4.MyRulesBaseListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import project1.antlr4.RulesLexer;
import project1.antlr4.RulesParser;

import static project1.Type.INTEGER;
import static project1.Type.VARCHAR;

public class Dbms {
    public Dbms() {
    }
// data members

    private static ArrayList<Relation> relations = new ArrayList<>();
    private static Deque<Relation> tempStack = new ArrayDeque<>();
    private static Relation specialRelation; // used for drawing a table subset

    public ArrayList<Relation> getRelations(){
        return relations;
    }
    // this method is called when at a certain point in Dijktra's algorithm
    public void setSpecialRelation(String tableName) {

        if(tableName.equals("#")) { // we know the table we want is on the stack
            specialRelation = tempStack.pop();
        }
        else { // we know the table we want is a real table (not temp table)
            for (int i = 0; i < relations.size(); i++) {
                if (relations.get(i).getName().equals(tableName)) {
                    specialRelation = relations.get(i);
                    break;
                }
            }
        }

        // the "special relation" is the relation from which we draw records when performing select query

    }

    // this is the powerhouse method that computes the select query
    public void tableSubset(String attribute, String value, String operator) {

        // we must convert value to an int to perform comparisons if it's actually a number
        int intValue = 0;
        boolean numberValue = false;
        boolean scheme = false;
        if(value.charAt(0) >= 48 && value.charAt(0) <= 57) {
            intValue = Integer.parseInt(value);
            numberValue = true;
        }
        else if(value.charAt(0) == '\"'){
            value = value.substring(1, value.length() - 1);
        }
        else {
            scheme = true;
        }

        // copy the schema field of specialRelation to tempNew but nothing else
        Relation tempNew = new Relation(specialRelation);
        tempNew.name = "#";

        // we must account for 6 different operators
        switch(operator) {
            case("=="):
                if(scheme){
                    for(int i=0; i<specialRelation.records.size(); i++) {
                        if(specialRelation.getRecord(i).getFields().get(attribute).equals(specialRelation.getRecord(i).getFields().get(value))){
                            tempNew.addToRecords(specialRelation.getRecord(i));
                        }
                    }
                }
                else {
                    for (int i = 0; i < specialRelation.records.size(); i++) {
                        for (int j = 0; j < specialRelation.schema.size(); j++) {
                            if (attribute.equals(specialRelation.schema.get(j).name)) {
                                if (numberValue) {
                                    if (intValue == Integer.parseInt((String) specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                        tempNew.addToRecords(specialRelation.records.get(i));
                                    }
                                } else {
                                    if (value.equals(specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                        tempNew.addToRecords(specialRelation.records.get(i));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case("!="):
                if(scheme){
                    for(int i=0; i<specialRelation.records.size(); i++) {
                        if(!specialRelation.getRecord(i).getFields().get(attribute).equals(specialRelation.getRecord(i).getFields().get(value))){
                            tempNew.addToRecords(specialRelation.getRecord(i));
                        }
                    }
                }
                else {
                    for (int i = 0; i < specialRelation.records.size(); i++) {
                        for (int j = 0; j < specialRelation.schema.size(); j++) {
                            if (attribute.equals(specialRelation.schema.get(j).name)) {
                                if (numberValue) {
                                    if (intValue != Integer.parseInt((String) specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                        tempNew.addToRecords(specialRelation.records.get(i));
                                    }
                                } else {
                                    if (!value.equals(specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                        tempNew.addToRecords(specialRelation.records.get(i));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case("<"):
                for(int i=0; i<specialRelation.records.size(); i++) {
                    for(int j=0; j<specialRelation.schema.size(); j++) {
                        if(attribute.equals(specialRelation.schema.get(j).name)) {
                            if(intValue > Integer.parseInt((String) specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                tempNew.addToRecords(specialRelation.records.get(i));
                            }
                        }
                    }
                }
                break;
            case(">"):
                for(int i=0; i<specialRelation.records.size(); i++) {
                    for(int j=0; j<specialRelation.schema.size(); j++) {
                        if(attribute.equals(specialRelation.schema.get(j).name)) {
                            if(intValue < Integer.parseInt((String) specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                tempNew.addToRecords(specialRelation.records.get(i));
                            }
                        }
                    }
                }
                break;
            case("<="):
                for(int i=0; i<specialRelation.records.size(); i++) {
                    for(int j=0; j<specialRelation.schema.size(); j++) {
                        if(attribute.equals(specialRelation.schema.get(j).name)) {
                            if(intValue >= Integer.parseInt((String) specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                tempNew.addToRecords(specialRelation.records.get(i));
                            }
                        }
                    }
                }
                break;
            case(">="):
                for(int i=0; i<specialRelation.records.size(); i++) {
                    for(int j=0; j<specialRelation.schema.size(); j++) {
                        if(attribute.equals(specialRelation.schema.get(j).name)) {
                            if(intValue <= Integer.parseInt((String) specialRelation.records.get(i).getAttribute(specialRelation.schema.get(j).name))) {
                                tempNew.addToRecords(specialRelation.records.get(i));
                            }
                        }
                    }
                }
                break;
            default:
                System.out.println("Error: There is an invalid operator.");
        }

        tempStack.push(tempNew);

    }

    /**
     * Checks if the relation exists in the table in the DBMS
     *
     * @param tableName     name of the table
     * @param relation      relation being checked for
     * @return              index of relation, -1 if not found
     */
    public int relationExists(String tableName, ArrayList<Relation> relation) {

        int index = -1;
        for(int i = 0; i < relation.size(); i++) {
            if(relation.get(i).getName().compareTo(tableName) == 0){
                index = i;
            }
        }
        return index;

        // an index of -1 means table is not real (it may be on stack as temp table though)

    }

    /**
     * Creates a table and adds it to the DBMS
     *
     * @param tableName             name of the table
     * @param qualities             list of qualities
     * @param requiredQualities     list of required qualities
     */
    public void createTable(String tableName, ArrayList<ArrayList<String>> qualities, ArrayList<String> requiredQualities) {

        Relation r = new Relation(tableName);

        String name;
        String type;
        String length;
        boolean key = false;
        for(int i = 0; i < qualities.size(); i++) {
            name = qualities.get(i).get(0);
            type = qualities.get(i).get(1);
            length = qualities.get(i).get(2);

            if(requiredQualities.contains(name)){
                key = true;
            }

            if(length == null && type.compareTo("INTEGER") == 0) {
                Field f = new Field(name, INTEGER, key);
                r.addToSchema(f);
            }

            if(length != null && type.compareTo("VARCHAR") == 0) {
                int len = Integer.parseInt(length);
                Field f = new Field(name, VARCHAR, key, len);
                r.addToSchema(f);
            }
            key = false;
        }

        relations.add(r);

    }

    // adds a table to the stack containing the columns of the specified table that have the specified names
    public void project(ArrayList<String> columns, String tableName) {

        String tempTableName = "#";
        Relation rel = validateTable(tableName);
        Relation temp = new Relation(tempTableName);
        ArrayList<Integer> locations = new ArrayList<>();

        for(int i = 0; i < columns.size(); i++)
        {
            for(int j = 0; j < rel.schema.size(); j++)
            {
                if(rel.schema.get(j).name.equals(columns.get(i)))
                {
                    temp.addToSchema(rel.schema.get(j));
                    locations.add(j);
                }
            }
        }

        for(int j = 0; j < rel.records.size(); j++) {
            Record tempRec = new Record();
            for (int i = 0; i < locations.size(); i++) {
                String schema = rel.schema.get(locations.get(i)).name;
                Object a = rel.records.get(j).getFields().get(schema);
                tempRec.addAttribute(schema, a);
            }
            temp.addToRecords(tempRec);
        }

        tempStack.push(temp);

    }

    /**
     * Displays a table to the user
     *
     * @param tableName     name of table being displayed
     */
    public void showTable(String tableName){

        Relation r1;

        // if name is "#" then it is a temp table and on the stack (otherwise it's real and index will be set)
        int index = 0;

        if(tableName.equals("#")) {
            r1 = tempStack.pop();
        }
        else {
            index = relationExists(tableName, relations);
            r1 = relations.get(index);
        }

        // this means table is real or on the stack and we can output it to console
        if(index > -1) {

            System.out.println(r1.getName() + ": ");
            for (int i = 0; i < r1.records.size(); i++) {
                for (int j = 0; j < r1.records.get(i).getSize(); j++) {
                    if(j == r1.records.get(i).getSize()-1)
                        System.out.print(r1.records.get(i).getAttribute(r1.schema.get(j).name));
                    else
                        System.out.print(r1.records.get(i).getAttribute(r1.schema.get(j).name) + ", ");
                }
                System.out.println();
            }
        }

    }

    /**
     * Inserts value into table inside of DBMS
     *
     * @param tableName     specifies table name
     * @param newEntry      specifies new entry being added
     */
    public void insertInto(String tableName, ArrayList<String> newEntry){
        //System.out.println("InsertInto: " + newEntry.get(1));
        int index = relationExists(tableName, relations);

        if(index > -1) {
            Record r = new Record();
            for(int i = 0; i < newEntry.size(); i++) {
                r.addAttribute(relations.get(index).schema.get(i).name, newEntry.get(i));
            }
            relations.get(index).addToRecords(r);
        }
    }

    /**
     * Writes a given DBMS to disk
     *
     * @param tableName     name of DBMS to be written
     * @throws IOException  thrown if write operation could not be completed
     */
    public void write(String tableName) throws IOException {

        // create new file with filepath
        String filePath = (Main.PATH+tableName+".db");
        File file = new File(filePath);

        if(file.delete()) {
            if(file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        }
        else {
            if(file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);

        String input = ("CREATE TABLE " + tableName + " (");

        Relation rel = new Relation(tableName);

        //call relationExists, rel = relations.get(index)
        for (int i = 0; i < relations.size(); i++) {
            if (relations.get(i).getName().equals(tableName)) {
                rel = relations.get(i);
            }
        }

        for(int i = 0; i < rel.schema.size(); i++) {
            input = input + rel.schema.get(i).name + (" ") + rel.schema.get(i).type.toString();
            if(rel.schema.get(i).type==VARCHAR) {
                input = input+ "(" + rel.schema.get(i).length + ")";
            }
            if(i < rel.schema.size()-1) {
                input = input + ", ";
            }
        }

        input = input + ") PRIMARY KEY (";

        for(int i = 0; i < rel.schema.size(); i++) { //for each field in the table
            if(rel.schema.get(i).isRequired) {
                input = input + rel.schema.get(i).name + ", ";
            }//print "fieldName, " if it is required
        }

        input = input.substring(0, input.length()-2); // removing final ", " from string as it is not required
        input = input + ");\n"; // first line of the db file is added to input
        fw.write(input);

        input = input.substring(0,0);

        for(int i = 0; i < rel.records.size(); i++) { //for each record in the table

            input = input + "INSERT INTO " + tableName + " VALUES FROM (";
            for(int j = 0; j < rel.records.get(i).getSize(); j++) { //for each field in the record

                if(rel.schema.get(j).type==INTEGER)
                    input = input + rel.records.get(i).getAttribute(rel.schema.get(j).name).toString() + ", "; //add INTEGER to line
                else {
                    input = input + "\"" + rel.records.get(i).getAttribute(rel.schema.get(j).name).toString() + "\", "; //add VARCHAR to line
                }

            }

            input = input.substring(0, input.length()-2); //removing ", " from input
            input = input + ");\n";
            fw.write(input);
            input = input.substring(0,0);

        }

        fw.close();
    }

    /**
     * Opens the table that is written on the disk and adds it to the DBMS
     *
     * @param tableName     name of the table which will be used for file search
     * @param listener      listener for parsing
     * @throws IOException  thrown if file not found or error in opening
     */
    public void open(String tableName, MyRulesBaseListener listener) throws IOException {
        try {

            // prepare for reading file from directory
            File file = new File(Main.PATH+tableName+".db");
            Scanner scanner = new Scanner(file);
            List<String> lines = new ArrayList<>();
            // parse the relation file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.length() != 0) { lines.add(line); }
            }
            // for each line of SQL code, perform this (very similar to Main.java)
            for (String line : lines) {
                CharStream charStream = CharStreams.fromString(line);
                RulesLexer lexer = new RulesLexer(charStream);
                CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
                RulesParser parser = new RulesParser(commonTokenStream);
                lexer.removeErrorListeners();
                parser.removeErrorListeners();
                RulesParser.ProgramContext programContext = parser.program();
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(listener, programContext);
            }
        }
        catch(IOException e) {
            throw new RuntimeException("This should never happen", e);
        }

    }

    /**
     * Writes a table to disk and then deletes from DBMS
     * Uses SQL query language when writing relation to disk.
     *
     * @param tableName name of the table to be written to disk
     */
    public void close(String tableName){

        try {
            write(tableName);
        }
        catch(IOException ignored) {

        }

        relations.remove(relationExists(tableName, relations));
    }

    // validateTable interprets the situation to return a saved table or a table on the stack
    public Relation validateTable(String tableName) {

        Relation rel;
        int index1 = relationExists(tableName, relations);
        if(index1 != -1) {
            rel = relations.get(index1);
        }
        else if(tempStack.size() > 0) {
            rel = tempStack.pop();
        }
        else {
            // this should never happen
            System.out.println("Error: we have a problem finding a table to use.");
            exit();
            rel = tempStack.pop();
        }

        return rel;

    }

    // this method computes the difference of 2 tables
    public void difference(String table1, String table2) {

        Relation r1;
        Relation r2;
        r1 = validateTable(table1);
        r2 = validateTable(table2);

        Relation tempNew = new Relation("#");
        tempNew.schema = r1.schema;
        for (int i = 0; i < r1.records.size(); i++) {
            boolean unique = true;
            for (int j = 0; j < r2.records.size(); j++) {
                if (r1.records.get(i).equalAttributes(r2.records.get(j))) {
                    unique = false;
                    r2.records.remove(j);
                    break;
                }
            }
            if (unique) {
                tempNew.records.add(r1.records.get(i));
            }
        }
        tempNew.records.addAll(r2.records);
        tempStack.push(tempNew);
    }

    /**
     * Performs a natural join between two tables
     *
     * @param table1    first table name
     * @param table2    second table name
     * @return          name of temporary name produced by natural join of two tables
     */
    public void naturalJoin(String table1, String table2) {

        Relation r1;
        Relation r2;
        r1 = validateTable(table1);
        r2 = validateTable(table2);
        Relation tempNew = new Relation("#");
        for(int i = 0; i < r1.schema.size();i++){
            if(!tempNew.schema.contains(r1.schema.get(i))){
                tempNew.schema.add(r1.schema.get(i));
            }
        }
        for(int i = 0; i < r2.schema.size(); i++){
            if(!tempNew.schema.contains(r2.schema.get(i))){
                tempNew.schema.add(r2.schema.get(i));
            }
            else{
                tempNew.schema.add(0,r2.schema.get(i));
                tempNew.schema.remove(1);
            }
        }

        for(int i = 0; i < r1.records.size(); i++){
            for(int j =0; j < r2.records.size(); j++){
                //enter when value at shared column is the same
                if(r1.records.get(i).getAttribute(tempNew.schema.get(0).name).equals(r2.records.get(j).getAttribute(tempNew.schema.get(0).name))){
                    //add all of r1 matching record to tempNew
                    tempNew.records.add(r1.records.get(i));
                    //add all of r2 matching record to tempNew minus the shared one
                    for(int k = 0; k < r2.schema.size(); k++){
                        if(r2.schema.get(k) != tempNew.schema.get(0)){
                            tempNew.records.get(tempNew.records.size()-1).addAttribute(r2.schema.get(k).name, r2.records.get(j).getAttribute(r2.schema.get(k).name));
                        }
                    }
                }
            }
        }
        tempStack.push(tempNew);
    }

    /**
     * Performs the product of two tables and returns the name pointing to
     * the product between the given tables stored in a temporary table
     *
     * @param table1    name of first table
     * @param table2    name of second table
     * @return          temporary table pointer
     */
    public void product(String table1, String table2) {
        String productTableName="#";
        int indexT1 = relationExists(table1, relations);
        int indexT2 = relationExists(table2, relations);

        Relation r1;
        Relation r2;
        Relation temp = new Relation(productTableName);

        if(indexT1 > -1 && indexT2 > -1) {
            r1 = relations.get(indexT1);
            r2 = relations.get(indexT2);
            temp.schema.addAll(r1.schema);
            temp.schema.addAll(r2.schema);

            for(int i = 0; i < r1.records.size(); i++) //for each record in r1
            {
                for(int j = 0; j < r2.records.size(); j++) //for each record in r2
                {
                    Record tempRec = new Record(); //create a temporary record
                    for(int k = 0; k < r1.schema.size(); k++) //for each piece of data in the current record of r1
                    {
                        tempRec.addAttribute(r1.schema.get(k).name, r1.records.get(i).getAttribute(r1.schema.get(k).name)); //add the data to temprec
                    }
                    for (int k = 0; k < r2.schema.size(); k++) //for each piece of data in the current record of r2
                    {
                        tempRec.addAttribute(r2.schema.get(k).name, r2.records.get(j).getAttribute(r2.schema.get(k).name)); //add the data to temprec
                    }
                    temp.records.add(tempRec);
                }
            }
        }
        tempStack.push(temp);
    }

    /**
     * Returns a name pointing to the temporary table of the outcome
     * of the operation of the Union between the two tables
     *
     * @param table1    name of first table
     * @param table2    name of second table
     * @return          temporary name
     */
    public void union(String table1, String table2) {

        // obtain the 2 necessary tables
        Relation r1 = validateTable(table1);
        Relation r2 = validateTable(table2);

        // perform the operation on a new temp table
        Relation temp = new Relation("#");
        temp.schema.addAll(r1.schema);
        temp.records.addAll(r1.records);
        temp.records.addAll(r2.records);

        // add the new temp table to the stack
        tempStack.push(temp);

    }

    /**
     * This method pops the top 2 elements on the stack, computes the
     * && operation on them, and then pushes the result onto the stack
     *
     */
    public void tableAnd(){

        // tableAnd will always be popping off the top 2 elements on temp stack
        // no need to check if tables are real

        Relation r1;
        Relation r2;
        r1 = tempStack.pop();
        r2 = tempStack.pop();

        Relation tempNew = new Relation("#");
        tempNew.schema.addAll(r1.schema);
        for(int i = 0; i < r1.records.size(); i++){
            for(int j = 0; j < r2.records.size(); j++){
                if(r1.records.get(i) == r2.records.get(j)){
                    tempNew.records.add(r1.records.get(i));
                }
            }
        }
        tempStack.push(tempNew);
    }

    /**
     * This method pops the top 2 elements on the stack, computes the
     * || operation on them, and then pushes the result onto the stack
     */
    public void tableOr(){

        // tableOr will always be popping off the top 2 elements on temp stack
        // no need to check if tables are real

        Relation r1;
        Relation r2;
        r1 = tempStack.pop();
        r2 = tempStack.pop();

        Relation tempNew = new Relation("#");
        tempNew.schema.addAll(r1.schema);
        tempNew.records.addAll(r1.records);
        for(int j = 0; j < r2.records.size(); j++) {
            if(!tempNew.records.contains(r2.records.get(j))) {
                tempNew.records.add(r2.records.get(j));
            }
        }

        tempStack.push(tempNew);

    }

    /**
     * Converts a temporary table into a real table
     *
     * @param newTableName   name for new real table
     * @param oldTableName   either junk value or name for real table to copy from
     */
    public void tableReal(String newTableName, String oldTableName){

        int index = relationExists(oldTableName, relations);
        if(index > -1) {
            Relation newTable = new Relation(newTableName);
            newTable.records.addAll(relations.get(index).records);
            newTable.schema.addAll(relations.get(index).schema);
            relations.add(newTable);
        }
        else {
            if (!tempStack.isEmpty()) {
                Relation temp;
                temp = tempStack.pop();
                temp.name = newTableName;
                relations.add(temp);
            }
        }

    }

    /**
     * Deletes table from DBMS
     *
     * @param tableName     name of table
     */
    public void delete(String tableName){

        int index = relationExists(tableName, relations);
        if(index > -1) {
            relations.remove(index);
        }

    }

    /**
     * Deletes records from a relation that satisfy a certain condition.
     *
     * @param tableName
     * @param attribute
     * @param value
     * @param operator
     */
    public void delete2(String tableName, String attribute, String value, String operator){

        int index = relationExists(tableName, relations);
        if(value.charAt(0) == 34){
            value = value.substring(1,value.length()-1);
            for(int i = relations.get(index).records.size()-1; i >= 0; i--){
                if(operator.equals("==")) {
                    if (relations.get(index).records.get(i).getAttribute(attribute).equals(value)) {
                        relations.get(index).records.remove(i);
                    }
                }
                else if(operator.equals("!=")) {
                    if (!relations.get(index).records.get(i).getAttribute(attribute).equals(value)) {
                        relations.get(index).records.remove(i);
                    }
                }
            }
        }
        else if(value.charAt(0) >= 48 && value.charAt(0) <= 57){
            Integer val = Integer.parseInt(value);
            for(int i = relations.get(index).records.size()-1; i >= 0; i--){
                String s = (String) relations.get(index).records.get(i).getAttribute(attribute);
                if(true) {
                    Integer stuff = Integer.parseInt(s);
                    if (operator.equals("==")) {
                        if (stuff == (val)) {
                            relations.get(index).records.remove(i);
                        }
                    } else if (operator.equals("!=")) {
                        if (stuff != (val)) {
                            relations.get(index).records.remove(i);
                        }
                    } else if (operator.equals("<")) {
                        if (stuff < (val)) {
                            relations.get(index).records.remove(i);
                        }
                    } else if (operator.equals(">")) {
                        if (stuff > (val)) {
                            relations.get(index).records.remove(i);
                        }
                    } else if (operator.equals("<=")) {
                        if (stuff <= (val)) {
                            relations.get(index).records.remove(i);
                        }
                    } else if (operator.equals(">=")) {
                        if (stuff >= (val)) {
                            relations.get(index).records.remove(i);
                        }
                    }
                }
                else {
                    break;
                }
            }
        }
        else{
            for(int i = relations.get(index).records.size()-1; i >= 0; i--){
                if(operator.equals("==")) {
                    if (relations.get(index).records.get(i).getAttribute(attribute).equals(relations.get(index).records.get(i).getAttribute(value))) {
                        relations.get(index).records.remove(i);
                    }
                }
                else if(operator.equals("!=")) {
                    if (!relations.get(index).records.get(i).getAttribute(attribute).equals(relations.get(index).records.get(i).getAttribute(value))) {
                        relations.get(index).records.remove(i);
                    }
                }
            }
        }
    }

    // this method renames attributes in a given table
    public void rename(ArrayList<String> columns, String tableName) {

        Relation r = validateTable(tableName);
        Relation temp = new Relation("#");
        for(int i=0; i<r.schema.size(); i++) {
            Field newField = new Field(r.schema.get(i).name, r.schema.get(i).type, r.schema.get(i).isRequired, r.schema.get(i).length);
            temp.schema.add(newField);
        }


        // loop for each attribute
        for(int i = 0; i < columns.size(); i++) {

            // loop for each record
            for(int j=0; j<r.records.size(); j++) {

                Record rec;
                if(i == 0) {
                    rec = new Record();
                }
                else {
                    rec = temp.records.get(j);
                }

                rec.addAttribute(r.schema.get(i).name, r.records.get(j).getAttribute(r.schema.get(i).name));

                // add the current record to temp table
                if(i == 0) {
                    temp.addToRecords(rec);
                }

            }


            // update the attribute names
            temp.schema.get(i).name = columns.get(i);

        }

        // for each field of each record temp, replace old string with new string

        for (int j = 0; j < temp.records.size(); j++) {
            for(int i=0; i<columns.size(); i++) {
                Object tempObject = temp.records.get(j).getFields().remove(r.schema.get(i).name);
                temp.records.get(j).getFields().put(columns.get(i), tempObject);
            }
        }

        // temp is our final product
        tempStack.push(temp);

    }

    // similar to the insertInto method, this method inserts values from an existing table
    public void insertInto2(String resultTable, String fromTable) {

        Relation fromTab = validateTable(fromTable);
        int index = relationExists(resultTable, relations);
        ArrayList<Field> toAddSchema = new ArrayList<>();
        if(index > -1){
            if(relations.get(index).records.size() != 0) {
                for (int j = 0; j < fromTab.records.size(); j++) {
                    Record r = new Record();
                    boolean addRecord = false;
                    for (int i = 0; i < fromTab.schema.size(); i++) {
                        if (j >= relations.get(index).records.size()) {
                            addRecord = true;
                            if (relations.get(index).schema.contains(fromTab.schema.get(i))) {
                                r.addAttribute(fromTab.schema.get(i).name, fromTab.getRecord(j).getAttribute(fromTab.schema.get(i).name));
                            } else {
                                r.addAttribute(fromTab.schema.get(i).name, fromTab.getRecord(j).getAttribute(fromTab.schema.get(i).name));
                                if (!toAddSchema.contains(fromTab.schema.get(i))) {
                                    toAddSchema.add(fromTab.schema.get(i));
                                }
                            }
                        } else {
                            if (relations.get(index).schema.contains(fromTab.schema.get(i))) {
                                relations.get(index).getRecord(j).getFields().remove(fromTab.schema.get(i).name);
                                relations.get(index).getRecord(j).addAttribute(fromTab.schema.get(i).name, fromTab.getRecord(j).getAttribute(fromTab.schema.get(i).name));
                            } else {
                                relations.get(index).getRecord(j).addAttribute(fromTab.schema.get(i).name, fromTab.getRecord(j).getAttribute(fromTab.schema.get(i).name));
                                if (!toAddSchema.contains(fromTab.schema.get(i))) {
                                    toAddSchema.add(fromTab.schema.get(i));
                                }
                            }
                        }
                        if (addRecord) {
                            relations.get(index).addToRecords(r);
                        }
                    }
                }

                for (int i = 0; i < toAddSchema.size(); i++) {
                    relations.get(index).schema.add(toAddSchema.get(i));
                }

            }

            else {
                for (int j = 0; j < fromTab.records.size(); j++) {
                    Record r = new Record();
                    for (int i = 0; i < fromTab.schema.size(); i++) {
                        if (relations.get(index).schema.contains(fromTab.schema.get(i))) {
                            r.addAttribute(fromTab.schema.get(i).name, fromTab.getRecord(j).getAttribute(fromTab.schema.get(i).name));
                        } else {
                            r.addAttribute(fromTab.schema.get(i).name, fromTab.getRecord(j).getAttribute(fromTab.schema.get(i).name));
                            if (!toAddSchema.contains(fromTab.schema.get(i))) {
                                toAddSchema.add(fromTab.schema.get(i));
                            }
                        }

                    }

                    relations.get(index).addToRecords(r);
                }
                for (int i = 0; i < toAddSchema.size(); i++) {
                    relations.get(index).addToSchema(toAddSchema.get(i));
                }
            }

        }

    }

    public void update(String tableName, String attributeName, String inputValue, String conditionName, String conditionOp, String conditionValue) {

        int index = relationExists(tableName, relations);
        if(!Character.isDigit(conditionValue.charAt(0))) {//if the first character of the conditionValue is a quotation mark, it's a VARCHAR, otherwise it's an INTEGER
            conditionValue = conditionValue.substring(1, conditionValue.length()-1);//removing quotation marks from VARCHAR
            for (int i = relations.get(index).records.size()-1; i >= 0; i--) {//for each record in the table
                if(conditionOp.equals("==")) {
                    if (relations.get(index).records.get(i).getAttribute(conditionName).equals(conditionValue)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue); //overwrite the attribute named with the named input
                    }
                }
                else if(conditionOp.equals("!=")) {
                    if (!relations.get(index).records.get(i).getAttribute(conditionName).equals(conditionValue)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue);//overwrite the attribute named with the named input
                    }
                }
            }
        }
        else {

            Integer val = Integer.parseInt(conditionValue);
            for (int i = relations.get(index).records.size() - 1; i >= 0; i--) {
                String s = (String) relations.get(index).records.get(i).getAttribute(conditionName);
                Integer stuff = 0;
                if(Character.isDigit(s.charAt(0))) {
                    stuff = Integer.parseInt(s);
                }

                if (conditionOp.equals("==")) {
                    if (stuff == (val)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue);
                    }
                } else if (conditionOp.equals("!=")) {
                    if (stuff != (val)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue);
                    }
                } else if (conditionOp.equals("<")) {
                    if (stuff < (val)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue);
                    }
                } else if (conditionOp.equals(">")) {
                    if (stuff > (val)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue);
                    }
                } else if (conditionOp.equals("<=")) {
                    if (stuff <= (val)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue);
                    }
                } else if (conditionOp.equals(">=")) {
                    if (stuff >= (val)) {
                        relations.get(index).records.get(i).addAttribute(attributeName, inputValue);
                    }
                }
            }
        }
    }

    /**
     * Exits DBMS program, clearing all data
     */
    public void exit(){

        while(!tempStack.isEmpty()){
            tempStack.pop();
        }

        System.out.println("DBMS is shutting down.");

    }

}
