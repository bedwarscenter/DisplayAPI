package center.bedwars.api.nametag;

import center.bedwars.api.nms.NMSHelper;
import center.bedwars.api.util.ReflectionCache;
import center.bedwars.api.util.TextUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("unused")
public class Nametag implements INametag {

    private final WeakReference<Player> playerRef;
    @Getter
    private final String teamName;
    private final String playerName;

    private String cachedPrefix = "";
    private String cachedSuffix = "";
    @Getter
    private boolean created = false;

    public Nametag(Player player) {
        this.playerRef = new WeakReference<>(player);
        this.playerName = player.getName();
        this.teamName = generateTeamName(player);
    }

    private String generateTeamName(Player player) {
        return "nt_" + player.getUniqueId().toString().substring(0, 8);
    }

    @Override
    public void create() {
        if (created)
            return;
        update(NametagData.empty(playerName));
        created = true;
    }

    @Override
    public void update(NametagData data) {
        if (hasChanged(data)) {
            removeTeamPacket();
            createTeamPacket(data);
            this.cachedPrefix = data.prefix();
            this.cachedSuffix = data.suffix();
        }
    }

    @Override
    public void remove() {
        if (!created)
            return;
        removeTeamPacket();
        created = false;
    }

    private boolean hasChanged(NametagData data) {
        return !cachedPrefix.equals(data.prefix()) || !cachedSuffix.equals(data.suffix());
    }

    private void createTeamPacket(NametagData data) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

        ReflectionCache.setField(packet, "a", teamName);
        ReflectionCache.setField(packet, "b", "");
        ReflectionCache.setField(packet, "c", TextUtil.truncateForPrefixSuffix(data.prefix()));
        ReflectionCache.setField(packet, "d", TextUtil.truncateForPrefixSuffix(data.suffix()));
        ReflectionCache.setField(packet, "e", "always");
        ReflectionCache.setField(packet, "h", 0);
        ReflectionCache.setField(packet, "g", Collections.singletonList(playerName));

        broadcastPacket(packet);
    }

    private void removeTeamPacket() {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        ReflectionCache.setField(packet, "a", teamName);
        ReflectionCache.setField(packet, "h", 1);
        broadcastPacket(packet);
    }

    private void broadcastPacket(PacketPlayOutScoreboardTeam packet) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player online : players) {
            NMSHelper.sendPacket(online, packet);
        }
    }
}
