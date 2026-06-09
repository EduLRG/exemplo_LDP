package com.mycompany.fivefieldkono.ui;

import com.mycompany.fivefieldkono.logica.*;
import com.mycompany.fivefieldkono.rede.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.AnimationTimer;

public class ControladorJogo implements RedeListener {

    // --- Lógica ---
    private final Jogo jogo = new Jogo();
    private final boolean isServidor;
    private final String ip;

    // --- Rede ---
    private Servidor servidor;
    private Cliente  cliente;

    // --- UI ---
    private Canvas canvas;
    private Label  labelTurno;
    private Label  labelTempo;
    private AnimationTimer cronometro;
    private long   tempoInicio;

    // --- Estado de seleção ---
    private int celulaSelLinha = -1;
    private int celulaSelColuna = -1;

    // --- Qual jogador sou eu ---
    private final Jogador meuJogador;

    // --- Dimensões do tabuleiro ---
    private static final int CELULA = 90;
    private static final int MARGEM = 30;

    public ControladorJogo(boolean isServidor, String ip) {
        this.isServidor  = isServidor;
        this.ip          = ip;
        this.meuJogador  = isServidor ? Jogador.JOGADOR1 : Jogador.JOGADOR2;
    }

    public Scene criarCena() {
        // --- Canvas do tabuleiro ---
        int tamanhoCanvas = Tabuleiro.TAMANHO * CELULA + 2 * MARGEM;
        canvas = new Canvas(tamanhoCanvas, tamanhoCanvas);
        canvas.setOnMouseClicked(e -> tratarClique(e.getX(), e.getY()));

        // --- Painel de estado ---
        labelTurno = new Label("A ligar...");
        labelTurno.setFont(Font.font(16));
        labelTurno.setTextFill(Color.WHITE);

        labelTempo = new Label("00:00");
        labelTempo.setFont(Font.font(16));
        labelTempo.setTextFill(Color.LIGHTGRAY);

        HBox painelEstado = new HBox(30, labelTurno, labelTempo);
        painelEstado.setAlignment(Pos.CENTER);
        painelEstado.setPadding(new Insets(10));

        // --- Painel de controlo ---
        Button btnRetomar  = new Button("Retomar");
        Button btnRegras   = new Button("Consultar Regras");
        Button btnDesistir = new Button("Desistir");

        btnRetomar.setOnAction(e -> canvas.requestFocus());

        btnRegras.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Regras");
            alert.setHeaderText("Five Field Kono — Movimentos");
            alert.setContentText(
                "• Movimento ORTOGONAL (↑↓←→): sempre permitido para casa vazia adjacente.\n\n" +
                "• Movimento DIAGONAL: só permitido em interseções onde (linha + coluna) é PAR.\n\n" +
                "• Objetivo: levar as tuas 10 peças para as 2 linhas iniciais do adversário."
            );
            alert.showAndWait();
        });

        btnDesistir.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Tens a certeza que queres desistir?",
                ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    enviarMensagem("DESISTIR");
                    jogo.desistir(meuJogador);
                    mostrarVencedor();
                }
            });
        });

        String estiloBotao = "-fx-background-color:#0f3460; -fx-text-fill:white; -fx-font-size:13;";
        btnRetomar.setStyle(estiloBotao);
        btnRegras.setStyle(estiloBotao);
        btnDesistir.setStyle("-fx-background-color:#e94560; -fx-text-fill:white; -fx-font-size:13;");

        HBox painelControlo = new HBox(15, btnRetomar, btnRegras, btnDesistir);
        painelControlo.setAlignment(Pos.CENTER);
        painelControlo.setPadding(new Insets(10));

        // --- Layout principal ---
        VBox root = new VBox(painelEstado, canvas, painelControlo);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(Color.web("#1a1a2e"), null, null)));

        desenharTabuleiro();
        ligarRede();

        return new Scene(root);
    }

    // ---------------------------------------------------------------
    // REDE
    // ---------------------------------------------------------------

    private void ligarRede() {
        if (isServidor) {
            servidor = new Servidor(this);
            new Thread(servidor).start();
        } else {
            cliente = new Cliente(ip, Servidor.PORTA, this);
            new Thread(cliente).start();
        }
    }

    private void enviarMensagem(String msg) {
        if (isServidor) { if (servidor != null) servidor.enviar(msg); }
        else            { if (cliente  != null) cliente.enviar(msg);  }
    }

    @Override
    public void onConectado(String mensagem) {
        Platform.runLater(() -> {
            labelTurno.setText(mensagem);
            // Só arranca o jogo quando os dois estão ligados
            if (mensagem.contains("ligado") || mensagem.contains("Ligado")) {
                iniciarCronometro();
                atualizarTurno();
                desenharTabuleiro();
            }
        });
    }

    @Override
    public void onMensagem(String msg) {
        Platform.runLater(() -> interpretarMensagem(msg));
    }

    @Override
    public void onErro(String erro) {
        Platform.runLater(() -> labelTurno.setText("Erro: " + erro));
    }

    private void interpretarMensagem(String msg) {
        if (msg.startsWith("MOVE:")) {
            // Formato: MOVE:lo,co,ld,cd
            String[] partes = msg.substring(5).split(",");
            int lo = Integer.parseInt(partes[0]);
            int co = Integer.parseInt(partes[1]);
            int ld = Integer.parseInt(partes[2]);
            int cd = Integer.parseInt(partes[3]);
            jogo.jogar(lo, co, ld, cd);
            desenharTabuleiro();
            atualizarTurno();
            if (jogo.isTerminado()) mostrarVencedor();
        } else if (msg.equals("DESISTIR")) {
            jogo.desistir(meuJogador.adversario());
            mostrarVencedor();
        }
    }

    // ---------------------------------------------------------------
    // INTERAÇÃO COM O TABULEIRO
    // ---------------------------------------------------------------

    private void tratarClique(double x, double y) {
        if (jogo.isTerminado()) return;
        if (jogo.getTurno() != meuJogador) return; // não é a minha vez

        int col = (int) ((x - MARGEM) / CELULA);
        int lin = (int) ((y - MARGEM) / CELULA);

        if (col < 0 || col >= Tabuleiro.TAMANHO) return;
        if (lin < 0 || lin >= Tabuleiro.TAMANHO) return;

        Celula celula = jogo.getTabuleiro().getCelula(lin, col);

        if (celulaSelLinha == -1) {
            // Primeira seleção — tem de ser uma peça minha
            if (!celula.estaVazia() && celula.getPeca().getDono() == meuJogador) {
                celulaSelLinha  = lin;
                celulaSelColuna = col;
                desenharTabuleiro(); // redesenha com destaque
            }
        } else {
            // Segunda seleção — tentar mover
            boolean moveu = jogo.jogar(celulaSelLinha, celulaSelColuna, lin, col);
            if (moveu) {
                enviarMensagem("MOVE:" + celulaSelLinha + "," + celulaSelColuna + "," + lin + "," + col);
                celulaSelLinha  = -1;
                celulaSelColuna = -1;
                desenharTabuleiro();
                atualizarTurno();
                if (jogo.isTerminado()) mostrarVencedor();
            } else {
                // Movimento inválido — muda seleção se clicar outra peça minha
                if (!celula.estaVazia() && celula.getPeca().getDono() == meuJogador) {
                    celulaSelLinha  = lin;
                    celulaSelColuna = col;
                    desenharTabuleiro();
                } else {
                    celulaSelLinha  = -1;
                    celulaSelColuna = -1;
                    desenharTabuleiro();
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // DESENHO
    // ---------------------------------------------------------------

    private void desenharTabuleiro() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int tam = Tabuleiro.TAMANHO;

        // Fundo
        gc.setFill(Color.web("#16213e"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int l = 0; l < tam; l++) {
            for (int c = 0; c < tam; c++) {
                double x = MARGEM + c * CELULA;
                double y = MARGEM + l * CELULA;

                // Zona de meta J1 (linhas 3-4) — tom avermelhado subtil
                // Zona de meta J2 (linhas 0-1) — tom azulado subtil
                if (l >= 3) {
                    gc.setFill(Color.web("#2d1b2e"));
                } else if (l <= 1) {
                    gc.setFill(Color.web("#1b2d2e"));
                } else {
                    gc.setFill(Color.web("#16213e"));
                }
                gc.fillRect(x, y, CELULA, CELULA);

                // Célula selecionada — realce amarelo
                if (l == celulaSelLinha && c == celulaSelColuna) {
                    gc.setFill(Color.web("#f5a623", 0.4));
                    gc.fillRect(x, y, CELULA, CELULA);
                }

                // Destinos possíveis da peça selecionada — realce verde
                if (celulaSelLinha != -1) {
                    if (jogo.getTabuleiro().validarMovimento(celulaSelLinha, celulaSelColuna, l, c)) {
                        gc.setFill(Color.web("#00ff88", 0.25));
                        gc.fillRect(x, y, CELULA, CELULA);
                    }
                }

                // Borda da célula
                gc.setStroke(Color.web("#0f3460"));
                gc.setLineWidth(2);
                gc.strokeRect(x, y, CELULA, CELULA);

                // Ponto diagonal (interseção par)
                if ((l + c) % 2 == 0) {
                    gc.setFill(Color.web("#ffffff", 0.15));
                    gc.fillOval(x + CELULA/2.0 - 4, y + CELULA/2.0 - 4, 8, 8);
                }

                // Peça
                Celula celula = jogo.getTabuleiro().getCelula(l, c);
                if (!celula.estaVazia()) {
                    Jogador dono = celula.getPeca().getDono();
                    double px = x + CELULA / 2.0;
                    double py = y + CELULA / 2.0;
                    double r  = CELULA * 0.35;

                    // Sombra
                    gc.setFill(Color.web("#000000", 0.4));
                    gc.fillOval(px - r + 3, py - r + 3, r * 2, r * 2);

                    // Peça
                    gc.setFill(dono == Jogador.JOGADOR1
                        ? Color.web("#e94560")
                        : Color.web("#00b4d8"));
                    gc.fillOval(px - r, py - r, r * 2, r * 2);

                    // Brilho
                    gc.setFill(Color.web("#ffffff", 0.3));
                    gc.fillOval(px - r * 0.5, py - r * 0.7, r * 0.6, r * 0.4);
                }
            }
        }
    }

    private void atualizarTurno() {
        if (jogo.isTerminado()) return;
        boolean minhavez = jogo.getTurno() == meuJogador;
        labelTurno.setText(minhavez ? "🟢 A TUA VEZ" : "⏳ Vez do adversário");
        labelTurno.setTextFill(minhavez ? Color.LIGHTGREEN : Color.LIGHTGRAY);
    }

    // ---------------------------------------------------------------
    // CRONÓMETRO  (usa AnimationTimer conforme os slides de Gráficos)
    // ---------------------------------------------------------------

    private void iniciarCronometro() {
        tempoInicio = System.currentTimeMillis();
        cronometro  = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long decorrido = (System.currentTimeMillis() - tempoInicio) / 1000;
                long min = decorrido / 60;
                long seg = decorrido % 60;
                labelTempo.setText(String.format("%02d:%02d", min, seg));
            }
        };
        cronometro.start();
    }

    // ---------------------------------------------------------------
    // VITÓRIA
    // ---------------------------------------------------------------

    private void mostrarVencedor() {
        if (cronometro != null) cronometro.stop();
        String msg = jogo.getVencedor() == meuJogador
            ? "🏆 Ganhaste! Parabéns!"
            : "💀 Perdeste. O adversário venceu.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fim do Jogo");
        alert.setHeaderText(msg);
        alert.showAndWait();
    }
}