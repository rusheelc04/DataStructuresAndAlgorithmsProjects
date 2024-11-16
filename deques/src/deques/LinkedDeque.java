package deques;

// @see Deque

public class LinkedDeque<T> extends AbstractDeque<T> {
    private int size; // Number of elements in the deque
    Node<T> front; // Sentinel Node at the front of the deque
    Node<T> back; // Sentinel Node at the back of the deque

    // Constructor that initializes the size to 0, sentinel nodes at the front and back
    public LinkedDeque() {
        size = 0;
        front = new Node<>(null); // front sentinel node without a value
        back = new Node<>(null); // back sentinel node without a value
        front.next = back; // link front sentinel to the back sentinel through the next reference
        back.prev = front; // link the back sentinel to the front sentinel through the prev reference
    }

    // Add elem to front of the deque. Adjusts pointers of front sentinel and the node that currently follows it, to
    // insert the new node directly after the front sentinel
    public void addFirst(T item) {
        size += 1; // increment size
        Node<T> temp = front.next; // store current first node in a temp variable
        // create new node with item, insert between front sentinel and previous first node
        front.next = new Node<>(item, front, temp);
        temp.prev = front.next; // update previous first prev reference to point to the new first
    }

    // Add elem to back of deque. Adjusts pointers of back sentinel and node that currently precedes it, to insert new
    // node directly before the back sentinel.
    public void addLast(T item) {
        size += 1; // increment size
        Node<T> temp = back.prev; // store current last node in temp variable
        // create new node with item, insert between the previous last node and back sentinel
        back.prev = new Node<>(item, temp, back);
        temp.next = back.prev; // update previous last next reference to point to new last
    }

    // Remove and return first elem. Adjusts pointer of front sentinel to skip the first node, removing it
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1; // decrease size
        Node<T> temp = front.next; // store first node in temp variable
        front.next = front.next.next; // front sentinel next skips over first, removing it
        front.next.prev = front; // new first prev pointer points back to front sentinel
        return temp.value; // return the removed node value
    }

    // Remove and return last elem. Adjusts pointer of back sentinel to skip the last node, removing it
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1; // decrease size
        Node<T> temp = back.prev; // store last node in temp variable
        back.prev = back.prev.prev; // back sentinel prev pointer skips over the last node, removing it
        back.prev.next = back; // new last next points to back sentinel
        return temp.value; // return the removed node value
    }

    // Gets elem at given index. starts search from front or the back based on the index closeness
    public T get(int index) {
        if ((index >= size) || (index < 0)) {
            return null;
        }
        if (index <= size / 2) { // if index is closer to front, start search from front
            Node<T> curr = front.next; // initialize curr to first elem
            for (int i = 0; i < index; i++) {
                curr = curr.next; // Traverse forward till given index
            }
            return curr.value; // return curr at index
        } else { // if index is closer to back, start search from back
            Node<T> curr = back.prev; // initialize curr to last elem
            for (int i = size - 1; i > index; i--) {
                curr = curr.prev; // Traverse back till given index
            }
            return curr.value; // return curr at index
        }
    }

    // returns the current # of elems
    public int size() {
        return size;
    }
}
