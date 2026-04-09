package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests by Arya Karnik.
 * Tests ProviderDatabase methods (own code) and MemberDatabase.deleteMember (Timothy's code).
 *
 * @author Arya Karnik
 */
public class AryaKarnikTest {
    private ProviderDatabase providerDb;
    private MemberDatabase memberDb;
    private Provider provider;

    @Before
    public void setUp() {
        providerDb = new ProviderDatabase();
        memberDb = new MemberDatabase();
        provider = new Provider("Dr. Jones", 200000001, "789 Elm St", "Mobile", "AL", "36601");
    }

    // ---- Own method 1: ProviderDatabase.addProvider() ----

    @Test
    public void testAddProvider_success() {
        assertTrue(providerDb.addProvider(provider));
        assertEquals(1, providerDb.size());
    }

    @Test
    public void testAddProvider_duplicateFails() {
        providerDb.addProvider(provider);
        assertFalse(providerDb.addProvider(provider));
        assertEquals(1, providerDb.size());
    }

    // ---- Own method 2: ProviderDatabase.validateProvider() ----

    @Test
    public void testValidateProvider_valid() {
        providerDb.addProvider(provider);
        assertTrue(providerDb.validateProvider(200000001));
    }

    @Test
    public void testValidateProvider_invalid() {
        assertFalse(providerDb.validateProvider(999999999));
    }

    // ---- Cross-member method: MemberDatabase.deleteMember() (Timothy Sazonov) ----

    @Test
    public void testDeleteMember_success() {
        Member m = new Member("Alice Brown", 100000003, "321 Pine Rd", "Huntsville", "AL", "35801");
        memberDb.addMember(m);
        assertTrue(memberDb.deleteMember(100000003));
        assertEquals(0, memberDb.size());
    }

    @Test
    public void testDeleteMember_notFound() {
        assertFalse(memberDb.deleteMember(999999999));
    }
}
