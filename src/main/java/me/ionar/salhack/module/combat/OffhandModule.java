package me.ionar.salhack.module.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.ionar.salhack.events.player.EventPlayerUpdate;
import me.ionar.salhack.gui.SalGuiScreen;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.entity.PlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

public final class OffhandModule extends Module
{
    public final Value<Modes> Mode = new Value<Modes>("Mode", new String[]{"Mode"}, "If you are above the required health for a totem, x will be used in offhand instead.", Modes.Gap);
    public final Value<Float> ToggleHealth = new Value<Float>("ToggleHealth", new String[]
    { "TH" }, "When you are below this value, this will disable the module.", 10.0f, 0.0f, 20.0f, 0.5f);
    public final Value<Boolean> HotbarFirst = new Value<Boolean>("HotbarFirst", new String[] {"Recursive"}, "Prioritizes your hotbar before inventory slots", false);
    
    public enum Modes
    {
        
        Crystal.
        
    }
    
    public OffhandModule(){
		super("OffhandCrystal", Category.Combat);
		this.moving = false;
		this.returnI = false;
	}

	@Override
	public void setup(){
		health = registerInteger("Health", "Health", 15, 0, 36);
	}

	public void onDisable(){
		if (OffhandCrystal.mc.currentScreen instanceof GuiContainer){
			return;
		}
		this.crystals = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
		if (OffhandCrystal.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING){
			if (this.crystals == 0){
				return;
			}
			int t = -1;
			for (int i = 0; i < 45; i++){
				if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING){
					t = i;
					break;
				}
			}
			if (t == -1){
				return;
			}
			OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
		}
	}

	@Override
	public void onUpdate(){
		this.item = Items.END_CRYSTAL;
		if (OffhandCrystal.mc.currentScreen instanceof GuiContainer){
			return;
		}
		if (this.returnI){
			int t = -1;
			for (int i = 0; i < 45; i++){
				if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).isEmpty()){
					t = i;
					break;
				}
			}
			if (t == -1){
				return;
			}
			OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			this.returnI = false;
		}
		this.totems = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
		this.crystals = OffhandCrystal.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == this.item).mapToInt(ItemStack::getCount).sum();
		if (this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING){
			this.totems++;
		} else if (!this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == this.item){
			this.crystals += OffhandCrystal.mc.player.getHeldItemOffhand().getCount();
		} else{
			if (this.moving){
				OffhandCrystal.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
				this.moving = false;
				this.returnI = true;
				return;
			}
			if (OffhandCrystal.mc.player.inventory.getItemStack().isEmpty()){
				if (!this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == this.item){
					return;
				}
				if (this.shouldTotem() && OffhandCrystal.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING){
					return;
				}
				if (!this.shouldTotem()){
					if (this.crystals == 0){
						return;
					}
					int t = -1;
					for (int i = 0; i < 45; i++){
						if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == this.item){
							t = i;
							break;
						}
					}
					if (t == -1){
						return;
					}
					OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
					this.moving = true;
				} else{
					if (this.totems == 0){
						return;
					}
					int t = -1;
					for (int i = 0; i < 45; i++){
						if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING){
							t = i;
							break;
						}
					}
					if (t == -1){
						return;
					}
					OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
					this.moving = true;
				}
			} else{
				int t = -1;
				for (int i = 0; i < 45; i++){
					if (OffhandCrystal.mc.player.inventory.getStackInSlot(i).isEmpty()){
						t = i;
						break;
					}
				}
				if (t == -1){
					return;
				}
				OffhandCrystal.mc.playerController.windowClick(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, OffhandCrystal.mc.player);
			}
		}
	}

	private boolean shouldTotem(){
		final boolean hp = OffhandCrystal.mc.player.getHealth() + OffhandCrystal.mc.player.getAbsorptionAmount() <= health.getValue();
		final boolean endcrystal = !this.isCrystalsAABBEmpty();
		return hp;
	}

	private boolean isEmpty(final BlockPos pos){
		final List<Entity> crystalsInAABB = OffhandCrystal.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream().filter(e -> e instanceof EntityEnderCrystal).collect(Collectors.toList());
		return crystalsInAABB.isEmpty();
	}

	private boolean isCrystalsAABBEmpty(){
		return this.isEmpty(OffhandCrystal.mc.player.getPosition().add(1, 0, 0)) && this.isEmpty(OffhandCrystal.mc.player.getPosition().add(-1, 0, 0)) && this.isEmpty(OffhandCrystal.mc.player.getPosition().add(0, 0, 1)) && this.isEmpty(OffhandCrystal.mc.player.getPosition().add(0, 0, -1)) && this.isEmpty(OffhandCrystal.mc.player.getPosition());
	}
