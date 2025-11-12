package no.helseid.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * An in-memory implementation of an expiring cache
 * @param <T> the class of the cached values
 */
public class InMemoryExpiringCache<T> implements ExpiringCache<T> {
  private final ConcurrentHashMap<String, ExpiringValue<T>> cache = new ConcurrentHashMap<>();


  /**
   * Create a new instance of InMemoryExpiringCache
   */
  public InMemoryExpiringCache() {
  }

  /**
   * @param key the key a value is cached on
   * @return the cached value if present, otherwise null
   */
  @Override
  public T get(String key) {
    var expiringValue = cache.get(key);

    if (expiringValue == null ||expiringValue.expireAtEpochMilliseconds < System.currentTimeMillis()) {
      remove(key);
      return null;
    }

    return expiringValue.value;
  }

  /**
   * @param key the key the value should be cached on
   * @param value the value to be cached
   * @param expireAtEpochMilliseconds when the value expires
   */
  @Override
  public void put(String key, T value, long expireAtEpochMilliseconds) {
    cache.put(key, new ExpiringValue<>(value, expireAtEpochMilliseconds));
  }

  /**
   * @param key the key a value is cached on
   */
  @Override
  public void remove(String key) {
    cache.remove(key);
  }

  /**
   * Internal class representing the expiration of a value
   * @param value
   * @param expireAtEpochMilliseconds
   * @param <T>
   */
  private record ExpiringValue<T>(T value, long expireAtEpochMilliseconds) {
  }
}
