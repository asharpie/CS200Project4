package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * JUnit tests by Joel Nelems.
 * Tests ServiceDirectory methods (own code) and ProviderDatabase.getProvider (Arya's code).
 *
 * @author Joel Nelems
 */
public class JoelNelemsTest {
    private ServiceDirectory serviceDir;
    private ProviderDatabase providerDb;

    @Before
    public void setUp() {
        serviceDir = new ServiceDirectory();
        providerDb = new ProviderDatabase();
    }

    // ---- Own method 1: ServiceDirectory.addService() ----

    @Test
    public void testAddService_success() {
        Service s = new Service(100000, "Dietitian Session", 150.00);
        assertTrue(serviceDir.addService(s));
        assertEquals(1, serviceDir.size());
    }

    @Test
    public void testAddService_duplicateFails() {
        Service s = new Service(100000, "Dietitian Session", 150.00);
        serviceDir.addService(s);
        assertFalse(serviceDir.addService(s));
        assertEquals(1, serviceDir.size());
    }

    // ---- Own method 2: ServiceDirectory.getAllServicesSorted() ----

    @Test
    public void testGetAllServicesSorted_alphabetical() {
        serviceDir.addService(new Service(100000, "Yoga Class", 50.00));
        serviceDir.addService(new Service(200000, "Aerobics", 75.00));
        serviceDir.addService(new Service(300000, "Massage", 120.00));

        List<Service> sorted = serviceDir.getAllServicesSorted();
        assertEquals(3, sorted.size());
        assertEquals("Aerobics", sorted.get(0).getName());
        assertEquals("Massage", sorted.get(1).getName());
        assertEquals("Yoga Class", sorted.get(2).getName());
    }

    @Test
    public void testGetAllServicesSorted_emptyDirectory() {
        List<Service> sorted = serviceDir.getAllServicesSorted();
        assertTrue(sorted.isEmpty());
    }

    // ---- Cross-member method: ProviderDatabase.getProvider() (Arya Karnik) ----

    @Test
    public void testGetProvider_found() {
        Provider p = new Provider("Dr. Adams", 200000002, "555 Oak Blvd", "Auburn", "AL", "36830");
        providerDb.addProvider(p);
        Provider found = providerDb.getProvider(200000002);
        assertNotNull(found);
        assertEquals("Dr. Adams", found.getName());
    }

    @Test
    public void testGetProvider_notFound() {
        assertNull(providerDb.getProvider(999999999));
    }
}
