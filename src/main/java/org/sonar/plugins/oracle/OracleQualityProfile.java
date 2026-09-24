package org.sonar.plugins.oracle;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.plugins.oracle.checks.AlterTableDropColumnCheck;
import org.sonar.plugins.oracle.checks.AnonymousBlockWithoutExceptionHandlingCheck;
import org.sonar.plugins.oracle.checks.AvoidSelectStarCheck;
import org.sonar.plugins.oracle.checks.CheckConstraintNullComparisonCheck;
import org.sonar.plugins.oracle.checks.CommitInProgramUnitCheck;
import org.sonar.plugins.oracle.checks.ConnectByWithoutPriorCheck;
import org.sonar.plugins.oracle.checks.CreateSequenceWithoutCacheOrderCheck;
import org.sonar.plugins.oracle.checks.DmlWithoutWhereCheck;
import org.sonar.plugins.oracle.checks.HavingWithoutGroupByCheck;
import org.sonar.plugins.oracle.checks.InsertWithoutColumnListCheck;
import org.sonar.plugins.oracle.checks.MergeWithoutNotMatchedCheck;
import org.sonar.plugins.oracle.checks.ProcedureWithoutExceptionHandlingCheck;
import org.sonar.plugins.oracle.checks.ProgramUnitWithoutOrReplaceCheck;
import org.sonar.plugins.oracle.checks.RollbackInProgramUnitCheck;
import org.sonar.plugins.oracle.checks.RownumWithoutOrderByCheck;
import org.sonar.plugins.oracle.checks.TableWithoutPrimaryKeyCheck;
import org.sonar.plugins.oracle.checks.UnnamedConstraintCheck;
import org.sonar.plugins.oracle.checks.UpperKeywordsCheck;
import org.sonar.plugins.oracle.checks.Varchar2WithoutSizeCheck;
import org.sonar.plugins.oracle.checks.WhenOthersWithoutRaiseCheck;

public class OracleQualityProfile implements BuiltInQualityProfilesDefinition {

    public static final String PROFILE_NAME = "Sonar way";

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(PROFILE_NAME, OracleLanguage.KEY);
        profile.setDefault(true);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, AvoidSelectStarCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, TableWithoutPrimaryKeyCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, UpperKeywordsCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, DmlWithoutWhereCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, CommitInProgramUnitCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, WhenOthersWithoutRaiseCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, RownumWithoutOrderByCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, Varchar2WithoutSizeCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, UnnamedConstraintCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, ProcedureWithoutExceptionHandlingCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, InsertWithoutColumnListCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, ConnectByWithoutPriorCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, CreateSequenceWithoutCacheOrderCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, MergeWithoutNotMatchedCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, AnonymousBlockWithoutExceptionHandlingCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, AlterTableDropColumnCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, HavingWithoutGroupByCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, ProgramUnitWithoutOrReplaceCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, RollbackInProgramUnitCheck.RULE_KEY);
        profile.activateRule(OracleRulesDefinition.REPOSITORY_KEY, CheckConstraintNullComparisonCheck.RULE_KEY);
        profile.done();
    }
}
