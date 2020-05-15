package utils;

public class Pair<T, K> {
    private T first;
    private K second;

    public Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }

    /**
     * Swaps the two elements, only works if they are of the same type.
     */
    public void swap() {
        K buff = second;
        second = (K) first;
        first = (T) buff;
    }
}
