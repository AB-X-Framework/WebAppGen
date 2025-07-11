package org.abx.webappgen.persistence.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceCache<V> {
    public static final String CachedResource = "::resources:";

    private static class CacheEntry<V> {
        int timestamp;
        V value;

        CacheEntry(V value, int timestamp) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }


    private final Map<Long, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final int maxElements;
    private int ts;

    public ResourceCache(int maxElements) {
        this.maxElements = maxElements;
    }

    /**
     * Synchronized add
     */
    public synchronized void add(long id, V value) {
        remove(id);
        cache.put(id, new CacheEntry<>(value, ++ts));
        if (cache.size() > maxElements) {
            trimCache();
        }
    }

    public synchronized void remove(long id) {
        CacheEntry<V> entry = cache.remove(id);
        if (entry != null) {
            dispose(entry.value);
        }
    }

    /**
     * Synchronized get
     */
    public synchronized V get(long key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            entry.timestamp = ++ts;
            return entry.value;
        }
        return null;
    }

    /**
     * Removes half of the entries with smallest timestamp (oldest)
     */
    private void trimCache() {
        // Create a list of entries and sort them by timestamp ascending
        List<Map.Entry<Long, CacheEntry<V>>> entries = new ArrayList<>(cache.entrySet());
        entries.sort(Comparator.comparingLong(e -> e.getValue().timestamp));

        int half = Math.max(entries.size() / 2, 1); // At least remove one if over capacity
        for (int i = 0; i < half; i++) {
            dispose(cache.remove(entries.get(i).getKey()).value);
        }
    }

    public void dispose(V value) {

    }
}
