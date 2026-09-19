import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class BankService {

    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";

    
    private Map<Integer, Account> accounts = new LinkedHashMap<>();
    private int nextAccountNumber = 1001; // account numbers start from 1001

    public BankService() {
        loadAccounts();
    }

  

    private void loadAccounts() {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) {
            return; 
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Account acc = Account.fromFileLine(line);
                accounts.put(acc.getAccountNumber(), acc);
                if (acc.getAccountNumber() >= nextAccountNumber) {
                    nextAccountNumber = acc.getAccountNumber() + 1;
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read accounts file (" + e.getMessage() + ")");
        }
    }

   
    private void saveAccounts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Account acc : accounts.values()) {
                writer.println(acc.toFileLine());
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save accounts file (" + e.getMessage() + ")");
        }
    }

    
    private void logTransaction(int accountNumber, String type, double amount, double balanceAfter) {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        String line = accountNumber + "," + type + "," + String.format("%.2f", amount)
                + "," + String.format("%.2f", balanceAfter) + "," + timestamp;
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            writer.println(line);
        } catch (IOException e) {
            System.out.println("Warning: could not write transaction log (" + e.getMessage() + ")");
        }
    }

    

    public Account createAccount(String name, String pin, double initialDeposit) {
        int accNo = nextAccountNumber++;
        Account acc = new Account(accNo, name, pin, initialDeposit);
        accounts.put(accNo, acc);
        saveAccounts();
        if (initialDeposit > 0) {
            logTransaction(accNo, "OPENING DEPOSIT", initialDeposit, initialDeposit);
        }
        return acc;
    }

    public Account findAccount(int accountNumber) {
        return accounts.get(accountNumber);
    }

    
    public Account authenticate(int accountNumber, String pin) {
        Account acc = accounts.get(accountNumber);
        if (acc != null && acc.getPin().equals(pin)) {
            return acc;
        }
        return null;
    }

    public void deposit(Account acc, double amount) {
        acc.setBalance(acc.getBalance() + amount);
        saveAccounts();
        logTransaction(acc.getAccountNumber(), "DEPOSIT", amount, acc.getBalance());
    }

    public void withdraw(Account acc, double amount) throws InsufficientBalanceException {
        if (amount > acc.getBalance()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: Rs. " + String.format("%.2f", acc.getBalance()));
        }
        acc.setBalance(acc.getBalance() - amount);
        saveAccounts();
        logTransaction(acc.getAccountNumber(), "WITHDRAW", amount, acc.getBalance());
    }

    public void transfer(Account from, Account to, double amount) throws InsufficientBalanceException {
        if (amount > from.getBalance()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: Rs. " + String.format("%.2f", from.getBalance()));
        }
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        saveAccounts();
        logTransaction(from.getAccountNumber(), "TRANSFER OUT to " + to.getAccountNumber(), amount, from.getBalance());
        logTransaction(to.getAccountNumber(), "TRANSFER IN from " + from.getAccountNumber(), amount, to.getBalance());
    }

    public void closeAccount(Account acc) {
        accounts.remove(acc.getAccountNumber());
        saveAccounts();
        logTransaction(acc.getAccountNumber(), "ACCOUNT CLOSED", 0, 0);
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    
    public List<String> getMiniStatement(int accountNumber, int maxEntries) {
        List<String> matching = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return matching;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(accountNumber + ",")) {
                    matching.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read transactions file (" + e.getMessage() + ")");
        }

        Collections.reverse(matching); // most recent first
        if (matching.size() > maxEntries) {
            return matching.subList(0, maxEntries);
        }
        return matching;
    }
}
