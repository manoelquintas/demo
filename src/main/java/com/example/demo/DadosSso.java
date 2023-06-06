package com.example.demo;

class DadosSSO
{
    private static final long serialVersionUID = 1L;
    private String uuid;
    private String mensagem;
    private String assinatura;
    private String certChain;

    public DadosSSO() {
    }

    public DadosSSO(final String assinatura, final String certChain, final String uuid, final String mensagem) {
        this.assinatura = assinatura;
        this.certChain = certChain;
        this.uuid = uuid;
        this.mensagem = mensagem;
    }

    public String getAssinatura() {
        return this.assinatura;
    }

    public void setAssinatura(final String assinatura) {
        this.assinatura = assinatura;
    }

    public String getCertChain() {
        return this.certChain;
    }

    public void setCertChain(final String certChain) {
        this.certChain = certChain;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(final String mensagem) {
        this.mensagem = mensagem;
    }
}