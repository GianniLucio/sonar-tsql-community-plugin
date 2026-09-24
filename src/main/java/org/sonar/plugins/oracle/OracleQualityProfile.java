package org.sonar.plugins.oracle;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.oracle.checks.AvoidSelectStarCheck;
import org.sonar.plugins.oracle.checks.TableWithoutPrimaryKeyCheck;
import org.sonar.plugins.oracle.checks.UpperKeywordsCheck;

public class OracleQualityProfile implements BuiltInQualityProfilesDefinition {

    public static final String PROFILE_NAME = "Sonar way";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(PROFILE_NAME, OracleLanguage.KEY);
        profile.setDefault(true);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, AvoidSelectStarCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, TableWithoutPrimaryKeyCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, UpperKeywordsCheck.RULE_KEY);
        profile.done();
    }
}
