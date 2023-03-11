package com.delivery.pagamento.service;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.delivery.pagamento.dto.PagamentoDto;
import com.delivery.pagamento.http.PedidoClient;
import com.delivery.pagamento.model.Pagamento;
import com.delivery.pagamento.model.Status;
import com.delivery.pagamento.repository.PagamentoRepository;

@Service
public class PagamentoService {

	private final PagamentoRepository repository;

	private final ModelMapper model;

	private final PedidoClient pedido;

	@Autowired
	public PagamentoService(PagamentoRepository repository, ModelMapper model, PedidoClient pedido) {
		this.repository = repository;
		this.model = model;
		this.pedido = pedido;
	}

	public Page<PagamentoDto> obterTodos(Pageable paginacao) {
		return repository.findAll(paginacao).map(p -> model.map(p, PagamentoDto.class));
	}

	public PagamentoDto obterPorId(Long id) {
		Pagamento pagamento = repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
		
		PagamentoDto dto = model.map(pagamento, PagamentoDto.class);
		dto.setItens(pedido.obterItensDoPedido(pagamento.getId()).getItens());
		
		return dto;
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
	
	public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        repository.save(pagamento.get());
        pedido.atualizaPagamento(pagamento.get().getPedidoId());
    }

	public void alteraStatus(@NotNull Long id) {
		Optional<Pagamento> pagamento = repository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        repository.save(pagamento.get());
	}
}
