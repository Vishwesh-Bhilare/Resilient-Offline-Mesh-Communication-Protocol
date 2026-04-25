# Flutter rewrite status

The application in `flutter_app/` is now a full Flutter-first rewrite of the original Android prototype.

## Completed in this rewrite

- New Flutter app shell with Material 3 UI and two-tab workflow (main control + chat room).
- End-to-end in-memory mesh message pipeline:
  - Hybrid logical clock ordering.
  - Message signing + verification helper.
  - Repository with dedupe, expiration filtering, and pending publication tracking.
- Runtime simulation for peer discovery/presence pings and controlled message gossip hops.
- Gateway publication simulation that marks pending messages as published and uploads discovered presence IDs when internet mode is enabled.
- Centralized `AppController` state orchestration.

## Current platform assumptions

This rewrite intentionally stays cross-platform and plugin-free. BLE transport, persistent DB, and background tasks are currently represented by simulation services and can be upgraded via platform plugins in follow-up work.
