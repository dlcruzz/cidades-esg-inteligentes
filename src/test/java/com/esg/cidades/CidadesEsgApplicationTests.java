package com.esg.cidades;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CidadesEsgApplicationTests {

    @Test
    void contextLoads() {
        // Garante que o contexto Spring sobe corretamente com o profile de teste (H2)
    }

}
