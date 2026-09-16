# T-SQL Community Analyzer for SonarQube

Plugin open-source per **SonarQube Community Edition** (compatibile con SonarQube 9.x, 10.x e LTS) dedicato all'analisi statica di codice e script **Microsoft Transact-SQL (T-SQL)**.

Il plugin sfrutta un parser **ANTLR4** integrato in Java per generare l'AST (Abstract Syntax Tree), calcolare le metriche di codice (NCLOC, righe di commento) ed eseguire controlli statici di qualità, sicurezza, performance e conformità alle best practice T-SQL.

---

## 🚀 Caratteristiche Principali

- **Compatibilità SonarQube**: SonarQube Community Edition 9.x - 10.x+.
- **Estensioni supportate**: `.sql`, `.tsql` (configurabili via property `sonar.tsql.file.suffixes`).
- **Analisi AST nativa**: Basato su grammatica ANTLR4 T-SQL incorporata (senza dipendenze esterne a runtime).
- **Quality Profile predefinito**: Profilo "Sonar way" per T-SQL con regole attive di default.
- **Metriche calcolate**: Righe di codice (NCLOC) e righe di commenti.

---

## 📋 Regole Incluse di Default

| Regola | Tipo | Severità | Descrizione |
|---|---|---|---|
| `S101_AvoidSelectStar` | Code Smell | Major | Evita l'uso di `SELECT *` e `SELECT table.*` in query, viste e stored procedure. |
| `S102_AvoidNoLock` | Bug | Critical | Evita l'uso di hint `NOLOCK` / `READUNCOMMITTED` (rischio dirty reads e phantom data). |
| `S103_AvoidSpPrefix` | Code Smell | Major | Le stored procedure utente non devono iniziare con `sp_` (riservato al database `master`). |
| `S104_MissingSemicolon` | Code Smell | Minor | I comandi T-SQL devono terminare con il punto e virgola `;` (sintassi deprecata da Microsoft). |
| `S105_AvoidCursor` | Code Smell | Major | Evita l'uso di cursori procedurali `CURSOR`; preferisci operazioni set-based. |
| `S106_TransactionXactAbort` | Bug | Critical | Le transazioni esplicite devono impostare `SET XACT_ABORT ON` o usare blocchi `TRY...CATCH`. |
| `S107_DynamicSqlInjection` | Vulnerability | Blocker | Evita SQL dinamico con concatenazione di stringhe `+` (vulnerabilità SQL Injection CWE-89). |
| `S108_AvoidOrderByOrdinal` | Code Smell | Minor | Evita numeri ordinali di colonna in `ORDER BY` (es. `ORDER BY 1, 2`). |
| `S109_TableWithoutPrimaryKey` | Bug | Critical | Le tabelle create con `CREATE TABLE` devono definire un vincolo `PRIMARY KEY`. |
| `S110_UpperKeywords` | Code Smell | Info | Le keyword SQL devono essere scritte in MAIUSCOLO per leggibilità e stile uniforme. |

---

## 🛠️ Build e Installazione

### 1. Prerequisiti
- Java JDK 17+
- Apache Maven 3.8+

### 2. Compilazione del plugin
Esegui dalla root del repository:
```bash
mvn clean package
```
Il file generato sarà presente in `target/sonar-tsql-community-plugin-1.0.0-SNAPSHOT.jar`.

### 3. Installazione in SonarQube
1. Copia il file `.jar` generato nella cartella dei plugin di SonarQube:
   ```bash
   cp target/sonar-tsql-community-plugin-1.0.0-SNAPSHOT.jar $SONARQUBE_HOME/extensions/plugins/
   ```
2. Riavvia il server SonarQube:
   ```bash
   $SONARQUBE_HOME/bin/[OS]/sonar.sh restart
   ```
3. Vai su SonarQube Web UI -> **Rules** -> filtra per linguaggio **T-SQL** per visualizzare e personalizzare le regole.

---

## 🔍 Esempio di scansione con SonarScanner

Crea un file `sonar-project.properties` nel repository dei tuoi script SQL:

```properties
sonar.projectKey=my-database-project
sonar.projectName=My Database Project
sonar.projectVersion=1.0

# Directory contenente gli script .sql
sonar.sources=src/sql
sonar.sourceEncoding=UTF-8

# (Opzionale) Estensioni file riconosciute
sonar.tsql.file.suffixes=.sql,.tsql
```

Esegui l'analisi con SonarScanner CLI:
```bash
sonar-scanner
```

---

## 🧩 Come Aggiungere Nuove Regole

1. **Crea la classe della regola** in `src/main/java/org/sonar/plugins/tsql/checks/`:
   ```java
   public class MyCustomCheck extends TSqlCheck {
       public static final String RULE_KEY = "S111_MyCustomRule";

       @Override
       public void enterSelect_stmt(TSqlParser.Select_stmtContext ctx) {
           // logica di analisi AST
           reportIssue(ctx, "Messaggio descrittivo della violazione");
       }
   }
   ```
2. **Registra la regola** in:
   - `TSqlRulesDefinition.java` (metadati, severità, tempo di remediation, tag)
   - `TSqlQualityProfile.java` (attivazione nel profilo standard)
   - `TSqlSensor.java` (mappatura `RULE_MAP`)
3. **Aggiungi la documentazione HTML** in:
   - `src/main/resources/org/sonar/l10n/tsql/rules/tsqlcommunity/S111_MyCustomRule.html`
4. **Aggiungi i test unitari** in `src/test/java/org/sonar/plugins/tsql/TSqlChecksTest.java`.
