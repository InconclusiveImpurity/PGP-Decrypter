package link.mcseu.crypto.pgp;

import java.io.IOException;
import java.io.InputStream;
import link.mcseu.crypto.pgp.api.Packet;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor
final class Decipher {
    private final PGPSecretKeyRingCollection secrets;
    private final KeyFingerPrintCalculator hasher;
    private final PGPDigestCalculatorProvider digest;
    private PGPPublicKeyEncryptedData data;
    private PGPPrivateKey key;

    /**
     * Finds the first data that can be encrypted with any secret key.
     *
     * @param is
     * @param pass
     *
     * @return true if initialization was successful.
     *
     * @throws IOException if any error occurs.
     */
    public boolean init(InputStream is, char[] pass) throws IOException {
        return APIException.wrap(() -> {
            for (Object o : parse(is)) {
                data = (PGPPublicKeyEncryptedData) o;
                key = getSecretKey(data.getKeyID())
                        .extractPrivateKey(newSecretDecypher(pass));
                return true;
            }
            return false;
        });
    }

    /**
     * Decrypts the initialized data with the initialized key.
     *
     * @return unencrypted Packet
     *
     * @throws IOException if any error occurs.
     */
    public Packet decrypt() throws IOException {
        return APIException.wrap(() -> {
            final InputStream is = data.getDataStream(newPublicDecypher(key));
            Object tmp = new JcaPGPObjectFactory(is).nextObject();

            if (tmp instanceof PGPCompressedData) {
                final PGPCompressedData compressed = (PGPCompressedData) tmp;
                tmp = new JcaPGPObjectFactory(compressed.getDataStream())
                        .nextObject();
            }

            if (tmp instanceof PGPLiteralData)
                return new PacketWrapper((PGPLiteralData) tmp);

            throw new IllegalArgumentException("Unknown Data: " + tmp);
        });
    }

    private PGPEncryptedDataList parse(InputStream is) throws IOException {
        // TODO: Use Iterator as soon as 1.55 is publishe
        final PGPObjectFactory fac = new PGPObjectFactory(is, hasher);
        Object tmp;
        while((tmp = fac.nextObject()) != null) {
            if(tmp instanceof PGPEncryptedDataList) 
                return (PGPEncryptedDataList) tmp;
        }
        throw new IllegalArgumentException("No EncryptedDataList found");
    }

    private PGPSecretKey getSecretKey(long keyId) throws IOException {
        return APIException.wrap(() -> {
            final PGPSecretKey result = secrets.getSecretKey(keyId);
            if (result != null) return result;
            throw new IllegalArgumentException("No secret key found.");
        });
    }

    private PublicKeyDataDecryptorFactory newPublicDecypher(PGPPrivateKey key) {
        return new BcPublicKeyDataDecryptorFactory(key);
    }

    private PBESecretKeyDecryptor newSecretDecypher(char[] pass) {
        return new BcPBESecretKeyDecryptorBuilder(digest).build(pass);
    }

    @RequiredArgsConstructor(access = PACKAGE)
    private final class PacketWrapper implements Packet {
        private @Delegate final PGPLiteralData data;
    }
}