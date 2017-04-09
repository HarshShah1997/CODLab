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

    @Override
    public boolean equals(Object second) {
        Production two = (Production)second;
        return (this.head.equals(two.head) && this.body.equals(two.body));
    }

    @Override
    public int hashCode() {
        return 31 * head.name.hashCode() + body.hashCode();
    }
}
