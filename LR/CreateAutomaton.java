import java.util.*;
import java.io.*;

class CreateAutomaton {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer = new HashMap<String,NonTerminal>();

    ArrayList<State> states = new ArrayList<State>();

    public CreateAutomaton() {
        grammer = null;
    }

    public CreateAutomaton(ArrayList<NonTerminal> inpGrammer) {
        grammer = inpGrammer;
    }

    ArrayList<State> run() {
        checkInput();

        State initialState = createInitialState();

        createTransitions(initialState);
        return states;
    }

    State createInitialState() {
        NonTerminal head = new NonTerminal("Z");
        String body = augment(grammer.get(0).name);
        body += "$";
        Production production = new Production(head, body);

        State initialState = new State();
        initialState.productions.add(production);
        initialState.productions.addAll(closure(production));

        return initialState;
    }

    void createTransitions(State state) {
        if (states.indexOf(state) != -1) {
            return;
        }
        //System.out.println(state);
        ArrayList<String> transitionSymbols = state.getTransitionSymbols();
        //System.out.println(transitionSymbols);

        states.add(state);

        for (String symbol : transitionSymbols) {
            State newstate = findTransition(state, symbol);
            //System.out.println(symbol);
            //System.out.println(newstate);
            //System.out.println("");
            createTransitions(newstate);
        }
    }

    State findTransition(State org, String symbol) {        
        ArrayList<Production> allProd = new ArrayList<Production>();
        char sym = symbol.charAt(0);
        for (Production prod : org.productions) {
            int pos = prod.body.indexOf(".");
            if (pos < prod.body.length() - 1 && sym == prod.body.charAt(pos+1)) {
                Production newProd = shift(prod);
                allProd.add(newProd);
                allProd.addAll(closure(newProd));
            }
        }
        State nextState = new State(allProd);
        org.transitions.put(symbol, nextState);
        return nextState;
    }

    Production shift(Production prev) {
        StringBuilder sb = new StringBuilder(prev.body);
        int pos = prev.body.indexOf(".");
        char temp = sb.charAt(pos+1);
        sb.setCharAt(pos+1, '.');
        sb.setCharAt(pos, temp);

        String newBody = sb.toString();
        return new Production(prev.head, newBody);
    }

    ArrayList<Production> closure(Production org) {
        HashMap<String,Boolean> taken = new HashMap<String,Boolean>();
        return closure(org, taken);
    }
        
    ArrayList<Production> closure(Production org, HashMap<String,Boolean> taken) {        
        ArrayList<Production> ans = new ArrayList<Production>();
        taken.put(org.body, true);

        String body = org.body;
        int pos = body.indexOf('.');
        if (pos == -1) {
            throw new IllegalArgumentException("No augmented production found");
        }
        if (pos < body.length() - 1) {
            String nextSym = "" + body.charAt(pos+1);
            if (!Helper.isTerminal(nextSym)) {
                NonTerminal nt = mapGrammer.get(nextSym);
                for (String prod : nt.productions) {
                    Production newprod = new Production(nt, augment(prod));

                    if (taken.get(newprod.body) == null) {
                        taken.put(newprod.body, true);
                        ans.add(newprod);
                        ans.addAll(closure(newprod, taken));
                    }
                }
            }
        }
        return ans;
    }

    String augment(String org) {
        return "." + org;
    }

    Production augment(Production org) {
        String newbody = "." + org.body;
        return new Production(org.head, newbody);
    }

    void checkInput() {
        try {
            if (grammer == null) {
                grammer = Helper.getGrammer(new FileReader("grammer.txt")); 
            }
            mapGrammer = Helper.map(grammer);
            //Helper.displayGrammer(grammer);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        ArrayList<State> automaton = new CreateAutomaton().run();
        Helper.displayStates(automaton);
    }
}

