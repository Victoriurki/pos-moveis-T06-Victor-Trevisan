# Marcador de Truco

**Autor: Victor Trevisan** — Projeto da avaliação final da disciplina de Android Básico (UTFPR — Campus Pato Branco): um marcador para o jogo de baralho Truco, em **Kotlin nativo com Views (XML)**, sem Jetpack Compose.

## 1. Sobre

O aplicativo é composto de três telas:

- **Tela principal** — mostra a pontuação corrente de cada jogador e informa se na jogada o jogador ganhou 1, 3, 6, 9 ou 12 pontos (5 botões por jogador). Conforme o enunciado, a tela é formada por **4 componentes TextView e 13 botões** (10 de pontos + 3 de navegação).
- **Histórico de Jogadas** — informa o nome de cada jogador e a quantidade de partidas que ele ganhou desde o último reinício.
- **Informe os Nomes** — dois campos `EditText` para digitar o nome das equipes; ao confirmar, os nomes passam a aparecer na tela principal.

Regras do enunciado implementadas:

- Ao iniciar, todos os jogadores começam com 0 pontos.
- Cada clique num botão de pontos acresce 1, 3, 6, 9 ou 12 na pontuação do jogador correspondente.
- Quando a pontuação é igual ou superior a 12, o jogador ganha a partida: é exibida uma mensagem informativa (`AlertDialog`) com o placar ainda refletindo a partida vencida e, ao fechá-la, a pontuação dos jogadores é zerada e o contador de partidas ganhas do vencedor é incrementado.
- O botão **Zerar Histórico** restaura pontuações, contadores de partidas e nomes aos valores iniciais e exibe mensagem via `Toast`.

A pontuação é armazenada **em memória, por meio de variáveis do tipo inteiro** (`IntArray` no singleton Kotlin `Placar`), exatamente como o enunciado permite.

## 2. Ambiente (criação pelo Android Studio)

Passos completos para replicar o ambiente de desenvolvimento em um macOS:

1. **Instale o Android Studio** (Ladybug 2024.2.1 ou superior, que traz o AGP 8.7.x): baixe em https://developer.android.com/studio e siga o assistente de instalação. Este projeto foi criado com o Android Studio como IDE e o assistente *"New Project → Empty Views Activity"* (o assistente de Empty Activity **Compose** não serve — é preciso escolher **Empty Views Activity** para Views XML).
2. **JDK**: o Android Studio embut um JDK, mas para builds externos (linha de comando) instale o **JDK 21** (ex.: Amazon Corretto 21 via Homebrew: `brew install --cask corretto21`). O projeto está configurado com `jvmTarget = 17` e compila com JDK 17 ou 21 (AGP 8.7.3 exige no mínimo JDK 17).
3. **SDK 35**: no Android Studio abra `Settings → Languages & Frameworks → Android SDK` e instale, na aba *SDK Platforms*, **Android 15.0 (API 35)**; na aba *SDK Tools* confirme **Android SDK Build-Tools 35.0.0**, *Android Emulator* e *Android SDK Platform-Tools*.
4. **Abra o projeto**: `File → Open` e selecione a pasta raiz (`av-android-basico`, a que contém `settings.gradle.kts`). O IntelliJ/Gradle executa o **Gradle Sync** automaticamente na primeira vez — aguarde a barra de progresso "Sync Project with Gradle Files" terminar (ícone do elefante verde sem alertas vermelhos nas abas *Build Output* e *Problems*). Se o sync não rodar sozinho, acione o ícone 🐘 *"Sync Project with Gradle Files"* na barra de ferramentas.
5. **Emulador**: `Tools → Device Manager → Create Virtual Device`, escolha um aparelho (ex.: Pixel 6) e uma imagem de sistema **API 35** com Google Play, finalize o assistente e inicie o AVD. Nesta máquina o AVD usado chama-se `flutter_emulator_2` (recriável com qualquer nome).
6. Requisitos mínimos do projeto: `minSdk 24`, `compileSdk 35`, `targetSdk 35` — definidos em `app/build.gradle.kts`, junto com AGP 8.7.3, Gradle wrapper 8.10.2 e Kotlin 2.0.21.

## 3. Como executar

Pelo Android Studio: abra o projeto (passo 4 do ambiente), selecione uma configuração `app` e um device/AVD e clique em ▶ **Run 'app'**.

Pela linha de comando (a partir da raiz do projeto):

```bash
# construir o APK de debug
./gradlew assembleDebug
# APK gerado em: app/build/outputs/apk/debug/app-debug.apk

# instalar e abrir num emulador já iniciado
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n br.edu.utfpr.truco/.MainActivity
```

Para iniciar o emulador pela linha de comando (macOS, caminho padrão):

```bash
~/Library/Android/sdk/emulator/emulator -avd flutter_emulator_2 -no-snapshot-load &
# aguarde: adb shell getprop sys.boot_completed => 1
```

## 4. Telas e fluxo

| Tela | Arquivo de layout | Conteúdo |
|---|---|---|
| Principal | `app/src/main/res/layout/activity_main.xml` | 4 `TextView` (nome + pontuação de cada jogador) e 13 botões: `+1 +3 +6 +9 +12` (Jogador 1), `+1 +3 +6 +9 +12` (Jogador 2), `Hist. de Jogadas`, `Zerar Histórico`, `Inf. Nomes` |
| Histórico | `app/src/main/res/layout/activity_historico.xml` | duas linhas no formato localizado `Nome: N partidas ganhas` (ex.: `Jogador 1: 0 partidas ganhas`) |
| Nomes | `app/src/main/res/layout/activity_nomes.xml` | labels `Jogador 1:` / `Jogador 2:`, dois `EditText` e botão `Confirmar` |

Fluxo: a tela principal é a launcher; `Hist. de Jogadas` abre `HistoricoActivity`; `Inf. Nomes` abre `NomesActivity`, que recebe os nomes digitados, grava no `Placar` e encerra com `finish()`, levando de volta à tela principal (atualizada em `onResume()`). Ao vencer (≥ 12), a principal exibe um `AlertDialog` de vitória e **a pontuação é zerada no fechamento da mensagem** (o placar atras do diálogo mostra a partida vencida; `Placar.resetPontuacoes()` é chamado no botão OK, preservando nomes e contadores). `Zerar Histórico` restaura todo o estado em memória com confirmação via `Toast`.

**Regra de nomes em branco (tela `Inf. Nomes`):** o Confirmar sempre grava o que foi digitado após `trim()`; se o campo ficou **em branco, grava string vazia no `Placar` — que passa a exibir o nome padrão localizado** (`R.string.jogador_1`/`R.string.jogador_2`). Ou seja: deixar em branco equivale a "usar o padrão do idioma do dispositivo" (sem `Toast` adicional — a própria tela principal mostra o padrão resolvido).

## 5. Justificativa dos componentes visuais (entrada / processamento / saída)

Os componentes visuais básicos usados e seu papel no processamento da informação:

| Componente | Classe / API | Papel | Categoria |
|---|---|---|---|
| Campo de texto de nome | `EditText` (`activity_nomes.xml`) | captura o texto digitado pela pessoa usuária (nome das equipes) | **Entrada** |
| Botões de pontos e de navegação | `Button` / `MaterialButton` (`activity_main.xml`, `activity_nomes.xml`) | disparam as ações: somar 1/3/6/9/12, abrir telas, zerar, confirmar nomes | **Entrada** |
| Lógica do placar | singleton `object Placar` (`Placar.kt`) | processa os pontos, detecta vitória (≥ 12), incrementa partidas ganhas, reseta pontuações após a mensagem de vitória (`resetPontuacoes()`), zera estado e guarda os nomes (vazio = padrão localizado) | **Processamento** |
| Listeners de clique (`setOnClickListener`) | `MainActivity.kt`, `NomesActivity.kt` | conectam a entrada (botões) ao processamento (chamadas no `Placar`) | **Processamento** |
| Roteamento de telas | `Intent` explícito | define qual tela será apresentada | **Processamento** |
| Mostrador de nome e pontuação | `TextView` (`tvNomeJogador1`, `tvPontuacaoJogador1`, `tvNomeJogador2`, `tvPontuacaoJogador2`) | exibe a informação resultante do processamento na tela principal | **Saída** |
| Mostrador do histórico | `TextView` (`tvHistoricoJogador1/2`) | exibe quantas partidas cada jogador ganhou | **Saída** |
| Mensagem de vitória | `MaterialAlertDialogBuilder` | apresenta o resultado final da partida de forma modal | **Saída** |
| Mensagem de confirmação | `Toast` | confirma a operação de zerar sem bloquear a interface | **Saída** |

## 6. LinearLayout vs ConstraintLayout

Escolha do **LinearLayout** como layout principal (raiz vertical, com sub-`LinearLayout` horizontais para cada linha de botões):

- O modelo de tela da avaliação é um **empilhamento vertical simples** de linhas (4 TextViews em duas linhas, duas linhas de 5 botões de mesmo peso, três botões de navegação plenos). Isso é o caso de uso clássico do `LinearLayout`: `android:orientation="vertical"` no raiz e `android:orientation="horizontal"` + `android:layout_weight="1"` nas linhas produzem a **grade uniforme e fixa** de botões que o enunciado pede.
- Cada linha de pontos fica com os 5 botões exatamente do mesmo tamanho (`layout_width="0dp"` + `layout_weight="1"`), sem margens manuais de constraint, e o código XML fica legível e didático para a disciplina.
- A tela não requer posicionamento relativo flexível, hierarquias profundas variáveis, telas aninhadas (`ConstraintLayout` dentro de `ConstraintLayout`) nem animações de transição de posição — os cenários em que o `ConstraintLayout` compensaria.

Por que **ConstraintLayout foi descartado**: com 13 botões + 4 TextViews, cada um exigiria 2 a 4 constraints manuais (top/left/start etc.) para descrever o que uma linha flexível já expressa; o XML ficaria mais verboso, mais sujeito a erro e sem nenhum ganho funcional, já que a grade é fixa e uniforme em todas as densidades suportadas (o peso escala as colunas proporcionalmente).

## 7. Internacionalização

Todas as strings visíveis estão em arquivos de recurso, um por idioma:

- `app/src/main/res/values/strings.xml` — **padrão, em pt-BR**;
- `app/src/main/res/values-en/strings.xml` — inglês;
- `app/src/main/res/values-es/strings.xml` — espanhol.

**Mecanismo:** o sistema de recursos do Android resolve automaticamente `res/values-<idioma>/` pelo locale configurado no dispositivo (por exemplo, `pt-BR` → `en` usa `values-en`; `es`/`es-MX` usam `values-es`). O diretório `values/` (sem sufixo) é o *fallback* obrigatório: quando não existe tradução para o idioma do usuário, o recurso padrão é usado. Os layouts referem-se às strings por `@string/nome`, e o código usa `getString(R.string.msg_vitoria, ...)` — nenhum texto está "hardcoded" no código ou no XML de layout. Strings com valores dinâmicos usam placeholders de formatação (`%1$s`, `%2$d`), que se reorganizam adequadamente na tradução.

**Nomes padrão também são i18n:** o singleton `Placar` guarda o nome customizado ou string **vazia** — nunca "Jogador 1"/"Jogador 2" fixos. Quando o nome está vazio, a UI (`MainActivity` e `HistoricoActivity`, via `nomeExibivel()`) exibe o recurso localizado `R.string.jogador_1`/`R.string.jogador_2`. Assim, um app em inglês mostra "Player 1"/"Player 2" sem que a lógica conheça texto algum.

Para testar: `Settings do AVD → System → Languages & input → Languages` e definir inglês ou espanhol; a tela passa a exibir "Player 1"/"Truco Scorekeeper" (en) ou "Jugador 1"/"Marcador de Truco" (es) — incluindo o histórico, no formato `Player 1: 0 games won` / `Jugador 1: 0 partidas ganadas`.

## 8. Roteamento sem pacotes (Intents explícitos)

A navegação entre telas é feita **apenas com `Intent` explícito do Android, manualmente no código** — nenhuma biblioteca de navegação (Navigation Component/Compose Navigation) é usada, nem declarada em Gradle:

```kotlin
// MainActivity.kt
startActivity(Intent(this, HistoricoActivity::class.java))
startActivity(Intent(this, NomesActivity::class.java))

// NomesActivity.kt — volta à tela principal
finish()
```

Cada tela é uma classe `Activity` declarada no `AndroidManifest.xml` (`MainActivity` é a launcher com `intent-filter` MAIN/LAUNCHER; as outras duas são `exported="false"`). A tela principal re-renderiza os dados em `onResume()`, refletindo o estado atualizado quando a pessoa usuária retorna da tela de nomes.

## 9. Biblioteca Material e ícones

O projeto usa `com.google.android.material:material:1.12.0` (configurado em `app/build.gradle.kts`):

- **Tema**: `Theme.Material3.DayNight.NoActionBar` (em `res/values/themes.xml`), que dá suporte a claro/escuro e estilo Material 3 aos componentes.
- **`MaterialButton` com ícones**: os três botões de navegação da tela principal (`Hist. de Jogadas`, `Zerar Histórico`, `Inf. Nomes`) são `MaterialButton` com o atributo `app:icon`.
- **Ícones**: vetores criados no projeto em `res/drawable`, 24dp, com paths dos ícones do Material Design:
  - `ic_history.xml` — histórico de jogadas;
  - `ic_restart.xml` — zerar/reiniciar;
  - `ic_person.xml` — informar nomes.
- O diálogo de vitória usa `MaterialAlertDialogBuilder` (API do pacote material) para manter a identidade visual Material3.

## 10. Testes

### 10.1 Testes unitários (JUnit4, lógica pura sem Android)

A lógica do enunciado foi extraída para um singleton Kotlin puro — `object Placar` (`app/src/main/java/br/edu/utfpr/truco/Placar.kt`) — com os métodos `adicionarPontos(jogador, pontos)` (retorna `true` ao atingir ≥ 12, sem zerar), `resetPontuacoes()` (zera só as pontuações, preservando nomes e contadores — chamada no fechamento da mensagem de vitória), deteção de vitória (≥ 12), `zerarTudo()` e `definirNomes()` (trim; em branco grava "" = padrão localizado). A suíte `app/src/test/java/br/edu/utfpr/truco/PlacarTest.kt` (JUnit 4.13.2) cobre (18 testes): estado inicial (tudo zerado; nomes vazios = padrão localizado), soma e acumulação por jogador, independência entre jogadores, vitória exata em 12 e acima de 12, `adicionarPontos` NÃO zera na vitória (placar mantém 12 durante a mensagem), `resetPontuacoes` zera depois preservando contadores e nomes, incrementa partidas do vencedor, preserva nomes, `definirNomes` (inclusive em branco) e `zerarTudo`. **Não há dependência de classes Android nos testes unitários.**

```bash
./gradlew testDebugUnitTest
# resultado esperado: 18/18 testes verdes (relatórios em app/build/test-results/testDebugUnitTest/)
```

A cobertura contratada por critério de aceitação está em `docs/test-author-claims.json`; a evidência da fase RED (execução antes da implementação, com falha de compilação `Unresolved reference 'Placar'`) está em `docs/evidence/unit-test-red.log`.

### 10.2 Testes de instrumentação (espresso)

`app/src/androidTest/java/br/edu/utfpr/truco/MainActivityTest.kt` usa `androidx.test.ext:junit` + `espresso-core`:

- clicar `+1` do Jogador 1 e verificar o `TextView` de pontuação com `"1"`;
- clicar `+1`, `+3` e `+6` do Jogador 2 e verificar `"10"` acumulado;
- fluxo completo de **vitória**: clicar `+12` do zero do Jogador 1 → `AlertDialog` de vitória exibido (texto da mensagem no root `isDialog()`; o placar atras do diálogo segue 12, verificado por estado no `Placar`) → fechar com OK → pontuação zerada `"0"` → `Hist. de Jogadas` mostra `: 1`;
- clicar `Zerar Histórico` e verificar pontuação restaurada a `"0"`;
- navegar a `Hist. de Jogadas` e verificar as duas linhas com `": 0"` (formato localizado `jogador: N partidas ganhas`);
- informar nomes nos `EditText` (`replaceText`), clicar `Confirmar` e verificar os novos nomes na tela principal.

```bash
# com o emulador (ou aparelho) conectado; para forçar o emulador:
ANDROID_SERIAL=emulator-5554 ./gradlew connectedDebugAndroidTest
# resultado esperado: 6/6 testes verdes; relatórios em app/build/outputs/androidTest-results/
```

### 10.3 Evidências

- `docs/evidence/unit-test-red.log` — fase RED (falha de compilação com o símbolo `Placar` ausente);
- `docs/evidence/assemble-debug-unit-green.log` — `assembleDebug` + `testDebugUnitTest` verdes;
- `docs/evidence/connected-green.log` — `connectedDebugAndroidTest` no emulador;
- `docs/sdd/SDD-av-android-2026-2.md` — decisões de projeto do rito de engenharia (D1–D11).
