package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages member records in the ChocAn Data Center.
 * Supports add, delete, update, and lookup operations.
 * 
 * @author Timothy Sazonov
 */
public class MemberDatabase {
    private final Map<Integer, Member> members = new HashMap<>();

    /**
     * Add a new member to the database.
     * @param member the member to add
     * @return true if added successfully, false if number already exists
     */
    public boolean addMember(Member member) {
        if (members.containsKey(member.getNumber())) {
            return false;
        }
        members.put(member.getNumber(), member);
        return true;
    }

    /**
     * Delete a member from the database.
     * @param memberNumber the member number to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteMember(int memberNumber) {
        return members.remove(memberNumber) != null;
    }

    /**
     * Update an existing member record.
     * @param member the updated member data
     * @return true if updated, false if member not found
     */
    public boolean updateMember(Member member) {
        if (!members.containsKey(member.getNumber())) {
            return false;
        }
        members.put(member.getNumber(), member);
        return true;
    }

    /**
     * Look up a member by number.
     * @param memberNumber the member number
     * @return the Member if found, null otherwise
     */
    public Member getMember(int memberNumber) {
        return members.get(memberNumber);
    }

    /**
     * Validate a member number. Returns a status string.
     * @param memberNumber the member number to validate
     * @return "Validated" if active, "Member suspended" if suspended, "Invalid number" if not found
     */
    public String validateMember(int memberNumber) {
        Member member = members.get(memberNumber);
        if (member == null) {
            return "Invalid number";
        }
        if (member.isSuspended()) {
            return "Member suspended";
        }
        return "Validated";
    }

    /**
     * Get all members in the database.
     * @return list of all members
     */
    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    /**
     * Get the total number of members.
     * @return count of members
     */
    public int size() {
        return members.size();
    }
}
