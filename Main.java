import java.util.Scanner;

/**
 * Entry point of the Bank Management System.
 * Shows a text menu in the console and calls into BankService to actually
 * perform the banking operations. All account data is saved to accounts.txt
 * and transactions.txt in the same folder, so it survives between runs.
 */
public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static BankService bank = new BankService();

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("     WELCOME TO JAVA BANK SYSTEM      ");
        System.out.println("=====================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: createAccount(); break;
                case 2: deposit(); break;
                case 3: withdraw(); break;
                case 4: checkBalance(); break;
                case 5: transfer(); break;
                case 6: miniStatement(); break;
                case 7: closeAccount(); break;
                case 8: viewAllAccounts(); break;
                case 9:
                    running = false;
                    System.out.println("Thank you for using Java Bank System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 9.");
            }
            System.out.println();
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("-------------------------------------");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Check Balance");
        System.out.println("5. Transfer Money");
        System.out.println("6. Mini Statement (last 5 transactions)");
        System.out.println("7. Close Account");
        System.out.println("8. View All Accounts (Admin)");
        System.out.println("9. Exit");
        System.out.println("-------------------------------------");
    }

    // ---------------------------------------------------------------
    // Menu actions
    // ---------------------------------------------------------------

    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();

        String pin = readPin("Set a 4-digit PIN for your account: ");

        double initialDeposit = readDouble("Enter initial deposit amount (0 or more): ");
        while (initialDeposit < 0) {
            System.out.println("Amount cannot be negative.");
            initialDeposit = readDouble("Enter initial deposit amount (0 or more): ");
        }

        Account acc = bank.createAccount(name, pin, initialDeposit);
        System.out.println("\nAccount created successfully!");
        System.out.println("Your account number is: " + acc.getAccountNumber());
        System.out.println("Please remember your account number and PIN, you will need them for future transactions.");
    }

    private static void deposit() {
        System.out.println("\n--- Deposit Money ---");
        Account acc = login();
        if (acc == null) return;

        double amount = readDouble("Enter amount to deposit: ");
        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }
        bank.deposit(acc, amount);
        System.out.printf("Deposit successful! New balance: Rs. %.2f%n", acc.getBalance());
    }

    private static void withdraw() {
        System.out.println("\n--- Withdraw Money ---");
        Account acc = login();
        if (acc == null) return;

        double amount = readDouble("Enter amount to withdraw: ");
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be greater than zero.");
            return;
        }
        try {
            bank.withdraw(acc, amount);
            System.out.printf("Withdrawal successful! New balance: Rs. %.2f%n", acc.getBalance());
        } catch (InsufficientBalanceException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }

    private static void checkBalance() {
        System.out.println("\n--- Check Balance ---");
        Account acc = login();
        if (acc == null) return;
        System.out.println(acc);
    }

    private static void transfer() {
        System.out.println("\n--- Transfer Money ---");
        Account from = login();
        if (from == null) return;

        int toAccNo = readInt("Enter recipient's account number: ");
        Account to = bank.findAccount(toAccNo);
        if (to == null) {
            System.out.println("Recipient account not found.");
            return;
        }
        if (to.getAccountNumber() == from.getAccountNumber()) {
            System.out.println("You cannot transfer money to your own account.");
            return;
        }

        double amount = readDouble("Enter amount to transfer: ");
        if (amount <= 0) {
            System.out.println("Transfer amount must be greater than zero.");
            return;
        }
        try {
            bank.transfer(from, to, amount);
            System.out.printf("Transfer successful! Your new balance: Rs. %.2f%n", from.getBalance());
        } catch (InsufficientBalanceException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }

    private static void miniStatement() {
        System.out.println("\n--- Mini Statement ---");
        Account acc = login();
        if (acc == null) return;

        var entries = bank.getMiniStatement(acc.getAccountNumber(), 5);
        if (entries.isEmpty()) {
            System.out.println("No transactions found yet.");
            return;
        }
        System.out.println("Type                        Amount        Balance After   Date/Time");
        for (String entry : entries) {
            String[] parts = entry.split(",");
            // parts[0]=accNo, [1]=type, [2]=amount, [3]=balanceAfter, [4]=timestamp
            System.out.printf("%-28s%-14s%-16s%s%n", parts[1], parts[2], parts[3], parts[4]);
        }
    }

    private static void closeAccount() {
        System.out.println("\n--- Close Account ---");
        Account acc = login();
        if (acc == null) return;

        System.out.print("Are you sure you want to close this account? (yes/no): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            bank.closeAccount(acc);
            System.out.println("Account closed successfully.");
        } else {
            System.out.println("Account closure cancelled.");
        }
    }

    private static void viewAllAccounts() {
        System.out.println("\n--- All Accounts (Admin View) ---");
        var all = bank.getAllAccounts();
        if (all.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("Acc No.    Name                 Balance");
        for (Account acc : all) {
            System.out.printf("%-11d%-21s%.2f%n", acc.getAccountNumber(), acc.getName(), acc.getBalance());
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    /** Asks for account number + PIN and returns the matching account, or null if login fails. */
    private static Account login() {
        int accNo = readInt("Enter your account number: ");
        String pin = readPin("Enter your PIN: ");
        Account acc = bank.authenticate(accNo, pin);
        if (acc == null) {
            System.out.println("Login failed: incorrect account number or PIN.");
            return null;
     }


        return acc;
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    private static String readPin(String prompt) {
        while (true) {
            System.out.print(prompt);
            String pin = sc.nextLine().trim();
            if (pin.matches("\\d{4}")) {
                return pin;
            }
            System.out.println("PIN must be exactly 4 digits.");
        }
    }
}
