import java.util.*;
import java.io.*;

class ParseString {

    ArrayList<NonTerminal> grammer;

    HashMap<String,NonTerminal> mapGrammer = new HashMap<String,NonTerminal>();

    String inputString;

    HashMap<Pair<NonTerminal, String>, Set<String>> firstplus;

    HashMap<Pair<NonTerminal,String>, Pair<NonTerminal,String>> table;

    public ParseString() {
        grammer = null;
        inputString = null;    
    }

    public ParseString(ArrayList<NonTerminal> inpGrammer) {
        grammer = inpGrammer;
    }

    public ParseString(String inpString) {
        inputString = inpString;
    }

    public ParseString(ArrayList<NonTerminal> inpGrammer, String inpString) {
        grammer = inpGrammer;
        inputString = inpString;
    }

    void run() {
        checkInput();
        firstplus = (new Firstplus(grammer)).run();

        table = createTable(firstplus);
        displayTable();

        boolean result = parseInput();
        System.out.println(result);
    }

    HashMap<Pair<NonTerminal,String>, Pair<NonTerminal,String>> createTable
    (HashMap<Pair<NonTerminal, String>, Set<String>> fp) {

        HashMap<Pair<NonTerminal,String>, Pair<NonTerminal,String>> parseTable
            = new HashMap<Pair<NonTerminal,String>, Pair<NonTerminal,String>>();

        for (Pair<NonTerminal,String> production : fp.keySet()) {
            Set<String> values = fp.get(production);

            for (String value : values) {
                Pair<NonTerminal, String> key = new Pair<NonTerminal,String>
                    (production.first, value);
                parseTable.put(key, production);
            }
        }
        return parseTable;
    }

    boolean parseInput() {
        Stack<String> stack = new Stack<String>();
        int pointer = 0;
        stack.push("$");
        stack.push(grammer.get(0).name);

        while (!stack.empty()) {
            System.out.println(stack.toString());
            String top = stack.pop();
            String front = ""+inputString.charAt(pointer);
            if (top.equals("epsilon")) {
                continue;
            }

            if (isTerminal(top)) {
                if (top.equals(front)) {
                    System.out.println(top);
                    pointer++;
                } else {
                    return false;
                }
            } else {
                Pair<NonTerminal,String> key = new Pair<NonTerminal,String>
                    (mapGrammer.get(top), front);
                Pair<NonTerminal,String> value = table.get(key);
                if (value == null) {
                    return false;
                }
                System.out.println(value.first.name + " -> " + value.second);
                addToStack(stack, value.second);
            }
            System.out.println("");
        }
        if (pointer != inputString.length()) {
            return false;
        } else {
            return true;
        }
    }

    void addToStack(Stack<String> stack, String prod) {
        if (prod.equals("epsilon")) {
            //stack.push("epsilon");
        } else {
            for (int i = prod.length() - 1; i >= 0; i--) {
                stack.push(""+prod.charAt(i));
            }
        }
    }

    void displayTable() {
        System.out.println("");
        System.out.println("PARSE TABLE");
        for (Pair<NonTerminal,String> key : table.keySet()) {
            System.out.print(key.first.name + "," + key.second);
            System.out.print(" : ");

            Pair<NonTerminal,String> value = table.get(key);
            System.out.print(value.first.name + "  ->  " + value.second);
            System.out.println("");
        }
    }

    void checkInput() {
        try {
            if (grammer == null) {
                grammer = Helper.getGrammer(new FileReader("testGrammer.txt"));                
            }
            grammer = (new EliminateLeftRecursion(grammer).run());
            grammer = (new EliminateLeftFactoring(grammer).run());
            if (inputString == null) {
                inputString = Helper.getInput();
            }
            mapGrammer = Helper.map(grammer);
            //Helper.displayGrammer(grammer);
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
        new ParseString().run();
    }
}

