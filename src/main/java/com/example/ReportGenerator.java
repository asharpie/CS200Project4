package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates all reports for the ChocAn system:
 * - Member reports
 * - Provider reports
 * - Summary report (for manager / accounts payable)
 * - EFT data file
 *
 * The main accounting procedure saves all reports to a timestamped folder
 * under {@code reports/} (e.g., {@code reports/Accounting_Report_MM-dd-yyyy_HH-mm-ss/}).
 * Individual report methods can also write to a specified output directory.
 * 
 * @author Jackie Clayton
 */
public class ReportGenerator {
    private final MemberDatabase memberDb;
    private final ProviderDatabase providerDb;
    private final ServiceDirectory serviceDir;
    private final ServiceRecordDatabase recordDb;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public ReportGenerator(MemberDatabase memberDb, ProviderDatabase providerDb,
                           ServiceDirectory serviceDir, ServiceRecordDatabase recordDb) {
        this.memberDb = memberDb;
        this.providerDb = providerDb;
        this.serviceDir = serviceDir;
        this.recordDb = recordDb;
    }

    /**
     * Generate a report for a specific member.
     * Lists services provided to the member, sorted by service date.
     * File name: MemberName_MM-DD-YYYY.txt
     * @param memberNumber the member to generate a report for
     * @return the filename of the generated report, or null if the member has no records
     */
    public String generateMemberReport(int memberNumber) {
        return generateMemberReport(memberNumber, null);
    }

    public String generateMemberReport(int memberNumber, String outputDir) {
        Member member = memberDb.getMember(memberNumber);
        if (member == null) return null;

        List<ServiceRecord> records = recordDb.getRecordsForMember(memberNumber);
        if (records.isEmpty()) return null;

        records.sort(Comparator.comparing(ServiceRecord::getServiceDate));

        String dateStr = LocalDate.now().format(DATE_FORMAT);
        String filename = member.getName().replaceAll("\\s+", "_") + "_" + dateStr + ".txt";
        if (outputDir != null) filename = outputDir + File.separator + filename;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== MEMBER REPORT ===");
            writer.printf("Member name: %s%n", member.getName());
            writer.printf("Member number: %09d%n", member.getNumber());
            writer.printf("Member street address: %s%n", member.getAddress());
            writer.printf("Member city: %s%n", member.getCity());
            writer.printf("Member state: %s%n", member.getState());
            writer.printf("Member ZIP code: %s%n", member.getZip());
            writer.println();
            writer.println("Services provided:");
            writer.println("--------------------------------------------------");

            for (ServiceRecord r : records) {
                Provider provider = providerDb.getProvider(r.getProviderNumber());
                Service service = serviceDir.getService(r.getServiceCode());
                writer.printf("  Date of service: %s%n", r.getServiceDate().format(DATE_FORMAT));
                writer.printf("  Provider name: %s%n", provider != null ? provider.getName() : "Unknown");
                writer.printf("  Service name: %s%n", service != null ? service.getName() : "Unknown");
                writer.println();
            }
            writer.println("=== END OF REPORT ===");
        } catch (IOException e) {
            System.err.println("Error writing member report: " + e.getMessage());
            return null;
        }

        return filename;
    }

    /**
     * Generate a report for a specific provider.
     * Lists services provided, with a summary of consultations and total fee.
     * File name: ProviderName_MM-DD-YYYY.txt
     * @param providerNumber the provider to generate a report for
     * @return the filename of the generated report, or null if the provider has no records
     */
    public String generateProviderReport(int providerNumber) {
        return generateProviderReport(providerNumber, null);
    }

    public String generateProviderReport(int providerNumber, String outputDir) {
        Provider provider = providerDb.getProvider(providerNumber);
        if (provider == null) return null;

        List<ServiceRecord> records = recordDb.getRecordsForProvider(providerNumber);
        if (records.isEmpty()) return null;

        String dateStr = LocalDate.now().format(DATE_FORMAT);
        String filename = provider.getName().replaceAll("\\s+", "_") + "_" + dateStr + ".txt";
        if (outputDir != null) filename = outputDir + File.separator + filename;

        int totalConsultations = records.size();
        double totalFee = 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== PROVIDER REPORT ===");
            writer.printf("Provider name: %s%n", provider.getName());
            writer.printf("Provider number: %09d%n", provider.getNumber());
            writer.printf("Provider street address: %s%n", provider.getAddress());
            writer.printf("Provider city: %s%n", provider.getCity());
            writer.printf("Provider state: %s%n", provider.getState());
            writer.printf("Provider ZIP code: %s%n", provider.getZip());
            writer.println();
            writer.println("Services provided:");
            writer.println("--------------------------------------------------");

            for (ServiceRecord r : records) {
                Member member = memberDb.getMember(r.getMemberNumber());
                Service service = serviceDir.getService(r.getServiceCode());
                double fee = service != null ? service.getFee() : 0;
                totalFee += fee;

                writer.printf("  Date of service: %s%n", r.getServiceDate().format(DATE_FORMAT));
                writer.printf("  Date and time received: %s%n", r.getCurrentDateTime().format(ServiceRecord.DATE_TIME_FORMAT));
                writer.printf("  Member name: %s%n", member != null ? member.getName() : "Unknown");
                writer.printf("  Member number: %09d%n", r.getMemberNumber());
                writer.printf("  Service code: %06d%n", r.getServiceCode());
                writer.printf("  Fee to be paid: $%.2f%n", fee);
                writer.println();
            }

            writer.println("--------------------------------------------------");
            writer.printf("Total number of consultations: %d%n", totalConsultations);
            writer.printf("Total fee for week: $%.2f%n", totalFee);
            writer.println("=== END OF REPORT ===");
        } catch (IOException e) {
            System.err.println("Error writing provider report: " + e.getMessage());
            return null;
        }

        return filename;
    }

    /**
     * Generate the summary report for the manager (accounts payable).
     * Lists every provider to be paid, number of consultations, and total fee.
     * Also prints overall totals.
     * @return the filename of the generated report
     */
    public String generateSummaryReport() {
        return generateSummaryReport(null);
    }

    public String generateSummaryReport(String outputDir) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        String filename = "SummaryReport_" + dateStr + ".txt";
        if (outputDir != null) filename = outputDir + File.separator + filename;

        // Find all providers who provided services this week
        Set<Integer> activeProviders = new HashSet<>();
        for (ServiceRecord r : recordDb.getAllRecords()) {
            activeProviders.add(r.getProviderNumber());
        }

        int totalProviders = 0;
        int totalConsultations = 0;
        double overallFee = 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== SUMMARY REPORT FOR ACCOUNTS PAYABLE ===");
            writer.printf("Report date: %s%n", dateStr);
            writer.println();
            writer.printf("%-25s  %12s  %10s%n", "Provider", "Consultations", "Total Fee");
            writer.println("----------------------------------------------------------");

            for (int provNum : activeProviders) {
                Provider provider = providerDb.getProvider(provNum);
                List<ServiceRecord> records = recordDb.getRecordsForProvider(provNum);
                int consultations = records.size();
                double fee = 0;
                for (ServiceRecord r : records) {
                    Service s = serviceDir.getService(r.getServiceCode());
                    if (s != null) fee += s.getFee();
                }

                writer.printf("%-25s  %12d  $%9.2f%n",
                        provider != null ? provider.getName() : "Unknown",
                        consultations, fee);

                totalProviders++;
                totalConsultations += consultations;
                overallFee += fee;
            }

            writer.println("----------------------------------------------------------");
            writer.printf("Total number of providers who provided services: %d%n", totalProviders);
            writer.printf("Total number of consultations: %d%n", totalConsultations);
            writer.printf("Overall fee total: $%.2f%n", overallFee);
            writer.println("=== END OF SUMMARY REPORT ===");
        } catch (IOException e) {
            System.err.println("Error writing summary report: " + e.getMessage());
            return null;
        }

        return filename;
    }

    /**
     * Generate the EFT (Electronic Funds Transfer) data file.
     * Contains provider name, provider number, and amount to be transferred.
     * @return the filename of the generated EFT file
     */
    public String generateEFTData() {
        return generateEFTData(null);
    }

    public String generateEFTData(String outputDir) {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        String filename = "EFT_Data_" + dateStr + ".txt";
        if (outputDir != null) filename = outputDir + File.separator + filename;

        // Calculate fees per provider
        Map<Integer, Double> providerFees = new HashMap<>();
        for (ServiceRecord r : recordDb.getAllRecords()) {
            Service s = serviceDir.getService(r.getServiceCode());
            double fee = s != null ? s.getFee() : 0;
            providerFees.merge(r.getProviderNumber(), fee, Double::sum);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== EFT DATA ===");
            for (Map.Entry<Integer, Double> entry : providerFees.entrySet()) {
                Provider provider = providerDb.getProvider(entry.getKey());
                writer.printf("Provider name: %s%n", provider != null ? provider.getName() : "Unknown");
                writer.printf("Provider number: %09d%n", entry.getKey());
                writer.printf("Amount to be transferred: $%.2f%n", entry.getValue());
                writer.println();
            }
            writer.println("=== END OF EFT DATA ===");
        } catch (IOException e) {
            System.err.println("Error writing EFT data: " + e.getMessage());
            return null;
        }

        return filename;
    }

    /**
     * Run the main accounting procedure.
     * Generates member reports, provider reports, summary report, and EFT data.
     * This runs at midnight on Friday or can be triggered manually by a manager.
     */
    public void runAccountingProcedure() {
        runAccountingProcedureToFolder();
    }

    public String runAccountingProcedureToFolder() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss"));
        String folderName = "reports" + File.separator + "Accounting_Report_" + timestamp;
        new File(folderName).mkdirs();

        System.out.println("\n=== RUNNING MAIN ACCOUNTING PROCEDURE ===");
        System.out.println("Saving reports to: " + folderName);

        // Generate member reports for all members who received services
        Set<Integer> activeMemberNumbers = new HashSet<>();
        for (ServiceRecord r : recordDb.getAllRecords()) {
            activeMemberNumbers.add(r.getMemberNumber());
        }

        System.out.println("Generating member reports...");
        for (int memberNum : activeMemberNumbers) {
            String file = generateMemberReport(memberNum, folderName);
            if (file != null) {
                System.out.println("  Generated: " + file);
            }
        }

        // Generate provider reports for all providers who billed services
        Set<Integer> activeProviderNumbers = new HashSet<>();
        for (ServiceRecord r : recordDb.getAllRecords()) {
            activeProviderNumbers.add(r.getProviderNumber());
        }

        System.out.println("Generating provider reports...");
        for (int provNum : activeProviderNumbers) {
            String file = generateProviderReport(provNum, folderName);
            if (file != null) {
                System.out.println("  Generated: " + file);
            }
        }

        // Generate summary report
        System.out.println("Generating summary report...");
        String summaryFile = generateSummaryReport(folderName);
        if (summaryFile != null) {
            System.out.println("  Generated: " + summaryFile);
        }

        // Generate EFT data
        System.out.println("Generating EFT data...");
        String eftFile = generateEFTData(folderName);
        if (eftFile != null) {
            System.out.println("  Generated: " + eftFile);
        }

        System.out.println("=== ACCOUNTING PROCEDURE COMPLETE ===");
        return folderName;
    }
}
