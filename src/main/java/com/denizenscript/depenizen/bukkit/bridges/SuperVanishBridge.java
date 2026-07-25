package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.PseudoObjectTagBase;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.denizenscript.depenizen.bukkit.properties.supervanish.SuperVanishPlayerExtensions;
import de.myzelyam.api.vanish.VanishAPI;
import de.myzelyam.supervanish.SuperVanish;

public class SuperVanishBridge extends Bridge {

    static class SuperVanishTagBase extends PseudoObjectTagBase<SuperVanishTagBase> {

        public static SuperVanishTagBase instance;

        public SuperVanishTagBase() {
            instance = this;
            TagManager.registerStaticTagBaseHandler(SuperVanishTagBase.class, "supervanish", (t) -> instance);
        }

        @Override
        public void register() {

            // <--[tag]
            // @attribute <supervanish.list_vanished>
            // @returns ListTag(PlayerTag)
            // @plugin Depenizen, SuperVanish
            // @description
            // Returns a list of all online vanished players.
            // -->
            tagProcessor.registerTag(ListTag.class, "list_vanished", (attribute, object) -> {
                return new ListTag(VanishAPI.getInvisiblePlayers(), PlayerTag::new);
            });

            // <--[tag]
            // @attribute <supervanish.list_vanished_all>
            // @returns ListTag(PlayerTag)
            // @plugin Depenizen, SuperVanish
            // @description
            // Returns a list of all offline and online vanished players.
            // -->
            tagProcessor.registerTag(ListTag.class, "list_vanished_all", (attribute, object) -> {
                return new ListTag(VanishAPI.getAllInvisiblePlayers(), PlayerTag::new);
            });
        }
    }

    public static SuperVanish apiInstance;

    @Override
    public void init() {
        apiInstance = (SuperVanish) plugin;
        SuperVanishPlayerExtensions.register();
        new SuperVanishTagBase();
    }
}
