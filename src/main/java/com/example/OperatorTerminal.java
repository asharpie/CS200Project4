package com.example;

import java.util.Scanner;

/**
 * The Operator Terminal interface.
 * Operators use this to manage member and provider records (add, delete, update).
 * Once logged in, the operator stays in a session menu until they explicitly log out.
 * 
 * @author Aaron Sharp
 */
public class OperatorTerminal {
    private final MemberDatabase memberDb;
    private final ProviderDatabase providerDb;
    private final Scanner scanner;

    public OperatorTerminal(MemberDatabase memberDb, ProviderDatabase providerDb, Scanner scanner) {
        this.memberDb = memberDb;
        this.providerDb = providerDb;
        this.scanner = scanner;
    }

    /**
     * Start an operator session.
     */
    public void startSession() {
        System.out.println("Operator logged in.");
        sessionMenu();
    }

    /**
     * Operator session menu loop.
     */
    private void sessionMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== OPERATOR TERMINAL ===");
            System.out.println("1. Add Member");
            System.out.println("2. Delete Member");
            System.out.println("3. Update Member");
            System.out.println("4. Add Provider");
            System.out.println("5. Delete Provider");
            System.out.println("6. Update Provider");
            System.out.println("7. Log Out");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addMember(); break;
                case "2": deleteMember(); break;
                case "3": updateMember(); break;
                case "4": addProvider(); break;
                case "5": deleteProvider(); break;
                case "6": updateProvider(); break;
                case "7":
                    running = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Add a new member to the database.
     */
    public void addMember() {
        System.out.println("\n--- Add Member ---");
        System.out.print("Enter member name (up to 25 chars): ");
        String name = scanner.nextLine().trim();
        if (name.length() > 25) name = name.substring(0, 25);

        System.out.print("Enter member number (9 digits): ");
        int number = readInt();
        if (number == -1) return;

        System.out.print("Enter street address (up to 25 chars): ");
        String address = scanner.nextLine().trim();
        if (address.length() > 25) address = address.substring(0, 25);

        System.out.print("Enter city (up to 14 chars): ");
        String city = scanner.nextLine().trim();
        if (city.length() > 14) city = city.substring(0, 14);

        System.out.print("Enter state (2 letters): ");
        String state = scanner.nextLine().trim().toUpperCase();
        if (state.length() > 2) state = state.substring(0, 2);

        System.out.print("Enter ZIP code (5 digits): ");
        String zip = scanner.nextLine().trim();
        if (zip.length() > 5) zip = zip.substring(0, 5);

        Member member = new Member(name, number, address, city, state, zip);
        if (memberDb.addMember(member)) {
            System.out.println("Member added successfully.");
        } else {
            System.out.println("Error: Member number already exists.");
        }
    }

    /**
     * Delete a member from the database.
     */
    public void deleteMember() {
        System.out.println("\n--- Delete Member ---");
        System.out.print("Enter member number to delete: ");
        int number = readInt();
        if (number == -1) return;

        if (memberDb.deleteMember(number)) {
            System.out.println("Member deleted successfully.");
        } else {
            System.out.println("Error: Member not found.");
        }
    }

    /**
     * Update an existing member record.
     */
    public void updateMember() {
        System.out.println("\n--- Update Member ---");
        System.out.print("Enter member number to update: ");
        int number = readInt();
        if (number == -1) return;

        Member existing = memberDb.getMember(number);
        if (existing == null) {
            System.out.println("Error: Member not found.");
            return;
        }

        System.out.println("Current info: " + existing);
        System.out.println("Press Enter to keep current value.");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            if (name.length() > 25) name = name.substring(0, 25);
            existing.setName(name);
        }

        System.out.print("New address [" + existing.getAddress() + "]: ");
        String address = scanner.nextLine().trim();
        if (!address.isEmpty()) {
            if (address.length() > 25) address = address.substring(0, 25);
            existing.setAddress(address);
        }

        System.out.print("New city [" + existing.getCity() + "]: ");
        String city = scanner.nextLine().trim();
        if (!city.isEmpty()) {
            if (city.length() > 14) city = city.substring(0, 14);
            existing.setCity(city);
        }

        System.out.print("New state [" + existing.getState() + "]: ");
        String state = scanner.nextLine().trim();
        if (!state.isEmpty()) {
            if (state.length() > 2) state = state.substring(0, 2);
            existing.setState(state.toUpperCase());
        }

        System.out.print("New ZIP [" + existing.getZip() + "]: ");
        String zip = scanner.nextLine().trim();
        if (!zip.isEmpty()) {
            if (zip.length() > 5) zip = zip.substring(0, 5);
            existing.setZip(zip);
        }

        memberDb.updateMember(existing);
        System.out.println("Member updated successfully.");
    }

    /**
     * Add a new provider to the database.
     */
    public void addProvider() {
        System.out.println("\n--- Add Provider ---");
        System.out.print("Enter provider name (up to 25 chars): ");
        String name = scanner.nextLine().trim();
        if (name.length() > 25) name = name.substring(0, 25);

        System.out.print("Enter provider number (9 digits): ");
        int number = readInt();
        if (number == -1) return;

        System.out.print("Enter street address (up to 25 chars): ");
        String address = scanner.nextLine().trim();
        if (address.length() > 25) address = address.substring(0, 25);

        System.out.print("Enter city (up to 14 chars): ");
        String city = scanner.nextLine().trim();
        if (city.length() > 14) city = city.substring(0, 14);

        System.out.print("Enter state (2 letters): ");
        String state = scanner.nextLine().trim().toUpperCase();
        if (state.length() > 2) state = state.substring(0, 2);

        System.out.print("Enter ZIP code (5 digits): ");
        String zip = scanner.nextLine().trim();
        if (zip.length() > 5) zip = zip.substring(0, 5);

        Provider provider = new Provider(name, number, address, city, state, zip);
        if (providerDb.addProvider(provider)) {
            System.out.println("Provider added successfully.");
        } else {
            System.out.println("Error: Provider number already exists.");
        }
    }

    /**
     * Delete a provider from the database.
     */
    public void deleteProvider() {
        System.out.println("\n--- Delete Provider ---");
        System.out.print("Enter provider number to delete: ");
        int number = readInt();
        if (number == -1) return;

        if (providerDb.deleteProvider(number)) {
            System.out.println("Provider deleted successfully.");
        } else {
            System.out.println("Error: Provider not found.");
        }
    }

    /**
     * Update an existing provider record.
     */
    public void updateProvider() {
        System.out.println("\n--- Update Provider ---");
        System.out.print("Enter provider number to update: ");
        int number = readInt();
        if (number == -1) return;

        Provider existing = providerDb.getProvider(number);
        if (existing == null) {
            System.out.println("Error: Provider not found.");
            return;
        }

        System.out.println("Current info: " + existing);
        System.out.println("Press Enter to keep current value.");

        System.out.print("New name [" + existing.getName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            if (name.length() > 25) name = name.substring(0, 25);
            existing.setName(name);
        }

        System.out.print("New address [" + existing.getAddress() + "]: ");
        String address = scanner.nextLine().trim();
        if (!address.isEmpty()) {
            if (address.length() > 25) address = address.substring(0, 25);
            existing.setAddress(address);
        }

        System.out.print("New city [" + existing.getCity() + "]: ");
        String city = scanner.nextLine().trim();
        if (!city.isEmpty()) {
            if (city.length() > 14) city = city.substring(0, 14);
            existing.setCity(city);
        }

        System.out.print("New state [" + existing.getState() + "]: ");
        String state = scanner.nextLine().trim();
        if (!state.isEmpty()) {
            if (state.length() > 2) state = state.substring(0, 2);
            existing.setState(state.toUpperCase());
        }

        System.out.print("New ZIP [" + existing.getZip() + "]: ");
        String zip = scanner.nextLine().trim();
        if (!zip.isEmpty()) {
            if (zip.length() > 5) zip = zip.substring(0, 5);
            existing.setZip(zip);
        }

        providerDb.updateProvider(existing);
        System.out.println("Provider updated successfully.");
    }

    /**
     * Helper to read an integer from scanner input.
     * @return the parsed integer, or -1 on error
     */
    private int readInt() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
            return -1;
        }
    }
}
