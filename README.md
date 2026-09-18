# Java Bank Management System

A console-based Bank Management System built in core Java, with file-based
data storage so accounts survive between runs.

## Files

- `Account.java` — represents one bank account (account number, name, PIN, balance).
- `InsufficientBalanceException.java` — custom exception used when a withdrawal/transfer exceeds the balance.
- `BankService.java` — all the banking logic (create account, deposit, withdraw, transfer, mini statement, close account) and file read/write.
- `Main.java` — the console menu that the user interacts with.
- `accounts.txt` / `transactions.txt` — created automatically the first time you run the program; this is where your data is stored.

## How to compile and run

Open a terminal in this folder and run:

```
javac *.java
java Main
```

## Features

1. Create Account — enter your name, set a 4-digit PIN, and make an initial deposit. You get a unique account number (starting from 1001).
2. Deposit Money — log in with account number + PIN, then deposit any amount.
3. Withdraw Money — log in, then withdraw (blocked if it would overdraw the account).
4. Check Balance — view your current balance.
5. Transfer Money — send money from your account to another account number.
6. Mini Statement — view your last 5 transactions with type, amount, balance, and timestamp.
7. Close Account — permanently remove your account after confirmation.
8. View All Accounts (Admin) — lists every account in the bank (for a viva demo).
9. Exit — closes the program (your data is already saved after every transaction).

## Notes for explaining this project (viva tips)

- **OOP concepts used**: encapsulation (private fields + getters/setters in `Account`), a custom exception (`InsufficientBalanceException`), and separation of concerns (`Account` = data, `BankService` = logic, `Main` = user interface).
- **File handling**: `BankService` reads `accounts.txt` on startup and rewrites it after every change; `transactions.txt` is appended to as a running log, which is also how the mini statement is generated.
- **Collections**: accounts are kept in memory in a `LinkedHashMap<Integer, Account>` for fast lookup by account number.
- Data is stored as plain CSV text files so you can open them yourself in a text editor to show how persistence works.
