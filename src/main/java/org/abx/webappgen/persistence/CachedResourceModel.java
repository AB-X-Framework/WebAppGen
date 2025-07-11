package org.abx.webappgen.persistence;

import org.abx.util.Pair;
import org.abx.webappgen.persistence.cache.BinaryCache;
import org.abx.webappgen.persistence.cache.BinaryMeta;
import org.abx.webappgen.persistence.model.ResourceData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.abx.webappgen.persistence.ResourceModel.validAccess;
import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

@Component
public class CachedResourceModel {

    @Autowired
    private BinaryCache binaryCache;

    @Autowired
    ResourceModel resourceModel;

    public CachedResourceModel() {
    }


    public void upload(String resourceName, byte[] data) throws Exception {
        long id = elementHashCode(resourceName);
        resourceModel.upload(id, data);
    }


    public JSONObject getBinaryResource(long resourceId) {
        BinaryMeta meta = binaryCache.get(resourceId);
        if (meta == null) {
            return getBinaryResourceAux(resourceId);
        }
        JSONObject jsonText = new JSONObject();
        jsonText.put("access", meta.access);
        jsonText.put("name", meta.resourceName);
        jsonText.put("package", meta.packageName);
        jsonText.put("hashcode", meta.hashcode);
        jsonText.put("owner", meta.owner);
        jsonText.put("contentType", meta.contentType);
        return jsonText;
    }

    private JSONObject getBinaryResourceAux(long resourceId) {
        BinaryMeta binaryResource = resourceModel.getBinaryResource(resourceId).first;
        if (binaryResource == null) {
            return null;
        }
        binaryCache.add(resourceId, binaryResource);
        JSONObject jsonText = new JSONObject();
        jsonText.put("access", binaryResource.access);
        jsonText.put("name", binaryResource.resourceName);
        jsonText.put("package", binaryResource.packageName);
        jsonText.put("owner", binaryResource.owner);
        jsonText.put("contentType", binaryResource.contentType);
        jsonText.put("hashcode", binaryResource.hashcode);
        return jsonText;
    }

    public ResourceData getBinaryResource(String resourceName, String username, Set<String> roles) {
        long resourceId = elementHashCode(resourceName);
        Pair<BinaryMeta, byte[]> cached = binaryCache.getBinary(resourceId);
        if (cached == null) {
            Pair<BinaryMeta, byte[]> binaryResource = resourceModel.getBinaryResource(resourceId);
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
}
