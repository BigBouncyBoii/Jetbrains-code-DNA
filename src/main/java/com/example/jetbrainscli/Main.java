package com.example.jetbrainscli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final ObjectMapper M = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        String cmd = args[0];
        switch (cmd) {
            case "analyze":
                handleAnalyze(args);
                break;
            case "compare":
                handleCompare(args);
                break;
            default:
                System.err.println("Unknown command: " + cmd);
                printUsage();
                System.exit(2);
        }
    }

    private static void handleAnalyze(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: analyze <artifact.jar|zip> [-k key] [-o out.json]");
            System.exit(3);
        }
        Path in = Path.of(args[1]);
        Path out = Path.of("report.json");
        String key = null;
        for (int i = 2; i < args.length; i++) {
            if ("-o".equals(args[i]) && i + 1 < args.length) {
                out = Path.of(args[++i]);
            } else if ("-k".equals(args[i]) && i + 1 < args.length) {
                keyPath = args[++i];
            }
        }

        if (!Files.exists(in)) {
            System.err.println("Input artifact not found: " + in);
            System.exit(4);
        }

        ArtifactReport report;
        if (keyPath != null) {
            byte[] keyBytes = Files.readAllBytes(keyPath);
            System.out.println("Using HMAC key: " + keyPath);
            report = ArtifactAnalyzer.analyze(in.toFile(), keyBytes);
        } else {
            report = ArtifactAnalyzer.analyze(in.toFile());
        }
        M.writeValue(out.toFile(), report);
        System.out.println("Saved report to " + out.toAbsolutePath());
        if (key != null) System.out.println("HMAC-SHA256 computed for entries (key provided).");
    }

    private static void handleCompare(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: compare <reportA.json> <reportB.json> [-o report.json]");
            System.exit(5);
        }
        Path a = Path.of(args[1]);
        Path b = Path.of(args[2]);
        Path out = Path.of("compare.json");
        for (int i = 3; i < args.length; i++) {
            if ("-o".equals(args[i]) && i + 1 < args.length) {
                out = Path.of(args[++i]);
            }
        }

        if (!Files.exists(a) || !Files.exists(b)) {
            System.err.println("One or both report files not found.");
            System.exit(6);
        }

        ArtifactReport ra = M.readValue(a.toFile(), ArtifactReport.class);
        ArtifactReport rb = M.readValue(b.toFile(), ArtifactReport.class);

        CompareTool.ComparisonReport cr = CompareTool.compare(ra, rb);
        M.writeValue(out.toFile(), cr);
        System.out.println("Saved comparison to " + out.toAbsolutePath());
        System.out.println(String.format("Jaccard similarity: %.4f (common %d / union %d)", cr.jaccard, cr.commonEntryCount, cr.unionEntryCount));
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  analyze <artifact.jar|zip> [-o out.json]   - produce a JSON report of zip/jar entries");
        System.out.println("  compare <reportA.json> <reportB.json> [-o compare.json] - compare two reports and save a JSON summary");
    }
}
