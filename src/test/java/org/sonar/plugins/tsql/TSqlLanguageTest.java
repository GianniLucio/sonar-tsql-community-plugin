package org.sonar.plugins.tsql;

import org.junit.jupiter.api.Test;
import org.sonar.api.config.Configuration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TSqlLanguageTest {

    @Test
    void testLanguageDefaults() {
        Configuration config = mock(Configuration.class);
        when(config.getStringArray(TSqlLanguage.FILE_SUFFIXES_KEY)).thenReturn(new String[0]);

        TSqlLanguage language = new TSqlLanguage(config);
        assertThat(language.getKey()).isEqualTo("tsql");
        assertThat(language.getName()).isEqualTo("T-SQL");
        assertThat(language.getFileSuffixes()).containsExactly(".tsql");
    }

    @Test
    void testCustomFileSuffixes() {
        Configuration config = mock(Configuration.class);
        when(config.getStringArray(TSqlLanguage.FILE_SUFFIXES_KEY)).thenReturn(new String[]{".sql", ".prc", ".tab"});

        TSqlLanguage language = new TSqlLanguage(config);
        assertThat(language.getFileSuffixes()).containsExactly(".sql", ".prc", ".tab");
    }
}
