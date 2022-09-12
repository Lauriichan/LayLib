package me.lauriichan.laylib.command.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LockedList<E> implements List<E> {

    private final List<E> list;
    private final AtomicBoolean locked;

    public LockedList() {
        this(new ArrayList<>(), new AtomicBoolean(false));
    }

    public LockedList(List<E> list) {
        this(list, new AtomicBoolean(false));
    }

    @SuppressWarnings("unchecked")
    public LockedList(E... array) {
        this(new ArrayList<>(), new AtomicBoolean(false));
        Collections.addAll(list, array);
    }

    private LockedList(List<E> list, AtomicBoolean locked) {
        this.list = list;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked.get();
    }

    public LockedList<E> lock() {
        locked.set(true);
        return this;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new LockedItr<>(list.iterator(), locked);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (locked.get()) {
            return false;
        }
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        if (locked.get()) {
            return false;
        }
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (locked.get()) {
            return false;
        }
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (locked.get()) {
            return false;
        }
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (locked.get()) {
            return false;
        }
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (locked.get()) {
            return false;
        }
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        if (locked.get()) {
            return;
        }
        list.clear();

    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        if (locked.get()) {
            return list.get(index);
        }
        return list.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        if (locked.get()) {
            return;
        }
        list.add(index, element);
    }

    @Override
    public E remove(int index) {
        if (locked.get()) {
            return list.get(index);
        }
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new LockedListItr<>(list.listIterator(), locked);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new LockedListItr<>(list.listIterator(index), locked);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new LockedList<>(list.subList(fromIndex, toIndex), locked);
    }

    private static final class LockedItr<V> implements Iterator<V> {

        private final Iterator<V> iterator;
        private final AtomicBoolean locked;

        public LockedItr(final Iterator<V> iterator, final AtomicBoolean locked) {
            this.iterator = iterator;
            this.locked = locked;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public void remove() {
            if (locked.get()) {
                return;
            }
            iterator.remove();
        }

        @Override
        public V next() {
            return iterator.next();
        }

    }

    private static final class LockedListItr<V> implements ListIterator<V> {

        private final ListIterator<V> iterator;
        private final AtomicBoolean locked;

        public LockedListItr(final ListIterator<V> iterator, final AtomicBoolean locked) {
            this.iterator = iterator;
            this.locked = locked;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public V next() {
            return iterator.next();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public V previous() {
            return iterator.previous();
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            if (locked.get()) {
                return;
            }
            iterator.remove();
        }

        @Override
        public void set(V e) {
            if (locked.get()) {
                return;
            }
            iterator.set(e);
        }

        @Override
        public void add(V e) {
            if (locked.get()) {
                return;
            }
            iterator.add(e);
        }

    }

}
