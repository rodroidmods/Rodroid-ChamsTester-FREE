# Rodroid Chams Tester — VIP

> **OpenGL ES chams / wallhack tester for Android games.**
> Modern, drop-in shader-capture and visual-test menu — built by **Rodroid Mods**.

This is the **FREE** build of Chams Tester.

---

## Highlights

- **Modern Material-style overlay menu** — drag, resize, persisted size/position per window
- **Theme color picker** — pick any accent; every dialog, floating window, button and section header follows it live
- **Two floating windows**
  - **Show Shaders** (captured-shader list, live refresh, export)
  - **Pick Shader** (radio-list with search, live active-shader label)
- **Shader Capture Mode** — full visible-shader inventory, not a static list
- **Show Full Shaders** — see every captured shader at once
- **Enter Shader (custom)** — type any shader name and apply it
- **X-Ray** — first-person overdraw + third-person tinted-occlusion variant
- **Flash Mode** — color-flash chams pass
- **HUD / Dump Shader** — one-tap shader inventory dump
- **Dialog Mode toggle** — switch between in-place dialog and floating-window UX

these are VIP features only.

## What's in the box

| Component | File |
|---|---|
| Menu UI | `app/src/main/java/com/rodroid/chamstester/Menu.java` |
| MainActivity (setup screen) | `app/src/main/java/com/rodroid/chamstester/MainActivity.java` |
| Native chams pipeline | `app/src/main/jni/Includes/Chams.h` |
| Hooks + bridge | `app/src/main/jni/Main.cpp` |
| Overlay launcher | `app/src/main/java/com/rodroid/chamstester/Launcher.java` |

## Setup

The MainActivity ships with **copy-ready snippets** for the target game's smali:

1. Add `SYSTEM_ALERT_WINDOW` permission (or use the no-permission start variant)
2. Add the `com.rodroid.chamstester.Launcher` service to the game's `AndroidManifest.xml`
3. Inject `invoke-static {p0}, Lcom/rodroid/chamstester/Main;->Start(Landroid/content/Context;)V` into the game's `MainActivity.onCreate`

Every snippet is one-tap copyable from the in-app setup screen.

## Build

```
use aide to build it.
```

ABIs: `armeabi-v7a`, `arm64-v8a`. Min SDK 23, target 29.

---

## VIP vs Free comparison

| Feature | Free (open-source) | **VIP** |
|---|---|---|
| Select Shader | ✓ | ✓ |
| Show Shaders | ✓ | ✓ |
| Enter Shader | ✓ | ✓ |
| Show **Full** Shaders | — | ✓ |
| Dialog Mode toggle | — | ✓ |
| Theme color picker | — | ✓ |
| X-Ray (1st / 3rd person) | — | ✓ |
| Flash Mode | — | ✓ |
| HUD / Dump Shader buttons | — | ✓ |
| Modern UI (resize, drag, themed) | — | ✓ |
| All known bugs fixed | — | ✓ |

The free version is functional but ships with a basic UI and is missing X-Ray, Flash Mode, HUD / Dump buttons, the theme color picker, Show Full Shaders, and Dialog Mode toggle. The VIP build adds all of those, redesigns the UI, and fixes every bug encountered in the free release.

---

## Buy / Contact

This is a **closed source** VIP release.

To get the source, contact **@rodroidmods** on Telegram.
Payment is accepted as a **Telegram Premium gift (3 months or longer)** or any equivalent subscription gifted to my Telegram account.

- Channel / news / report bugs: https://t.me/+WmudnO0-xoNhMDQ8

---

## Credit

Built and maintained by **Rodroid Mods**.
Subscribe to the channel for new tools and updates.
