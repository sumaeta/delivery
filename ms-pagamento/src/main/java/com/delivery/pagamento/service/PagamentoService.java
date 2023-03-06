package com.delivery.pagamento.service;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.delivery.pagamento.dto.PagamentoDto;
import com.delivery.pagamento.model.Pagamento;
import com.delivery.pagamento.model.Status;
import com.delivery.pagamento.repository.PagamentoRepository;

@Service
public class PagamentoService {

	private final PagamentoRepository repository;

	private final ModelMapper model;

	@Autowired
	public PagamentoService(PagamentoRepository repository, ModelMapper model) {
		this.repository = repository;
		this.model = model;
	}

	public Page<PagamentoDto> obterTodos(Pageable paginacao) {
		return repository.findAll(paginacao).map(p -> model.map(p, PagamentoDto.class));
	}

	public PagamentoDto obterPorId(Long id) {
		Pagamento pagamento = repository.findById(id).orElseThrow(() -> new EntityNotFoundException());

		return model.map(pagamento, PagamentoDto.class);
	}

	public PagamentoDto criarPagamento(PagamentoDto dto) {
		Pagamento pagamento = model.map(dto, Pagamento.class);
		pagamento.setStatus(Status.CRIADO);
		repository.save(pagamento);

		return model.map(pagamento, PagamentoDto.class);
	}

	public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {
		Pagamento pagamento = model.map(dto, Pagamento.class);
		pagamento.setId(id);
		pagamento = repository.save(pagamento);
		return model.map(pagamento, PagamentoDto.class);
	}

	public void excluirPagamento(Long id) {
		repository.deleteById(id);
	}
}