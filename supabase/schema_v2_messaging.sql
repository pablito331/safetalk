-- ============================================================
-- SafeTalk — Mensageria real entre dispositivos (Fase 1 + 2)
--
-- Rode este script UMA VEZ no Supabase:
-- Dashboard > SQL Editor > New query > cole tudo > Run.
--
-- Modelo:
--   * Cada celular tem uma identidade própria (device_identities),
--     criada no primeiro login Supabase Auth do aparelho.
--   * Mensagens são endereçadas de pessoa→pessoa (sender_id /
--     recipient_id = login_identifier, ex: e-mail).
--   * O destinatário busca suas mensagens, grava local e apaga
--     do servidor (fila temporária — o histórico fica só no
--     aparelho, conforme a proposta offline-first do SafeTalk).
--   * SUPERVISÃO: os PAIS da mesma família podem LER (não apagar)
--     as mensagens em que remetente ou destinatário seja uma
--     CRIANÇA da família. Entre adultos da mesma família não há
--     auditoria — conversa de pai/mãe é privada entre eles.
-- ============================================================

create extension if not exists "pgcrypto";

-- ------------------------------------------------------------
-- 1) Identidade do dispositivo (criada no login do aparelho)
-- ------------------------------------------------------------
create table if not exists public.device_identities (
    device_id uuid primary key default gen_random_uuid(),
    owner_id text not null unique,         -- = auth.uid() (texto) após o login
    login_identifier text not null unique, -- e-mail/@usuário do perfil local
    display_name text not null default '',
    family_code text not null default '',
    role text not null default 'CHILD',    -- 'PARENT' ou 'CHILD'
    created_at timestamptz not null default now()
);

-- ------------------------------------------------------------
-- 2) Fila de mensagens endereçadas (temporária)
-- ------------------------------------------------------------
create table if not exists public.device_messages (
    id uuid primary key default gen_random_uuid(),
    sender_id text not null,               -- login_identifier de quem envia
    sender_name text not null default '',
    recipient_id text not null,            -- login_identifier de quem recebe
    family_code text not null default '',  -- família do remetente (para auditoria)
    client_msg_id text not null,           -- uuid gerado no envio (dedup)
    text text not null default '',
    media_type text not null default 'TEXT',
    media_uri text,
    media_duration_seconds integer default 0,
    formatted_time text not null default '00:00',
    timestamp bigint not null default 0,
    created_at timestamptz not null default now()
);

create index if not exists idx_device_messages_recipient
    on public.device_messages (recipient_id, created_at);
create unique index if not exists uq_device_messages_client
    on public.device_messages (client_msg_id);

-- ------------------------------------------------------------
-- 3) Row Level Security
-- ------------------------------------------------------------
alter table public.device_messages enable row level security;
alter table public.device_identities enable row level security;

-- Enviar: apenas em nome de si mesmo
create policy "enviar em nome de si"
on public.device_messages
for insert
to authenticated
with check (lower(sender_id) = lower(auth.email()));

-- Destinatário lê as mensagens endereçadas a ele
create policy "destinatario le as proprias"
on public.device_messages
for select
to authenticated
using (lower(recipient_id) = lower(auth.email()));

-- SUPERVISÃO: pais da família leem mensagens cujo remetente OU
-- destinatário seja uma CRIANÇA da mesma família. Nunca mensagens
-- entre dois adultos.
create policy "pais supervisionam filhos"
on public.device_messages
for select
to authenticated
using (
  exists (
    select 1 from public.device_identities pai
    where pai.owner_id = auth.uid()::text
      and pai.role = 'PARENT'
      and pai.family_code = device_messages.family_code
  )
  and (
    exists (
      select 1 from public.device_identities crianca
      where crianca.role = 'CHILD'
        and crianca.family_code = device_messages.family_code
        and lower(crianca.login_identifier) = lower(device_messages.sender_id)
    )
    or exists (
      select 1 from public.device_identities crianca
      where crianca.role = 'CHILD'
        and crianca.family_code = device_messages.family_code
        and lower(crianca.login_identifier) = lower(device_messages.recipient_id)
    )
  )
);

-- Apagar da fila: SOMENTE o destinatário (pais leem, não apagam)
create policy "destinatario apaga da fila"
on public.device_messages
for delete
to authenticated
using (lower(recipient_id) = lower(auth.email()));

-- Identidade do dispositivo: o dono insere/lê a própria linha
create policy "identidade do dono - insert"
on public.device_identities
for insert
to authenticated
with check (owner_id = auth.uid()::text and lower(login_identifier) = lower(auth.email()));

create policy "identidade do dono - select"
on public.device_identities
for select
to authenticated
using (owner_id = auth.uid()::text);

-- ------------------------------------------------------------
-- 4) Realtime (opcional agora, usado depois p/ instantâneo)
-- ------------------------------------------------------------
alter publication supabase_realtime add table public.device_messages;

-- ------------------------------------------------------------
-- 5) Limpeza automática: mensagem não coletada em 7 dias expira
-- ------------------------------------------------------------
create or replace function public.purge_old_device_messages()
returns void as $$
begin
  delete from public.device_messages
  where created_at < now() - interval '7 days';
end;
$$ language plpgsql security definer;

-- roda todo dia às 3h da manhã (extensão pg_cron do Supabase)
-- se pg_cron não estiver habilitado, comente as 2 linhas abaixo:
select cron.schedule('purge-device-messages', '0 3 * * *',
  $$ select public.purge_old_device_messages(); $$);
