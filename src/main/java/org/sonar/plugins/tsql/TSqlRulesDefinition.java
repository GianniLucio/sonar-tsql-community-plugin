package org.sonar.plugins.tsql;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.tsql.checks.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TSqlRulesDefinition implements RulesDefinition {

    public static final String REPOSITORY_KEY = "tsqlcommunity";
    public static final String REPOSITORY_NAME = "T-SQL Community Rules";

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, TSqlLanguage.KEY)
                .setName(REPOSITORY_NAME);

        defineRule(repository, AvoidSelectStarCheck.RULE_KEY,
                "Avoid 'SELECT *' queries",
                RuleType.CODE_SMELL, Severity.MAJOR, "10min",
                "sql", "performance", "bad-practice");

        defineRule(repository, AvoidNoLockCheck.RULE_KEY,
                "Avoid 'NOLOCK' and 'READUNCOMMITTED' table hints",
                RuleType.BUG, Severity.CRITICAL, "20min",
                "sql", "concurrency", "data-integrity");

        defineRule(repository, AvoidSpPrefixCheck.RULE_KEY,
                "Stored procedures should not use 'sp_' prefix",
                RuleType.CODE_SMELL, Severity.MAJOR, "15min",
                "sql", "naming", "convention");

        defineRule(repository, MissingSemicolonCheck.RULE_KEY,
                "Statements should be terminated with a semicolon",
                RuleType.CODE_SMELL, Severity.MINOR, "2min",
                "sql", "deprecated", "syntax");

        defineRule(repository, AvoidCursorCheck.RULE_KEY,
                "Avoid procedural CURSORs",
                RuleType.CODE_SMELL, Severity.MAJOR, "30min",
                "sql", "performance", "antipattern");

        defineRule(repository, TransactionXactAbortCheck.RULE_KEY,
                "Explicit transactions should enable XACT_ABORT or use TRY...CATCH",
                RuleType.BUG, Severity.CRITICAL, "15min",
                "sql", "reliability", "transactions");

        defineRule(repository, DynamicSqlInjectionCheck.RULE_KEY,
                "Avoid dynamic SQL with string concatenation",
                RuleType.VULNERABILITY, Severity.BLOCKER, "45min",
                "sql", "security", "cwe-89", "owasp-a1", "injection");

        defineRule(repository, AvoidOrderByOrdinalCheck.RULE_KEY,
                "Avoid integer column ordinals in ORDER BY",
                RuleType.CODE_SMELL, Severity.MINOR, "5min",
                "sql", "maintainability");

        defineRule(repository, TableWithoutPrimaryKeyCheck.RULE_KEY,
                "Tables should define a PRIMARY KEY",
                RuleType.BUG, Severity.CRITICAL, "20min",
                "sql", "database-design");

        defineRule(repository, UpperKeywordsCheck.RULE_KEY,
                "Keywords should be in UPPERCASE",
                RuleType.CODE_SMELL, Severity.INFO, "1min",
                "sql", "convention", "formatting");

        defineRule(repository, DmlWithoutWhereCheck.RULE_KEY,
                "UPDATE and DELETE statements should use a WHERE clause",
                RuleType.BUG, Severity.CRITICAL, "15min",
                "sql", "data-integrity", "safety");

        defineRule(repository, TopWithoutOrderByCheck.RULE_KEY,
                "TOP queries should use ORDER BY",
                RuleType.BUG, Severity.MAJOR, "10min",
                "sql", "determinism", "pagination");

        defineRule(repository, DeprecatedDataTypeCheck.RULE_KEY,
                "Avoid deprecated 'TEXT', 'NTEXT', and 'IMAGE' data types",
                RuleType.CODE_SMELL, Severity.MAJOR, "15min",
                "sql", "deprecated", "database-design");

        defineRule(repository, AvoidPrintStatementCheck.RULE_KEY,
                "Avoid using the PRINT statement",
                RuleType.CODE_SMELL, Severity.MINOR, "5min",
                "sql", "bad-practice", "observability");

        defineRule(repository, AvoidRaiserrorCheck.RULE_KEY,
                "Prefer THROW over RAISERROR",
                RuleType.CODE_SMELL, Severity.MINOR, "10min",
                "sql", "deprecated", "error-handling");

        defineRule(repository, CartesianProductCheck.RULE_KEY,
                "Comma-separated FROM tables without a WHERE clause should be avoided",
                RuleType.BUG, Severity.CRITICAL, "20min",
                "sql", "performance", "data-integrity");

        defineRule(repository, AvoidWhileLoopCheck.RULE_KEY,
                "Avoid procedural WHILE loops",
                RuleType.CODE_SMELL, Severity.MAJOR, "30min",
                "sql", "performance", "antipattern");

        defineRule(repository, ConvertWithoutStyleCheck.RULE_KEY,
                "CONVERT to a date/time type should specify an explicit style code",
                RuleType.BUG, Severity.MINOR, "5min",
                "sql", "reliability", "date-time");

        defineRule(repository, AvoidIndexHintCheck.RULE_KEY,
                "Avoid forcing a specific INDEX table hint",
                RuleType.CODE_SMELL, Severity.MAJOR, "15min",
                "sql", "performance", "bad-practice");

        defineRule(repository, ProcedureWithoutErrorHandlingCheck.RULE_KEY,
                "Stored procedures should handle errors with TRY...CATCH",
                RuleType.BUG, Severity.CRITICAL, "20min",
                "sql", "error-handling", "reliability");

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

        String htmlDescription = loadHtmlDescription(ruleKey);
        if (htmlDescription != null) {
            rule.setHtmlDescription(htmlDescription);
        } else {
            rule.setHtmlDescription("<p>" + name + "</p>");
        }
    }

    private String loadHtmlDescription(String ruleKey) {
        String path = "/org/sonar/l10n/tsql/rules/tsqlcommunity/" + ruleKey + ".html";
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
