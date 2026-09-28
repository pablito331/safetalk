# 🛡️ SafeTalk — Mensageiro Seguro & Gestão Familiar

> **Comunicação segura para crianças, tranquilidade total para os pais e educação financeira prática para a família toda.**

SafeTalk é um aplicativo Android desenvolvido para permitir que crianças e adolescentes conversem com amigos e familiares em um ambiente protegido, sem exposição a riscos da internet aberta, ao mesmo tempo em que ensina responsabilidade através de tarefas, cofrinho digital e supervisão parental proativa.

---

## 💡 Por que o SafeTalk existe?

Os aplicativos tradicionais de mensagens (como WhatsApp ou redes sociais) expõem crianças a contatos desconhecidos, conteúdos inadequados e distrações sem qualquer camada de controle parental eficiente. 

O **SafeTalk** resolve isso oferecendo uma interface moderna e divertida para os filhos (estilo mensageiro instantâneo), enquanto entrega aos responsáveis o controle e a visibilidade necessários para garantir a segurança dos filhos.

---

## ✨ Principais Funcionalidades

### 💬 1. Mensagens Seguras & Filtro Divertido
- **Chat Estilo WhatsApp**: Envio de textos, fotos, vídeos, mensagens de voz com gravação e reprodução.
- **Aprovação Prévia de Contatos**: A criança só consegue conversar com contatos aprovados pelos pais.
- **Filtro Divertido de Palavrões**: Palavras ofensivas ou palavrões são automaticamente convertidos em termos cômicos e emojis (ex: 💩, 🦄, 🌈), promovendo um vocabulário saudável com bom humor.
- **Auditoria Parental**: Os pais podem auditar conversas e receber alertas de segurança caso novos contatos tentem interagir.

### 💰 2. Cofrinho Familiar, Tarefas & Mesada
- **Missões e Recompensas**: Pais cadastram tarefas do dia a dia (ex: *Arrumar a cama*, *Lição de Matemática*) com valores em reais e pontos.
- **Evidência por Foto**: Tarefas que exigem comprovação fotográfica antes da aprovação dos pais.
- **Multas por Atraso**: Penalidades automáticas configuráveis para prazos não cumpridos, ensinando pontualidade.
- **Transferência entre Irmãos**: Crianças podem passar tarefas entre irmãos (com consentimento mútuo e aprovação dos pais) e transferir saldo de mesada.
- **Saque em Mãos / Dívida Paga**: Criança solicita o resgate do cofrinho para despesas reais e os pais confirmam a entrega do dinheiro físico no app.
- **Metas do Mês**: Metas de lazer (ex: *Cinema com Pipoca*) e bônus no cofrinho desbloqueados por % de cumprimento de missões.

### 🚨 3. Segurança Física, SOS & Alarme
- **Check-in "Cheguei Bem"**: Envio com 1 toque de confirmação de chegada segura à escola ou casa.
- **Aviso de Saída / Em Trânsito**: Notifica a família no chat em tempo real que o filho está em deslocamento.
- **Botão SOS de Emergência**: Emite alerta prioritário com aviso sonoro para os pais.
- **Alarme Sonoro Remoto**: Toca um alarme alto no celular da criança mesmo se o aparelho estiver no modo silencioso (ótimo para encontrar o celular ou situações urgentes).

### 📱 4. Modos de Uso Inteligentes por Idade
- **Modo Criança / Filho**: Focado no chat, tarefas pendentes, extrato do cofrinho e SOS.
- **Modo Pais / Responsável**: Protegido por PIN de 4 dígitos, com acesso total a relatórios, aprovação de contatos, gestão financeira e supervisão.
- **Modo Amigo Convidado**: Permite que amigos instalem de forma simplificada para conversar com a criança sob autorização dos pais.

---

## 🔒 Arquitetura & Privacidade

- **Offline-First (Room Database)**: As conversas e dados financeiros ficam salvos primariamente no banco de dados SQLite local no próprio aparelho.
- **Sincronização em Nuvem (Supabase)**: Entrega temporária de mensagens e sincronização entre dispositivos da família.
- **Auto-Atualização Direta (In-App OTA)**: O aplicativo consulta novos lançamentos no GitHub Releases e permite atualizar com 1 clique diretamente pelo app.

---

## 🚀 Como Instalar & Rodar

### Instalação Direta (APK)
1. Baixe o arquivo `safetalk.apk` disponível na seção de [Releases do GitHub](https://github.com/pablito331/safetalk/releases).
2. Instale no celular Android (habilite a opção de instalar de fontes conhecidas caso solicitado).
3. O aplicativo atualizará sozinho automaticamente quando novas versões forem lançadas.

### Compilação a partir do Código Fonte
```bash
# Clone o repositório
git clone https://github.com/pablito331/safetalk.git
cd safetalk

# Compile o APK de debug
./gradlew assembleDebug
```
O arquivo APK gerado estará em `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📄 Licença
Distribuído sob licença de uso privado familiar. Desenvolvido para proporcionar uma infância digital mais segura e conectada.
