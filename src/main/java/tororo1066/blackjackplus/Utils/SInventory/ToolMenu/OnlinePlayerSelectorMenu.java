package tororo1066.blackjackplus.Utils.SInventory.ToolMenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import tororo1066.coinflip.CoinFlip;
import tororo1066.coinflip.Utils.SInventory.SInventoryItem;
import tororo1066.coinflip.Utils.SItemStack;
import tororo1066.coinflip.Utils.SStringBuilder;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public class OnlinePlayerSelectorMenu extends LargeSInventoryMenu{

    CoinFlip plugin;
    Player player;
    Consumer<Player> onClick = null;

    ArrayList<UUID> exceptions = new ArrayList<>();

    public OnlinePlayerSelectorMenu(Player p, CoinFlip plugin){
        super(new SStringBuilder().aqua().bold().text("オンラインプレイヤー一覧").build(), plugin);
        this.player = p;
        this.plugin = plugin;
    }

    public void setOnClick(Consumer<Player> event){
        this.onClick = event;
    }

    public void renderMenu(){
        ArrayList<SInventoryItem> items = new ArrayList<>();

        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            if(exceptions.contains(p.getUniqueId())) continue;

            SItemStack icon = new SItemStack(Material.PLAYER_HEAD);
            icon.setDisplayName(new SStringBuilder().yellow().bold().text(p.getName()).build());
            icon.setHeadOwner(p.getUniqueId());
            SInventoryItem item = new SInventoryItem(icon.build());

            item.clickable(false);
            item.setEvent(e -> {
                if(onClick != null) onClick.accept(p);
            });

            items.add(item);
        }
        setItems(items);
    }

    public void addException(UUID player){
        exceptions.add(player);
    }

    public void afterRenderMenu() {
        renderInventory(0);
    }

}
