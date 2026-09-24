package org.sonar.plugins.oracle;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.oracle.checks.AvoidSelectStarCheck;
import org.sonar.plugins.oracle.checks.DmlWithoutWhereCheck;
import org.sonar.plugins.oracle.checks.TableWithoutPrimaryKeyCheck;
import org.sonar.plugins.oracle.checks.UpperKeywordsCheck;

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
