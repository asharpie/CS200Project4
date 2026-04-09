package com.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a record of a service provided to a member.
 * Written to disk when a provider bills for a service.
 * 
 * @author Tim Madden
 */
public class ServiceRecord {
    private LocalDateTime currentDateTime;  // MM-DD-YYYY HH:MM:SS
    private LocalDate serviceDate;          // MM-DD-YYYY
    private int providerNumber;             // 9 digits
    private int memberNumber;               // 9 digits
    private int serviceCode;                // 6 digits
    private String comments;                // up to 100 characters (optional)

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public ServiceRecord(LocalDateTime currentDateTime, LocalDate serviceDate,
                         int providerNumber, int memberNumber, int serviceCode, String comments) {
        this.currentDateTime = currentDateTime;
        this.serviceDate = serviceDate;
        this.providerNumber = providerNumber;
        this.memberNumber = memberNumber;
        this.serviceCode = serviceCode;
        this.comments = comments;
    }

    public LocalDateTime getCurrentDateTime() { return currentDateTime; }
    public void setCurrentDateTime(LocalDateTime currentDateTime) { this.currentDateTime = currentDateTime; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public int getProviderNumber() { return providerNumber; }
    public void setProviderNumber(int providerNumber) { this.providerNumber = providerNumber; }

    public int getMemberNumber() { return memberNumber; }
    public void setMemberNumber(int memberNumber) { this.memberNumber = memberNumber; }

    public int getServiceCode() { return serviceCode; }
    public void setServiceCode(int serviceCode) { this.serviceCode = serviceCode; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    @Override
    public String toString() {
        return String.format("ServiceRecord[dateTime=%s, serviceDate=%s, provider=%09d, member=%09d, service=%06d, comments=%s]",
                currentDateTime.format(DATE_TIME_FORMAT),
                serviceDate.format(DATE_FORMAT),
                providerNumber, memberNumber, serviceCode,
                comments != null ? comments : "");
    }
}
