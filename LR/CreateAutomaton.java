import java.util.*;
import java.io.*;

class CreateAutomaton {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer = new HashMap<String,NonTerminal>();

    public CreateAutomaton() {
        grammer = null;
    }

    public CreateAutomaton(ArrayList<NonTerminal> inpGrammer) {
        grammer = inpGrammer;
    }

    void run() {
        checkInput();

        State initialState = createInitialState();
        System.out.println(initialState);

    }

    State createInitialState() {
        NonTerminal head = new NonTerminal("Z");
        String body = augment(grammer.get(0).name);
        Production production = new Production(head, body);

        State initialState = new State();
        initialState.productions.add(production);
        initialState.productions.addAll(closure(production));

        return initialState;
    }

    ArrayList<Production> closure(Production org) {
        HashMap<String,Boolean> taken = new HashMap<String,Boolean>();
        return closure(org, taken);
    }
        
    ArrayList<Production> closure(Production org, HashMap<String,Boolean> taken) {        
        ArrayList<Production> ans = new ArrayList<Production>();
        //ans.add(org);
        taken.put(org.body, true);

        String body = org.body;
        int pos = body.indexOf('.');
        if (pos == -1) {
            throw new IllegalArgumentException("No augmented production found");
        }
        if (pos < body.length() - 1) {
            String nextSym = "" + body.charAt(pos+1);
            if (!isTerminal(nextSym)) {
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
            Helper.displayGrammer(grammer);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    boolean isTerminal(String symbol) {
        return isTerminal(symbol.charAt(0));
    }

    boolean isTerminal(char symbol) {
        if (symbol < 'A' || symbol > 'Z') {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        new CreateAutomaton().run();
    }
}

