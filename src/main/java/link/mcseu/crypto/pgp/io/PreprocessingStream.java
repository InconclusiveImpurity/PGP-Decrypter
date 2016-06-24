package link.mcseu.crypto.pgp.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class PreprocessingStream extends BufferedInputStream {
    protected static final int EOF = -1;

    public PreprocessingStream(InputStream in) {
        super(in);
        if(!in.markSupported()) 
            throw new IllegalArgumentException("marking not supported");
    }

    @Override
    public final int read(byte[] bytes, int off, int len) throws IOException {
        int b = 0;
        for (int i = off; i < len; i++) {
            final byte c = (byte) read();
            if(c == EOF) break;
            bytes[i] = c;
            b++;
        }
        return b;
    }

    /**
     * Reads the whole buffer.
     * Equals to read(bytes, 0, bytes.length);
     *
     * @param bytes
     *
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the stream has been reached.
     *
     * @throws java.io.IOException
     */
    @Override
    public final int read(byte[] bytes) throws IOException {
        return super.read(bytes, 0, bytes.length);
    }

    protected boolean matchesNext(byte[] arr) throws IOException {
        mark(arr.length - 1);
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] != super.read()) {
                reset();
                return false;
            }
        }
        reset();
        return true;
    }
}