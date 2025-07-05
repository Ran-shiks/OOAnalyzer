import java.util.*;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        BankingApplication app = new BankingApplication();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n====== Banking Application Menu ======");
            System.out.println("1. Create Account");
            System.out.println("2. Display All Accounts");
            System.out.println("3. Update Account Balance");
            System.out.println("4. Delete Account");
            System.out.println("5. Deposit Amount");
            System.out.println("6. Withdraw Amount");
            System.out.println("7. Search Account");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    app.createAccount();
                    break;
                case 2:
                    app.displayAllAccounts();
                    break;
                case 3:
                    System.out.print("Enter account number: ");
                    int updAcc = sc.nextInt();
                    app.updateAccountBalance(updAcc);
                    break;
                case 4:
                    System.out.print("Enter account number to delete: ");
                    int delAcc = sc.nextInt();
                    app.deleteAccount(delAcc);
                    break;
                case 5:
                    System.out.print("Enter account number: ");
                    int depAcc = sc.nextInt();
                    app.depositAmount(depAcc);
                    break;
                case 6:
                    System.out.print("Enter account number: ");
                    int witAcc = sc.nextInt();
                    app.withdrawAmount(witAcc);
                    break;
                case 7:
                    System.out.print("Enter account number: ");
                    int srchAcc = sc.nextInt();
                    app.searchAccount(srchAcc);
                    break;
                case 8:
                    System.out.println("Thank you for using the Banking Application!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    // ----------------------------------------------------------
    // BankingApplication class
    // ----------------------------------------------------------
    static class BankingApplication {
        private List<Account> accounts = new ArrayList();
        private Scanner sc = new Scanner(System.in);

        public void createAccount() {
            System.out.println("Select Account Type: 1.Savings 2.Salary 3.Current");
            int type = sc.nextInt();
            System.out.print("Enter name: ");
            String name = sc.next();
            System.out.print("Enter account number: ");
            int accNumber = sc.nextInt();
            if (!isAccountNumberUnique(accNumber)) {
                System.out.println("Account number already exists!");
                return;
            }
            System.out.print("Enter initial balance: ");
            int balance = sc.nextInt();

            Account acc = null;
            switch (type) {
                case 1:
                    acc = new SavingsAccount(name, accNumber, balance);
                    break;
                case 2:
                    acc = new SalaryAccount(name, accNumber, balance);
                    break;
                case 3:
                    acc = new CurrentAccount(name, accNumber, balance);
                    break;
                default:
                    System.out.println("Invalid account type.");
                    return;
            }
            accounts.add(acc);
            System.out.println("Account created successfully.");
        }

        public void displayAllAccounts() {
            if (accounts.isEmpty()) {
                System.out.println("No accounts available.");
                return;
            }
            for (Account acc : accounts) {
                acc.display();
            }
        }

        public void updateAccountBalance(int accountNumber) {
            Account acc = findAccount(accountNumber);
            if (acc != null) {
                System.out.print("Enter new balance: ");
                int newBal = sc.nextInt();
                acc.updateBalance(newBal);
                System.out.println("Balance updated.");
            } else {
                System.out.println("Account not found.");
            }
        }

        public void deleteAccount(int accountNumber) {
            Account acc = findAccount(accountNumber);
            if (acc != null) {
                accounts.remove(acc);
                System.out.println("Account deleted.");
            } else {
                System.out.println("Account not found.");
            }
        }

        public void depositAmount(int accountNumber) {
            Account acc = findAccount(accountNumber);
            if (acc != null) {
                System.out.print("Enter amount to deposit: ");
                int amt = sc.nextInt();
                acc.updateBalance(acc.getBalance() + amt);
                System.out.println("Deposit successful. New balance: " + acc.getBalance());
            } else {
                System.out.println("Account not found.");
            }
        }

        public void withdrawAmount(int accountNumber) {
            Account acc = findAccount(accountNumber);
            if (acc != null) {
                System.out.print("Enter amount to withdraw: ");
                int amt = sc.nextInt();
                if (isPossibleWithdraw(accountNumber, amt)) {
                    acc.updateBalance(acc.getBalance() - amt);
                    System.out.println("Withdrawal successful. New balance: " + acc.getBalance());
                } else {
                    System.out.println("Insufficient balance.");
                }
            } else {
                System.out.println("Account not found.");
            }
        }

        public void searchAccount(int accountNumber) {
            Account acc = findAccount(accountNumber);
            if (acc != null) {
                acc.display();
            } else {
                System.out.println("Account not found.");
            }
        }

        public boolean isPossibleWithdraw(int accountNumber, int amount) {
            Account acc = findAccount(accountNumber);
            return acc != null && acc.getBalance() >= amount;
        }

        public boolean isAccountExist(int accountNumber) {
            return findAccount(accountNumber) != null;
        }

        public boolean isAccountNumberUnique(int accountNumber) {
            return findAccount(accountNumber) == null;
        }

        private Account findAccount(int accountNumber) {
            for (Account acc : accounts) {
                if (acc.getAccountNumber() == accountNumber) {
                    return acc;
                }
            }
            return null;
        }
    }

    // ----------------------------------------------------------
    // Abstract Account class
    // ----------------------------------------------------------
    static abstract class Account {
        private String name;
        private int accountNumber;
        private LocalDate creationDate;
        private int balance;

        public Account(String name, int accountNumber, int balance) {
            this.name = name;
            this.accountNumber = accountNumber;
            this.creationDate = LocalDate.now();
            this.balance = balance;
        }

        public String getName() { return name; }
        public int getAccountNumber() { return accountNumber; }
        public LocalDate getCreationDate() { return creationDate; }
        public int getBalance() { return balance; }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public abstract void display();
        public abstract void updateBalance(int amount);
    }

    // ----------------------------------------------------------
    // SavingsAccount class
    // ----------------------------------------------------------
    static class SavingsAccount extends Account {
        public SavingsAccount(String name, int accountNumber, int balance) {
            super(name, accountNumber, balance);
        }

        @Override
        public void display() {
            System.out.println("Savings Account - " + getAccountNumber() +
                    ", Name: " + getName() + ", Balance: " + getBalance() +
                    ", Created: " + getCreationDate());
        }

        @Override
        public void updateBalance(int amount) {
            setBalance(amount);
        }
    }

    // ----------------------------------------------------------
    // SalaryAccount class
    // ----------------------------------------------------------
    static class SalaryAccount extends Account {
        public SalaryAccount(String name, int accountNumber, int balance) {
            super(name, accountNumber, balance);
        }

        @Override
        public void display() {
            System.out.println("Salary Account - " + getAccountNumber() +
                    ", Name: " + getName() + ", Balance: " + getBalance() +
                    ", Created: " + getCreationDate());
        }

        @Override
        public void updateBalance(int amount) {
            setBalance(amount);
        }
    }

    // ----------------------------------------------------------
    // CurrentAccount class
    // ----------------------------------------------------------
    static class CurrentAccount extends Account {
        public CurrentAccount(String name, int accountNumber, int balance) {
            super(name, accountNumber, balance);
        }

        @Override
        public void display() {
            System.out.println("Current Account - " + getAccountNumber() +
                    ", Name: " + getName() + ", Balance: " + getBalance() +
                    ", Created: " + getCreationDate());
        }

        @Override
        public void updateBalance(int amount) {
            setBalance(amount);
        }
    }
}
