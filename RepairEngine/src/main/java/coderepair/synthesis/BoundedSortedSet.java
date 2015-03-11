package coderepair.synthesis;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by alexreinking on 11/11/14.
 */
class BoundedSortedSet<E> extends TreeSet<E> implements NavigableSet<E>, SortedSet<E>, Cloneable, Serializable {
    private final int capacity;
    private final Comparator<E> comparator;

    public BoundedSortedSet(int capacity, Comparator<E> comparator) {
        super(comparator);
        this.capacity = capacity;
        this.comparator = comparator;
    }

    @Override
    public boolean add(E e) {
        if (size() < capacity)
            return super.add(e);
        E highest = pollLast();
        if (comparator.compare(e, highest) < 0)
            highest = e;
        return super.add(highest);
    }
}
