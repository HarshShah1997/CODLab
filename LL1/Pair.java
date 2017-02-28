class Pair<E, S> {
    E first;
    S second;

    Pair(E firstElement, S secondElement) {
        first = firstElement;
        second = secondElement;
    }

    public String toString() {
        String disp = "";
        if (first != null) {
            disp += first.toString() + " and ";
        } 
        if (second != null) {
            disp += second.toString();
        }
        return disp;
    }
}
