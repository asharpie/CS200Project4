# ChocAn Project 4 — Submission Guide & Task Distribution

**Course:** CS200 — Spring 2026
**Deadline:** 04/14/2026, 11:59 PM CST
**Team Lead / Final Submitter:** Peyton Doucette

---

## Table of Contents

1. [Team Overview](#team-overview)
2. [File Inventory](#file-inventory)
3. [Who Uploads What — Per-Person Breakdown](#who-uploads-what)
4. [Git Commit Instructions](#git-commit-instructions)
5. [Task Distribution & Contribution Percentages](#task-distribution)
6. [Compilation & Build Guide](#compilation--build-guide)
7. [Running the Application](#running-the-application)
8. [Running Unit Tests](#running-unit-tests)
9. [Repository Structure Checklist](#repository-structure-checklist)

---

## Team Overview

| # | Name               | Role         |
|---|--------------------|--------------|
| 1 | Timothy Sazonov    | Developer    |
| 2 | Arya Karnik        | Developer    |
| 3 | Joel Nelems        | Developer    |
| 4 | Tim Madden         | Developer    |
| 5 | Peyton Doucette    | **Team Lead** |
| 6 | Jackie Clayton     | Developer    |
| 7 | Aaron Sharp        | Developer    |

---

## File Inventory

### Source Files (13 total — in `src/main/java/com/example/`)

| File                      | Author            | Description                              |
|---------------------------|-------------------|------------------------------------------|
| Member.java               | Timothy Sazonov   | ChocAn member entity class               |
| MemberDatabase.java       | Timothy Sazonov   | Member CRUD operations                   |
| Provider.java             | Arya Karnik       | Provider entity class                    |
| ProviderDatabase.java     | Arya Karnik       | Provider CRUD operations                 |
| Service.java              | Joel Nelems       | Service/Provider Directory entry class   |
| ServiceDirectory.java     | Joel Nelems       | Provider Directory management            |
| ServiceRecord.java        | Tim Madden        | Service record entity class              |
| ServiceRecordDatabase.java| Tim Madden        | Weekly service record storage             |
| Main.java                 | Peyton Doucette   | Entry point, login system, sample data   |
| ManagerTerminal.java      | Peyton Doucette   | Manager UI (reports, accounting)         |
| ProviderTerminal.java     | Jackie Clayton    | Provider UI (verify, bill, directory)    |
| ReportGenerator.java      | Jackie Clayton    | All report generation                    |
| OperatorTerminal.java     | Aaron Sharp       | Operator UI (member/provider CRUD)       |

### Test Files (7 total — in `src/test/java/com/example/`)

| File                      | Author            | Tests 2 Own + 1 Cross-Member Method      |
|---------------------------|-------------------|-------------------------------------------|
| TimothySazonovTest.java   | Timothy Sazonov   | MemberDatabase.addMember, MemberDatabase.validateMember + ServiceDirectory.getService (Joel) |
| AryaKarnikTest.java       | Arya Karnik       | ProviderDatabase.addProvider, ProviderDatabase.validateProvider + MemberDatabase.deleteMember (Timothy) |
| JoelNelemsTest.java       | Joel Nelems       | ServiceDirectory.addService, ServiceDirectory.getAllServicesSorted + ProviderDatabase.getProvider (Arya) |
| TimMaddenTest.java        | Tim Madden        | ServiceRecordDatabase.addRecord, ServiceRecordDatabase.getRecordsForMember + ServiceDirectory.size (Joel) |
| PeytonDoucetteTest.java   | Peyton Doucette   | ReportGenerator.generateSummaryReport, ReportGenerator.runAccountingProcedure + ServiceRecordDatabase.clearRecords (Tim) |
| JackieClaytonTest.java    | Jackie Clayton    | ReportGenerator.generateMemberReport, ReportGenerator.generateProviderReport + MemberDatabase.updateMember (Timothy) |
| AaronSharpTest.java       | Aaron Sharp       | MemberDatabase add/delete (via OperatorTerminal), ProviderDatabase add/delete (via OperatorTerminal) + ServiceRecordDatabase.getRecordsForProvider (Tim) |

### Build & Documentation Files

| File             | Uploader          | Description                              |
|------------------|-------------------|------------------------------------------|
| build.xml        | Peyton Doucette   | ANT build script                         |
| pom.xml          | Aaron Sharp       | Maven build configuration                |
| UserManual.md    | Peyton Doucette   | User manual for the ChocAn system        |

---

## Who Uploads What

Each team member must commit their assigned files to the GitHub repository. **Every person pushes 3 files** (Peyton pushes 5 as team lead, to include build config and the manual).

### 1. Timothy Sazonov — 3 files

```
git add src/main/java/com/example/Member.java
git add src/main/java/com/example/MemberDatabase.java
git add src/test/java/com/example/TimothySazonovTest.java
git commit -m "Timothy Sazonov: Member entity, MemberDatabase, and unit tests"
git push
```

### 2. Arya Karnik — 3 files

```
git add src/main/java/com/example/Provider.java
git add src/main/java/com/example/ProviderDatabase.java
git add src/test/java/com/example/AryaKarnikTest.java
git commit -m "Arya Karnik: Provider entity, ProviderDatabase, and unit tests"
git push
```

### 3. Joel Nelems — 3 files

```
git add src/main/java/com/example/Service.java
git add src/main/java/com/example/ServiceDirectory.java
git add src/test/java/com/example/JoelNelemsTest.java
git commit -m "Joel Nelems: Service entity, ServiceDirectory, and unit tests"
git push
```

### 4. Tim Madden — 3 files

```
git add src/main/java/com/example/ServiceRecord.java
git add src/main/java/com/example/ServiceRecordDatabase.java
git add src/test/java/com/example/TimMaddenTest.java
git commit -m "Tim Madden: ServiceRecord entity, ServiceRecordDatabase, and unit tests"
git push
```

### 5. Peyton Doucette (Team Lead) — 5 files

```
git add src/main/java/com/example/Main.java
git add src/main/java/com/example/ManagerTerminal.java
git add src/test/java/com/example/PeytonDoucetteTest.java
git add build.xml
git add manual/UserManual.md
git commit -m "Peyton Doucette: Main, ManagerTerminal, unit tests, build.xml, user manual"
git push
```

### 6. Jackie Clayton — 3 files

```
git add src/main/java/com/example/ProviderTerminal.java
git add src/main/java/com/example/ReportGenerator.java
git add src/test/java/com/example/JackieClaytonTest.java
git commit -m "Jackie Clayton: ProviderTerminal, ReportGenerator, and unit tests"
git push
```

### 7. Aaron Sharp — 3 files

```
git add src/main/java/com/example/OperatorTerminal.java
git add src/test/java/com/example/AaronSharpTest.java
git add pom.xml
git commit -m "Aaron Sharp: OperatorTerminal, unit tests, and pom.xml"
git push
```

### Final Assembly (Peyton Doucette — Team Lead)

After all 7 members have pushed their files, Peyton does the final assembly push:

```
# Generate JavaDocs
ant javadoc

# Build the JAR
ant clean jar

# Push generated artifacts
git add doc/
git add release/
git commit -m "Peyton Doucette: Final assembly - JavaDocs and release JAR"
git push
```

---

## Git Commit Instructions

1. **Clone the repository** (everyone):
   ```
   git clone <your-repo-url>
   cd <repo-name>
   ```

2. **Each member** copies their assigned files into the correct paths in the cloned repo.

3. **Commit order** (recommended to avoid merge conflicts):
   - Members 1–4 (entity/database classes) push first — these have no dependencies on each other.
   - Members 5–7 (terminal/report classes) push second — these depend on the entity/database classes.
   - Peyton does the final assembly last.

4. **If using the same local copy**, everyone should `git pull` before committing to stay up to date.

---

## Task Distribution

| Team Member        | Classes Implemented              | Test File                   | Other Contributions       | Contribution % |
|--------------------|----------------------------------|-----------------------------|---------------------------|---------------|
| Timothy Sazonov    | Member, MemberDatabase           | TimothySazonovTest.java     | JavaDoc for Member.java   | ~14%          |
| Arya Karnik        | Provider, ProviderDatabase       | AryaKarnikTest.java         | JavaDoc for Provider.java | ~14%          |
| Joel Nelems        | Service, ServiceDirectory        | JoelNelemsTest.java         | JavaDoc for Service.java  | ~14%          |
| Tim Madden         | ServiceRecord, ServiceRecordDatabase | TimMaddenTest.java      | JavaDoc for ServiceRecord.java | ~14%     |
| Peyton Doucette    | Main, ManagerTerminal            | PeytonDoucetteTest.java     | build.xml, UserManual.md, final assembly, JavaDoc for Main.java | ~16% |
| Jackie Clayton     | ProviderTerminal, ReportGenerator | JackieClaytonTest.java     | JavaDoc for ReportGenerator.java | ~14%   |
| Aaron Sharp        | OperatorTerminal                 | AaronSharpTest.java         | pom.xml, JavaDoc for OperatorTerminal.java | ~14% |

---

## Compilation & Build Guide

### Prerequisites

- **Java JDK 17+** (tested with JDK 20)
- **Apache ANT 1.10+** (required for grading)
- **Apache Maven 3.6+** (optional, alternative build tool)

### Step 1: Clone the Repository

```bash
git clone <your-repo-url>
cd <repo-name>
```

### Step 2: Build with ANT (Primary — Required for Grading)

```bash
# Clean, compile, and create JAR
ant clean jar

# This will:
#   1. Download JUnit JARs to lib/ (if not present)
#   2. Compile all source files to build/classes/
#   3. Compile all test files to build/test-classes/
#   4. Create release/ChocAn.jar
```

### Step 3: Generate JavaDocs

```bash
ant javadoc
# Output: doc/ directory
```

### Step 4: Run Unit Tests

```bash
ant test
# Runs all 7 test files; output shows pass/fail for each test
```

### Step 5: Run the Application

```bash
java -jar release/ChocAn.jar
```

### Alternative: Build with Maven

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package JAR
mvn clean package

# Run
java -jar target/cs200project4-1.0-SNAPSHOT.jar
```

---

## Running the Application

After building the JAR:

```bash
java -jar release/ChocAn.jar
```

You will see the main menu:

```
=== ChocAn Data Processing System ===
1. Provider Login
2. Operator Login
3. Manager Login
4. Exit
Choose an option:
```

### Sample Login Credentials (Provider Numbers)

| Provider         | Number    |
|------------------|-----------|
| Dr. Smith        | 200000001 |
| Dr. Johnson      | 200000002 |
| Dr. Williams     | 200000003 |

### Sample Member Numbers

| Member           | Number    | Status    |
|------------------|-----------|-----------|
| John Smith       | 100000001 | Active    |
| Jane Doe         | 100000002 | Active    |
| Bob Wilson       | 100000003 | Active    |
| Alice Brown      | 100000004 | Suspended |

### Testing Each Feature

1. **Provider Terminal**: Log in with provider number → verify a member → bill a service → request directory
2. **Operator Terminal**: Log in (enter any input) → add/delete/update members and providers
3. **Manager Terminal**: Log in (enter any input) → request reports → run accounting procedure

---

## Running Unit Tests

### With ANT (required for demo)

```bash
ant test
```

Expected output: All tests pass with green "OK" status.

### With Maven

```bash
mvn test
```

Expected output: `Tests run: XX, Failures: 0, Errors: 0`

### Individual Test Files

| Test File                   | # Tests | What It Tests                                   |
|-----------------------------|---------|--------------------------------------------------|
| TimothySazonovTest.java     | 7       | MemberDatabase add/validate + ServiceDirectory   |
| AryaKarnikTest.java         | 6       | ProviderDatabase add/validate + MemberDatabase   |
| JoelNelemsTest.java         | 6       | ServiceDirectory add/sort + ProviderDatabase     |
| TimMaddenTest.java          | 7       | ServiceRecordDatabase add/query + ServiceDirectory |
| PeytonDoucetteTest.java     | 6       | ReportGenerator summary/accounting + ServiceRecordDatabase |
| JackieClaytonTest.java      | 8       | ReportGenerator member/provider reports + MemberDatabase |
| AaronSharpTest.java         | 8       | Operator member/provider operations + ServiceRecordDatabase |

**Total: 48 tests**

---

## Repository Structure Checklist

Before final submission, verify the repo matches this structure:

```
├── build.xml                          ← ANT script (Peyton)
├── pom.xml                            ← Maven config (Aaron)
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               ├── Main.java                  ← Peyton
│   │               ├── Member.java                ← Timothy
│   │               ├── MemberDatabase.java        ← Timothy
│   │               ├── Provider.java              ← Arya
│   │               ├── ProviderDatabase.java      ← Arya
│   │               ├── Service.java               ← Joel
│   │               ├── ServiceDirectory.java      ← Joel
│   │               ├── ServiceRecord.java         ← Tim M.
│   │               ├── ServiceRecordDatabase.java ← Tim M.
│   │               ├── ManagerTerminal.java       ← Peyton
│   │               ├── ProviderTerminal.java      ← Jackie
│   │               ├── ReportGenerator.java       ← Jackie
│   │               └── OperatorTerminal.java      ← Aaron
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   ├── TimothySazonovTest.java    ← Timothy
│                   ├── AryaKarnikTest.java        ← Arya
│                   ├── JoelNelemsTest.java        ← Joel
│                   ├── TimMaddenTest.java         ← Tim M.
│                   ├── PeytonDoucetteTest.java    ← Peyton
│                   ├── JackieClaytonTest.java     ← Jackie
│                   └── AaronSharpTest.java        ← Aaron
├── doc/                               ← Generated JavaDocs (Peyton final push)
├── manual/
│   └── UserManual.md                  ← User manual (Peyton)
├── release/
│   └── ChocAn.jar                     ← Generated JAR (Peyton final push)
└── old/                               ← Previous project files (Projects 1-3)
```

### Final Checklist

- [ ] All 7 members have commits in the repo
- [ ] All 13 source files compile without errors
- [ ] All 48 unit tests pass (green)
- [ ] `ant clean jar` produces `release/ChocAn.jar`
- [ ] `java -jar release/ChocAn.jar` runs successfully
- [ ] `ant javadoc` generates docs in `doc/`
- [ ] `ant test` runs all tests successfully
- [ ] Each source file has the correct `@author` tag
- [ ] User manual is in `manual/`
- [ ] Previous project files are in `old/` (if applicable)
