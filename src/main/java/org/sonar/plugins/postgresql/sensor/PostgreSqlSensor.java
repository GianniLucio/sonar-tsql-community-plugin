package org.sonar.plugins.postgresql.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.postgresql.PostgreSqlLanguage;
import org.sonar.plugins.postgresql.PostgreSqlRulesDefinition;
import org.sonar.plugins.postgresql.ast.IssueLocation;
import org.sonar.plugins.postgresql.ast.PostgreSqlAstScanner;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;
import org.sonar.plugins.postgresql.checks.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgreSqlSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreSqlSensor.class);
    private final PostgreSqlAstScanner scanner = new PostgreSqlAstScanner();

    private static final Map<String, Class<? extends PostgreSqlCheck>> RULE_MAP = new HashMap<>();

    static {
        RULE_MAP.put(AvoidSelectStarCheck.RULE_KEY, AvoidSelectStarCheck.class);
        RULE_MAP.put(AvoidCursorCheck.RULE_KEY, AvoidCursorCheck.class);
        RULE_MAP.put(MissingSemicolonCheck.RULE_KEY, MissingSemicolonCheck.class);
        RULE_MAP.put(DynamicSqlInjectionCheck.RULE_KEY, DynamicSqlInjectionCheck.class);
        RULE_MAP.put(AvoidOrderByOrdinalCheck.RULE_KEY, AvoidOrderByOrdinalCheck.class);
        RULE_MAP.put(TableWithoutPrimaryKeyCheck.RULE_KEY, TableWithoutPrimaryKeyCheck.class);
        RULE_MAP.put(UpperKeywordsCheck.RULE_KEY, UpperKeywordsCheck.class);
        RULE_MAP.put(MissingIndexOnForeignKeyCheck.RULE_KEY, MissingIndexOnForeignKeyCheck.class);
        RULE_MAP.put(AvoidSerialDataTypeCheck.RULE_KEY, AvoidSerialDataTypeCheck.class);
        RULE_MAP.put(UnloggedTableUsageCheck.RULE_KEY, UnloggedTableUsageCheck.class);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("PostgreSQL Community Sensor")
                .onlyOnLanguage(PostgreSqlLanguage.KEY)
                .createIssuesForRuleRepositories(PostgreSqlRulesDefinition.REPOSITORY_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fileSystem = context.fileSystem();
        FilePredicates predicates = fileSystem.predicates();
        Iterable<InputFile> inputFiles = fileSystem.inputFiles(predicates.hasLanguage(PostgreSqlLanguage.KEY));

        List<PostgreSqlCheck> activeChecks = instantiateActiveChecks(context);
        LOG.info("Executing PostgreSQL Community Sensor with {} active rules", activeChecks.size());

        int analyzedFilesCount = 0;
        for (InputFile inputFile : inputFiles) {
            try {
                analyzeFile(inputFile, context, activeChecks);
                analyzedFilesCount++;
            } catch (Exception e) {
                LOG.error("Failed to analyze PostgreSQL file: " + inputFile.uri(), e);
            }
        }

        LOG.info("PostgreSQL analysis finished. Analyzed {} files.", analyzedFilesCount);
    }

    private List<PostgreSqlCheck> instantiateActiveChecks(SensorContext context) {
        List<PostgreSqlCheck> checks = new ArrayList<>();
        for (Map.Entry<String, Class<? extends PostgreSqlCheck>> entry : RULE_MAP.entrySet()) {
            RuleKey ruleKey = RuleKey.of(PostgreSqlRulesDefinition.REPOSITORY_KEY, entry.getKey());
            if (context.activeRules().find(ruleKey) != null) {
                try {
                    checks.add(entry.getValue().getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    LOG.error("Unable to instantiate check for rule " + ruleKey, e);
                }
            }
        }
        return checks;
    }

    private void analyzeFile(InputFile inputFile, SensorContext context, List<PostgreSqlCheck> checks) throws IOException {
        String content = inputFile.contents();
        PostgreSqlAstScanner.ScanResult scanResult = scanner.parse(content);

        // Record metrics
        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.NCLOC)
                .on(inputFile)
                .withValue(scanResult.getNcloc())
                .save();

        context.<Integer>newMeasure()
                .forMetric(CoreMetrics.COMMENT_LINES)
                .on(inputFile)
                .withValue(scanResult.getCommentLines())
                .save();

        if (checks.isEmpty()) {
            return;
        }

        scanner.executeRules(scanResult, content, checks);

        for (PostgreSqlCheck check : checks) {
            String ruleKeyStr = getRuleKey(check);
            if (ruleKeyStr == null) {
                continue;
            }
            RuleKey ruleKey = RuleKey.of(PostgreSqlRulesDefinition.REPOSITORY_KEY, ruleKeyStr);

            for (IssueLocation issue : check.getIssues()) {
                reportIssue(inputFile, context, ruleKey, issue);
            }
        }
    }

    private String getRuleKey(PostgreSqlCheck check) {
        for (Map.Entry<String, Class<? extends PostgreSqlCheck>> entry : RULE_MAP.entrySet()) {
            if (entry.getValue().isInstance(check)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void reportIssue(InputFile inputFile, SensorContext context, RuleKey ruleKey, IssueLocation issue) {
        try {
            NewIssue newIssue = context.newIssue().forRule(ruleKey);
            NewIssueLocation location = newIssue.newLocation()
                    .on(inputFile)
                    .message(issue.getMessage());

            if (issue.getStartLine() > 0 && issue.getEndLine() > 0) {
                int startLine = Math.min(issue.getStartLine(), inputFile.lines());
                int endLine = Math.min(issue.getEndLine(), inputFile.lines());
                try {
                    TextRange textRange = inputFile.newRange(startLine, issue.getStartLineOffset(), endLine, issue.getEndLineOffset());
                    location.at(textRange);
                } catch (Exception e) {
                    LOG.debug("Failed to create TextRange for issue at line " + startLine, e);
                }
            }

            newIssue.at(location).save();
        } catch (Exception e) {
            LOG.error("Failed to report issue", e);
        }
    }
}
