package link.mcseu.crypto.pgp.util;

import java.util.Iterator;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Implements an Iterator for a 2D-for-loop.
 *
 * @author Björn 'manf' Heinrichs
 *
 * @param <T> Type of lower iterable
 */
@RequiredArgsConstructor(access = PRIVATE)
public final class Iterator2D<T> implements Iterator<T> {
    private final Iterator<? extends Iterable<T>> parent;
    private Iterator<T> child;

    @Override
    public boolean hasNext() {
        if(child != null && child.hasNext()) return true;
        if(parent.hasNext()) {
            child = parent.next().iterator();
            return hasNext();
        }
        return false;
    }

    @Override
    public T next() {
        return child.next();
    }

    public static <T> Iterable<T> of(Iterable<? extends Iterable<T>> parent) {
        return () -> new Iterator2D<>(parent.iterator());
    }
}