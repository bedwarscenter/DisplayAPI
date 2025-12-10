package center.bedwars.api.tablist;

import center.bedwars.api.nms.NMSHelper;
import center.bedwars.api.util.ReflectionCache;
import center.bedwars.api.util.TextUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class Tablist implements ITablist {

    private final WeakReference<Player> playerRef;
    private String cachedHeader = "";
    private String cachedFooter = "";
    @Getter
    private boolean updated = false;

    public Tablist(Player player) {
        this.playerRef = new WeakReference<>(player);
    }

    @Override
    public void update(String header, String footer) {
        Player player = playerRef.get();
        if (player == null || !player.isOnline())
            return;

        if (hasChanged(header, footer)) {
            sendPacket(player, header, footer);
            this.cachedHeader = header;
            this.cachedFooter = footer;
            this.updated = true;
        }
    }

    @Override
    public void clear() {
        Player player = playerRef.get();
        if (player == null || !player.isOnline())
            return;

        sendPacket(player, "", "");
        this.cachedHeader = "";
        this.cachedFooter = "";
    }

    private boolean hasChanged(String header, String footer) {
        return !cachedHeader.equals(header) || !cachedFooter.equals(footer);
    }

    private void sendPacket(Player player, String header, String footer) {
        try {
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
            ReflectionCache.setField(packet, "a", createComponent(header));
            ReflectionCache.setField(packet, "b", createComponent(footer));
            NMSHelper.sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IChatBaseComponent createComponent(String text) {
        return IChatBaseComponent.ChatSerializer.a(
                "{\"text\":\"" + TextUtil.escapeJson(text) + "\"}");
    }
}
