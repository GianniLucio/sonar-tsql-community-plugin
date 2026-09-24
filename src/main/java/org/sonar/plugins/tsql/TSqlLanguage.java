package org.sonar.plugins.tsql;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.Arrays;
import java.util.Optional;

/**
 * Defines Microsoft T-SQL language for SonarQube.
 */
public class TSqlLanguage extends AbstractLanguage {

    public static final String KEY = "tsql";
    public static final String NAME = "T-SQL";
    public static final String FILE_SUFFIXES_KEY = "sonar.tsql.file.suffixes";
    public static final String DEFAULT_FILE_SUFFIXES = ".tsql";

    private final Configuration configuration;

    public TSqlLanguage(Configuration configuration) {
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
