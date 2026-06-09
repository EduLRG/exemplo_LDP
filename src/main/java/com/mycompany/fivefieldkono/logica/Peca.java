package com.mycompany.fivefieldkono.logica;

public class Peca {
    private final Jogador dono;

    public Peca(Jogador dono) {
        this.dono = dono;
    }

    public Jogador getDono() {
        return dono;
    }
}