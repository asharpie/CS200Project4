package com.example;

import java.util.Scanner;

/**
 * Main entry point for the Chocoholics Anonymous (ChocAn) data processing system.
 * Presents a login menu with three roles: Provider, Operator, and Manager.
 * Each role enters a persistent session that returns to its own menu
 * until the user explicitly logs out, at which point they return here.
 * 
 * The system pre-loads sample data for demonstration purposes.
 * 
 * @author Peyton Doucette
 */
public class Main {
    private static MemberDatabase memberDb = new MemberDatabase();
    private static ProviderDatabase providerDb = new ProviderDatabase();
    private static ServiceDirectory serviceDir = new ServiceDirectory();
    private static ServiceRecordDatabase recordDb = new ServiceRecordDatabase();
    private static ReportGenerator reportGenerator;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean loaded = DataPersistence.loadAll(memberDb, providerDb, serviceDir, recordDb);
        if (!loaded) {
            loadSampleData();
        }
        reportGenerator = new ReportGenerator(memberDb, providerDb, serviceDir, recordDb);

        System.out.println("========================================");
        System.out.println("  Chocoholics Anonymous (ChocAn) System");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Provider Login");
            System.out.println("2. Operator Login");
            System.out.println("3. Manager Login");
            System.out.println("4. Run Main Accounting Procedure");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    ProviderTerminal provTerminal = new ProviderTerminal(
                            memberDb, providerDb, serviceDir, recordDb, scanner);
                    provTerminal.startSession();
                    break;
                case "2":
                    OperatorTerminal opTerminal = new OperatorTerminal(memberDb, providerDb, scanner);
                    opTerminal.startSession();
                    break;
                case "3":
                    ManagerTerminal mgrTerminal = new ManagerTerminal(
                            reportGenerator, memberDb, providerDb, scanner);
                    mgrTerminal.startSession();
                    break;
                case "4":
                    reportGenerator.runAccountingProcedure();
                    break;
                case "5":
                    running = false;
                    DataPersistence.saveAll(memberDb, providerDb, serviceDir, recordDb);
                    System.out.println("Data saved. Shutting down ChocAn system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    /**
     * Load sample data for demonstration and testing.
     */
    private static void loadSampleData() {
        // Sample Members
        memberDb.addMember(new Member("John Smith", 100000001, "123 Main St", "Tuscaloosa", "AL", "35401"));
        memberDb.addMember(new Member("Jane Doe", 100000002, "456 Oak Ave", "Birmingham", "AL", "35203"));
        memberDb.addMember(new Member("Bob Johnson", 100000003, "789 Pine Rd", "Huntsville", "AL", "35801"));
        Member suspended = new Member("Alice Brown", 100000004, "321 Elm St", "Montgomery", "AL", "36104");
        suspended.setSuspended(true);
        memberDb.addMember(suspended);

        // Sample Providers
        providerDb.addProvider(new Provider("Dr. Sarah Wilson", 999000001, "100 Medical Dr", "Tuscaloosa", "AL", "35401"));
        providerDb.addProvider(new Provider("Dr. Mike Chen", 999000002, "200 Health Ave", "Birmingham", "AL", "35203"));
        providerDb.addProvider(new Provider("Dr. Emily Davis", 999000003, "300 Wellness Blvd", "Huntsville", "AL", "35801"));

        // Sample Services (Provider Directory)
        serviceDir.addService(new Service(598470, "Dietitian Session", 150.00));
        serviceDir.addService(new Service(883948, "Aerobics Exercise", 75.00));
        serviceDir.addService(new Service(100000, "Nutrition Counseling", 125.00));
        serviceDir.addService(new Service(200000, "Weight Management", 200.00));
        serviceDir.addService(new Service(300000, "Group Therapy", 50.00));
        serviceDir.addService(new Service(400000, "Chocolate Addiction", 175.00));
        serviceDir.addService(new Service(500000, "Internist Consult", 250.00));
    }
}
