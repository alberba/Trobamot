package com.example.plantillatrobamot;

import java.util.Iterator;
import java.util.Stack;

public class BSTSet<E extends Comparable> {

    private class Node {
        private E elem;

        private Node left, right;

        public Node (E elem, Node left, Node right){
            this.elem = elem;
            this.left = left;
            this.right = right;
        }
    }
    private static class Cerca {
        boolean trobat;
        public Cerca(boolean trobat) {
            this.trobat = trobat;
        }
    }

    private Node root;


    public BSTSet() {
        root = null;
    }

    boolean isEmpty(){
        return root == null;
    }

    public boolean contains (E elem) {
        return contains(elem, root);
    }

    private boolean contains(E elem, Node current) {
        // Si el árbol esta vacio: no encontrado
        if(elem == null) {
            return false;
        } else {
            if(current.elem.equals(elem)) {
                return true;
            }
            // Si l'element es inferior a l'element del node
            if(elem.compareTo(current.elem) < 0){
                return contains(elem, current.left);
            } else {
                return contains(elem, current.right);
            }
        }
    }

    public boolean add(E elem) {
        Cerca cerca = new Cerca(false);
        this.root = add(elem, root, cerca);
        return !cerca.trobat;
    }

    private Node add(E elem, Node current, Cerca cerca){
        if (current == null) {
            return new Node(elem, null, null);
        } else {
            if(current.elem.equals(elem)) {
                cerca.trobat = true;
                return current;
            }
            if(elem.compareTo(current.elem) < 0) {
                current.left = add(elem, current.left, cerca);
            } else {
                current.right = add(elem, current.right, cerca);
            }
            return current;


        }
    }

    private Node remove(E elem, Node current, Cerca cerca) {
        if (current == null) {
            cerca.trobat = false;
            return null;
        }
        if (current.elem.equals(elem)) {
            cerca.trobat = true;
            if (current.left == null && current.right == null) {
                return null;
            } else if (current.left == null && current.right !=null) {
                return current.right;
            } else if (current.left != null && current.right == null) {
                return current.left;
            } else {
                Node plowest = current.right;
                Node parent = current;
                // Cogemos el nodo de mas a la izquierda del hijo derecho
                while (plowest.left != null) {
                    parent = plowest;
                    plowest = plowest.left;
                }
                plowest.left = current.left;
                if(plowest != current.right) {
                    parent.left = plowest.right;
                    plowest.right = current.right;
                }
                return plowest;

            }
        }
        if(elem.compareTo(current.elem) < 0) {
            current.left = remove(elem, current.left, cerca);
        } else {
            current.right = remove(elem, current.right, cerca);

        }
        return current;
    }

    private class IteratorBSTSet implements Iterator {
        // La implementació de l'iterador serà una pila de nodes

        private Stack<Node> iterator;

        public IteratorBSTSet() {
            Node p;
            iterator = new Stack<Node>();
            if (root != null) {
                p = root;
                while (p.left != null) {
                    iterator.push(p);
                    p = p.left;
                }
                iterator.push(p);
            }
        }

        public Object next() {
            Node p = iterator.pop();
            E elem = p.elem;
            if (p.right != null) {
                p = p.right;
                while (p.left != null) {
                    iterator.push(p);
                    p = p.left;
                }
                iterator.push(p);
            }
            return elem;
        }

        public boolean hasNext() {
            return !iterator.isEmpty();
        }
    }
}