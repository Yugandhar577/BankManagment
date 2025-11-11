import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BankAppUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel rootPanel;

	// Theme
	private final Color COLOR_BG = new Color(0xF7F9FC);
	private final Color COLOR_SURFACE = Color.WHITE;
	private final Color COLOR_PRIMARY = new Color(0x1E88E5);
	private final Color COLOR_PRIMARY_DARK = new Color(0x1565C0);
	private final Color COLOR_TEXT = new Color(0x1F2937);
	private final Color COLOR_MUTED = new Color(0x6B7280);

    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final BankService bankService = new BankService();

    private User currentUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
			// Increase global font for a cleaner, larger UI
			setUIFont(new Font("Segoe UI", Font.PLAIN, 14));
            new BankAppUI().start();
        });
    }

    private void start() {
        frame = new JFrame("Online Bank Management System");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(960, 640));

        cardLayout = new CardLayout();
		rootPanel = new JPanel(cardLayout);
		rootPanel.setBackground(COLOR_BG);

        rootPanel.add(buildAuthPanel(), "auth");
        rootPanel.add(buildDashboardPanel(), "dashboard");

        frame.setContentPane(rootPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel buildAuthPanel() {
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBackground(COLOR_SURFACE);
		tabs.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tabs.addTab("Login", buildLoginPanel());
        tabs.addTab("Register", buildRegisterPanel());

		JPanel container = new JPanel(new BorderLayout());
		container.setBackground(COLOR_BG);
		container.add(makeBrandBanner("Welcome back. Manage your accounts securely."), BorderLayout.NORTH);

		JPanel centerWrap = new JPanel(new GridBagLayout());
		centerWrap.setOpaque(false);
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(COLOR_SURFACE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(0xE5E7EB)),
				BorderFactory.createEmptyBorder(16, 16, 16, 16)
		));
		card.add(tabs, BorderLayout.CENTER);
		centerWrap.add(card);

		container.add(centerWrap, BorderLayout.CENTER);
		return container;
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
        JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(COLOR_SURFACE);

        JTextField emailField = new JTextField(24);
        JPasswordField passwordField = new JPasswordField(24);
        JButton loginBtn = new JButton("Login");
		stylePrimaryButton(loginBtn);

        int r = 0;
        addFormRow(form, r++, "Email", emailField);
        addFormRow(form, r++, "Password", passwordField);
        addFormButton(form, r, loginBtn);

        loginBtn.addActionListener((ActionEvent e) -> {
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword());
            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter email and password.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            User u = userDAO.login(email, pass);
            if (u == null) {
                JOptionPane.showMessageDialog(frame, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentUser = u;
            JOptionPane.showMessageDialog(frame, "Welcome, " + u.name + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            showDashboard();
        });

        panel.add(form);
        return wrapCentered(panel);
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
        JPanel form = new JPanel(new GridBagLayout());
		form.setBackground(COLOR_SURFACE);

        JTextField nameField = new JTextField(24);
        JTextField emailField = new JTextField(24);
        JPasswordField passwordField = new JPasswordField(24);
        JButton registerBtn = new JButton("Create Account");
		stylePrimaryButton(registerBtn);

        int r = 0;
        addFormRow(form, r++, "Full Name", nameField);
        addFormRow(form, r++, "Email", emailField);
        addFormRow(form, r++, "Password", passwordField);
        addFormButton(form, r, registerBtn);

        registerBtn.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword());
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            User u = new User(name, email, pass);
            boolean ok = userDAO.register(u);
            if (ok) {
                JOptionPane.showMessageDialog(frame, "Registration successful. You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Registration failed. Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form);
        return wrapCentered(panel);
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(COLOR_BG);
		panel.add(buildTopNavBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tabs.addTab("Accounts", buildAccountsTab());
        tabs.addTab("Transactions", buildTransactionsTab());
        tabs.addTab("Transfers", buildTransfersTab());
        tabs.addTab("Loans", buildLoansTab());

        panel.add(tabs, BorderLayout.CENTER);
        return panel;
    }

	private JComponent buildTopNavBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(COLOR_PRIMARY);
		bar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

		JLabel brand = new JLabel("BankMS");
		brand.setForeground(Color.WHITE);
		brand.setFont(brand.getFont().deriveFont(Font.BOLD, 18f));

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		right.setOpaque(false);
		JButton logout = new JButton("Logout");
		styleLightButton(logout);
		logout.addActionListener(e -> {
			currentUser = null;
			cardLayout.show(rootPanel, "auth");
		});
		right.add(logout);

		bar.add(brand, BorderLayout.WEST);
		bar.add(right, BorderLayout.EAST);
		return bar;
    }

    private JPanel buildAccountsTab() {
        JPanel tab = new JPanel(new BorderLayout());
		tab.setBackground(COLOR_BG);

        // Create account
        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		createPanel.setOpaque(false);
        JButton createBtn = new JButton("Create New Account");
		stylePrimaryButton(createBtn);
        JLabel createdAccLabel = new JLabel(" ");
        createBtn.addActionListener(e -> {
            if (ensureLoggedIn()) {
                int accId = accountDAO.createAccount(currentUser.id, 0);
                if (accId > 0) {
                    createdAccLabel.setText("Created Account ID: " + accId);
                    JOptionPane.showMessageDialog(frame, "New Account Created. ID: " + accId);
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to create account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        createPanel.add(createBtn);
        createPanel.add(createdAccLabel);

        JPanel actions = new JPanel(new GridLayout(1, 2, 16, 16));
		actions.setOpaque(false);
        actions.add(buildDepositWithdrawPanel(true));
        actions.add(buildDepositWithdrawPanel(false));

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		balancePanel.setOpaque(false);
        JTextField balAccId = new JTextField(10);
        JButton checkBalBtn = new JButton("Check Balance");
		styleLightButton(checkBalBtn);
        JLabel balanceLabel = new JLabel(" ");
        checkBalBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(balAccId.getText().trim());
                double bal = bankService.getBalance(id);
                balanceLabel.setText("Balance: ₹ " + String.format("%.2f", bal));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid Account ID.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });
        balancePanel.add(new JLabel("Account ID"));
        balancePanel.add(balAccId);
        balancePanel.add(checkBalBtn);
        balancePanel.add(balanceLabel);

        JPanel center = new JPanel(new BorderLayout(0, 16));
		center.setOpaque(false);
        center.add(actions, BorderLayout.CENTER);
        center.add(balancePanel, BorderLayout.SOUTH);

        tab.add(createPanel, BorderLayout.NORTH);
        tab.add(center, BorderLayout.CENTER);
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return tab;
    }

    private JPanel buildDepositWithdrawPanel(boolean deposit) {
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder(deposit ? "Deposit" : "Withdraw"));
        JTextField accId = new JTextField(10);
        JTextField amount = new JTextField(10);
        JButton action = new JButton(deposit ? "Deposit" : "Withdraw");
		stylePrimaryButton(action);

        int r = 0;
        addFormRow(p, r++, "Account ID", accId);
        addFormRow(p, r++, "Amount (₹)", amount);
        addFormButton(p, r, action);

        action.addActionListener(e -> {
            try {
                int id = Integer.parseInt(accId.getText().trim());
                double amt = Double.parseDouble(amount.getText().trim());
                boolean ok = deposit ? bankService.deposit(id, amt) : bankService.withdraw(id, amt);
                if (ok) {
                    JOptionPane.showMessageDialog(frame, (deposit ? "Deposited " : "Withdrew ") + "₹" + amt + " successfully.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Operation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });
        return p;
    }

    private JPanel buildTransactionsTab() {
        JPanel tab = new JPanel(new BorderLayout());
		tab.setBackground(COLOR_BG);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.setOpaque(false);
        JTextField accField = new JTextField(10);
        JButton load = new JButton("Load Transactions");
		styleLightButton(load);
        top.add(new JLabel("Account ID"));
        top.add(accField);
        top.add(load);

        String[] cols = new String[] { "Txn ID", "Type", "Amount", "Description", "Timestamp" };
        Object[][] data = new Object[][] {};
        JTable table = new JTable(data, cols);
        JScrollPane scroll = new JScrollPane(table);

        load.addActionListener(e -> {
            try {
                int accId = Integer.parseInt(accField.getText().trim());
                Object[][] rows = TransactionsDataProvider.fetchForAccount(accId);
                table.setModel(new javax.swing.table.DefaultTableModel(rows, cols));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid Account ID.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        tab.add(top, BorderLayout.NORTH);
        tab.add(scroll, BorderLayout.CENTER);
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return tab;
    }

    private JPanel buildTransfersTab() {
        JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Transfer Funds"));
        JTextField from = new JTextField(10);
        JTextField to = new JTextField(10);
        JTextField amt = new JTextField(10);
        JButton transferBtn = new JButton("Transfer");
		stylePrimaryButton(transferBtn);

        int r = 0;
        addFormRow(p, r++, "From Account ID", from);
        addFormRow(p, r++, "To Account ID", to);
        addFormRow(p, r++, "Amount (₹)", amt);
        addFormButton(p, r, transferBtn);

        transferBtn.addActionListener(e -> {
            try {
                int fromId = Integer.parseInt(from.getText().trim());
                int toId = Integer.parseInt(to.getText().trim());
                double amount = Double.parseDouble(amt.getText().trim());
                boolean ok = bankService.transfer(fromId, toId, amount);
                if (ok) {
                    JOptionPane.showMessageDialog(frame, "Transfer successful.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Transfer failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });
        return p;
    }

    private JPanel buildLoansTab() {
        JPanel tab = new JPanel(new BorderLayout(12, 12));
		tab.setBackground(COLOR_BG);

        // Apply for loan
        JPanel apply = new JPanel(new GridBagLayout());
		apply.setBorder(BorderFactory.createTitledBorder("Apply for Loan"));
        JTextField accId = new JTextField(10);
        JTextField amount = new JTextField(10);
        JTextField rate = new JTextField(10);
        JTextField months = new JTextField(10);
        JButton applyBtn = new JButton("Apply Loan");
		stylePrimaryButton(applyBtn);

        int r = 0;
        addFormRow(apply, r++, "Account ID", accId);
        addFormRow(apply, r++, "Amount (₹)", amount);
        addFormRow(apply, r++, "Interest Rate (%)", rate);
        addFormRow(apply, r++, "Duration (months)", months);
        addFormButton(apply, r, applyBtn);

        applyBtn.addActionListener(e -> {
            try {
                int a = Integer.parseInt(accId.getText().trim());
                double am = Double.parseDouble(amount.getText().trim());
                double rt = Double.parseDouble(rate.getText().trim());
                int mo = Integer.parseInt(months.getText().trim());
				boolean ok = bankService.applyLoan(a, am, rt, mo);
				JOptionPane.showMessageDialog(frame, ok ? "Loan application submitted." : "Failed to submit loan.", ok ? "Success" : "Error", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid values.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        // View loans
        JPanel view = new JPanel(new BorderLayout());
		view.setOpaque(false);
        JPanel viewTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
		viewTop.setOpaque(false);
        JTextField viewAcc = new JTextField(10);
        JButton loadLoans = new JButton("Load Loans");
		styleLightButton(loadLoans);
        viewTop.add(new JLabel("Account ID"));
        viewTop.add(viewAcc);
        viewTop.add(loadLoans);

        String[] cols = new String[] { "Loan ID", "Amount", "Outstanding", "Rate (%)", "Months", "Status", "Approved By" };
        JTable loansTable = new JTable(new Object[][] {}, cols);
        JScrollPane loanScroll = new JScrollPane(loansTable);

        loadLoans.addActionListener(e -> {
            try {
                int a = Integer.parseInt(viewAcc.getText().trim());
                Object[][] rows = LoansDataProvider.fetchForAccount(a);
                loansTable.setModel(new javax.swing.table.DefaultTableModel(rows, cols));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid Account ID.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Repay
        JPanel repay = new JPanel(new GridBagLayout());
		repay.setBorder(BorderFactory.createTitledBorder("Repay Loan"));
        JTextField repayAcc = new JTextField(10);
        JTextField loanId = new JTextField(10);
        JTextField repayAmt = new JTextField(10);
        JButton repayBtn = new JButton("Repay Loan");
		stylePrimaryButton(repayBtn);
        int rr = 0;
        addFormRow(repay, rr++, "Account ID", repayAcc);
        addFormRow(repay, rr++, "Loan ID", loanId);
        addFormRow(repay, rr++, "Repay Amount (₹)", repayAmt);
        addFormButton(repay, rr, repayBtn);

        repayBtn.addActionListener(e -> {
            try {
                int a = Integer.parseInt(repayAcc.getText().trim());
                int l = Integer.parseInt(loanId.getText().trim());
                double am = Double.parseDouble(repayAmt.getText().trim());
				boolean ok = bankService.repayLoan(a, l, am);
				JOptionPane.showMessageDialog(frame, ok ? "Loan repaid." : "Repayment failed.", ok ? "Success" : "Error", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid values.", "Validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        view.add(viewTop, BorderLayout.NORTH);
        view.add(loanScroll, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.add(apply);
        center.add(repay);

        tab.add(center, BorderLayout.NORTH);
        tab.add(view, BorderLayout.CENTER);
        tab.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return tab;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.gridy = row; lc.insets = new Insets(6, 6, 6, 6);
        lc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(label), lc);

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 1; fc.gridy = row; fc.insets = new Insets(6, 6, 6, 6);
        fc.weightx = 1.0; fc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, fc);
    }

    private void addFormButton(JPanel panel, int row, JButton button) {
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 1; bc.gridy = row + 1; bc.insets = new Insets(10, 6, 6, 6);
        bc.anchor = GridBagConstraints.LINE_START;
        panel.add(button, bc);
    }

	private JPanel makeBrandBanner(String subtitle) {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(COLOR_SURFACE);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE5E7EB)));

		JPanel inner = new JPanel(new BorderLayout());
		inner.setOpaque(false);
		inner.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

		JLabel brand = new JLabel("BankMS");
		brand.setFont(brand.getFont().deriveFont(Font.BOLD, 22f));
		brand.setForeground(COLOR_TEXT);

		JLabel sub = new JLabel(subtitle);
		sub.setForeground(COLOR_MUTED);

		JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
		text.setOpaque(false);
		text.add(brand);
		text.add(sub);

		inner.add(text, BorderLayout.WEST);
		header.add(inner, BorderLayout.CENTER);
		return header;
	}

    private JPanel wrapCentered(JComponent content) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.add(content, new GridBagConstraints());
        outer.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return outer;
    }

    private void showDashboard() {
        cardLayout.show(rootPanel, "dashboard");
    }

	private void stylePrimaryButton(JButton button) {
		button.setBackground(COLOR_PRIMARY);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
		button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorderPainted(false);
		button.setRolloverEnabled(true);
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(COLOR_PRIMARY_DARK); }
			public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(COLOR_PRIMARY); }
		});
	}

	private void styleLightButton(JButton button) {
		button.setBackground(new Color(0xE5E7EB));
		button.setForeground(COLOR_TEXT);
		button.setFocusPainted(false);
		button.setFont(button.getFont().deriveFont(Font.PLAIN, 14f));
		button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorderPainted(false);
	}

	private boolean ensureLoggedIn() {
		if (currentUser == null) {
			JOptionPane.showMessageDialog(frame, "Please log in first.", "Authentication Required", JOptionPane.WARNING_MESSAGE);
			cardLayout.show(rootPanel, "auth");
			return false;
		}
		return true;
	}
	// Utility: set global UI font
	private static void setUIFont(Font f) {
		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof Font) {
				UIManager.put(key, f);
			}
		}
	}
}
