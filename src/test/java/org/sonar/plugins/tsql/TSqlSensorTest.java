package org.sonar.plugins.tsql;

import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextPointer;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.plugins.tsql.checks.AvoidSelectStarCheck;
import org.sonar.plugins.tsql.sensor.TSqlSensor;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TSqlSensorTest {

    @Test
    void testDescribe() {
        TSqlSensor sensor = new TSqlSensor();
        SensorDescriptor descriptor = mock(SensorDescriptor.class);
        when(descriptor.name(anyString())).thenReturn(descriptor);
        when(descriptor.onlyOnLanguage(anyString())).thenReturn(descriptor);
        when(descriptor.createIssuesForRuleRepositories(any(String[].class))).thenReturn(descriptor);

        sensor.describe(descriptor);

        verify(descriptor).name("T-SQL Community Sensor");
        verify(descriptor).onlyOnLanguage(TSqlLanguage.KEY);
        verify(descriptor).createIssuesForRuleRepositories(TSqlRulesDefinition.REPOSITORY_KEY);
    }

    @Test
    void testExecuteOnValidSql() throws IOException {
        TSqlSensor sensor = new TSqlSensor();
        SensorContext context = mock(SensorContext.class);
        FileSystem fs = mock(FileSystem.class);
        FilePredicates predicates = mock(FilePredicates.class);
        FilePredicate langPredicate = mock(FilePredicate.class);
        InputFile inputFile = mock(InputFile.class);
        ActiveRules activeRules = mock(ActiveRules.class);
        NewMeasure<Integer> measure = mock(NewMeasure.class);
        NewIssue newIssue = mock(NewIssue.class);
        NewIssueLocation issueLocation = mock(NewIssueLocation.class);
        TextRange textRange = mock(TextRange.class);

        when(context.fileSystem()).thenReturn(fs);
        when(fs.predicates()).thenReturn(predicates);
        when(predicates.hasLanguage(TSqlLanguage.KEY)).thenReturn(langPredicate);
        when(fs.inputFiles(langPredicate)).thenReturn(Collections.singletonList(inputFile));
        when(context.activeRules()).thenReturn(activeRules);
        when(context.<Integer>newMeasure()).thenReturn(measure);
        when(measure.forMetric(any())).thenReturn(measure);
        when(measure.on(any(InputFile.class))).thenReturn(measure);
        when(measure.withValue(anyInt())).thenReturn(measure);

        when(inputFile.uri()).thenReturn(URI.create("file:///test.sql"));
        when(inputFile.contents()).thenReturn("SELECT * FROM dbo.Users;\n");
        when(inputFile.lines()).thenReturn(2);
        when(inputFile.newRange(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(textRange);

        RuleKey selectStarKey = RuleKey.of(TSqlRulesDefinition.REPOSITORY_KEY, AvoidSelectStarCheck.RULE_KEY);
        when(activeRules.find(selectStarKey)).thenReturn(mock(org.sonar.api.batch.rule.ActiveRule.class));

        when(context.newIssue()).thenReturn(newIssue);
        when(newIssue.forRule(any())).thenReturn(newIssue);
        when(newIssue.newLocation()).thenReturn(issueLocation);
        when(issueLocation.on(any(InputFile.class))).thenReturn(issueLocation);
        when(issueLocation.message(anyString())).thenReturn(issueLocation);
        when(issueLocation.at(any(TextRange.class))).thenReturn(issueLocation);
        when(newIssue.at(any(NewIssueLocation.class))).thenReturn(newIssue);

        assertThatCode(() -> sensor.execute(context)).doesNotThrowAnyException();

        verify(context, atLeastOnce()).newIssue();
        verify(measure, atLeastOnce()).save();
    }
}
