package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
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
    private JTable memberTable;
    private JTable providerTable;

    // Bama theme colors
    private static final Color BAMA_BLACK = new Color(0, 0, 0);
    private static final Color BAMA_CRIMSON = new Color(158, 27, 50);
    private static final Color BAMA_CRIMSON_DARK = new Color(118, 20, 38);
    private static final Color BAMA_WHITE = Color.WHITE;
    private static final Color BAMA_GRAY = new Color(30, 30, 30);

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
        getContentPane().setBackground(BAMA_BLACK);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BAMA_BLACK);

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
        panel.setBackground(BAMA_BLACK);

        // Title
        JLabel titleLabel = new JLabel("Chocoholics Anonymous (ChocAn)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(BAMA_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));

        JLabel subtitleLabel = new JLabel("Data Processing System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 80, 200));

        JButton providerBtn = createStyledButton("Provider Login", BAMA_CRIMSON);
        JButton operatorBtn = createStyledButton("Operator Login", BAMA_CRIMSON);
        JButton managerBtn = createStyledButton("Manager Login", BAMA_CRIMSON);
        JButton exitBtn = createStyledButton("Exit", BAMA_CRIMSON_DARK);

        providerBtn.addActionListener(e -> providerLogin());
        operatorBtn.addActionListener(e -> operatorLogin());
        managerBtn.addActionListener(e -> managerLogin());
        exitBtn.addActionListener(e -> exitApplication());

        buttonPanel.add(providerBtn);
        buttonPanel.add(operatorBtn);
        buttonPanel.add(managerBtn);
        buttonPanel.add(exitBtn);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(BAMA_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(BAMA_CRIMSON);
        btn.setForeground(BAMA_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setBackground(BAMA_GRAY);
        table.setForeground(BAMA_WHITE);
        table.setGridColor(new Color(60, 60, 60));
        table.setSelectionBackground(BAMA_CRIMSON);
        table.setSelectionForeground(BAMA_WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(24);
        JTableHeader header = table.getTableHeader();
        header.setBackground(BAMA_CRIMSON_DARK);
        header.setForeground(BAMA_WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private JTextArea createStyledTextArea() {
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ta.setBackground(BAMA_GRAY);
        ta.setForeground(BAMA_WHITE);
        ta.setCaretColor(BAMA_WHITE);
        return ta;
    }

    // ==================== PROVIDER PANEL ====================

    private JPanel createProviderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BAMA_BLACK);

        JLabel header = new JLabel("  Provider Terminal", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(BAMA_CRIMSON);
        header.setForeground(BAMA_WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonBar.setBackground(BAMA_BLACK);
        JButton verifyBtn = createBarButton("Verify Member");
        JButton billBtn = createBarButton("Bill Service");
        JButton dirBtn = createBarButton("Provider Directory");
        JButton logoutBtn = createBarButton("Log Out");

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

        outputArea = createStyledTextArea();
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.getViewport().setBackground(BAMA_GRAY);

        panel.add(header, BorderLayout.NORTH);
        panel.add(buttonBar, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ==================== OPERATOR PANEL ====================

    private JPanel createOperatorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BAMA_BLACK);

        JLabel header = new JLabel("  Operator Terminal", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(BAMA_CRIMSON);
        header.setForeground(BAMA_WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        // Member buttons
        JPanel memberButtonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        memberButtonBar.setBackground(BAMA_BLACK);
        JButton addMemberBtn = createBarButton("Add Member");
        JButton editMemberBtn = createBarButton("Edit Member");
        JButton deleteMemberBtn = createBarButton("Delete Member");
        JButton logoutBtn1 = createBarButton("Log Out");
        addMemberBtn.addActionListener(e -> guiAddMember());
        editMemberBtn.addActionListener(e -> guiEditMember());
        deleteMemberBtn.addActionListener(e -> guiDeleteMember());
        logoutBtn1.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        memberButtonBar.add(addMemberBtn);
        memberButtonBar.add(editMemberBtn);
        memberButtonBar.add(deleteMemberBtn);
        memberButtonBar.add(logoutBtn1);

        // Provider buttons
        JPanel providerButtonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        providerButtonBar.setBackground(BAMA_BLACK);
        JButton addProvBtn = createBarButton("Add Provider");
        JButton editProvBtn = createBarButton("Edit Provider");
        JButton deleteProvBtn = createBarButton("Delete Provider");
        JButton logoutBtn2 = createBarButton("Log Out");
        addProvBtn.addActionListener(e -> guiAddProvider());
        editProvBtn.addActionListener(e -> guiEditProvider());
        deleteProvBtn.addActionListener(e -> guiDeleteProvider());
        logoutBtn2.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        providerButtonBar.add(addProvBtn);
        providerButtonBar.add(editProvBtn);
        providerButtonBar.add(deleteProvBtn);
        providerButtonBar.add(logoutBtn2);

        // Button bar that swaps based on active tab
        JPanel buttonBar = new JPanel(new CardLayout());
        buttonBar.add(memberButtonBar, "MEMBERS");
        buttonBar.add(providerButtonBar, "PROVIDERS");
        CardLayout buttonCardLayout = (CardLayout) buttonBar.getLayout();

        // Members and Providers tables
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BAMA_GRAY);
        tabbedPane.setForeground(BAMA_WHITE);
        tabbedPane.addTab("Members", createMemberTablePanel());
        tabbedPane.addTab("Providers", createProviderTablePanel());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                buttonCardLayout.show(buttonBar, "MEMBERS");
            } else {
                buttonCardLayout.show(buttonBar, "PROVIDERS");
            }
        });

        panel.add(header, BorderLayout.NORTH);
        panel.add(buttonBar, BorderLayout.SOUTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMemberTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BAMA_BLACK);
        JButton refreshBtn = createBarButton("Refresh");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Number", "Name", "Address", "City", "State", "ZIP", "Suspended"}, 0);
        memberTable = new JTable(model);
        JTable table = memberTable;
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleTable(table);

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
        panel.setBackground(BAMA_BLACK);
        JButton refreshBtn = createBarButton("Refresh");
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Number", "Name", "Address", "City", "State", "ZIP"}, 0);
        providerTable = new JTable(model);
        JTable table = providerTable;
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleTable(table);

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
        panel.setBackground(BAMA_BLACK);

        JLabel header = new JLabel("  Manager Terminal", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(BAMA_CRIMSON);
        header.setForeground(BAMA_WHITE);
        header.setPreferredSize(new Dimension(0, 45));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonBar.setBackground(BAMA_BLACK);
        JButton memberRptBtn = createBarButton("Member Report");
        JButton providerRptBtn = createBarButton("Provider Report");
        JButton summaryRptBtn = createBarButton("Summary Report");
        JButton accountingBtn = createBarButton("Run Accounting Procedure");
        JButton viewReportsBtn = createBarButton("View Reports");
        JButton logoutBtn = createBarButton("Log Out");

        JTextArea mgrOutput = createStyledTextArea();

        memberRptBtn.addActionListener(e -> guiMemberReport(mgrOutput));
        providerRptBtn.addActionListener(e -> guiProviderReport(mgrOutput));
        summaryRptBtn.addActionListener(e -> guiSummaryReport(mgrOutput));
        accountingBtn.addActionListener(e -> guiAccountingProcedure(mgrOutput));
        viewReportsBtn.addActionListener(e -> guiViewReports());
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        buttonBar.add(memberRptBtn);
        buttonBar.add(providerRptBtn);
        buttonBar.add(summaryRptBtn);
        buttonBar.add(accountingBtn);
        buttonBar.add(viewReportsBtn);
        buttonBar.add(logoutBtn);

        JScrollPane mgrScroll = new JScrollPane(mgrOutput);
        mgrScroll.getViewport().setBackground(BAMA_GRAY);

        panel.add(header, BorderLayout.NORTH);
        panel.add(buttonBar, BorderLayout.SOUTH);
        panel.add(mgrScroll, BorderLayout.CENTER);
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

    // ==================== PROVIDER ACTIONS ==

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

        // Step 3: Select service from clickable table
        List<Service> allServices = serviceDir.getAllServicesSorted();
        DefaultTableModel serviceModel = new DefaultTableModel(
                new String[]{"Code", "Service Name", "Fee"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Service s : allServices) {
            serviceModel.addRow(new Object[]{
                    String.format("%06d", s.getCode()), s.getName(),
                    String.format("$%.2f", s.getFee())
            });
        }
        JTable serviceTable = new JTable(serviceModel);
        serviceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleTable(serviceTable);
        serviceTable.setPreferredScrollableViewportSize(new Dimension(400, 200));

        JScrollPane tableScroll = new JScrollPane(serviceTable);
        tableScroll.getViewport().setBackground(BAMA_GRAY);

        int tableResult = JOptionPane.showConfirmDialog(this, tableScroll,
                "Bill Service - Step 3: Select a Service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (tableResult != JOptionPane.OK_OPTION) return;

        int selectedRow = serviceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "No service selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int serviceCode = allServices.get(selectedRow).getCode();
        Service service = allServices.get(selectedRow);

        // Step 4: Confirm service
        int confirm = JOptionPane.showConfirmDialog(this,
                "Service: " + service.getName() + "\nCode: " + String.format("%06d", service.getCode()) +
                        "\nFee: $" + String.format("%.2f", service.getFee()) +
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
        int selectedRow = memberTable != null ? memberTable.getSelectedRow() : -1;
        int number;
        if (selectedRow >= 0) {
            number = Integer.parseInt(memberTable.getValueAt(selectedRow, 0).toString().trim());
        } else {
            String input = JOptionPane.showInputDialog(this,
                    "Select a member in the table, or enter member number:", "Edit Member", JOptionPane.PLAIN_MESSAGE);
            if (input == null) return;
            try {
                number = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
        int selectedRow = memberTable != null ? memberTable.getSelectedRow() : -1;
        int number;
        if (selectedRow >= 0) {
            number = Integer.parseInt(memberTable.getValueAt(selectedRow, 0).toString().trim());
        } else {
            String input = JOptionPane.showInputDialog(this,
                    "Select a member in the table, or enter member number:", "Delete Member", JOptionPane.PLAIN_MESSAGE);
            if (input == null) return;
            try {
                number = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
        int selectedRow = providerTable != null ? providerTable.getSelectedRow() : -1;
        int number;
        if (selectedRow >= 0) {
            number = Integer.parseInt(providerTable.getValueAt(selectedRow, 0).toString().trim());
        } else {
            String input = JOptionPane.showInputDialog(this,
                    "Select a provider in the table, or enter provider number:", "Edit Provider", JOptionPane.PLAIN_MESSAGE);
            if (input == null) return;
            try {
                number = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
        int selectedRow = providerTable != null ? providerTable.getSelectedRow() : -1;
        int number;
        if (selectedRow >= 0) {
            number = Integer.parseInt(providerTable.getValueAt(selectedRow, 0).toString().trim());
        } else {
            String input = JOptionPane.showInputDialog(this,
                    "Select a provider in the table, or enter provider number:", "Delete Provider", JOptionPane.PLAIN_MESSAGE);
            if (input == null) return;
            try {
                number = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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

        String folder = reportGenerator.runAccountingProcedureToFolder();
        output.append("=== ACCOUNTING PROCEDURE COMPLETED ===\n");
        output.append("Reports saved to: " + folder + "\n");

        // Display summary and EFT from the folder
        File dir = new File(folder);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                output.append("\n--- " + f.getName() + " ---\n");
                output.append(readFileContents(f.getAbsolutePath()));
            }
        }
        output.append("\n");
    }

    private void guiViewReports() {
        File reportsDir = new File("reports");
        if (!reportsDir.exists() || !reportsDir.isDirectory()) {
            JOptionPane.showMessageDialog(this, "No reports found. Run the accounting procedure first.",
                    "No Reports", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File[] folders = reportsDir.listFiles(File::isDirectory);
        if (folders == null || folders.length == 0) {
            JOptionPane.showMessageDialog(this, "No report folders found.",
                    "No Reports", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        java.util.Arrays.sort(folders, (a, b) -> b.getName().compareTo(a.getName()));

        JDialog dialog = new JDialog(this, "Accounting Reports", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BAMA_BLACK);

        // Left panel: folder list
        DefaultListModel<String> folderModel = new DefaultListModel<>();
        for (File f : folders) folderModel.addElement(f.getName());
        JList<String> folderList = new JList<>(folderModel);
        folderList.setBackground(BAMA_GRAY);
        folderList.setForeground(BAMA_WHITE);
        folderList.setSelectionBackground(BAMA_CRIMSON);
        folderList.setSelectionForeground(BAMA_WHITE);
        folderList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane folderScroll = new JScrollPane(folderList);
        folderScroll.setPreferredSize(new Dimension(280, 0));
        folderScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BAMA_CRIMSON), "Report Folders",
                0, 0, null, BAMA_WHITE));
        folderScroll.getViewport().setBackground(BAMA_GRAY);

        // Center panel: file list
        DefaultListModel<String> fileModel = new DefaultListModel<>();
        JList<String> fileList = new JList<>(fileModel);
        fileList.setBackground(BAMA_GRAY);
        fileList.setForeground(BAMA_WHITE);
        fileList.setSelectionBackground(BAMA_CRIMSON);
        fileList.setSelectionForeground(BAMA_WHITE);
        fileList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane fileScroll = new JScrollPane(fileList);
        fileScroll.setPreferredSize(new Dimension(200, 0));
        fileScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BAMA_CRIMSON), "Files",
                0, 0, null, BAMA_WHITE));
        fileScroll.getViewport().setBackground(BAMA_GRAY);

        // Right panel: file contents
        JTextArea contentArea = createStyledTextArea();
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BAMA_CRIMSON), "File Contents",
                0, 0, null, BAMA_WHITE));
        contentScroll.getViewport().setBackground(BAMA_GRAY);

        // Wire up folder selection -> populate file list
        folderList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String selected = folderList.getSelectedValue();
            if (selected == null) return;
            fileModel.clear();
            contentArea.setText("");
            File dir = new File("reports" + File.separator + selected);
            File[] files = dir.listFiles();
            if (files != null) {
                java.util.Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
                for (File f : files) {
                    if (f.isFile()) fileModel.addElement(f.getName());
                }
            }
        });

        // Wire up file selection -> show contents
        fileList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String folder = folderList.getSelectedValue();
            String file = fileList.getSelectedValue();
            if (folder == null || file == null) return;
            String path = "reports" + File.separator + folder + File.separator + file;
            contentArea.setText(readFileContents(path));
            contentArea.setCaretPosition(0);
        });

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileScroll, contentScroll);
        rightSplit.setDividerLocation(200);
        rightSplit.setBackground(BAMA_BLACK);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, folderScroll, rightSplit);
        mainSplit.setDividerLocation(280);
        mainSplit.setBackground(BAMA_BLACK);

        dialog.getContentPane().add(mainSplit, BorderLayout.CENTER);

        // Header
        JLabel header = new JLabel("  Accounting Reports Viewer", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setOpaque(true);
        header.setBackground(BAMA_CRIMSON);
        header.setForeground(BAMA_WHITE);
        header.setPreferredSize(new Dimension(0, 35));
        dialog.getContentPane().add(header, BorderLayout.NORTH);

        // Close button at bottom
        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(BAMA_CRIMSON_DARK);
        closeBtn.setForeground(BAMA_WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BAMA_BLACK);
        bottomPanel.add(closeBtn);
        dialog.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private String readFileContents(String filename) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filename)));
        } catch (Exception e) {
            return "(Could not read file: " + filename + ")\n";
        }
    }
}
