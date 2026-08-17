package com.capstone.validator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

public class DocumentationValidator {
    public static class Finding {
        public final String rule;
        public final String message;
        public Finding(String rule, String message) {
            this.rule = rule;
            this.message = message;
        }
        @Override
        public String toString() { return rule + ": " + message; }
    }

    public static class ValidationResult {
        public final boolean passed;
        public final List<Finding> findings;
        public ValidationResult(boolean passed, List<Finding> findings) {
            this.passed = passed;
            this.findings = findings;
        }
    }

    private final List<String> requiredHeadings;
    private final List<String> requiredTerminology;

    public DocumentationValidator(List<String> requiredHeadings, List<String> requiredTerminology) {
        this.requiredHeadings = requiredHeadings == null ? new ArrayList<>() : requiredHeadings;
        this.requiredTerminology = requiredTerminology == null ? new ArrayList<>() : requiredTerminology;
    }

    public ValidationResult validateMarkdownString(String markdown) {
        List<Finding> findings = new ArrayList<>();
        if (markdown == null) {
            findings.add(new Finding("empty-input", "Document content is null"));
            return new ValidationResult(false, findings);
        }

        // Normalize line endings
        String text = markdown.replace("\r\n", "\n");

        // Check required headings
        for (String heading : requiredHeadings) {
            Pattern p = Pattern.compile("(?im)^#*\\s*" + Pattern.quote(heading) + "\\s*$", Pattern.MULTILINE);
            Matcher m = p.matcher(text);
            if (!m.find()) {
                findings.add(new Finding("missing-heading", "Required heading not found: '" + heading + "'"));
            } else {
                // Check content under heading (until next heading of same or higher level)
                int idx = m.end();
                int nextHeading = findNextHeadingIndex(text, idx);
                String section = text.substring(idx, nextHeading).trim();
                if (section.isEmpty()) {
                    findings.add(new Finding("empty-section", "Heading exists but section is empty: '" + heading + "'"));
                }
            }
        }

        // Check required terminology
        for (String term : requiredTerminology) {
            if (!text.toLowerCase().contains(term.toLowerCase())) {
                findings.add(new Finding("missing-terminology", "Required terminology not found: '" + term + "'"));
            }
        }

        // Check placeholders
        String[] placeholders = {"TODO", "TBD", "TO-DO"};
        for (String ph : placeholders) {
            Pattern p = Pattern.compile("(?i)\\b" + Pattern.quote(ph) + "\\b");
            Matcher m = p.matcher(text);
            if (m.find()) {
                findings.add(new Finding("placeholder-found", "Unresolved placeholder found: '" + m.group() + "'"));
            }
        }

        // Check fabricated endpoints / obvious placeholders like {endpoint} or example.com or localhost
        Pattern curly = Pattern.compile("\\{.+?\\}");
        Matcher cm = curly.matcher(text);
        if (cm.find()) {
            findings.add(new Finding("fabricated-endpoint", "Found curly-brace placeholder: '" + cm.group() + "'"));
        }
        Pattern example = Pattern.compile("(?i)example\\.(com|org)|localhost");
        Matcher em = example.matcher(text);
        if (em.find()) {
            findings.add(new Finding("fabricated-endpoint", "Found example/localhost endpoint: '" + em.group() + "'"));
        }

        boolean passed = findings.isEmpty();
        return new ValidationResult(passed, findings);
    }

    public ValidationResult validateFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            List<Finding> f = new ArrayList<>();
            f.add(new Finding("file-not-found", "File not found: " + path.toString()));
            return new ValidationResult(false, f);
        }
        String content = new String(Files.readAllBytes(path));
        return validateMarkdownString(content);
    }

    private int findNextHeadingIndex(String text, int start) {
        Pattern p = Pattern.compile("(?m)^#{1,6}\\s+.*$");
        Matcher m = p.matcher(text);
        if (m.find(start)) {
            return m.start();
        }
        return text.length();
    }
}
