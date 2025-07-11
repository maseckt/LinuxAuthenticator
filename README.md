Вот английская версия `README.md`, в которую включена кнопка-переключатель языка в самом верху:

---

````markdown
> 🇷🇺 [Переключиться на русский язык](https://github.com/maseckt/LinuxAuthenticator/blob/main/README-ru.md)

# LinuxAuthenticator

A plugin for Minecraft servers based on Paper that authenticates players using Linux-related questions. Only real Linux users can access the server! 🐧

## Features

- **Two authentication modes:**
  - **CHOICE** – players choose the correct answer from multiple options (click or chat)
  - **TEXT** – players type the exact answer in chat
- **Customizable questions** on various aspects of Linux
- **Timeout system** to prevent players from stalling
- **Error limit** – configurable number of mistakes before being kicked
- **Ban system** – automatic ban after too many failed attempts
- **Mob protection** – mobs ignore unauthenticated players
- **Once-per-session authentication** – no need to re-authenticate on every reconnect
- **Admin commands** to control the plugin
- **Permission system** for skipping authentication
- **Supports Paper 1.16.5–1.21.7**
- **Folia support** – works on multi-threaded servers

## Installation

1. Download the latest version of the plugin.
2. Place the JAR file in your server's `plugins` folder.
3. Restart the server.
4. Configure the plugin in `plugins/LinuxAuthenticator/config.yml`.

## Configuration

### Main Settings

```yaml
settings:
  mode: "CHOICE" # Authentication mode: CHOICE or TEXT
  questions_count: 3
  timeout_seconds: 60
  max_errors: 3
  max_attempts: 3
  ban_duration_minutes: 15
  ban_command: "" # Optional external command to execute on ban
  auth_once_per_session: true
````

### Messages

```yaml
settings:
  messages:
    welcome: "§aWelcome to the server! §ePlease answer some Linux questions to log in:"
    correct_answer: "§aCorrect! Next question:"
    wrong_answer: "§cWrong! Try again."
    timeout: "§cTime's up! You have been kicked from the server."
    success: "§aCongratulations! You are now authenticated!"
    kick_message: "§cYou failed Linux authentication. Please try again later!"
```

### Questions

The plugin uses a universal question system compatible with both modes:

* **In CHOICE mode**, multiple answer options are shown.
* **In TEXT mode**, only the exact answer is accepted.

A ready-to-use set of Linux-related questions is included, covering:

* Package managers (pacman, apt, dnf, zypper, emerge, apk)
* Core Linux commands (ls, find, chmod, ps, mkdir, rm, cp, mv, cat)
* System files (/proc/modules, /proc/loadavg, /proc/cpuinfo, /proc/meminfo)

You can add your own questions in the config.

## Commands

### For Players

* No special commands required

### For Admins

* `/linuxauth reload` – Reload the config
* `/linuxauth reset <player>` – Reset a player's authentication status
* `/linuxauth unban <player>` – Unban a player
* `/linuxauth info` – Show plugin info
* `/linuxauth resetsession` – Reset the authentication session (all players must re-authenticate)

## Permissions

* `linuxauthenticator.admin` – Access to admin commands (default: op)
* `linuxauthenticator.bypass` – Skip authentication (default: op)
* `linuxauthenticator.answer` – Can answer questions (default: true)

## How It Works

1. **When a player joins**, authentication begins (if enabled).
2. **The player is asked questions** about Linux based on the selected mode.
3. **In CHOICE mode**, the player:

   * **Clicks an answer**, or
   * **Types it in chat** (number or text)
4. **In TEXT mode**, the player must type the exact answer.
5. **Answer options are randomized** each time.
6. **Too many errors** – player is kicked.
7. **Too many attempts** – player is banned for a set time.
8. **Mob protection** – mobs cannot attack unauthenticated players.
9. **Correct answers** – player gains access to the server.
10. **One-time per session** – no need to re-authenticate after every reconnect.

## Adding Your Own Questions

Add your questions in the `questions` section:

```yaml
questions:
  - question: "Your question here?"
    answer: "correct_answer"
    options:
      - "option_1"
      - "correct_answer"
      - "option_3"
      - "option_4"
```

**Notes:**

* `answer` – correct answer (used in both modes)
* `options` – choices (only used in CHOICE mode)
* In TEXT mode, options are ignored

## Compatibility

* **Paper/Spigot:** 1.16.5 – 1.21.7
* **Folia:** Supported
* **Java:** 17+
* **Operating Systems:** All supported by Paper

## Support

If you encounter issues or have suggestions, please open an issue in this repository.

## License

This project is licensed under the MIT License.

---

**Made with ❤️ for the Linux & Minecraft community!**
