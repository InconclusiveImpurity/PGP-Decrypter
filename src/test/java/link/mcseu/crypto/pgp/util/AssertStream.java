package link.mcseu.crypto.pgp.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import link.mcseu.crypto.pgp.Constants;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.junit.Assert;

@UtilityClass
public class AssertStream {
    public void equals(InputStream expected, InputStream actual) {
        equals(new DataInputStream(expected), actual);
    }

    // Loosly based on http://stackoverflow.com/a/4245881 
    @SneakyThrows(IOException.class) // Tests should not catch IOException
    public void equals(DataInputStream expected, InputStream actual) {
        final byte[] left = new byte[Constants.BUFFER_SIZE];
        final byte[] right = new byte[Constants.BUFFER_SIZE];

        int len;
        while ((len = actual.read(left)) > 0) {
            expected.readFully(right, 0, len);
            Assert.assertArrayEquals(left, right);
        }

        Assert.assertEquals(expected.read(), expected.read());
    }
}