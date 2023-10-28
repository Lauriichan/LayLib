package me.lauriichan.laylib.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class JsonArray implements IJson<List<IJson<?>>>, List<IJson<?>>, RandomAccess {

    private final ObjectArrayList<IJson<?>> list = new ObjectArrayList<>();

    public boolean contains(final int index, final JsonType type) {
        final IJson<?> value = get(index);
        return value == null || !type.hasType(value);
    }

    /*
     * List override
     */

    @Override
    public boolean contains(final Object o) {
        if (o instanceof IJson<?>) {
            return list.contains(o);
        }
        return list.contains(IJson.of(o));
    }

    /*
     * Object override
     */

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JsonArray) {
            return list.equals(((JsonArray) obj).list);
        }
        if (obj instanceof List) {
            return list.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    /*
     * IJson implementation
     */

    @Override
    public JsonType type() {
        return JsonType.ARRAY;
    }

    @Override
    public List<IJson<?>> value() {
        return new ObjectArrayList<>(list);
    }

    /*
     * List implementation
     */

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(final IJson<?> e) {
        return list.add(e);
    }

    @Override
    public boolean remove(final Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends IJson<?>> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends IJson<?>> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public IJson<?> get(final int index) {
        return list.get(index);
    }

    @Override
    public IJson<?> set(final int index, final IJson<?> element) {
        return list.set(index, element);
    }

    @Override
    public void add(final int index, final IJson<?> element) {
        list.add(index, element);
    }

    @Override
    public IJson<?> remove(final int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<IJson<?>> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<IJson<?>> listIterator(final int index) {
        return list.listIterator(index);
    }

    @Override
    public List<IJson<?>> subList(final int fromIndex, final int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Iterator<IJson<?>> iterator() {
        return list.iterator();
    }

}
