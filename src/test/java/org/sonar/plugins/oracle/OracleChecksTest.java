package org.sonar.plugins.oracle;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.oracle.ast.OracleAstScanner;
import org.sonar.plugins.oracle.ast.OracleCheck;
import org.sonar.plugins.oracle.checks.AvoidSelectStarCheck;
import org.sonar.plugins.oracle.checks.DmlWithoutWhereCheck;
import org.sonar.plugins.oracle.checks.TableWithoutPrimaryKeyCheck;
import org.sonar.plugins.oracle.checks.UpperKeywordsCheck;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class OracleChecksTest {

    private final OracleAstScanner scanner = new OracleAstScanner();

    private OracleAstScanner.ScanResult scan(String sql, OracleCheck check) {
        OracleAstScanner.ScanResult result = scanner.parse(sql);
        scanner.executeRules(result, sql, Collections.singletonList(check));
        return result;
    }

    @Test
    void parsesOracleSqlAndPlsql() {
        String sql = "CREATE TABLE employees (id NUMBER PRIMARY KEY, name VARCHAR2(100));\n"
                + "DECLARE v_count NUMBER; BEGIN SELECT COUNT(*) INTO v_count FROM employees; END; /\n"
                + "CREATE OR REPLACE PROCEDURE refresh_stats AS BEGIN NULL; END; /";

        OracleAstScanner.ScanResult result = scanner.parse(sql);

        assertThat(result.hasSyntaxErrors()).isFalse();
        assertThat(result.getParseTree()).isNotNull();
    }

    @Test
    void detectsSelectStar() {
        AvoidSelectStarCheck check = new AvoidSelectStarCheck();
        OracleAstScanner.ScanResult result = scan("SELECT * FROM employees;", check);

        assertThat(result.hasSyntaxErrors()).isFalse();
        assertThat(result.getParseTree()).isNotNull();
        assertThat(check.getIssues()).hasSize(1);
    }

    @Test
    void detectsTableWithoutPrimaryKey() {
        TableWithoutPrimaryKeyCheck check = new TableWithoutPrimaryKeyCheck();
        scan("CREATE TABLE employees (id NUMBER, name VARCHAR2(100));", check);

        assertThat(check.getIssues()).hasSize(1);
        assertThat(check.getIssues().get(0).getMessage()).contains("PRIMARY KEY");
    }

    @Test
    void acceptsTableWithPrimaryKey() {
        TableWithoutPrimaryKeyCheck check = new TableWithoutPrimaryKeyCheck();
        scan("CREATE TABLE employees (id NUMBER PRIMARY KEY, name VARCHAR2(100));", check);

        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsLowercaseOracleKeywords() {
        UpperKeywordsCheck check = new UpperKeywordsCheck();
        scan("select id from employees;", check);

        assertThat(check.getIssues()).isNotEmpty();
    }

    @Test
    void detectsDmlWithoutWhere() {
        DmlWithoutWhereCheck check = new DmlWithoutWhereCheck();
        scan("UPDATE employees SET active = 0; DELETE FROM audit_log;", check);

        assertThat(check.getIssues()).hasSize(2);

        check = new DmlWithoutWhereCheck();
        scan("UPDATE employees SET active = 0 WHERE employee_id = 1; DELETE FROM audit_log WHERE id = 1;", check);
        assertThat(check.getIssues()).isEmpty();
    }
}
