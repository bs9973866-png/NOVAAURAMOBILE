# NOVAURA MOBILE LAUNCHER

An Android launcher project for Minecraft: Java Edition, built with Kotlin and Jetpack Compose.

## Current development status

This repository was initially empty. The first implementation establishes the Android project and a working starting point for the launcher UI.

Implemented in the initial milestone:
- Kotlin + Jetpack Compose Android application.
- NOVAURA dark cosmic visual direction and responsive Compose layout.
- Live retrieval of Minecraft release IDs from Mojang's public version manifest.
- Loading, network-error and retry states for the version catalog.
- GitHub Actions workflow to compile a debug APK and publish it as a workflow artifact.

Not implemented yet:
- A Minecraft Java runtime / actual game launch.
- Download and verification of client JARs, libraries and assets.
- Fabric, Forge, NeoForge and Quilt installation.
- Microsoft or Ely.by authentication.
- Modrinth and CurseForge API integrations.
- Profile isolation, modpack import/export, skins and 3D skin preview.
- Turnip/Zink integration, audio player and remote AI service.

The current **ИГРАТЬ** button opens the version selector; it does not launch Minecraft yet. This is intentionally stated in the UI rather than pretending the game runtime is connected.

## Build

The GitHub Actions workflow is at `.github/workflows/android.yml`. Pushes to `main` or `master`, pull requests, and manual workflow dispatch attempt to build a debug APK. On success, download the `novaura-mobile-debug-apk` artifact from the workflow run.

Local requirements:
- JDK 17
- Android SDK platform 35 and build tools 35.0.0
- Gradle 8.11.1

Run from the repository root:
```bash
gradle --no-daemon assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`.

## Engineering principles

- Keep launcher UI separate from the future game-runtime and download services.
- Do not ship API secrets inside the APK.
- Verify downloaded artifacts using published checksums when available.
- Do not claim game-launch support until the required runtime, libraries and launch pipeline are implemented and tested.


## Milestone 2 — version catalog

The version selector now parses the Mojang manifest's version type and supports:
- Release versions.
- Snapshots.
- Legacy Beta and Alpha entries when present in the manifest.
- Text search by version ID.
- Type filters and a visible result count.
- A larger filtered result list and an explicit refresh action.

Important: catalog selection is not installation. The launcher still does not download the selected Minecraft client, libraries or assets, and does not launch the game. Legacy entries are listed only when Mojang's manifest provides them; listing a version does not guarantee that a modern Android runtime can launch it.
