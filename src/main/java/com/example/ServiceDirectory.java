package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the Provider Directory - an alphabetically ordered list
 * of service names, codes, and fees.
 * 
 * @author Joel Nelems
 */
public class ServiceDirectory {
    private final Map<Integer, Service> services = new HashMap<>();

    /**
     * Add a service to the directory.
     * @param service the service to add
     * @return true if added, false if code already exists
     */
    public boolean addService(Service service) {
        if (services.containsKey(service.getCode())) {
            return false;
        }
        services.put(service.getCode(), service);
        return true;
    }

    /**
     * Look up a service by its code.
     * @param serviceCode the 6-digit service code
     * @return the Service if found, null otherwise
     */
    public Service getService(int serviceCode) {
        return services.get(serviceCode);
    }

    /**
     * Get all services sorted alphabetically by name.
     * @return sorted list of services
     */
    public List<Service> getAllServicesSorted() {
        List<Service> sorted = new ArrayList<>(services.values());
        sorted.sort(Comparator.comparing(Service::getName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    /**
     * Generate the Provider Directory file.
     * The directory is an alphabetically ordered list of service names,
     * codes, and fees.
     * @param filename the output file name
     */
    public void generateDirectoryFile(String filename) {
        List<Service> sorted = getAllServicesSorted();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== PROVIDER DIRECTORY ===");
            writer.println();
            writer.printf("%-20s  %-6s  %s%n", "Service Name", "Code", "Fee");
            writer.println("--------------------------------------------------");
            for (Service s : sorted) {
                writer.printf("%-20s  %06d  $%.2f%n", s.getName(), s.getCode(), s.getFee());
            }
            writer.println();
            writer.println("=== END OF DIRECTORY ===");
        } catch (IOException e) {
            System.err.println("Error writing Provider Directory: " + e.getMessage());
        }
    }

    /**
     * Get the number of services in the directory.
     * @return count of services
     */
    public int size() {
        return services.size();
    }
}
