# KESGram for Android

### THIS FORK IS A WORK IN PROGRESS. THERE ARE NO RELEASES YET.

![PubliGram Logo](.github/PubliGram.png)

## What's this fork even about?

To understand why this fork is a necessary one, we need to investigate Telegram:
<br><br><br>
![Telegram Defaults](.github/telegramdefault.png)

Somewhere in that line is something called MTProto. 

### What is MTProto?
MTProto — a cryptographic protocol created by Nikolai Durov and the Telegram team.

### How does MTProto work?
I don't know, I didn't check. But look at this important diagram:

![MTProto Lifecycle](.github/mtprotoworkflow.png)


### How can this be mitigated?

What I'm suggesting, is something along the lines of this:
<br><br><br>
![KESgram Defaults](.github/kesgramdefault.png)

### More details to come with code updates.

---

## Features

| Telegather | Ayugram | KESGram  |
|:------------- |:-------------|:-----|
| Built with official keys| Full ghost mode |   |
| Screenshots in secret chats| Messages history  |    |
| No emulator detection |Message filters |   |
| No ads |Customizable edited/deleted marks|   |
| Save chats where you were banned/kicked |Local Telegram Premium|   |
| Expire button for TTL photos/videos |Sync read states and message history with AyuSync|   |
|  |Up to stream Telegram version|  |

## How to build

1. Clone source code using `git clone https://github.com/AyuGram/AyuGram4A.git`
2. Open the project in Android Studio. It should be opened, **not imported**
3. Replace `google-services.json` (we don't want to see crash reports from your app...)
4. Generate application certificate and fill API_KEYS:
   ```
   APP_ID = 6
   APP_HASH = "eb06d4abfb49dc3eeb1aeb98ae0f581e"
   MAPS_V2_API = abcdef12345678
   
   SIGNING_KEY_PASSWORD = password
   SIGNING_KEY_ALIAS = alias
   SIGNING_KEY_STORE_PASSWORD = password
   ```
5. You are ready to compile `PubliGram`

- **KESGram** can be built with **Android Studio** or from the command line with **Gradle**:
```
./gradlew assembleAfatRelease
```
- _Base code for AyuHistoryHook and AyuMessageUtils derived from [Dr4iv3rNope's Fork](https://github.com/Dr4iv3rNope/NotSoAndroidAyuGram)_

## Credits

- **[AyuGram](https://github.com/AyuGram/AyuGram4A)**
- [exteraGram](https://github.com/exteraSquad/exteraGram)
- [Telegraher](https://github.com/nikitasius/Telegraher)
- [Cherrygram](https://github.com/arsLan4k1390/Cherrygram)
- [Nagram](https://github.com/NextAlone/Nagram)
- [Telegram FOSS](https://github.com/Telegram-FOSS-Team/Telegram-FOSS)
