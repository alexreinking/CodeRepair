package coderepair.synthesis;

import java.io.Serializable;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by alexreinking on 11/11/14.
 */
class BoundedSortedSet<E extends Comparable>
        extends TreeSet<E> implements NavigableSet<E>, SortedSet<E>, Cloneable, Serializable {
    private final int capacity;

    public BoundedSortedSet(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    public boolean add(E e) {
        if (size() < capacity)
            return super.add(e);
        E highest = pollLast();
        if(e.compareTo(highest) < 0)
            highest = e;
        return super.add(highest);
    }
}
