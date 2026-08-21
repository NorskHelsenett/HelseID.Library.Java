package no.helseid.cache;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Caching interface used by HelseID internals
 * May be implemended to provide distributed caching or interactions with a custom cache
 *
 * @param <T> the class of the cached values
 */
@NullMarked
public interface ExpiringCache<T> {
  /**
   * Get the cached value on a key, an expired value is interpreted as missing
   *
   * @param key the key a value is cached on
   * @return cached value, null if missing or expired
   */
  @Nullable T get(String key);


  /**
   * Add a value to the cache
   * @param key the key a value is cached on
   * @param value the value that will be cached
   * @param expireAtEpochMillisecond the timestamp where the value is expiring, represented in milliseconds since epoch
   */
  void put(String key, T value, long expireAtEpochMillisecond);

  /**
   * Remove a cached value on the given key
   * @param key the key a value is cached on
   */
  void remove(String key);
}
