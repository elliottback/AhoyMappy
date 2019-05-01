package elliott.back.maps;

import elliott.back.common.Tuple;

import java.util.*;

/***
 * This class sticks buckets (linked lists) on the leaf of the previous circular array implementation, and resizes
 * when buckets start getting beefy.
 */
public class SimpleCircularListBucketFlatMap<K,V> implements Map<K,V> {

    private List<Tuple<K,V>>[] backing;
    private double avgBackingBucketSize;
    private int currentSize = 0;
    private double reSizeWhenAverageIs = 64;

    /**
     * Default construct = initial 32 entries
     */
    public SimpleCircularListBucketFlatMap() {
        this(32);
    }

    public SimpleCircularListBucketFlatMap(int initialSize ) {
        backing = new List [initialSize];
    }

    private void incrementBucketAverage() {
        avgBackingBucketSize = currentSize == 0 ? 0 : (avgBackingBucketSize * (currentSize - 1.0 ) + 1.0) / ((double) this.backing.length);
    }

    private void decrementBucketAverage() {
        avgBackingBucketSize = currentSize == 0 ? 0 : (avgBackingBucketSize * (currentSize - 1.0 ) - 1.0) / ((double) this.backing.length );
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
     * Lookup the bucket index in our array from some object
     */
    private int bucketIndexFromObject(Object key )
    {
        return key == null ? 0 : key.hashCode() % this.backing.length;
    }

    @Override
    public boolean containsKey(Object key) {
        Tuple keyTuple = new Tuple(key, null);

        int bucketIdx = bucketIndexFromObject(key);

        List<Tuple<K,V>> bucket = this.backing[bucketIdx];

        return bucket != null && bucket.indexOf( keyTuple ) != -1;
    }

    @Override
    public boolean containsValue(Object value) {
        for(List<Tuple<K,V>> list : this.backing)
            if( list != null )
                for(Tuple<K,V> entry : list)
                    if( entry != null ) // think I might not need this null check
                        if( entry.getValue() == null && value == null )
                            return true;
                        else if( entry.getValue() != null && entry.getValue().equals(value))
                            return true;

        return false;
    }

    @Override
    public V get(Object key) {
        Tuple keyTuple = new Tuple(key, null);

        int bucketIdx = bucketIndexFromObject(key);

        List<Tuple<K,V>> bucket = this.backing[bucketIdx];

        if( bucket != null )
        {
            int foundIdx = bucket.indexOf( keyTuple );
            if( foundIdx != -1 )
                return bucket.get(foundIdx).getValue();
        }

        return null;
    }

    /**
     * Rehashing will double the size of the backing array buckets
     */
    private void reHash(){
        List<Tuple<K,V>> [] oldBacking = this.backing;

        this.backing = new List [this.backing.length * 2 ];
        this.currentSize = 0;
        this.avgBackingBucketSize = 0.0;

        for(List<Tuple<K,V>> oldList : oldBacking)
            if(oldList != null)
                for(Tuple<K,V> oldEntry : oldList)
                    if(oldEntry != null)
                        put(oldEntry.getKey(), oldEntry.getValue());
    }

    @Override
    // TODO: the iteration order could be abstracted
    public V put(K key, V value) {
        // ran out of space, we need to resize!
        if(this.avgBackingBucketSize >= reSizeWhenAverageIs )
            reHash();

        int bucketIdx = bucketIndexFromObject(key);
        List<Tuple<K,V>> bucket = this.backing[bucketIdx];

        if( bucket == null ) {
            bucket = new LinkedList<Tuple<K,V>>();
            this.backing[bucketIdx] = bucket;
        }

        Tuple<K,V> newEntry = new Tuple(key, value);

        for(Tuple<K,V> existingEntry : bucket)
            if( ( existingEntry.getKey() == null && key == null ) ||
                ( existingEntry.getKey() != null && existingEntry.getKey().equals(key) ) )
            {
                bucket.remove(existingEntry);
                bucket.add(newEntry);
                return existingEntry.getValue();
            }

        // we fell through the loop
        bucket.add(newEntry);

        // keep an accounting
        this.currentSize += 1;
        this.incrementBucketAverage();

        return null;
    }

    @Override
    public V remove(Object key) {
        int bucketOffset = bucketIndexFromObject(key);

        if( this.backing[bucketOffset] == null )
            return null;

        List<Tuple<K,V>> bucket = this.backing[bucketOffset];

        // search the bucket
        for(Tuple<K,V> existingEntry : bucket )
            if( ( existingEntry.getKey() == null && key == null ) ||
                ( existingEntry.getKey() != null && existingEntry.getKey().equals(key) ) )
            {
                bucket.remove(existingEntry);
                this.currentSize -= 1;
                this.decrementBucketAverage();
                return existingEntry.getValue();
            }

        // ran out of space
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        Arrays.fill(this.backing, null);
        this.currentSize = 0;
        this.avgBackingBucketSize = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();

        for(List<Tuple<K,V>> list : this.backing)
            if(list != null)
                for(Tuple<K,V> entry : list)
                    if( entry != null )
                        keys.add(entry.getKey());

        return keys;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new HashSet<>();

        for(List<Tuple<K,V>> list : this.backing)
            if(list != null)
                for(Tuple<K,V> entry : list)
                    if( entry != null )
                        values.add(entry.getValue());

        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K,V>> entries = new HashSet<>();

        for(List<Tuple<K,V>> list : this.backing)
            if( list != null )
                for(Tuple<K,V> entry : list)
                    if( entry != null )
                        entries.add(entry);

        return entries;
    }

    @Override
    public String toString() {
        return "SimpleCircularListBucketFlatMap{" +
                "avgBackingBucketSize=" + avgBackingBucketSize +
                ", currentSize=" + currentSize +
                ", reSizeWhenAverageIs=" + reSizeWhenAverageIs +
                '}';
    }
}