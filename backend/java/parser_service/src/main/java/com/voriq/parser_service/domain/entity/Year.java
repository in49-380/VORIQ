package com.voriq.parser_service.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "cars_year",
        uniqueConstraints = @UniqueConstraint(name = "uq_year_year", columnNames = "year")
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Year {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "year", nullable = false, unique = true)
    private Integer year;

    @OneToMany(mappedBy = "year", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private Set<Car> cars = new HashSet<>();
}
