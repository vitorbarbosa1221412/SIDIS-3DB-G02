package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core;

// Q é o tipo específico de Query; R é o tipo de resultado.
public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
