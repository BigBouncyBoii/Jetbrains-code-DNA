package com.example.jetbrainscli;

import java.util.HashSet;
import java.util.Set;

public class CompareTool {

    public static ComparisonReport compare(ArtifactReport a, ArtifactReport b) {
        ComparisonReport r = new ComparisonReport();
        r.aPath = a.artifactPath;
        r.bPath = b.artifactPath;
        r.aEntryCount = a.entryCount;
        r.bEntryCount = b.entryCount;
        r.aTotalSize = a.totalSize;
        r.bTotalSize = b.totalSize;

        // name-based sets
        Set<String> sa = new HashSet<>();
        Set<String> sb = new HashSet<>();
        for (EntryRecord e : a.entries) sa.add(e.name);
        for (EntryRecord e : b.entries) sb.add(e.name);

        Set<String> nameIntersection = new HashSet<>(sa);
        nameIntersection.retainAll(sb);

        Set<String> nameUnion = new HashSet<>(sa);
        nameUnion.addAll(sb);

        r.commonEntryCount = nameIntersection.size();
        r.unionEntryCount = nameUnion.size();
        r.jaccard = nameUnion.size() == 0 ? 1.0 : ((double) nameIntersection.size()) / nameUnion.size();
        r.commonEntries = nameIntersection;

        // hash-based sets (only include non-null hmacs)
        Set<String> ha = new HashSet<>();
        Set<String> hb = new HashSet<>();
        for (EntryRecord e : a.entries) if (e.hmac != null) ha.add(e.hmac);
        for (EntryRecord e : b.entries) if (e.hmac != null) hb.add(e.hmac);

        Set<String> hashIntersection = new HashSet<>(ha);
        hashIntersection.retainAll(hb);

        Set<String> hashUnion = new HashSet<>(ha);
        hashUnion.addAll(hb);

        r.commonByHashCount = hashIntersection.size();
        r.unionByHashCount = hashUnion.size();
        r.hashJaccard = hashUnion.size() == 0 ? 1.0 : ((double) hashIntersection.size()) / hashUnion.size();
        r.commonHashes = hashIntersection;

        return r;
    }

    public static class ComparisonReport {
        public String aPath;
        public String bPath;
        public int aEntryCount;
        public int bEntryCount;
        public long aTotalSize;
        public long bTotalSize;
        // name-based
        public int commonEntryCount;
        public int unionEntryCount;
        public double jaccard;
        public Set<String> commonEntries;
        // hash-based
        public int commonByHashCount;
        public int unionByHashCount;
        public double hashJaccard;
        public Set<String> commonHashes;
    }
}
