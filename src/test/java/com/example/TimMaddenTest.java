package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JUnit tests by Tim Madden.
 * Tests ServiceRecordDatabase methods (own code) and ServiceDirectory.size (Joel's code).
 *
 * @author Tim Madden
 */
public class TimMaddenTest {
    private ServiceRecordDatabase recordDb;
    private ServiceDirectory serviceDir;
    private ServiceRecord record1;
    private ServiceRecord record2;

    @Before
    public void setUp() {
        recordDb = new ServiceRecordDatabase();
        serviceDir = new ServiceDirectory();
        record1 = new ServiceRecord(
                LocalDateTime.of(2026, 4, 7, 10, 30, 0),
                LocalDate.of(2026, 4, 5),
                200000001, 100000001, 100000, "First session");
        record2 = new ServiceRecord(
                LocalDateTime.of(2026, 4, 7, 14, 0, 0),
                LocalDate.of(2026, 4, 6),
                200000001, 100000002, 200000, "Follow-up");
    }

    // ---- Own method 1: ServiceRecordDatabase.addRecord() ----

    @Test
    public void testAddRecord_success() {
        recordDb.addRecord(record1);
        assertEquals(1, recordDb.size());
    }

    @Test
    public void testAddRecord_multipleRecords() {
        recordDb.addRecord(record1);
        recordDb.addRecord(record2);
        assertEquals(2, recordDb.size());
    }

    // ---- Own method 2: ServiceRecordDatabase.getRecordsForMember() ----

    @Test
    public void testGetRecordsForMember_found() {
        recordDb.addRecord(record1);
        recordDb.addRecord(record2);
        List<ServiceRecord> results = recordDb.getRecordsForMember(100000001);
        assertEquals(1, results.size());
        assertEquals(100000001, results.get(0).getMemberNumber());
    }

    @Test
    public void testGetRecordsForMember_notFound() {
        recordDb.addRecord(record1);
        List<ServiceRecord> results = recordDb.getRecordsForMember(999999999);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetRecordsForMember_empty() {
        List<ServiceRecord> results = recordDb.getRecordsForMember(100000001);
        assertTrue(results.isEmpty());
    }

    // ---- Cross-member method: ServiceDirectory.size() (Joel Nelems) ----

    @Test
    public void testServiceDirectorySize_empty() {
        assertEquals(0, serviceDir.size());
    }

    @Test
    public void testServiceDirectorySize_afterAdding() {
        serviceDir.addService(new Service(100000, "Dietitian", 150.00));
        serviceDir.addService(new Service(200000, "Aerobics", 75.00));
        assertEquals(2, serviceDir.size());
    }
}
