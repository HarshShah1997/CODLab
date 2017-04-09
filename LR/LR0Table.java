import java.util.*;
import java.io.*;

class LR0Table extends Table {

    public LR0Table() {
        super();
    }

    public LR0Table(ArrayList<NonTerminal> inpGrammer) {
        super(inpGrammer);
    }

    HashMap<Pair<Integer,String>,String> buildTable() {
        checkInput();
        
        fillShift();
        fillReduce();

        printTable();
        return table;
    }

    void checkInput() {
        try {
            if (grammer == null) {
                grammer = Helper.getGrammer(new FileReader("grammer.txt")); 
            }
            mapGrammer = Helper.map(grammer);
            if (automaton == null) {
                automaton = new CreateAutomaton(grammer).run();
            }
            Helper.displayStates(automaton);
            allProductions = Helper.fillProductions(grammer);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    void fillReduce() {
        for (State state : automaton) {
            fillReduce(state);
        }        
    }

    void fillReduce(State state) {
        if (!isFinal(state)) {
            return;
        }
        if (state.productions.size() > 1) {
            printConflict(state);
        } else {
            int stateIndex = automaton.indexOf(state);
            ArrayList<String> terminals = Helper.getTerminals(grammer);
            for (String terminal : terminals) {
                Pair<Integer,String> key = new Pair<Integer,String>
                    (stateIndex, terminal);

                String value = getValue(state.productions.get(0));
                table.put(key, value);
            }
        }
    }

    String getValue(Production orgProd) {
        String newBody = orgProd.body.substring(0, orgProd.body.length() -1);

        Production newProd = new Production(orgProd.head, newBody);

        if (newProd.body.charAt(newProd.body.length() - 1) == '$') {
            return "accept";
        } else {
            return "R" + allProductions.indexOf(newProd);
        }
    }

    void printConflict(State state) {
        System.out.println("CONFLICT - Not parsable");
    }

    boolean isFinal(State currentState) {
        for (Production prod : currentState.productions) {
            String body = prod.body;
            if (body.charAt(body.length() - 1) == '.') {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        new LR0Table().buildTable();
    }
}
