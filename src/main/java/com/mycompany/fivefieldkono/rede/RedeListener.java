package com.mycompany.fivefieldkono.rede;

public interface RedeListener {
    void onConectado(String mensagem);
    void onMensagem(String mensagem);
    void onErro(String erro);
}