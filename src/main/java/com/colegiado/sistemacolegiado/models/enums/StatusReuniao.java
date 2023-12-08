package com.colegiado.sistemacolegiado.models.enums;

public enum StatusReuniao {
    ENCERRADA ("Encerrada"),
    PROGRAMADA ("Programada");

    private String statusReuniao;

    StatusReuniao (String _statusReuniao){
        this.statusReuniao = _statusReuniao;
    }

    public String getStatusReuniao (){
        return statusReuniao;
    }

}
