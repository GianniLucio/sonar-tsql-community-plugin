package org.sonar.plugins.oracle;

import org.junit.jupiter.api.Test;
import org.sonar.api.config.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OracleLanguageTest {

    @Test
    void testLanguageDefaultsAreUnambiguous() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getStringArray(OracleLanguage.FILE_SUFFIXES_KEY)).thenReturn(new String[0]);

        OracleLanguage language = new OracleLanguage(configuration);

        assertThat(language.getKey()).isEqualTo("oracle");
        assertThat(language.getName()).isEqualTo("Oracle");
        assertThat(language.getFileSuffixes()).containsExactly(".oracle");
    }

    @Test
    void testCustomFileSuffixes() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getStringArray(OracleLanguage.FILE_SUFFIXES_KEY))
                .thenReturn(new String[]{".sql", ".pls"});

        OracleLanguage language = new OracleLanguage(configuration);

        assertThat(language.getFileSuffixes()).containsExactly(".sql", ".pls");
    }
}
