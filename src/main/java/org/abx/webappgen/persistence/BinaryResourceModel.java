package org.abx.webappgen.persistence;

import org.abx.util.Pair;
import org.abx.webappgen.persistence.cache.BinaryCache;
import org.abx.webappgen.persistence.cache.BinaryMeta;
import org.abx.webappgen.persistence.model.ResourceData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.abx.webappgen.persistence.ResourceModel.validAccess;
import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

@Component
public class BinaryResourceModel {

    @Autowired
    private BinaryCache binaryCache;

    @Autowired
    ResourceModel resourceModel;

    public BinaryResourceModel() {
    }


    public void upload(String resourceName, byte[] data) throws Exception {
        long id = elementHashCode(resourceName);
        BinaryMeta meta = resourceModel._upload(id, data);
        binaryCache.add(id, meta);
    }


    public JSONObject getBinaryResource(long resourceId) {
        BinaryMeta meta = binaryCache.get(resourceId);
        if (meta == null) {
            return getBinaryResourceAux(resourceId);
        }
        return getJsonObject(meta);
    }

    private JSONObject getBinaryResourceAux(long resourceId) {
        BinaryMeta meta = resourceModel._getBinaryResource(resourceId).first;
        if (meta == null) {
            return null;
        }
        binaryCache.add(resourceId, meta);
        return getJsonObject(meta);
    }

    private JSONObject getJsonObject(BinaryMeta meta) {
        JSONObject jsonText = new JSONObject();
        jsonText.put("access", meta.access);
        jsonText.put("name", meta.resourceName);
        jsonText.put("package", meta.packageName);
        jsonText.put("hashcode", meta.hashcode);
        jsonText.put("owner", meta.owner);
        jsonText.put("contentType", meta.contentType);
        return jsonText;
    }

    public void deleteBinaryResource(String resourceName) {
        long resourceId = elementHashCode(resourceName);
        binaryCache.remove(resourceId);
        resourceModel._deleteBinaryResource(resourceId);
    }

    public ResourceData getBinaryResource(String resourceName, String username, Set<String> roles) {
        long resourceId = elementHashCode(resourceName);
        Pair<BinaryMeta, byte[]> cached = binaryCache.getBinary(resourceId);
        if (cached == null) {
            Pair<BinaryMeta, byte[]> binaryResource = resourceModel._getBinaryResource(resourceId);
            if (binaryResource != null) {
                binaryCache.add(resourceId, binaryResource.first);
            }
        }
        if (cached == null) {
            return null;
        }
        BinaryMeta binaryMeta = cached.first;
        if (!validAccess(binaryMeta.access, binaryMeta.owner, username, roles)) {
            return null;
        }
        return new ResourceData(binaryMeta.contentType, cached.second, binaryMeta.hashcode);
    }


    public void saveBinaryResource(String resourceName, String packageName, String owner,
                                   String contentType, String access) {
        BinaryMeta meta = resourceModel._saveBinaryResource(resourceName, packageName, owner,
                contentType, access);
        long resourceId = elementHashCode(resourceName);
        binaryCache.add(resourceId, meta);
    }
}
