package org.abx.webappgen.persistence;

import org.abx.util.Pair;
import org.abx.webappgen.persistence.dao.*;
import org.abx.webappgen.persistence.model.*;
import org.abx.webappgen.utils.ElementUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static org.abx.webappgen.utils.ElementUtils.*;

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

    @Autowired
    private ArrayPairEntryRepository arrayPairEntryRepository;

    @Autowired
    private ArrayPairResourceRepository arrayPairResourceRepository;

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

    @Transactional
    public JSONArray getArrayPairEntries(String arrayResourceName, int page, int size) {
        // Assuming elementHashCode returns the ID of the resource
        long resourceId = elementHashCode(arrayResourceName);
        // Get the MapResource or throw if not found
        ArrayPairResource arrayResource = arrayPairResourceRepository.findByArrayPairResourceId(resourceId);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("arrayPairEntryId").ascending());
        // Use the custom query
        org.springframework.data.domain.Page<ArrayPairEntry> pageResult =
                arrayPairEntryRepository.findByArrayPairResourceId(resourceId, pageRequest);
        JSONArray jsonArray = new JSONArray();
        for (ArrayPairEntry entry : pageResult.getContent()) {
            JSONObject jsonObject = new JSONObject();
            jsonArray.put(jsonObject);
            jsonObject.put("id", entry.arrayPairEntryId);
            jsonObject.put("key", entry.arrayPairKey);
            jsonObject.put("value", entry.arrayPairValue);
        }
        return jsonArray;
    }

    @Transactional
    public JSONArray getUsers(int page, int size) {
        // Assuming elementHashCode returns the ID of the resource
        // Get the MapResource or throw if not found
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("username").ascending());
        // Use the custom query
        org.springframework.data.domain.Page<User> pageResult =
                userRepository.findAll(pageRequest);
        JSONArray jsonArray = new JSONArray();
        for (User entry : pageResult.getContent()) {
            JSONObject jsonObject = new JSONObject();
            jsonArray.put(jsonObject);
            jsonObject.put("name", entry.username);
            jsonObject.put("role", entry.role);
        }
        return jsonArray;
    }

    @Transactional
    public long userCount() {
        return userRepository.count();
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
        String requiredAccess = text.access;
        if (!requiredAccess.equals(Anonymous)) {
            if (!roles.contains(requiredAccess)) {
                return null;
            }
        }
        JSONObject jsonText = new JSONObject();
        jsonText.put("title", text.title);
        jsonText.put("name", text.resourceName);
        jsonText.put("package", text.packageName);
        jsonText.put("content", text.resourceValue);
        jsonText.put("owner", userRepository.findByUserId(text.owner).username);
        jsonText.put("access", text.access);
        return jsonText;
    }

    @Transactional
    public String getText(Set<String> roles, String resourceName) {
        TextResource text = textResourceRepository.findByTextResourceId(
                elementHashCode(resourceName)
        );
        if (text == null) {
            return null;
        }
        String requiredAccess = text.access;
        if (!requiredAccess.equals("Anonymous")) {
            if (!roles.contains(requiredAccess)) {
                return null;
            }
        }
        return text.resourceValue;
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
        jsonText.put("access", binaryResource.access);
        jsonText.put("name", binaryResource.resourceName);
        jsonText.put("package", binaryResource.packageName);
        jsonText.put("owner", userRepository.findByUserId(binaryResource.owner).username);
        jsonText.put("contentType", binaryResource.contentType);
        return jsonText;
    }


    @Transactional
    public void deleteBinaryResource(String resourceName) {
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(
                elementHashCode(resourceName)
        );
        binaryResourceRepository.delete(binaryResource);
        binaryResourceRepository.flush();
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
        text.access = content.getString("access");
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
        String access = binaryResource.access;
        if (!access.equals(Anonymous)) {
            if (!roles.contains(access)) {
                return null;
            }
        }
        return new Pair<>(binaryResource.contentType, binaryResource.resourceValue);
    }

    @Transactional
    public void cloneBinary(String original, String resourceName, String packageName, String owner,
                            String contentType, String access) {
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
        if (access != null) {
            binaryResource.access = access;
        }
        binaryResource.packageName = packageName;
        binaryResource.resourceName = resourceName;
        binaryResourceRepository.save(binaryResource);
    }

    @Transactional
    public long saveBinaryResource(String resourceName, String packageName, String owner,
                                   String contentType, String access) {
        long id = elementHashCode(resourceName);
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(id);
        if (binaryResource == null) {
            binaryResource = new BinaryResource();
            binaryResource.binaryResourceId = id;
            binaryResource.resourceValue = new byte[0];
        }
        binaryResource.contentType = contentType;
        binaryResource.owner = elementHashCode(owner);
        binaryResource.access = access;
        binaryResource.packageName = packageName;
        binaryResource.resourceName = resourceName;
        binaryResourceRepository.save(binaryResource);
        return id;
    }


    @Transactional
    public void upload(String resourceName, byte[] data) throws Exception {
        long id = elementHashCode(resourceName);
        BinaryResource binaryResource = binaryResourceRepository.findByBinaryResourceId(id);
        if (binaryResource == null) {
            throw new Exception("Binary not found");
        }
        binaryResource.resourceValue = data;
        binaryResourceRepository.save(binaryResource);
    }

    public JSONArray getMapPackages() {
        ArrayList<String> packages = new ArrayList<>(mapResourceRepository.findDistinctPackageNames());
        Collections.sort(packages);
        return new JSONArray(packages);
    }

    public JSONArray getArrayPackages() {
        ArrayList<String> packages = new ArrayList<>(arrayResourceRepository.findDistinctPackageNames());
        Collections.sort(packages);
        return new JSONArray(packages);
    }

    public JSONArray getArrayPairPackages() {
        ArrayList<String> packages = new ArrayList<>(arrayPairResourceRepository.findDistinctPackageNames());
        Collections.sort(packages);
        return new JSONArray(packages);
    }

    public JSONArray getTextPackages() {
        ArrayList<String> packages = new ArrayList<>(textResourceRepository.findDistinctPackageNames());
        Collections.sort(packages);
        return new JSONArray(packages);
    }


    public JSONArray getMethodPackages() {
        ArrayList<String> packages = new ArrayList<>(methodSpecRepository.findDistinctPackageNames());
        Collections.sort(packages);
        return new JSONArray(packages);
    }

    public JSONArray getBinaryPackages() {
        ArrayList<String> binaryPackages = new ArrayList<>(binaryResourceRepository.findDistinctPackageNames());
        Collections.sort(binaryPackages);
        return new JSONArray(binaryPackages);
    }

    public JSONArray getMapsByPackageName(String packageName) {
        ArrayList<String> maps = new ArrayList<>();
        mapResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            maps.add(mapResource.resourceName);
        });
        Collections.sort(maps);
        return new JSONArray(maps);
    }

    public JSONArray getArraysByPackageName(String packageName) {
        ArrayList<String> arrays = new ArrayList<>();
        arrayResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            arrays.add(mapResource.resourceName);
        });
        Collections.sort(arrays);
        return new JSONArray(arrays);
    }

    public JSONArray getArrayPairsByPackageName(String packageName) {
        ArrayList<String> arrayPairs = new ArrayList<>();
        arrayPairResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            arrayPairs.add(mapResource.resourceName);
        });
        Collections.sort(arrayPairs);
        return new JSONArray(arrayPairs);
    }

    public JSONArray getTextsByPackageName(String packageName) {
        ArrayList<String> texts = new ArrayList<>();
        textResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            texts.add(mapResource.resourceName);
        });
        Collections.sort(texts);
        return new JSONArray(texts);
    }


    public JSONArray getMethodsByPackageName(String packageName) {
        ArrayList<String> methodByPackage = new ArrayList<>();
        methodSpecRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            methodByPackage.add(mapResource.methodName);
        });
        Collections.sort(methodByPackage);
        return new JSONArray(methodByPackage);
    }


    public JSONArray getBinariesByPackageName(String packageName) {
        ArrayList<String> binaries = new ArrayList<>();
        binaryResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            binaries.add(mapResource.resourceName);
        });
        Collections.sort(binaries);
        return new JSONArray(binaries);
    }


    public JSONArray getJSByPackageName(String packageName) {
        ArrayList<String> js = new ArrayList<>();
        binaryResourceRepository.findAllByPackageName(packageName).forEach((mapResource) -> {
            if ("text/javascript".equals(mapResource.contentType)
                    || "text/css".equals(mapResource.contentType)) {
                js.add(mapResource.resourceName);
            }
        });
        Collections.sort(js);
        return new JSONArray(js);
    }

    public JSONArray getBinariesOutputTypes() {
        ArrayList<String> array = new ArrayList<>(binaryResourceRepository.findDistinctContentTypes());
        Collections.sort(array);
        return new JSONArray(array);
    }


    public long getMapEntriesCount(String mapName) {
        MapResource mapResource = mapResourceRepository.findByMapResourceId(elementHashCode(mapName));
        return mapEntryRepository.countByMapResource(mapResource);
    }


    public long getArrayEntriesCount(String arrayName) {
        return arrayEntryRepository.countByArrayResourceId(elementHashCode(arrayName));
    }


    public long getArrayPairEntriesCount(String arrayPairName) {
        return arrayPairEntryRepository.countByArrayPairResourceId(elementHashCode(arrayPairName));
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
    public void createArrayPair(String packageName, String arrayPairName) throws Exception {
        long id = elementHashCode(arrayPairName);
        ArrayPairResource arrayResource = arrayPairResourceRepository.findByArrayPairResourceId(
                elementHashCode(arrayPairName));
        if (arrayResource != null) {
            throw new Exception("Duplicate map entry");
        }
        arrayResource = new ArrayPairResource();
        arrayResource.packageName = packageName;
        arrayResource.arrayPairResourceId = id;
        arrayResource.resourceName = arrayPairName;
        arrayPairResourceRepository.save(arrayResource);
    }

    @Transactional
    public JSONArray getArrayPair(String arrayPairName, String keyLabel, String valueLabel, String username,
                             Set<String> roles) {
        long id = elementHashCode(arrayPairName);
        ArrayPairResource arrayResource = arrayPairResourceRepository.findByArrayPairResourceId(id);
        if (arrayResource == null) {
            return null;
        }
        if (arrayResource.access.equals(ElementUtils.User)){
            if (!username.equals(userRepository.findByUserId(arrayResource.owner).username)) {
                if (!roles.contains(Admin)){
                    return null;
                }
            }
        }
        JSONArray jsonArrayPair = new JSONArray();
        for(ArrayPairEntry entry: arrayPairEntryRepository.findAllByArrayPairResourceId(id)){
            JSONObject jsonEntry = new JSONObject();
            jsonArrayPair.put(jsonEntry);
            jsonEntry.put(keyLabel, entry.arrayPairKey);
            jsonEntry.put(valueLabel, entry.arrayPairValue);
        }
        return jsonArrayPair;
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
            throw new Exception("Array not found");
        }
        arrayEntryRepository.deleteAll(arrayEntryRepository.findAllByArrayResourceId(id));
        arrayEntryRepository.flush();
        arrayResourceRepository.delete(arrayResource);
    }

    @Transactional
    public void deleteArrayPair(String arrayPairName) throws Exception {
        long id = elementHashCode(arrayPairName);
        ArrayPairResource arrayPairResource = arrayPairResourceRepository.findByArrayPairResourceId(id);
        if (arrayPairResource == null) {
            throw new Exception("ArrayPair not found");
        }
        arrayPairEntryRepository.deleteAll(arrayPairEntryRepository.findAllByArrayPairResourceId(id));
        arrayPairEntryRepository.flush();
        arrayPairResourceRepository.delete(arrayPairResource);
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
        arrayEntryRepository.flush();
        return true;
    }

    @Transactional
    public boolean deleteArrayPairIndex(String arrayName, long key) {
        ArrayPairEntry entry = arrayPairEntryRepository.findByArrayPairEntryId(key);
        if (entry == null) {
            return false;
        }
        if (entry.arrayPairResourceId != elementHashCode(arrayName)) {
            return false;
        }
        arrayPairEntryRepository.delete(entry);
        arrayPairEntryRepository.flush();
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
    public void updateArrayPairEntries(String arrayMap, JSONArray values) throws Exception {
        long id = elementHashCode(arrayMap);
        for (int i = 0; i < values.length(); i++) {
            JSONObject object = (JSONObject) values.get(i);
            long key = object.getLong("key");

            JSONObject pair = object.getJSONObject("value");
            ArrayPairEntry entry = arrayPairEntryRepository.findByArrayPairEntryId(key);
            if (entry == null) {
                throw new Exception("Entry not found");
            }
            if (entry.arrayPairResourceId != id) {
                throw new Exception("Values does not belong to resource");
            }
            entry.arrayPairKey = pair.getString("key");
            entry.arrayPairValue = pair.getString("value");
            entry.arrayPairResourceId = id;
            arrayPairEntryRepository.save(entry);
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
    public void addArrayPairEntry(String arrayMap, String key, String value) {
        long id = elementHashCode(arrayMap);
        ArrayPairEntry entry = new ArrayPairEntry();
        entry.arrayPairKey = key;
        entry.arrayPairValue = value;
        entry.arrayPairResourceId = id;
        arrayPairEntryRepository.save(entry);
        arrayPairEntryRepository.flush();
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

    public void saveArrayResource(String resourceName, String packageName, JSONArray data,
                                  String owner,String access) {
        long id = elementHashCode(resourceName);
        ArrayResource previous = arrayResourceRepository.findByArrayResourceId(id);
        if (previous != null) {
            arrayResourceRepository.delete(previous);
            arrayResourceRepository.flush();
            arrayEntryRepository.deleteAll(arrayEntryRepository.findAllByArrayResourceId(id));
        }
        ArrayResource arrayResource = new ArrayResource();
        arrayResource.arrayResourceId = id;
        arrayResource.resourceName = resourceName;
        arrayResource.packageName = packageName;
        arrayResource.owner=elementHashCode(owner);
        arrayResource.access=access;
        arrayResourceRepository.save(arrayResource);
        for (int i = 0; i < data.length(); i++) {
            String value = data.getString(i);
            ArrayEntry entry = new ArrayEntry();
            entry.arrayValue = value;
            entry.arrayResourceId = id;
            arrayEntryRepository.save(entry);
        }
    }

    public void saveArrayPairResource(String resourceName, String packageName, JSONArray data) {
        long id = elementHashCode(resourceName);
        ArrayPairResource previous = arrayPairResourceRepository.findByArrayPairResourceId(id);
        if (previous != null) {
            arrayPairResourceRepository.delete(previous);
            arrayPairResourceRepository.flush();
            arrayPairEntryRepository.deleteAll(arrayPairEntryRepository.findAllByArrayPairResourceId(id));
        }
        ArrayPairResource arrayPairResource = new ArrayPairResource();
        arrayPairResource.arrayPairResourceId = id;
        arrayPairResource.resourceName = resourceName;
        arrayPairResource.packageName = packageName;
        arrayPairResourceRepository.save(arrayPairResource);
        for (int i = 0; i < data.length(); i++) {
            JSONObject value = data.getJSONObject(i);
            ArrayPairEntry entry = new ArrayPairEntry();
            entry.arrayPairKey = value.getString("key");
            entry.arrayPairValue = value.getString("value");
            entry.arrayPairResourceId = id;
            arrayPairEntryRepository.save(entry);
        }
    }

    public void saveTextResource(String resourceName, String owner,
                                 String title, String packageName, String data, String access) {
        long id = elementHashCode(resourceName);
        TextResource textResource = new TextResource();
        textResource.textResourceId = id;
        textResource.resourceValue = data;
        textResource.access = access;
        textResource.title = title;
        textResource.owner = elementHashCode(owner);
        textResource.packageName = packageName;
        textResource.resourceName = resourceName;
        textResourceRepository.save(textResource);

    }
}
