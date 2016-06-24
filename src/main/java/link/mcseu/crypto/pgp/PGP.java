package link.mcseu.crypto.pgp;

import java.io.IOException;
import java.io.InputStream;
import link.mcseu.crypto.pgp.api.Packet;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;

public final class PGP {
    private final KeyFingerPrintCalculator hasher;
    private final PGPSecretKeyRingCollection secrets;
    private final PGPDigestCalculatorProvider digest;

    /**
     * Creates a new PGP Instance with the given secret Key.
     *
     * @param in Stream containing secret Key.
     *
     * @throws IOException if any error occures.
     */
    public PGP(InputStream in) throws IOException {
        this.hasher = new BcKeyFingerprintCalculator();
        this.digest = new BcPGPDigestCalculatorProvider();
        this.secrets = APIException.wrap(() -> {
            final InputStream is = PGPUtil.getDecoderStream(in);
            return new PGPSecretKeyRingCollection(is, hasher);
        });
    }

    // TODO: Doc: Reads first ASCII Armor it can find, ignores anything after / before
    public Packet decrypt(InputStream in, char[] pass) throws IOException {
        final Decipher cipher = new Decipher(secrets, hasher, digest);

        if (!cipher.init(PGPUtil.getDecoderStream(in), pass)) {
            throw new IllegalArgumentException("Could not init decipher.");
        }

        return cipher.decrypt();
    }
}