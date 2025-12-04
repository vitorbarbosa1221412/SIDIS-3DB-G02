package com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.handlers;

import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.queries.GetPhysicianByNumberQuery;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.repositories.PhysicianReadRepository;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.readmodels.PhysicianReadModel;
import com.example.psoft25_1221392_1211686_1220806_1211104.physicianmanagement.api.PhysicianDTO; // Seu DTO de retorno

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class GetPhysicianByNumberQueryHandlerTest {

    // Simula o repositório MongoDB (Read DB)
    @Mock
    private PhysicianReadRepository physicianReadRepository;

    // Injeta os mocks no handler real que queremos testar
    @InjectMocks
    private GetPhysicianByNumberQueryHandler handler;

    private final String TEST_NUMBER = "P-1234";

    @Test
    void shouldReturnPhysicianDTOWhenFound() {
        //
        GetPhysicianByNumberQuery query = new GetPhysicianByNumberQuery(TEST_NUMBER);

        // Simula o documento MongoDB que seria retornado
        PhysicianReadModel readModel = new PhysicianReadModel(
                1L,
                TEST_NUMBER,
                "Dr. Query Teste",
                "Dermatologia"
        );


        when(physicianReadRepository.findByPhysicianNumber(TEST_NUMBER)).thenReturn(Optional.of(readModel));


        Optional<PhysicianDTO> result = handler.handle(query);



        verify(physicianReadRepository, times(1)).findByPhysicianNumber(TEST_NUMBER);


        assertTrue(result.isPresent(), "O resultado deveria conter o PhysicianDTO.");


        assertEquals(TEST_NUMBER, result.get().getPhysicianNumber());
        assertEquals("Dermatologia", result.get().getSpecialty());
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {

        GetPhysicianByNumberQuery query = new GetPhysicianByNumberQuery(TEST_NUMBER);


        when(physicianReadRepository.findByPhysicianNumber(TEST_NUMBER)).thenReturn(Optional.empty());


        Optional<PhysicianDTO> result = handler.handle(query);



        verify(physicianReadRepository, times(1)).findByPhysicianNumber(TEST_NUMBER);


        assertTrue(result.isEmpty(), "O resultado deveria ser Optional.empty().");
    }
}
