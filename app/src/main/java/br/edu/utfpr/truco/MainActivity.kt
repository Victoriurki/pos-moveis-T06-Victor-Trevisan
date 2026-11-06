package br.edu.utfpr.truco

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Tela principal: mostra a pontuacao corrente de cada jogador, recebe os
 * botoes de 1/3/6/9/12 pontos e navega para o historico e para a tela de
 * nomes; tambem tem o botao de zerar pontuacoes/historico/nomes.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Processamento: cada botao de pontos soma o valor no Placar.
        val botoesJogador1 = listOf(
            R.id.btnJogador1Mais1 to 1,
            R.id.btnJogador1Mais3 to 3,
            R.id.btnJogador1Mais6 to 6,
            R.id.btnJogador1Mais9 to 9,
            R.id.btnJogador1Mais12 to 12,
        )
        botoesJogador1.forEach { (id, pontos) ->
            findViewById<Button>(id).setOnClickListener {
                adicionarPontos(0, pontos)
            }
        }
        val botoesJogador2 = listOf(
            R.id.btnJogador2Mais1 to 1,
            R.id.btnJogador2Mais3 to 3,
            R.id.btnJogador2Mais6 to 6,
            R.id.btnJogador2Mais9 to 9,
            R.id.btnJogador2Mais12 to 12,
        )
        botoesJogador2.forEach { (id, pontos) ->
            findViewById<Button>(id).setOnClickListener {
                adicionarPontos(1, pontos)
            }
        }

        // Roteamento: Intent explicito manual para cada tela.
        findViewById<Button>(R.id.btnHistorico).setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
        findViewById<Button>(R.id.btnNomes).setOnClickListener {
            startActivity(Intent(this, NomesActivity::class.java))
        }

        // Zerar historico: valores iniciais + mensagem via Toast.
        findViewById<Button>(R.id.btnZerar).setOnClickListener {
            Placar.zerarTudo()
            atualizarPontuacaoETela()
            Toast.makeText(this, R.string.msg_zerado, Toast.LENGTH_SHORT).show()
        }

        atualizarPontuacaoETela()
    }

    override fun onResume() {
        super.onResume()
        // Ao voltar da tela de nomes, reflete o estado no Placar.
        atualizarPontuacaoETela()
    }

    private fun adicionarPontos(jogador: Int, pontos: Int) {
        val venceu = Placar.adicionarPontos(jogador, pontos)
        atualizarPontuacaoETela()
        if (venceu) {
            mensagemDeVitoria(jogador)
        }
    }

    private fun atualizarPontuacaoETela() {
        findViewById<TextView>(R.id.tvNomeJogador1).text = nomeExibivel(0)
        findViewById<TextView>(R.id.tvNomeJogador2).text = nomeExibivel(1)
        findViewById<TextView>(R.id.tvPontuacaoJogador1).text = Placar.pontuacoes[0].toString()
        findViewById<TextView>(R.id.tvPontuacaoJogador2).text = Placar.pontuacoes[1].toString()
    }

    /**
     * Nome apresentado na tela: o nome customizado informado na tela de nomes
     * ou, se vazio, o nome padrao localizado (R.string.jogador_1/jogador_2).
     */
    private fun nomeExibivel(jogador: Int): String {
        val nome = Placar.nomes[jogador]
        return if (nome.isBlank()) {
            getString(if (jogador == 0) R.string.jogador_1 else R.string.jogador_2)
        } else {
            nome
        }
    }

    private fun mensagemDeVitoria(jogador: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.app_name)
            .setMessage(getString(R.string.msg_vitoria, nomeExibivel(jogador), Placar.LIMITE_VITORIA))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // Q2: a pontuacao e zerada APÓS a mensagem — por isso o
                // placar atras do dialogo ainda mostra a partida vencida.
                Placar.resetPontuacoes()
                atualizarPontuacaoETela()
            }
            .show()
    }
}
