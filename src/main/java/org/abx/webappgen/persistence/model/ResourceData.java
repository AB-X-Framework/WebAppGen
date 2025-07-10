package org.abx.webappgen.persistence.model;

public class ResourceData {
    public byte[] data;
    public long hashcode;
    public String contentType;

    public ResourceData(String contentType, byte[] data, long hashcode) {
        this.contentType = contentType;
        this.data = data;
        this.hashcode = hashcode;
    }
}
