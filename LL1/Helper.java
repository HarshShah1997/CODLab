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
}

