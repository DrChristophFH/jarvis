package com.hagenberg.jarvis.debuggee;

public class KeyValuePair<K, V> {
    private K key;
    private V value;
    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        // If the object is compared with itself then return true
        if (other == this) {
            return true;
        }
        if (!(other instanceof KeyValuePair<?, ?>)) {
            return false;
        }
        // typecast other
        KeyValuePair<K, V> pair = (KeyValuePair<K, V>) other;

        return pair.getValue() == this.value && pair.getKey() == this.key;
    }
}