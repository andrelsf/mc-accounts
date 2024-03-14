package andrelsf.github.com.mcaccounts.entities.domains;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "accounts")
public class AccountEntity {

  @Id
  @Column("account_id")
  private String accountId;

  @Column("customer_id")
  private String customerId;

  @Column("agency")
  private Integer agency;

  @Column("account_number")
  private Integer accountNumber;

  @Column("status")
  private String status;

  @Column("daily_transfer_limit")
  private BigDecimal dailyTransferLimit;

  @Column("balance")
  private BigDecimal balance;

  @CreatedDate
  @Column("created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column("last_updated")
  private LocalDateTime lastUpdated;

  public AccountEntity() {
  }

  public AccountEntity(
      String accountId,
      String customerId,
      Integer agency,
      Integer accountNumber,
      String status,
      BigDecimal dailyTransferLimit,
      BigDecimal balance,
      LocalDateTime createdAt,
      LocalDateTime lastUpdated) {
    this.accountId = accountId;
    this.customerId = customerId;
    this.agency = agency;
    this.accountNumber = accountNumber;
    this.status = status;
    this.dailyTransferLimit = dailyTransferLimit;
    this.balance = balance;
    this.createdAt = createdAt;
    this.lastUpdated = lastUpdated;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public Integer getAgency() {
    return agency;
  }

  public void setAgency(Integer agency) {
    this.agency = agency;
  }

  public Integer getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(Integer accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public BigDecimal getDailyTransferLimit() {
    return dailyTransferLimit;
  }

  public void setDailyTransferLimit(BigDecimal dailyTransferLimit) {
    this.dailyTransferLimit = dailyTransferLimit;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountEntity that = (AccountEntity) o;
    return Objects.equals(accountId, that.accountId) && Objects.equals(customerId,
        that.customerId) && Objects.equals(agency, that.agency) && Objects.equals(
        accountNumber, that.accountNumber) && Objects.equals(status, that.status)
        && Objects.equals(dailyTransferLimit, that.dailyTransferLimit)
        && Objects.equals(balance, that.balance) && Objects.equals(createdAt,
        that.createdAt) && Objects.equals(lastUpdated, that.lastUpdated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, customerId, agency, accountNumber, status, dailyTransferLimit,
        balance, createdAt, lastUpdated);
  }

  @Override
  public String toString() {
    return "AccountEntity{" +
        "accountId='" + accountId + '\'' +
        ", customerId='" + customerId + '\'' +
        ", agency=" + agency +
        ", accountNumber=" + accountNumber +
        ", status='" + status + '\'' +
        ", dailyTransferLimit=" + dailyTransferLimit +
        ", balance=" + balance +
        ", createdAt=" + createdAt +
        ", lastUpdated=" + lastUpdated +
        '}';
  }

  public void debit(BigDecimal amount, LocalDateTime lastUpdated) {
    this.balance = this.balance.subtract(amount);
    this.lastUpdated = lastUpdated;
  }

  public void credit(BigDecimal amount, LocalDateTime lastUpdated) {
    this.balance = this.balance.add(amount);
    this.lastUpdated = lastUpdated;
  }

  public void decreaseDailyTransferLimit(BigDecimal amount) {
    this.dailyTransferLimit = this.dailyTransferLimit.subtract(amount);
  }
}
