package elliott.back.maps;

import common.Tuple;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/***
 * This class is a simple map, with no concurrency protections, that is based on
 * a flat array which is doubled every time it becomes full (on insert).
 *
 * Both inserts and lookups use probing in the array to find the next free slot.
 *
 * Limited to around MAX_INT entries because arrays are int-indexed in Java.
 */
public class SimpleFlatMap <K,V> implements Map<K,V> {

    private Tuple<K,V> [] backing;
    private int currentSize = 0;

    /**
     * Default construct = initial 32 entries
     */
    public SimpleFlatMap() {
        this(32);
    }

    public SimpleFlatMap( int initialSize ) {
        backing = new Tuple [initialSize];
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public boolean isEmpty() {
        return currentSize == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(K key : map.keySet() )
            this.put(key, map.get(key));
    }

    @Override
    public void clear() {
        Arrays.fill(this.backing, null);
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}