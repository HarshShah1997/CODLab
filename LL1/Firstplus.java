import java.io.*;
import java.util.*;

class Firstplus {

    ArrayList<NonTerminal> grammer;

    HashMap<String, NonTerminal> mapGrammer = 
        new HashMap<String, NonTerminal>();

    HashMap<NonTerminal, Set<String>> first = 
        new HashMap<NonTerminal, Set<String>>();

    HashMap<NonTerminal, Set<String>> follow = 
        new HashMap<NonTerminal, Set<String>>();

    HashMap<Pair<NonTerminal, String>, Set<String>> firstplus =
        new HashMap<Pair<NonTerminal, String>, Set<String>>();

    public Firstplus() {
        grammer = null;
    }

    public Firstplus(ArrayList<NonTerminal> inputGrammer) {
        grammer = inputGrammer;
    }

    HashMap<Pair<NonTerminal, String>, Set<String>> run() {
        if (grammer == null) {
            grammer = Helper.getGrammer(new InputStreamReader(System.in));
        }
        mapGrammer = Helper.map(grammer);
        Helper.displayGrammer(grammer);

        calculateFirst();
        displayFirst();

        calculateFollow();
        displayFollow();

        calculateFirstplus();
        displayFirstplus();

        return firstplus;
    }

    HashMap<NonTerminal, Set<String>> calculateFirst() {
        for (NonTerminal nt : grammer) {
            calculateFirst(nt);
        }
        return first;
    }

    Set<String> calculateFirst(NonTerminal nt) {
        if (first.get(nt) != null) {
            return first.get(nt);
        }
        Set<String> currFirst = new HashSet<String>();
        for (String production : nt.productions) {
            currFirst.addAll(calculateFirst(production));
        }
        if (nt.nullable) {
            currFirst.add("epsilon");
        }
        first.put(nt, currFirst);
        return currFirst;
    }

    Set<String> calculateFirst(String production) {
        Set<String> currFirst = new HashSet<String>();
        int i = 0;
        for (i = 0; i < production.length(); i++) {
            char currSymbol = production.charAt(i);

            if (isTerminal(currSymbol)) {
                currFirst.add("" + currSymbol);
                break;
            } else {
                NonTerminal nt = mapGrammer.get(""+currSymbol);
                Set<String> result = calculateFirst(nt);
                currFirst.addAll(result);
                if (currFirst.contains("epsilon")) {
                    currFirst.remove("epsilon");
                } else {
                    break;
                }
            }
        }
        if (i == production.length()) {
            currFirst.add("epsilon");
        }
        return currFirst;
    }

    void displayFirst() {
        System.out.println("");
        System.out.println("FIRST");
        for (NonTerminal nt : grammer) {
            System.out.println(nt.name + " " + first.get(nt));
        }
    }

    HashMap<NonTerminal, Set<String>> calculateFollow() {
        ArrayList<Pair<NonTerminal, NonTerminal>> toProcess = 
            new ArrayList<Pair<NonTerminal, NonTerminal>>();

        fillEnd();
        for (NonTerminal nt : grammer) {
            for (String production : nt.productions) {
                //System.out.println("Prod: " + production);
                toProcess.addAll(calculateFollow(nt, production));
            }
        }
        process(toProcess);        
        return follow;
    }

    /* Update follow of all Non Terminals in the production */
    ArrayList<Pair<NonTerminal, NonTerminal>> 
        calculateFollow(NonTerminal parentNt, String production) {

            ArrayList<Pair<NonTerminal, NonTerminal>> toProcess 
                = new ArrayList<Pair<NonTerminal, NonTerminal>>();

            for (int i = 0; i < production.length(); i++) {
                char symbol = production.charAt(i);
                if (!isTerminal(symbol)) {
                    String rem = production.substring(i+1);
                    Set<String> currFollow = calculateFirst(rem);
                    NonTerminal currNonTerminal = mapGrammer.get("" + symbol);

                    boolean nullable = false;

                    if (currFollow.contains("epsilon")) {
                        nullable = true;
                        currFollow.remove("epsilon");
                    }

                    addToFollow(currNonTerminal, currFollow);

                    if (nullable) {
                        toProcess.add(new Pair<NonTerminal, NonTerminal>
                                (currNonTerminal, parentNt));
                    }
                }
            }
            return toProcess;
        }

    /* Add current follow to the follow of current Non Terminal */
    void addToFollow(NonTerminal currNonTerminal, Set<String> currFollow) {
        if (follow.get(currNonTerminal) == null) {
            follow.put(currNonTerminal, currFollow);
        } else {
            Set<String> combined = follow.get(currNonTerminal);
            combined.addAll(currFollow);
            follow.put(currNonTerminal, combined);
        }
    }

    /* Add follow of second to follow of first */
    void process(ArrayList<Pair<NonTerminal, NonTerminal>> toProcess) {
        for (int i = 0; i < toProcess.size(); i++) {
            NonTerminal to = toProcess.get(i).first;
            NonTerminal from = toProcess.get(i).second;

            Set<String> combined = follow.get(to);
            combined.addAll(follow.get(from));
            follow.put(to, combined);
        }
    }

    void displayFollow() {
        System.out.println("");
        System.out.println("FOLLOW");
        for (NonTerminal nt : grammer) {
            System.out.println(nt.name + " " + follow.get(nt));
        }
    }

    void calculateFirstplus() {
        for (NonTerminal nt : grammer) {
            calculateFirstplus(nt);
        }
    }

    void calculateFirstplus(NonTerminal nt) {
        for (String production : nt.productions) {
            calculateFirstplus(nt, production);
        }
        if (nt.nullable) {
            Pair<NonTerminal, String> key = 
                new Pair<NonTerminal, String>(nt, "epsilon");
            firstplus.put(key, follow.get(nt));
        }
    }

    void calculateFirstplus(NonTerminal nt, String production) {
        Set<String> currFirst = calculateFirst(production);
        Pair<NonTerminal, String> key = 
            new Pair<NonTerminal, String>(nt, production);

        if (currFirst.contains("epsilon")) {
            currFirst.remove("epsilon");
            currFirst.addAll(follow.get(nt));
        }
        firstplus.put(key, currFirst);
    }

    void displayFirstplus() {
        System.out.println("");
        System.out.println("FIRST PLUS");
        for (NonTerminal nt : grammer) {
            for (String production : nt.productions) {
                Pair<NonTerminal, String> key = 
                    new Pair<NonTerminal, String>(nt, production);

                System.out.println(nt.name + " -> " + production + " " +
                        firstplus.get(key));
            }
            if (nt.nullable) {
                Pair<NonTerminal, String> key = 
                    new Pair<NonTerminal, String>(nt, "epsilon");
                System.out.println(nt.name + " -> epsilon " + 
                        firstplus.get(key));
            }
        }
    }

    /* Add $ to the follow of first production */
    void fillEnd() {
        Set<String> end = new HashSet<String>();
        end.add("$");
        follow.put(grammer.get(0), end);
    }

    boolean isTerminal(char symbol) {
        if (symbol < 'A' || symbol > 'Z') {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        new Firstplus().run();
    }
}
