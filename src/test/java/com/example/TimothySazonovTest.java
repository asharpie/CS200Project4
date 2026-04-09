package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests by Timothy Sazonov.
 * Tests MemberDatabase methods (own code) and ServiceDirectory.getService (Joel's code).
 *
 * @author Timothy Sazonov
 */
public class TimothySazonovTest {
    private MemberDatabase memberDb;
    private ServiceDirectory serviceDir;
    private Member member;

    @Before
    public void setUp() {
        memberDb = new MemberDatabase();
        serviceDir = new ServiceDirectory();
        member = new Member("John Smith", 100000001, "123 Main St", "Tuscaloosa", "AL", "35401");
    }

    // ---- Own method 1: MemberDatabase.addMember() ----

    @Test
    public void testAddMember_success() {
        assertTrue(memberDb.addMember(member));
        assertEquals(1, memberDb.size());
    }

    @Test
    public void testAddMember_duplicateFails() {
        memberDb.addMember(member);
        assertFalse(memberDb.addMember(member));
        assertEquals(1, memberDb.size());
    }

    // ---- Own method 2: MemberDatabase.validateMember() ----

    @Test
    public void testValidateMember_active() {
        memberDb.addMember(member);
        assertEquals("Validated", memberDb.validateMember(100000001));
    }

    @Test
    public void testValidateMember_suspended() {
        member.setSuspended(true);
        memberDb.addMember(member);
        assertEquals("Member suspended", memberDb.validateMember(100000001));
    }

    @Test
    public void testValidateMember_invalidNumber() {
        assertEquals("Invalid number", memberDb.validateMember(999999999));
    }

    // ---- Cross-member method: ServiceDirectory.getService() (Joel Nelems) ----

    @Test
    public void testGetService_found() {
        Service s = new Service(100000, "Dietitian Session", 150.00);
        serviceDir.addService(s);
        assertNotNull(serviceDir.getService(100000));
        assertEquals("Dietitian Session", serviceDir.getService(100000).getName());
    }

    @Test
    public void testGetService_notFound() {
        assertNull(serviceDir.getService(999999));
    }
}
