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

    ArrayList<String> getTransitionSymbols() {
        Set<String> transitionSymbols = new HashSet<String>();
        for (Production prod : productions) {
            int pos = prod.body.indexOf(".");
            if (pos != prod.body.length() - 1) {
                transitionSymbols.add("" + prod.body.charAt(pos+1));
            }
        }
        ArrayList<String> ans = new ArrayList<String>(transitionSymbols);
        return ans;
    }

    @Override
    public String toString() {
        String ans = "";
        for (Production prod : productions) {
            ans += prod.toString() + '\n';
        }
        return ans;
    }

    @Override
    public boolean equals(Object second) {
        State two = (State)second;
        return this.productions.equals(two.productions);
    }

    @Override
    public int hashCode() {
        return this.productions.hashCode();
    }
}

