package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.core.Query;

public interface QueryDispatcher {
    // Retorna o resultado da consulta.
    <R> R dispatch(Query<R> query);
}
