package io.github.skippyall.minions.docs;

import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Tuple;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DocsManager implements SimpleResourceReloadListener<Tuple<Map<ResourceLocation, DocsEntry>, DocsTree>> {
    private static Map<ResourceLocation, DocsEntry> docs;
    private static DocsTree tree;

    public static DocsTree getTree() {
        return tree;
    }

    public static void showDocsEntry(ServerPlayer player, ResourceLocation id) {
        DocsEntry entry = getDocsEntry(id);
        if(entry == null) {
            return;
        }

        List<ActionButton> buttons = new ArrayList<>();
        if(tree != null) {
            DocsTree.DocElement element = tree.getElement(id);
            if (element.previous() != null) {
                ResourceLocation previousId = element.previous().getId();
                buttons.add(getDialogButton(Component.literal("<- ").append(Component.translatable(getDocsEntry(previousId).getMetadata().titleKey())), previousId.toString()));
            }
            if (element.next() != null) {
                ResourceLocation nextId = element.next().getId();
                buttons.add(getDialogButton(Component.translatable(getDocsEntry(nextId).getMetadata().titleKey()).append(Component.literal(" ->")), nextId.toString()));
            }
        }

        buttons.add(new ActionButton(
                new CommonButtonData(Component.translatable("gui.ok"), 100),
                Optional.empty()
        ));

        player.openDialog(Holder.direct(new MultiActionDialog(
                new CommonDialogData(
                        Component.translatable(entry.getMetadata().titleKey()),
                        Optional.empty(),
                        true,
                        false,
                        DialogAction.CLOSE,
                        entry.getDialog(player.registryAccess()),
                        List.of()
                ),
                buttons,
                Optional.empty(),
                2
        )));
    }

    private static ActionButton getDialogButton(Component text, String dialogToOpen) {
        return new ActionButton(
                new CommonButtonData(
                        text, 100
                ),
                Optional.of(new StaticAction(new ClickEvent.RunCommand("/minions docs " + dialogToOpen)))
        );
    }

    public static DocsEntry getDocsEntry(ResourceLocation id) {
        return docs.get(id);
    }

    public static Collection<ResourceLocation> getDocsEntryIds() {
        return docs.keySet();
    }

    @Override
    public CompletableFuture<Tuple<Map<ResourceLocation, DocsEntry>, DocsTree>> load(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, Resource> resources = resourceManager.listResources("docs", id -> id.getNamespace().equals(Minions.MOD_ID) && id.getPath().endsWith(".json"));

            final DocsTree.BranchElement[] root = {null};
            Map<ResourceLocation, DocsEntry> docsEntries = new HashMap<>();
            resources.forEach((id, resource) -> {
                try(Reader reader = resource.openAsReader()) {
                    if(id.getPath().equals("docs/tree.json")) {
                        DocsTree.BranchElement.CODEC.decode(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
                                .ifSuccess(entry -> root[0] = entry.getFirst())
                                .ifError(error -> Minions.LOGGER.warn("Could not parse docs tree {}: {}", id, error.message()));
                    } else {
                        ResourceLocation docId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath().substring("docs/".length(), id.getPath().length() - ".json".length()));
                        DocsEntry.CODEC.decode(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
                                .ifSuccess(entry -> docsEntries.put(docId, entry.getFirst()))
                                .ifError(error -> Minions.LOGGER.warn("Could not parse docs entry {}: {}", id, error.message()));
                    }
                } catch (IOException | JsonParseException e) {
                    Minions.LOGGER.warn("Could not read file {}", id, e);
                }
            });
            if(root[0] != null) {
                DocsTree tree = new DocsTree(root[0]);
                return new Tuple<>(docsEntries, tree);
            } else {
                return new Tuple<>(docsEntries, null);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Tuple<Map<ResourceLocation, DocsEntry>, DocsTree> o, ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            docs = o.getA();
            tree = o.getB();
            return null;
        });
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(Minions.MOD_ID, "docs");
    }
}
