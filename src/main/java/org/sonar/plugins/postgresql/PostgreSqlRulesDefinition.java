package org.sonar.plugins.postgresql;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.postgresql.checks.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PostgreSqlRulesDefinition implements RulesDefinition {

    public static final String REPOSITORY_KEY = "pgsqlcommunity";
    public static final String REPOSITORY_NAME = "PostgreSQL Community Rules";

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, PostgreSqlLanguage.KEY)
                .setName(REPOSITORY_NAME);

        defineRule(repository, AvoidSelectStarCheck.RULE_KEY,
                "Avoid 'SELECT *' queries",
                RuleType.CODE_SMELL, Severity.MAJOR, "10min",
                "sql", "performance", "bad-practice");

        defineRule(repository, AvoidCursorCheck.RULE_KEY,
                "Avoid procedural CURSORs",
                RuleType.CODE_SMELL, Severity.MAJOR, "30min",
                "sql", "performance", "antipattern");

        defineRule(repository, MissingSemicolonCheck.RULE_KEY,
                "Statements should be terminated with a semicolon",
                RuleType.CODE_SMELL, Severity.MINOR, "2min",
                "sql", "deprecated", "syntax");

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

        defineRule(repository, MissingIndexOnForeignKeyCheck.RULE_KEY,
                "Foreign keys should have an index",
                RuleType.BUG, Severity.MAJOR, "15min",
                "sql", "performance", "database-design");

        defineRule(repository, AvoidSerialDataTypeCheck.RULE_KEY,
                "Avoid SERIAL data type, use IDENTITY or GENERATED",
                RuleType.CODE_SMELL, Severity.MINOR, "10min",
                "sql", "best-practice", "compatibility");

        defineRule(repository, UnloggedTableUsageCheck.RULE_KEY,
                "Use UNLOGGED tables cautiously",
                RuleType.BUG, Severity.MAJOR, "20min",
                "sql", "reliability", "performance");

        defineRule(repository, DmlWithoutWhereCheck.RULE_KEY,
                "UPDATE and DELETE statements should use a WHERE clause",
                RuleType.BUG, Severity.CRITICAL, "15min",
                "sql", "data-integrity", "safety");

        defineRule(repository, LimitWithoutOrderByCheck.RULE_KEY,
                "LIMIT and OFFSET queries should use ORDER BY",
                RuleType.BUG, Severity.MAJOR, "10min",
                "sql", "determinism", "pagination");

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
        String path = "/org/sonar/l10n/pgsql/rules/pgsqlcommunity/" + ruleKey + ".html";
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
