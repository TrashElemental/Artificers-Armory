package net.trashelemental.artificers_armory.entity.ai.familiar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class FamiliarAwareness {
    public List<Mob> nearbyMobs = new ArrayList<>();
    public List<ItemEntity> nearbyItems = new ArrayList<>();
    public List<Player> nearbyPlayers = new ArrayList<>();
    public List<BlockPos> interestingBlocks = new ArrayList<>();

}
