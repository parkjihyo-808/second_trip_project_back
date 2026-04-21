package com.busanit401.second_trip_project_back.domain.car;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trip_rent_company")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RentCarCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String region;

    private String address;

    private String latitude;

    private String longitude;

    private String phone;
}