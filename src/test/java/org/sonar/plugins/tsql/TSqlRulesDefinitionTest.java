package org.sonar.plugins.tsql;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class TSqlRulesDefinitionTest {

    @Test
    void testRulesDefinition() {
        TSqlRulesDefinition rulesDefinition = new TSqlRulesDefinition();
        RulesDefinition.Context context = new RulesDefinition.Context();
        rulesDefinition.define(context);

        RulesDefinition.Repository repo = context.repository(TSqlRulesDefinition.REPOSITORY_KEY);
        assertThat(repo).isNotNull();
        assertThat(repo.name()).isEqualTo(TSqlRulesDefinition.REPOSITORY_NAME);
        assertThat(repo.language()).isEqualTo(TSqlLanguage.KEY);
        assertThat(repo.rules()).hasSize(10);

        RulesDefinition.Rule selectStarRule = repo.rule("S101_AvoidSelectStar");
        assertThat(selectStarRule).isNotNull();
        assertThat(selectStarRule.htmlDescription()).contains("SELECT *");
        assertThat(selectStarRule.tags()).contains("performance", "bad-practice");
    }
}
