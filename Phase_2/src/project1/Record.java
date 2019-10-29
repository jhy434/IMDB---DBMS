/**************************************************************
 ** File   : Record.java
 ** Project: CSCE 315 - Project 1: Databases
 ** Author : Team Five
 ** Date   : 10/4/19
 ** Section: 910
 ** E-mail : rwilson@tamu.edu
 ** Members: Reid Wilson, Jonathan Yang, Will Morland, Trey Shaffer, Israel Blevins
 **
 ** Description: This is the Record class. A record can be thought of as a row in a table.
 ** Each record is simply a hashtable with the attribute name as the key. A variety of
 ** operations may be performed on these records.
 **
 **************************************************************/

package project1;

import java.util.Hashtable;
import java.util.Objects;
import project1.Dbms;

public class Record {

    // each of these represents a row in the Table
    private Hashtable<String, Object> fields = new Hashtable<>();

    // adds an attribute (thereby extending length of row)
    public void addAttribute(String key, Object object){
        fields.put(key, object);
    }

    // returns the data stored in specified column
    public Object getAttribute(String key) {
        return fields.get(key);
    }

    // returns how many attributes pertain to this record
    public int getSize() {
        return fields.size();
    }

    // returns the hashtable
    public Hashtable<String, Object> getFields() {
        return this.fields;
    }

    // checks if two records are equal to each other
    public boolean equalAttributes(Record other) {
        return this.fields.equals(other.getFields());
    }

    // this method is called when comparing fields with one another
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(fields, record.fields);
    }

    // performs the hash function
    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }

}
