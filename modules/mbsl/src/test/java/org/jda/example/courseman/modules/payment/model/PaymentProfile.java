package org.jda.example.courseman.modules.payment.model;

/**
 * @overview 
 *  Represents a payment profile.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class PaymentProfile {

  private String bankName;
  private String accountNumber;

  /**
   * @effects 
   *
   * @version 
   */
  public PaymentProfile(String bankName, String accountNumber) {
    this.bankName = bankName;
    this.accountNumber = accountNumber;
  }

  
  /**
   * @effects return bankName
   */
  public String getBankName() {
    return bankName;
  }


  /**
   * @effects set bankName = bankName
   */
  public void setBankName(String bankName) {
    this.bankName = bankName;
  }


  /**
   * @effects return accountNumber
   */
  public String getAccountNumber() {
    return accountNumber;
  }


  /**
   * @effects set accountNumber = accountNumber
   */
  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }


  /**
   * @effects 
   *  return a formatted string for {@link #bankName} and {@link #accountNumber}.
   */
  public String getDetailsAsString() {
    return "Bank: " + bankName + "\n" + "Account no: " + accountNumber;
  }

}
