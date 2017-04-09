import java.util.*;
import java.io.*;

class ParseString {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer;

    String inputString;

    HashMap<Pair<Integer,String>,String> table;

    ArrayList<Production> allProductions;

    public ParseString() {
        grammer = null;
        inputString = null;    
    }

    public ParseString(ArrayList<NonTerminal> inpGrammer) {
        grammer = inpGrammer;
    }

    public ParseString(String inpString) {
        inputString = inpString;
    }

    public ParseString(ArrayList<NonTerminal> inpGrammer, String inpString) {
        grammer = inpGrammer;
        inputString = inpString;
    }

    void run() {
        checkInput();
        table = (new LR0Table(grammer)).buildTable();
    }

    void checkInput() {
        try {
            if (grammer == null) {
                grammer = Helper.getGrammer(new FileReader("grammer.txt"));
            }
            if (inputString == null) {
                inputString = Helper.getInput();
            }
            mapGrammer = Helper.map(grammer);
            allProductions = Helper.fillProductions(grammer);
            //Helper.displayGrammer(grammer);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new ParseString().run();
    }
}

