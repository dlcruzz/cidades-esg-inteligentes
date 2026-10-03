package com.esg.cidades.service;

import com.esg.cidades.exception.RecursoNaoEncontradoException;
import com.esg.cidades.model.Cidade;
import com.esg.cidades.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CidadeService {

    private final CidadeRepository cidadeRepository;

    @Autowired
    public CidadeService(CidadeRepository cidadeRepository) {
        this.cidadeRepository = cidadeRepository;
    }

    public List<Cidade> listarTodas() {
        return cidadeRepository.findAll();
    }

    public Cidade buscarPorId(Long id) {
        return cidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cidade não encontrada com id: " + id));
    }

    public List<Cidade> buscarPorUf(String uf) {
        return cidadeRepository.findByUfIgnoreCase(uf);
    }

    public List<Cidade> buscarPorNome(String nome) {
        return cidadeRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Cidade criar(Cidade cidade) {
        return cidadeRepository.save(cidade);
    }

    public Cidade atualizar(Long id, Cidade dadosAtualizados) {
        Cidade cidadeExistente = buscarPorId(id);

        cidadeExistente.setNome(dadosAtualizados.getNome());
        cidadeExistente.setUf(dadosAtualizados.getUf());
        cidadeExistente.setPopulacao(dadosAtualizados.getPopulacao());
        cidadeExistente.setIndicadorAmbiental(dadosAtualizados.getIndicadorAmbiental());
        cidadeExistente.setIndicadorSocial(dadosAtualizados.getIndicadorSocial());
        cidadeExistente.setIndicadorGovernanca(dadosAtualizados.getIndicadorGovernanca());

        return cidadeRepository.save(cidadeExistente);
    }

    public void deletar(Long id) {
        Cidade cidade = buscarPorId(id);
        cidadeRepository.delete(cidade);
    }
}
