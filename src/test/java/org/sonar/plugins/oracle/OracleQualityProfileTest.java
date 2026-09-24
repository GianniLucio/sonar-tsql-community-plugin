package org.sonar.plugins.oracle;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class OracleQualityProfileTest {

    @Test
    void definesDefaultOracleProfile() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new OracleQualityProfile().define(context);

        assertThat(context.profilesByLanguageAndName().get("oracle"))
                .isNotNull()
                .hasSize(1);
    }
}