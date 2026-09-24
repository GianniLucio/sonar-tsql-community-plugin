package org.sonar.plugins.oracle.sensor;

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
import org.sonar.plugins.oracle.OracleLanguage;
import org.sonar.plugins.oracle.OracleRulesDefinition;
import org.sonar.plugins.oracle.ast.IssueLocation;
import org.sonar.plugins.oracle.ast.OracleAstScanner;
import org.sonar.plugins.oracle.ast.OracleCheck;
import org.sonar.plugins.oracle.checks.AvoidSelectStarCheck;
import org.sonar.plugins.oracle.checks.TableWithoutPrimaryKeyCheck;
import org.sonar.plugins.oracle.checks.UpperKeywordsCheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OracleSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(OracleSensor.class);
    private final OracleAstScanner scanner = new OracleAstScanner();
    private static final Map<String, Class<? extends OracleCheck>> RULE_MAP = new HashMap<>();

    static {
        RULE_MAP.put(AvoidSelectStarCheck.RULE_KEY, AvoidSelectStarCheck.class);
        RULE_MAP.put(TableWithoutPrimaryKeyCheck.RULE_KEY, TableWithoutPrimaryKeyCheck.class);
        RULE_MAP.put(UpperKeywordsCheck.RULE_KEY, UpperKeywordsCheck.class);
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Oracle Community Sensor")
                .onlyOnLanguage(OracleLanguage.KEY)
                .createIssuesForRuleRepositories(OracleRulesDefinition.REPOSITORY_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fileSystem = context.fileSystem();
        FilePredicates predicates = fileSystem.predicates();
        Iterable<InputFile> inputFiles = fileSystem.inputFiles(predicates.hasLanguage(OracleLanguage.KEY));
        List<OracleCheck> activeChecks = instantiateActiveChecks(context);
        LOG.info("Executing Oracle Community Sensor with {} active rules", activeChecks.size());

        int analyzedFilesCount = 0;
        for (InputFile inputFile : inputFiles) {
            try {
                analyzeFile(inputFile, context, activeChecks);
                analyzedFilesCount++;
            } catch (Exception e) {
                LOG.error("Failed to analyze Oracle file: " + inputFile.uri(), e);
            }
        }
        LOG.info("Oracle analysis finished. Analyzed {} files.", analyzedFilesCount);
    }

    private List<OracleCheck> instantiateActiveChecks(SensorContext context) {
        List<OracleCheck> checks = new ArrayList<>();
        for (Map.Entry<String, Class<? extends OracleCheck>> entry : RULE_MAP.entrySet()) {
            RuleKey ruleKey = RuleKey.of(OracleRulesDefinition.REPOSITORY_KEY, entry.getKey());
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

    private void analyzeFile(InputFile inputFile, SensorContext context, List<OracleCheck> checks) throws IOException {
        String content = inputFile.contents();
        OracleAstScanner.ScanResult scanResult = scanner.parse(content);
        context.<Integer>newMeasure().forMetric(CoreMetrics.NCLOC).on(inputFile).withValue(scanResult.getNcloc()).save();
        context.<Integer>newMeasure().forMetric(CoreMetrics.COMMENT_LINES).on(inputFile).withValue(scanResult.getCommentLines()).save();
        if (checks.isEmpty()) {
            return;
        }

        scanner.executeRules(scanResult, content, checks);
        for (OracleCheck check : checks) {
            String ruleKey = getRuleKey(check);
            if (ruleKey == null) {
                continue;
            }
            for (IssueLocation issue : check.getIssues()) {
                reportIssue(inputFile, context, RuleKey.of(OracleRulesDefinition.REPOSITORY_KEY, ruleKey), issue);
            }
        }
    }

    private String getRuleKey(OracleCheck check) {
        for (Map.Entry<String, Class<? extends OracleCheck>> entry : RULE_MAP.entrySet()) {
            if (entry.getValue().isInstance(check)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void reportIssue(InputFile inputFile, SensorContext context, RuleKey ruleKey, IssueLocation issue) {
        try {
            NewIssue newIssue = context.newIssue().forRule(ruleKey);
            NewIssueLocation location = newIssue.newLocation().on(inputFile).message(issue.getMessage());
            if (issue.getStartLine() > 0 && issue.getEndLine() > 0) {
                int startLine = Math.min(issue.getStartLine(), inputFile.lines());
                int endLine = Math.min(issue.getEndLine(), inputFile.lines());
                try {
                    location.at(inputFile.newRange(startLine, issue.getStartLineOffset(), endLine, issue.getEndLineOffset()));
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
