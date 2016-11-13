package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by kubut on 12.11.16.
 */
public class HashArray<K, V> implements Iterable<V>{
    private ArrayList<K> keys;
    private LinkedHashMap<K, V> map;

    public HashArray() {
        this.keys = new ArrayList<K>();
        this.map = new LinkedHashMap<K, V>();
    }

    public V get(int index) {
        K key = this.keys.get(index);

        return this.map.get(key);
    }

    public V get(K key){
        return this.map.get(key);
    }

    public void put(int index, K key, V value){
        if(index < 0) {
            this.put(key, value);
            return;
        }

        this.keys.add(index, key);
        this.map.put(key, value);
    }

    public void put(K key, V value){
        if(!this.keys.contains(key)){
            this.keys.add(key);

        }
        this.map.put(key, value);
    }

    public int size(){
        return this.keys.size();
    }

    public boolean isEmpty() {
        return this.keys.isEmpty();
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            private int currentPosition = -1;

            @Override
            public boolean hasNext() {
                return currentPosition < keys.size() - 1;
            }

            @Override
            public V next() {
                currentPosition++;

                return get(currentPosition);
            }
        };
    }
}