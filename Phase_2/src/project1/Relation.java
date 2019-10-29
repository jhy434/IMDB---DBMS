/**************************************************************
 ** File   : Relation.java
 ** Project: CSCE 315 - Project 1: Databases
 ** Author : Team Five
 ** Date   : 10/4/19
 ** Section: 910
 ** E-mail : rwilson@tamu.edu
 ** Members: Reid Wilson, Jonathan Yang, Will Morland, Trey Shaffer, Israel Blevins
 **
 ** Description: This is the Relation class, also referred to as the table class. It
 ** possesses data members expected for a table like name, attributes, and records. A variety of
 ** operations may be performed on these tables.
 **
 **************************************************************/

package project1;
import java.util.ArrayList;
import java.util.Hashtable;
import project1.Dbms;

public class Relation {

    // data members

    String name;
    ArrayList<Field> schema = new ArrayList<Field>();
    ArrayList<Record> records = new ArrayList<Record>();
    // member functions

    // simple constructor which sets the table's name
    public Relation(String tableName){
        name = tableName;
    }

    // this is a copy constructor but we don't copy the records
    public Relation(Relation cloneMe){

        name = cloneMe.name;
        for(int i=0; i<cloneMe.schema.size(); i++) {
            schema.add(cloneMe.schema.get(i));
        }

    }

    // insert an attribute
    public void addToSchema(Field f){
        schema.add(f);
    }

    // insert a record
    public void addToRecords(Record r){
        records.add(r);
    }

    // returns the table name
    public String getName(){
        return name;
    }

    // returns the record at the specified index
    public Record getRecord(int i){
        return records.get(i);
    }

}
