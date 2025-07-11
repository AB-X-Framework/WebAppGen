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
import java.util.Map;
import java.util.Set;

import static org.abx.webappgen.persistence.ResourceModel.validAccess;
import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

@Component
public class CachedResourceModel {

    public static final String CachedResource = "::resources:";
    public static final String BinaryResources = "::resources:binary/";

    private final Map<Long, Set<EnvListener>> resourceListener;

    @Autowired
    private BinaryCache binaryCache;

    @Autowired
    ResourceModel resourceModel;

    public CachedResourceModel() {
        resourceListener = new HashMap<>();
    }

    public void addResourceListener(long id, EnvListener listener) {
        if (!resourceListener.containsKey(id)) {
            resourceListener.put(id, new HashSet<>());
        }
        resourceListener.get(id).add(listener);
    }

    private void resourceChanged(long id) {
        Set<EnvListener> list = resourceListener.get(id);
        if (list != null) {
            for (EnvListener listener : list) {
                listener.envChanged();
            }
        }
    }

    public void upload(String resourceName, byte[] data) throws Exception {
        long id = elementHashCode(resourceName);
        BinaryMeta meta = resourceModel._upload(id, data);
        binaryCache.add(id, meta);
        resourceChanged(id);
    }

    public JSONObject getBinaryResource(String resourceName) {
        return getBinaryResource(elementHashCode(resourceName));
    }

    public JSONObject getBinaryResource(long resourceId) {
        BinaryMeta meta = binaryCache.get(resourceId);
        if (meta == null) {
            meta = getBinaryMeta(resourceId);
        }
        if (meta == null) {
            return null;
        }
        return getJsonObject(meta);
    }

    private BinaryMeta getBinaryMeta(long resourceId) {
        BinaryMeta meta = resourceModel._getBinaryResource(resourceId).first;
        if (meta == null) {
            return null;
        }
        binaryCache.add(resourceId, meta);
        return meta;
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
        resourceChanged(resourceId);
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
        resourceChanged(resourceId);
    }

    public String cacheResource(String resourceName) {
        if (!resourceName.startsWith(CachedResource)) {
            return resourceName;
        }
        resourceName = resourceName.substring(CachedResource.length());
        int index = resourceName.indexOf('/');
        String resourceType = resourceName.substring(0, index);
        resourceName = resourceName.substring(index + 1);
        if (resourceType.equals("binary")) {
            BinaryMeta meta = getBinaryMeta(elementHashCode(resourceName));
            return "/resources/binary/" + meta.resourceName + "?hc=" + meta.hashcode;
        }
        throw new IllegalArgumentException("Invalid resource name: " + resourceName);
    }


    public void cloneBinary(String original, String resourceName, String packageName, String owner, String contentType) {
        long originalId = elementHashCode(original);
        BinaryMeta meta = resourceModel._cloneBinary(originalId, resourceName, packageName, owner, contentType);
        binaryCache.add(originalId, meta);

    }
}
