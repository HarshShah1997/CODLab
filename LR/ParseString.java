import java.util.*;
import java.io.*;

class ParseString {

    ArrayList<NonTerminal> grammer;
    HashMap<String,NonTerminal> mapGrammer;

    String inputString;

    HashMap<Pair<Integer,String>,String> table;

    ArrayList<Production> allProductions;

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
        table = (new LR1Table(grammer)).buildTable();

        boolean result = parse();
        System.out.println(result);
    }

    boolean parse() {
        Stack<String> stack = new Stack<String>();
        stack.push("0");

        int pointer = 0;
        boolean accept = false;
        while (!stack.empty()) {
            int top = Integer.parseInt(stack.peek());
            boolean last = false;
            if (pointer == inputString.length()) {
                pointer--;
                last = true;
            }
            Pair<Integer,String> key = new Pair<Integer,String>
                (top, ""+inputString.charAt(pointer));
            if (table.get(key) == null) {
                break;
            }
            String value = table.get(key);
            System.out.println(stack);
            if (last) {
                if (value.equals("accept")) {
                    accept = true;
                }
                break;
            }
            if (value.charAt(0) == 'S') {
                stack.push(""+inputString.charAt(pointer));
                pointer++;
                stack.push(value.substring(1));
            } else if (value.charAt(0) == 'R') {
                int index = value.charAt(1) - '0';
                Production reduce = allProductions.get(index);
                popFromStack(stack, reduce.body.length() * 2);

                int prev = Integer.parseInt(stack.peek());
                stack.push(reduce.head.name);

                Pair<Integer,String> newkey = new Pair<Integer,String>
                    (prev, reduce.head.name);
                if (table.get(newkey) == null) {
                    break;
                }
                stack.push(table.get(newkey));
            }
        }
        return accept;
    }

    void checkInput() {
        try {
            if (grammer == null) {
                grammer = Helper.getGrammer(new FileReader("grammer.txt"));
            }
            if (inputString == null) {
                inputString = Helper.getInput();
            }
            mapGrammer = Helper.map(grammer);
            allProductions = Helper.fillProductions(grammer);
            //Helper.displayGrammer(grammer);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    void popFromStack(Stack<String> stack, int times) {
        for (int i = 0; i < times; i++) {
            stack.pop();
        }
    }

    public static void main(String[] args) {
        new ParseString().run();
    }
}

