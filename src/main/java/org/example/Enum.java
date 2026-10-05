import java.math.BigDecimal;

public enum BankType {
    NEO("НеоКредит Банк", new BigDecimal("0.01")),
    AUM("Арум Финтех", new BigDecimal("0.02")),
    VTA("Вектор Альянс Банк", new BigDecimal("0.00"));

    private final String russianName;
    private final BigDecimal commission;

    BankType(String russianName, BigDecimal commission) {
        this.russianName = russianName;
        this.commission = commission;
    }
    public String getRussianName() {
        return russianName;
    }

    public BigDecimal getCommission() {
        return commission;
    }
}