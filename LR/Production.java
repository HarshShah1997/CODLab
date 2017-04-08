import java.util.*;

class Production {

    NonTerminal head;
    String body;

    public Production(NonTerminal nt, String prod) {
        head = nt;
        body = prod;
    }

    @Override
    public String toString() {
        String disp = head.name + " -> " + body;
        return disp;
    }
}
