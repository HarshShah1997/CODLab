import java.util.*;
import java.io.*;

class LR1Table extends Table {

    HashMap<NonTerminal, Set<String>> follow;

    public LR1Table() {
        super();
    }

    public LR1Table(ArrayList<NonTerminal> inpGrammer) {
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
        Firstplus fp = new Firstplus(grammer);
        follow = fp.getFollow();
        //System.out.println(follow);
        for (State state : automaton) {
            fillReduce(state);
        }
    }

    void fillReduce(State state) {
        if (!isFinal(state)) {
            return;
        }
        for (Production production : state.productions) {
            String body = production.body;
            fillReduce(state, production);
        }
    }

    void fillReduce(State state, Production production) {
        int stateIndex = automaton.indexOf(state);
        int prodIndex = getIndex(production);
        if (prodIndex == -2) {
            ArrayList<String> terminals = Helper.getTerminals(grammer);
            for (String terminal : terminals) {
                Pair<Integer,String> key = new Pair<Integer,String>
                    (stateIndex, terminal);
                String value = "accept";
                table.put(key, value);
            }
        } else {
            Set<String> currentFollow = follow.get(production.head);
            for (String symbol : currentFollow) {
                Pair<Integer,String> key = new Pair<Integer,String>
                    (stateIndex, symbol);
                String value = "R" + prodIndex;
                if (table.get(key) != null) {
                    //System.out.println("Conflict");
                } else {
                    table.put(key, value);
                }
            }
        }
    }

    int getIndex(Production production) {
        String newBody = production.body.substring(0, production.body.length() - 1);
        Production newProd = new Production(production.head, newBody);

        if (newProd.body.charAt(newProd.body.length() - 1) == '$') {
            return -2;
        } else {
            return allProductions.indexOf(newProd);
        }
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
        new LR1Table().buildTable();
    }
}

