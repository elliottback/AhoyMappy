package elliott.back.common;

import java.util.Map;
import java.util.Objects;

/**
 * Tuple class for storing key,value pairs
 */
public class Tuple <K,V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V value) {
        this.value = value;
        return this.value;
    }

    /**
     * Equality is based on keys only
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(key, tuple.key);
    }

    /**
     * Hashcode is based on keys only
     * @return
     */
    @Override
    public int hashCode() {

        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}