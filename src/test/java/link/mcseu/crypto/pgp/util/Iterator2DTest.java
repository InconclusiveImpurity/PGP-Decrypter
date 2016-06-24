package link.mcseu.crypto.pgp.util;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

public final class Iterator2DTest {
    @Test
    public void test() {
        final int x = 10, y = 20; 
        final AtomicInteger latch = new AtomicInteger();
        for (int o : Iterator2D.of(mock(x, y, latch::incrementAndGet))) {
            Assert.assertEquals(x * y, o + latch.decrementAndGet());
        }

        Assert.assertEquals(0, latch.get());
    }

    private <T> Iterable<Iterable<T>> mock(int x, int y, Supplier<T> t) {
        return mock(x, () -> mock(y, t));
    }

    private <T> Collection<T> mock(int amount, Supplier<T> generator) {
        return IntStream.range(0, amount)
                .mapToObj(i -> generator.get())
                .collect(Collectors.toList());
    }
}