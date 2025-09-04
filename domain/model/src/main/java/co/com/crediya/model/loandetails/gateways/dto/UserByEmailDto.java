package co.com.crediya.model.loandetails.gateways.dto;

import java.math.BigDecimal;

public record UserByEmailDto(
        String name,
        String lastname,
        String email,
        BigDecimal baseSalary
) {
}
