package elliott.back.maps;

import common.AbstractMapCommonMethods;
import common.Tuple;

import java.util.Arrays;
import java.util.Map;

/***
 * This class is a simple map, with no concurrency protections, that is based on
 * a flat array which is doubled every time it becomes full (on insert).
 *
 * Both inserts and lookups use probing in the array to find the next free slot.
 * Probing wraps around the array size.
 *
 * Limited to around MAX_INT entries because arrays are int-indexed in Java.
 */
public class SimpleCircularFlatMap<K,V> extends AbstractMapCommonMethods<K,V> implements Map<K,V> {

    private Tuple<K,V> [] backing;
    private int currentSize = 0;

    /**
     * Default construct = initial 32 entries
     */
    public SimpleCircularFlatMap() {
        this(32);
    }

    public SimpleCircularFlatMap(int initialSize ) {
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

        int startOffset = startIndexFromObject(key);

        for( int idx = 0; idx < this.backing.length; idx++ )
        {
            int pos = ( idx + startOffset ) % this.backing.length;

            // if we hit a null we did not find the item
            if( this.backing[pos] == null )
                return false;

            // if we hit a matching key, we found it
            if( this.backing[pos].equals(keyTuple))
                return true;
        }

        return false;
    }

    @Override
    public V get(Object key) {
        Tuple keyTuple = new Tuple(key, null);

        int startOffset = startIndexFromObject(key);

        for( int idx = 0; idx < this.backing.length; idx++ )
        {
            int pos = ( idx + startOffset ) % this.backing.length;

            // if we hit a null we did not find the item
            if( this.backing[pos] == null )
                return null;

            // if we hit a matching key, we found it
            if( this.backing[pos].equals(keyTuple))
                return this.backing[pos].getValue();
        }

        // ran out of space
        return null;
    }

    /**
     * The rehash operation will just double the array
     * TODO: dupe
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
    // TODO: the iteration order could be abstracted
    public V put(K key, V value) {
        // ran out of space, we need to resize!
        if(this.currentSize == this.backing.length)
            reHash();

        int startOffset = startIndexFromObject(key);

        // there should be some space, use it
        for( int idx = 0; idx < this.backing.length; idx++ )
        {
            int pos = ( startOffset + idx ) % this.backing.length;

            // if we hit a null there is nothing there
            if( this.backing[pos] == null ) {
                this.backing[pos] = new Tuple(key, value);
                this.currentSize++;
                return null;
            } // the key itself is equal, replace
            else if( ( this.backing[pos].getKey() == null && key == null ) || this.backing[pos].getKey().equals(key) ) {
                V oldValue = this.backing[pos].getValue();
                this.backing[pos] = new Tuple(key, value);
                return oldValue;
            }
        }

        throw new IllegalStateException("Array should have a slot but doesn't");
    }

    @Override
    public V remove(Object key) {
        Tuple keyTuple = new Tuple(key, null);

        int startOffset = startIndexFromObject(key);

        for( int idx = 0; idx < this.backing.length; idx++ )
        {
            int pos = ( startOffset + idx ) % this.backing.length;

            // if we hit a null we did not find the item
            if( this.backing[pos] == null )
                return null;

            // if we hit a matching key, we found it
            if( this.backing[pos].equals(keyTuple)) {
                V value = this.backing[pos].getValue();
                this.backing[pos] = null;
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