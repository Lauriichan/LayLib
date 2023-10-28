package me.lauriichan.laylib.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class JsonArray implements IJson<List<IJson<?>>>, List<IJson<?>>, RandomAccess {

    private final ObjectArrayList<IJson<?>> list = new ObjectArrayList<>();

    public boolean contains(int index, JsonType type) {
        IJson<?> value = get(index);
        return value == null || !type.hasType(value);
    }

    /*
     * List override
     */

    @Override
    public boolean contains(Object o) {
        if (o instanceof IJson<?>) {
            return list.contains(o);
        }
        return list.contains(IJson.of(o));
    }

    /*
     * Object override
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JsonArray) {
            return list.equals(((JsonArray) obj).list);
        } else if (obj instanceof List) {
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
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(IJson<?> e) {
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends IJson<?>> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends IJson<?>> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public IJson<?> get(int index) {
        return list.get(index);
    }

    @Override
    public IJson<?> set(int index, IJson<?> element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, IJson<?> element) {
        list.add(index, element);
    }

    @Override
    public IJson<?> remove(int index) {
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
    public ListIterator<IJson<?>> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<IJson<?>> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<IJson<?>> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Iterator<IJson<?>> iterator() {
        return list.iterator();
    }

}
