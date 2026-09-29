# 🔧 Funções Desativadas / Parciais — SafeTalk

> **Como usar este documento:** cada seção descreve uma função que existe na interface ou na arquitetura, mas que hoje está **desativada, simulada ou incompleta**. Escreva em **"Como eu quero"** exatamente como você imagina o funcionamento. Não precisa ser técnico — descreva o comportamento que você quer ver no celular.

Legenda: 🟡 Parcial (existe mas incompleto) · 🔴 Desativada (código pronto, nunca chamado) · ⚪ Simulada (UI mostra coisa fake)

---

## 1. Sincronização real entre celulares (Supabase) 🟡

**Estado hoje:** O app envia mensagens e o perfil para o Supabase, mas **nunca baixa** o que os outros celulares enviam. Ou seja: você manda mensagem para a sua filha, ela não recebe no celular dela em tempo real — as mensagens só existem localmente em cada aparelho.

**O que falta:** ouvir o servidor (realtime) para receber mensagens, contatos aprovados, SOS e alarme de outro celular; apagar da fila o que já foi entregue.

**Como eu quero (escreva aqui):**

```
...
```

---

## 2. Backup / restaurar conversas (Google Drive) 🔴

**Estado hoje:** A função `backupHistoryToDrive()` existe mas está vazia — só tem um comentário "Reservado para backup local/drive". Se a sua filha perder ou trocar de celular, **perde todo o histórico**.

**Como eu quero (escreva aqui):**

```
...
```

---

## 3. Sincronização de contatos entre dispositivos 🔴

**Estado hoje:** `syncContacts()` está vazia de propósito. Quando você aprova a amiguinha da sua filha, a aprovação **não chega no celular dela** — cada celular tem sua própria lista.

**Como eu quero (escreva aqui):**

```
...
```

---

## 4. Localização real da criança ⚪

**Estado hoje:** A aba de localização dos pais mostra dados **fictícios gravados no código** ("Dentro da Zona Segura (Escola)"). Não existe GPS: a permissão de localização está no manifest, mas nenhuma linha de código lê a posição real do celular da criança.

**Como eu quero (escreva aqui):**

```
...
```

---

## 5. Bateria e modo silencioso da criança ⚪

**Estado hoje:** O painel dos pais mostra bateria e "modo silencioso" com valores padrão zerados/vazios — nada lê a bateria real do celular da criança.

**Como eu quero (escreva aqui):**

```
...
```

---

## 6. Alarme remoto no celular da criança 🟡

**Estado hoje:** O botão SOS toca o alarme **no próprio celular da criança** e mostra um aviso local. Os pais **não recebem** nada no celular deles — o alarme "remoto" (tocar no celular da criança a partir do celular dos pais) não existe sem sincronização (item 1).

**Como eu quero (escreva aqui):**

```
...
```

---

## 7. Foto real nos Stories / Destaques ⚪

**Estado hoje:** Quando alguém publica um story "com foto", o app salva apenas uma string fake (`photo_story_123456`), não a foto. A foto tirada na câmera não vai para o story.

**Como eu quero (escreva aqui):**

```
...
```

---

## 8. Confirmação de aprovação entre famílias 🟡

**Estado hoje:** Quando alguém entra com o código da família (ex: sua filha de 19 anos), o perfil fica "PENDENTE_APROVACAO" **no próprio celular dela**. Mas os pais não recebem o pedido para aprovar — sem sincronização (item 1), o pedido não cruza dispositivos. Hoje a aprovação seria manual/verbal.

**Como eu quero (escreva aqui):**

```
...
```

---

## 9. Convite real para outra família (@usuário) 🟡

**Estado hoje:** O @usuário (`ana.fam-7k4q9x2m`) já é gerado e pode ser digitado ao adicionar contato, mas como não há servidor cruzando os dados, **nada confirma** que aquele @usuário existe nem que a outra família aceitou. A conversa entre famílias só funciona se ambas adicionarem manualmente.

**Como eu quero (escreva aqui):**

```
...
```

---

## 10. Cadastro com e-mail real (Supabase Auth) 🟡

**Estado hoje:** O cadastro valida e-mail/senha **localmente**. Se o Supabase estiver configurado, envia um convite por e-mail, mas se falhar, o app engole o erro e diz "salvo com sucesso" de qualquer forma. Não há verificação de que o e-mail é real nem login em outro dispositivo com a mesma conta.

**Como eu quero (escreva aqui):**

```
...
```

---

## 11. Notificações (push) 🔴

**Estado hoje:** Não existe notificação push no app — nem FCM, nem notificação local. "Nova mensagem", "pedido de amizade" etc. só aparecem se você estiver com o app aberto.

**Como eu quero (escreva aqui):**

```
...
```

---

## 12. Grupos de conversa 🟡

**Estado hoje:** Existem as entidades e regras de grupo familiar (`FamilyGroupEntity`, `GroupApprovalRules`) com aprovação dos pais, mas **nenhuma tela usa** — não dá para criar grupo de conversa nem convidar membros por ele. As regras foram escritas, a interface nunca foi ligada nelas.

**Como eu quero (escreva aqui):**

```
...
```

---

## 13. Auditar conversas — o que os pais veem 🟡

**Estado hoje:** Os pais podem abrir a conversa do filho em "modo auditoria" (borda diferente, aviso de supervisão). Falta definir: todas as conversas? só de menores? com aviso para o filho? histórico de quem auditou?

**Como eu quero (escreva aqui):**

```
...
```

---

## 14. Filtro de palavrões 🟡

**Estado hoje:** O filtro troca palavrões por emojis/termos cômicos e marca a mensagem (`isFlaggedByFilter`). Os pais veem a marcação, mas não recebem alerta separado nem há lista de palavras configurável pela família.

**Como eu quero (escreva aqui):**

```
...
```

---

## 15. Gemini / Firebase AI 🔴

**Estado hoje:** O projeto tem a dependência do Firebase AI (Gemini) e a chave no `.env.example`, mas **nenhuma linha de código usa**. Nenhuma função de IA existe no app.

**Como eu quero (escreva aqui):**

```
...
```

---

## 16. Transferência de tarefas entre irmãos (aprovação dos pais) 🟡

**Estado hoje:** A criança pede para transferir tarefa a um irmão e a transferência acontece na hora (`siblingAcceptTaskTransfer` salva direto). A regra original previa consentimento mútuo **e aprovação dos pais** — a parte da aprovação parental não é exigida no fluxo.

**Como eu quero (escreva aqui):**

```
...
```

---

## 17. Saque em mãos / dívida paga 🟡

**Estado hoje:** A criança pede o resgate, o pedido é salvo e o saldo é descontado **na hora do pedido**. Não há etapa de o pai confirmar "entreguei o dinheiro na mão" para concluir o ciclo.

**Como eu quero (escreva aqui):**

```
...
```

---

## 18. Aprovação pelos DOIS responsáveis ⚪

**Estado hoje:** Contatos novos e pedidos são aprovados por **um** responsável (quem estiver com o app na mão). Você comentou que a regra da casa seria você **e** a esposa aprovarem.

**Como eu quero (escreva aqui):**

```
...
```

---

*Preencha os campos "Como eu quero" (pode ser solto, do jeito que sair) e me devolva este arquivo — eu transformo cada item em implementação.*
