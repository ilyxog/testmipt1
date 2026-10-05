import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.forLanguageTag("ru-RU"));

        Account account = new Account(
                12345,
                999,
                new BigDecimal("10000.00"),
                BankType.AUM
        );

        System.out.println("Добро пожаловать!");
        System.out.print("Введите номер карты: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Ошибка ввода.");
            return;
        }

        int cardNumber = scanner.nextInt();

        System.out.print("Введите PIN-код: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Ошибка ввода.");
            return;
        }

        int pinCode = scanner.nextInt();

        // Проверка авторизации
        if (cardNumber != account.getCardNumber()
                || pinCode != account.getPinCode()) {
            System.out.println("Ошибка доступа.");
            return;
        }

        System.out.println("Авторизация успешна!");

        CashMachine cashMachine = new CashMachine();

        // Тест 1: внесение денег
        System.out.print("Введите сумму для внесения: ");

        if (!scanner.hasNextDouble()) {
            System.out.println("Ошибка ввода.");
            return;
        }

        double depositValue = scanner.nextDouble();
        BigDecimal depositAmount = BigDecimal.valueOf(depositValue);

        BigDecimal balance = account.getBalance();

        balance = cashMachine.deposit(balance, depositAmount);

        System.out.printf(
                "Баланс после внесения: %.2f руб.%n",
                balance
        );

        // Тест 2: снятие денег
        System.out.print("Введите сумму для снятия: ");

        if (!scanner.hasNextDouble()) {
            System.out.println("Ошибка ввода.");
            return;
        }

        double withdrawValue = scanner.nextDouble();
        BigDecimal withdrawAmount = BigDecimal.valueOf(withdrawValue);

        balance = cashMachine.withdraw(
                balance,
                withdrawAmount,
                account.getBankType()
        );

        System.out.printf(
                "Баланс после снятия: %.2f руб.%n",
                balance
        );

        scanner.close();
    }
}