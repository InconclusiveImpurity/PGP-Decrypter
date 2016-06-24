package link.mcseu.crypto.pgp;

import java.io.IOException;
import link.mcseu.crypto.pgp.APIException.Expression;
import org.bouncycastle.openpgp.PGPException;
import org.junit.Assert;
import org.junit.Test;

public final class APIExceptionTest {
    @Test
    public void canConstructWithNull() throws IOException {
        Assert.assertNotNull(new APIException(null));
    }

    @Test(expected = APIException.class)
    public void catchesIOExceptionWithWrap() throws Exception {
        APIException.wrap(throwExeption(new IOException()));
    }

    @Test(expected = APIException.class)
    public void catchesPGPExceptionWithWrap() throws Exception {
        APIException.wrap(throwExeption(new PGPException("test")));
    }

    @Test
    public void checkWrapDoesNotChangeRuntimeException() throws Exception {
        final Exception expected = new IllegalArgumentException();

        try {
            APIException.wrap(throwExeption(expected));
        } catch (RuntimeException actual) {
            Assert.assertSame(expected, actual);
        }
    }

    @Test
    public void checkReturnValue() throws Exception {
        final int i = 10;
        Assert.assertSame(i, APIException.wrap(() -> i));
    }

    private <T extends Exception> Expression<?> throwExeption(T t) {
        return () -> {
            if (t instanceof PGPException) throw (PGPException) t;
            if (t instanceof IOException) throw (IOException) t;
            if (t instanceof RuntimeException) throw (RuntimeException) t;
            throw new IllegalArgumentException();
        };
    }
}