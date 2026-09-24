package org.sonar.plugins.postgresql;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgreSqlQualityProfileTest {

    @Test
    public void testQualityProfile() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        PostgreSqlQualityProfile profile = new PostgreSqlQualityProfile();
        profile.define(context);

        assertThat(context.profilesByLanguageAndName().get("pgsql"))
                .isNotNull()
                .hasSize(1);
    }
}
