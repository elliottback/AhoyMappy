package elliott.back.maps;

import elliott.back.common.AbstractMapCommonMethods;
import elliott.back.common.Tuple;

import java.util.Arrays;
import java.util.Map;

/***
 * This class is a simple map, with no concurrency protections, that is based on
 * a flat array which is doubled every time it becomes full (on insert).
 *
 * Both inserts and lookups use probing in the array to find the next free slot.
 *
 * Limited to around MAX_INT entries because arrays are int-indexed in Java.
 */
public class SimpleFlatMap <K,V> extends AbstractMapCommonMethods<K,V> implements Map<K,V> {

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
    public Tuple<K,V> [] getBackingArray(){
        return backing;
    }

    /**
     * Lookup the start index in our array from some object
     */
    private int startIndexFromObject(Object key )
    {
        return key == null ? 0 : key.hashCode() % this.backing.length;
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
        this.currentSize = 0;

        for(Tuple<K,V> oldEntry : oldBacking)
            if(oldEntry != null)
                put(oldEntry.getKey(), oldEntry.getValue());
    }

    @Override
    public V put(K key, V value) {
        // there should be some space, use it
        for( int idx = startIndexFromObject(key); idx < this.backing.length; idx++ )
        {
            // if we hit a null there is nothing there
            if( this.backing[idx] == null ) {
                this.backing[idx] = new Tuple(key, value);
                this.currentSize++;
                return null;
            } // the key itself is equal, replace
            else if( ( this.backing[idx].getKey() == null && key == null ) || this.backing[idx].getKey().equals(key) ) {
                V oldValue = this.backing[idx].getValue();
                this.backing[idx] = new Tuple(key, value);
                return oldValue;
            }
        }

        // ran out of space, we need to resize!
        // this resize technique means an adversary could simply pick keys that fall to the end our
        // array, causing us to double it each time.  Maybe less-simple flat map can avoid that
        reHash();
        return this.put(key, value);
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
    public void clear() {
        Arrays.fill(this.backing, null);
        this.currentSize = 0;
    }
}