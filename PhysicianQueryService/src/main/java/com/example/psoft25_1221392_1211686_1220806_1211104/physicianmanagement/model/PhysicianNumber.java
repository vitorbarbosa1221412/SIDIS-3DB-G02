package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PhysicianNumber implements Serializable {

    private String physicianNumber;

    public PhysicianNumber(String physicianNumber) {
        this.physicianNumber = physicianNumber;
    }

}
