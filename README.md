<meta name="google-site-verification" content="YTE6KNAH688qJUuSz1c7AYMcH7jA3K_T_vonABzvjx8" />

TEST APP HERE. IT WILL BE BETTER BUT FOR NOW SO WE CAN START WORK ON BLANKET FOR ANDROID.

https://play.google.com/store/apps/details?id=com.vinberg88.blanket&pcampaignid=web_share

<a href="https://play.google.com/store/apps/details?id=com.vinberg88.blanket&pcampaignid=web_share"><img width="200" alt="Download via PlayStore" src="https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg"/></a>

- Android is a trademark of Google LLC. By Mattias Vinberg for Google PLAY.

# Blanket for ANDROID - Listen to different sounds and Relax. 

UPDATE 2026-07-26 - I have posted BLANKET to play store and are waiting for the green light to publish Blanket
for all user via GOOGLE. But it take some time.... No stress

## UPDATE 2026-07-22 - Blanket Android mixer refresh

The Android version has been updated to feel closer to the original Blanket desktop app while still being comfortable on a phone.

What changed:

- New Blanket launcher icon for Android install/app drawer.
- More compact sound mixer layout with 4 sounds per row.
- Custom line-style icons for Rain, Storm and Waves, inspired by the original Blanket screenshot.
- Master volume slider in the bottom control bar.
- Sleep timer with 15, 30 and 60 minute options.
- Smooth fade in and fade out when sounds start, stop, pause or resume.
- Imported/custom sounds can now be edited:
  - rename the sound
  - choose an icon
  - delete the sound from the edit dialog
- Imported sounds still use Android document permissions so they can keep working after app restart.
- Gradle memory setting was lowered from 2048 MB to 1024 MB to make local builds easier on smaller machines.

## UPDATE 2026-07-18 - Play Store launch preparation

The Android project has also been prepared for a Google Play launch.

What changed:

- Android target/compile SDK is now API 35 for current Google Play requirements.
- Added release signing support through a local `keystore.properties` file.
- Added Android App Bundle build task for Play Store upload:
  - `./gradlew :app:buildBlanketReleaseBundle`
  - output: `dist/blanket.aab`
- Added an in-app About/Credits dialog with privacy and sound licensing notes.
- Added [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md) for Play Console.
- Added [`docs/PLAY_STORE_RELEASE.md`](docs/PLAY_STORE_RELEASE.md) with release signing, Data Safety and launch checklist.
- Added [`docs/play-store/listing.md`](docs/play-store/listing.md) and a 1024 x 500 feature graphic for store listing work.
- Added `.github/dependabot.yml` so GitHub can open weekly dependency/security update PRs for Gradle and GitHub Actions.

Before uploading to Google Play, create your private upload key locally and add a local `keystore.properties` file. Do not commit the keystore or passwords.

<img width="128" height="128" alt="Mattias Vinberg - Blanket" src="https://github.com/user-attachments/assets/3d145fab-7abb-43a1-91b3-88892a24ba9e" />

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)

Thanks to Mattias Vinberg for build for Android - https://github.com/vinberg88 - Thanks to Rafael Mardojai CM for the original Blanket application - 2026 - https://github.com/rafaelmardojai

**Listen to different sounds**

## Description - Blanket
Improve focus and increase your productivity by listening to different sounds. Blanket can also be used to help you to fall asleep in a noisy environment.
<br>

<img width="570" height="650" alt="image" src="https://github.com/user-attachments/assets/91e1c6ce-80da-4a98-85a0-21925584f293" />

Android - What it looks like - BLANKET Version 1.0.1

<img width="600" height="1050" alt="INTRO" src="https://github.com/user-attachments/assets/424ae701-45e8-4949-83d9-bb75e88c692b" />

<img width="600" height="1050" alt="FRONT" src="https://github.com/user-attachments/assets/623f3ad2-9bef-4957-9694-1d0ac806aa82" />

What it looks like now on Version 1.1.6 via GOOGEL PLAY STORE

<img width="600" height="1050" alt="blanket" src="https://github.com/user-attachments/assets/2066db28-97d1-46e7-9f17-f24e7f3bd73f" />

## Install from FLATPAK FOR LINUX

<a href="https://flathub.org/apps/details/com.rafaelmardojai.Blanket"><img width="200" alt="Download on Flathub" src="https://flathub.org/api/badge?svg&locale=en"/></a>

### Third Party Packages 

| Distribution | Package | Maintainer |
|:-:|:-:|:-:|
| Alpine Linux (edge) | [`blanket`](https://pkgs.alpinelinux.org/packages?name=blanket) | mio |
| Arch Linux (extra) | [`blanket`](https://archlinux.org/packages/extra/any/blanket/) | [Balló György](https://github.com/City-busz) |
| Fedora (Copr) | Copr: [`tuxino/Blob`](https://copr.fedorainfracloud.org/coprs/tuxino/Blob/), package: `blanket` | Tuxino |
| NixOS | [`blanket`](https://search.nixos.org/packages?channel=unstable&show=blanket&from=0&size=50&sort=relevance&type=packages&query=blanket) | onny |
| openSUSE  | [`blanket`](https://build.opensuse.org/package/show/multimedia%3Aapps/blanket) | [Michael Vetter](https://github.com/jubalh) |
| Ubuntu (PPA) | [`Stable Releases`](https://launchpad.net/~apandada1/+archive/ubuntu/blanket), [`Daily Builds`](https://launchpad.net/~apandada1/+archive/ubuntu/blanket-daily) | [Archisman Panigrahi](https://github.com/apandada1) |
| MX Linux | [`blanket`](http://mxrepo.com/mx/repo/pool/main/b/blanket/) | [SwampRabbit](https://github.com/SwampRabbit) |

### Build from source for linux

IF you use LINUX look here.

You can clone and run from GNOME Builder.

#### Requirements

- Python 3 `python`
- PyGObject `python-gobject`
- GTK4 `gtk4`
- libadwaita (>= 1.5.0) `libadwaita`
- GStreamer 1.0 `gstreamer`
- Meson `meson`
- Ninja `ninja`

Alternatively, use the following commands to build it with meson.
```bash
meson builddir --prefix=/usr/local
sudo ninja -C builddir install
```

## Translations
Blanket is translated into several languages. If your language is missing or incomplete, please help to [translate Blanket in Weblate](https://hosted.weblate.org/engage/blanket/).

<a href="https://hosted.weblate.org/engage/blanket/">
<img src="https://hosted.weblate.org/widget/blanket/blanket/horizontal-auto.svg" alt="Translation status" />
</a>

## Credits
Developed for Linux **[Rafael Mardojai CM](https://github.com/rafaelmardojai)** and [contributors](https://github.com/rafaelmardojai/blanket/graphs/contributors).

Thanks to Jorge Toledo for the name idea.

Thanks to Mattias Vinberg for blanket for Android

For detailed information about sounds licensing, [check this file](https://github.com/rafaelmardojai/blanket/blob/master/SOUNDS_LICENSING.md).

## Related Projects
- [Blankie](https://github.com/codybrom/blankie) - Native macOS app inspired by Blanket
- [feeltheblow](https://feeltheblow.web.app/) - Web App inspired by Blanket
- [Soothing Noise Player](https://f-droid.org/en/packages/ie.delilahsthings.soothingloop/),  [Napify](https://play.google.com/store/apps/details?id=com.pronaycoding.blanket_mobile) - Android apps inspired by Blanket
- [Blanket Web](https://apps.roanapur.de/blanket/) - Web clone of Blanket

## License

This project is licensed under the MIT License — see the [LICENSE](./LICENSE) file for details.

# Sounds Licensing

| Sound | Author | Editor* | License
| :-:  | :-: | :-: | :-:
| [Birds](https://freesound.org/people/kvgarlic/sounds/156826/) | kvgarlic | Porrumentzio | CC0
| [Boat](https://freesound.org/people/Falcet/sounds/439365/) | Falcet | Porrumentzio | CC0
| [City](https://freesound.org/people/gezortenplotz/sounds/44796/) | gezortenplotz | Porrumentzio | CC BY
| [Coffee Shop](https://soundbible.com/1664-Restaurant-Ambiance.html) | stephan | - | Public Domain
| [Fireplace](https://soundbible.com/1543-Fireplace.html) | ezwa | - | Public Domain
| [Pink noise](https://es.wikipedia.org/wiki/Archivo:Pink_noise.ogg) | Omegatron | - | CC BY-SA
| [Rain](https://freesound.org/people/alex36917/sounds/524605/) | alex36917 | Porrumentzio | CC BY
| [Summer night](https://soundbible.com/2083-Crickets-Chirping-At-Night.html) | Lisa Redfern | - | Public Domain | 
| [Storm](https://freesound.org/people/digifishmusic/sounds/41739/) | Digifish music | Porrumentzio | CC BY
| [Stream](https://freesound.org/people/gluckose/sounds/333987/) | gluckose | - | CC0
| [Train](https://freesound.org/people/SDLx/sounds/259988/) | SDLx | - | CC BY 3.0
| [Waves](https://freesound.org/people/Luftrum/sounds/48412/) | Luftrum | Porrumentzio | CC BY
| [White noise](https://commons.wikimedia.org/w/index.php?title=File%3AWhite-noise-sound-20sec-mono-44100Hz.ogg) | Jorge Stolfi | - | CC BY-SA |
| [Wind](https://freesound.org/people/felix.blume/sounds/217506/) | felix.blume | Porrumentzio | CC0

(*) Editing implies making the sound meet [this guidelines](https://github.com/rafaelmardojai/blanket/blob/master/CONTRIBUTING.md#sounds).

## Related Projects
- [Blankie](https://github.com/codybrom/blankie) - Native macOS app inspired by Blanket
- [feeltheblow](https://feeltheblow.web.app/) - Web App inspired by Blanket
- [Soothing Noise Player](https://f-droid.org/en/packages/ie.delilahsthings.soothingloop/),  [Napify](https://play.google.com/store/apps/details?id=com.pronaycoding.blanket_mobile) - Android apps inspired by Blanket
- [Blanket Web](https://apps.roanapur.de/blanket/) - Web clone of Blanket

Thank you! ❤️ for watching - Regards Mattias Vinberg - Ubuntu - Android - Blanket - Stockholm - Sweden - 2025
