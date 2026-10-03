package com.esg.cidades.service;

import com.esg.cidades.exception.RecursoNaoEncontradoException;
import com.esg.cidades.model.Cidade;
import com.esg.cidades.repository.CidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CidadeServiceTest {

    @Mock
    private CidadeRepository cidadeRepository;

    @InjectMocks
    private CidadeService cidadeService;

    private Cidade cidadeExemplo;

    @BeforeEach
    void setUp() {
        cidadeExemplo = new Cidade("Curitiba", "PR", 1963726L, 85.0, 78.0, 90.0);
        cidadeExemplo.setId(1L);
    }

    @Test
    void deveListarTodasAsCidades() {
        when(cidadeRepository.findAll()).thenReturn(List.of(cidadeExemplo));

        List<Cidade> resultado = cidadeService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Curitiba", resultado.get(0).getNome());
        verify(cidadeRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarCidadePorIdComSucesso() {
        when(cidadeRepository.findById(1L)).thenReturn(Optional.of(cidadeExemplo));

        Cidade resultado = cidadeService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Curitiba", resultado.getNome());
    }

    @Test
    void deveLancarExcecaoQuandoCidadeNaoEncontrada() {
        when(cidadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> cidadeService.buscarPorId(99L));
    }

    @Test
    void deveCriarNovaCidade() {
        when(cidadeRepository.save(any(Cidade.class))).thenReturn(cidadeExemplo);

        Cidade resultado = cidadeService.criar(cidadeExemplo);

        assertNotNull(resultado);
        assertEquals("PR", resultado.getUf());
        verify(cidadeRepository, times(1)).save(cidadeExemplo);
    }

    @Test
    void deveCalcularScoreEsgCorretamente() {
        // média de (85 + 78 + 90) / 3 = 84.33
        assertEquals(84.33, cidadeExemplo.getScoreEsg());
    }

    @Test
    void deveDeletarCidadeExistente() {
        when(cidadeRepository.findById(1L)).thenReturn(Optional.of(cidadeExemplo));
        doNothing().when(cidadeRepository).delete(cidadeExemplo);

        cidadeService.deletar(1L);

        verify(cidadeRepository, times(1)).delete(cidadeExemplo);
    }
}
