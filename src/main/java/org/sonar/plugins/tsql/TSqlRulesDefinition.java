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
