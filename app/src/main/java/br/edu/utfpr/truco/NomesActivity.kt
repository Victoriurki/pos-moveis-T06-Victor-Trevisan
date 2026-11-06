package br.edu.utfpr.truco

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * Tela de informacao de nomes: dois EditText para o nome dos jogadores.
 * Ao confirmar, grava no Placar e volta para a tela principal.
 */
class NomesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nomes)

        val etNomeJogador1 = findViewById<EditText>(R.id.etNomeJogador1)
        val etNomeJogador2 = findViewById<EditText>(R.id.etNomeJogador2)
        etNomeJogador1.setText(Placar.nomes[0])
        etNomeJogador2.setText(Placar.nomes[1])

        findViewById<MaterialButton>(R.id.btnConfirmar).setOnClickListener {
            Placar.definirNomes(
                nomeJogador1 = etNomeJogador1.text.toString().trim(),
                nomeJogador2 = etNomeJogador2.text.toString().trim(),
            )
            finish()
        }
    }
}
