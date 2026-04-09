package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Swing GUI for the ChocAn Data Processing System.
 * Provides graphical interfaces for Provider, Operator, and Manager terminals.
 *
 * @author Peyton Doucette
 */
public class ChocAnGUI extends JFrame {
    private final MemberDatabase memberDb;
    private final ProviderDatabase providerDb;
    private final ServiceDirectory serviceDir;
    private final ServiceRecordDatabase recordDb;
    private final ReportGenerator reportGenerator;

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextArea outputArea;

    private Provider currentProvider;

    public ChocAnGUI(MemberDatabase memberDb, ProviderDatabase providerDb,
                     ServiceDirectory serviceDir, ServiceRecordDatabase recordDb) {
        this.memberDb = memberDb;
        this.providerDb = providerDb;
        this.serviceDir = serviceDir;
        this.recordDb = recordDb;
        this.reportGenerator = new ReportGenerator(memberDb, providerDb, serviceDir, recordDb);

        setTitle("ChocAn Data Processing System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        setSize(900, 650);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createProviderPanel(), "PROVIDER");
        mainPanel.add(createOperatorPanel(), "OPERATOR");
        mainPanel.add(createManagerPanel(), "MANAGER");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Save data and exit?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataPersistence.saveAll(memberDb, providerDb, serviceDir, recordDb);
            dispose();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    // ==================== LOGIN PANEL ====================

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 60));

        // Title
        JLabel titleLabel = new JLabel("Chocoholics Anonymous (ChocAn)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));

        JLabel subtitleLabel = new JLabel("Data Processing System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(180, 180, 200));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 80, 200));

        JButton providerBtn = createStyledButton("Provider Login", new Color(46, 125, 50));
        JButton operatorBtn = createStyledButton("Operator Login", new Color(21, 101, 192));
        JButton managerBtn = createStyledButton("Manager Login", new Color(156, 39, 176));
        JButton accountingBtn = createStyledButton("Run Main Accounting Procedure", new Color(230, 126, 34));
        JButton exitBtn = createStyledButton("Exit", new Color(183, 28, 28));

        providerBtn.addActionListener(e -> providerLogin());
        operatorBtn.addActionListener(e -> operatorLogin());
        managerBtn.addActionListener(e -> managerLogin());
        accountingBtn.addActionListener(e -> runAccountingFromMain());
        exitBtn.addActionListener(e -> exitApplication());

        buttonPanel.add(providerBtn);
        buttonPanel.add(operatorBtn);
        buttonPanel.add(managerBtn);
        buttonPanel.add(accountingBtn);
        buttonPanel.add(exitBtn);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==================== PROVIDER PANEL ====================

    private JPanel createProviderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel header = new JLabel("  Provider Terminal", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(new Color(46, 125, 50));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton verifyBtn = new JButton("Verify Member");
        JButton billBtn = new JButton("Bill Service");
        JButton dirBtn = new JButton("Provider Directory");
        JButton logoutBtn = new JButton("Log Out");

        verifyBtn.addActionListener(e -> guiVerifyMember());
        billBtn.addActionListener(e -> guiBillService());
        dirBtn.addActionListener(e -> guiProviderDirectory());
        logoutBtn.addActionListener(e -> {
            currentProvider = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        buttonBar.add(verifyBtn);
        buttonBar.add(billBtn);
        buttonBar.add(dirBtn);
        buttonBar.add(logoutBtn);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(header, BorderLayout.NORTH);
        panel.add(buttonBar, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ==================== OPERATOR PANEL ====================

    private JPanel createOperatorPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel header = new JLabel("  Operator Terminal", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(new Color(21, 101, 192));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        JButton addMemberBtn = new JButton("Add Member");
        JButton editMemberBtn = new JButton("Edit Member");
        JButton deleteMemberBtn = new JButton("Delete Member");
        JButton addProvBtn = new JButton("Add Provider");
        JButton editProvBtn = new JButton("Edit Provider");
        JButton deleteProvBtn = new JButton("Delete Provider");
        JButton logoutBtn = new JButton("Log Out");

        addMemberBtn.addActionListener(e -> guiAddMember());
        editMemberBtn.addActionListener(e -> guiEditMember());
        deleteMemberBtn.addActionListener(e -> guiDeleteMember());
        addProvBtn.addActionListener(e -> guiAddProvider());
        editProvBtn.addActionListener(e -> guiEditProvider());
        deleteProvBtn.addActionListener(e -> guiDeleteProvider());
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        buttonBar.add(addMemberBtn);
        buttonBar.add(editMemberBtn);
        buttonBar.add(deleteMemberBtn);
        buttonBar.add(addProvBtn);
        buttonBar.add(editProvBtn);
        buttonBar.add(deleteProvBtn);
        buttonBar.add(logoutBtn);

        // Members and Providers tables
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Members", createMemberTablePanel());
        tabbedPane.addTab("Providers", createProviderTablePanel());

        panel.add(header, BorderLayout.NORTH);
        panel.add(buttonBar, BorderLayout.SOUTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMemberTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("Refresh");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Number", "Name", "Address", "City", "State", "ZIP", "Suspended"}, 0);
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable refreshMembers = () -> {
            model.setRowCount(0);
            for (Member m : memberDb.getAllMembers()) {
                model.addRow(new Object[]{
                        String.format("%09d", m.getNumber()), m.getName(), m.getAddress(),
                        m.getCity(), m.getState(), m.getZip(), m.isSuspended() ? "Yes" : "No"
                });
            }
        };
        refreshMembers.run();
        refreshBtn.addActionListener(e -> refreshMembers.run());

        panel.add(refreshBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Store refresh action for later use
        panel.putClientProperty("refresh", refreshMembers);
        return panel;
    }

    private JPanel createProviderTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("Refresh");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Number", "Name", "Address", "City", "State", "ZIP"}, 0);
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable refreshProviders = () -> {
            model.setRowCount(0);
            for (Provider p : providerDb.getAllProviders()) {
                model.addRow(new Object[]{
                        String.format("%09d", p.getNumber()), p.getName(), p.getAddress(),
                        p.getCity(), p.getState(), p.getZip()
                });
            }
        };
        refreshProviders.run();
        refreshBtn.addActionListener(e -> refreshProviders.run());

        panel.add(refreshBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.putClientProperty("refresh", refreshProviders);
        return panel;
    }

    // ==================== MANAGER PANEL ====================

    private JPanel createManagerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel header = new JLabel("  Manager Terminal", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(new Color(156, 39, 176));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton memberRptBtn = new JButton("Member Report");
        JButton providerRptBtn = new JButton("Provider Report");
        JButton summaryRptBtn = new JButton("Summary Report");
        JButton accountingBtn = new JButton("Run Accounting Procedure");
        JButton logoutBtn = new JButton("Log Out");

        JTextArea mgrOutput = new JTextArea();
        mgrOutput.setEditable(false);
        mgrOutput.setFont(new Font("Monospaced", Font.PLAIN, 13));

        memberRptBtn.addActionListener(e -> guiMemberReport(mgrOutput));
        providerRptBtn.addActionListener(e -> guiProviderReport(mgrOutput));
        summaryRptBtn.addActionListener(e -> guiSummaryReport(mgrOutput));
        accountingBtn.addActionListener(e -> guiAccountingProcedure(mgrOutput));
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        buttonBar.add(memberRptBtn);
        buttonBar.add(providerRptBtn);
        buttonBar.add(summaryRptBtn);
        buttonBar.add(accountingBtn);
        buttonBar.add(logoutBtn);

        panel.add(header, BorderLayout.NORTH);
        panel.add(buttonBar, BorderLayout.SOUTH);
        panel.add(new JScrollPane(mgrOutput), BorderLayout.CENTER);
        return panel;
    }

    // ==================== LOGIN ACTIONS ====================

    private void providerLogin() {
        String input = JOptionPane.showInputDialog(this,
                "Enter provider number (9 digits):", "Provider Login", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        int providerNumber;
        try {
            providerNumber = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid provider number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Provider p = providerDb.getProvider(providerNumber);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Provider not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentProvider = p;
        outputArea.setText("Welcome, " + p.getName() + "!\nProvider login validated.\n\n");
        cardLayout.show(mainPanel, "PROVIDER");
    }

    private void operatorLogin() {
        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, passwordField,
                "Enter operator password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String password = new String(passwordField.getPassword()).trim();
        if (!"operator123".equals(password)) {
            JOptionPane.showMessageDialog(this, "Invalid password. Access denied.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Operator login validated.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        // Rebuild operator panel to refresh tables
        mainPanel.remove(mainPanel.getComponent(2)); // Remove old operator panel
        mainPanel.add(createOperatorPanel(), "OPERATOR", 2);
        cardLayout.show(mainPanel, "OPERATOR");
    }

    private void managerLogin() {
        JPasswordField passwordField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, passwordField,
                "Enter manager password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String password = new String(passwordField.getPassword()).trim();
        if (!"manager123".equals(password)) {
            JOptionPane.showMessageDialog(this, "Invalid password. Access denied.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Manager login validated.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        cardLayout.show(mainPanel, "MANAGER");
    }

    private void runAccountingFromMain() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Run main accounting procedure now?", "Accounting", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            reportGenerator.runAccountingProcedure();
            JOptionPane.showMessageDialog(this, "Main accounting procedure completed.\nReports generated.",
                    "Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ==================== PROVIDER ACTIONS ====================

    private void guiVerifyMember() {
        String input = JOptionPane.showInputDialog(this,
                "Enter member number (9 digits):", "Verify Member", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        int memberNumber;
        try {
            memberNumber = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            outputArea.append("Invalid number format.\n");
            return;
        }

        String status = memberDb.validateMember(memberNumber);
        outputArea.append("Member " + input.trim() + ": " + status + "\n");

        if ("Validated".equals(status)) {
            Member m = memberDb.getMember(memberNumber);
            outputArea.append("  Name: " + m.getName() + "\n\n");
        } else {
            outputArea.append("\n");
        }
    }

    private void guiBillService() {
        if (currentProvider == null) return;

        // Step 1: Member number
        String memberInput = JOptionPane.showInputDialog(this,
                "Enter member number (9 digits):", "Bill Service - Step 1", JOptionPane.PLAIN_MESSAGE);
        if (memberInput == null) return;

        int memberNumber;
        try {
            memberNumber = Integer.parseInt(memberInput.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = memberDb.validateMember(memberNumber);
        outputArea.append("Member " + memberInput.trim() + ": " + status + "\n");
        if (!"Validated".equals(status)) {
            JOptionPane.showMessageDialog(this, status, "Validation Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 2: Service date
        String dateInput = JOptionPane.showInputDialog(this,
                "Enter date of service (MM-DD-YYYY):", "Bill Service - Step 2", JOptionPane.PLAIN_MESSAGE);
        if (dateInput == null) return;

        LocalDate serviceDate;
        try {
            serviceDate = LocalDate.parse(dateInput.trim(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 3: Service code
        String codeInput = JOptionPane.showInputDialog(this,
                "Enter service code (6 digits):", "Bill Service - Step 3", JOptionPane.PLAIN_MESSAGE);
        if (codeInput == null) return;

        int serviceCode;
        try {
            serviceCode = Integer.parseInt(codeInput.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid service code.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Service service = serviceDir.getService(serviceCode);
        if (service == null) {
            JOptionPane.showMessageDialog(this, "Service code not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 4: Confirm service
        int confirm = JOptionPane.showConfirmDialog(this,
                "Service: " + service.getName() + "\nFee: $" + String.format("%.2f", service.getFee()) +
                        "\n\nIs this correct?",
                "Confirm Service", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            outputArea.append("Billing cancelled.\n\n");
            return;
        }

        // Step 5: Comments
        String comments = JOptionPane.showInputDialog(this,
                "Enter comments (optional, up to 100 chars):", "Bill Service - Step 5", JOptionPane.PLAIN_MESSAGE);
        if (comments != null && comments.trim().isEmpty()) comments = null;
        if (comments != null && comments.length() > 100) comments = comments.substring(0, 100);

        // Step 6: Save record
        ServiceRecord record = new ServiceRecord(LocalDateTime.now(), serviceDate,
                currentProvider.getNumber(), memberNumber, serviceCode, comments);
        recordDb.addRecord(record);

        outputArea.append("Service record saved.\n");
        outputArea.append("  Provider: " + currentProvider.getName() + "\n");
        outputArea.append("  Member: " + memberDb.getMember(memberNumber).getName() + "\n");
        outputArea.append("  Service: " + service.getName() + "\n");
        outputArea.append("  Fee: $" + String.format("%.2f", service.getFee()) + "\n\n");
    }

    private void guiProviderDirectory() {
        List<Service> sorted = serviceDir.getAllServicesSorted();
        StringBuilder sb = new StringBuilder("=== PROVIDER DIRECTORY ===\n\n");
        sb.append(String.format("%-20s  %-6s  %s\n", "Service Name", "Code", "Fee"));
        sb.append("--------------------------------------------------\n");
        for (Service s : sorted) {
            sb.append(String.format("%-20s  %06d  $%.2f\n", s.getName(), s.getCode(), s.getFee()));
        }
        sb.append("\n=== END OF DIRECTORY ===\n\n");
        outputArea.append(sb.toString());
    }

    // ==================== OPERATOR ACTIONS ====================

    private void guiAddMember() {
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField numberField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField stateField = new JTextField();
        JTextField zipField = new JTextField();

        form.add(new JLabel("Name (up to 25 chars):"));  form.add(nameField);
        form.add(new JLabel("Number (9 digits):"));      form.add(numberField);
        form.add(new JLabel("Address (up to 25 chars):")); form.add(addressField);
        form.add(new JLabel("City (up to 14 chars):"));  form.add(cityField);
        form.add(new JLabel("State (2 letters):"));      form.add(stateField);
        form.add(new JLabel("ZIP (5 digits):"));         form.add(zipField);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Add Member", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            if (name.length() > 25) name = name.substring(0, 25);
            int number = Integer.parseInt(numberField.getText().trim());
            String address = addressField.getText().trim();
            if (address.length() > 25) address = address.substring(0, 25);
            String city = cityField.getText().trim();
            if (city.length() > 14) city = city.substring(0, 14);
            String state = stateField.getText().trim().toUpperCase();
            if (state.length() > 2) state = state.substring(0, 2);
            String zip = zipField.getText().trim();
            if (zip.length() > 5) zip = zip.substring(0, 5);

            Member m = new Member(name, number, address, city, state, zip);
            if (memberDb.addMember(m)) {
                JOptionPane.showMessageDialog(this, "Member '" + name + "' added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshOperatorTables();
            } else {
                JOptionPane.showMessageDialog(this, "Member number already exists.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guiEditMember() {
        String input = JOptionPane.showInputDialog(this,
                "Enter member number to edit:", "Edit Member", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        int number;
        try {
            number = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Member existing = memberDb.getMember(number);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Member not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField nameField = new JTextField(existing.getName());
        JTextField addressField = new JTextField(existing.getAddress());
        JTextField cityField = new JTextField(existing.getCity());
        JTextField stateField = new JTextField(existing.getState());
        JTextField zipField = new JTextField(existing.getZip());

        form.add(new JLabel("Name:"));    form.add(nameField);
        form.add(new JLabel("Address:")); form.add(addressField);
        form.add(new JLabel("City:"));    form.add(cityField);
        form.add(new JLabel("State:"));   form.add(stateField);
        form.add(new JLabel("ZIP:"));     form.add(zipField);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Edit Member #" + String.format("%09d", number),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        if (!name.isEmpty()) { if (name.length() > 25) name = name.substring(0, 25); existing.setName(name); }
        String address = addressField.getText().trim();
        if (!address.isEmpty()) { if (address.length() > 25) address = address.substring(0, 25); existing.setAddress(address); }
        String city = cityField.getText().trim();
        if (!city.isEmpty()) { if (city.length() > 14) city = city.substring(0, 14); existing.setCity(city); }
        String state = stateField.getText().trim().toUpperCase();
        if (!state.isEmpty()) { if (state.length() > 2) state = state.substring(0, 2); existing.setState(state); }
        String zip = zipField.getText().trim();
        if (!zip.isEmpty()) { if (zip.length() > 5) zip = zip.substring(0, 5); existing.setZip(zip); }

        memberDb.updateMember(existing);
        JOptionPane.showMessageDialog(this, "Member updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshOperatorTables();
    }

    private void guiDeleteMember() {
        String input = JOptionPane.showInputDialog(this,
                "Enter member number to delete:", "Delete Member", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        int number;
        try {
            number = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Member m = memberDb.getMember(number);
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Member not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete member '" + m.getName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            memberDb.deleteMember(number);
            JOptionPane.showMessageDialog(this, "Member deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshOperatorTables();
        }
    }

    private void guiAddProvider() {
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField numberField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField stateField = new JTextField();
        JTextField zipField = new JTextField();

        form.add(new JLabel("Name (up to 25 chars):"));  form.add(nameField);
        form.add(new JLabel("Number (9 digits):"));      form.add(numberField);
        form.add(new JLabel("Address (up to 25 chars):")); form.add(addressField);
        form.add(new JLabel("City (up to 14 chars):"));  form.add(cityField);
        form.add(new JLabel("State (2 letters):"));      form.add(stateField);
        form.add(new JLabel("ZIP (5 digits):"));         form.add(zipField);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Add Provider", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            if (name.length() > 25) name = name.substring(0, 25);
            int number = Integer.parseInt(numberField.getText().trim());
            String address = addressField.getText().trim();
            if (address.length() > 25) address = address.substring(0, 25);
            String city = cityField.getText().trim();
            if (city.length() > 14) city = city.substring(0, 14);
            String state = stateField.getText().trim().toUpperCase();
            if (state.length() > 2) state = state.substring(0, 2);
            String zip = zipField.getText().trim();
            if (zip.length() > 5) zip = zip.substring(0, 5);

            Provider p = new Provider(name, number, address, city, state, zip);
            if (providerDb.addProvider(p)) {
                JOptionPane.showMessageDialog(this, "Provider '" + name + "' added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshOperatorTables();
            } else {
                JOptionPane.showMessageDialog(this, "Provider number already exists.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guiEditProvider() {
        String input = JOptionPane.showInputDialog(this,
                "Enter provider number to edit:", "Edit Provider", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        int number;
        try {
            number = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Provider existing = providerDb.getProvider(number);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Provider not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField nameField = new JTextField(existing.getName());
        JTextField addressField = new JTextField(existing.getAddress());
        JTextField cityField = new JTextField(existing.getCity());
        JTextField stateField = new JTextField(existing.getState());
        JTextField zipField = new JTextField(existing.getZip());

        form.add(new JLabel("Name:"));    form.add(nameField);
        form.add(new JLabel("Address:")); form.add(addressField);
        form.add(new JLabel("City:"));    form.add(cityField);
        form.add(new JLabel("State:"));   form.add(stateField);
        form.add(new JLabel("ZIP:"));     form.add(zipField);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Edit Provider #" + String.format("%09d", number),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        if (!name.isEmpty()) { if (name.length() > 25) name = name.substring(0, 25); existing.setName(name); }
        String address = addressField.getText().trim();
        if (!address.isEmpty()) { if (address.length() > 25) address = address.substring(0, 25); existing.setAddress(address); }
        String city = cityField.getText().trim();
        if (!city.isEmpty()) { if (city.length() > 14) city = city.substring(0, 14); existing.setCity(city); }
        String state = stateField.getText().trim().toUpperCase();
        if (!state.isEmpty()) { if (state.length() > 2) state = state.substring(0, 2); existing.setState(state); }
        String zip = zipField.getText().trim();
        if (!zip.isEmpty()) { if (zip.length() > 5) zip = zip.substring(0, 5); existing.setZip(zip); }

        providerDb.updateProvider(existing);
        JOptionPane.showMessageDialog(this, "Provider updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshOperatorTables();
    }

    private void guiDeleteProvider() {
        String input = JOptionPane.showInputDialog(this,
                "Enter provider number to delete:", "Delete Provider", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        int number;
        try {
            number = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Provider p = providerDb.getProvider(number);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Provider not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete provider '" + p.getName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            providerDb.deleteProvider(number);
            JOptionPane.showMessageDialog(this, "Provider deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshOperatorTables();
        }
    }

    private void refreshOperatorTables() {
        // Rebuild the operator panel to refresh tables
        mainPanel.remove(2);
        mainPanel.add(createOperatorPanel(), "OPERATOR", 2);
        cardLayout.show(mainPanel, "OPERATOR");
    }

    // ==================== MANAGER ACTIONS ====================

    private void guiMemberReport(JTextArea output) {
        String input = JOptionPane.showInputDialog(this,
                "Enter member number:", "Member Report", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        int memberNumber;
        try {
            memberNumber = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (memberDb.getMember(memberNumber) == null) {
            JOptionPane.showMessageDialog(this, "Member not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String file = reportGenerator.generateMemberReport(memberNumber);
        if (file != null) {
            output.append("Member report generated: " + file + "\n");
            output.append(readFileContents(file));
            output.append("\n");
        } else {
            output.append("No service records found for this member.\n\n");
        }
    }

    private void guiProviderReport(JTextArea output) {
        String input = JOptionPane.showInputDialog(this,
                "Enter provider number:", "Provider Report", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        int providerNumber;
        try {
            providerNumber = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (providerDb.getProvider(providerNumber) == null) {
            JOptionPane.showMessageDialog(this, "Provider not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String file = reportGenerator.generateProviderReport(providerNumber);
        if (file != null) {
            output.append("Provider report generated: " + file + "\n");
            output.append(readFileContents(file));
            output.append("\n");
        } else {
            output.append("No service records found for this provider.\n\n");
        }
    }

    private void guiSummaryReport(JTextArea output) {
        String file = reportGenerator.generateSummaryReport();
        if (file != null) {
            output.append("Summary report generated: " + file + "\n");
            output.append(readFileContents(file));
            output.append("\n");
        } else {
            output.append("Error generating summary report.\n\n");
        }
    }

    private void guiAccountingProcedure(JTextArea output) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Run the main accounting procedure?", "Accounting Procedure", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        reportGenerator.runAccountingProcedure();
        output.append("=== ACCOUNTING PROCEDURE COMPLETED ===\n");

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String summaryFile = "SummaryReport_" + dateStr + ".txt";
        String eftFile = "EFT_Data_" + dateStr + ".txt";

        if (new File(summaryFile).exists()) {
            output.append("\n--- Summary Report ---\n");
            output.append(readFileContents(summaryFile));
        }
        if (new File(eftFile).exists()) {
            output.append("\n--- EFT Data ---\n");
            output.append(readFileContents(eftFile));
        }
        output.append("\n");
    }

    private String readFileContents(String filename) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filename)));
        } catch (Exception e) {
            return "(Could not read file: " + filename + ")\n";
        }
    }
}
