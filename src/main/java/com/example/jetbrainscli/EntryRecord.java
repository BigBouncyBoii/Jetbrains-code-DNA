package com.example.jetbrainscli;

public class EntryRecord {
    public String name;
    public long size;
    public long compressedSize;
    public long crc;
    public boolean isDirectory;
    public long time;
    public String hmac; // HMAC-SHA256 hex string, optional

    public EntryRecord() {}

    public EntryRecord(String name, long size, long compressedSize, long crc, boolean isDirectory, long time) {
        this.name = name;
        this.size = size;
        this.compressedSize = compressedSize;
        this.crc = crc;
        this.isDirectory = isDirectory;
        this.time = time;
    }

    public void setHmac(String hmac) {
        this.hmac = hmac;
    }
}
