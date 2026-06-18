package com.mycompany.fivefieldkono.logica;

/**
 * Controla o estado e as regras de uma partida de Five Field Kono.
 * <p>
 * Junta o {@link Tabuleiro}, gere de quem é a vez de jogar, aplica os
 * movimentos validados, deteta a condição de vitória e trata da
 * desistência de um jogador. É a classe central da lógica do jogo,
 * independente da interface gráfica e da rede.
 *
 * @author Eduardo
 * @version 1.0
 */
public class Jogo {

    /** Tabuleiro onde decorre a partida. */
    private final Tabuleiro tabuleiro;

    /** Jogador a quem pertence a vez atual. */
    private Jogador turno;

    /** Indica se a partida já terminou. */
    private boolean terminado;

    /** Jogador vencedor, ou {@code null} enquanto a partida não terminar. */
    private Jogador vencedor;

    /**
     * Cria uma nova partida com o tabuleiro já preparado na posição inicial.
     */
    public Jogo() {
        tabuleiro = new Tabuleiro();
        iniciarJogo();
    }

    /**
     * Coloca as peças nas posições iniciais e arranca a partida.
     * <p>
     * Limpa o tabuleiro, distribui as 10 peças do {@code JOGADOR1} pelas
     * linhas 0 e 1, as 10 peças do {@code JOGADOR2} pelas linhas 3 e 4,
     * define o {@code JOGADOR1} como primeiro a jogar e marca a partida
     * como não terminada.
     */
    public final void iniciarJogo() {
        for (int l = 0; l < Tabuleiro.TAMANHO; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++)
                tabuleiro.getCelula(l, c).setPeca(null);

        for (int l = 0; l <= 1; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++)
                tabuleiro.getCelula(l, c).setPeca(new Peca(Jogador.JOGADOR1));

        for (int l = 3; l <= 4; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++)
                tabuleiro.getCelula(l, c).setPeca(new Peca(Jogador.JOGADOR2));

        turno     = Jogador.JOGADOR1;
        terminado = false;
        vencedor  = null;
    }

    /**
     * Devolve o tabuleiro da partida.
     *
     * @return o {@link Tabuleiro} atual
     */
    public Tabuleiro getTabuleiro() { return tabuleiro; }

    /**
     * Devolve o jogador a quem pertence a vez atual.
     *
     * @return o {@link Jogador} do turno atual
     */
    public Jogador getTurno() { return turno; }

    /**
     * Indica se a partida já terminou.
     *
     * @return {@code true} se a partida terminou
     */
    public boolean isTerminado() { return terminado; }

    /**
     * Devolve o jogador vencedor.
     *
     * @return o {@link Jogador} vencedor, ou {@code null} se ainda não houver
     */
    public Jogador getVencedor() { return vencedor; }

    /**
     * Tenta executar uma jogada para o jogador do turno atual.
     * <p>
     * Verifica que a partida não terminou, que existe na origem uma peça
     * pertencente ao jogador da vez e que o movimento respeita as regras.
     * Se tudo for válido, move a peça, verifica a vitória e, caso o jogo
     * continue, passa a vez ao adversário.
     *
     * @param lo linha de origem
     * @param co coluna de origem
     * @param ld linha de destino
     * @param cd coluna de destino
     * @return {@code true} se a jogada foi efetuada, {@code false} se for inválida
     */
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

    /**
     * Termina a partida por desistência de um jogador.
     * <p>
     * O adversário de quem desiste é declarado vencedor.
     *
     * @param quemDesiste o jogador que desiste da partida
     */
    public void desistir(Jogador quemDesiste) {
        terminado = true;
        vencedor  = quemDesiste.adversario();
    }

    /**
     * Verifica se um jogador cumpriu a condição de vitória.
     * <p>
     * O {@code JOGADOR1} vence se ocupar totalmente as linhas 3 e 4 (a zona
     * inicial do adversário); o {@code JOGADOR2} vence se ocupar as linhas
     * 0 e 1.
     *
     * @param j o jogador cuja vitória se quer verificar
     * @return {@code true} se o jogador ocupar por completo a zona de meta
     */
    private boolean verificarVitoria(Jogador j) {
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
