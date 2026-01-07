# Blanket for ANDROID Build by Mattias Vinberg - https://github.com/vinberg88 - 2026 - Thanks to Rafael Mardojai CM for the original Blanket application.

<img width="128" height="128" alt="com rafaelmardojai Blanket" src="https://github.com/user-attachments/assets/3d145fab-7abb-43a1-91b3-88892a24ba9e" />

**Listen to different sounds**

## Description
Improve focus and increase your productivity by listening to different sounds. Blanket can also be used to help you to fall asleep in a noisy environment.
<br>

<img width="570" height="650" alt="image" src="https://github.com/user-attachments/assets/91e1c6ce-80da-4a98-85a0-21925584f293" />


## Description
IF you use LINUX look here.

## Install

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
