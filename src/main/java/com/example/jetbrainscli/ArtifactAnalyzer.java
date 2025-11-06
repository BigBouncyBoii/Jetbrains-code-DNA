package com.example.jetbrainscli;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ArtifactAnalyzer {

    // Backwards-compatible analyze without HMAC key
    public static ArtifactReport analyze(File f) throws IOException {
        return analyze(f, null);
    }

    // Analyze and optionally compute HMAC-SHA256 per entry when hmacKey != null
    public static ArtifactReport analyze(File f, byte[] hmacKey) throws IOException {
        ArtifactReport report = new ArtifactReport();
        report.artifactPath = f.getAbsolutePath();

        try (ZipFile zip = new ZipFile(f)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry e = entries.nextElement();
                EntryRecord r = new EntryRecord(
                        e.getName(),
                        e.getSize() < 0 ? 0 : e.getSize(),
                        e.getCompressedSize() < 0 ? 0 : e.getCompressedSize(),
                        e.getCrc(),
                        e.isDirectory(),
                        e.getTime()
                );

                if (hmacKey != null && !e.isDirectory()) {
                    // compute HMAC-SHA256 for entry contents
                    try (InputStream is = zip.getInputStream(e)) {
                        Mac mac = Mac.getInstance("HmacSHA256");
                        SecretKeySpec keySpec = new SecretKeySpec(hmacKey, "HmacSHA256");
                        mac.init(keySpec);

                        byte[] buf = new byte[8192];
                        int read;
                        while ((read = is.read(buf)) != -1) {
                            mac.update(buf, 0, read);
                        }
                        byte[] h = mac.doFinal();
                        r.setHmac(bytesToHex(h));
                        System.out.println("Computed HMAC for entry: " + e.getName());
                    } catch (Exception ex) {
                        r.setHmac(null);
                    }
                }

                report.entries.add(r);
            }
        }

        report.recompute();
        return report;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
