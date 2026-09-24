package org.sonar.plugins.oracle;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.Arrays;

/**
 * Defines Oracle SQL and PL/SQL as a SonarQube language.
 */
public class OracleLanguage extends AbstractLanguage {

    public static final String KEY = "oracle";
    public static final String NAME = "Oracle";
    public static final String FILE_SUFFIXES_KEY = "sonar.oracle.file.suffixes";
    public static final String DEFAULT_FILE_SUFFIXES = ".oracle";

    private final Configuration configuration;

    public OracleLanguage(Configuration configuration) {
        super(KEY, NAME);
        this.configuration = configuration;
    }

    @Override
    public String[] getFileSuffixes() {
        String[] suffixes = configuration.getStringArray(FILE_SUFFIXES_KEY);
        if (suffixes != null && suffixes.length > 0) {
            return suffixes;
        }
        return Arrays.stream(DEFAULT_FILE_SUFFIXES.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }
}
