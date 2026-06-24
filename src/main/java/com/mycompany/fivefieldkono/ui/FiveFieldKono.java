
package com.mycompany.fivefieldkono.ui;

import com.mycompany.fivefieldkono.ui.view.GestorSom;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principal e ponto de entrada da aplicação Five Field Kono.
 * <p>
 * Limita-se a arrancar o JavaFX e a delegar a construção do menu no
 * {@link MenuInicial}. O regresso ao menu a partir de uma partida também
 * passa por aqui, mantendo uma única janela principal.
 *
 * @author Eduardo
 * @version 1.0
 */
public class FiveFieldKono extends Application {

    /** Janela principal da aplicação. */
    private Stage stagePrincipal;

    /** Menu inicial reutilizável. */
    private MenuInicial menu;

    /**
     * Arranca a aplicação e mostra o menu inicial.
     *
     * @param stage a janela principal fornecida pelo JavaFX
     */
    @Override
    public void start(Stage stage) {
        GestorSom.carregar();
        this.stagePrincipal = stage;
        this.menu = new MenuInicial(this, stage);
        stage.setScene(menu.criarCena());
        stage.setTitle("Five Field Kono");
        stage.show();
    }

    /**
     * Regressa ao menu inicial (chamado quando uma partida termina).
     */
    public void voltarAoMenu() {
        stagePrincipal.setScene(menu.criarCena());
        stagePrincipal.setTitle("Five Field Kono");
    }

    /**
     * Devolve a janela principal.
     *
     * @return a {@link Stage} principal
     */
    public Stage getStage() {
        return stagePrincipal;
    }

    /**
     * Ponto de arranque do programa.
     *
     * @param args argumentos de linha de comandos (não utilizados)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
