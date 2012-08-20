package org.modelmapper.internal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple ArrayList MultiMap implementation.
 * 
 * @author Jonathan Halterman
 * @param <K> key type
 * @param <V> value type
 */
public class MultiMap<K, V> {
  private final Map<K, List<V>> map = new HashMap<K, List<V>>();

  public static <K, V> MultiMap<K, V> create() {
    return new MultiMap<K, V>();
  }

  public void put(K key, V value) {
    List<V> values = map.get(key);
    if (values == null) {
      values = new ArrayList<V>();
      map.put(key, values);
    }
    values.add(value);
  }

  public List<V> get(K key) {
    return map.get(key);
  }

  public boolean containsKey(K key) {
    return map.containsKey(key);
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public Set<K> keySet() {
    return map.keySet();
  }
}
