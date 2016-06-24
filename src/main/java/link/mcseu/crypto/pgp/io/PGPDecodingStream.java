package link.mcseu.crypto.pgp.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import link.mcseu.crypto.pgp.PGP;
import lombok.RequiredArgsConstructor;

public final class PGPDecodingStream extends PreprocessingStream {
    private static final byte[] START = getSeperator("BEGIN PGP MESSAGE"); 
    private static final byte[] END   = getSeperator("END PGP MESSAGE");
    private final PGP pgp;
    private final char[] pass;
    private InputStream buffer = new ByteArrayInputStream(new byte[0]);

    public PGPDecodingStream(InputStream is, PGP pgp, char[] pass) {
        super(is);
        this.pgp = pgp;
        this.pass = pass;
    }

    @Override
    public int read() throws IOException {
        return canReadBuffer() ? buffer.read() : super.read();
    }

    private boolean canReadBuffer() throws IOException {
        if (buffer.available() > 0) return true;
        if (!matchesNext(START)) return false;

        buffer = pgp.decrypt(new DecodingStream(), pass).getDataStream();
        return true;
    }

    private final class DecodingStream extends InputStream {
        private int next = 0;

        @Override
        public int read() throws IOException {
            if (!isAvailable()) return EOF;
            if (next > 0 || matchesNext(END)) next++;
            return PGPDecodingStream.super.read();
        }

        @Override
        public int available() {
            return END.length - next;
        }

        private boolean isAvailable() {
            return available() > 0;
        }
    }

    private static byte[] getSeperator(String name) {
        return ("-----" + name + "-----").getBytes();
    }
}