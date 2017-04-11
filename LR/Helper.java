import java.io.*;
import java.util.*;

public class Helper {

    public static ArrayList<NonTerminal> getGrammer(InputStreamReader isr) {
        ArrayList<NonTerminal> grammer = new ArrayList<NonTerminal>();
        try {
            String input = "";
            BufferedReader br = new BufferedReader(isr);


            NonTerminal previous = null;
            while ((input = br.readLine()) != null) {
                if (input.trim().equals("")) {
                    continue;
                }
                String[] split = input.split("\\s*->\\s*");

                if (split.length == 1) {
                    String production = (split[0]).split("\\s*\\Q|\\E\\s*")[1];
                    production = production.trim();
                    if (production.equals("epsilon")) {
                        previous.nullable = true;
                    } else {
                        previous.productions.add(production);
                    }
                } else {
                    previous = new NonTerminal(split[0]);
                    previous.productions.add(split[1]);
                    grammer.add(previous);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
            
        }
        return grammer;
    }

    public static HashMap<String, NonTerminal> map(ArrayList<NonTerminal> grammer) {
        HashMap<String, NonTerminal> mapGrammer = 
            new HashMap<String, NonTerminal>();
        for (NonTerminal nt : grammer) {
            mapGrammer.put(nt.name, nt);
        }
        return mapGrammer;
    }

    public static void displayGrammer(ArrayList<NonTerminal> grammer) {
        System.out.println("");
        System.out.println("GRAMMER");
        for (NonTerminal nt : grammer) {
            System.out.print(nt.name + " -> ");            
            for (int i = 0; i < nt.productions.size(); i++) {
                if (i != 0) {
                    System.out.print("  |  ");
                }
                System.out.println(nt.productions.get(i));
            }
            if (nt.nullable) {
                if (nt.productions.size() != 0) {
                    System.out.print("  |  ");
                }
                System.out.println("epsilon");
            }
        }
    }

    public static String getInput() {
        String input = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader
                    (System.in));
            input = br.readLine();
        } catch (IOException iex) {
            iex.printStackTrace();
        }
        return input;
    }

    public static void displayStates(ArrayList<State> states) {
        for (State state : states) {
            System.out.println(state);
        }
    }

    public static boolean isTerminal(String symbol) {
        return isTerminal(symbol.charAt(0));
    }

    public static boolean isTerminal(char symbol) {
        if (symbol < 'A' || symbol > 'Z') {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<String> getTerminals(ArrayList<NonTerminal> grammer) {
        Set<String> terminals = new HashSet<String>();
        for (NonTerminal nt : grammer) {
            for (String production : nt.productions) {
                terminals.addAll(getTerminals(production));
            }
        }
        terminals.add("$");
        ArrayList<String> ans = new ArrayList<String>(terminals);
        return ans;
    }

    public static ArrayList<String> getTerminals(String string) {
        ArrayList<String> ans = new ArrayList<String>();
        for (int i = 0; i < string.length(); i++) {
            if (isTerminal(string.charAt(i))) {
                ans.add("" + string.charAt(i));
            }
        }
        return ans;
    }

    public static ArrayList<Production> fillProductions(ArrayList<NonTerminal> grammer) {
        ArrayList<Production> allProductions = new ArrayList<Production>();
        for (NonTerminal nt : grammer) {
            for (String body : nt.productions) {
                Production prod = new Production(nt, body);
                allProductions.add(prod);
            }
        }
        return allProductions;
    }
}

