import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
public class Q1_ATM {
    static class InsufficientFundsException
            extends Exception {
        InsufficientFundsException(String message) {
            super(message);
        }
    }
    static void withdraw(String account,
                         double amount,
                         double balance)
            throws InsufficientFundsException {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException(
                    "Account number is required");
        }
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive and finite");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(
                    "Insufficient balance");
        }
        System.out.printf(
                "Withdrawal successful: Rs. %.2f%n",
                amount);
        System.out.printf(
                "Remaining balance: Rs. %.2f%n",
                balance - amount);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter account number: ");
            String account = sc.nextLine();

            System.out.print("Enter withdrawal amount: ");
            double amount =
                    Double.parseDouble(sc.nextLine());

            withdraw(account, amount, 5000.0);

        } catch (NumberFormatException e) {
            System.out.println(
                    "Invalid input: enter a numeric amount");

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Validation error: " + e.getMessage());

        } catch (InsufficientFundsException e) {
            System.out.println(
                    "Transaction declined: " + e.getMessage());

        } catch (Exception e) {
            System.out.println(
                    "Unexpected failure: " + e.getMessage());

        } finally {
            System.out.println(
                    "ATM session closed; cleanup completed.");
        }
    }
}
