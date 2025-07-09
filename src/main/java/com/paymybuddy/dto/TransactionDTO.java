package com.paymybuddy.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransactionDTO {
    private String email; // destinataire ou expéditeur selon le contexte
    private String amount; // "+25.00€" ou "-10.00€"
    private String description;

    public TransactionDTO(String email, BigDecimal amount,String description, boolean received) {
        this.email = email;
        String sign = received ? "+" : "-";
        this.amount = sign + amount.setScale(2, RoundingMode.HALF_EVEN).toPlainString() + "€";
        this.description= description;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    
}