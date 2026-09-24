package org.sonar.plugins.postgresql;

import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgreSqlRulesDefinitionTest {

    @Test
    public void testRulesDefinition() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        PostgreSqlRulesDefinition definition = new PostgreSqlRulesDefinition();
        definition.define(context);

        RulesDefinition.Repository repository = context.repository(PostgreSqlRulesDefinition.REPOSITORY_KEY);
        assertThat(repository).isNotNull();
        assertThat(repository.key()).isEqualTo("pgsqlcommunity");
        assertThat(repository.name()).isEqualTo("PostgreSQL Community Rules");
        assertThat(repository.rules()).hasSize(20);
    }
}
