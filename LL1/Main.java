import java.io.*;
import java.util.*;

class Main {

    ArrayList<NonTerminal> grammer = new ArrayList<NonTerminal>();
    HashMap<String, NonTerminal> strToNonTerminal = new HashMap<String, NonTerminal>();

    HashMap<NonTerminal, Set<String>> first = new HashMap<NonTerminal, Set<String>>();

    void run() {
        getInputFromFile("grammer.txt");
        calculateFirst();
        displayFirst();
    }

    void calculateFirst() {
        for (NonTerminal nt : grammer) {
            calculateFirst(nt);
        }
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

            if (currSymbol <= 'A' || currSymbol >= 'Z') {
                currFirst.add("" + currSymbol);
                break;
            } else {
                NonTerminal nt = strToNonTerminal.get(""+currSymbol);
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
        for (NonTerminal nt : grammer) {
            System.out.println(nt.name + " " + first.get(nt));
        }
    }



    void getInputFromFile(String filename) {
        try {
            String input = "";
            BufferedReader br = new BufferedReader(new FileReader(filename));

            NonTerminal previous = null;
            while ((input = br.readLine()) != null) {
                String[] split = input.split("\\s*->\\s*");
                System.out.println(Arrays.toString(split));

                if (split.length == 1) {
                    String production = (split[0]).split("\\s*\\Q|\\E\\s*")[1];
                    if (production.equals("epsilon")) {
                        previous.nullable = true;
                    } else {
                        previous.productions.add(production);
                    }
                } else {
                    previous = new NonTerminal(split[0]);
                    previous.productions.add(split[1]);
                    strToNonTerminal.put(split[0], previous);
                    grammer.add(previous);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
