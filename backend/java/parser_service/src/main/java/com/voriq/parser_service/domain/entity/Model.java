package com.voriq.parser_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "models",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_models_brand_name",
                columnNames = {"brand_id", "name"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(name = "name", nullable = false) // БЕЗ unique=true
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_model_brand"))
    @ToString.Exclude
    private Brand brand;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model other)) return false;
        String b1 = brand != null ? brand.getName() : null;
        String b2 = other.brand != null ? other.brand.getName() : null;
        return Objects.equals(name, other.name) && Objects.equals(b1, b2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, brand != null ? brand.getName() : null);
    }
}
