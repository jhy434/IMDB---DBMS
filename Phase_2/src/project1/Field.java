/**************************************************************
 ** File   : Field.java
 ** Project: CSCE 315 - Project 1: Databases
 ** Author : Team Five
 ** Date   : 10/4/19
 ** Section: 910
 ** E-mail : rwilson@tamu.edu
 ** Members: Reid Wilson, Jonathan Yang, Will Morland, Trey Shaffer, Israel Blevins
 **
 ** Description: This is the Field class. A field can be thought of as the column in a
 ** table or as the attribute in a table. It contains data members like name and type.
 ** A collection of fields makes up a schema for a relation.
 **
 **************************************************************/

package project1;
import project1.Dbms;

import java.util.Objects;

public class Field {

    // data members

    public String name;
    public Type type;
    public int length;
    public boolean isRequired;

    // field constructor without length
    public Field(String name, Type type, boolean isRequired) {
        this.name = name;
        this.type = type;
        this.isRequired = isRequired;
    }

    // field constructor with length
    public Field(String name, Type type, boolean isRequired, int length) {
        this(name, type, isRequired);
        this.length = length;
        type.setLength(length);
    }

    // this method is called when comparing fields with one another
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return this.isRequired == field.isRequired &&
                Objects.equals(this.name, field.name) &&
                this.type == field.type;
    }

    // performs the hash function
    @Override
    public int hashCode() {
        return Objects.hash(name, type, isRequired);
    }

}
