package org.sonar.plugins.postgresql;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.postgresql.ast.IssueLocation;
import org.sonar.plugins.postgresql.ast.PostgreSqlAstScanner;
import org.sonar.plugins.postgresql.ast.PostgreSqlCheck;
import org.sonar.plugins.postgresql.checks.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostgreSqlChecksTest {

    private final PostgreSqlAstScanner scanner = new PostgreSqlAstScanner();

    private List<IssueLocation> scanWithCheck(String sql, PostgreSqlCheck check) {
        PostgreSqlAstScanner.ScanResult result = scanner.parse(sql);
        scanner.executeRules(result, sql, Collections.singletonList(check));
        return check.getIssues();
    }

    @Test
    public void testAvoidSelectStar() {
        AvoidSelectStarCheck check = new AvoidSelectStarCheck();
        String content = "SELECT * FROM users;\nSELECT u.* FROM users u;";
        assertThat(scanWithCheck(content, check)).hasSize(2);
        assertThat(check.getIssues().get(0).getMessage())
                .contains("Avoid using 'SELECT *'");
    }

    @Test
    public void testAvoidSelectStarNegative() {
        AvoidSelectStarCheck check = new AvoidSelectStarCheck();
        String content = "SELECT id, name FROM users;";
        assertThat(scanWithCheck(content, check)).isEmpty();
    }

    @Test
    public void testAvoidCursor() {
        AvoidCursorCheck check = new AvoidCursorCheck();
        String content = "DECLARE user_cursor CURSOR FOR SELECT id FROM users;";
        assertThat(scanWithCheck(content, check)).hasSize(1);
        assertThat(check.getIssues().get(0).getMessage())
                .contains("Avoid procedural CURSORs");
    }

    @Test
    public void testMissingSemicolon() {
        MissingSemicolonCheck check = new MissingSemicolonCheck();
        String content = "SELECT id, name FROM users\nINSERT INTO logs (id) VALUES (1);";
        assertThat(scanWithCheck(content, check)).hasSize(1);
    }

    @Test
    public void testDynamicSqlInjection() {
        DynamicSqlInjectionCheck check = new DynamicSqlInjectionCheck();
        String content = "EXECUTE 'SELECT * FROM users WHERE id = ' || id;";
        assertThat(scanWithCheck(content, check)).hasSize(1);
        assertThat(check.getIssues().get(0).getMessage())
                .contains("SQL injection");
    }

    @Test
    public void testAvoidOrderByOrdinal() {
        AvoidOrderByOrdinalCheck check = new AvoidOrderByOrdinalCheck();
        String content = "SELECT id, name FROM users ORDER BY 1, 2;";
        assertThat(scanWithCheck(content, check)).hasSize(2);
        assertThat(check.getIssues().get(0).getMessage())
                .contains("ordinal column numbers");
    }

    @Test
    public void testTableWithoutPrimaryKey() {
        TableWithoutPrimaryKeyCheck check = new TableWithoutPrimaryKeyCheck();
        String content = "CREATE TABLE users (\n  id INT,\n  name VARCHAR(100)\n);";
        assertThat(scanWithCheck(content, check)).hasSize(1);
        assertThat(check.getIssues().get(0).getMessage())
                .contains("PRIMARY KEY");
    }

    @Test
    public void testTableWithPrimaryKey() {
        TableWithoutPrimaryKeyCheck check = new TableWithoutPrimaryKeyCheck();
        String content = "CREATE TABLE users (\n  id INT PRIMARY KEY,\n  name VARCHAR(100)\n);";
        assertThat(scanWithCheck(content, check)).isEmpty();

        check = new TableWithoutPrimaryKeyCheck();
        String contentConstraint = "CREATE TABLE users (\n  id INT,\n  name VARCHAR(100),\n  PRIMARY KEY (id)\n);";
        assertThat(scanWithCheck(contentConstraint, check)).isEmpty();
    }

    @Test
    public void testAvoidSerialDataType() {
        AvoidSerialDataTypeCheck check = new AvoidSerialDataTypeCheck();
        String content = "CREATE TABLE users (\n  id SERIAL PRIMARY KEY,\n  name VARCHAR(100)\n);";
        assertThat(scanWithCheck(content, check)).hasSize(1);
        assertThat(check.getIssues().get(0).getMessage())
                .contains("SERIAL data type");
    }

    @Test
    public void testUnloggedTableUsage() {
        UnloggedTableUsageCheck check = new UnloggedTableUsageCheck();
        String content = "CREATE UNLOGGED TABLE temp_users (\n  id INT PRIMARY KEY\n);";
        assertThat(scanWithCheck(content, check)).hasSize(1);
        assertThat(check.getIssues().get(0).getMessage())
                .contains("UNLOGGED");
    }

    @Test
    public void testDmlWithoutWhere() {
        DmlWithoutWhereCheck check = new DmlWithoutWhereCheck();
        String content = "UPDATE users SET active = false;\nDELETE FROM audit_log;";
        assertThat(scanWithCheck(content, check)).hasSize(2);

        check = new DmlWithoutWhereCheck();
        content = "UPDATE users SET active = false WHERE id = 1;\nDELETE FROM audit_log WHERE id = 1;";
        assertThat(scanWithCheck(content, check)).isEmpty();
    }

    @Test
    public void testLimitWithoutOrderBy() {
        LimitWithoutOrderByCheck check = new LimitWithoutOrderByCheck();
        assertThat(scanWithCheck("SELECT id FROM users LIMIT 10;", check)).hasSize(1);

        check = new LimitWithoutOrderByCheck();
        assertThat(scanWithCheck("SELECT id FROM users ORDER BY id LIMIT 10;", check)).isEmpty();
    }

    @Test
    public void testUpperKeywords() {
        UpperKeywordsCheck check = new UpperKeywordsCheck();
        String nonCompliant = "select id, name from users;";
        assertThat(scanWithCheck(nonCompliant, check)).isNotEmpty();

        check = new UpperKeywordsCheck();
        String compliant = "SELECT id, name FROM users;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    public void testMissingIndexOnForeignKey() {
        MissingIndexOnForeignKeyCheck check = new MissingIndexOnForeignKeyCheck();
        String content = "CREATE TABLE orders (\n  id INT PRIMARY KEY,\n  user_id INT,\n  FOREIGN KEY (user_id) REFERENCES users (id)\n);";
        assertThat(scanWithCheck(content, check)).hasSize(1);
    }

    @Test
    public void testPostgreSqlWindowFunctions() {
        String sql = "SELECT emp_name, salary,\n" +
                "  ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rank_in_dept,\n" +
                "  SUM(salary) OVER (PARTITION BY dept_id ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_total\n" +
                "FROM employees;";
        PostgreSqlAstScanner.ScanResult result = scanner.parse(sql);
        assertThat(result.hasSyntaxErrors()).isFalse();
        assertThat(result.getParseTree()).isNotNull();

        UpperKeywordsCheck upperCheck = new UpperKeywordsCheck();
        scanner.executeRules(result, sql, Collections.singletonList(upperCheck));
        assertThat(upperCheck.getIssues()).isEmpty();
    }

    @Test
    public void testPostgreSqlCommonTableExpressions() {
        String sql = "WITH RECURSIVE subordinates AS (\n" +
                "  SELECT employee_id, manager_id, full_name FROM employees WHERE manager_id IS NULL\n" +
                "  UNION ALL\n" +
                "  SELECT e.employee_id, e.manager_id, e.full_name FROM employees e\n" +
                "  INNER JOIN subordinates s ON e.manager_id = s.employee_id\n" +
                ")\n" +
                "SELECT employee_id, manager_id, full_name FROM subordinates;";
        PostgreSqlAstScanner.ScanResult result = scanner.parse(sql);
        assertThat(result.hasSyntaxErrors()).isFalse();
        assertThat(result.getParseTree()).isNotNull();

        AvoidSelectStarCheck selectStarCheck = new AvoidSelectStarCheck();
        scanner.executeRules(result, sql, Collections.singletonList(selectStarCheck));
        assertThat(selectStarCheck.getIssues()).isEmpty();
    }

    @Test
    public void testPostgreSqlArrays() {
        String ddlSql = "CREATE TABLE article (\n" +
                "  id INT PRIMARY KEY,\n" +
                "  tags TEXT[],\n" +
                "  ratings INT[]\n" +
                ");";
        PostgreSqlAstScanner.ScanResult ddlResult = scanner.parse(ddlSql);
        assertThat(ddlResult.hasSyntaxErrors()).isFalse();

        String querySql = "SELECT tags[1] AS first_tag, ARRAY[1, 2, 3] AS nums\n" +
                "FROM article\n" +
                "WHERE 'postgres' = ANY(tags) AND ratings @> ARRAY[5];";
        PostgreSqlAstScanner.ScanResult queryResult = scanner.parse(querySql);
        assertThat(queryResult.hasSyntaxErrors()).isFalse();
    }
}
