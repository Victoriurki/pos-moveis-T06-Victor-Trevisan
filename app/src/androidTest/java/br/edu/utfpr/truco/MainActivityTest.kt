package br.edu.utfpr.truco

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes de instrumentacao (espresso) da tela principal:
 * pontuar, vitoria, navegar ao historico e informar nomes.
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
    fun navegarAoHistoricoMostraContagemInicial() {
        onView(withId(R.id.btnHistorico)).perform(click())

        onView(withId(R.id.tvHistoricoJogador1)).check(matches(isDisplayed()))
        onView(withId(R.id.tvHistoricoJogador2)).check(matches(isDisplayed()))
    }

    @Test
    fun informarNomesEConfirmarAtualizaTelaPrincipal() {
        onView(withId(R.id.btnNomes)).perform(click())

        onView(withId(R.id.etNomeJogador1)).perform(clearText(), typeText("Time Alfa"))
        onView(withId(R.id.etNomeJogador2)).perform(clearText(), typeText("Time Beta"))
        onView(withId(R.id.btnConfirmar)).perform(click())

        onView(withId(R.id.tvNomeJogador1)).check(matches(withText("Time Alfa")))
        onView(withId(R.id.tvNomeJogador2)).check(matches(withText("Time Beta")))
    }
}
