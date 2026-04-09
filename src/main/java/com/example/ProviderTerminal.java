package com.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * The Provider Terminal interface.
 * Providers use this to verify members, bill services, and request the Provider Directory.
 * Once logged in, the provider stays in a session menu until they explicitly log out.
 * 
 * @author Jackie Clayton
 */
public class ProviderTerminal {
    private final MemberDatabase memberDb;
    private final ProviderDatabase providerDb;
    private final ServiceDirectory serviceDir;
    private final ServiceRecordDatabase recordDb;
    private final Scanner scanner;
    private Provider currentProvider;

    public ProviderTerminal(MemberDatabase memberDb, ProviderDatabase providerDb,
                            ServiceDirectory serviceDir, ServiceRecordDatabase recordDb,
                            Scanner scanner) {
        this.memberDb = memberDb;
        this.providerDb = providerDb;
        this.serviceDir = serviceDir;
        this.recordDb = recordDb;
        this.scanner = scanner;
    }

    /**
     * Start a provider session. Prompts for provider number, then shows the menu.
     */
    public void startSession() {
        System.out.print("Enter provider number (9 digits): ");
        String input = scanner.nextLine().trim();
        int providerNumber;
        try {
            providerNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid provider number format.");
            return;
        }

        currentProvider = providerDb.getProvider(providerNumber);
        if (currentProvider == null) {
            System.out.println("Provider not found.");
            return;
        }

        System.out.println("Welcome, " + currentProvider.getName() + "!");
        sessionMenu();
    }

    /**
     * Provider session menu loop.
     */
    private void sessionMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== PROVIDER TERMINAL ===");
            System.out.println("1. Verify Member");
            System.out.println("2. Bill Service (Record Service)");
            System.out.println("3. Request Provider Directory");
            System.out.println("4. Log Out");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    verifyMember();
                    break;
                case "2":
                    billService();
                    break;
                case "3":
                    requestProviderDirectory();
                    break;
                case "4":
                    running = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Verify a member by card swipe (member number input).
     * Displays "Validated", "Member suspended", or "Invalid number".
     * @return the validation status string
     */
    public String verifyMember() {
        System.out.print("Enter member number (9 digits): ");
        String input = scanner.nextLine().trim();
        int memberNumber;
        try {
            memberNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
            return "Invalid number";
        }

        String status = memberDb.validateMember(memberNumber);
        System.out.println(status);
        return status;
    }

    /**
     * Bill a service - the main billing workflow.
     * Provider swipes/enters member card, enters date of service, service code,
     * verifies service name, and optionally enters comments.
     */
    public void billService() {
        // Step 1: Verify member
        System.out.print("Enter member number (9 digits): ");
        String memberInput = scanner.nextLine().trim();
        int memberNumber;
        try {
            memberNumber = Integer.parseInt(memberInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
            return;
        }

        String status = memberDb.validateMember(memberNumber);
        System.out.println(status);
        if (!"Validated".equals(status)) {
            return;
        }

        // Step 2: Enter date of service
        LocalDate serviceDate = null;
        while (serviceDate == null) {
            System.out.print("Enter date of service (MM-DD-YYYY): ");
            String dateInput = scanner.nextLine().trim();
            try {
                serviceDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use MM-DD-YYYY.");
            }
        }

        // Step 3: Enter service code
        Service service = null;
        while (service == null) {
            System.out.print("Enter service code (6 digits): ");
            String codeInput = scanner.nextLine().trim();
            int serviceCode;
            try {
                serviceCode = Integer.parseInt(codeInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid service code format.");
                continue;
            }

            service = serviceDir.getService(serviceCode);
            if (service == null) {
                System.out.println("Error: Service code not found. Please try again.");
            }
        }

        // Step 4: Verify service name
        System.out.println("Service: " + service.getName());
        System.out.print("Is this the correct service? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Service billing cancelled.");
            return;
        }

        // Step 5: Optional comments
        System.out.print("Enter comments (up to 100 chars, or press Enter to skip): ");
        String comments = scanner.nextLine().trim();
        if (comments.length() > 100) {
            comments = comments.substring(0, 100);
        }
        if (comments.isEmpty()) {
            comments = null;
        }

        // Step 6: Write service record
        LocalDateTime now = LocalDateTime.now();
        ServiceRecord record = new ServiceRecord(now, serviceDate,
                currentProvider.getNumber(), memberNumber, service.getCode(), comments);
        recordDb.addRecord(record);

        // Step 7: Display fee
        System.out.printf("Fee to be paid: $%.2f%n", service.getFee());
        System.out.println("Service record saved successfully.");
    }

    /**
     * Request the Provider Directory.
     * Generates an alphabetically ordered list of service names, codes, and fees
     * and saves it to a file.
     */
    public void requestProviderDirectory() {
        String filename = "ProviderDirectory.txt";
        serviceDir.generateDirectoryFile(filename);
        System.out.println("Provider Directory generated: " + filename);

        // Also display it on screen
        List<Service> sorted = serviceDir.getAllServicesSorted();
        System.out.println("\n=== PROVIDER DIRECTORY ===");
        System.out.printf("%-20s  %-6s  %s%n", "Service Name", "Code", "Fee");
        System.out.println("--------------------------------------------------");
        for (Service s : sorted) {
            System.out.printf("%-20s  %06d  $%.2f%n", s.getName(), s.getCode(), s.getFee());
        }
        System.out.println("=== END OF DIRECTORY ===");
    }

    /**
     * Get the currently logged-in provider.
     * @return the current provider
     */
    public Provider getCurrentProvider() {
        return currentProvider;
    }
}
