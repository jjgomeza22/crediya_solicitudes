package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "solicitud")
public class LoanApplicationEntity {
    @Id
    @Column("id_solicitud")
    private Integer id;

    @Column("monto")
    private BigDecimal amount;

    @Column("plazo")
    private int timeLimit;

    @Column("email")
    private String email;

    @Column("id_estado")
    private int stateId;

    @Column("id_tipo_prestamo")
    private int loanTypeId;
}
