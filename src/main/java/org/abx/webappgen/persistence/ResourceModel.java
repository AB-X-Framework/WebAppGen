package org.abx.webappgen.persistence;

import org.abx.util.Pair;
import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.abx.webappgen.utils.ElementUtils.elementHashCode;

@Component
public class ResourceModel {
    @Autowired
    TextResourceRepository textResourceRepository;

    @Autowired
    BinaryResourceRepository binaryResourceRepository;

    @Autowired
    private ArrayResourceRepository arrayResourceRepository;

    @Autowired
    private ArrayEntryRepository arrayEntryRepository;

    @Autowired
    private MapResourceRepository mapResourceRepository;

    @Autowired
    private MapEntryRepository mapEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MethodSpecRepository methodSpecRepository;


    @Transactional
    public JSONArray getMapEntries(String mapResourceName, int page, int size) {
        // Assuming elementHashCode returns the ID of the resource
        long resourceId = elementHashCode(mapResourceName);
        // Get the MapResource or throw if not found
        MapResource mapResource = mapResourceRepository.findByMapResourceId(resourceId);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("entryName").ascending());
        // Use the custom query
        org.springframework.data.domain.Page<MapEntry> pageResult = mapEntryRepository.findByMapResource(mapResource, pageRequest);
        JSONArray jsonArray = new JSONArray();
        for (MapEntry entry : pageResult.getContent()) {
            JSONObject jsonObject = new JSONObject();
            jsonArray.put(jsonObject);
            jsonObject.put("key", entry.entryName);
            jsonObject.put("value", entry.mapValue);
        }
        return jsonArray;
    }

    @Transactional
    public JSONArray getEntireArrayEntries(String arrayResourceName) {
        // Assuming elementHashCode returns the ID of the resource
        long resourceId = elementHashCode(arrayResourceName);
        // Get the MapResource or throw if not found
        JSONArray jsonArray = new JSONArray();
        for (ArrayEntry entry : arrayEntryRepository.findAllByArrayResourceId(resourceId)) {

            jsonArray.put(entry.arrayValue);
        }
        return jsonArray;
    }

    @Transactional
    public JSONArray getArrayEntries(String arrayResourceName, int page, int size) {
        // Assuming elementHashCode returns the ID of the resource
        long resourceId = elementHashCode(arrayResourceName);
        // Get the MapResource or throw if not found
        ArrayResource arrayResource = arrayResourceRepository.findByArrayResourceId(resourceId);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("arrayEntryId").ascending());
        // Use the custom query
        org.springframework.data.domain.Page<ArrayEntry> pageResult = arrayEntryRepository.findByArrayResourceId(resourceId, pageRequest);
        JSONArray jsonArray = new JSONArray();
        for (ArrayEntry entry : pageResult.getContent()) {
            JSONObject jsonObject = new JSONObject();
            jsonArray.put(jsonObject);
            jsonObject.put("key", entry.arrayEntryId);
            jsonObject.put("value", entry.arrayValue);
        }
        return jsonArray;
    }

    /**
     * Return title + content + owner
     *
     * @param roles        The roles of the request
     * @param resourceName The requested text
     * @return
     */
    @Transactional
    public JSONObject getTextResource(Set<String> roles, String resourceName) {
        TextResource text = textResourceRepository.findByTextResourceId(
                elementHashCode(resourceName)
        );

        if (text == null) {
            return null;
        }
        String requiredRole = text.role;
        if (!requiredRole.equals("Anonymous")) {
            if (!roles.contains(requiredRole)) {
                return null;
            }
        }
        JSONObject jsonText = new JSONObject();
        jsonText.put("title", text.title);
        jsonText.put("name", text.resourceName);
        jsonText.put("package", text.packageName);
        jsonText.put("content", text.resourceValue);
        jsonText.put("owner", userRepository.findByUserId(text.owner).username);
        jsonText.put("role", text.role);
        return jsonText;
    }

    @Transactional
    public JSONObject getMethodResource(String resourceName) {
        MethodSpec methodSpec = methodSpecRepository.findByMethodSpecId(
                elementHashCode(resourceName)
        );

        if (methodSpec == null) {
            return null;
        }
        JSONObject jsonText = new JSONObject();
        jsonText.put("name", methodSpec.methodName);
        jsonText.put("js", methodSpec.methodJS);
        jsonText.put("description", methodSpec.description);
        jsonText.put("role", methodSpec.role);
        jsonText.put("package", methodSpec.packageName);
        jsonText.put("outputName", methodSpec.outputName);
        jsonText.put("type", methodSpec.type);
        return jsonText;
    }

    @Transactional
    public JSONObject getBinaryResource(String resourceName) {
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(
                elementHashCode(resourceName)
        );
        if (binaryResource == null) {
            return null;
        }
        JSONObject jsonText = new JSONObject();
        jsonText.put("role", binaryResource.role);
        jsonText.put("name", binaryResource.resourceName);
        jsonText.put("package", binaryResource.packageName);
        jsonText.put("owner", userRepository.findByUserId(binaryResource.owner).username);
        jsonText.put("contentType", binaryResource.contentType);
        return jsonText;
    }

    @Transactional
    public void addTextResource(JSONObject content) {
        long resourceId = elementHashCode(content.getString("name"));
        TextResource text = textResourceRepository.findByTextResourceId(resourceId);
        if (text == null) {
            text = new TextResource();
            text.textResourceId = resourceId;
        }
        text.resourceName = content.getString("name");
        text.title = content.getString("title");
        text.packageName = content.getString("package");
        text.resourceValue = content.getString("content");
        text.owner = elementHashCode(content.getString("owner"));
        text.role = content.getString("role");
        textResourceRepository.save(text);
    }

    /**
     * Adds new method
     *
     * @param content method spec
     */
    @Transactional
    public void addMethodResource(JSONObject content) {
        long resourceId = elementHashCode(content.getString("name"));
        MethodSpec method = methodSpecRepository.findByMethodSpecId(resourceId);
        if (method == null) {
            method = new MethodSpec();
            method.methodSpecId = resourceId;
        }
        method.methodJS = content.getString("js");
        method.methodName = content.getString("name");
        method.role = content.getString("role");
        method.packageName = content.getString("package");
        method.description = content.getString("description");
        method.type = content.getString("type");
        method.outputName = content.getString("outputName");
        methodSpecRepository.save(method);
    }

    @Transactional
    public JSONObject deleteText(String resourceName) {
        JSONObject result = new JSONObject();
        TextResource text = textResourceRepository.findByTextResourceId(
                elementHashCode(resourceName)
        );
        if (text == null) {
            result.put("success", false);
            result.put("message", "Resource not found");
            return result;
        }
        textResourceRepository.delete(text);
        result.put("success", true);
        return result;
    }

    @Transactional
    public JSONObject deleteMethod(String resourceName) {
        JSONObject result = new JSONObject();
        MethodSpec method = methodSpecRepository.findByMethodSpecId(
                elementHashCode(resourceName)
        );
        if (method == null) {
            result.put("success", false);
            result.put("message", "Resource not found");
            return result;
        }
        methodSpecRepository.delete(method);
        result.put("success", true);
        return result;
    }

    /**
     * Delete if resource
     *
     * @param owner
     * @param resourceName
     * @return
     */
    @Transactional
    public JSONObject deleteTextIfOwner(long owner, String resourceName) {
        JSONObject result = new JSONObject();
        TextResource text = textResourceRepository.findByTextResourceId(
                elementHashCode(resourceName)
        );

        if (text == null) {
            result.put("success", false);
            result.put("message", "Resource not found");
            return result;
        }
        if (owner != text.owner) {
            result.put("success", false);
            result.put("message", "Resource not owned");
            return result;
        }
        textResourceRepository.delete(text);
        result.put("success", true);
        return result;
    }

    /**
     * Return content plus title
     *
     * @param owner        The resource owner
     * @param resourceName the resource name
     * @return The text
     */
    public JSONObject getTextIfOwner(long owner, String resourceName) {
        TextResource text = textResourceRepository.findByTextResourceId(
                elementHashCode(resourceName)
        );

        if (text == null) {
            return null;
        }
        if (owner != text.owner) {
            return null;
        }
        JSONObject jsonText = new JSONObject();
        jsonText.put("title", text.title);
        jsonText.put("content", text.resourceValue);
        return jsonText;
    }


    public Pair<String, byte[]> getBinaryResource(Set<String> roles, String resourceName) {
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(
                elementHashCode(resourceName));
        if (binaryResource == null) {
            return null;
        }
        String requiredRole = binaryResource.role;
        if (!requiredRole.equals("Anonymous")) {
            if (!roles.contains(requiredRole)) {
                return null;
            }
        }
        return new Pair<>(binaryResource.contentType, binaryResource.resourceValue);
    }

    @Transactional
    public long cloneBinary(String original, String resourceName, String packageName, String owner,
                            String contentType, String role) {
        long originalId = elementHashCode(original);
        long id = elementHashCode(resourceName);
        BinaryResource originalData = binaryResourceRepository.findByBinaryResourceId(originalId);
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(id);
        if (binaryResource == null) {
            binaryResource = new BinaryResource();
            binaryResource.binaryResourceId = id;
        }
        if (contentType != null) {
            binaryResource.contentType = contentType;
        }
        binaryResource.resourceValue = originalData.resourceValue;
        binaryResource.owner = elementHashCode(owner);
        if (role != null) {
            binaryResource.role = role;
        }
        binaryResource.packageName = packageName;
        binaryResource.resourceName = resourceName;
        binaryResourceRepository.save(binaryResource);
        return id;

    }

    @Transactional
    public long saveBinaryResource(String resourceName, String packageName, String owner,
                                   String contentType, byte[] data, String role) {
        long id = elementHashCode(resourceName);
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(id);
        if (binaryResource == null) {
            binaryResource = new BinaryResource();
            binaryResource.binaryResourceId = id;
        }
        if (contentType != null) {
            binaryResource.contentType = contentType;
        }
        if (data != null) {
            binaryResource.resourceValue = data;
        }
        if (binaryResource.resourceValue == null) {
            binaryResource.resourceValue = new byte[0];
        }
        binaryResource.owner = elementHashCode(owner);
        if (role != null) {
            binaryResource.role = role;
        }
        binaryResource.packageName = packageName;
        binaryResource.resourceName = resourceName;
        binaryResourceRepository.save(binaryResource);
        return id;

    }

    public JSONArray getMapPackages() {
        JSONArray array = new JSONArray();
        array.putAll(mapResourceRepository.findDistinctPackageNames());
        return array;
    }


    public JSONArray getArrayPackages() {
        JSONArray array = new JSONArray();
        array.putAll(arrayResourceRepository.findDistinctPackageNames());
        return array;
    }


    public JSONArray getTextPackages() {
        JSONArray array = new JSONArray();
        array.putAll(textResourceRepository.findDistinctPackageNames());
        return array;
    }


    public JSONArray getMethodPackages() {
        JSONArray array = new JSONArray();
        array.putAll(methodSpecRepository.findDistinctPackageNames());
        return array;
    }

    public JSONArray getBinaryPackages() {
        JSONArray array = new JSONArray();
        array.putAll(binaryResourceRepository.findDistinctPackageNames());
        return array;
    }

    public JSONArray getMapsByPackageName(String packageName) {
        JSONArray array = new JSONArray();
        mapResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            array.put(mapResource.resourceName);
        });
        return array;
    }


    public JSONArray getArraysByPackageName(String packageName) {
        JSONArray array = new JSONArray();
        arrayResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            array.put(mapResource.resourceName);
        });
        return array;
    }

    public JSONArray getTextsByPackageName(String packageName) {
        JSONArray array = new JSONArray();
        textResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            array.put(mapResource.resourceName);
        });
        return array;
    }


    public JSONArray getMethodsByPackageName(String packageName) {
        JSONArray array = new JSONArray();
        methodSpecRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            array.put(mapResource.methodName);
        });
        return array;
    }


    public JSONArray getBinariesByPackageName(String packageName) {
        JSONArray array = new JSONArray();
        binaryResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            array.put(mapResource.resourceName);
        });
        return array;
    }


    public JSONArray getBinariesOutputTypes() {
        JSONArray array = new JSONArray();
        binaryResourceRepository.findDistinctContentTypes().forEach(array::put);
        return array;
    }


    public long getMapEntriesCount(String mapName) {
        MapResource mapResource = mapResourceRepository.findByMapResourceId(elementHashCode(mapName));
        return mapEntryRepository.countByMapResource(mapResource);
    }


    public long getArrayEntriesCount(String arrayName) {
        return arrayEntryRepository.countByArrayResourceId(elementHashCode(arrayName));
    }


    @Transactional
    public void createMap(String packageName, String mapName) throws Exception {
        long id = elementHashCode(mapName);
        MapResource mapResource = mapResourceRepository.findByMapResourceId(elementHashCode(mapName));
        if (mapResource != null) {
            throw new Exception("Duplicate map entry");
        }
        mapResource = new MapResource();
        mapResource.packageName = packageName;
        mapResource.mapResourceId = id;
        mapResource.resourceName = mapName;
        mapResourceRepository.save(mapResource);
    }


    @Transactional
    public void createArray(String packageName, String arrayName) throws Exception {
        long id = elementHashCode(arrayName);
        ArrayResource arrayResource = arrayResourceRepository.findByArrayResourceId(elementHashCode(arrayName));
        if (arrayResource != null) {
            throw new Exception("Duplicate map entry");
        }
        arrayResource = new ArrayResource();
        arrayResource.packageName = packageName;
        arrayResource.arrayResourceId = id;
        arrayResource.resourceName = arrayName;
        arrayResourceRepository.save(arrayResource);
    }

    @Transactional
    public void deleteMap(String mapName) throws Exception {
        long id = elementHashCode(mapName);
        MapResource mapResource = mapResourceRepository.findByMapResourceId(id);
        if (mapResource == null) {
            throw new Exception("Map not found");
        }
        mapEntryRepository.deleteAll(mapResource.resourceEntries);
        mapEntryRepository.flush();
        mapResourceRepository.delete(mapResource);
    }

    @Transactional
    public void deleteArray(String arrayName) throws Exception {
        long id = elementHashCode(arrayName);
        ArrayResource arrayResource = arrayResourceRepository.findByArrayResourceId(id);
        if (arrayResource == null) {
            throw new Exception("Map not found");
        }
        arrayEntryRepository.deleteAll(arrayEntryRepository.findAllByArrayResourceId(id));
        arrayEntryRepository.flush();
        arrayResourceRepository.delete(arrayResource);
    }


    public String getMapResource(String mapName, String key) {
        return mapEntryRepository.findByMapEntryId(
                elementHashCode(mapName + "." + key)).mapValue;
    }


    @Transactional
    public boolean deleteMapEntry(String mapName, String key) {
        MapEntry entry = mapEntryRepository.findByMapEntryId(
                elementHashCode(mapName + "." + key));
        if (entry == null) {
            return false;
        }
        mapEntryRepository.delete(entry);
        mapEntryRepository.flush();
        return true;
    }

    @Transactional
    public boolean deleteArrayIndex(String arrayName, long key) {
        ArrayEntry entry = arrayEntryRepository.findByArrayEntryId(key);
        if (entry == null) {
            return false;
        }
        if (entry.arrayResourceId != elementHashCode(arrayName)) {
            return false;
        }
        arrayEntryRepository.delete(entry);
        mapEntryRepository.flush();
        return true;
    }

    @Transactional
    public void updateArrayEntries(String arrayMap, JSONArray values) throws Exception {
        long id = elementHashCode(arrayMap);
        for (int i = 0; i < values.length(); i++) {
            JSONObject object = (JSONObject) values.get(i);
            long key = object.getLong("key");

            String value = object.getString("value");
            ArrayEntry entry = arrayEntryRepository.findByArrayEntryId(key);
            if (entry == null) {
                throw new Exception("Entry not found");
            }
            if (entry.arrayResourceId != id) {
                throw new Exception("Values does not belong to resource");
            }
            entry.arrayValue = value;
            entry.arrayResourceId = id;
            arrayEntryRepository.save(entry);
        }
        arrayEntryRepository.flush();
    }

    @Transactional
    public void addArrayEntry(String arrayMap, String value) {
        long id = elementHashCode(arrayMap);
        ArrayEntry entry = new ArrayEntry();
        entry.arrayValue = value;
        entry.arrayResourceId = id;
        arrayEntryRepository.save(entry);
        arrayEntryRepository.flush();
    }

    @Transactional
    public void saveMapEntries(String mapName, JSONArray values) {
        long id = elementHashCode(mapName);
        MapResource mapResource = mapResourceRepository.findByMapResourceId(id);
        for (int i = 0; i < values.length(); i++) {
            JSONObject object = (JSONObject) values.get(i);
            String key = object.getString("key");
            String value = object.getString("value");
            MapEntry entry = new MapEntry();
            entry.entryName = key;
            entry.mapEntryId = elementHashCode(mapName + "." + key);
            entry.mapValue = value;
            entry.mapResource = mapResource;
            mapEntryRepository.save(entry);
        }
        mapEntryRepository.flush();
    }


    public void saveMapResource(String mapName, String packageName, JSONObject data) {
        long id = elementHashCode(mapName);
        MapResource mapResource = new MapResource();
        mapResource.mapResourceId = id;
        mapResource.resourceName = mapName;
        mapResource.packageName = packageName;
        mapResourceRepository.save(mapResource);
        for (String key : data.keySet()) {
            MapEntry entry = new MapEntry();
            entry.entryName = key;
            entry.mapEntryId = elementHashCode(mapResource.resourceName + "." + entry.entryName);
            entry.mapValue = data.getString(key);
            entry.mapResource = mapResource;
            mapEntryRepository.save(entry);
        }
    }

    public void saveArrayResource(String resourceName, String packageName, JSONArray data) {
        long id = elementHashCode(resourceName);
        ArrayResource previous = arrayResourceRepository.findByArrayResourceId(id);
        if (previous != null) {
            arrayResourceRepository.delete(previous);
            arrayResourceRepository.flush();
        }
        ArrayResource arrayResource = new ArrayResource();
        arrayResource.arrayResourceId = id;
        arrayResource.resourceName = resourceName;
        arrayResource.packageName = packageName;
        arrayResourceRepository.save(arrayResource);
        for (int i = 0; i < data.length(); i++) {
            String value = data.getString(i);
            ArrayEntry entry = new ArrayEntry();
            entry.arrayValue = value;
            entry.arrayResourceId = id;
            arrayEntryRepository.save(entry);
        }
    }

    public void saveTextResource(String resourceName, String owner, String title, String packageName, String data, String role) {
        long id = elementHashCode(resourceName);
        TextResource textResource = new TextResource();
        textResource.textResourceId = id;
        textResource.resourceValue = data;
        textResource.role = role;
        textResource.title = title;
        textResource.owner = elementHashCode(owner);
        textResource.packageName = packageName;
        textResource.resourceName = resourceName;
        textResourceRepository.save(textResource);

    }
}
