package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "estados")
public class StatesEntity {
    @Id
    @Column("id_estado")
    private Integer id;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;
}
