package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationRequest {
    @Valid
    @NotNull
    Page page;
}
