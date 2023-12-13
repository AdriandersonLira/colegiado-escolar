package com.colegiado.sistemacolegiado.models.enums;

public enum TipoDecisao {
    DEFERIDO ("Deferido"),
    INDEFERIDO ("Indeferido");

    private  String tipoDecisao;

    TipoDecisao (String _tipoDecisao){
        tipoDecisao = _tipoDecisao;
    }

    public String getTipoDecisao() {
        return tipoDecisao;
    }
}