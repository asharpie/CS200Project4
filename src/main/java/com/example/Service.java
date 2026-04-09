package com.example;

/**
 * Represents a service entry in the Provider Directory.
 * Each service has a code, name, and associated fee.
 * 
 * @author Joel Nelems
 */
public class Service {
    private int code;       // 6 digits
    private String name;    // up to 20 characters
    private double fee;     // up to $999.99

    public Service(int code, String name, double fee) {
        this.code = code;
        this.name = name;
        this.fee = fee;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    @Override
    public String toString() {
        return String.format("Service[code=%06d, name=%s, fee=$%.2f]", code, name, fee);
    }
}
