package com.lambdaworks.redis.cluster.models.slots;

import java.util.*;

import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.models.partitions.RedisClusterNode;
import com.lambdaworks.redis.internal.LettuceAssert;
import com.lambdaworks.redis.internal.LettuceLists;
import com.lambdaworks.redis.internal.LettuceMaps;
import com.lambdaworks.redis.internal.LettuceSets;

/**
 * Parser for redis <a href="http://redis.io/commands/cluster-slots">CLUSTER SLOTS</a> command output.
 * 
 * @author Mark Paluch
 * @since 3.0
 */
public class ClusterSlotsParser {

    /**
     * Utility constructor.
     */
    private ClusterSlotsParser() {

    }

    /**
     * Parse the output of the redis CLUSTER SLOTS command and convert it to a list of
     * {@link com.lambdaworks.redis.cluster.models.slots.ClusterSlotRange}
     * 
     * @param clusterSlotsOutput output of CLUSTER SLOTS command
     * @return List&gt;ClusterSlotRange&gt;
     */
    public static List<ClusterSlotRange> parse(List<?> clusterSlotsOutput) {
        List<ClusterSlotRange> result = LettuceLists.newList();
        Map<String, RedisClusterNode> nodeCache = LettuceMaps.newHashMap();

        for (Object o : clusterSlotsOutput) {

            if (!(o instanceof List)) {
                continue;
            }

            List<?> range = (List<?>) o;
            if (range.size() < 2) {
                continue;
            }

            ClusterSlotRange clusterSlotRange = parseRange(range, nodeCache);
            result.add(clusterSlotRange);
        }

        Collections.sort(result, new Comparator<ClusterSlotRange>() {
            @Override
            public int compare(ClusterSlotRange o1, ClusterSlotRange o2) {
                return o1.getFrom() - o2.getFrom();
            }
        });

        return Collections.unmodifiableList(result);
    }

    private static ClusterSlotRange parseRange(List<?> range, Map<String, RedisClusterNode> nodeCache) {
        Iterator<?> iterator = range.iterator();

        int from = Math.toIntExact(getLongFromIterator(iterator, 0));
        int to = Math.toIntExact(getLongFromIterator(iterator, 0));
        RedisClusterNode master = null;

        List<RedisClusterNode> slaves = LettuceLists.newList();
        if (iterator.hasNext()) {
            master = getRedisClusterNode(iterator, nodeCache);
            if(master != null) {
                master.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.MASTER));
                Set<Integer> slots = LettuceSets.newTreeSet(master.getSlots());
                slots.addAll(createSlots(from, to));
                master.setSlots(LettuceLists.newList(slots));
            }
        }

        while (iterator.hasNext()) {
            RedisClusterNode slave = getRedisClusterNode(iterator, nodeCache);
            if (slave != null) {
                slave.setSlaveOf(master.getNodeId());
                slave.setFlags(Collections.singleton(RedisClusterNode.NodeFlag.SLAVE));
                slaves.add(slave);
            }
        }

        return new ClusterSlotRange(from, to, master, Collections.unmodifiableList(slaves));
    }

    private static List<Integer> createSlots(int from, int to) {
        List<Integer> slots = LettuceLists.newList();
        for (int i = from; i < to + 1; i++) {
            slots.add(i);
        }
        return slots;
    }

    private static RedisClusterNode getRedisClusterNode(Iterator<?> iterator, Map<String, RedisClusterNode> nodeCache) {
        Object element = iterator.next();
        RedisClusterNode redisClusterNode = null;
        if (element instanceof List) {
            List<?> hostAndPortList = (List<?>) element;
            if (hostAndPortList.size() < 2) {
                return null;
            }

            Iterator<?> hostAndPortIterator = hostAndPortList.iterator();
            String host = (String) hostAndPortIterator.next();
            int port = Math.toIntExact(getLongFromIterator(hostAndPortIterator, 0));
            String nodeId;


            if (hostAndPortIterator.hasNext()) {
                nodeId = (String) hostAndPortIterator.next();

                redisClusterNode = nodeCache.get(nodeId);
                if(redisClusterNode == null) {
                    redisClusterNode = createNode(host, port);
                    nodeCache.put(nodeId, redisClusterNode);
                    redisClusterNode.setNodeId(nodeId);
                }
            }
            else {
                String key = host + ":" + port;
                redisClusterNode = nodeCache.get(key);
                if(redisClusterNode == null) {
                    redisClusterNode = createNode(host, port);
                    nodeCache.put(key, redisClusterNode);
                }
            }
        }
        return redisClusterNode;
    }

    private static RedisClusterNode createNode(String host, int port) {
        RedisClusterNode redisClusterNode = new RedisClusterNode();
        redisClusterNode.setUri(RedisURI.create(host, port));
        redisClusterNode.setSlots(LettuceLists.newList());
        return redisClusterNode;
    }

    private static long getLongFromIterator(Iterator<?> iterator, long defaultValue) {
        if (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof String) {
                return Long.parseLong((String) object);
            }

            if (object instanceof Number) {
                return ((Number) object).longValue();
            }
        }
        return defaultValue;
    }
}
