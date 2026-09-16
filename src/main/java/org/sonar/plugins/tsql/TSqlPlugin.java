package org.sonar.plugins.tsql;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.tsql.sensor.TSqlSensor;

public class TSqlPlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtensions(
                // Language
                TSqlLanguage.class,

                // Rules and Profiles
                TSqlRulesDefinition.class,
                TSqlQualityProfile.class,

                // Sensor
                TSqlSensor.class,

                // Configuration Properties
                PropertyDefinition.builder(TSqlLanguage.FILE_SUFFIXES_KEY)
                        .name("File Suffixes")
                        .description("List of file suffixes to analyze as T-SQL.")
                        .category("T-SQL")
                        .defaultValue(TSqlLanguage.DEFAULT_FILE_SUFFIXES)
                        .onQualifiers(Qualifiers.PROJECT)
                        .type(PropertyType.STRING)
                        .multiValues(true)
                        .build()
        );
    }
}
