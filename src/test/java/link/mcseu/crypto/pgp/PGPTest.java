package link.mcseu.crypto.pgp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import link.mcseu.crypto.pgp.util.AssertStream;
import org.junit.Before;
import org.junit.Test;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

// TODO: NEEDS Cleanup
public class PGPTest {
    private static final String EMPTY_FILE = "/empty.msg";
    private static final String SECRET = "/secret.key";
    private static final String ENCODED = "/test.enc.msg";
    private static final String DECODED = "/test.msg";
    private static final String SECRET_LOST = "/404.key";
    private Map<String, InputStream> resources;

    @Before
    public void loadFromFile() {
        resources = Arrays.asList(getClass().getDeclaredFields()).stream()
                .filter(f -> f.getType() == String.class)
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .map(this::getValue) // Static field
                .collect(toMap(identity(), getClass()::getResourceAsStream));
    }

    @Test(expected = IllegalArgumentException.class)
    public void willFailWithoutSecretKey() throws IOException {
        final char[] pass ={};
        create(SECRET).decrypt(getResource(SECRET_LOST), pass);
    }

    @Test
    public void canDecryptEncodedFile() throws IOException {
        final PGP test = create(SECRET);
        final InputStream encrypted = getResource(ENCODED);
        final InputStream result = test.decrypt(encrypted, new char[0])
                .getDataStream();

        AssertStream.equals(getResource(DECODED), result);
    }

    private InputStream getResource(String resource) {
        if(!resources.containsKey(resource))
            throw new IllegalArgumentException("Resource unknown: " + resource);
        return resources.get(resource);
    }

    private PGP create(String resource) throws IOException {
        return new PGP(getResource(resource));
    }

    private String getValue(Field f) {
        try {
            return (String) f.get(null);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }
}