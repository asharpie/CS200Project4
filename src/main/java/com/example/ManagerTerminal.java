package com.example;

import java.util.Scanner;

/**
 * The Manager Terminal interface.
 * Managers can request individual reports and run the accounting procedure.
 * Once logged in, the manager stays in a session menu until they explicitly log out.
 * 
 * @author Peyton Doucette
 */
public class ManagerTerminal {
    private final ReportGenerator reportGenerator;
    private final MemberDatabase memberDb;
    private final ProviderDatabase providerDb;
    private final Scanner scanner;

    public ManagerTerminal(ReportGenerator reportGenerator, MemberDatabase memberDb,
                           ProviderDatabase providerDb, Scanner scanner) {
        this.reportGenerator = reportGenerator;
        this.memberDb = memberDb;
        this.providerDb = providerDb;
        this.scanner = scanner;
    }

    /**
     * Start a manager session.
     */
    public void startSession() {
        System.out.println("Manager logged in.");
        sessionMenu();
    }

    /**
     * Manager session menu loop.
     */
    private void sessionMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== MANAGER TERMINAL ===");
            System.out.println("1. Request Member Report");
            System.out.println("2. Request Provider Report");
            System.out.println("3. Request Summary Report");
            System.out.println("4. Run Accounting Procedure");
            System.out.println("5. Log Out");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": requestMemberReport(); break;
                case "2": requestProviderReport(); break;
                case "3": requestSummaryReport(); break;
                case "4": runAccountingProcedure(); break;
                case "5":
                    running = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Request an individual member report.
     */
    public void requestMemberReport() {
        System.out.print("Enter member number: ");
        String input = scanner.nextLine().trim();
        int memberNumber;
        try {
            memberNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
            return;
        }

        if (memberDb.getMember(memberNumber) == null) {
            System.out.println("Member not found.");
            return;
        }

        String file = reportGenerator.generateMemberReport(memberNumber);
        if (file != null) {
            System.out.println("Member report generated: " + file);
        } else {
            System.out.println("No service records found for this member.");
        }
    }

    /**
     * Request an individual provider report.
     */
    public void requestProviderReport() {
        System.out.print("Enter provider number: ");
        String input = scanner.nextLine().trim();
        int providerNumber;
        try {
            providerNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
            return;
        }

        if (providerDb.getProvider(providerNumber) == null) {
            System.out.println("Provider not found.");
            return;
        }

        String file = reportGenerator.generateProviderReport(providerNumber);
        if (file != null) {
            System.out.println("Provider report generated: " + file);
        } else {
            System.out.println("No service records found for this provider.");
        }
    }

    /**
     * Request the summary report for accounts payable.
     */
    public void requestSummaryReport() {
        String file = reportGenerator.generateSummaryReport();
        if (file != null) {
            System.out.println("Summary report generated: " + file);
        } else {
            System.out.println("Error generating summary report.");
        }
    }

    /**
     * Manually trigger the main accounting procedure.
     */
    public void runAccountingProcedure() {
        reportGenerator.runAccountingProcedure();
    }
}
