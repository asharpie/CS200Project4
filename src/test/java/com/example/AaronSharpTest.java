package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests by Aaron Sharp.
 * Tests MemberDatabase and ProviderDatabase operations used by OperatorTerminal (own code)
 * and ServiceRecordDatabase.getRecordsForProvider (Tim Madden's code).
 *
 * @author Aaron Sharp
 */
public class AaronSharpTest {
    private MemberDatabase memberDb;
    private ProviderDatabase providerDb;
    private ServiceRecordDatabase recordDb;

    @Before
    public void setUp() {
        memberDb = new MemberDatabase();
        providerDb = new ProviderDatabase();
        recordDb = new ServiceRecordDatabase();
    }

    // ---- Own method 1: MemberDatabase add/delete operations (OperatorTerminal.addMember/deleteMember) ----

    @Test
    public void testOperatorAddMember_success() {
        Member m = new Member("Test User", 100000010, "100 Test Ln", "TestCity", "AL", "35401");
        assertTrue(memberDb.addMember(m));
        assertNotNull(memberDb.getMember(100000010));
        assertEquals("Test User", memberDb.getMember(100000010).getName());
    }

    @Test
    public void testOperatorAddMember_duplicate() {
        Member m = new Member("Test User", 100000010, "100 Test Ln", "TestCity", "AL", "35401");
        memberDb.addMember(m);
        assertFalse(memberDb.addMember(m));
    }

    @Test
    public void testOperatorDeleteMember_success() {
        Member m = new Member("Test User", 100000010, "100 Test Ln", "TestCity", "AL", "35401");
        memberDb.addMember(m);
        assertTrue(memberDb.deleteMember(100000010));
        assertNull(memberDb.getMember(100000010));
    }

    // ---- Own method 2: ProviderDatabase add/delete operations (OperatorTerminal.addProvider/deleteProvider) ----

    @Test
    public void testOperatorAddProvider_success() {
        Provider p = new Provider("Dr. Test", 200000010, "200 Test Dr", "TestCity", "AL", "35401");
        assertTrue(providerDb.addProvider(p));
        assertNotNull(providerDb.getProvider(200000010));
    }

    @Test
    public void testOperatorDeleteProvider_success() {
        Provider p = new Provider("Dr. Test", 200000010, "200 Test Dr", "TestCity", "AL", "35401");
        providerDb.addProvider(p);
        assertTrue(providerDb.deleteProvider(200000010));
        assertNull(providerDb.getProvider(200000010));
    }

    @Test
    public void testOperatorDeleteProvider_notFound() {
        assertFalse(providerDb.deleteProvider(999999999));
    }

    // ---- Cross-member method: ServiceRecordDatabase.getRecordsForProvider() (Tim Madden) ----

    @Test
    public void testGetRecordsForProvider_found() {
        ServiceRecord r = new ServiceRecord(
                java.time.LocalDateTime.now(), java.time.LocalDate.now(),
                200000001, 100000001, 100000, "Test");
        recordDb.addRecord(r);
        assertEquals(1, recordDb.getRecordsForProvider(200000001).size());
    }

    @Test
    public void testGetRecordsForProvider_empty() {
        assertEquals(0, recordDb.getRecordsForProvider(200000001).size());
    }
}
