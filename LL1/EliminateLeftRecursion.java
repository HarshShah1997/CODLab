import java.io.*;
import java.util.*;

class EliminateLeftRecursion {

    ArrayList<NonTerminal> inputGrammer;
    HashMap<String, NonTerminal> mapInputGrammer;

    private char symbol = 'Z';
    private HashMap<String, String> taken = new HashMap<String, String>();

    public EliminateLeftRecursion() {
        inputGrammer = null;
    }

    public EliminateLeftRecursion(ArrayList<NonTerminal> grammer) {
        inputGrammer = grammer;
    }

    ArrayList<NonTerminal> run() {
        if (inputGrammer == null) {
            inputGrammer = Helper.getGrammer(new InputStreamReader(System.in));
        }
        mapInputGrammer = Helper.map(inputGrammer);
        Helper.displayGrammer(inputGrammer);

        eliminateLeft();
        Helper.displayGrammer(inputGrammer);
        return inputGrammer;
    }

    void eliminateLeft() {
        for (int i = 0; i < inputGrammer.size(); i++) {
            NonTerminal nt = inputGrammer.get(i);
            for (int j = 0; j < i; j++) {
                eliminateLeft(nt, i, j);
            }
            eliminateDirect(nt);
        }
    }

    void eliminateLeft(NonTerminal parent, int i, int j) {
        ArrayList<String> toAdd = new ArrayList<String>();
        ArrayList<String> toRemove = new ArrayList<String>();

        for (String prod : parent.productions) {
            char first = prod.charAt(0);
            if (!isTerminal(first)) {
                NonTerminal child = mapInputGrammer.get(""+first);
                int currIndex = inputGrammer.indexOf(child);
                if (currIndex == j) {
                    ArrayList<String> newProds = append(child.productions, 
                            prod.substring(1));
                    toAdd.addAll(newProds);
                    toRemove.add(prod);
                }
            }
        }
        parent.productions.addAll(toAdd);
        parent.productions.removeAll(toRemove);
    }

    void eliminateDirect() {
        for (int i = 0; i < inputGrammer.size(); i++) {
            eliminateDirect(inputGrammer.get(i));
        }
    }

    void eliminateDirect(NonTerminal nt) {
        ArrayList<String> nonDirect = getNonDirect(nt);
        ArrayList<String> direct = getDirect(nt);

        //System.out.println(nt.name);
        //System.out.println("NonDirect: " + nonDirect);
        //System.out.println("Direct: " + direct);
        if (direct.size() == 0) {
            return;
        }
        NonTerminal newNt = createNonTerminal(getSymbol(nt.name));

        ArrayList<String> orgProd = append(nonDirect, newNt.name);
        if (nt.nullable) {
            orgProd.add(newNt.name);
        }

        ArrayList<String> newProd = append(direct, newNt.name);
        newNt.nullable = true;

        nt.productions = orgProd;
        newNt.productions = newProd;
    }

    ArrayList<String> getNonDirect(NonTerminal nt) {
        ArrayList<String> nonDirect = new ArrayList<String>();
        for (String prod : nt.productions) {
            if (!("" + prod.charAt(0)).equals(nt.name)) {
                nonDirect.add(prod);
            }
        }
        return nonDirect;
    }

    ArrayList<String> getDirect(NonTerminal nt) {
        ArrayList<String> direct = new ArrayList<String>();
        for (String prod : nt.productions) {
            if (("" + prod.charAt(0)).equals(nt.name)) {
                direct.add(prod.substring(1));
            }
        }
        return direct;
    }

    NonTerminal createNonTerminal(String newSym) {
        NonTerminal newNt = new NonTerminal(newSym);
        inputGrammer.add(newNt);
        mapInputGrammer.put(newSym, newNt);
        return newNt;
    }

    String getSymbol(String name) {
        String ans = ""+symbol;
        symbol--;
        taken.put(name, ans);
        return ans;
    }

    ArrayList<String> append(ArrayList<String> org, String add) {
        ArrayList<String> ans = new ArrayList<String>();
        for (String prod : org) {
            ans.add(prod + add);
        }
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
        new EliminateLeftRecursion().run();
    }
}

