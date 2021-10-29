package com.denizenscript.depenizen.bukkit.properties.worldguard;

import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.depenizen.bukkit.bridges.WorldGuardBridge;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardPlayerProperties implements Property {

    @Override
    public String getPropertyString() {
        return null;
    }

    @Override
    public String getPropertyId() {
        return "WorldGuardPlayer";
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // None
    }

    public static boolean describes(ObjectTag object) {
        return object instanceof PlayerTag
                && ((PlayerTag) object).isOnline();
    }

    public static WorldGuardPlayerProperties getFrom(ObjectTag object) {
        if (!describes(object)) {
            return null;
        }
        return new WorldGuardPlayerProperties((PlayerTag) object);
    }

    public static final String[] handledTags = new String[] {
            "worldguard"
    };

    public static final String[] handledMechs = new String[] {
    }; // None

    private WorldGuardPlayerProperties(PlayerTag player) {
        this.player = player.getPlayerEntity();
    }

    Player player;

    private StateFlag getStateFlag(String s) {
        Flag flag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), s);
        return flag instanceof StateFlag ? (StateFlag) flag : null;
    }

    @Override
    public String getAttribute(Attribute attribute) {

        if (!attribute.startsWith("worldguard") && !attribute.startsWith("wg")) {
            return null;
        }
        attribute = attribute.fulfill(1);

        // <--[tag]
        // @attribute <PlayerTag.worldguard.can_build[<location>]>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, WorldGuard
        // @description
        // Whether WorldGuard allows to build at a location.
        // -->
        if (attribute.startsWith("can_build") && attribute.hasParam()) {
            LocationTag location = attribute.paramAsType(LocationTag.class);
            if (location == null) {
                return null;
            }
            WorldGuardPlugin worldGuard = (WorldGuardPlugin) WorldGuardBridge.instance.plugin;
            return new ElementTag(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().testBuild(BukkitAdapter.adapt(location), worldGuard.wrapPlayer(player)))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <PlayerTag.worldguard.test_flag[<name>]>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, WorldGuard
        // @description
        // Returns the state of a flag for that player at their location.
        // For example: .test_flag[pvp] returns 'true' when the player can be attacked.
        // -->
        if (attribute.startsWith("test_flag")) {
            if (!attribute.hasParam()) {
                Debug.echoError("The tag PlayerTag.worlduard.test_flag[...] must have a value.");
                return null;
            }
            StateFlag flag = getStateFlag(attribute.getParam());
            if (flag == null) {
                Debug.echoError("The tag PlayerTag.worlduard.test_flag[...] has an invalid value: " + attribute.getParam());
                return null;
            }

            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            Location loc = player.getLocation();
            int args = 1;

            // <--[tag]
            // @attribute <PlayerTag.worldguard.test_flag[<name>].at[<location>]>
            // @returns ElementTag(Boolean)
            // @plugin Depenizen, WorldGuard
            // @description
            // Returns the state of a flag for that player at the specified location.
            // -->
            if (attribute.getAttribute(2).startsWith("at") && attribute.hasContext(2)) {
                loc = attribute.contextAsType(2, LocationTag.class);
                args = 2;
                if (loc == null) {
                    return null;
                }
            }
            WorldGuardPlugin worldGuard = (WorldGuardPlugin) WorldGuardBridge.instance.plugin;
            return new ElementTag(query.testState(BukkitAdapter.adapt(loc), worldGuard.wrapPlayer(player), flag))
                    .getAttribute(attribute.fulfill(args));
        }

        return null;

    }
}
