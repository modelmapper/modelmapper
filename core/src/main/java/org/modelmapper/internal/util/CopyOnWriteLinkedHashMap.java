package org.modelmapper.internal.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A thread-safe {@link LinkedHashMap} which creates a copy of the underlying map on write
 * operations.
 * 
 * <p>
 * Read operations, including iteration, do not interfere with writes since the underlying map is
 * never modified.
 * 
 * @author Jonathan Halterman
 */
public class CopyOnWriteLinkedHashMap<K, V> implements Map<K, V> {
  private volatile Map<K, V> map;

  public CopyOnWriteLinkedHashMap() {
    map = new LinkedHashMap<K, V>();
  }

  public CopyOnWriteLinkedHashMap(Map<K, V> data) {
    map = new LinkedHashMap<K, V>(data);
  }

  public synchronized void clear() {
    map = new LinkedHashMap<K, V>();
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  public Set<Entry<K, V>> entrySet() {
    return map.entrySet();
  }

  public V get(Object key) {
    return map.get(key);
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public Set<K> keySet() {
    return map.keySet();
  }

  public synchronized V put(K key, V value) {
    Map<K, V> newMap = new LinkedHashMap<K, V>(map);
    V previous = newMap.put(key, value);
    map = newMap;
    return previous;
  }

  public synchronized void putAll(Map<? extends K, ? extends V> newData) {
    Map<K, V> newMap = new LinkedHashMap<K, V>(map);
    newMap.putAll(newData);
    map = newMap;
  }

  public synchronized V remove(Object key) {
    Map<K, V> newMap = new LinkedHashMap<K, V>(map);
    V previous = newMap.remove(key);
    map = newMap;
    return previous;
  }

  public int size() {
    return map.size();
  }

  public Collection<V> values() {
    return map.values();
  }
}