# Circle

A social media Android app combining a **friend-only visual feed** (Instagram-style), a **reciprocal friend network** (Facebook-style), and a **real-time trending topics layer** (Twitter/X-style) — built as no single existing app combines all three.

> Full research and design documentation for this app lives in the accompanying Part 1 submission: `Circle_Research_Report.docx` and `Circle_Design_Document.docx`.

## App purpose

Circle gives users two clearly separated spaces:

- **Home Feed** — a private, reverse-chronological feed of photo/video posts from mutually-accepted friends only (never a wider recommended audience).
- **Discover** — a public space for real-time trending hashtags, kept structurally separate from the private feed.

This Part 2 prototype implements the core flow: **Login/Register → Home Feed → Friends → Create Post**. Discover/Trending and additional features (Stories, offline caching, gamified streaks) are documented in the Design Document and deferred to the final PoE submission, as permitted by the brief.

## Design considerations

- **Two-way friend model** (not a one-way follow) — a user's feed only ever shows posts from *accepted* friends, directly addressing the "diluted network" weakness identified in the Part 1 research report for Instagram/Twitter's one-way follow model.
- **Separation of concerns in the backend access layer**: Firebase Authentication's native SDK handles sign-in/sign-up, while all *data* (posts, friendships) goes through hand-written REST calls to Firestore's REST API via Retrofit — deliberately keeping the "SDK" and "RESTful API" requirements distinct rather than blurring them into one Firebase SDK call.
- **MVVM-adjacent structure**: UI (Activities/Fragments) → Repository (network + auth logic) → Network layer (Retrofit/Firestore REST), so business logic can be unit tested without any Android framework dependency.

## Tech stack

| Requirement | Implementation |
|---|---|
| RESTful API | Retrofit calling Firestore's REST API directly (`network/FirestoreApi.kt`) |
| Appropriate SDK | Firebase Authentication SDK (`auth/AuthRepository.kt`) |
| External library | Glide, for image loading in the feed and post creation screens |
| Unit testing | JUnit + Mockito, covering the Firestore JSON conversion logic and repository error handling |

## Project structure

```
app/src/main/java/com/herd/circle/
├── MainActivity.kt              # Bottom-nav host (Feed / Post / Friends)
├── auth/                        # Login, Register, Firebase Auth wrapper
├── feed/                        # Home Feed fragment + adapter
├── friends/                     # Friends fragment + adapter
├── post/                        # Create Post screen
├── network/                     # Retrofit client, Firestore REST interface, JSON converter
├── repository/                  # PostRepository, FriendRepository
└── model/                       # Post, User, Friendship data classes

app/src/test/java/com/herd/circle/
├── FirestoreConverterTest.kt    # Unit tests for REST <-> model mapping
└── PostRepositoryTest.kt        # Unit tests for repository logic (mocked API/auth)
```

## Setup

1. Create a free project at [Firebase Console](https://console.firebase.google.com).
2. Enable **Email/Password** sign-in under Authentication.
3. Create a **Firestore** database (test mode is fine for a prototype).
4. Download `google-services.json` and place it in `app/`.
5. In `network/RetrofitClient.kt`, replace `PROJECT_ID` with your actual Firebase project ID.
6. Open in Android Studio (Hedgehog or later) and let Gradle sync.

## GitHub Actions

This repo uses GitHub Actions (`.github/workflows/build.yml`) to automatically:

1. Check out the repository on every push/PR to `main`.
2. Set up JDK 17.
3. Run all unit tests (`./gradlew testDebugUnitTest`).
4. Build a debug APK (`./gradlew assembleDebug`).
5. Upload the APK as a build artifact.

This confirms the app builds and its tests pass on a clean machine, not only on my own computer, following the guidance in:
- https://github.com/marketplace/actions/automated-build-android-app-with-github-action
- https://github.com/IMAD5112/Github-actions/blob/main/.github/workflows/build.yml

## Video presentation

Demonstration video: https://youtu.be/sMlbe_hPK3g

## AI usage

I used Claude (Anthropic) at several stages of this project, always reviewing and understanding the output before using it rather than copying it blindly.

**Research and design (Part 1).** I used Claude to help research and compare Instagram, Facebook, and Twitter/X for the Part 1 research report, and to help structure the design document (requirements list, API design, data listing, and project plan). I directed the app concept, feature choices (visual feed + two-way friend network + trending topics), and the app name (Circle) myself; Claude helped organise and write these up against the assignment rubric.

**Code scaffolding (Part 2).** I used Claude to scaffold the initial Android project structure — the Gradle build files, the Login/Register/Home Feed/Friends/Create Post screens, the Retrofit-based Firestore REST layer, and the JUnit/Mockito unit tests. I reviewed this code, understood how each part worked, and adjusted the Firebase project configuration (project ID, `google-services.json`) myself to connect it to my own Firebase project.

**Debugging.** Claude helped me work through several real build errors while setting up the project in Android Studio, including:
- A stray typo (`it`) at the top of `app/build.gradle.kts` that caused a Gradle sync failure.
- An "AndroidX dependencies detected but `android.useAndroidX` not enabled" error, which turned out to be caused by a missing `gradle.properties` file, and — after that was fixed — by a Windows read-only file attribute on the project folder that was silently blocking my saved changes from reaching disk. Diagnosing this required checking the file directly via PowerShell and Windows file Properties rather than trusting the IDE's display.
- Connecting BlueStacks to Android Studio via ADB to run and test the app, since I didn't have a physical device set up.

**Documentation.** I used Claude to help draft this README and the accompanying comprehensive report (`Circle_Comprehensive_Report.docx`), based on the actual app structure and the real debugging steps taken above, rather than generic or invented content.

All code in this repository was reviewed and understood by me before submission, and the final app design, feature decisions, and Firebase configuration are my own.
