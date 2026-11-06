# SDD — SDD-av-android-2026-2 — Marcador de Truco (Android nativo, Kotlin + Views XML)

Trabalho final da disciplina de Android Básico (UTFPR — Campus Pato Branco).
Autor: Victor Trevisan (usuário solicitante e dono do repositório).
Nível de rito: L2 (sem Jira). Fonte primária: enunciado do PDF da avaliação (transcrito
verbatim no contexto do ciclo) + requisitos explícitos do usuário.

## 1. Descoberta (resumo)

Especificado no enunciado: app de 3 telas — principal (pontuação corrente + botões de
1/3/6/9/12 pontos para cada jogador), histórico (quantidade de partidas ganhas por
jogador) e telas auxiliares de zerar e informar nomes (2 EditText + botão Confirmar).
Condições de vitória: pontuação ≥ 12 → jogador vence a partida, mensagem informativa
(Toast/AlertDialog), zera pontuações e incrementa contador de partidas do vencedor.

Requisitos adicionais explícitos: (a) Kotlin sem Compose; (b) README descritivo do
ambiente até emulador; (c) justificativa dos componentes visuais básicos
(entrada/processamento/saída); (d) escolha justificada LinearLayout vs ConstraintLayout;
(e) i18n pt-BR/en/es; (f) JUnit4 sobre lógica pura testável sem Android; (g) roteamento
por Intent explícito manual, proibido Navigation Component; (h) Material com ícones
vector drawables próprios (ic_history, ic_restart, ic_person, 24dp); (i) testes de
instrumentação com espresso.

## 2. Decisões

- **D1 — Layout principal: LinearLayout.** Motivo: o modelo de tela do PDF tem
  estrutura de empilhamento vertical + linhas uniformes de 5 botões de mesmo peso —
  exatamente o caso de uso "grid uniforme fixo" que o LinearLayout resolve com
  `orientation` + `layout_weight`, sem custo cognitivo. Alternativa rejeitada:
  **ConstraintLayout** — cada um dos 13 botões precisaria de 2+ constraints manuais,
  verboso e sem benefício aqui; nada na tela pede posicionamento relativo flexível,
  telas aninhadas por hierarquia ou animação de transição.

- **D2 — Estado em memória via singleton `object Placar` (Kotlin puro).** Armazena
  nomes, pontuações, partidas ganhas e encapsula `adicionarPontos(jogador, pontos)`,
  detecção de vitória (≥ 12) com flag de retorno, `resetPontuacoes()` (zera apenas as
  pontuações, sem tocar nomes/contadores), `zerarTudo()` e `definirNomes()`. Motivo: o enunciado
  determina "armazenada em memória, por meio de variáveis do tipo inteiro"; o singleton
  permite que as 3 Activities compartilhem o mesmo estado sem `Parcelable`/`Bundle`, e
  torna a lógica testável com JUnit4 puro (sem Robolectric). Alternativa rejeitada:
  ViewModel — arquitetura adicional fora do escopo da disciplina e desnecessária para
  3 telas não rotativas; SharedPreferences contradiz "em memória" do enunciado.

- **D3 — Roteamento por Intent explícito manual.** `startActivity(Intent(this,
  HistoricoActivity::class.java))` nos 2 botões de navegação; retorno da tela de nomes
  via `finish()` e re-render em `onResume()` da principal. Alternativa rejeitada:
  Navigation Component — proibido explicitamente pelo usuário (requisito g).

- **D4 — Mensagem de vitória via AlertDialog.** Motivo: evento terminal da partida, o
  usuário precisa confirmar a leitura; AlertDialog da biblioteca Material (tema
  Material3) é modal e mais visível que Toast. Alternativa rejeitada: Toast —
  efêmero (~2 s) e não modal, fácil de perder num evento de vitória.

- **D5 — Botões de navegação com ícones: vetores locais próprios.** `ic_history`,
  `ic_restart`, `ic_person` (24dp, paths base do Material Design) em `res/drawable`,
  apontados por `app:icon` de `MaterialButton` (biblioteca
  `com.google.android.material:material:1.12.0`). Alternativa rejeitada: `ImageButton`
  com PNG — escala mal e foge do requisito (h).

- **D6 — i18n com recursos padrão: `res/values/` = pt-BR, mais `values-en` e `values-es`.**
  Motivo: é idiomático Android — o seletor de recursos resolve `values-<locale>` pelo
  idioma do sistema e cai no `values/` default como fallback; basta 1 `strings.xml` por
  idioma, sem código. Alternativa rejeitada: strings no código — quebra o requisito (e)
  e o lint `UseStringResources`.

- **D7 — Componentes de entrada/processamento/saída mapeados.** Entrada: EditText
  (nomes), Button/MaterialButton (comandos); processamento: `Placar` (singleton) +
  listeners de clique; saída: TextView (nomes/pontuação/histórico), AlertDialog
  (vitória), Toast (confirmação de zerar). Tabela completa no README §5.

- **D8 — Tema Theme.Material3.DayNight.NoActionBar.** Motivo: dá escura/claro de
  graça e vem da biblioteca Material requisitada. Alternativa rejeitada: AppCompat
  básico — perderia os estilos dos componentes Material3.

- **D9 — Nomes padrão i18n-safe.** O `Placar` guarda nome customizado OU string
  vazia; nunca conhece "Jogador 1"/"Jogador 2" hardcoded. A UI (MainActivity e
  HistoricoActivity, regra `nomeExibivel`) exibe `R.string.jogador_1`/`R.string.jogador_2`
  quando o nome está vazio — assim a UI respeita o i18n para os padrões
  (revisão, achado 2). `zerarTudo()` restaura nomes para vazio (= padrão localizado). A
  tela de nomes pré-preenche os EditText com o valor cru atual.
  `definirNomes()` normaliza: trim e, se em branco, grava "" (nome padrão localizado
  é mantido na tela).

- **D10 — Vitória reseta apenas as pontuações, APÓS a mensagem.** Quando alguém atinge
  ≥ 12: `adicionarPontos` incrementa `partidasGanhas[jogador]` e devolve true — NÃO zera;
  a MainActivity exibe o `AlertDialog` de vitória com o placar ainda refletindo a
  partida vencida e, no dismiss (botão OK), chama `Placar.resetPontuacoes()` (só
  pontuações; nomes + contadores preservados, requisito Q2: "zerando a pontuação dos
  jogadores após a mensagem").

- **D11 — Toolchain fixada.** AGP 8.7.3, Gradle wrapper 8.10.2, Kotlin 2.0.21,
  compileSdk/targetSdk 35, minSdk 24, JVM 17, package/appId `br.edu.utfpr.truco`.
  Motivo: valores pedidos nas diretrizes técnicas do trabalho; JDK 21 da máquina é
  compatível (AGP 8.7.3 exige JDK 17+).

## 3. Testes

- **Unitários (JUnit4, sem Android):** `app/src/test/.../PlacarTest.kt` sobre
  `Placar` (18 testes) — estado inicial zerado (pontuações, contadores e nomes vazios
  = padrão localizado), soma e acumulação por jogador, independência entre jogadores,
  vitória exata em 12 e acima de 12, `adicionarPontos` NÃO zera na vitória (placar
  mantém 12 durante a mensagem) e `resetPontuacoes()` zera depois (preservando
  contadores e nomes), `definirNomes` (inclusive nome em branco → vazio = padrão
  localizado) e `zerarTudo`. Relatório: 18/18 verdes.
- **Instrumentação (espresso):** `app/src/androidTest/.../MainActivityTest.kt`
  (6 testes) — clicar +1 e verificar TextView; clicar +1/+3/+6 do jogador 2 e
  verificar 10 acumulado; fluxo completo de vitória: +12 do zero → AlertDialog exibido
  (`withText(containsString("12")).inRoot(isDialog())`) + pontuação ainda 12 (estado do
  `Placar`, mesmo processo) durante a mensagem → OK → pontuação zerada → Histórico
  mostra `: 1`; navegar a Histórico e verificar conteúdo `: 0` em ambas as linhas;
  informar nome, confirmar e verificar atualização na principal. Relatório: 6/6 verdes.

## 4. Ret de revisão (revisão adversarial — fechamento dos 6 achados)

| Achado | Correção | Teste que cobre |
|---|---|---|
| 1 (Médio): SDD §3 prometia teste espresso do fluxo de vitória inexistente | Novo teste de instrumentação `vitoriaComMais12ExibeDialogoZeraPontuacaoAposMensagemEIncrementaHistorico`: +12 do zero → AlertDialog exibido (`withText(containsString("12")).inRoot(isDialog())`) → OK (dismiss) → TextView zerado → Histórico mostra `: 1`. §3 emendado para conferir 1:1 com a suíte final (18 unit + 6 instrumentados). | `MainActivityTest.vitoriaComMais12ExibeDialogoZeraPontuacaoAposMensagemEIncrementaHistorico` (6/6 verdes no emulador) |
| 2 (Médio): nomes padrão "Jogador 1"/"Jogador 2" hardcoded no Placar quebram i18n | `Placar` guarda nome customizado ou string vazia; `nomePadrao` removido. MainActivity/HistoricoActivity resolvem o padrão via `R.string.jogador_1`/`R.string.jogador_2` (função `nomeExibivel`); `zerarTudo` restaura nomes vazios. README §7 descreve a regra. | `PlacarTest.nomesIniciaisSaoVaziosIndicandoPadraoLocalizado`; teste de tela `navegarAoHistoricoMostraContagemInicial` usa nome localizado |
| 3 (Médio): Q2 exige zerar pontuação APÓS a mensagem; `adicionarPontos` zerava antes | `adicionarPontos` marca a vitória (incrementa contador, devolve true) e NÃO zera; novo `Placar.resetPontuacoes()` zera só pontuações; MainActivity chama `resetPontuacoes()` no dismiss do AlertDialog. Testes unitários atualizados à nova sequência. | `PlacarTest.vitoriaZeraAsPontuacoesDosDoisJogadores` (nova sequência) + `PlacarTest.resetPontuacoesPreservaContadoresENomes` + estado verificado no teste instrumentado do achado 1 (pontuação 12 durante o diálogo) |
| 4 (Baixo): string `partidas_ganhas` morta; formato do histórico sem rótulo | Formato único por idioma com rótulo: pt `%1$s: %2$d partidas ganhas`, en `%1$s: %2$d games won`, es `%1$s: %2$d partidas ganadas`; `partidas_ganhas` removida dos 3 `strings.xml` (a string `nome_padrao/2` continua — hint do EditText de nomes). | Testes instrumentados usam `containsString(": 0")`/`: 1`, válidos nos 3 idiomas |
| 5 (Baixo): `navegarAoHistoricoMostraContagemInicial` vazio (só `isDisplayed`) | Verificação de conteúdo com `withText(containsString(": 0"))` nas duas linhas, compatível com o formato do achado 4. | `MainActivityTest.navegarAoHistoricoMostraContagemInicial` |
| 6 (Baixo): Confirmar sem validação gravava string vazia como nome | Regra (mais simples, documentada no README §4): `definirNomes` faz trim e, se em branco, grava "" — o `Placar`/UI tratam vazio como "usar padrão localizado" (achado 2), sem Toast. | `PlacarTest.definirNomesComBrancoMantemPadraoLocalizado` |

Observação de execução (achado 1/3): a janela da Activity atrás de um dialogo modal
nao tem foco e nao e alcancavel pelo `onView` espresso nesta API
(`RootViewWithoutFocusException`); por isso a verificacao do placar "12 atras do
dialogo" e feita por estado no singleton `Placar` (mesmo processo
instrumentado), e a checagem visual e o `AlertDialog` exibido via root matcher
`isDialog()`.
