# ChocAn Data Processing System - User Manual

## Overview
The Chocoholics Anonymous (ChocAn) Data Processing System manages provider services,
member records, and automated billing for the ChocAn health organization.

## Prerequisites
- Java 17 or later
- Apache ANT (for building)

## Building & Running

### Build with ANT
```bash
ant clean jar
```
This creates `release/ChocAn.jar`.

### Run the application
```bash
java -jar release/ChocAn.jar
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

## Main Menu
When the application starts, you see:

```
=== MAIN MENU ===
1. Provider Login
2. Operator Login
3. Manager Login
4. Exit
```

Select a role by entering its number.

---

## 1. Provider Terminal

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

## 2. Operator Terminal

Operators manage member and provider records.

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

## 3. Manager Terminal

Managers can request reports and run the weekly accounting procedure.

### Menu Options

| Option | Description |
|--------|-------------|
| 1. Request Member Report | Generate a report for a specific member showing all services received. |
| 2. Request Provider Report | Generate a report for a specific provider showing all services billed, with total consultations and fees. |
| 3. Request Summary Report | Generate a summary for accounts payable listing all providers to be paid. |
| 4. Run Accounting Procedure | Runs the full weekly accounting procedure: generates all member reports, all provider reports, the summary report, and EFT data. |
| 5. Log Out | Return to the main menu. |

### Reports Generated

- **Member Report** (`MemberName_MM-DD-YYYY.txt`): Member info and a list of services received, sorted by date.
- **Provider Report** (`ProviderName_MM-DD-YYYY.txt`): Provider info, services billed, total consultations, and total fees.
- **Summary Report** (`SummaryReport_MM-DD-YYYY.txt`): All providers to be paid with consultation counts and fee totals.
- **EFT Data** (`EFT_Data_MM-DD-YYYY.txt`): Electronic funds transfer data with provider name, number, and transfer amount.

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
├── build.xml              # ANT build script
├── pom.xml                # Maven build configuration
├── UserManual.md          # This file
├── release/
│   └── ChocAn.jar         # Compiled JAR (after build)
├── doc/                   # Generated JavaDocs (after ant javadoc)
├── src/
│   ├── main/java/com/example/
│   │   ├── Main.java                 # Entry point
│   │   ├── Member.java               # Member entity
│   │   ├── Provider.java             # Provider entity
│   │   ├── Service.java              # Service entity
│   │   ├── ServiceRecord.java        # Service record entity
│   │   ├── MemberDatabase.java       # Member CRUD operations
│   │   ├── ProviderDatabase.java     # Provider CRUD operations
│   │   ├── ServiceDirectory.java     # Provider Directory management
│   │   ├── ServiceRecordDatabase.java # Service record storage
│   │   ├── ProviderTerminal.java     # Provider interface
│   │   ├── OperatorTerminal.java     # Operator interface
│   │   ├── ManagerTerminal.java      # Manager interface
│   │   └── ReportGenerator.java      # Report generation
│   └── test/java/com/example/
│       ├── EntityTest.java
│       ├── MemberDatabaseTest.java
│       ├── ProviderDatabaseTest.java
│       ├── ServiceDirectoryTest.java
│       ├── ServiceRecordDatabaseTest.java
│       └── ReportGeneratorTest.java
```
