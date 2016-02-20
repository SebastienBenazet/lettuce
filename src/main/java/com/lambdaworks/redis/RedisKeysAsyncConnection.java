package com.lambdaworks.redis;

import java.util.Date;
import java.util.List;

import com.lambdaworks.redis.output.KeyStreamingChannel;
import com.lambdaworks.redis.output.ValueStreamingChannel;

/**
 * Asynchronous executed commands for Keys (Key manipulation/querying).
 * 
 * @param <K> Key type.
 * @param <V> Value type.
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 3.0
 * @deprecated Use {@literal RedisKeyAsyncCommands}
 */
@Deprecated
public interface RedisKeysAsyncConnection<K, V> {

    /**
     * Delete one or more keys.
     *
     * @param keys the keys
     * @return RedisFuture&lt;Long&gt; integer-reply The number of keys that were removed.
     */
    RedisFuture<Long> del(K... keys);

    /**
     * Unlink one or more keys (non blocking DEL).
     *
     * @param keys the keys
     * @return RedisFuture&lt;Long&gt; integer-reply The number of keys that were removed.
     */
    RedisFuture<Long> unlink(K... keys);

    /**
     * Return a serialized version of the value stored at the specified key.
     *
     * @param key the key
     * @return RedisFuture&lt;byte[]&gt;bulk-string-reply the serialized value.
     */
    RedisFuture<byte[]> dump(K key);

    /**
     * Determine if a key exists.
     *
     * @param key the key
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if the key exists. {@literal false} if the key does not exist.
     * @deprecated Use {@link #exists(Object[])} instead
     */
    @Deprecated
    RedisFuture<Boolean> exists(K key);

    /**
     * Determine how many keys exist.
     *
     * @param keys the keys
     * @return Long integer-reply specifically: Number of existing keys
     */
    RedisFuture<Long> exists(K... keys);

    /**
     * Set a key's time to live in seconds.
     *
     * @param key the key
     * @param seconds the seconds type: long
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if the timeout was set. {@literal false} if {@code key} does not exist or the timeout could not
     *         be set.
     */
    RedisFuture<Boolean> expire(K key, long seconds);

    /**
     * Set the expiration for a key as a UNIX timestamp.
     *
     * @param key the key
     * @param timestamp the timestamp type: posix time
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if the timeout was set. {@literal false} if {@code key} does not exist or the timeout could not
     *         be set (see: {@code EXPIRE}).
     */
    RedisFuture<Boolean> expireat(K key, Date timestamp);

    /**
     * Set the expiration for a key as a UNIX timestamp.
     *
     * @param key the key
     * @param timestamp the timestamp type: posix time
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if the timeout was set. {@literal false} if {@code key} does not exist or the timeout could not
     *         be set (see: {@code EXPIRE}).
     */
    RedisFuture<Boolean> expireat(K key, long timestamp);

    /**
     * Find all keys matching the given pattern.
     *
     * @param pattern the pattern type: patternkey (pattern)
     * @return RedisFuture&lt;List&lt;K&gt;&gt; array-reply list of keys matching {@code pattern}.
     */
    RedisFuture<List<K>> keys(K pattern);

    /**
     * Find all keys matching the given pattern.
     *
     * @param channel the channel
     * @param pattern the pattern
     *
     * @return RedisFuture&lt;Long&gt; array-reply list of keys matching {@code pattern}.
     */
    RedisFuture<Long> keys(KeyStreamingChannel<K> channel, K pattern);

    /**
     * Atomically transfer a key from a Redis instance to another one.
     *
     * @param host the host
     * @param port the port
     * @param key the key
     * @param db the database
     * @param timeout the timeout in milliseconds
     *
     * @return RedisFuture&lt;String&gt; simple-string-reply The command returns OK on success.
     */
    RedisFuture<String> migrate(String host, int port, K key, int db, long timeout);

    /**
     * Atomically transfer one or more keys from a Redis instance to another one.
     *
     * @param host the host
     * @param port the port
     * @param db the database
     * @param timeout the timeout in milliseconds
     * @param migrateArgs migrate args that allow to configure further options
     * @return String simple-string-reply The command returns OK on success.
     */
    RedisFuture<String> migrate(String host, int port, int db, long timeout, MigrateArgs<K> migrateArgs);

    /**
     * Move a key to another database.
     *
     * @param key the key
     * @param db the db type: long
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     */
    RedisFuture<Boolean> move(K key, int db);

    /**
     * returns the kind of internal representation used in order to store the value associated with a key.
     *
     * @param key the key
     * @return RedisFuture&lt;String&gt;
     */
    RedisFuture<String> objectEncoding(K key);

    /**
     * returns the number of seconds since the object stored at the specified key is idle (not requested by read or write
     * operations).
     *
     * @param key the key
     * @return RedisFuture&lt;Long&gt; number of seconds since the object stored at the specified key is idle.
     */
    RedisFuture<Long> objectIdletime(K key);

    /**
     * returns the number of references of the value associated with the specified key.
     *
     * @param key the key
     * @return RedisFuture&lt;Long&gt;
     */
    RedisFuture<Long> objectRefcount(K key);

    /**
     * Remove the expiration from a key.
     *
     * @param key the key
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if the timeout was removed. {@literal false} if {@code key} does not exist or does not have an
     *         associated timeout.
     */
    RedisFuture<Boolean> persist(K key);

    /**
     * Set a key's time to live in milliseconds.
     *
     * @param key the key
     * @param milliseconds the milliseconds type: long
     * @return integer-reply, specifically:
     *
     *         {@literal true} if the timeout was set. {@literal false} if {@code key} does not exist or the timeout could not
     *         be set.
     */
    RedisFuture<Boolean> pexpire(K key, long milliseconds);

    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds.
     *
     * @param key the key
     * @param timestamp the milliseconds-timestamp type: posix time
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if the timeout was set. {@literal false} if {@code key} does not exist or the timeout could not
     *         be set (see: {@code EXPIRE}).
     */
    RedisFuture<Boolean> pexpireat(K key, Date timestamp);

    /**
     * Set the expiration for a key as a UNIX timestamp specified in milliseconds.
     *
     * @param key the key
     * @param timestamp the milliseconds-timestamp type: posix time
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if the timeout was set. {@literal false} if {@code key} does not exist or the timeout could not
     *         be set (see: {@code EXPIRE}).
     */
    RedisFuture<Boolean> pexpireat(K key, long timestamp);

    /**
     * Get the time to live for a key in milliseconds.
     *
     * @param key the key
     * @return RedisFuture&lt;Long&gt; integer-reply TTL in milliseconds, or a negative value in order to signal an error (see
     *         the description above).
     */
    RedisFuture<Long> pttl(K key);

    /**
     * Return a random key from the keyspace.
     *
     * @return RedisFuture&lt;V&gt; bulk-string-reply the random key, or {@literal null} when the database is empty.
     */
    RedisFuture<V> randomkey();

    /**
     * Rename a key.
     *
     * @param key the key
     * @param newKey the newkey type: key
     * @return RedisFuture&lt;String&gt; simple-string-reply
     */
    RedisFuture<String> rename(K key, K newKey);

    /**
     * Rename a key, only if the new key does not exist.
     *
     * @param key the key
     * @param newKey the newkey type: key
     * @return RedisFuture&lt;Boolean&gt; integer-reply specifically:
     *
     *         {@literal true} if {@code key} was renamed to {@code newkey}. {@literal false} if {@code newkey} already exists.
     */
    RedisFuture<Boolean> renamenx(K key, K newKey);

    /**
     * Create a key using the provided serialized value, previously obtained using DUMP.
     *
     * @param key the key
     * @param ttl the ttl type: long
     * @param value the serialized-value type: string
     * @return RedisFuture&lt;String&gt; simple-string-reply The command returns OK on success.
     */
    RedisFuture<String> restore(K key, long ttl, byte[] value);

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param key the key
     * @return RedisFuture&lt;List&lt;V&gt;&gt; array-reply list of sorted elements.
     */
    RedisFuture<List<V>> sort(K key);

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param channel streaming channel that receives a call for every value
     * @param key the key
     * @return RedisFuture&lt;Long&gt; number of values.
     */
    RedisFuture<Long> sort(ValueStreamingChannel<V> channel, K key);

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param key the key
     * @param sortArgs sort arguments
     * @return RedisFuture&lt;List&lt;V&gt;&gt; array-reply list of sorted elements.
     */
    RedisFuture<List<V>> sort(K key, SortArgs sortArgs);

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param channel streaming channel that receives a call for every value
     * @param key the key
     * @param sortArgs sort arguments
     * @return RedisFuture&lt;Long&gt; number of values.
     */
    RedisFuture<Long> sort(ValueStreamingChannel<V> channel, K key, SortArgs sortArgs);

    /**
     * Sort the elements in a list, set or sorted set.
     *
     * @param key the key
     * @param sortArgs sort arguments
     * @param destination the destination key to store sort results
     * @return RedisFuture&lt;Long&gt; number of values.
     */
    RedisFuture<Long> sortStore(K key, SortArgs sortArgs, K destination);

    /**
     * Get the time to live for a key.
     *
     * @param key the key
     * @return RedisFuture&lt;Long&gt; integer-reply TTL in seconds, or a negative value in order to signal an error (see the
     *         description above).
     */
    RedisFuture<Long> ttl(K key);

    /**
     * Determine the type stored at key.
     *
     * @param key the key
     * @return RedisFuture&lt;String&gt; simple-string-reply type of {@code key}, or {@code none} when {@code key} does not
     *         exist.
     */
    RedisFuture<String> type(K key);

    /**
     * Incrementally iterate the keys space.
     *
     * @return RedisFuture&lt;KeyScanCursor&lt;K&gt;&gt; scan cursor.
     */
    RedisFuture<KeyScanCursor<K>> scan();

    /**
     * Incrementally iterate the keys space.
     *
     * @param scanArgs scan arguments
     * @return RedisFuture&lt;KeyScanCursor&lt;K&gt;&gt; scan cursor.
     */
    RedisFuture<KeyScanCursor<K>> scan(ScanArgs scanArgs);

    /**
     * Incrementally iterate the keys space.
     *
     * @param scanCursor cursor to resume from a previous scan
     * @param scanArgs scan arguments
     * @return RedisFuture&lt;KeyScanCursor&lt;K&gt;&gt; scan cursor.
     */
    RedisFuture<KeyScanCursor<K>> scan(ScanCursor scanCursor, ScanArgs scanArgs);

    /**
     * Incrementally iterate the keys space.
     *
     * @param scanCursor cursor to resume from a previous scan
     * @return RedisFuture&lt;KeyScanCursor&lt;K&gt;&gt; scan cursor.
     */
    RedisFuture<KeyScanCursor<K>> scan(ScanCursor scanCursor);

    /**
     * Incrementally iterate the keys space.
     *
     * @param channel streaming channel that receives a call for every key
     * @return RedisFuture&lt;StreamScanCursor&gt; scan cursor.
     */
    RedisFuture<StreamScanCursor> scan(KeyStreamingChannel<K> channel);

    /**
     * Incrementally iterate the keys space.
     *
     * @param channel streaming channel that receives a call for every key
     * @param scanArgs scan arguments
     * @return RedisFuture&lt;StreamScanCursor&gt; scan cursor.
     */
    RedisFuture<StreamScanCursor> scan(KeyStreamingChannel<K> channel, ScanArgs scanArgs);

    /**
     * Incrementally iterate the keys space.
     *
     * @param channel streaming channel that receives a call for every key
     * @param scanCursor cursor to resume from a previous scan
     * @param scanArgs scan arguments
     * @return RedisFuture&lt;StreamScanCursor&gt; scan cursor.
     */
    RedisFuture<StreamScanCursor> scan(KeyStreamingChannel<K> channel, ScanCursor scanCursor, ScanArgs scanArgs);

    /**
     * Incrementally iterate the keys space.
     *
     * @param channel streaming channel that receives a call for every key
     * @param scanCursor cursor to resume from a previous scan
     * @return RedisFuture&lt;StreamScanCursor&gt; scan cursor.
     */
    RedisFuture<StreamScanCursor> scan(KeyStreamingChannel<K> channel, ScanCursor scanCursor);

}
