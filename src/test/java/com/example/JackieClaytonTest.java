package com.example;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JUnit tests by Jackie Clayton.
 * Tests ReportGenerator methods (own code) and MemberDatabase.updateMember (Timothy's code).
 *
 * @author Jackie Clayton
 */
public class JackieClaytonTest {
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
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        new File("Alice_Brown_" + dateStr + ".txt").delete();
        new File("Dr._Jones_" + dateStr + ".txt").delete();
    }

    // ---- Own method 1: ReportGenerator.generateMemberReport() ----

    @Test
    public void testGenerateMemberReport_success() {
        recordDb.addRecord(new ServiceRecord(
                LocalDateTime.now(), LocalDate.now(), 200000001, 100000001, 100000, "Consultation"));
        String filename = reportGenerator.generateMemberReport(100000001);
        assertNotNull(filename);
        assertTrue(filename.contains("Alice_Brown"));
        assertTrue(new File(filename).exists());
    }

    @Test
    public void testGenerateMemberReport_noRecords() {
        String filename = reportGenerator.generateMemberReport(100000001);
        assertNull(filename);
    }

    @Test
    public void testGenerateMemberReport_invalidMember() {
        String filename = reportGenerator.generateMemberReport(999999999);
        assertNull(filename);
    }

    // ---- Own method 2: ReportGenerator.generateProviderReport() ----

    @Test
    public void testGenerateProviderReport_success() {
        recordDb.addRecord(new ServiceRecord(
                LocalDateTime.now(), LocalDate.now(), 200000001, 100000001, 100000, "Consultation"));
        String filename = reportGenerator.generateProviderReport(200000001);
        assertNotNull(filename);
        assertTrue(filename.contains("Dr._Jones"));
        assertTrue(new File(filename).exists());
    }

    @Test
    public void testGenerateProviderReport_noRecords() {
        String filename = reportGenerator.generateProviderReport(200000001);
        assertNull(filename);
    }

    @Test
    public void testGenerateProviderReport_invalidProvider() {
        String filename = reportGenerator.generateProviderReport(999999999);
        assertNull(filename);
    }

    // ---- Cross-member method: MemberDatabase.updateMember() (Timothy Sazonov) ----

    @Test
    public void testUpdateMember_success() {
        Member updated = new Member("Alice Updated", 100000001, "New Addr", "New City", "GA", "30301");
        assertTrue(memberDb.updateMember(updated));
        assertEquals("Alice Updated", memberDb.getMember(100000001).getName());
    }

    @Test
    public void testUpdateMember_notFound() {
        Member ghost = new Member("Ghost", 999999999, "Nowhere", "None", "XX", "00000");
        assertFalse(memberDb.updateMember(ghost));
    }
}
