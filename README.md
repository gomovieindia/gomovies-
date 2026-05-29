# GoMovies - The Play-Store Ready Cinematic Dashboard 🎬

GoMovies is a modern, premium, Android mobile application designed to look and feel like a high-end streaming service (inspired by Netflix and IMDb). It is written entirely in **Kotlin** using modern **Jetpack Compose** and **Material Design 3 (Material You)**, adhering to best-in-class UI/UX guidelines.

---

## 🚀 Play Store App Description
<!--
[GOOGLE PLAY STORE LISTING DETAILS]
Title: GoMovies - Cinematic Tracker & Trailer Discoverer
Short Description: Track, organize, and discover cinema blockbusters in real-time.
Full Description:
Immerse yourself in theatrical entertainment with GoMovies! GoMovies is your master digital cinematic organizer, offering hardware-accelerated trailer streaming and offline tracking.

Key Core Highlights:
• Instant Spotlights: Explore blockbuster releases immediately on our massive edge-to-edge cinematic Hero Banner.
• Multi-Row Collections: Scan horizontal reels for Trending Now, Highest Rated, and Latest Releases.
• Detailed Bios: Get information on runtimes, languages, star reviews, detailed synopses, and cast listings.
• Built-In Web Trailer: Securely stream movie trailers (YouTube) and video previews inside an internal custom web container.
• Personal Local Watchlist: Save or remove cinematic list items offline instantly, persistent across reboots via Room Database.
• Custom Filtering: Refine movie recommendations and suggestions instantly by Category/Genre, production Year, or Star ratings.
• Theme Shifting Customizer: Switch seamlessly between Cinema Dark Mode and Light Theme for optimal day/night viewing.

No signup required. No login limits. Fully optimized for Android phones, tablets, foldables, and ChromeOS devices! Download GoMovies today!
-->

---

## 🎨 Visual Identity & Aesthetic Choices
- **Cinema Slate Dark Theme**: Deep black canvases (`#0A0A0A`) paired with modern gray highlights (`#161616`), allowing movie poster designs to pop with beautiful contrast.
- **Electric DodgerBlue Accents**: Neon DodgerBlue highlights (`#1E90FF`) are reserved for primary action layouts, buttons, active tabs, and navigation focus pills.
- **Adaptive Logo App-Icon**: Features a solid dark square background with a bright blue play button circle encircling the letters "GO" in bold capital letters, accented by a white lower-third "movies" inscription.
- **Typography Pairing**: Features clean modern sans-serif headings paired with monospaced metadata layouts.

---

## 🔌 API & Secret Configuration (The Movie Database)
GoMovies integrates with **The Movie Database (TMDB) API** to load movie backdrops, posters, ratings, cast profiles, and YouTube keys.

### Secure Key Configuration inside Google AI Studio
Following strict container security practices, GoMovies uses the **Secrets Gradle Plugin** to read api keys without hardcoding:

1. Open your **Secrets Panel** in the Google AI Studio UI sidebar.
2. Enter a new key named **`TMDB_API_KEY`**.
3. Set its value to your TMDB API Key (obtained from [themoviedb.org](https://www.themoviedb.org/)).
4. The incremental hot rebuilder automatically injects this secret into your `.env` workspace on next compilation, parsing it securely as `BuildConfig.TMDB_API_KEY`.

> 💡 **Graceful First-Build Stability**: To guarantee an immediate visual review on first compilation, if the `TMDB_API_KEY` is not yet configured, GoMovies falls back to stunning, hand-crafted offline mock files (e.g., *Interstellar*, *Dune: Part Two*, *Inside Out 2*) mapped with genuine TMDB image resources and trailers.

---

## 🧱 Technical Architecture Stack

GoMovies demonstrates clean, production-ready MVVM (Model-View-ViewModel) coding architecture:

- **Language**: Kotlin 2.x (Modern DSL)
- **UI Engine**: Jetpack Compose (100% full-bleed Edge-to-Edge with full transparent `WindowInsets` safe areas)
- **Image Loader**: Coil Compose (asynchronous, crossfaded, and cached loading)
- **Networking Driver**: Retrofit 2.x + OkHttp 4.x + Logging Interceptor (fully TLS sandbox safe)
- **Serializers**: Moshi JSON with KSP code-generated adapters (`ksp`)
- **Database Engine**: Room SQLite Persistence (for persistent offline watchlist tracking)
- **Navigation Coordinator**: Navigation Compose (type-safe standard transitions)
- **Testing Guardrails**: Robolectric + Roborazzi (visual regression screenshot capturing verified)

---

## 📱 Interactive Screens
1. **Logo Splash**: Displays animated scaling & fading visual transitions on app startup.
2. **Home Feed**: Holds cinematic Hero spotlight banners with pull-to-refresh swipe controls and horizontal categories.
3. **Movie detail**: Full-screen layout including ratings, duration, release dates, expandable casts, and trailer action.
4. **Instant search**: Interactive keyword searches filtering by Categories, release years, or rating values concurrently.
5. **Genre Grid**: Browse collections of movies categorised under Action, Science Fiction, comedies, etc.
6. **Room persistence Watchlist**: Save or quick-delete films on an offline local repository instantly.
7. **Settings Settings**: Toggle active color schemes, select interface language, and review TMDB attributions.

---

## ⚠️ APK Export Security Disclaimer
> **Security Warning**: If you share the exported APK containing a valid API key, please be aware that Android APKs can be decompiled, and embedded strings or `BuildConfig` variables can be extracted. For strict production release, it is recommended to route requests through a secure proxy server (such as Firebase Cloud Functions).
