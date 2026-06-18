package com.mycompany.fivefieldkono.logica;

/**
 * Representa o tabuleiro 5x5 do jogo Five Field Kono.
 * <p>
 * Guarda uma grelha de 25 objetos {@link Celula} e contém a lógica de
 * validação de movimentos segundo as regras do jogo: movimentos
 * ortogonais para casas vazias adjacentes são sempre permitidos,
 * enquanto os diagonais só são permitidos a partir de interseções
 * onde a soma da linha com a coluna é par.
 *
 * @author Eduardo
 * @version 1.0
 */
public class Tabuleiro {

    /** Número de linhas e colunas do tabuleiro (5x5). */
    public static final int TAMANHO = 5;

    /** Grelha de células que compõem o tabuleiro. */
    private final Celula[][] celulas = new Celula[TAMANHO][TAMANHO];

    /**
     * Cria um tabuleiro 5x5 com todas as células vazias.
     */
    public Tabuleiro() {
        for (int l = 0; l < TAMANHO; l++)
            for (int c = 0; c < TAMANHO; c++)
                celulas[l][c] = new Celula(l, c);
    }

    /**
     * Devolve a célula numa dada posição.
     *
     * @param l índice da linha (0 a 4)
     * @param c índice da coluna (0 a 4)
     * @return a {@link Celula} nessa posição
     */
    public Celula getCelula(int l, int c) { return celulas[l][c]; }

    /**
     * Verifica se uma posição está dentro dos limites do tabuleiro.
     *
     * @param l índice da linha
     * @param c índice da coluna
     * @return {@code true} se a posição for válida
     */
    private boolean dentro(int l, int c) {
        return l >= 0 && l < TAMANHO && c >= 0 && c < TAMANHO;
    }

    /**
     * Valida se um movimento é legal segundo as regras do Five Field Kono.
     * <p>
     * Um movimento é válido quando: a origem e o destino estão dentro do
     * tabuleiro, existe uma peça na origem, o destino está vazio, e o
     * deslocamento é para uma casa adjacente. Movimentos ortogonais são
     * sempre aceites; movimentos diagonais só são aceites se a célula de
     * origem for uma interseção onde {@code (linha + coluna)} é par.
     *
     * @param lo linha de origem
     * @param co coluna de origem
     * @param ld linha de destino
     * @param cd coluna de destino
     * @return {@code true} se o movimento for válido, {@code false} caso contrário
     */
    public boolean validarMovimento(int lo, int co, int ld, int cd) {
        if (!dentro(lo, co) || !dentro(ld, cd)) return false;
        if (celulas[lo][co].estaVazia())         return false;
        if (!celulas[ld][cd].estaVazia())         return false;

        int dl = Math.abs(ld - lo);
        int dc = Math.abs(cd - co);

        if (dl == 0 && dc == 0) return false;
        if (dl > 1  || dc > 1)  return false;

        boolean ortogonal = (dl + dc == 1);
        boolean diagonal  = (dl == 1 && dc == 1);

        if (ortogonal) return true;
        if (diagonal)  return celulas[lo][co].permiteDiagonal();
        return false;
    }

    /**
     * Move uma peça da origem para o destino.
     * <p>
     * Pressupõe que o movimento já foi validado com
     * {@link #validarMovimento(int, int, int, int)}.
     *
     * @param lo linha de origem
     * @param co coluna de origem
     * @param ld linha de destino
     * @param cd coluna de destino
     */
    public void mover(int lo, int co, int ld, int cd) {
        Peca p = celulas[lo][co].getPeca();
        celulas[lo][co].setPeca(null);
        celulas[ld][cd].setPeca(p);
    }
}
