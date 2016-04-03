package com.lambdaworks.redis.commands.rx;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lambdaworks.redis.internal.LettuceMaps;
import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import com.lambdaworks.Delay;
import com.lambdaworks.redis.AbstractRedisClientTest;
import com.lambdaworks.redis.api.rx.RedisReactiveCommands;
import com.lambdaworks.redis.internal.LettuceLists;

/**
 * @author Mark Paluch
 */
public class RxTest extends AbstractRedisClientTest {
    private RedisReactiveCommands<String, String> reactive;

    @Before
    public void openConnection() throws Exception {
        super.openConnection();
        reactive = redis.getStatefulConnection().reactive();
    }

    @Test
    public void reactiveChain() throws Exception {

        Map<String, String> map = LettuceMaps.newHashMap();
        map.put(key, value);
        map.put("key1", "value1");

        reactive.mset(map).toBlocking().first();

        List<String> values = reactive.keys("*").flatMap(s -> reactive.get(s)).toList().subscribeOn(Schedulers.immediate())
                .toBlocking().first();

        assertThat(values).hasSize(2).contains(value, "value1");
    }

    @Test
    public void auth() throws Exception {
        List<Throwable> errors = LettuceLists.newList();
        reactive.auth("error").doOnError(errors::add).subscribe(new TestSubscriber<>());
        Delay.delay(millis(50));
        assertThat(errors).hasSize(1);
    }
}
