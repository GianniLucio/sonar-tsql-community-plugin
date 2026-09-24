package org.sonar.plugins.tsql;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class TSqlQualityProfileTest {

    @Test
    void testQualityProfileDefinition() {
        TSqlQualityProfile qualityProfile = new TSqlQualityProfile();
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        qualityProfile.define(context);

        BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile =
                context.profile(TSqlLanguage.KEY, TSqlQualityProfile.PROFILE_NAME);

        assertThat(profile).isNotNull();
        assertThat(profile.isDefault()).isTrue();
        assertThat(profile.rules()).hasSize(12);
    }
}
