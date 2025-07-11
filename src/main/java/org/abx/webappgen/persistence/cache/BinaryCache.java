package org.abx.webappgen.persistence.cache;

import org.abx.util.Pair;
import org.abx.util.StreamUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class BinaryCache extends ResourceCache<BinaryMeta> {

    public BinaryCache() {
        super(1000);
    }


    public Pair<BinaryMeta, byte[]> getBinary(long id) {
        BinaryMeta binaryMeta = get(id);
        if (binaryMeta == null) {
            return null;
        }
        try {
            byte[] data = StreamUtils.readByteArrayStream(new FileInputStream(binaryMeta.file));
            return new Pair<>(binaryMeta, data);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void dispose(BinaryMeta binaryMeta) {
        binaryMeta.file.delete();
    }
}
