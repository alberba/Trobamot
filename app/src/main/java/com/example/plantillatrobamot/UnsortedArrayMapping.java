package com.example.plantillatrobamot;

import java.util.Iterator;

public class UnsortedArrayMapping<K, V> {
    private final K[] claus;
    private final V[] valors;
    private int n;

    public UnsortedArrayMapping(int max) {
        claus = (K[]) new Object[max];
        valors = (V[]) new Object[max];
        n = 0;
    }

    public V get(K key) {
        int i = 0;

        while (i < n) {
            if (key.equals(claus[i]))
                return valors[i];
            i++;
        }

        return null;
    }

    public V put(K key, V value) {
        if (key != null) {
            V ant;
            int i = 0;
            boolean found = false;

            for (; (i < n) && !found; i++) {// Si found == true se hará el i++ o no?
                found = claus[i].equals(key);
            }

            if (found) {
                i--;
                ant = valors[i];
                valors[i] = value;
                return ant;
            } else {
                claus[n] = key;
                valors[n] = value;
                n++;
                return null;
            }
        }
        return null;
    }

    public V remove(K key) {
        int i = 0;
        boolean found = false;

        for (; (i < n) && !found; i++) {// Si found == true se hará el i++ o no?
            found = claus[i].equals(key);
        }

        if (found) {
            i--;
            n--;
            V val = valors[i];
            valors[i] = valors [n];
            claus[i] = claus[n];
            return val;
        } else return null;

    }

    public boolean isEmpty() {
        return n == 0;
    }

    public Iterator iterator() {
        Iterator it = new IteratorUnsortedArrayMapping();
        return it;
    }

    protected class Pair {
        private final K key;
        private final V value;

        private Pair(K k, V v) {
            key = k;
            value = v;
        }

        public K getKey() {
            return key;
        }
        public V getValue() {
            return value;
        }
    }

    private class IteratorUnsortedArrayMapping implements Iterator {
        private int idxIterator;

        private IteratorUnsortedArrayMapping() {
            idxIterator = 0;
        }

        public boolean hasNext() {
            return idxIterator != n;
        }

        public Object next() {
            Pair p = new Pair(claus[idxIterator], valors[idxIterator]);
            idxIterator++;
            return p;
        }
    }
}