package br.com.alurafood.pedidos.dto;

import br.com.alurafood.pedidos.model.Status;

public class StatusDto {
	
    private Status status;

	public StatusDto() {
	}

	public StatusDto(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
    
    
}
