package com.mycompany.fivefieldkono.rede;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Lado cliente da ligação em rede do Five Field Kono (TCP/IP).
 * <p>
 * Cria um {@link Socket} para se ligar a um servidor num dado endereço e
 * porta, e depois troca mensagens de texto através de streams. Implementa
 * {@link Runnable} para correr numa <i>thread</i> separada, mantendo a
 * interface gráfica responsiva durante a comunicação.
 *
 * @author Eduardo
 * @version 1.0
 */
public class Cliente implements Runnable {

    /** Endereço (IP ou nome) do servidor a contactar. */
    private final String host;

    /** Porta de comunicação do servidor. */
    private final int porta;

    /** Socket de comunicação com o servidor. */
    private Socket socket;

    /** Stream de entrada (mensagens vindas do servidor). */
    private DataInputStream entrada;

    /** Stream de saída (mensagens enviadas ao servidor). */
    private DataOutputStream saida;

    /** Recetor dos eventos de rede (a interface). */
    private final RedeListener listener;

    /** Indica se o cliente está ativo e a ouvir mensagens. */
    private boolean ativo;

    /**
     * Cria um cliente preparado para ligar a um servidor.
     *
     * @param host     endereço IP ou nome do servidor
     * @param porta    porta de comunicação do servidor
     * @param listener objeto que reage aos eventos de rede
     */
    public Cliente(String host, int porta, RedeListener listener) {
        this.host     = host;
        this.porta    = porta;
        this.listener = listener;
    }

    /**
     * Liga-se ao servidor e começa a ouvir mensagens. Executado na
     * <i>thread</i> do cliente.
     */
    @Override
    public void run() {
        try {
            socket  = new Socket(host, porta);
            entrada = new DataInputStream(socket.getInputStream());
            saida   = new DataOutputStream(socket.getOutputStream());
            listener.onConectado("ADVERSARIO_LIGADO");
            ativo = true;
            ouvirMensagens();
        } catch (IOException e) {
            listener.onErro("Não foi possível ligar a " + host + ":" + porta);
        }
    }

    /**
     * Lê mensagens do servidor em ciclo, reencaminhando-as ao recetor,
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
     * Envia uma mensagem de texto ao servidor.
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
     * Encerra a ligação ao servidor, parando o ciclo de escuta.
     */
    public void fechar() {
        ativo = false;
        try { if (socket != null) socket.close(); } catch (IOException e) {}
    }
}
