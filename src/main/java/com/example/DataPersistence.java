package com.example;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles saving and loading all ChocAn data to/from files for persistence.
 * Data is stored in the data/ directory as pipe-delimited text files.
 * 
 * @author Peyton Doucette
 */
public class DataPersistence {
    private static final String DATA_DIR = "data";
    private static final String MEMBERS_FILE = DATA_DIR + "/members.dat";
    private static final String PROVIDERS_FILE = DATA_DIR + "/providers.dat";
    private static final String SERVICES_FILE = DATA_DIR + "/services.dat";
    private static final String RECORDS_FILE = DATA_DIR + "/records.dat";

    /**
     * Save all database contents to files.
     */
    public static void saveAll(MemberDatabase memberDb, ProviderDatabase providerDb,
                               ServiceDirectory serviceDir, ServiceRecordDatabase recordDb) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        saveMembers(memberDb);
        saveProviders(providerDb);
        saveServices(serviceDir);
        saveRecords(recordDb);
        System.out.println("All data saved to " + DATA_DIR + "/ directory.");
    }

    /**
     * Load all database contents from files.
     * @return true if data files were found and loaded, false if no data files exist
     */
    public static boolean loadAll(MemberDatabase memberDb, ProviderDatabase providerDb,
                                  ServiceDirectory serviceDir, ServiceRecordDatabase recordDb) {
        File dir = new File(DATA_DIR);
        if (!dir.exists() || !new File(MEMBERS_FILE).exists()) {
            return false;
        }

        loadMembers(memberDb);
        loadProviders(providerDb);
        loadServices(serviceDir);
        loadRecords(recordDb);
        System.out.println("Data loaded from " + DATA_DIR + "/ directory.");
        return true;
    }

    private static void saveMembers(MemberDatabase memberDb) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : memberDb.getAllMembers()) {
                writer.printf("%s|%d|%s|%s|%s|%s|%b%n",
                        m.getName(), m.getNumber(), m.getAddress(),
                        m.getCity(), m.getState(), m.getZip(), m.isSuspended());
            }
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
        }
    }

    private static void loadMembers(MemberDatabase memberDb) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MEMBERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 7) {
                    Member m = new Member(parts[0], Integer.parseInt(parts[1]),
                            parts[2], parts[3], parts[4], parts[5]);
                    m.setSuspended(Boolean.parseBoolean(parts[6]));
                    memberDb.addMember(m);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading members: " + e.getMessage());
        }
    }

    private static void saveProviders(ProviderDatabase providerDb) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROVIDERS_FILE))) {
            for (Provider p : providerDb.getAllProviders()) {
                writer.printf("%s|%d|%s|%s|%s|%s%n",
                        p.getName(), p.getNumber(), p.getAddress(),
                        p.getCity(), p.getState(), p.getZip());
            }
        } catch (IOException e) {
            System.err.println("Error saving providers: " + e.getMessage());
        }
    }

    private static void loadProviders(ProviderDatabase providerDb) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PROVIDERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 6) {
                    Provider p = new Provider(parts[0], Integer.parseInt(parts[1]),
                            parts[2], parts[3], parts[4], parts[5]);
                    providerDb.addProvider(p);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading providers: " + e.getMessage());
        }
    }

    private static void saveServices(ServiceDirectory serviceDir) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SERVICES_FILE))) {
            for (Service s : serviceDir.getAllServicesSorted()) {
                writer.printf("%d|%s|%.2f%n", s.getCode(), s.getName(), s.getFee());
            }
        } catch (IOException e) {
            System.err.println("Error saving services: " + e.getMessage());
        }
    }

    private static void loadServices(ServiceDirectory serviceDir) {
        try (BufferedReader reader = new BufferedReader(new FileReader(SERVICES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 3) {
                    Service s = new Service(Integer.parseInt(parts[0]),
                            parts[1], Double.parseDouble(parts[2]));
                    serviceDir.addService(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading services: " + e.getMessage());
        }
    }

    private static void saveRecords(ServiceRecordDatabase recordDb) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RECORDS_FILE))) {
            for (ServiceRecord r : recordDb.getAllRecords()) {
                writer.printf("%s|%s|%d|%d|%d|%s%n",
                        r.getCurrentDateTime().toString(),
                        r.getServiceDate().toString(),
                        r.getProviderNumber(), r.getMemberNumber(),
                        r.getServiceCode(),
                        r.getComments() != null ? r.getComments() : "");
            }
        } catch (IOException e) {
            System.err.println("Error saving records: " + e.getMessage());
        }
    }

    private static void loadRecords(ServiceRecordDatabase recordDb) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RECORDS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 6) {
                    LocalDateTime dateTime = LocalDateTime.parse(parts[0]);
                    LocalDate serviceDate = LocalDate.parse(parts[1]);
                    int providerNum = Integer.parseInt(parts[2]);
                    int memberNum = Integer.parseInt(parts[3]);
                    int serviceCode = Integer.parseInt(parts[4]);
                    String comments = parts[5].isEmpty() ? null : parts[5];
                    ServiceRecord r = new ServiceRecord(dateTime, serviceDate,
                            providerNum, memberNum, serviceCode, comments);
                    recordDb.addRecord(r);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading records: " + e.getMessage());
        }
    }
}
