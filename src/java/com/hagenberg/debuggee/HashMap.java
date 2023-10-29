package com.hagenberg.debuggee;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashMap<K, V> implements Iterable<KeyValuePair<K,V>>{

    private static class HashMapEntry<K, V> {
        private KeyValuePair<K, V> data;
        private HashMapEntry<K, V> next = null;

        public HashMapEntry(K key, V value) {
            data = new KeyValuePair<>(key, value);
        }
    }

    private class HashMapIterator implements Iterator<KeyValuePair<K,V>> {
        private HashMapEntry<K,V> previous = null;
        private HashMapEntry<K,V> current = null;
        private HashMapEntry<K,V> next;
        private int index = 0;
        private int currentIndex = 0;

        public HashMapIterator() {
            while(index < map.length - 1 && map[index] == null) {
                index++;
            }
            next = map[index];
        }

        public boolean hasNext() {
            return next != null;
        }

        public KeyValuePair<K,V> next() {
            KeyValuePair<K,V> item;
            if(next == null) {
                throw new NoSuchElementException();
            }
            item = next.data;
            if(next.next != null || index == map.length - 1) { //get next entry in list
                currentIndex = index;
                updateReferences(next.next);
            } else { //get next list
                currentIndex = index;
                do {
                    index++;
                } while(map[index] == null && index < map.length-1);
                updateReferences(map[index]);
            }
            return item;
        }

        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            if(previous == null || previous.next == null) { //current is first element in bucket
                map[currentIndex] = current.next;
            } else {
                //current.next == null -> previous is last element in bucket
                previous.next = current.next;
            }
            size--;
            current = null;
        }

        private void updateReferences(HashMapEntry<K,V> new_next) {
            if(current != null) {
                previous = current;
            }  //previous == null -> no update of secondPrevious
            current = next;
            next = new_next;
        }
    }

    private int hash(K key) {
        int hash = key.hashCode() % (map.length-1);
        //get rid of negative indices
        if (hash < 0) {
            hash *= -1;
        }
        return hash;
    }

    private HashMapEntry<K, V>[] map;
    private int size = 0;
    private float maxLoadFactor = 0.75f;

    /**
     * Erzeugt eine HashMap mit einer initialen Größe von 11 und einem max. Lastfaktor von 0.75.
      */
    public HashMap() {
        map = new HashMapEntry[11];
    }

    /**
     * Erzeugt eine HashMap mit der angegebenen Kapazität und dem angegebenen Lastfaktor.
     */
    public HashMap(int initialCapacity, float maxLoadFactor) {
        map = new HashMapEntry[initialCapacity];
        this.maxLoadFactor = maxLoadFactor;
    }

    /**
     * Gibt den mit key assoziierten Wert zurück. Falls key nicht in dieser Map enthalten ist, wird null retourniert.
     */
    public V get(K key) {
        HashMapEntry<K, V> node = map[hash(key)];
        while(node != null && !node.data.getKey().equals(key)) {
            node = node.next;
        }
        return (node != null) ? node.data.getValue() : null;
    }

    /**
     * Gibt true zurück, falls key in dieser Map enthalten ist.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Überprüft, ob in dieser Map ein Schlüssel existiert, der mit value assoziiert ist.
      */
    public boolean containsValue(V value) {
        boolean found = false;
        int i = 0;

        while(!found && i < map.length) {
            HashMapEntry<K, V> entry = map[i];
            while(!found && entry != null) {
                if (entry.data.getValue().equals(value)) {
                    found = true;
                } else {
                    entry = entry.next;
                }
            }
            i++;
        }
        return found;
    }

    /**
     * Assoziiert mit key ein neues Objekt value. Existiert kein Eintrag unter key, wird ein neuer
     * Eintrag hinzugefügt. Gibt den alten mit key assoziieren Wert zurück. Falls key nicht in dieser
     * Map nicht enthalten ist, wird null retourniert. Organisiert die Hashtabelle neu (rehash),
     * falls durch das Einfügen dieses Elements der maximale Lastfaktor überschritten wird.
     */
    public V put(K key, V value) {
        V old = null;

        HashMapEntry<K, V> new_entry = new HashMapEntry<>(key, value);
        HashMapEntry<K, V> entry = map[hash(key)];

        if(entry == null) { //bucket has no entries
            map[hash(key)] = new_entry;
            size++;
        } else { //look for existing key entry
            HashMapEntry<K, V> prev = null;
            while(entry != null && !entry.data.getKey().equals(key)) {
                prev = entry;
                entry = entry.next;
            }
            if (entry != null) { //key already exists
                old = entry.data.getValue();
                entry.data = new KeyValuePair<>(key, value);
            } else { //append new entry
                prev.next = new_entry;
                size++;
            }
        }

        if(size > map.length * maxLoadFactor) {
            rehash();
        }
        return old;
    }

    /**
     * Checks if map is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Entfernt den Eintrag unter key. Gibt den Wert zurück, der mit key assoziiert war. Falls key
     * nicht in dieser Map enthalten ist, wird null retourniert.
     */
    public V remove(K key) {
        V removed = null;
        HashMapEntry<K, V> entry = map[hash(key)];
        HashMapEntry<K, V> prev = null;
        while(entry != null && !entry.data.getKey().equals(key)) {
            prev = entry;
            entry = entry.next;
        }
        if (entry != null) {
            removed = entry.data.getValue();
            size--;
            if(prev != null) {
                prev.next = entry.next;
            } else {
                map[hash(key)] = entry.next;
            }
        }
        return removed;
    }

    /**
     * Entfernt alle Einträge dieser Map
     */
    public void clear() {
        Arrays.fill(map, null);
        size = 0;
    }

    /**
     * Gibt die Anzahl der Einträge zurück.
     */
    public int size() {
        return size;
    }

    /**
     * Gibt alle in dieser Map gespeicherten Schlüssel als Feld zurück
     */
    public K[] keys() {
        if(size == 0) {
            return null;
        }
        Iterator<KeyValuePair<K, V>> it = this.iterator();
        K[] keys = (K[]) Array.newInstance(it.next().getKey().getClass(), size);
        int k = 0;
        for (HashMapEntry<K, V> kvHashMapEntry : map) {
            HashMapEntry<K, V> entry = kvHashMapEntry;
            while (entry != null) {
                keys[k++] = entry.data.getKey();
                entry = entry.next;
            }
        }
        return keys;
    }

    /**
     * Gibt alle in dieser Map gespeicherten Werte als Feld zurück.
     */
    public V[] values(){
        if(size == 0) {
            return null;
        }
        Iterator<KeyValuePair<K, V>> it = this.iterator();
        V[] values = (V[]) Array.newInstance(it.next().getValue().getClass(), size);
        int v = 0;
        for (HashMapEntry<K, V> kvHashMapEntry : map) {
            HashMapEntry<K, V> entry = kvHashMapEntry;
            while (entry != null) {
                values[v++] = entry.data.getValue();
                entry = entry.next;
            }
        }
        return values;
    }

    /**
     * Liefert einen Iterator auf der Key/Value-Paare.
     */
    public Iterator<KeyValuePair<K, V>> iterator() {
        return new HashMapIterator();
    }

    /**
     * Gibt den maximalen Lastfaktor zurück.
     */
    public float getMaxLoadFactor() {
        return maxLoadFactor;
    }

    /**
     * Gibt den aktuellen Lastfaktor zurück.
     */
    public float getLoadFactor() {
        return (float) size / map.length;
    }

    /**
     * Vergrößert die Hashtabelle und reorganisiert die Einträge
     */
    public void rehash() {
        HashMapEntry<K, V>[] new_map = new HashMapEntry[map.length * 2];
        //transfer all entries to new map
        for(KeyValuePair<K, V> pair : this) {
            int entryKeyHash = hash(pair.getKey());
            HashMapEntry<K, V> entry = new HashMapEntry<>(pair.getKey(), pair.getValue());
            //all entries are unique -> simple prepend
            entry.next = new_map[entryKeyHash];
            new_map[entryKeyHash] = entry;
        }
        map = new_map;
    }

}
