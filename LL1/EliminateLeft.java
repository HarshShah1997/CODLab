import java.io.*;
import java.util.*;

class EliminateLeft {

    ArrayList<NonTerminal> inputGrammer;

    HashMap<String, NonTerminal> mapInputGrammer;

    private char symbol = 'Z';
    private HashMap<String, String> taken = new HashMap<String, String>();

    public EliminateLeft() {
        inputGrammer = null;
    }

    public EliminateLeft(ArrayList<NonTerminal> grammer) {
        inputGrammer = grammer;
    }

    void run() {
        if (inputGrammer == null) {
            inputGrammer = Helper.getGrammer(new InputStreamReader(System.in));
        }
        mapInputGrammer = Helper.map(inputGrammer);
        Helper.displayGrammer(inputGrammer);

        eliminateDirect();
        Helper.displayGrammer(inputGrammer);
    }

    void eliminateLeft() {
        for (int i = 0; i < inputGrammer.size(); i++) {
            NonTerminal nt = inputGrammer.get(i);
            eliminateLeft(nt, i);
            //TODO: Eliminate direct recursion
        }
    }

    void eliminateLeft(NonTerminal parent, int i) {
        for (String production : parent.productions) {
            char first = production.charAt(0);
            if (!isTerminal(first)) {
                NonTerminal child = mapInputGrammer.get(""+first);
                int j = inputGrammer.indexOf(child);
                if (j < i) {
                    //ArrayList<String> replaced = replace(production, child);
                    //parent.productions.remove(production);
                    //parent.productions.addAll(replaced);
                }
            }
        }
    }

    void eliminateDirect() {
        for (int i = 0; i < inputGrammer.size(); i++) {
            eliminateDirect(inputGrammer.get(i));
        }
    }

    void eliminateDirect(NonTerminal nt) {
        ArrayList<Pair<Integer, Integer>> pairs = getPairs(nt);
        for (Pair<Integer, Integer> pair : pairs) {
            eliminateDirect(nt, pair);
        }
        for (Pair<Integer, Integer> pair : pairs) {
            int index2 = pair.second;
            nt.productions.remove(index2);
        }
    }

    void eliminateDirect(NonTerminal nt, Pair<Integer, Integer> pair) {
        int index1 = pair.first;
        int index2 = pair.second;

        String pAAlpha = nt.productions.get(index1);
        String pBeta = nt.productions.get(index2);

        NonTerminal newNt;
        String newSym;

        if (taken.get(nt.name) != null) {
            newSym = taken.get(nt.name);
            newNt = mapInputGrammer.get(newSym);
        } else {
            newSym = getSymbol(nt.name);
            newNt = createNonTerminal(newSym);
        }

        newNt.productions.add(pAAlpha.substring(1) + newSym);

        pAAlpha = pBeta + newSym;
        nt.productions.set(index1, pAAlpha);
    }

    ArrayList<Pair<Integer, Integer>> getPairs(NonTerminal nt) {
        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>();
        if (nt.productions.size() > 1) {
            // TODO: Create all pairs instead of one
            String p1 = nt.productions.get(0);
            String p2 = nt.productions.get(1);
            if ((""+p1.charAt(0)).equals(nt.name)) {
                pairs.add(new Pair<Integer, Integer>(0, 1));
            } else if ((""+p2.charAt(0)).equals(nt.name)) {
                pairs.add(new Pair<Integer, Integer>(1, 0));
            }
        }
        return pairs;
    }

    NonTerminal createNonTerminal(String newSym) {
        NonTerminal newNt = new NonTerminal(newSym);
        inputGrammer.add(newNt);
        mapInputGrammer.put(newSym, newNt);
        newNt.nullable = true;
        return newNt;
    }

    String getSymbol(String name) {
        String ans = ""+symbol;
        symbol--;
        taken.put(name, ans);
        return ans;
    }

    boolean isTerminal(char symbol) {
        if (symbol < 'A' || symbol > 'Z') {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        new EliminateLeft().run();
    }
}

