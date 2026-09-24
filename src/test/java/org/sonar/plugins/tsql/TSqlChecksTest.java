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

    @Test
    void testDmlWithoutWhere() {
        DmlWithoutWhereCheck check = new DmlWithoutWhereCheck();
        String nonCompliant = "UPDATE dbo.Users SET IsActive = 0;\nDELETE FROM dbo.AuditLog;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(2);

        check = new DmlWithoutWhereCheck();
        String compliant = "UPDATE dbo.Users SET IsActive = 0 WHERE UserID = 1;\nDELETE FROM dbo.AuditLog WHERE AuditID = 1;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testTopWithoutOrderBy() {
        TopWithoutOrderByCheck check = new TopWithoutOrderByCheck();
        assertThat(scanWithCheck("SELECT TOP 10 ID FROM dbo.Users;", check)).hasSize(1);

        check = new TopWithoutOrderByCheck();
        assertThat(scanWithCheck("SELECT TOP 10 ID FROM dbo.Users ORDER BY ID;", check)).isEmpty();
    }

    @Test
    void testDeprecatedDataType() {
        DeprecatedDataTypeCheck check = new DeprecatedDataTypeCheck();
        String nonCompliant = "CREATE TABLE dbo.Articles (ArticleID INT, Body TEXT, Thumbnail IMAGE);";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(2);

        check = new DeprecatedDataTypeCheck();
        String compliant = "CREATE TABLE dbo.Articles (ArticleID INT, Body VARCHAR(MAX));";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }

    @Test
    void testAvoidPrintStatement() {
        AvoidPrintStatementCheck check = new AvoidPrintStatementCheck();
        assertThat(scanWithCheck("PRINT 'Processing started';", check)).hasSize(1);

        check = new AvoidPrintStatementCheck();
        assertThat(scanWithCheck("SELECT 1;", check)).isEmpty();
    }

    @Test
    void testAvoidRaiserror() {
        AvoidRaiserrorCheck check = new AvoidRaiserrorCheck();
        assertThat(scanWithCheck("RAISERROR('Invalid value', 16, 1);", check)).hasSize(1);

        check = new AvoidRaiserrorCheck();
        assertThat(scanWithCheck("THROW 51000, 'Invalid value', 1;", check)).isEmpty();
    }

    @Test
    void testCartesianProduct() {
        CartesianProductCheck check = new CartesianProductCheck();
        String nonCompliant = "SELECT o.OrderID, c.CustomerName FROM dbo.Orders o, dbo.Customers c;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new CartesianProductCheck();
        String compliant = "SELECT o.OrderID, c.CustomerName FROM dbo.Orders o INNER JOIN dbo.Customers c ON c.CustomerID = o.CustomerID;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();

        check = new CartesianProductCheck();
        String compliantWithWhere = "SELECT o.OrderID, c.CustomerName FROM dbo.Orders o, dbo.Customers c WHERE c.CustomerID = o.CustomerID;";
        assertThat(scanWithCheck(compliantWithWhere, check)).isEmpty();
    }

    @Test
    void testAvoidWhileLoop() {
        AvoidWhileLoopCheck check = new AvoidWhileLoopCheck();
        String nonCompliant = "DECLARE @i INT = 0;\nWHILE @i < 10\nBEGIN\nSET @i = @i + 1;\nEND;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new AvoidWhileLoopCheck();
        assertThat(scanWithCheck("SELECT 1;", check)).isEmpty();
    }

    @Test
    void testConvertWithoutStyle() {
        ConvertWithoutStyleCheck check = new ConvertWithoutStyleCheck();
        assertThat(scanWithCheck("SELECT CONVERT(DATETIME, @d);", check)).hasSize(1);

        check = new ConvertWithoutStyleCheck();
        assertThat(scanWithCheck("SELECT CONVERT(DATETIME, @d, 120);", check)).isEmpty();
    }

    @Test
    void testAvoidIndexHint() {
        AvoidIndexHintCheck check = new AvoidIndexHintCheck();
        assertThat(scanWithCheck("SELECT ID FROM dbo.T WITH (INDEX(1));", check)).hasSize(1);

        check = new AvoidIndexHintCheck();
        assertThat(scanWithCheck("SELECT ID FROM dbo.T WITH (NOLOCK);", check)).isEmpty();
    }

    @Test
    void testProcedureWithoutErrorHandling() {
        ProcedureWithoutErrorHandlingCheck check = new ProcedureWithoutErrorHandlingCheck();
        String nonCompliant = "CREATE PROCEDURE dbo.usp_Test AS SELECT 1;";
        assertThat(scanWithCheck(nonCompliant, check)).hasSize(1);

        check = new ProcedureWithoutErrorHandlingCheck();
        String compliant = "CREATE PROCEDURE dbo.usp_Test AS BEGIN TRY SELECT 1; END TRY BEGIN CATCH SELECT 2; END CATCH;";
        assertThat(scanWithCheck(compliant, check)).isEmpty();
    }
}
