package com.colegiado.sistemacolegiado.models.enums;

public enum StatusProcesso {
    CRIADO("Criado"),
    DISTRIBUIDO("Distribuido"),
    EM_PAUTA("Em Pauta"),
    EM_JULGAMENTO("Em Julgamento"),
    JULGADO("Julgado"),
    NAO_PODE_SER_ALTERADO("Não pode ser alterado");

    private String status;

    StatusProcesso (String _status){
        this.status = _status;
    }

    public String getStatuString(){
        return status;
    }
}
