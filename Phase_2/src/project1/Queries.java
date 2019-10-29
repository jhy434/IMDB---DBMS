

package project1;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import project1.antlr4.MyRulesBaseListener;
import project1.antlr4.RulesLexer;
import project1.antlr4.RulesParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Queries {
    private Dbms queryDBMS;
    //private MyRulesBaseListener listener;
    public void Queries(){
        queryDBMS = new Dbms();
        //listener = new MyRulesBaseListener();
    }
    private void QueryOneSQL(){

    }

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

    private void QueryTwoSQL(String name) throws IOException {
        String tableName = "Query2";
        String filePath = (Main.PATH + tableName + ".db");
        File file = new File(filePath);

        if (file.delete()) {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        } else {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);
        String line = "";
        if(!Main.first){
            Main.first = true;
            line = "OPEN Movies;\n";
            fw.write(line);
            line = "OPEN Credits;\n";
            fw.write(line);
        }
        line = "actorMovieList <- project( id ) (select(name == \"" + name + "\" && job == \"Actor\") Credits);\n";
        fw.write(line);
        line = "bob <- Credits & actorMovieList;\n";
        fw.write(line);
        line = "a<-project(name)(select(job == \"Actor\" && name != \"" + name + "\") bob);\n";
        fw.write(line);
        fw.close();
    }

    public ArrayList<String> QueryTwo(String name, Integer appearances) throws IOException {
        QueryTwoSQL(name);
        MyRulesBaseListener listener = new MyRulesBaseListener();
        open("Query2", listener);
        queryDBMS = listener.getMyDbms();
        ArrayList<String> listApp = new ArrayList<String>();
        Hashtable<String, Integer> hash = new Hashtable<>();
        //queryDBMS.showTable("a");

        int index_a = queryDBMS.relationExists("a", queryDBMS.getRelations());
        Relation rel = queryDBMS.getRelations().get(index_a);
        //queryDBMS.showTable("a");
        for(int i = 0; i < rel.records.size(); i++){
            if(hash.get(rel.records.get(i).getFields().get("name")) == null) {
                hash.put((String) rel.records.get(i).getFields().get("name"), 1);
                if(appearances == 1){
                    listApp.add((String) rel.records.get(i).getFields().get("name"));
                }
            }
            else{
                int temp = hash.get(rel.records.get(i).getFields().get("name")) + 1;
                hash.put((String) rel.records.get(i).getFields().get("name"), temp);
                if(appearances == temp){
                    listApp.add((String) rel.records.get(i).getFields().get("name"));
                }
                else if(appearances < temp){
                    listApp.remove((String) rel.records.get(i).getFields().get("name"));
                }
            }
        }
        int index = queryDBMS.relationExists("actorMovieList", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("bob", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("a", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        return listApp;
    }

    private  void QueryThreeSQL(String name) throws IOException {
        String tableName = "Query3";
        String filePath = (Main.PATH + tableName + ".db");
        File file = new File(filePath);

        if (file.delete()) {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        } else {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);
        String line = "";
        if(!Main.first){
            Main.first = true;
            line = "OPEN Movies;\n";
            fw.write(line);
            line = "OPEN Credits;\n";
            fw.write(line);
        }
        line = "actorMovieList <- project( id ) (select(name == \"" + name + "\") Credits);\n";
        fw.write(line);
        line = "bob <- Movies & actorMovieList;\n";
        fw.write(line);
        line = "a<-project(genre) bob;\n";
        fw.write(line);
        fw.close();
    }

    public ArrayList<String> QueryThree(String name) throws IOException {
        QueryThreeSQL(name);
        MyRulesBaseListener listener = new MyRulesBaseListener();
        open("Query3", listener);
        queryDBMS = listener.getMyDbms();
        ArrayList<String> listGenre = new ArrayList<String>();
        Hashtable<String, Integer> hash = new Hashtable<>();
        //queryDBMS.showTable("a");
        int maxGenre = 0;
        int index_a = queryDBMS.relationExists("a", queryDBMS.getRelations());
        Relation rel = queryDBMS.getRelations().get(index_a);
        //queryDBMS.showTable("a");
        for(int i = 0; i < rel.records.size(); i++){
            if(hash.get(rel.records.get(i).getFields().get("genre")) == null) {
                hash.put((String) rel.records.get(i).getFields().get("genre"), 1);
                if(maxGenre == 1){
                    listGenre.add((String) rel.records.get(i).getFields().get("genre"));
                }
                else if(maxGenre < 1){
                    maxGenre = 1;
                    listGenre.add((String) rel.records.get(i).getFields().get("genre"));
                }
            }
            else{
                int temp = hash.get(rel.records.get(i).getFields().get("genre")) + 1;
                hash.put((String) rel.records.get(i).getFields().get("genre"), temp);
                if(maxGenre == temp){
                    listGenre.add((String) rel.records.get(i).getFields().get("genre"));
                }
                else if(maxGenre < temp){
                    listGenre.clear();
                    listGenre.add((String) rel.records.get(i).getFields().get("genre"));
                    maxGenre = temp;
                }
            }
        }
        int index = queryDBMS.relationExists("actorMovieList", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("bob", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("a", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        return listGenre;
    }

    private  void QueryFOURSQL(String name) throws IOException {
        String tableName = "Query4";
        String filePath = (Main.PATH + tableName + ".db");
        File file = new File(filePath);

        if (file.delete()) {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        } else {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);
        String line = "";
        if(!Main.first){
            Main.first = true;
            line = "OPEN Movies;\n";
            fw.write(line);
            line = "OPEN Credits;\n";
            fw.write(line);
        }
        line = "coverRole<- project( name ) (select(character == \"" + name + "\") Credits);";
        fw.write(line);
        fw.close();
    }

    public ArrayList<String> QueryFour(String name) throws IOException {
        QueryFOURSQL(name);
        MyRulesBaseListener listener = new MyRulesBaseListener();
        open("Query4", listener);
        queryDBMS = listener.getMyDbms();
        ArrayList<String> actors = new ArrayList<String>();
        //queryDBMS.showTable("a");
        int maxGenre = 0;
        int index_a = queryDBMS.relationExists("coverRole", queryDBMS.getRelations());
        Relation rel = queryDBMS.getRelations().get(index_a);
        //queryDBMS.showTable("a");
        for(int i = 0; i < rel.records.size(); i++){
            if(!actors.contains((String) rel.records.get(i).getFields().get("name"))){
                actors.add((String) rel.records.get(i).getFields().get("name"));
            }
        }
        int index = queryDBMS.relationExists("coverRole", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        return actors;
    }


    private  void QueryFiveSQLPart1(String name) throws IOException {
        String tableName = "Query5_1";
        String filePath = (Main.PATH + tableName + ".db");
        File file = new File(filePath);

        if (file.delete()) {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        } else {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);
        String line = "";
        if(!Main.first){
            Main.first = true;
            line = "OPEN Movies;\n";
            fw.write(line);
            line = "OPEN Credits;\n";
            fw.write(line);
        }
        line = "actorMovieList<- project( id ) (select(name == \"" + name + "\") Credits);\n";
        fw.write(line);
        line = "bob <- Movies & actorMovieList;\n";
        fw.write(line);
        line = "a<-project(vote_average, id) bob;\n";
        fw.write(line);
        fw.close();
    }

    private  void QueryFiveSQLPart2(String id) throws IOException {
        String tableName = "Query5_2";
        String filePath = (Main.PATH + tableName + ".db");
        File file = new File(filePath);

        if (file.delete()) {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        } else {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);
        String line = "";
        if(!Main.first){
            Main.first = true;
            line = "OPEN Movies;\n";
            fw.write(line);
            line = "OPEN Credits;\n";
            fw.write(line);
        }
        line = "director <- project(name) (select (job == \"Director\" && id == " + id + ") Credits);\n";
        fw.write(line);
        fw.close();
    }

    private  void QueryFiveSQLPart3(String directorName) throws IOException {
        String tableName = "Query5_3";
        String filePath = (Main.PATH + tableName + ".db");
        File file = new File(filePath);

        if (file.delete()) {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        } else {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);
        String line = "";
        if(!Main.first){
            Main.first = true;
            line = "OPEN Movies;\n";
            fw.write(line);
            line = "OPEN Credits;\n";
            fw.write(line);
        }
        line = "directorMovies <- project(id) (select (name == \"" + directorName + "\" && job == \"Director\") Credits);\n";
        fw.write(line);
        line = "b <- Movies & directorMovies;\n";
        fw.write(line);
        line = "c<-project(vote_average, id)b);\n";
        fw.write(line);
        fw.close();
    }

    private  void QueryFiveSQLPart4(String id) throws IOException {
        String tableName = "Query5_4";
        String filePath = (Main.PATH + tableName + ".db");
        File file = new File(filePath);

        if (file.delete()) {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " already exists. Clearing it.");
            }
        } else {
            if (file.createNewFile()) {
                System.out.println("File " + filePath + " created");
            }
        }

        //fileWriter that will write to our created file, will write over old data
        FileWriter fw = new FileWriter(file);
        String line = "";
        if(!Main.first){
            Main.first = true;
            line = "OPEN Movies;\n";
            fw.write(line);
            line = "OPEN Credits;\n";
            fw.write(line);
        }
        line = "badMovie <- project(title) (select (id == " + id + ") Movies);\n";
        fw.write(line);
        fw.close();
    }

    public ArrayList<String> QueryFive(String name) throws IOException {
        QueryFiveSQLPart1(name);
        MyRulesBaseListener listener = new MyRulesBaseListener();
        open("Query5_1", listener);
        queryDBMS = listener.getMyDbms();
        int index_a = queryDBMS.relationExists("a", queryDBMS.getRelations());
        ArrayList<String> returnList = new ArrayList<>();
        Relation ratings = queryDBMS.getRelations().get(index_a);
        int topRating = 0;
        String topMovieID = "";
        for(int i = 0; i < ratings.records.size(); i++){
            int vote_average = Integer.parseInt((String) ratings.records.get(i).getFields().get("vote_average"));
            if(topRating < vote_average){
                topRating = vote_average;
                topMovieID = (String) ratings.records.get(i).getFields().get("id");
            }
        }

        QueryFiveSQLPart2(topMovieID);
        open("Query5_2", listener);
        queryDBMS = listener.getMyDbms();
        int index = queryDBMS.relationExists("director", queryDBMS.getRelations());
        Relation director = queryDBMS.getRelations().get(index);
        String directorName = (String) director.records.get(0).getFields().get("name");
        returnList.add(directorName);
        QueryFiveSQLPart3(directorName);
        open("Query5_3", listener);
        queryDBMS = listener.getMyDbms();
        index = queryDBMS.relationExists("b", queryDBMS.getRelations());
        Relation c = queryDBMS.getRelations().get(index);
        int lowestRating = 1000;
        String lowestMovieID = "";
        for(int i = 0; i < c.records.size(); i++){
            int vote_average = Integer.parseInt((String) c.records.get(i).getFields().get("vote_average"));
            if(lowestRating > vote_average){
                lowestRating = vote_average;
                lowestMovieID = (String) c.records.get(i).getFields().get("id");
            }
        }
        QueryFiveSQLPart4(lowestMovieID);
        open("Query5_4", listener);
        queryDBMS = listener.getMyDbms();
        index = queryDBMS.relationExists("badMovie", queryDBMS.getRelations());
        Relation badMovie = queryDBMS.getRelations().get(index);
        String worstMovie = (String) badMovie.records.get(0).getFields().get("title");
        returnList.add(worstMovie);
        index = queryDBMS.relationExists("badMovie", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("c", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("b", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("directorMovies", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("director", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("a", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("bob", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        index = queryDBMS.relationExists("actorMovieList", queryDBMS.getRelations());
        queryDBMS.getRelations().remove(index);
        return returnList;
    }

}
