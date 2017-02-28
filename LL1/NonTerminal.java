import java.util.*;
class NonTerminal {
    String name;
    ArrayList<String> productions;
    boolean nullable;

    public NonTerminal(String n) {
        name = n;
        productions = new ArrayList<String>();
        nullable = false;
    }

    public String toString() {
        /*String output = name + " -> ";
        for (String production : productions) {
            output += production + " | ";
        }
        if (nullable) {
            output += "epsilon";
        }
        return output;
        */
        return name;
    }
}

