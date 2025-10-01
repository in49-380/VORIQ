package com.voriq.parser_service.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cars_transmission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "gears")
    private Integer gears;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "supplier")
    private String supplier;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "family_code")
    private String family_code;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "marketing_name")
    private String marketingName;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "manual", nullable = false)
    private Boolean manual;

    @OneToMany(mappedBy = "transmission", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private Set<Car> cars = new HashSet<>();
}