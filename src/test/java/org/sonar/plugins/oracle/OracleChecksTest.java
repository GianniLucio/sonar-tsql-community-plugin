package org.sonar.plugins.oracle;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.oracle.ast.OracleAstScanner;
import org.sonar.plugins.oracle.ast.OracleCheck;
import org.sonar.plugins.oracle.checks.AlterTableDropColumnCheck;
import org.sonar.plugins.oracle.checks.AnonymousBlockWithoutExceptionHandlingCheck;
import org.sonar.plugins.oracle.checks.AvoidSelectStarCheck;
import org.sonar.plugins.oracle.checks.CheckConstraintNullComparisonCheck;
import org.sonar.plugins.oracle.checks.CommitInProgramUnitCheck;
import org.sonar.plugins.oracle.checks.ConnectByWithoutPriorCheck;
import org.sonar.plugins.oracle.checks.CreateSequenceWithoutCacheOrderCheck;
import org.sonar.plugins.oracle.checks.DmlWithoutWhereCheck;
import org.sonar.plugins.oracle.checks.HavingWithoutGroupByCheck;
import org.sonar.plugins.oracle.checks.InsertWithoutColumnListCheck;
import org.sonar.plugins.oracle.checks.MergeWithoutNotMatchedCheck;
import org.sonar.plugins.oracle.checks.ProcedureWithoutExceptionHandlingCheck;
import org.sonar.plugins.oracle.checks.ProgramUnitWithoutOrReplaceCheck;
import org.sonar.plugins.oracle.checks.RollbackInProgramUnitCheck;
import org.sonar.plugins.oracle.checks.RownumWithoutOrderByCheck;
import org.sonar.plugins.oracle.checks.TableWithoutPrimaryKeyCheck;
import org.sonar.plugins.oracle.checks.UnnamedConstraintCheck;
import org.sonar.plugins.oracle.checks.UpperKeywordsCheck;
import org.sonar.plugins.oracle.checks.Varchar2WithoutSizeCheck;
import org.sonar.plugins.oracle.checks.WhenOthersWithoutRaiseCheck;

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

    @Test
    void detectsCommitInProgramUnit() {
        CommitInProgramUnitCheck check = new CommitInProgramUnitCheck();
        scan("CREATE OR REPLACE PROCEDURE update_balance AS BEGIN UPDATE accounts SET balance = 0; COMMIT; END; /", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new CommitInProgramUnitCheck();
        scan("CREATE OR REPLACE PROCEDURE update_balance AS BEGIN UPDATE accounts SET balance = 0; END; /", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsWhenOthersWithoutRaise() {
        WhenOthersWithoutRaiseCheck check = new WhenOthersWithoutRaiseCheck();
        scan("CREATE OR REPLACE PROCEDURE p AS BEGIN NULL; EXCEPTION WHEN OTHERS THEN NULL; END; /", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new WhenOthersWithoutRaiseCheck();
        scan("CREATE OR REPLACE PROCEDURE p AS BEGIN NULL; EXCEPTION WHEN OTHERS THEN RAISE; END; /", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsRownumWithoutOrderBy() {
        RownumWithoutOrderByCheck check = new RownumWithoutOrderByCheck();
        scan("SELECT id FROM employees WHERE ROWNUM <= 10;", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new RownumWithoutOrderByCheck();
        scan("SELECT id FROM employees WHERE ROWNUM <= 10 ORDER BY id;", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsVarchar2WithoutSize() {
        Varchar2WithoutSizeCheck check = new Varchar2WithoutSizeCheck();
        scan("CREATE TABLE employees (id NUMBER PRIMARY KEY, name VARCHAR2);", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new Varchar2WithoutSizeCheck();
        scan("CREATE TABLE employees (id NUMBER PRIMARY KEY, name VARCHAR2(100));", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsUnnamedConstraint() {
        UnnamedConstraintCheck check = new UnnamedConstraintCheck();
        scan("CREATE TABLE employees (id NUMBER, PRIMARY KEY (id));", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new UnnamedConstraintCheck();
        scan("CREATE TABLE employees (id NUMBER, CONSTRAINT pk_emp PRIMARY KEY (id));", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsProcedureWithoutExceptionHandling() {
        ProcedureWithoutExceptionHandlingCheck check = new ProcedureWithoutExceptionHandlingCheck();
        scan("CREATE OR REPLACE PROCEDURE update_balance AS BEGIN UPDATE accounts SET balance = 0; END; /", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new ProcedureWithoutExceptionHandlingCheck();
        scan("CREATE OR REPLACE PROCEDURE update_balance AS BEGIN UPDATE accounts SET balance = 0; EXCEPTION WHEN OTHERS THEN RAISE; END; /", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsInsertWithoutColumnList() {
        InsertWithoutColumnListCheck check = new InsertWithoutColumnListCheck();
        scan("INSERT INTO employees VALUES (1, 'John');", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new InsertWithoutColumnListCheck();
        scan("INSERT INTO employees (id, name) VALUES (1, 'John');", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsConnectByWithoutPrior() {
        ConnectByWithoutPriorCheck check = new ConnectByWithoutPriorCheck();
        scan("SELECT id FROM employees CONNECT BY id = manager_id;", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new ConnectByWithoutPriorCheck();
        scan("SELECT id FROM employees CONNECT BY PRIOR manager_id = id;", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsCreateSequenceWithoutCacheOrder() {
        CreateSequenceWithoutCacheOrderCheck check = new CreateSequenceWithoutCacheOrderCheck();
        scan("CREATE SEQUENCE order_seq;", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new CreateSequenceWithoutCacheOrderCheck();
        scan("CREATE SEQUENCE order_seq CACHE 20 ORDER;", check);
        assertThat(check.getIssues()).isEmpty();

        check = new CreateSequenceWithoutCacheOrderCheck();
        scan("CREATE SEQUENCE order_seq NOCACHE NOORDER;", check);
        assertThat(check.getIssues()).isEmpty();

        check = new CreateSequenceWithoutCacheOrderCheck();
        scan("CREATE SEQUENCE order_seq CACHE 20;", check);
        assertThat(check.getIssues()).hasSize(1);
    }

    @Test
    void detectsMergeWithoutNotMatched() {
        MergeWithoutNotMatchedCheck check = new MergeWithoutNotMatchedCheck();
        scan("MERGE INTO accounts USING staging s ON (accounts.id = s.id) WHEN MATCHED THEN UPDATE SET accounts.balance = s.balance;", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new MergeWithoutNotMatchedCheck();
        scan("MERGE INTO accounts USING staging s ON (accounts.id = s.id) WHEN MATCHED THEN UPDATE SET accounts.balance = s.balance "
                + "WHEN NOT MATCHED THEN INSERT (id, balance) VALUES (s.id, s.balance);", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsAnonymousBlockWithoutExceptionHandling() {
        AnonymousBlockWithoutExceptionHandlingCheck check = new AnonymousBlockWithoutExceptionHandlingCheck();
        scan("BEGIN NULL; END; /", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new AnonymousBlockWithoutExceptionHandlingCheck();
        scan("BEGIN NULL; EXCEPTION WHEN OTHERS THEN RAISE; END; /", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsAlterTableDropColumn() {
        AlterTableDropColumnCheck check = new AlterTableDropColumnCheck();
        scan("ALTER TABLE employees DROP COLUMN legacy_code;", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new AlterTableDropColumnCheck();
        scan("ALTER TABLE employees MODIFY name VARCHAR2(200);", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsHavingWithoutGroupBy() {
        HavingWithoutGroupByCheck check = new HavingWithoutGroupByCheck();
        scan("SELECT salary FROM employees HAVING salary > 1000;", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new HavingWithoutGroupByCheck();
        scan("SELECT department, salary FROM employees GROUP BY department, salary HAVING salary > 1000;", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsProgramUnitWithoutOrReplace() {
        ProgramUnitWithoutOrReplaceCheck check = new ProgramUnitWithoutOrReplaceCheck();
        scan("CREATE PROCEDURE refresh_stats AS BEGIN NULL; END; /", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new ProgramUnitWithoutOrReplaceCheck();
        scan("CREATE OR REPLACE PROCEDURE refresh_stats AS BEGIN NULL; END; /", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsRollbackInProgramUnit() {
        RollbackInProgramUnitCheck check = new RollbackInProgramUnitCheck();
        scan("CREATE OR REPLACE PROCEDURE update_balance AS BEGIN UPDATE accounts SET balance = 0; ROLLBACK; END; /", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new RollbackInProgramUnitCheck();
        scan("CREATE OR REPLACE PROCEDURE update_balance AS BEGIN UPDATE accounts SET balance = 0; END; /", check);
        assertThat(check.getIssues()).isEmpty();
    }

    @Test
    void detectsCheckConstraintNullComparison() {
        CheckConstraintNullComparisonCheck check = new CheckConstraintNullComparisonCheck();
        scan("CREATE TABLE employees (id NUMBER PRIMARY KEY, status VARCHAR2(20) CHECK (status = NULL));", check);

        assertThat(check.getIssues()).hasSize(1);

        check = new CheckConstraintNullComparisonCheck();
        scan("CREATE TABLE employees (id NUMBER, status VARCHAR2(20), CONSTRAINT chk_status CHECK (status = NULL));", check);
        assertThat(check.getIssues()).hasSize(1);

        check = new CheckConstraintNullComparisonCheck();
        scan("CREATE TABLE employees (id NUMBER PRIMARY KEY, status VARCHAR2(20) CHECK (status = 'ACTIVE'));", check);
        assertThat(check.getIssues()).isEmpty();
    }
}
