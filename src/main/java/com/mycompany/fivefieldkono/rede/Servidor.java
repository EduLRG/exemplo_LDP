package com.mycompany.fivefieldkono.rede;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Lado servidor da ligação em rede do Five Field Kono (TCP/IP).
 * <p>
 * Abre um {@link ServerSocket} numa porta livre, aguarda a ligação de um
 * cliente e depois troca mensagens de texto através de streams. Implementa
 * {@link Runnable} para correr numa <i>thread</i> separada, evitando
 * bloquear a interface gráfica enquanto espera por ligações ou mensagens.
 *
 * @author Eduardo
 * @version 1.0
 */
public class Servidor implements Runnable {

    /** Primeira porta a tentar abrir. */
    public static final int PORTA_BASE = 6666;

    /** Última porta a tentar caso as anteriores estejam ocupadas. */
    public static final int PORTA_MAX  = 6700;

    /** Socket que aceita a ligação do cliente. */
    private ServerSocket serverSocket;

    /** Socket de comunicação com o cliente já ligado. */
    private Socket socket;

    /** Stream de entrada (mensagens vindas do cliente). */
    private DataInputStream entrada;

    /** Stream de saída (mensagens enviadas ao cliente). */
    private DataOutputStream saida;

    /** Recetor dos eventos de rede (a interface). */
    private final RedeListener listener;

    /** Indica se o servidor está ativo e a ouvir mensagens. */
    private boolean ativo;

    /** Porta efetivamente aberta, ou -1 enquanto nenhuma for atribuída. */
    private int portaUsada = -1;

    /**
     * Cria um servidor que notifica o recetor indicado.
     *
     * @param listener objeto que reage aos eventos de rede
     */
    public Servidor(RedeListener listener) {
        this.listener = listener;
    }

    /**
     * Devolve a porta que o servidor conseguiu abrir.
     *
     * @return o número da porta usada, ou -1 se nenhuma foi aberta
     */
    public int getPortaUsada() {
        return portaUsada;
    }

    /**
     * Procura uma porta livre, aguarda a ligação do cliente e começa a
     * ouvir mensagens. Executado na <i>thread</i> do servidor.
     */
    @Override
    public void run() {
        for (int p = PORTA_BASE; p <= PORTA_MAX; p++) {
            try {
                serverSocket = new ServerSocket(p);
                portaUsada = p;
                break;
            } catch (IOException ex) {
                // porta ocupada, tenta a seguinte
            }
        }

        if (serverSocket == null) {
            listener.onErro("Sem portas livres entre " + PORTA_BASE + " e " + PORTA_MAX);
            return;
        }

        try {
            listener.onConectado("À escuta na porta " + portaUsada + "...");
            socket  = serverSocket.accept();
            entrada = new DataInputStream(socket.getInputStream());
            saida   = new DataOutputStream(socket.getOutputStream());
            listener.onConectado("ADVERSARIO_LIGADO");
            ativo = true;
            ouvirMensagens();
        } catch (IOException e) {
            if (ativo) listener.onErro("Servidor: " + e.getMessage());
        }
    }

    /**
     * Lê mensagens do cliente em ciclo, reencaminhando-as ao recetor,
     * até a ligação fechar.
     */
    private void ouvirMensagens() {
        try {
            while (ativo) {
                String msg = entrada.readUTF();
                listener.onMensagem(msg);
            }
        } catch (IOException e) {
            if (ativo) listener.onErro("Ligação perdida.");
        }
    }

    /**
     * Envia uma mensagem de texto ao cliente.
     *
     * @param msg a mensagem a enviar
     */
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

    /**
     * Encerra a ligação e liberta a porta, parando o ciclo de escuta.
     */
    public void fechar() {
        ativo = false;
        try { if (socket != null) socket.close(); } catch (IOException e) {}
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException e) {}
    }
}
