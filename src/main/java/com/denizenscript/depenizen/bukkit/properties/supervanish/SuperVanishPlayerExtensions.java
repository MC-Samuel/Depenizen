package com.denizenscript.depenizen.bukkit.properties.supervanish;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.depenizen.bukkit.bridges.SuperVanishBridge;
import de.myzelyam.api.vanish.VanishAPI;
import de.myzelyam.supervanish.VanishPlayer;

public class SuperVanishPlayerExtensions {

    static VanishPlayer getVanishPlayer(PlayerTag player) {
        return SuperVanishBridge.apiInstance.getVanishPlayer(player.getPlayerEntity());
    }

    public static void register() {

        // <--[tag]
        // @attribute <PlayerTag.sv_vanished>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @mechanism PlayerTag.sv_vanished
        // @description
        // Returns whether a player is vanished.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "sv_vanished", (attribute, player) -> {
            return new ElementTag(VanishAPI.isInvisible(player.getPlayerEntity()));
        });

        // <--[tag]
        // @attribute <PlayerTag.sv_can_see[<player>]>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @description
        // Returns whether a player can see another player.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, PlayerTag.class, "sv_can_see", (attribute, player, target) -> {
           return new ElementTag(VanishAPI.canSee(player.getPlayerEntity(), target.getPlayerEntity()));
        });

        // <--[tag]
        // @attribute <PlayerTag.sv_item_pickup>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @mechanism PlayerTag.sv_item_pickup
        // @description
        // Returns whether the player can pick up items while vanished.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "sv_item_pickup", (attribute, player) -> {
            return new ElementTag(getVanishPlayer(player).hasItemPickUpsEnabled());
        });

        // <--[tag]
        // @attribute <PlayerTag.sv_permission_levels>
        // @returns MapTag
        // @plugin Depenizen, SuperVanish
        // @description
        // Returns a MapTag with the player's 'see' and 'use' vanish permission levels (if enabled).
        // -->
        PlayerTag.tagProcessor.registerTag(MapTag.class, "sv_permission_levels", (attribute, player) -> {
            if (!SuperVanishBridge.apiInstance.getSettings().getBoolean("IndicationFeatures.LayeredPermissions.LayeredSeeAndUsePermissions", false)) {
                attribute.echoError("SuperVanish leveled permissions are disabled in your config file.");
                return null;
            }
            MapTag values = new MapTag();
            values.putObject("see", new ElementTag(getVanishPlayer(player).getSeePermissionLevel()));
            values.putObject("use", new ElementTag(getVanishPlayer(player).getUsePermissionLevel()));
            return values;
        });

        // <--[mechanism]
        // @object PlayerTag
        // @name sv_vanished
        // @input ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @description
        // Sets whether a player is vanished or not.
        // @tags <PlayerTag.sv_vanished>
        // -->
        PlayerTag.registerOnlineOnlyMechanism("sv_vanished", ElementTag.class, (player, mechanism, value) -> {
            if (!mechanism.requireBoolean()) {
                return;
            }
            if (value.asBoolean()) {
                VanishAPI.hidePlayer(player.getPlayerEntity());
            }
            else {
                VanishAPI.showPlayer(player.getPlayerEntity());
            }
        });

        // <--[mechanism]
        // @object PlayerTag
        // @name sv_item_pickup
        // @input ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @description
        // Sets whether a player can pick up items while vanished.
        // @tags <PlayerTag.sv_item_pickup>
        // -->
        PlayerTag.registerOnlineOnlyMechanism("sv_item_pickup", ElementTag.class, (player, mechanism, value) -> {
            if (mechanism.requireBoolean()) {
                getVanishPlayer(player).setItemPickUps(value.asBoolean());
            }
        });
    }
}
