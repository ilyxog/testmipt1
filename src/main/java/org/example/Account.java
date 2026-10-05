import java.math.BigDecimal;
import java.math.RoundingMode;

public class Account {

    private final int cardNumber;
    private final int pinCode;
    private final BigDecimal balance;
    private final BankType bankType;

    public Account(int cardNumber, int pinCode,
                   BigDecimal balance, BankType bankType) {

        if (cardNumber < 10000 || cardNumber > 99999) {
            throw new IllegalArgumentException("Номер карты должен состоять из 5 цифр");
        }

        if (pinCode < 100 || pinCode > 999) {
            throw new IllegalArgumentException("ПИН-код должен состоять из 3 цифр");
        }

        this.cardNumber = cardNumber;
        this.pinCode = pinCode;

        if (balance == null) {
            this.balance = BigDecimal.ZERO.setScale(2);
        } else {
            this.balance = balance.setScale(2, RoundingMode.HALF_UP);
        }

        this.bankType = bankType == null ? BankType.NEO : bankType;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public int getPinCode() {
        return pinCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BankType getBankType() {
        return bankType;
    }

    @Override
    public String toString() {
        return String.format(
                "%s Карта: %05d, Баланс: %s руб.",
                bankType.getRussianName(),
                cardNumber,
                balance
        );
    }
}