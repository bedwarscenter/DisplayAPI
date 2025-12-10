package center.bedwars.api.scoreboard;

import center.bedwars.api.nms.NMSHelper;
import center.bedwars.api.util.ReflectionCache;
import center.bedwars.api.util.TextUtil;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.*;

public class Scoreboard implements IScoreboard {

    private static final int SIDEBAR_POSITION = 1;
    private static final int MAX_LINES = 15;
    private static final String[] COLOR_ENTRIES = generateColorEntries();

    private final WeakReference<Player> playerRef;
    private final String objectiveName;
    private final Map<Integer, String> lineCache = new HashMap<>();
    private String currentTitle = "";
    @Getter
    private boolean created = false;
    @Getter
    private int lineCount = 0;

    public Scoreboard(Player player) {
        this.playerRef = new WeakReference<>(player);
        this.objectiveName = "bwsb_" + player.getName();
    }

    private static String[] generateColorEntries() {
        String[] entries = new String[MAX_LINES + 1];
        String colors = "0123456789abcdef";
        for (int i = 0; i <= MAX_LINES; i++) {
            if (i < 16) {
                entries[i] = "" + ChatColor.COLOR_CHAR + colors.charAt(i) + ChatColor.RESET;
            } else {
                entries[i] = "" + ChatColor.COLOR_CHAR + colors.charAt(i / 16) +
                        ChatColor.COLOR_CHAR + colors.charAt(i % 16) + ChatColor.RESET;
            }
        }
        return entries;
    }

    @Override
    public void create() {
        if (created)
            return;

        Player player = getPlayer();
        if (player == null)
            return;

        sendPacket(player, createObjectivePacket(0, ""));
        sendPacket(player, createDisplayPacket());
        created = true;
    }

    @Override
    public void update(String title, List<String> lines) {
        updateTitle(title);
        updateLines(lines);
    }

    @Override
    public void updateTitle(String title) {
        if (title == null)
            title = "";
        if (currentTitle.equals(title))
            return;

        Player player = getPlayer();
        if (player == null)
            return;

        String truncated = TextUtil.truncateForTitle(title);
        sendPacket(player, createObjectivePacket(2, truncated));
        currentTitle = title;
    }

    @Override
    public void updateLines(List<String> lines) {
        if (lines == null)
            lines = Collections.emptyList();

        Player player = getPlayer();
        if (player == null)
            return;

        int newSize = Math.min(lines.size(), MAX_LINES);
        removeExtraLines(player, newSize);
        updateExistingLines(player, lines, newSize);
        lineCount = newSize;
    }

    private void removeExtraLines(Player player, int newSize) {
        Iterator<Map.Entry<Integer, String>> it = lineCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = it.next();
            int score = entry.getKey();
            if (score > newSize || score <= 0) {
                String teamName = getTeamName(score);
                sendPacket(player, createScoreRemovePacket(score));
                sendPacket(player, createTeamRemovePacket(teamName));
                it.remove();
            }
        }
    }

    private void updateExistingLines(Player player, List<String> lines, int size) {
        for (int i = 0; i < size; i++) {
            int score = size - i;
            String line = lines.get(i);
            updateLine(player, score, line);
        }
    }

    private void updateLine(Player player, int score, String line) {
        String truncated = TextUtil.truncateForLine(line);
        String cached = lineCache.get(score);

        if (truncated.equals(cached))
            return;

        String teamName = getTeamName(score);
        String entry = getEntry(score);
        String[] parts = TextUtil.splitLine(truncated);

        if (cached != null) {
            sendPacket(player, createTeamRemovePacket(teamName));
        }

        sendPacket(player, createTeamPacket(teamName, entry, parts[0], parts[1]));
        sendPacket(player, createScorePacket(entry, score));
        lineCache.put(score, truncated);
    }

    @Override
    public void remove() {
        if (!created)
            return;

        Player player = getPlayer();
        if (player == null)
            return;

        for (int score : new HashSet<>(lineCache.keySet())) {
            String teamName = getTeamName(score);
            sendPacket(player, createTeamRemovePacket(teamName));
        }

        sendPacket(player, createObjectivePacket(1, ""));
        lineCache.clear();
        created = false;
        lineCount = 0;
    }

    private Player getPlayer() {
        Player player = playerRef.get();
        return (player != null && player.isOnline()) ? player : null;
    }

    private String getTeamName(int score) {
        return "sb_" + score;
    }

    private String getEntry(int score) {
        return score <= MAX_LINES ? COLOR_ENTRIES[score] : COLOR_ENTRIES[0];
    }

    private void sendPacket(Player player, Packet<?> packet) {
        NMSHelper.sendPacket(player, packet);
    }

    private PacketPlayOutScoreboardObjective createObjectivePacket(int mode, String displayName) {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        ReflectionCache.setField(packet, "a", objectiveName);
        ReflectionCache.setField(packet, "b", displayName);
        ReflectionCache.setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        ReflectionCache.setField(packet, "d", mode);
        return packet;
    }

    private PacketPlayOutScoreboardDisplayObjective createDisplayPacket() {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
        ReflectionCache.setField(packet, "a", SIDEBAR_POSITION);
        ReflectionCache.setField(packet, "b", objectiveName);
        return packet;
    }

    private PacketPlayOutScoreboardTeam createTeamPacket(String teamName, String entry,
            String prefix, String suffix) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        ReflectionCache.setField(packet, "a", teamName);
        ReflectionCache.setField(packet, "b", "");
        ReflectionCache.setField(packet, "c", prefix);
        ReflectionCache.setField(packet, "d", suffix);
        ReflectionCache.setField(packet, "e", "always");
        ReflectionCache.setField(packet, "h", 0);
        ReflectionCache.setField(packet, "g", Collections.singletonList(entry));
        return packet;
    }

    private PacketPlayOutScoreboardTeam createTeamRemovePacket(String teamName) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        ReflectionCache.setField(packet, "a", teamName);
        ReflectionCache.setField(packet, "h", 1);
        return packet;
    }

    private PacketPlayOutScoreboardScore createScorePacket(String entry, int score) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
        ReflectionCache.setField(packet, "a", entry);
        ReflectionCache.setField(packet, "b", objectiveName);
        ReflectionCache.setField(packet, "c", score);
        ReflectionCache.setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
        return packet;
    }

    private PacketPlayOutScoreboardScore createScoreRemovePacket(int score) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
        ReflectionCache.setField(packet, "a", getEntry(score));
        ReflectionCache.setField(packet, "b", objectiveName);
        ReflectionCache.setField(packet, "c", score);
        ReflectionCache.setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE);
        return packet;
    }
}
