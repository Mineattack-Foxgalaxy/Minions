package io.github.skippyall.minions.polymer.block;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class DelegatingElementHolder extends ElementHolder {
    private final List<ElementHolder> elementHolders;

    public DelegatingElementHolder(List<ElementHolder> elementHolders) {
        this.elementHolders = elementHolders;
    }

    public abstract @Nullable ElementHolder getElementHolderFor(ServerGamePacketListenerImpl player);

    @Override
    public boolean isPartOf(int entityId) {
        for(ElementHolder holder : elementHolders) {
            if(holder.isPartOf(entityId)) {
                return true;
            }
        }
        return super.isPartOf(entityId);
    }

    @Override
    public IntList getEntityIds() {
        IntList ids = new IntArrayList(super.getEntityIds());
        for(ElementHolder holder : elementHolders) {
            ids.addAll(holder.getEntityIds());
        }
        return ids;
    }

    @Override
    public List<VirtualElement> getElements() {
        List<VirtualElement> elements = new ArrayList<>(super.getElements());
        for(ElementHolder holder : elementHolders) {
            elements.addAll(holder.getElements());
        }
        return elements;
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        super.startWatching(player);
        ElementHolder holder = getElementHolderFor(player);
        if(holder != null) {
            return holder.startWatching(player);
        } else {
            return false;
        }
    }

    @Override
    public boolean stopWatching(ServerGamePacketListenerImpl player) {
        super.stopWatching(player);
        ElementHolder holder = getElementHolderFor(player);
        if(holder != null) {
            return holder.startWatching(player);
        } else {
            return false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        for(ElementHolder holder : elementHolders) {
            holder.tick();
        }
    }

    @Override
    public void setAttachment(@Nullable HolderAttachment attachment) {
        super.setAttachment(attachment);
        for(ElementHolder holder : elementHolders) {
            holder.setAttachment(attachment);
        }
    }

    @Override
    public VirtualElement.InteractionHandler getInteraction(int id, ServerPlayer player) {
        for(ElementHolder holder : elementHolders) {
            VirtualElement.InteractionHandler handler = holder.getInteraction(id, player);
            if(handler != VirtualElement.InteractionHandler.EMPTY) {
                return handler;
            }
        }
        return super.getInteraction(id, player);
    }

    @Override
    public void destroy() {
        super.destroy();
        for(ElementHolder holder : elementHolders) {
            holder.destroy();
        }
    }

    @Override
    public void notifyUpdate(HolderAttachment.UpdateType updateType) {
        super.notifyUpdate(updateType);
        for(ElementHolder holder : elementHolders) {
            holder.notifyUpdate(updateType);
        }
    }

    @Override
    public IntList getAttachedPassengerEntityIds() {
        IntList ids = new IntArrayList(super.getAttachedPassengerEntityIds());
        for(ElementHolder holder : elementHolders) {
            ids.addAll(holder.getAttachedPassengerEntityIds());
        }
        return ids;
    }
}
