package com.example;

/**
 * Represents a ChocAn healthcare provider.
 * Providers deliver services (consultations, treatments) to ChocAn members.
 * 
 * @author Arya Karnik
 */
public class Provider {
    private String name;        // 25 characters
    private int number;         // 9 digits
    private String address;     // 25 characters
    private String city;        // 14 characters
    private String state;       // 2 letters
    private String zip;         // 5 digits

    public Provider(String name, int number, String address, String city, String state, String zip) {
        this.name = name;
        this.number = number;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    @Override
    public String toString() {
        return String.format("Provider[name=%s, number=%09d, address=%s, city=%s, state=%s, zip=%s]",
                name, number, address, city, state, zip);
    }
}
