package common;

import java.util.*;

public abstract class AbstractMapCommonMethods <K,V> implements Map<K, V> {

    public abstract Tuple <K,V> [] getBackingArray();

    /**
     * This will be a horrible linear scan, we have to check every single item
     */
    @Override
    public boolean containsValue(Object value) {
        Tuple<K,V> [] backing = getBackingArray();

        for( int idx = 0; idx < backing.length; idx++ )
        {
            // if we hit a null we did not find the item
            if( backing[idx] != null &&
                backing[idx].getValue().equals(value ) )
                return true;
        }

        return false;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();

        for (Tuple<K, V> aBacking : getBackingArray()) {
            if (aBacking != null)
                keySet.add(aBacking.getKey());
        }

        return keySet;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();

        for (Tuple<K, V> aBacking : getBackingArray()) {
            if (aBacking != null)
                values.add(aBacking.getValue());
        }

        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = new HashSet<>();

        for (Tuple<K, V> aBacking : getBackingArray()) {
            if (aBacking != null)
                entrySet.add(aBacking);
        }

        return entrySet;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(K key : map.keySet() )
            this.put(key, map.get(key));
    }
}