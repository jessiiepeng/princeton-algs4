import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] arr;
    private int size, cap;

    // construct an empty randomized queue
    public RandomizedQueue() {
        arr = (Item[]) new Object[1];
        size = 0;
        cap = 1;
    }

    // double array size when full; halving array size when its 25% full
    // 0 for halving, 1 for doubling
    private void resize(int newSize) {
        Item[] copy = (Item[]) new Object[newSize];
        System.arraycopy(arr, 0, copy, 0, size);
        arr = copy;
        cap = newSize;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException();
        // check if need to double size so see if full
        if (cap == size) resize(cap * 2);
        arr[size] = item;
        size++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        int i = StdRandom.uniform(size);
        Item item = arr[i];

        // put last item to removed item index --> then removed last index
        arr[i] = arr[size - 1];
        arr[size - 1] = null;
        size--;

        // check if need to halve so see if 1/4 full
        if (size <= cap / 4 && size != 0) resize(cap / 2);

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();
        int i = StdRandom.uniform(size);
        return arr[i];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedIterator();
    }

    private class RandomizedIterator implements Iterator<Item> {
        private int index = 0;
        private Item[] copy;

        RandomizedIterator() {
            copy = (Item[]) new Object[size];
            System.arraycopy(arr, 0, copy, 0, size);
            StdRandom.shuffle(copy);
        }

        public boolean hasNext() {
            return index != size;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy[index++];
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        rq.enqueue(5);
        rq.enqueue(1);
        rq.enqueue(2);
        System.out.println(rq.size());

        System.out.println(rq.dequeue());

        Iterator it = rq.iterator();
        System.out.println(it.next());
        System.out.println(it.next());

    }

}
