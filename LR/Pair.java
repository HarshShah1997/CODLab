class Pair<E, S> {
    E first;
    S second;

    Pair(E firstElement, S secondElement) {
        first = firstElement;
        second = secondElement;
    }
    
    @Override
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

    @Override
    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair))
            return false;
        if (this == obj)
            return true;
        return equal(first, ((Pair) obj).first)
            && equal(second, ((Pair) obj).second);
    }

    private boolean equal(Object o1, Object o2) {
        return o1 == null ? o2 == null : (o1 == o2 || o1.equals(o2));
    }
}
