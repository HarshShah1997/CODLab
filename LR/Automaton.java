import java.util.*;
import java.io.*;

class Automaton {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer = new HashMap<String,NonTerminal>();

    void createAutomaton() {
        checkInput();
    }

    public Automaton() {
        grammer = null;
    }

    public Automaton(ArrayList<NonTerminal> inpGrammer) {
        grammer = inpGrammer;
    }

    void checkInput() {
        try {
            if (grammer == null) {
                grammer = Helper.getGrammer(new FileReader("grammer.txt")); 
            }
            mapGrammer = Helper.map(grammer);
            Helper.displayGrammer(grammer);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Automaton().createAutomaton();
    }
}

