package com.capstone.tests;

import com.capstone.validator.DocumentationValidator;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class DocumentationValidatorTest {

    @Test
    void TC_001_validInput_shouldPassValidation() throws Exception {
        String md = "# Requirements\n\n## User Story\nSome user story content.\n\n## Functional Requirements\n- FR-001\n\n";
        DocumentationValidator v = new DocumentationValidator(
                Arrays.asList("Requirements","User Story","Functional Requirements"),
                Arrays.asList("user story","Functional Requirements")
        );
        DocumentationValidator.ValidationResult r = v.validateMarkdownString(md);
        assertTrue(r.passed, "Expected validation to pass for well-formed document. Findings: " + r.findings);
    }

    @Test
    void TC_002_emptyInput_shouldHandleGracefully() throws Exception {
        DocumentationValidator v = new DocumentationValidator(Arrays.asList("Requirements"), null);
        DocumentationValidator.ValidationResult r = v.validateMarkdownString("");
        assertFalse(r.passed);
        assertTrue(r.findings.stream().anyMatch(f -> f.rule.equals("missing-heading") || f.rule.equals("empty-section") ), "Expected missing heading or empty section");
    }

    @Test
    void TC_003_fileNotFound_shouldReturnNotFound() throws Exception {
        DocumentationValidator v = new DocumentationValidator(Arrays.asList("Requirements"), null);
        DocumentationValidator.ValidationResult r = v.validateFile(Path.of("non-existent-file.md"));
        assertFalse(r.passed);
        assertTrue(r.findings.stream().anyMatch(f -> f.rule.equals("file-not-found")));
    }

    @Test
    void TC_004_missingField_shouldReturnValidationError() throws Exception {
        String md = "# Requirements\n\n## User Story\n\n"; // empty user story section
        DocumentationValidator v = new DocumentationValidator(
                Arrays.asList("Requirements","User Story"), null);
        DocumentationValidator.ValidationResult r = v.validateMarkdownString(md);
        assertFalse(r.passed);
        assertTrue(r.findings.stream().anyMatch(f -> f.rule.equals("empty-section")));
    }

    @Test
    void TC_005_invalidInput_shouldReject() throws Exception {
        String md = null; // invalid input
        DocumentationValidator v = new DocumentationValidator(null, null);
        DocumentationValidator.ValidationResult r = v.validateMarkdownString(md);
        assertFalse(r.passed);
        assertTrue(r.findings.stream().anyMatch(f -> f.rule.equals("empty-input")));
    }
}
