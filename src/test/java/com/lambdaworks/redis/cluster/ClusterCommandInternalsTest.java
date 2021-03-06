package com.lambdaworks.redis.cluster;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.lambdaworks.redis.codec.Utf8StringCodec;
import com.lambdaworks.redis.output.StatusOutput;
import com.lambdaworks.redis.protocol.AsyncCommand;
import com.lambdaworks.redis.protocol.Command;
import com.lambdaworks.redis.protocol.CommandType;

public class ClusterCommandInternalsTest {

    private ClusterCommand<String, String, String> sut;
    private Command<String, String, String> command = new Command<String, String, String>(CommandType.TYPE,
            new StatusOutput<String, String>(new Utf8StringCodec()), null);

    @Before
    public void before() throws Exception {
        sut = new ClusterCommand<String, String, String>(command, null, 1);
    }

    @Test
    public void testException() throws Exception {

        sut.completeExceptionally(new Exception());
        assertThat(sut.isCompleted());
    }

    @Test
    public void testCancel() throws Exception {

        assertThat(command.isCancelled()).isFalse();
        sut.cancel();
        assertThat(command.isCancelled()).isTrue();
    }

    @Test
    public void testComplete() throws Exception {

        sut.complete();
        assertThat(sut.isCompleted()).isTrue();
        assertThat(sut.isCancelled()).isFalse();
    }

    @Test
    public void testCompleteListener() throws Exception {

        final List<String> someList = Lists.newArrayList();

        AsyncCommand<?, ?, ?> asyncCommand = new AsyncCommand<>(sut);

        asyncCommand.thenRun(() -> someList.add(""));
        asyncCommand.complete();
        asyncCommand.await(1, TimeUnit.MINUTES);

        assertThat(sut.isCompleted()).isTrue();
        assertThat(someList.size()).describedAs("Inner listener has to add one element").isEqualTo(1);
    }
}
