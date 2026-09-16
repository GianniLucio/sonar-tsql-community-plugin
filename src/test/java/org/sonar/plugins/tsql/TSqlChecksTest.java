package org.sonar.plugins.tsql;

import org.junit.jupiter.api.Test;
import org.sonar.plugins.tsql.ast.TSqlAstScanner;
import org.sonar.plugins.tsql.ast.TSqlCheck;
import org.sonar.plugins.tsql.checks.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TSqlChecksTest {

    private final TSqlAstScanner scanner = new TSqlAstScanner();

    private List<org.sonar.plugins.tsql.ast.IssueLocation> scanWithCheck(String sql, TSqlCheck check) {
        TSqlAstScanner.ScanResult result = scanner.parse(sql);
        scanner.executeRules(result, sql, Collections.singletonList(check));
        return check.getIssues();
    }

    @Test
    void testAvoidSelectStar() {
        AvoidSelectStarCheck check = new AvoidSelectStarCheck();
        String nonCompliant = "SELECT * FROM dbo.Users;\nSELECT u.* FROM dbo.Users u;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(2);

        check = new AvoidSelectStarCheck();
        String compliant = "SELECT UserID, UserName FROM dbo.Users;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testAvoidNoLock() {
        AvoidNoLockCheck check = new AvoidNoLockCheck();
        String nonCompliant = "SELECT OrderID FROM dbo.Orders WITH (NOLOCK);\nSELECT ItemID FROM dbo.Items WITH (READUNCOMMITTED);";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(2);

        check = new AvoidNoLockCheck();
        String compliant = "SELECT OrderID FROM dbo.Orders;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testAvoidSpPrefix() {
        AvoidSpPrefixCheck check = new AvoidSpPrefixCheck();
        String nonCompliant = "CREATE PROCEDURE dbo.sp_GetReport AS SELECT 1;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new AvoidSpPrefixCheck();
        String compliant = "CREATE PROCEDURE dbo.usp_GetReport AS SELECT 1;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testMissingSemicolon() {
        MissingSemicolonCheck check = new MissingSemicolonCheck();
        String nonCompliant = "SELECT 1\nSELECT 2;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new MissingSemicolonCheck();
        String compliant = "SELECT 1;\nSELECT 2;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testAvoidCursor() {
        AvoidCursorCheck check = new AvoidCursorCheck();
        String nonCompliant = "DECLARE cur CURSOR FOR SELECT ID FROM dbo.T;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new AvoidCursorCheck();
        String compliant = "SELECT ID FROM dbo.T;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testTransactionXactAbort() {
        TransactionXactAbortCheck check = new TransactionXactAbortCheck();
        String nonCompliant = "BEGIN TRANSACTION;\nCOMMIT TRANSACTION;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new TransactionXactAbortCheck();
        String compliantWithSet = "SET XACT_ABORT ON;\nBEGIN TRANSACTION;\nCOMMIT TRANSACTION;";
        assertThat(scanWithCheck(compliantWithSet, check)).isEmpty();

        check = new TransactionXactAbortCheck();
        String compliantWithTryCatch = "BEGIN TRY\nBEGIN TRANSACTION;\nCOMMIT TRANSACTION;\nEND TRY\nBEGIN CATCH\nROLLBACK TRANSACTION;\nEND CATCH;";
        assertThat(scanWithCheck(compliantWithTryCatch, check)).isEmpty();
    }

    @Test
    void testDynamicSqlInjection() {
        DynamicSqlInjectionCheck check = new DynamicSqlInjectionCheck();
        String nonCompliant = "EXEC ('SELECT * FROM dbo.T WHERE id = ' + @id);";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new DynamicSqlInjectionCheck();
        String compliant = "EXEC dbo.usp_MyProc @id = 1;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testAvoidOrderByOrdinal() {
        AvoidOrderByOrdinalCheck check = new AvoidOrderByOrdinalCheck();
        String nonCompliant = "SELECT Col1, Col2 FROM dbo.T ORDER BY 1, 2;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(2);

        check = new AvoidOrderByOrdinalCheck();
        String compliant = "SELECT Col1, Col2 FROM dbo.T ORDER BY Col1, Col2;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testTableWithoutPrimaryKey() {
        TableWithoutPrimaryKeyCheck check = new TableWithoutPrimaryKeyCheck();
        String nonCompliant = "CREATE TABLE dbo.Test (ID INT, Name VARCHAR(50));";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new TableWithoutPrimaryKeyCheck();
        String compliantCol = "CREATE TABLE dbo.Test (ID INT PRIMARY KEY, Name VARCHAR(50));";
        assertThat(scanWithCheck(compliantCol, check)).isEmpty();

        check = new TableWithoutPrimaryKeyCheck();
        String compliantConstraint = "CREATE TABLE dbo.Test (ID INT, CONSTRAINT PK_Test PRIMARY KEY (ID));";
        assertThat(scanWithCheck(compliantConstraint, check)).isEmpty();
    }

    @Test
    void testUpperKeywords() {
        UpperKeywordsCheck check = new UpperKeywordsCheck();
        String nonCompliant = "select Col1 from dbo.T;";
        assertThat(scanWithCheck(nonCompliant, check)).isNotEmpty();

        check = new UpperKeywordsCheck();
        String compliant = "SELECT Col1 FROM dbo.T;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }
}
