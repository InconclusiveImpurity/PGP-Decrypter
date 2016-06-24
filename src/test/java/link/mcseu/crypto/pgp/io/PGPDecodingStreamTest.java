package link.mcseu.crypto.pgp.io;

import java.io.IOException;
import java.io.InputStream;
import link.mcseu.crypto.pgp.PGP;
import lombok.SneakyThrows;

public final class PGPDecodingStreamTest extends PreprocessingStreamContract {
    @Override
    @SneakyThrows(IOException.class)
    protected PreprocessingStream create(InputStream is){
        final PGP pgp = new PGP(getClass().getResourceAsStream("/secret.key"));
        return new PGPDecodingStream(is, pgp, new char[0]);
    }
}