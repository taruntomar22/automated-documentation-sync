package com.capstone.validator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocValidationRunner {

    public static void main(String[] args) {
        try {
            runValidation();
        } catch (Exception e) {
            System.err.println("Documentation validation failed with exception: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void runValidation() throws IOException {
        Path docsDir = Paths.get("docs");
        if (!Files.exists(docsDir) || !Files.isDirectory(docsDir)) {
            System.out.println("No docs/ directory found; skipping documentation validation.");
            // still create an empty report
            writeReport(new HashMap<>());
            return;
        }

        // Use an empty required headings/terminology list to avoid inventing rules here.
        DocumentationValidator validator = new DocumentationValidator(new ArrayList<>(), new ArrayList<>());

        Map<String, Object> report = new HashMap<>();
        List<Object> files = new ArrayList<>();

        List<Path> mdFiles = Files.walk(docsDir)
            .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".md"))
            .collect(Collectors.toList());

        for (Path md : mdFiles) {
            Map<String, Object> fileEntry = new HashMap<>();
            fileEntry.put("path", docsDir.getParent() == null ? md.toString() : docsDir.relativize(md).toString());
            try {
                DocumentationValidator.ValidationResult vr = validator.validateFile(md);
                fileEntry.put("passed", vr.passed);
                List<Map<String, String>> findings = new ArrayList<>();
                for (DocumentationValidator.Finding f : vr.findings) {
                    Map<String, String> fi = new HashMap<>();
                    fi.put("rule", f.rule);
                    fi.put("message", f.message);
                    findings.add(fi);
                }
                fileEntry.put("findings", findings);
            } catch (IOException e) {
                fileEntry.put("passed", false);
                List<Map<String, String>> findings = new ArrayList<>();
                Map<String, String> fi = new HashMap<>();
                fi.put("rule", "exception");
                fi.put("message", e.getMessage());
                findings.add(fi);
                fileEntry.put("findings", findings);
            }
            files.add(fileEntry);
        }

        report.put("files", files);
        report.put("count", files.size());

        writeReport(report);
        System.out.println("Documentation validation completed. Report written to target/doc-validation-report.json");
    }

    private static void writeReport(Map<String, Object> report) throws IOException {
        Path target = Paths.get("target");
        if (!Files.exists(target)) Files.createDirectories(target);
        Path out = target.resolve("doc-validation-report.json");

        // Simple JSON serialization (no external deps)
        String json = toJson(report);
        Files.write(out, json.getBytes(StandardCharsets.UTF_8));
    }

    // Minimal JSON serializer for the simple report structure used here.
    private static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            Map<?,?> m = (Map<?,?>) obj;
            boolean first = true;
            for (Map.Entry<?,?> e : m.entrySet()) {
                if (!first) sb.append(",");
                sb.append(escape(String.valueOf(e.getKey()))).append(":").append(toJson(e.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof List) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            List<?> l = (List<?>) obj;
            boolean first = true;
            for (Object o : l) {
                if (!first) sb.append(",");
                sb.append(toJson(o));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof String) {
            return escape((String) obj);
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return String.valueOf(obj);
        }
        // fallback
        return escape(String.valueOf(obj));
    }

    private static String escape(String s) {
        if (s == null) return "\"\"";
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32 || c > 126) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
