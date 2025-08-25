package com.voriq.parser_service.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cars")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", referencedColumnName = "id")
    private Engine engine;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "id")
    private Model model;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id", referencedColumnName = "id")
    private Year year;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (!id.equals(car.id)) return false;
        if (!engine.equals(car.engine)) return false;
        return model.equals(car.model);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + engine.hashCode();
        result = 31 * result + model.hashCode();
        return result;
    }
}
