package org.sonar.plugins.postgresql;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.postgresql.checks.*;

public class PostgreSqlQualityProfile implements BuiltInQualityProfilesDefinition {

    public static final String PROFILE_NAME = "Sonar way";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(PROFILE_NAME, PostgreSqlLanguage.KEY);
        profile.setDefault(true);

        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, AvoidSelectStarCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, AvoidCursorCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, MissingSemicolonCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, DynamicSqlInjectionCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, AvoidOrderByOrdinalCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, TableWithoutPrimaryKeyCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, UpperKeywordsCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, MissingIndexOnForeignKeyCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, AvoidSerialDataTypeCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, UnloggedTableUsageCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, DmlWithoutWhereCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, LimitWithoutOrderByCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, OffsetWithoutLimitCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, CartesianProductCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, RedundantDistinctWithGroupByCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, UselessCheckConstraintCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, LikeLeadingWildcardCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, NotInWithSubqueryCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, DropWithoutIfExistsCheck.RULE_KEY);
        profile.activateRule(PostgreSqlRulesDefinition.REPOSITORY_KEY, ForeignKeyWithoutOnDeleteActionCheck.RULE_KEY);

        profile.done();
    }
}
