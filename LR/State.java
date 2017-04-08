import java.util.*;

class State {

    ArrayList<Production> productions;

    HashMap<String,State> transitions = new HashMap<String,State>();

    public State() {
        productions = new ArrayList<Production>();
    }

    public State(ArrayList<Production> prevProd) {
        productions = prevProd;
    }

    @Override
    public String toString() {
        String ans = "";
        for (Production prod : productions) {
            ans += prod.toString() + '\n';
        }
        return ans;
    }
}

