package link.mcseu.crypto.pgp.api;

import java.io.InputStream;

public interface Packet {
    public String getFileName();
    public InputStream getDataStream();
}