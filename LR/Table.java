import java.io.*;
import java.util.*;

class Table {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer;

    ArrayList<State> automaton;

    public Table() {
        grammer = null;
    }

    public Table(ArrayList<NonTerminal> inpGrammer) {
        grammer = inpGrammer;
    }

    void buildTable() {
        checkInput();
        automaton = new CreateAutomaton(grammer).run();
        Helper.displayStates(automaton);
    }
        
    void fillShift() {
        ;
    }

    void fillReduce() { ; }

    void checkInput() {
        try {
            if (grammer == null) {
                grammer = Helper.getGrammer(new FileReader("grammer.txt")); 
            }
            mapGrammer = Helper.map(grammer);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Table().buildTable();
    }
}
