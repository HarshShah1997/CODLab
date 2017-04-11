import java.io.*;
import java.util.*;

abstract class Table {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer;

    ArrayList<State> automaton;
    ArrayList<Production> allProductions = new ArrayList<Production>();

    HashMap<Pair<Integer,String>,String> table =
        new HashMap<Pair<Integer,String>, String>();

    public Table() {
        grammer = null;
    }

    public Table(ArrayList<NonTerminal> inpGrammer) {
        grammer = inpGrammer;
    }

    abstract HashMap<Pair<Integer,String>,String> buildTable();

    void fillShift() {
        HashMap<State,Boolean> visited = new HashMap<State,Boolean>();
        fillShift(automaton.get(0), visited);
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
                
    abstract void fillReduce();

    void printTable() {
        for (int i = 0; i < automaton.size(); i++) {
            System.out.print(i + " ");

            State currentState = automaton.get(i);
            ArrayList<String> first = Helper.getTerminals(grammer);
            first.addAll(mapGrammer.keySet());
            for (String entry : first) {
                Pair<Integer,String> key = new Pair<Integer,String>(i, entry);
                System.out.print("(" + entry + ",");
                if (table.get(key) == null) {
                    System.out.print("  ) ");
                } else {
                    System.out.print(table.get(key) + ") ");
                }
            }
            System.out.println("");
        }
    }
}
