package com.lambdaworks.redis.cluster;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.lambdaworks.redis.cluster.api.async.AsyncExecutions;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.internal.LettuceMaps;

/**
 * @author Mark Paluch
 */
class AsyncExecutionsImpl<T> implements AsyncExecutions<T> {

    private Map<RedisClusterNode, CompletionStage<T>> executions;

    public AsyncExecutionsImpl(Map<RedisClusterNode, CompletionStage<T>> executions) {
        this.executions = LettuceMaps.unmodifiableMap(executions);
    }

    @Override
    public Map<RedisClusterNode, CompletionStage<T>> asMap() {
        return executions;
    }

    @Override
    public Collection<RedisClusterNode> nodes() {
        return executions.keySet();
    }

    @Override
    public CompletionStage<T> get(RedisClusterNode redisClusterNode) {
        return executions.get(redisClusterNode);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public CompletableFuture<T>[] futures() {
        return executions.values().toArray(new CompletableFuture[executions.size()]);
    }
}
