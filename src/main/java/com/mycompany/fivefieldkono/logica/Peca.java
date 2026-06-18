package com.mycompany.fivefieldkono.logica;

/**
 * Representa uma peça do jogo Five Field Kono.
 * <p>
 * Cada peça pertence a um {@link Jogador} e esse dono nunca muda
 * durante a partida (por isso o atributo é {@code final}).
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public class Peca {

    /** Jogador a quem esta peça pertence. */
    private final Jogador dono;

    /**
     * Cria uma nova peça pertencente a um jogador.
     *
     * @param dono o jogador dono desta peça
     */
    public Peca(Jogador dono) {
        this.dono = dono;
    }

    /**
     * Devolve o jogador dono desta peça.
     *
     * @return o {@link Jogador} dono da peça
     */
    public Jogador getDono() {
        return dono;
    }
}
