package com.example;

/**
 * Represents a ChocAn member.
 * Members pay monthly fees and receive consultations/treatments from providers.
 * 
 * @author Timothy Sazonov
 */
public class Member {
    private String name;        // 25 characters
    private int number;         // 9 digits
    private String address;     // 25 characters
    private String city;        // 14 characters
    private String state;       // 2 letters
    private String zip;         // 5 digits
    private boolean suspended;  // true if fees are owed

    public Member(String name, int number, String address, String city, String state, String zip) {
        this.name = name;
        this.number = number;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.suspended = false;
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

    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }

    @Override
    public String toString() {
        return String.format("Member[name=%s, number=%09d, address=%s, city=%s, state=%s, zip=%s, suspended=%b]",
                name, number, address, city, state, zip, suspended);
    }
}
