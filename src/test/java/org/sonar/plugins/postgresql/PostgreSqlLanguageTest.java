package org.sonar.plugins.postgresql;

import org.junit.jupiter.api.Test;
import org.sonar.api.config.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostgreSqlLanguageTest {

    @Test
    public void testLanguageKey() {
        Configuration config = mock(Configuration.class);
        when(config.getStringArray(PostgreSqlLanguage.FILE_SUFFIXES_KEY)).thenReturn(new String[0]);

        PostgreSqlLanguage language = new PostgreSqlLanguage(config);
        assertThat(language.getKey()).isEqualTo("pgsql");
    }

    @Test
    public void testLanguageName() {
        Configuration config = mock(Configuration.class);
        when(config.getStringArray(PostgreSqlLanguage.FILE_SUFFIXES_KEY)).thenReturn(new String[0]);

        PostgreSqlLanguage language = new PostgreSqlLanguage(config);
        assertThat(language.getName()).isEqualTo("PostgreSQL");
    }

    @Test
    public void testDefaultFileSuffixes() {
        Configuration config = mock(Configuration.class);
        when(config.getStringArray(PostgreSqlLanguage.FILE_SUFFIXES_KEY)).thenReturn(new String[0]);

        PostgreSqlLanguage language = new PostgreSqlLanguage(config);
        String[] suffixes = language.getFileSuffixes();
        assertThat(suffixes).contains(".sql", ".pgsql", ".postgres");
    }

    @Test
    public void testCustomFileSuffixes() {
        Configuration config = mock(Configuration.class);
        when(config.getStringArray(PostgreSqlLanguage.FILE_SUFFIXES_KEY)).thenReturn(new String[]{".sql", ".plpgsql"});

        PostgreSqlLanguage language = new PostgreSqlLanguage(config);
        String[] suffixes = language.getFileSuffixes();
        assertThat(suffixes).containsExactly(".sql", ".plpgsql");
    }
}
