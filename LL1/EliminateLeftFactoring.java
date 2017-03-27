import java.io.*;
import java.util.*;

class EliminateLeftFactoring {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer;

    private char symbol = 'M';

    public EliminateLeftFactoring() {
        grammer = null;
    }

    public EliminateLeftFactoring(ArrayList<NonTerminal> gram) {
        grammer = gram;
    }

    ArrayList<NonTerminal> run() {
        if (grammer == null) {
            grammer = Helper.getGrammer(new InputStreamReader(System.in));
        }
        mapGrammer = Helper.map(grammer);
        Helper.displayGrammer(grammer);

        eliminateLeftFactoring();
        Helper.displayGrammer(grammer);
        return grammer;
    }

    void eliminateLeftFactoring() {
        for (int i = 0; i < grammer.size(); i++) {
            NonTerminal nt =  grammer.get(i);
            eliminateLeftFactoring(nt);
        }        
    }

    void eliminateLeftFactoring(NonTerminal nt) {

        ArrayList<ArrayList<String>> common = toGroups(nt.productions);
        
        for (ArrayList<String> group : common) {
            if (group.size() == 1) {
                continue;
            }
            String lcp = findLcp(group);
            nt.productions.removeAll(group);
            NonTerminal newnt = createNonTerminal(getSymbol());
            nt.productions.add(lcp + newnt.name);

            addGroup(newnt, group, lcp);
        }
    }

    ArrayList<ArrayList<String>> toGroups(ArrayList<String> productions) {
        HashMap<String,Boolean> marked = new HashMap<String,Boolean>();

        ArrayList<ArrayList<String>> groups= new ArrayList<ArrayList<String>>();
        for (int i = 0; i < productions.size(); i++) {
            String currProd = productions.get(i);
            if (marked.get(currProd) != null) {
                continue;
            }
            ArrayList<String> currGroup = new ArrayList<String>();
            currGroup.add(currProd);

            for (int j = i+1; j < productions.size(); j++) {
                String newProd = productions.get(j);
                if (newProd.charAt(0) == currProd.charAt(0)) {
                    currGroup.add(newProd);
                    marked.put(newProd, true);
                }
            }
            groups.add(currGroup);
        }
        return groups;
    }

    /* Find Longest Common Prefix */
    String findLcp(ArrayList<String> group) {
        String lcp = group.get(0);
        for (int i = 1; i < group.size(); i++) {
            String prod = group.get(i);

            int len = lcp.length() < prod.length() ? lcp.length() : prod.length();
            int j = 0;
            for (j = 0; j < len; j++) {
                if (prod.charAt(j) != lcp.charAt(j)) {
                    break;
                }
            }
            lcp = lcp.substring(0, j);

        }
        return lcp;
    }

    void addGroup(NonTerminal nt, ArrayList<String> group, String lcp) {
        for (String prod : group) {
            String remain = prod.substring(lcp.length());
            if (remain.length() == 0) {
                nt.nullable = true;
            } else {
                nt.productions.add(remain);
            }
        }
    }

    NonTerminal createNonTerminal(String newSym) {
        NonTerminal newNt = new NonTerminal(newSym);
        grammer.add(newNt);
        mapGrammer.put(newSym, newNt);
        return newNt;
    }

    String getSymbol() {
        String ans = ""+symbol;
        symbol--;
        return ans;
    }

    public static void main(String[] args) {
        new EliminateLeftFactoring().run();
    }
}

