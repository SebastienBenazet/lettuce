package com.lambdaworks.redis.cluster;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.cluster.api.StatefulRedisClusterConnection;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.internal.LettuceMaps;

/**
 * @param <CMD> Command command interface type to invoke multi-node operations.
 * @param <K> Key type.
 * @param <V> Value type.
 * @author Mark Paluch
 */
class DynamicAsyncNodeSelection<CMD, K, V> extends DynamicNodeSelection<RedisAsyncCommands<K, V>, CMD, K, V> {

    public DynamicAsyncNodeSelection(StatefulRedisClusterConnection<K, V> globalConnection,
            Predicate<RedisClusterNode> selector, ClusterConnectionProvider.Intent intent) {
        super(globalConnection, selector, intent);
    }

    public Iterator<RedisAsyncCommands<K, V>> iterator() {
        List<RedisClusterNode> list = nodes().stream().collect(Collectors.toList());
        return list.stream().map(node -> getConnection(node).async()).iterator();
    }

    @Override
    public RedisAsyncCommands<K, V> commands(int index) {
        return statefulMap().get(nodes().get(index)).async();
    }

    @Override
    public Map<RedisClusterNode, RedisAsyncCommands<K, V>> asMap() {

        List<RedisClusterNode> list = nodes().stream().collect(Collectors.toList());
        Map<RedisClusterNode, RedisAsyncCommands<K, V>> map = LettuceMaps.newHashMap();

        list.forEach((key) -> map.put(key, getConnection(key).async()));

        return map;
    }

    // This method is never called, the value is supplied by AOP magic.
    @Override
    public CMD commands() {
        return null;
    }
}
