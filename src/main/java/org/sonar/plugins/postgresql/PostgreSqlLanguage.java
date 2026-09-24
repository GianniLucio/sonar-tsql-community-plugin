package org.sonar.plugins.postgresql;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.Arrays;

/**
 * Defines PostgreSQL language for SonarQube.
 */
public class PostgreSqlLanguage extends AbstractLanguage {

    public static final String KEY = "pgsql";
    public static final String NAME = "PostgreSQL";
    public static final String FILE_SUFFIXES_KEY = "sonar.pgsql.file.suffixes";
    public static final String DEFAULT_FILE_SUFFIXES = ".sql,.pgsql,.postgres";

    private final Configuration configuration;

    public PostgreSqlLanguage(Configuration configuration) {
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
