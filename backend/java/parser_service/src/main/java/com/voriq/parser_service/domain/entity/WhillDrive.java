package com.voriq.parser_service.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "cars_whilldrive",
        uniqueConstraints = @UniqueConstraint(name = "uq_whilldrive_name", columnNames = "name")
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WhillDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "whilldrive", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private Set<Car> cars = new HashSet<>();
}