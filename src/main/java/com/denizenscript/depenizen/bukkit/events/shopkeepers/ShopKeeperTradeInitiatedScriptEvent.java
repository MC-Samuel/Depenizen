package com.denizenscript.depenizen.bukkit.events.shopkeepers;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.depenizen.bukkit.bridges.ShopkeepersBridge;
import com.denizenscript.depenizen.bukkit.objects.shopkeepers.ShopKeeperTag;
import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopKeeperTradeInitiatedScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // shopkeepers player tries trading
    //
    // @Warning This event is called for each successful trade option a ShopKeeper offers. Canceling a trade will also cancel all successive trades that might otherwise have been triggered.
    //
    // @Cancellable true
    //
    // @Triggers when a trade with a shopkeeper is initiated.
    //
    // @Context
    // <context.recipe> Returns a ListTag(ItemTag) of the trade in the form Offered|Offered|Result.
    // <context.shopkeeper> Returns the ShopKeeperTag of the ShopKeeper that the trade occurs with.
    //
    // @Plugin Depenizen, ShopKeepers
    //
    // @Player Always.
    //
    // @Group Depenizen
    //
    // -->

    public ShopKeeperTradeInitiatedScriptEvent() {
        registerCouldMatcher("shopkeepers player tries trading");
    }

    public ShopkeeperTradeEvent event;

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(event.getPlayer());
    }

    @Override
    public ObjectTag getContext(String name) {
        return switch (name) {
            case "recipe" -> ShopkeepersBridge.tradingRecipeToList(event.getTradingRecipe());
            case "shopkeeper" -> new ShopKeeperTag(event.getShopkeeper());
            default -> super.getContext(name);
        };
    }

    @EventHandler
    public void onShopKeeperTrade(ShopkeeperTradeEvent event) {
        this.event = event;
        fire(event);
    }
}
