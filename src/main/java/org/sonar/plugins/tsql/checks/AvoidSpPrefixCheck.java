package org.sonar.plugins.tsql.checks;

import org.sonar.plugins.tsql.antlr.TSqlParser;
import org.sonar.plugins.tsql.ast.TSqlCheck;

public class AvoidSpPrefixCheck extends TSqlCheck {

    public static final String RULE_KEY = "S103_AvoidSpPrefix";

    @Override
    public void enterCreate_procedure_stmt(TSqlParser.Create_procedure_stmtContext ctx) {
        checkProcName(ctx.proc_name());
    }

    @Override
    public void enterAlter_procedure_stmt(TSqlParser.Alter_procedure_stmtContext ctx) {
        checkProcName(ctx.proc_name());
    }

    private void checkProcName(TSqlParser.Proc_nameContext procNameCtx) {
        if (procNameCtx == null) {
            return;
        }
        String text = procNameCtx.getText().replace("[", "").replace("]", "").replace("\"", "");
        String baseName = text;
        if (text.contains(".")) {
            baseName = text.substring(text.lastIndexOf('.') + 1);
        }
        if (baseName.toLowerCase().startsWith("sp_")) {
            reportIssue(procNameCtx, "Do not name user stored procedures with the 'sp_' prefix. SQL Server reserves 'sp_' for system procedures and scans the master database first, hurting performance.");
        }
    }
}
