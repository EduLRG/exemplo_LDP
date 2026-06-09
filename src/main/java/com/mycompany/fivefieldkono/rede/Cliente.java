package com.mycompany.fivefieldkono.rede;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente implements Runnable {
    private final String host;
    private final int porta;
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream saida;
    private RedeListener listener;
    private boolean ativo;

    public Cliente(String host, int porta, RedeListener listener) {
        this.host     = host;
        this.porta    = porta;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            socket  = new Socket(host, porta);
            entrada = new DataInputStream(socket.getInputStream());
            saida   = new DataOutputStream(socket.getOutputStream());
            listener.onConectado("Ligado ao servidor " + host + ":" + porta);
            ativo = true;
            ouvirMensagens();
        } catch (IOException e) {
            listener.onErro("Cliente: " + e.getMessage());
        }
    }

    private void ouvirMensagens() {
        try {
            while (ativo) {
                String msg = entrada.readUTF();
                listener.onMensagem(msg);
            }
        } catch (IOException e) {
            listener.onErro("Ligação perdida.");
        }
    }

    public void enviar(String msg) {
        try {
            if (saida != null) {
                saida.writeUTF(msg);
                saida.flush();
            }
        } catch (IOException e) {
            listener.onErro("Erro ao enviar: " + e.getMessage());
        }
    }

    public void fechar() {
        ativo = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) { /* ignora */ }
    }
}