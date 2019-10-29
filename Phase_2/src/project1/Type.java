/**************************************************************
 ** File   : Type.java
 ** Project: CSCE 315 - Project 1: Databases
 ** Author : Team Five
 ** Date   : 10/4/19
 ** Section: 910
 ** E-mail : rwilson@tamu.edu
 ** Members: Reid Wilson, Jonathan Yang, Will Morland, Trey Shaffer, Israel Blevins
 **
 ** Description: This is the Type class. It is simply used alongside attributes
 ** in tables.
 **
 **************************************************************/

package project1;
import project1.Dbms;

public enum Type {

    VARCHAR, INTEGER; //add other data types as needed
    private int length;

    public void setLength(int len) { length = len; }
    public int getLength() { return length; }

}