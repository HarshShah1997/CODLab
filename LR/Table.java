import java.io.*;
import java.util.*;

class Table {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer;

    ArrayList<State> automaton;

    HashMap<Pair<Integer,String>,String> table =
        new HashMap<Pair<Integer,String>, String>();

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
        fillShift();
    }
        
    void fillShift() {
        HashMap<State,Boolean> visited = new HashMap<State,Boolean>();
        fillShift(automaton.get(0), visited);
        System.out.println(table);
    }

    void fillShift(State currentState, HashMap<State,Boolean> visited) {
        if (visited.get(currentState) != null) {
            return;
        }
        visited.put(currentState, true);
        int indexCurrentState = automaton.indexOf(currentState);
        for (Map.Entry<String,State> entry : currentState.transitions.entrySet()) {
            String symbol = entry.getKey();
            State newState = entry.getValue();
            int indexNewState = automaton.indexOf(newState);

            Pair<Integer,String> key = new Pair<Integer,String>(indexCurrentState, symbol);
            String value = null;
            if (Helper.isTerminal(symbol)) {
                value = "S" + indexNewState;
            } else {
                value = ""+indexNewState;
            }
            table.put(key, value);
            fillShift(newState, visited);
        }
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
