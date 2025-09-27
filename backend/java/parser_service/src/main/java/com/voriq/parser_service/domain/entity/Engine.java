package com.voriq.parser_service.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cars_engine")
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

    @Column(name = "type", nullable = false)
    @ToString.Include
    private String type;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "fuel_type_id")
    @ToString.Include
    private FuelType fuelType;

    @Column(name = "series_code")
    @ToString.Include
    private String seriesCode;

    @ToString.Include
    private String engineCode;

    @Column(name = "displacement_cc", nullable = false)
    @ToString.Include
    private Double displacementCC;

    @OneToMany(mappedBy = "engine", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private Set<Car> cars = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Engine engine = (Engine) o;

        if (!id.equals(engine.id)) return false;
        if (!type.equals(engine.type)) return false;
        if (!fuelType.equals(engine.fuelType)) return false;
        return displacementCC.equals(engine.displacementCC);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + fuelType.hashCode();
        result = 31 * result + displacementCC.hashCode();
        return result;
    }
}

