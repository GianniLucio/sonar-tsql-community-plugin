package org.sonar.plugins.tsql;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.tsql.sensor.TSqlSensor;
import org.sonar.plugins.postgresql.PostgreSqlLanguage;
import org.sonar.plugins.postgresql.PostgreSqlRulesDefinition;
import org.sonar.plugins.postgresql.PostgreSqlQualityProfile;
import org.sonar.plugins.postgresql.sensor.PostgreSqlSensor;
import org.sonar.plugins.oracle.OracleLanguage;
import org.sonar.plugins.oracle.OracleQualityProfile;
import org.sonar.plugins.oracle.OracleRulesDefinition;
import org.sonar.plugins.oracle.sensor.OracleSensor;

public class TSqlPlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtensions(
                // T-SQL Language
                TSqlLanguage.class,

                // T-SQL Rules and Profiles
                TSqlRulesDefinition.class,
                TSqlQualityProfile.class,

                // T-SQL Sensor
                TSqlSensor.class,

                // PostgreSQL Language
                PostgreSqlLanguage.class,

                // PostgreSQL Rules and Profiles
                PostgreSqlRulesDefinition.class,
                PostgreSqlQualityProfile.class,

                // PostgreSQL Sensor
                PostgreSqlSensor.class,

                // Oracle Language
                OracleLanguage.class,

                // Oracle Rules and Profile
                OracleRulesDefinition.class,
                OracleQualityProfile.class,

                // Oracle Sensor
                OracleSensor.class,

                // Configuration Properties - T-SQL
                PropertyDefinition.builder(TSqlLanguage.FILE_SUFFIXES_KEY)
                        .name("T-SQL File Suffixes")
                        .description("List of file suffixes to analyze as T-SQL.")
                        .category("T-SQL")
                        .defaultValue(TSqlLanguage.DEFAULT_FILE_SUFFIXES)
                        .onQualifiers(Qualifiers.PROJECT)
                        .type(PropertyType.STRING)
                        .multiValues(true)
                        .build(),

                // Configuration Properties - PostgreSQL
                PropertyDefinition.builder(PostgreSqlLanguage.FILE_SUFFIXES_KEY)
                        .name("PostgreSQL File Suffixes")
                        .description("List of file suffixes to analyze as PostgreSQL.")
                        .category("PostgreSQL")
                        .defaultValue(PostgreSqlLanguage.DEFAULT_FILE_SUFFIXES)
                        .onQualifiers(Qualifiers.PROJECT)
                        .type(PropertyType.STRING)
                        .multiValues(true)
                        .build(),

                    // Configuration Properties - Oracle
                    PropertyDefinition.builder(OracleLanguage.FILE_SUFFIXES_KEY)
                        .name("Oracle File Suffixes")
                        .description("List of file suffixes to analyze as Oracle SQL and PL/SQL.")
                        .category("Oracle")
                        .defaultValue(OracleLanguage.DEFAULT_FILE_SUFFIXES)
                        .onQualifiers(Qualifiers.PROJECT)
                        .type(PropertyType.STRING)
                        .multiValues(true)
                        .build()
        );
    }
}
