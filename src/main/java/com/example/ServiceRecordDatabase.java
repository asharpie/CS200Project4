package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the weekly service records in the ChocAn Data Center.
 * Service records are written when providers bill for services.
 * 
 * @author Tim Madden
 */
public class ServiceRecordDatabase {
    private final List<ServiceRecord> records = new ArrayList<>();

    /**
     * Add a service record to the database.
     * @param record the service record to add
     */
    public void addRecord(ServiceRecord record) {
        records.add(record);
    }

    /**
     * Get all service records for a given member.
     * @param memberNumber the member number
     * @return list of service records for the member
     */
    public List<ServiceRecord> getRecordsForMember(int memberNumber) {
        return records.stream()
                .filter(r -> r.getMemberNumber() == memberNumber)
                .collect(Collectors.toList());
    }

    /**
     * Get all service records for a given provider.
     * @param providerNumber the provider number
     * @return list of service records for the provider
     */
    public List<ServiceRecord> getRecordsForProvider(int providerNumber) {
        return records.stream()
                .filter(r -> r.getProviderNumber() == providerNumber)
                .collect(Collectors.toList());
    }

    /**
     * Get all service records.
     * @return list of all service records
     */
    public List<ServiceRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    /**
     * Clear all service records (e.g., after weekly processing).
     */
    public void clearRecords() {
        records.clear();
    }

    /**
     * Get the total number of service records.
     * @return count of records
     */
    public int size() {
        return records.size();
    }
}
