package baegmon.blocksurvival.tools;

import org.bukkit.ChatColor;

public final class Strings {

    public static final String CREATOR = ChatColor.GOLD + "baegmon@gmail.com";
    public static final String VERSION = "1.0.1";
    public static final String LOBBY_INVALID = "The lobby has not been set or is currently invalid. Contact an administrator to fix this issue.";
    public static final String PREFIX = ChatColor.GREEN + "[ " + ChatColor.GOLD +  "BlockSurvival" + ChatColor.GREEN+ " ] ";
    public static final String COMMAND_PREFIX = ChatColor.GOLD +  "/BlockSurvival ";

    public static final String INCORRECT_ARGUMENTS = Strings.PREFIX + ChatColor.RED + "Error: Incorrect Arguments";

    // PERMISSION
    public static final String PERMISSION_ALL = "blocksurvival.*";
    public static final String PERMISSION_ADMIN = "blocksurvival.admin.*";
    public static final String PERMISSION_PLAYER = "blocksurvival.player.*";

    // INDIVIDUAL PLAYER PERMISSION
    public static final String PERMISSION_JOIN = "blocksurvival.player.join";
    public static final String PERMISSION_LEAVE = "blocksurvival.player.leave";
    public static final String PERMISSION_LIST = "blocksurvival.player.list";

    // INDIVIDUAL ADMIN PERMISSION
    public static final String PERMISSION_FORCE_STOP = "blocksurvival.admin.forcestop";
    public static final String PERMISSION_FORCE_START = "blocksurvival.admin.forcestart";
    public static final String PERMISSION_ARENA_SETUP = "blocksurvival.admin.setup";

    // SIGN PERMISSION
    public static final String PERMISSION_USE_SIGN = "blocksurvival.player.sign";

}

