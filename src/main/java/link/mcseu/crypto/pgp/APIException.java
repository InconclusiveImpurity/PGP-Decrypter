package link.mcseu.crypto.pgp;

import java.io.IOException;
import org.bouncycastle.openpgp.PGPException;

final class APIException extends IOException {
    public APIException(Exception ex) {
        super(ex);
    }

    static <T> T wrap(Expression<T> expr) throws APIException {
        try {
            return expr.invoke();
        } catch (IOException | PGPException ex) {
            throw new APIException(ex);
        }
    }

    @FunctionalInterface
    static interface Expression<T> {
        public T invoke() throws IOException, PGPException;
    }
}