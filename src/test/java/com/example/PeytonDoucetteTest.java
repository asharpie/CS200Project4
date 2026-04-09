package com.example;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JUnit tests by Peyton Doucette.
 * Tests ReportGenerator.runAccountingProcedure via ManagerTerminal delegation (own code)
 * and ServiceRecordDatabase.clearRecords (Tim Madden's code).
 *
 * @author Peyton Doucette
 */
public class PeytonDoucetteTest {
    private MemberDatabase memberDb;
    private ProviderDatabase providerDb;
    private ServiceDirectory serviceDir;
    private ServiceRecordDatabase recordDb;
    private ReportGenerator reportGenerator;

    @Before
    public void setUp() {
        memberDb = new MemberDatabase();
        providerDb = new ProviderDatabase();
        serviceDir = new ServiceDirectory();
        recordDb = new ServiceRecordDatabase();
        reportGenerator = new ReportGenerator(memberDb, providerDb, serviceDir, recordDb);

        memberDb.addMember(new Member("Alice Brown", 100000001, "123 Main St", "Tuscaloosa", "AL", "35401"));
        providerDb.addProvider(new Provider("Dr. Jones", 200000001, "789 Elm St", "Mobile", "AL", "36601"));
        serviceDir.addService(new Service(100000, "Dietitian Session", 150.00));
    }

    @After
    public void cleanUp() {
        // Clean up generated report files
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        new File("Alice_Brown_" + dateStr + ".txt").delete();
        new File("Dr._Jones_" + dateStr + ".txt").delete();
        new File("SummaryReport_" + dateStr + ".txt").delete();
        new File("EFT_Data_" + dateStr + ".txt").delete();
    }

    // ---- Own method 1: ReportGenerator.generateSummaryReport() (called by ManagerTerminal) ----

    @Test
    public void testGenerateSummaryReport_withRecords() {
        recordDb.addRecord(new ServiceRecord(
                LocalDateTime.now(), LocalDate.now(), 200000001, 100000001, 100000, "Test"));
        String filename = reportGenerator.generateSummaryReport();
        assertNotNull(filename);
        assertTrue(new File(filename).exists());
    }

    @Test
    public void testGenerateSummaryReport_noRecords() {
        String filename = reportGenerator.generateSummaryReport();
        assertNotNull(filename);
        assertTrue(new File(filename).exists());
    }

    // ---- Own method 2: ReportGenerator.runAccountingProcedure() (called by ManagerTerminal) ----

    @Test
    public void testRunAccountingProcedure_generatesFiles() {
        recordDb.addRecord(new ServiceRecord(
                LocalDateTime.now(), LocalDate.now(), 200000001, 100000001, 100000, "Test"));
        reportGenerator.runAccountingProcedure();

        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        assertTrue(new File("SummaryReport_" + dateStr + ".txt").exists());
        assertTrue(new File("EFT_Data_" + dateStr + ".txt").exists());
    }

    @Test
    public void testRunAccountingProcedure_noRecords() {
        // Should not throw even with empty records
        reportGenerator.runAccountingProcedure();
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        assertTrue(new File("SummaryReport_" + dateStr + ".txt").exists());
    }

    // ---- Cross-member method: ServiceRecordDatabase.clearRecords() (Tim Madden) ----

    @Test
    public void testClearRecords() {
        recordDb.addRecord(new ServiceRecord(
                LocalDateTime.now(), LocalDate.now(), 200000001, 100000001, 100000, "Test"));
        assertEquals(1, recordDb.size());
        recordDb.clearRecords();
        assertEquals(0, recordDb.size());
    }

    @Test
    public void testClearRecords_alreadyEmpty() {
        assertEquals(0, recordDb.size());
        recordDb.clearRecords();
        assertEquals(0, recordDb.size());
    }
}
