/**
 * Represents a single bank account.
 * This is a simple data class (POJO) that stores account details
 * and knows how to convert itself to/from a line of text so it can
 * be saved in a file.
 */
public class Account {
    private int accountNumber;
    private String name;
    private String pin;      // stored as String so leading zeros (e.g. "0042") are preserved
    private double balance;

    public Account(int accountNumber, String name, String pin, double balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.pin = pin;
        this.balance = balance;
    }

    // ---------- Getters / Setters ----------
    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Converts this account into one CSV line so it can be written to accounts.txt
     * Format: accountNumber,name,pin,balance
     */
    public String toFileLine() {
        return accountNumber + "," + name + "," + pin + "," + balance;
    }

    /**
     * Rebuilds an Account object from one CSV line read from accounts.txt
     */
    public static Account fromFileLine(String line) {
        String[] parts = line.split(",");
        int accNo = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String pin = parts[2].trim();
        double balance = Double.parseDouble(parts[3].trim());
        return new Account(accNo, name, pin, balance);
    }

    @Override
    public String toString() {
        return "Account No : " + accountNumber +
               "\nName       : " + name +
               "\nBalance    : Rs. " + String.format("%.2f", balance);
    }
}
