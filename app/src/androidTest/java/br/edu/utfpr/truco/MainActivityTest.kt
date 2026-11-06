package br.edu.utfpr.truco

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes de instrumentacao (espresso) da tela principal:
 * pontuar, vitoria (fluxo completo do achado 1: dialogo, reset apos a
 * mensagem e contador no historico), navegar ao historico e informar nomes.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Before
    fun setUp() {
        Placar.zerarTudo()
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun clicarMais1Jogador1AtualizaTextViewPontuacao() {
        onView(withId(R.id.btnJogador1Mais1)).perform(click())

        onView(withId(R.id.tvPontuacaoJogador1)).check(matches(withText("1")))
    }

    @Test
    fun clicarMais1Mais3Mais6Jogador2AcumulaPontuacao() {
        onView(withId(R.id.btnJogador2Mais1)).perform(click())
        onView(withId(R.id.btnJogador2Mais3)).perform(click())
        onView(withId(R.id.btnJogador2Mais6)).perform(click())

        onView(withId(R.id.tvPontuacaoJogador2)).check(matches(withText("10")))
    }

    @Test
    fun botaoZerarRestauraPontuacaoInicial() {
        onView(withId(R.id.btnJogador1Mais6)).perform(click())
        onView(withId(R.id.btnZerar)).perform(click())

        onView(withId(R.id.tvPontuacaoJogador1)).check(matches(withText("0")))
    }

    @Test
    fun vitoriaComMais12ExibeDialogoZeraPontuacaoAposMensagemEIncrementaHistorico() {
        // Fluxo de vitoria do zero: +12 do Jogador 1 deve abrir o dialogo.
        onView(withId(R.id.btnJogador1Mais12)).perform(click())

        // Mensagem de vitoria (AlertDialog Material) exibida: o texto da
        // mensagem, no root de dialogo, esta visivel (contem o limite de 12).
        onView(withText(containsString("12")))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // Q2: DURANTE a mensagem a pontuacao NAO foi zerada — o placar atras
        // do dialogo ainda mostra a partida vencida (12). A verificacao e feita
        // por estado (mesmo processo, singleton Placar): a janela da Activity
        // atras de um dialogo modal nao tem foco e nao e alcancavel pelo
        // espresso nesta API (RootViewWithoutFocusException).
        org.junit.Assert.assertEquals(12, Placar.pontuacoes[0])
        org.junit.Assert.assertEquals(1, Placar.partidasGanhas[0])

        // Fechamento da mensagem (botao OK) - a partir dai a pontuacao zera.
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.tvPontuacaoJogador1)).check(matches(withText("0")))

        // O contador de partidas ganhas incrementou (linha ": 1" no historico,
        // compativel com o formato "%1$s: %2$d partidas ganhas" dos 3 idiomas).
        onView(withId(R.id.btnHistorico)).perform(click())
        onView(withId(R.id.tvHistoricoJogador1)).check(matches(withText(containsString(": 1"))))
    }

    @Test
    fun navegarAoHistoricoMostraContagemInicial() {
        onView(withId(R.id.btnHistorico)).perform(click())

        // Conteudo: zero partidas ganhas para os 2 jogadores no formato localizado.
        onView(withId(R.id.tvHistoricoJogador1)).check(matches(withText(containsString(": 0"))))
        onView(withId(R.id.tvHistoricoJogador2)).check(matches(withText(containsString(": 0"))))
    }

    @Test
    fun informarNomesEConfirmarAtualizaTelaPrincipal() {
        onView(withId(R.id.btnNomes)).perform(click())

        // replaceText (sem IME): o teclado virtual deste emulador engole
        // teclas de typeText — a acao direta de texto e deterministica.
        onView(withId(R.id.etNomeJogador1)).perform(clearText(), replaceText("Time Alfa"))
        onView(withId(R.id.etNomeJogador2)).perform(clearText(), replaceText("Time Beta"))
        closeSoftKeyboard()
        onView(withId(R.id.btnConfirmar)).perform(click())

        onView(withId(R.id.tvNomeJogador1)).check(matches(withText("Time Alfa")))
        onView(withId(R.id.tvNomeJogador2)).check(matches(withText("Time Beta")))
    }
}
