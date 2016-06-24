package link.mcseu.crypto.pgp.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class PreprocessingStreamContract {
    @Test(expected = IllegalArgumentException.class)
    public void failsWithUnmarkableStream() {
        final InputStream is = mock(InputStream.class);
        create(is);
    }

    @Test
    public void verifyReadBytesContract() throws IOException {
        final byte[] data = "abcd".getBytes();
        final byte[] buffer = new byte[data.length * 5];
        int length = create(new ByteArrayInputStream(data))
                .read(buffer);

        Assert.assertEquals(data.length, length);
    }

    @Test
    public void testLength() throws IOException {
        final byte[] data = "0123456891011121314151617181920".getBytes();
        final int times = data.length / 4;
        final InputStream is = create(new ByteArrayInputStream(data));

        assertEquals(times, is.read(new byte[times], 0, times));
        assertEquals(data.length - times, is.available());
    }

    protected abstract PreprocessingStream create(InputStream is);
}