/**************************************************************
 ** File   : Main.java
 ** Project: CSCE 315 - Project 1: Databases
 ** Author : Team Five
 ** Date   : 10/4/19
 ** Section: 910
 ** E-mail : rwilson@tamu.edu
 ** Members: Reid Wilson, Jonathan Yang, Will Morland, Trey Shaffer, Israel Blevins
 **
 ** Description: This is the Main class, also referred to as the driver file for our database.
 ** The code was written following the guide presented to us and remains unchanged.
 **
 **************************************************************/

package project1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import csce315.project1.Credits;
import csce315.project1.Movie;
import csce315.project1.MovieDatabaseParser;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import project1.antlr4.MyRulesBaseListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import project1.antlr4.RulesLexer;
import project1.antlr4.RulesParser;

public class Main extends Application {

    public static final String PATH = "src/project1/";
    public static boolean first = false;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
        primaryStage.setTitle("Project 1 - Team 5... or Team 8 I guess?");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toString());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {


        /*
        File file = new File("src/project1/input.txt");
        Scanner scanner = new Scanner(file);
        List<String> lines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() != 0) { lines.add(line); }
        }

        for (String line : lines) {

            CharStream charStream = CharStreams.fromString(line);
            RulesLexer lexer = new RulesLexer(charStream);
            CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
            RulesParser parser = new RulesParser(commonTokenStream);

            lexer.removeErrorListeners();
            parser.removeErrorListeners();

            RulesParser.ProgramContext programContext = parser.program();
            ParseTreeWalker walker = new ParseTreeWalker();
            MyRulesBaseListener listener = new MyRulesBaseListener();
            walker.walk(listener, programContext);

            // comment
        }*/
/*
        MovieDatabaseParser parser = new MovieDatabaseParser();

        List<Movie> moviesList = MovieDatabaseParser.deserializeMovies("src/project1/movies.json");
        List<Credits> creditsList = MovieDatabaseParser.deserializeCredits("src/project1/credits.json");

        String tableName = "Movies";
        String filePath = ("src/project1/" + tableName + ".db");
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
        String input = ("CREATE TABLE " + tableName + " (id INTEGER, title VARCHAR(999), vote_average INTEGER, genre VARCHAR(999)) PRIMARY KEY (id, title);\n");
        fw.write(input);
        for(int j = 0; j < moviesList.size(); j++) {
            Movie movie = moviesList.get(j);
            for (int i = 0; i < movie.getGenres().size(); i++) {
                String insert = ("INSERT INTO " + tableName + " VALUES FROM (" + movie.getId() + ", \"" + movie.getTitle().replaceAll(" ", "_").replaceAll(",", "").replaceAll("\"", "") + "\", " + Math.round(movie.getVote_average() * 100) + ", " + "\"" + movie.getGenres().get(i).getName().replaceAll(" ", "_") + "\");\n");
                fw.write(insert);
            }
        }
        fw.close();
        tableName = "Credits";
        filePath = ("src/project1/" + tableName + ".db");
        file = new File(filePath);
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
        fw = new FileWriter(file);
        input = ("CREATE TABLE " + tableName + " (id INTEGER, name VARCHAR(999), job VARCHAR(999), character VARCHAR(999)) PRIMARY KEY (id, name);\n");
        fw.write(input);
        for(int j = 0; j < creditsList.size(); j++){
            Movie movie = moviesList.get(j);
            Credits credits = creditsList.get(j);

            for (int i = 0; i < credits.getCastMember().size(); i++) {
                String insert = ("INSERT INTO " + tableName + " VALUES FROM (" + movie.getId() + ", \"" + credits.getCastMember().get(i).getName().replaceAll(" ", "_").replaceAll(",", "").replaceAll("-", "_") + "\", " + "Actor" + ", " + "\"" + credits.getCastMember().get(i).getCharacter().replaceAll(" ", "_").replaceAll(",", "").replaceAll("[()]", "").replaceAll("_voice", "").replaceAll("-", "_") + "\");\n");
                fw.write(insert);
            }
            for (int i = 0; i < credits.getCrewMember().size(); i++) {
                if (credits.getCrewMember().get(i).getJob().equals("Director")) {
                    String insert = ("INSERT INTO " + tableName + " VALUES FROM (" + movie.getId() + ", \"" + credits.getCrewMember().get(i).getName().replaceAll(" ", "_").replaceAll(",", "") + "\", " + credits.getCrewMember().get(i).getJob() + ", " + "\"" + "none" + "\");\n");
                    fw.write(insert);
                    break;
                }
            }
        }
        fw.close();*/
        launch(args);

//        System.out.println("********* INIT **************");
/*        Queries q = new Queries();
        ArrayList<String> out;
        System.out.println("********* Q TWO **************");
        out = q.QueryTwo("Al_Pacino", 5);
        System.out.println(out);
        System.out.println("********* Q THREE **************");
        out = q.QueryThree("Al_Pacino");
        System.out.println(out);
        System.out.println("********* Q FOUR **************");
        out = q.QueryFour("Paramedic");
        System.out.println(out);
        System.out.println("********* Q FIVE **************");
        System.out.println(q.QueryFive("Al_Pacino"));
*/
    }
}
