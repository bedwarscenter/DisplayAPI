# DisplayAPI

A high-performance display library for Minecraft 1.8.8 servers providing Tablist, Nametag, and Scoreboard APIs.

## Features

- **Tablist API** - Per-player header/footer with caching
- **Nametag API** - Team-based nametags with prefix/suffix support
- **Scoreboard API** - Sidebar scoreboards with animated titles
- **NMS/Netty Layer** - Packet interception and manipulation
- **Performance Optimized** - Cached reflection, WeakReferences, minimal packet overhead

## Installation

### Gradle

Add the JAR to your `libs` folder and configure your `build.gradle`:

```groovy
dependencies {
    compileOnly fileTree(dir: 'libs', include: ['DisplayAPI*.jar'])
}
```

## Quick Start

### Tablist
```java
import center.bedwars.api.tablist.Tablist;

Tablist tablist = new Tablist(player);
tablist.update("§6Header", "§aFooter");
```

### Nametag
```java
import center.bedwars.api.nametag.*;

Nametag nametag = new Nametag(player);
nametag.create();

NametagData data = NametagData.builder(player.getName())
    .prefix("§c[Admin] ")
    .suffix(" §7[VIP]")
    .priority(0)
    .build();
nametag.update(data);
```

### Scoreboard
```java
import center.bedwars.api.scoreboard.Scoreboard;

Scoreboard board = new Scoreboard(player);
board.create();

List<String> lines = Arrays.asList(
    "§7§m----------------",
    "§fPlayer: §a" + player.getName(),
    "§fPing: §e" + ping + "ms",
    "§7§m----------------"
);
board.update("§6§lMY SERVER", lines);
```

### NMS Helper
```java
import center.bedwars.api.nms.NMSHelper;

// Send packets
NMSHelper.sendPacket(player, packet);

// Get player ping
int ping = NMSHelper.getPing(player);

// Packet interception
NMSHelper.listenIncoming(player, "listener_name", PacketClass.class, (p, pkt) -> {
    // Handle packet
});
```

## Package Structure

```
center.bedwars.api
├── nms
│   ├── NMSHelper          - NMS utility facade
│   └── netty
│       ├── NettyManager   - Packet interceptor manager
│       ├── PacketInterceptor
│       ├── PacketHandler
│       └── PacketDirection
├── tablist
│   ├── ITablist           - Interface
│   └── Tablist            - Implementation
├── nametag
│   ├── INametag           - Interface
│   ├── Nametag            - Implementation
│   └── NametagData        - Data record with builder
├── scoreboard
│   ├── IScoreboard        - Interface
│   └── Scoreboard         - Implementation
└── util
    ├── ColorUtil          - Color & title utilities
    ├── ReflectionCache    - Cached reflection
    └── TextUtil           - Text manipulation
```

## Performance

- **Cached Reflection**: Fields are cached to avoid repeated lookup overhead
- **WeakReferences**: Player references prevent memory leaks
- **Smart Caching**: Only sends packets when data actually changes
- **Pre-computed Entries**: Color entries for scoreboard are pre-computed

## Build

```bash
./gradlew shadowJar
```

Output: `build/libs/DisplayAPI-1.0.0.jar`

## License

MIT License
