public class Q6_WalletTransfer {
    static class Account {
        final int id;
        int balance;
        Account(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }
    static void transfer(Account from,
                         Account to,
                         int amount) {
        if (from == to)
            return;
        if (amount <= 0)
            throw new IllegalArgumentException(
                    "Amount must be positive");
        Account first = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;
        if (from.id == to.id)
            throw new IllegalArgumentException(
                    "Account IDs must be unique");
        synchronized (first) {
            synchronized (second) {
                if (from.balance >= amount) {
                    from.balance -= amount;
                    to.balance += amount;
                    System.out.println(
                            "Transferred Rs." + amount +
                            " from " + from.id +
                            " to " + to.id);
                } else {
                    System.out.println(
                            "Insufficient balance in " +
                            from.id);
                }
            }
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        Account a = new Account(101, 5000);
        Account b = new Account(102, 3000);

        Thread t1 = new Thread(() ->
                transfer(a, b, 1000));

        Thread t2 = new Thread(() ->
                transfer(b, a, 500));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("\nFinal balances:");
        System.out.println(
                "Account 101: Rs." + a.balance);
        System.out.println(
                "Account 102: Rs." + b.balance);

        System.out.println(
                "Total balance: Rs." +
                (a.balance + b.balance));
    }
}
