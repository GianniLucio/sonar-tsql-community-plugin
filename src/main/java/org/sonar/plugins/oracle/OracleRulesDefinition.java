package org.sonar.plugins.oracle;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class OracleRulesDefinition implements RulesDefinition {

    public static final String REPOSITORY_KEY = "oraclecommunity";
    public static final String REPOSITORY_NAME = "Oracle Community Rules";

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, OracleLanguage.KEY)
                .setName(REPOSITORY_NAME);

        defineRule(repository, AvoidSelectStarCheck.RULE_KEY, "Avoid 'SELECT *' queries",
                RuleType.CODE_SMELL, Severity.MAJOR, "10min", "sql", "performance");
        defineRule(repository, TableWithoutPrimaryKeyCheck.RULE_KEY, "Tables should define a PRIMARY KEY",
                RuleType.BUG, Severity.CRITICAL, "20min", "sql", "database-design");
        defineRule(repository, UpperKeywordsCheck.RULE_KEY, "Keywords should be in UPPERCASE",
                RuleType.CODE_SMELL, Severity.INFO, "1min", "sql", "formatting");
        defineRule(repository, DmlWithoutWhereCheck.RULE_KEY,
            "UPDATE and DELETE statements should use a WHERE clause",
            RuleType.BUG, Severity.CRITICAL, "15min", "sql", "data-integrity", "safety");

        defineRule(repository, CommitInProgramUnitCheck.RULE_KEY,
            "Avoid COMMIT inside a procedure, function, or package body",
            RuleType.CODE_SMELL, Severity.MAJOR, "20min", "sql", "transactions", "bad-practice");

        defineRule(repository, WhenOthersWithoutRaiseCheck.RULE_KEY,
            "'WHEN OTHERS' exception handlers should re-raise the exception",
            RuleType.BUG, Severity.CRITICAL, "15min", "sql", "error-handling", "reliability");

        defineRule(repository, RownumWithoutOrderByCheck.RULE_KEY,
            "ROWNUM should not be used without an ORDER BY clause",
            RuleType.BUG, Severity.MAJOR, "10min", "sql", "determinism", "pagination");

        defineRule(repository, Varchar2WithoutSizeCheck.RULE_KEY,
            "VARCHAR2 columns should declare an explicit size",
            RuleType.CODE_SMELL, Severity.MINOR, "5min", "sql", "database-design");

        defineRule(repository, UnnamedConstraintCheck.RULE_KEY,
            "Table constraints should be explicitly named",
            RuleType.CODE_SMELL, Severity.MINOR, "10min", "sql", "database-design", "maintainability");

        defineRule(repository, ProcedureWithoutExceptionHandlingCheck.RULE_KEY,
            "Procedures and functions should define an EXCEPTION handling section",
            RuleType.BUG, Severity.CRITICAL, "20min", "sql", "error-handling", "reliability");

        defineRule(repository, InsertWithoutColumnListCheck.RULE_KEY,
            "INSERT statements should specify an explicit column list",
            RuleType.CODE_SMELL, Severity.MINOR, "5min", "sql", "maintainability");

        defineRule(repository, ConnectByWithoutPriorCheck.RULE_KEY,
            "CONNECT BY should reference PRIOR",
            RuleType.BUG, Severity.MAJOR, "15min", "sql", "reliability", "hierarchical-query");

        defineRule(repository, CreateSequenceWithoutCacheOrderCheck.RULE_KEY,
            "CREATE SEQUENCE should explicitly specify CACHE/NOCACHE and ORDER/NOORDER",
            RuleType.CODE_SMELL, Severity.MAJOR, "10min", "sql", "database-design");

        defineRule(repository, MergeWithoutNotMatchedCheck.RULE_KEY,
            "MERGE should handle the WHEN NOT MATCHED case",
            RuleType.BUG, Severity.MAJOR, "15min", "sql", "data-integrity");

        defineRule(repository, AnonymousBlockWithoutExceptionHandlingCheck.RULE_KEY,
            "Anonymous PL/SQL blocks should define an EXCEPTION handling section",
            RuleType.BUG, Severity.CRITICAL, "20min", "sql", "error-handling", "reliability");

        defineRule(repository, AlterTableDropColumnCheck.RULE_KEY,
            "ALTER TABLE DROP COLUMN should be reviewed carefully",
            RuleType.CODE_SMELL, Severity.MAJOR, "10min", "sql", "destructive-ddl", "safety");

        defineRule(repository, HavingWithoutGroupByCheck.RULE_KEY,
            "HAVING should not be used without GROUP BY",
            RuleType.CODE_SMELL, Severity.MINOR, "5min", "sql", "readability");

        defineRule(repository, ProgramUnitWithoutOrReplaceCheck.RULE_KEY,
            "Procedures, functions, packages and triggers should use OR REPLACE",
            RuleType.CODE_SMELL, Severity.MINOR, "5min", "sql", "maintainability", "deployment");

        defineRule(repository, RollbackInProgramUnitCheck.RULE_KEY,
            "Avoid ROLLBACK inside a procedure, function, package, or trigger body",
            RuleType.CODE_SMELL, Severity.MAJOR, "20min", "sql", "transactions", "bad-practice");

        defineRule(repository, CheckConstraintNullComparisonCheck.RULE_KEY,
            "CHECK constraints should not compare with NULL using = or <>",
            RuleType.BUG, Severity.MAJOR, "10min", "sql", "data-integrity", "reliability");

        repository.done();
    }

    private void defineRule(NewRepository repository, String ruleKey, String name,
                            RuleType type, String severity, String remediationTime, String... tags) {
        NewRule rule = repository.createRule(ruleKey)
                .setName(name)
                .setType(type)
                .setSeverity(severity)
                .setStatus(RuleStatus.READY)
                .addTags(tags);
        rule.setDebtRemediationFunction(rule.debtRemediationFunctions().constantPerIssue(remediationTime));
        String description = loadHtmlDescription(ruleKey);
        rule.setHtmlDescription(description == null ? "<p>" + name + "</p>" : description);
    }

    private String loadHtmlDescription(String ruleKey) {
        String path = "/org/sonar/l10n/oracle/rules/oraclecommunity/" + ruleKey + ".html";
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }
            try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
                return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            }
        } catch (Exception e) {
            return null;
        }
    }
}
