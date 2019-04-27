package elliott.back.maps;

import common.Tuple;

import java.util.*;

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

    /**
     * Lookup the start index in our array from some object
     */
    private int startIndexFromObject(Object key )
    {
        return key.hashCode() % this.backing.length;
    }

    @Override
    public boolean containsKey(Object key) {
        Tuple keyTuple = new Tuple(key, null);

        for( int idx = startIndexFromObject(key); idx < this.backing.length; idx++ )
        {
            // if we hit a null we did not find the item
            if( this.backing[idx] == null )
                return false;

            // if we hit a matching key, we found it
            if( this.backing[idx].equals(keyTuple))
                return true;
        }

        return false;
    }

    /**
     * This will be a horrible linear scan, we have to check every single item
     */
    @Override
    public boolean containsValue(Object value) {
        for( int idx = 0; idx < this.backing.length; idx++ )
        {
            // if we hit a null we did not find the item
            if( this.backing[idx] != null &&
                this.backing[idx].getValue().equals(value ) )
                return true;
        }

        return false;
    }

    @Override
    public V get(Object key) {
        Tuple keyTuple = new Tuple(key, null);

        for( int idx = startIndexFromObject(key); idx < this.backing.length; idx++ )
        {
            // if we hit a null we did not find the item
            if( this.backing[idx] == null )
                return null;

            // if we hit a matching key, we found it
            if( this.backing[idx].equals(keyTuple))
                return this.backing[idx].getValue();
        }

        // ran out of space
        return null;
    }

    /**
     * The rehash operation will just double the array
     */
    private void reHash(){
        Tuple<K,V> [] oldBacking = this.backing;
        this.backing = new Tuple[this.backing.length * 2 ];
        for(Tuple<K,V> oldEntry : oldBacking)
            put(oldEntry.getKey(), oldEntry.getValue());
    }

    @Override
    public V put(K key, V value) {
        // ran out of space, we need to resize!
        if(this.backing.length == this.currentSize)
            reHash();

        // there should be some space, use it
        for( int idx = startIndexFromObject(key); idx < this.backing.length; idx++ )
        {
            // if we hit a null we found a good spot to stick it
            if( this.backing[idx] == null ) {
                this.backing[idx] = new Tuple(key, value);
                this.currentSize++;
                return value;
            }
        }

        throw new IllegalStateException("Could not find a place to this element, event though there should be one");
    }

    @Override
    public V remove(Object key) {
        Tuple keyTuple = new Tuple(key, null);

        for( int idx = startIndexFromObject(key); idx < this.backing.length; idx++ )
        {
            // if we hit a null we did not find the item
            if( this.backing[idx] == null )
                return null;

            // if we hit a matching key, we found it
            if( this.backing[idx].equals(keyTuple)) {
                V value = this.backing[idx].getValue();
                this.backing[idx] = null;
                this.currentSize--;
                return value;
            }
        }

        // ran out of space
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
        this.currentSize = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();

        for (Tuple<K, V> aBacking : backing) {
            if (aBacking != null)
                keySet.add(aBacking.getKey());
        }

        return keySet;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();

        for (Tuple<K, V> aBacking : backing) {
            if (aBacking != null)
                values.add(aBacking.getValue());
        }

        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = new HashSet<>();

        for (Tuple<K, V> aBacking : backing) {
            if (aBacking != null)
                entrySet.add(aBacking);
        }

        return entrySet;
    }
}