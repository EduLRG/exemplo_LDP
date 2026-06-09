package com.mycompany.fivefieldkono.logica;

public class Jogo {
    private final Tabuleiro tabuleiro;
    private Jogador turno;
    private boolean terminado;
    private Jogador vencedor;

    public Jogo() {
        tabuleiro = new Tabuleiro();
        iniciarJogo();
    }

    public final void iniciarJogo() {
        // Limpa o tabuleiro
        for (int l = 0; l < Tabuleiro.TAMANHO; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++)
                tabuleiro.getCelula(l, c).setPeca(null);

        // J1 nas linhas 0 e 1
        for (int l = 0; l <= 1; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++)
                tabuleiro.getCelula(l, c).setPeca(new Peca(Jogador.JOGADOR1));

        // J2 nas linhas 3 e 4
        for (int l = 3; l <= 4; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++)
                tabuleiro.getCelula(l, c).setPeca(new Peca(Jogador.JOGADOR2));

        turno     = Jogador.JOGADOR1;
        terminado = false;
        vencedor  = null;
    }

    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public Jogador getTurno()       { return turno; }
    public boolean isTerminado()    { return terminado; }
    public Jogador getVencedor()    { return vencedor; }

    public boolean jogar(int lo, int co, int ld, int cd) {
        if (terminado) return false;
        Peca p = tabuleiro.getCelula(lo, co).getPeca();
        if (p == null || p.getDono() != turno) return false;
        if (!tabuleiro.validarMovimento(lo, co, ld, cd)) return false;

        tabuleiro.mover(lo, co, ld, cd);

        if (verificarVitoria(turno)) {
            terminado = true;
            vencedor  = turno;
        } else {
            turno = turno.adversario();
        }
        return true;
    }

    public void desistir(Jogador quemDesiste) {
        terminado = true;
        vencedor  = quemDesiste.adversario();
    }

    private boolean verificarVitoria(Jogador j) {
        // J1 vence ocupando linhas 3-4; J2 vence ocupando linhas 0-1
        int linhaA = (j == Jogador.JOGADOR1) ? 3 : 0;
        int linhaB = linhaA + 1;
        for (int l = linhaA; l <= linhaB; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++) {
                Peca peca = tabuleiro.getCelula(l, c).getPeca();
                if (peca == null || peca.getDono() != j) return false;
            }
        return true;
    }
}