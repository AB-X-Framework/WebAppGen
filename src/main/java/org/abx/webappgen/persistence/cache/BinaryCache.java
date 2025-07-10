package org.abx.webappgen.persistence.cache;

import org.abx.util.Pair;
import org.abx.util.StreamUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class BinaryCache {
    private Map<Long, BinaryMeta> currentCache;

    public BinaryCache() {
        currentCache = new HashMap<>();
    }

    public void add(long hashcode,String contentType,String owner,String access,
                    byte[] data) throws IOException {
        File f = File.createTempFile("data", ".tmp");
        f.deleteOnExit();
        try (FileOutputStream stream = new FileOutputStream(f)) {
            stream.write(data);
            BinaryMeta meta = new BinaryMeta();
            meta.file = f;
            meta.contentType = contentType;
            meta.access = access;
            meta.username = owner;
            currentCache.put(hashcode, meta);
        } catch (IOException e){
            //Will not add the caceh
        }
    }

    public void remove(long id) {
        if (currentCache.containsKey(id)) {
            currentCache.remove(id).file.delete();
        }
    }

    public Pair<BinaryMeta,byte[]> get(long id) {
        if (currentCache.containsKey(id)) {
            BinaryMeta meta = currentCache.get(id);
            try {
                return new Pair(meta,
                        StreamUtils.readByteArrayStream(new FileInputStream(meta.file)));
            }catch (IOException e){
                return null;
            }
        }else {
            return null;
        }
    }
}
