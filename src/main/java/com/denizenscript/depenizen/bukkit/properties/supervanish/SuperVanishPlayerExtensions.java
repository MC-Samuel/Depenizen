package com.denizenscript.depenizen.bukkit.properties.supervanish;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import de.myzelyam.api.vanish.VanishAPI;

public class SuperVanishPlayerExtensions {

    public static void register() {

        // <--[tag]
        // @attribute <PlayerTag.sv_vanished>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @mechanism PlayerTag.sv_vanished
        // @description
        // Returns whether a player is vanished.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, "sv_vanished", (attribute, object) -> {
            return new ElementTag(VanishAPI.isInvisible(object.getPlayerEntity()));
        });

        // <--[tag]
        // @attribute <PlayerTag.sv_can_see[<player>]>
        // @returns ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @description
        // Returns whether a player can see another player.
        // -->
        PlayerTag.tagProcessor.registerTag(ElementTag.class, PlayerTag.class, "sv_can_see", (attribute, object, target) -> {
           return new ElementTag(VanishAPI.canSee(object.getPlayerEntity(), target.getPlayerEntity()));
        });

        // <--[mechanism]
        // @object PlayerTag
        // @name sv_vanished
        // @input ElementTag(Boolean)
        // @plugin Depenizen, SuperVanish
        // @description
        // Controls whether a player is vanished or not.
        // @tags <PlayerTag.sv_vanished>
        // -->
        PlayerTag.registerOnlineOnlyMechanism("sv_vanished", ElementTag.class, (player, mechanism, value) -> {
            if (mechanism.requireBoolean()) {
                if (value.asBoolean()) {
                    VanishAPI.hidePlayer(player.getPlayerEntity());
                }
                else {
                    VanishAPI.showPlayer(player.getPlayerEntity());
                }
            }
        });
    }
}
