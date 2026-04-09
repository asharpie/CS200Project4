package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages provider records in the ChocAn Data Center.
 * Supports add, delete, update, and lookup operations.
 * 
 * @author Arya Karnik
 */
public class ProviderDatabase {
    private final Map<Integer, Provider> providers = new HashMap<>();

    /**
     * Add a new provider to the database.
     * @param provider the provider to add
     * @return true if added successfully, false if number already exists
     */
    public boolean addProvider(Provider provider) {
        if (providers.containsKey(provider.getNumber())) {
            return false;
        }
        providers.put(provider.getNumber(), provider);
        return true;
    }

    /**
     * Delete a provider from the database.
     * @param providerNumber the provider number to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteProvider(int providerNumber) {
        return providers.remove(providerNumber) != null;
    }

    /**
     * Update an existing provider record.
     * @param provider the updated provider data
     * @return true if updated, false if provider not found
     */
    public boolean updateProvider(Provider provider) {
        if (!providers.containsKey(provider.getNumber())) {
            return false;
        }
        providers.put(provider.getNumber(), provider);
        return true;
    }

    /**
     * Look up a provider by number.
     * @param providerNumber the provider number
     * @return the Provider if found, null otherwise
     */
    public Provider getProvider(int providerNumber) {
        return providers.get(providerNumber);
    }

    /**
     * Validate a provider number.
     * @param providerNumber the provider number to validate
     * @return true if the provider exists
     */
    public boolean validateProvider(int providerNumber) {
        return providers.containsKey(providerNumber);
    }

    /**
     * Get all providers in the database.
     * @return list of all providers
     */
    public List<Provider> getAllProviders() {
        return new ArrayList<>(providers.values());
    }

    /**
     * Get the total number of providers.
     * @return count of providers
     */
    public int size() {
        return providers.size();
    }
}
