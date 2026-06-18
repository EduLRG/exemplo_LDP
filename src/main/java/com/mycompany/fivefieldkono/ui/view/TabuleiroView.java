package com.mycompany.fivefieldkono.ui.view;

import com.mycompany.fivefieldkono.logica.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Responsável por desenhar o tabuleiro do Five Field Kono num {@link Canvas}.
 * <p>
 * Esta classe trata apenas da parte visual: pinta as células, as zonas de
 * meta, a peça selecionada, os destinos válidos, as interseções diagonais
 * e as peças dos jogadores. Não contém lógica de jogo nem de rede.
 *
 * @author Eduardo e Laurindo
 * @version 1.0
 */
public class TabuleiroView {

    /** Lado de cada célula em pixéis. */
    public static final int CELULA = 90;

    /** Margem à volta do tabuleiro em pixéis. */
    public static final int MARGEM = 30;

    /** Área de desenho onde o tabuleiro é pintado. */
    private final Canvas canvas;

    /**
     * Cria a vista do tabuleiro, preparando o Canvas com o tamanho certo.
     */
    public TabuleiroView() {
        int tamanho = Tabuleiro.TAMANHO * CELULA + 2 * MARGEM;
        this.canvas = new Canvas(tamanho, tamanho);
    }

    /**
     * Devolve o Canvas para ser colocado na cena.
     *
     * @return o {@link Canvas} do tabuleiro
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Converte a coordenada X de um clique na coluna correspondente.
     *
     * @param x coordenada X em pixéis
     * @return índice da coluna, podendo ficar fora dos limites
     */
    public int colunaDoClique(double x) {
        return (int) ((x - MARGEM) / CELULA);
    }

    /**
     * Converte a coordenada Y de um clique na linha correspondente.
     *
     * @param y coordenada Y em pixéis
     * @return índice da linha, podendo ficar fora dos limites
     */
    public int linhaDoClique(double y) {
        return (int) ((y - MARGEM) / CELULA);
    }

    /**
     * Desenha o tabuleiro completo no estado atual.
     *
     * @param jogo        o jogo a representar
     * @param selLinha    linha da célula selecionada, ou -1 se nenhuma
     * @param selColuna   coluna da célula selecionada, ou -1 se nenhuma
     */
    public void desenhar(Jogo jogo, int selLinha, int selColuna) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int tam = Tabuleiro.TAMANHO;

        gc.setFill(Color.web("#16213e"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int l = 0; l < tam; l++) {
            for (int c = 0; c < tam; c++) {
                double x = MARGEM + c * CELULA;
                double y = MARGEM + l * CELULA;

                desenharCelula(gc, l, c, x, y);
                desenharRealces(gc, jogo, l, c, x, y, selLinha, selColuna);
                desenharBorda(gc, x, y);
                desenharPontoDiagonal(gc, l, c, x, y);
                desenharPeca(gc, jogo, l, c, x, y);
            }
        }
    }

    /**
     * Pinta o fundo de uma célula, destacando as zonas de meta.
     */
    private void desenharCelula(GraphicsContext gc, int l, int c, double x, double y) {
        if (l >= 3)      gc.setFill(Color.web("#2d1b2e"));
        else if (l <= 1) gc.setFill(Color.web("#1b2d2e"));
        else             gc.setFill(Color.web("#16213e"));
        gc.fillRect(x, y, CELULA, CELULA);
    }

    /**
     * Realça a célula selecionada e os destinos válidos.
     */
    private void desenharRealces(GraphicsContext gc, Jogo jogo, int l, int c,
                                 double x, double y, int selLinha, int selColuna) {
        if (l == selLinha && c == selColuna) {
            gc.setFill(Color.web("#f5a623", 0.4));
            gc.fillRect(x, y, CELULA, CELULA);
        }
        if (selLinha != -1 &&
            jogo.getTabuleiro().validarMovimento(selLinha, selColuna, l, c)) {
            gc.setFill(Color.web("#00ff88", 0.25));
            gc.fillRect(x, y, CELULA, CELULA);
        }
    }

    /**
     * Desenha a borda de uma célula.
     */
    private void desenharBorda(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.web("#0f3460"));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, CELULA, CELULA);
    }

    /**
     * Marca com um ponto as interseções onde as diagonais são permitidas.
     */
    private void desenharPontoDiagonal(GraphicsContext gc, int l, int c, double x, double y) {
        if ((l + c) % 2 == 0) {
            gc.setFill(Color.web("#ffffff", 0.15));
            gc.fillOval(x + CELULA/2.0 - 4, y + CELULA/2.0 - 4, 8, 8);
        }
    }

    /**
     * Desenha a peça presente numa célula, se existir.
     */
    private void desenharPeca(GraphicsContext gc, Jogo jogo, int l, int c, double x, double y) {
        Celula celula = jogo.getTabuleiro().getCelula(l, c);
        if (celula.estaVazia()) return;

        Jogador dono = celula.getPeca().getDono();
        double px = x + CELULA / 2.0;
        double py = y + CELULA / 2.0;
        double r  = CELULA * 0.35;

        gc.setFill(Color.web("#000000", 0.4));
        gc.fillOval(px - r + 3, py - r + 3, r * 2, r * 2);

        gc.setFill(dono == Jogador.JOGADOR1
            ? Color.web("#e94560")
            : Color.web("#00b4d8"));
        gc.fillOval(px - r, py - r, r * 2, r * 2);

        gc.setFill(Color.web("#ffffff", 0.3));
        gc.fillOval(px - r * 0.5, py - r * 0.7, r * 0.6, r * 0.4);
    }
}
