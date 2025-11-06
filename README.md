# JetBrains Plugin Artifact Analyzer (CLI)

Small Java CLI that analyzes a plugin artifact (ZIP/JAR) by listing its internal zip entries and saves that to a JSON report. You can also compare two reports to get similarity metrics.

Build (requires Java 11+ and Maven):

```
mvn package
```

This produces a runnable jar in `target/jetbrains-cli-0.1.0-jar-with-dependencies.jar`.

Usage:

Analyze an artifact (zip/jar):

```
java -jar target/jetbrains-cli-0.1.0-jar-with-dependencies.jar analyze path/to/plugin.jar -o reportA.json
```

If you want the tool to compute an HMAC-SHA256 for each entry (useful for content-based comparison), provide a key with `-k`:

```
java -jar target/jetbrains-cli-0.1.0-jar-with-dependencies.jar analyze path/to/plugin.jar -k "my-secret-key" -o reportA.json
```

The key is treated as a UTF-8 string. The analyzer will compute an HMAC-SHA256 for each non-directory entry and store it as `hmac` in the JSON report. You can later compare reports and the comparator will include hash-based similarity metrics when `hmac` values are present.

Compare two reports:

```
java -jar target/jetbrains-cli-0.1.0-jar-with-dependencies.jar compare reportA.json reportB.json -o compare.json
```

What it records:
- Entry name
- size, compressedSize
- crc, timestamp
- isDirectory

Comparison:
- Jaccard similarity over entry names
- counts and list of common entries (in JSON)


