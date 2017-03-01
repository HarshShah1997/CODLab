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
                    grammer.add(previous);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
            
        }
        return grammer;
    }
}

