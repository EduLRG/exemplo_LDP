package com.mycompany.fivefieldkono.rede;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor implements Runnable {
    public static final int PORTA = 6666;

    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream saida;
    private RedeListener listener;
    private boolean ativo;

    public Servidor(RedeListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORTA);
            listener.onConectado("Servidor à escuta na porta " + PORTA + "...");
            socket  = serverSocket.accept();
            entrada = new DataInputStream(socket.getInputStream());
            saida   = new DataOutputStream(socket.getOutputStream());
            listener.onConectado("Cliente ligado!");
            ativo = true;
            ouvirMensagens();
        } catch (IOException e) {
            listener.onErro("Servidor: " + e.getMessage());
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
            if (socket     != null) socket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) { /* ignora */ }
    }
}