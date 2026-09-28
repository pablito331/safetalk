# Supabase realtime setup for SafeTalk

This folder contains the database schema and realtime configuration needed for a real-time family messaging flow.

## Files
- `schema.sql`: PostgreSQL schema for profiles, contacts, chat messages, groups, and invites
- `README.md`: instructions for setup and validation

## Required setup in Supabase
1. Open Supabase SQL editor.
2. Run `schema.sql`.
3. Confirm the `supabase_realtime` publication exists.
4. If it does not, enable Realtime from the Supabase dashboard.
5. Copy your project URL and keys into `.env` and the Android config file.

## Important rule
Use the anon key for normal app clients and the service role key only in a secure backend or server-side process.

## Realtime usage
The app should subscribe to `chat_messages`, `contacts`, `profiles`, and `family_groups` events and reflect those updates in local Room storage.

This keeps the local-first app model while allowing delivery through Supabase for cross-device sync.
