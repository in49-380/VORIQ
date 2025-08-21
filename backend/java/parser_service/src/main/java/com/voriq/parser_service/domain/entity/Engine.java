package com.voriq.parser_service.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "engines",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_engines_type_fuel",
                columnNames = {"type", "fuel_type_id"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Engine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(name = "type", nullable = false) // БЕЗ unique=true
    private String type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "fuel_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_engine_fuel_type"))
    @ToString.Exclude
    private FuelType fuelType;

    @OneToMany(mappedBy = "engine", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private Set<Car> cars = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Engine other)) return false;
        return Objects.equals(type, other.type)
                && Objects.equals(
                fuelType != null ? fuelType.getName() : null,
                other.fuelType != null ? other.fuelType.getName() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, fuelType != null ? fuelType.getName() : null);
    }
}

