# SafeTalk MVP + Supabase Strategy

This project is set up to support a real cross-device sync layer later, without hardcoding secrets in code.

## Current MVP behavior
- Local Room database keeps the app history on-device.
- Supabase is planned as the delivery layer for message sync and temporary transit.
- Google Drive backup can be added later for restoring chat history on a new device.

## Required values to add later
Create a .env file in the project root with:

SUPABASE_URL=https://nxmscabgbcwbifwhijwu.supabase.co
SUPABASE_ANON_KEY=COLOQUE_SUA_ANON_KEY_AQUI
SUPABASE_SERVICE_ROLE_KEY=COLOQUE_SUA_SERVICE_ROLE_KEY_AQUI

Then wire the values into SupabaseConfig.kt and replace the placeholder logic in SupabaseSyncService.kt.

## Suggested flow
1. User edits profile locally.
2. App sends profile/contact updates to Supabase.
3. Destination device listens for realtime changes.
4. When the message is downloaded, the app saves it locally.
5. Message is removed from the temporary Supabase queue or marked expired.
6. User can restore chat history from Drive backup after reinstall.

## Notes
- Room remains the local source of truth for the current device.
- Supabase is a short-lived delivery layer for messages between devices.
- Google Drive backup is best used for restore/reinstall scenarios, not active chat state.
