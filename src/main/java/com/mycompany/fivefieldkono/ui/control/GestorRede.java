package com.mycompany.fivefieldkono.ui.control;

import com.mycompany.fivefieldkono.rede.*;

/**
 * Gere a ligação de rede de uma partida, escondendo do controlador os
 * detalhes de ser servidor ou cliente.
 * <p>
 * Arranca o {@link Servidor} ou o {@link Cliente} na sua própria thread,
 * reencaminha as mensagens a enviar e fecha a ligação quando o jogo
 * termina. Os eventos recebidos são entregues ao {@link RedeListener}
 * fornecido (normalmente o controlador do jogo).
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public class GestorRede {

    /** {@code true} se esta máquina é o servidor. */
    private final boolean isServidor;

    /** Objeto servidor, se aplicável. */
    private Servidor servidor;

    /** Objeto cliente, se aplicável. */
    private Cliente cliente;

    /**
     * Cria o gestor de rede e arranca a ligação adequada.
     *
     * @param isServidor {@code true} para criar servidor, {@code false} para cliente
     * @param host       endereço do servidor (usado só no cliente)
     * @param porta      porta do servidor (usada só no cliente)
     * @param listener   recetor dos eventos de rede
     */
    public GestorRede(boolean isServidor, String host, int porta, RedeListener listener) {
        this.isServidor = isServidor;
        if (isServidor) {
            servidor = new Servidor(listener);
            new Thread(servidor).start();
        } else {
            cliente = new Cliente(host, porta, listener);
            new Thread(cliente).start();
        }
    }

    /**
     * Envia uma mensagem ao adversário.
     *
     * @param msg a mensagem a enviar
     */
    public void enviar(String msg) {
        if (isServidor) { if (servidor != null) servidor.enviar(msg); }
        else            { if (cliente  != null) cliente.enviar(msg);  }
    }

    /**
     * Devolve a porta que o servidor conseguiu abrir.
     *
     * @return a porta usada, ou {@link Servidor#PORTA_BASE} se não for servidor
     */
    public int getPortaServidor() {
        return servidor != null ? servidor.getPortaUsada() : Servidor.PORTA_BASE;
    }

    /**
     * Fecha a ligação de rede ativa.
     */
    public void fechar() {
        if (servidor != null) servidor.fechar();
        if (cliente  != null) cliente.fechar();
    }
}
