package org.sonar.plugins.oracle;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class OracleRulesDefinitionTest {

    @Test
    void definesOracleRules() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        new OracleRulesDefinition().define(context);

        RulesDefinition.Repository repository = context.repository(OracleRulesDefinition.REPOSITORY_KEY);
        assertThat(repository).isNotNull();
        assertThat(repository.key()).isEqualTo("oraclecommunity");
        assertThat(repository.name()).isEqualTo("Oracle Community Rules");
        assertThat(repository.rules()).hasSize(20);
    }
}