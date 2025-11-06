package com.example.jetbrainscli;

import java.util.ArrayList;
import java.util.List;

public class ArtifactReport {
    public String artifactPath;
    public long totalSize;
    public int entryCount;
    public List<EntryRecord> entries = new ArrayList<>();

    public ArtifactReport() {}

    public void recompute() {
        long s = 0;
        for (EntryRecord e : entries) {
            s += e.size;
        }
        this.totalSize = s;
        this.entryCount = entries.size();
    }
}
