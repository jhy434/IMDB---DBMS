package project1;

public class Test {

    public Test() {
        testRecord();
        testRelation();
        testField();
        testType();
        testRelationName();
        testDbms();
    }

    public void testRecord() {
        Record record = new Record();

    }

    public void testRelation() {
        testRelationName();
    }

    public void testField() {
        final String testName1 = "testName1";
        final Type type1 = Type.INTEGER;
        final boolean isRequired1 = true;
        Field f1 = new Field(testName1, type1, isRequired1);
        assert(f1.name.equals(testName1) && f1.type == type1 && f1.isRequired);

        final String testName2 = "testName2";
        final Type type2 = Type.VARCHAR;
        final boolean isRequired2 = true;
        final int length = 20;
        Field f2 = new Field(testName2, type2, isRequired2, length);
        assert(f2.name.equals(testName2) && f2.type == type2 &&
                f2.isRequired && f2.type.getLength() == length);
    }

    public void testType() {
        final int length = 10;
        Type type1 = Type.VARCHAR;
        type1.setLength(length);
        assert(type1.getLength() == length);
    }

    public void testRelationName() {
        final String testName = "testName";
        Relation relation = new Relation(testName);
        assert(relation.getName() == testName);
    }

    public void testDbms() {

    }
}
