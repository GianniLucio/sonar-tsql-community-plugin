package org.sonar.plugins.tsql;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.tsql.checks.*;

public class TSqlQualityProfile implements BuiltInQualityProfilesDefinition {

    public static final String PROFILE_NAME = "Sonar way";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(PROFILE_NAME, TSqlLanguage.KEY);
        profile.setDefault(true);

        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidSelectStarCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidNoLockCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidSpPrefixCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, MissingSemicolonCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidCursorCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, TransactionXactAbortCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, DynamicSqlInjectionCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidOrderByOrdinalCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, TableWithoutPrimaryKeyCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, UpperKeywordsCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, DmlWithoutWhereCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, TopWithoutOrderByCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, DeprecatedDataTypeCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidPrintStatementCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidRaiserrorCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, CartesianProductCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidWhileLoopCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, ConvertWithoutStyleCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, AvoidIndexHintCheck.RULE_KEY);
        profile.activateRule(TSqlRulesDefinition.REPOSITORY_KEY, ProcedureWithoutErrorHandlingCheck.RULE_KEY);

        profile.done();
    }
}
