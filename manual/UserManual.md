# ChocAn Data Processing System - User Manual

## Overview
The Chocoholics Anonymous (ChocAn) Data Processing System manages provider services,
member records, and automated billing for the ChocAn health organization.

The system features both a **graphical user interface (GUI)** and a **console mode**.
The GUI launches by default with a Bama-themed Swing interface; console mode can be
started with the `--console` flag.

## Prerequisites
- Java 17 or later
- Apache ANT (for building)

## Building & Running

### Build with ANT
```bash
ant clean jar
```
This creates `release/ChocAn.jar`.

### Run the application (GUI mode — default)
```bash
java -jar release/ChocAn.jar
```

### Run the application (Console mode)
```bash
java -jar release/ChocAn.jar --console
```

### Build with Maven (alternative)
```bash
mvn clean package
java -jar target/cs200project4-1.0-SNAPSHOT.jar
```

### Run tests
```bash
mvn test        # Maven
ant test         # ANT
```

### Generate JavaDocs
```bash
ant javadoc
```
Documentation is output to the `doc/` directory.

---

## Data Persistence

The system auto-saves all member, provider, service, and service-record data to the
`data/` directory on exit. When the application starts, it loads any previously saved
data from `data/`. If no saved data exists, the system loads sample data.

---

## GUI Mode

The default mode opens a Bama-themed graphical window (crimson and black color scheme).
The login screen presents buttons for **Provider Login**, **Operator Login**,
**Manager Login**, and **Exit**.

### Authentication (GUI)

| Terminal | Login Method |
|----------|-------------|
| Provider | Enter 9-digit provider number (must exist in database) |
| Operator | Password: `operator123` |
| Manager  | Password: `manager123` |

### GUI Provider Terminal

After logging in with a valid provider number, the provider sees a panel with buttons:

- **Verify Member** — Enter a 9-digit member number to check status.
- **Bill Service** — Record a service. Click a row in the service table to auto-fill the service code.
- **Provider Directory** — View the full service list with codes and fees.
- **Log Out** — Return to the login screen.

### GUI Operator Terminal

After logging in with the operator password, the operator sees a tabbed panel
with **Members** and **Providers** tabs. Each tab shows a data table and
context-specific buttons:

- **Members tab**: Add Member, Update Member, Delete Member, Log Out
- **Providers tab**: Add Provider, Update Provider, Delete Provider, Log Out
- **Click a table row** to edit or delete that record directly.

### GUI Manager Terminal

After logging in with the manager password, the manager sees a panel with buttons:

- **Member Report** — Generate a report for a specific member.
- **Provider Report** — Generate a report for a specific provider.
- **Summary Report** — Generate a summary for accounts payable.
- **Run Accounting Procedure** — Runs the full weekly accounting procedure. Reports are saved to a timestamped folder under `reports/` (e.g., `reports/Accounting_Report_04-09-2026_14-30-00/`).
- **View Reports** — Opens a report browser dialog showing all saved report folders. Click a folder to see its files, click a file to view its contents. Use the **Close** button to return.
- **Log Out** — Return to the login screen.

---

## Console Mode

Start the application with `--console` to use the text-based interface.

### Main Menu
```
=== MAIN MENU ===
1. Provider Login
2. Operator Login
3. Manager Login
4. Exit
```

Select a role by entering its number.

---

## 1. Provider Terminal (Console)

Providers use this terminal to verify members, bill for services, and request the
Provider Directory.

### Login
Enter your 9-digit provider number when prompted.

### Menu Options

| Option | Description |
|--------|-------------|
| 1. Verify Member | Slide a member's card (enter 9-digit member number) to check if the member is active, suspended, or invalid. |
| 2. Bill for Service | Record a service you provided to a member. You will be prompted for the member number, date of service (MM-DD-YYYY), and service code (6 digits). The system confirms the service name and records the transaction. |
| 3. Request Provider Directory | Generates a file listing all available services alphabetically with their codes and fees. |
| 4. Log Out | Return to the main menu. |

### Billing Workflow
1. Enter the member's 9-digit number.
2. The system displays "Validated", "Member suspended", or "Invalid number".
3. If validated, enter the date of service (MM-DD-YYYY).
4. Enter the 6-digit service code (from the Provider Directory).
5. Confirm the displayed service name (Y/N).
6. Optionally enter comments (up to 100 characters).
7. The fee is displayed and the record is saved.

---

## 2. Operator Terminal (Console)

Operators manage member and provider records.

### Login
Enter the operator password when prompted (password: `operator123`).

### Menu Options

| Option | Description |
|--------|-------------|
| 1. Add Member | Add a new member with name, number, address, city, state, and zip. |
| 2. Delete Member | Remove a member by their 9-digit number. |
| 3. Update Member | Update an existing member's information. Press Enter to keep a field unchanged. |
| 4. Add Provider | Add a new provider with name, number, address, city, state, and zip. |
| 5. Delete Provider | Remove a provider by their 9-digit number. |
| 6. Update Provider | Update an existing provider's information. Press Enter to keep a field unchanged. |
| 7. Log Out | Return to the main menu. |

---

## 3. Manager Terminal (Console)

Managers can request reports and run the weekly accounting procedure. Only managers
have access to accounting functions.

### Login
Enter the manager password when prompted (password: `manager123`).

### Menu Options

| Option | Description |
|--------|-------------|
| 1. Request Member Report | Generate a report for a specific member showing all services received. |
| 2. Request Provider Report | Generate a report for a specific provider showing all services billed, with total consultations and fees. |
| 3. Request Summary Report | Generate a summary for accounts payable listing all providers to be paid. |
| 4. Run Accounting Procedure | Runs the full weekly accounting procedure: generates all member reports, all provider reports, the summary report, and EFT data. All files are saved to a timestamped folder under `reports/`. |
| 5. Log Out | Return to the main menu. |

### Reports Generated

When the accounting procedure runs, all reports are saved to a folder:
`reports/Accounting_Report_MM-dd-yyyy_HH-mm-ss/`

The folder contains:

- **Member Reports** (`MemberName_MM-DD-YYYY.txt`): Member info and a list of services received, sorted by date.
- **Provider Reports** (`ProviderName_MM-DD-YYYY.txt`): Provider info, services billed, total consultations, and total fees.
- **Summary Report** (`SummaryReport_MM-DD-YYYY.txt`): All providers to be paid with consultation counts and fee totals.
- **EFT Data** (`EFT_Data_MM-DD-YYYY.txt`): Electronic funds transfer data with provider name, number, and transfer amount.

Individual reports requested from the manager menu (options 1–3) are saved to the current working directory.

---

## Sample Data

The system is pre-loaded with sample data for testing:

### Members
| Name | Number | City | State |
|------|--------|------|-------|
| John Smith | 100000001 | Tuscaloosa | AL |
| Jane Doe | 100000002 | Birmingham | AL |
| Bob Johnson | 100000003 | Huntsville | AL |
| Alice Brown (suspended) | 100000004 | Montgomery | AL |

### Providers
| Name | Number | City | State |
|------|--------|------|-------|
| Dr. Sarah Wilson | 999000001 | Tuscaloosa | AL |
| Dr. Mike Chen | 999000002 | Birmingham | AL |
| Dr. Emily Davis | 999000003 | Huntsville | AL |

### Services (Provider Directory)
| Code | Service Name | Fee |
|------|-------------|-----|
| 100000 | Nutrition Counseling | $125.00 |
| 200000 | Weight Management | $200.00 |
| 300000 | Group Therapy | $50.00 |
| 400000 | Chocolate Addiction | $175.00 |
| 500000 | Internist Consult | $250.00 |
| 598470 | Dietitian Session | $150.00 |
| 883948 | Aerobics Exercise | $75.00 |

---

## File Structure

```
cs200project4/
├── build.xml                  # ANT build script
├── pom.xml                    # Maven build configuration
├── .gitignore                 # Git ignore rules
├── SubmissionGuide.md         # Submission guide & task distribution
├── manual/
│   └── UserManual.md          # This file
├── release/
│   └── ChocAn.jar             # Compiled JAR (after build)
├── doc/                       # Generated JavaDocs (after ant javadoc)
├── data/                      # Auto-saved data files (created at runtime)
├── reports/                   # Accounting report folders (created when procedure runs)
├── src/
│   ├── main/java/com/example/
│   │   ├── Main.java                 # Entry point & sample data
│   │   ├── ChocAnGUI.java            # Swing GUI (Bama theme)
│   │   ├── DataPersistence.java       # Save/load data to disk
│   │   ├── Member.java               # Member entity
│   │   ├── Provider.java             # Provider entity
│   │   ├── Service.java              # Service entity
│   │   ├── ServiceRecord.java        # Service record entity
│   │   ├── MemberDatabase.java       # Member CRUD operations
│   │   ├── ProviderDatabase.java     # Provider CRUD operations
│   │   ├── ServiceDirectory.java     # Provider Directory management
│   │   ├── ServiceRecordDatabase.java # Service record storage
│   │   ├── ProviderTerminal.java     # Provider console interface
│   │   ├── OperatorTerminal.java     # Operator console interface
│   │   ├── ManagerTerminal.java      # Manager console interface
│   │   └── ReportGenerator.java      # Report generation & accounting
│   └── test/java/com/example/
│       ├── TimothySazonovTest.java
│       ├── AryaKarnikTest.java
│       ├── JoelNelemsTest.java
│       ├── TimMaddenTest.java
│       ├── PeytonDoucetteTest.java
│       ├── JackieClaytonTest.java
│       └── AaronSharpTest.java
```
