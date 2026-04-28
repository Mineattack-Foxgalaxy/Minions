package io.github.skippyall.minions.docs;

import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.StrictJsonParser;

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

public class DocsManager implements SimpleResourceReloadListener<Pair<Map<Identifier, DocsEntry>, DocsTree>> {
    private static Map<Identifier, DocsEntry> docs;
    private static DocsTree tree;

    public static DocsTree getTree() {
        return tree;
    }

    public static void showDocsEntry(ServerPlayerEntity player, Identifier id) {
        DocsEntry entry = getDocsEntry(id);
        if(entry == null) {
            return;
        }

        List<DialogActionButtonData> buttons = new ArrayList<>();
        if(tree != null) {
            DocsTree.DocElement element = tree.getElement(id);
            if (element.previous() != null) {
                Identifier previousId = element.previous().getId();
                buttons.add(getDialogButton(Text.literal("<- ").append(Text.translatable(getDocsEntry(previousId).getMetadata().titleKey())), previousId.toString()));
            }
            if (element.next() != null) {
                Identifier nextId = element.next().getId();
                buttons.add(getDialogButton(Text.translatable(getDocsEntry(nextId).getMetadata().titleKey()).append(Text.literal(" ->")), nextId.toString()));
            }
        }

        buttons.add(new DialogActionButtonData(
                new DialogButtonData(Text.translatable("gui.ok"), 100),
                Optional.empty()
        ));

        player.openDialog(RegistryEntry.of(new MultiActionDialog(
                new DialogCommonData(
                        Text.translatable(entry.getMetadata().titleKey()),
                        Optional.empty(),
                        true,
                        false,
                        AfterAction.CLOSE,
                        entry.getDialog(player.getRegistryManager()),
                        List.of()
                ),
                buttons,
                Optional.empty(),
                2
        )));
    }

    private static DialogActionButtonData getDialogButton(Text text, String dialogToOpen) {
        return new DialogActionButtonData(
                new DialogButtonData(
                        text, 100
                ),
                Optional.of(new SimpleDialogAction(new ClickEvent.RunCommand("/minions docs " + dialogToOpen)))
        );
    }

    public static DocsEntry getDocsEntry(Identifier id) {
        return docs.get(id);
    }

    public static Collection<Identifier> getDocsEntryIds() {
        return docs.keySet();
    }

    @Override
    public CompletableFuture<Pair<Map<Identifier, DocsEntry>, DocsTree>> load(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Identifier, Resource> resources = resourceManager.findResources("docs", id -> id.getNamespace().equals(Minions.MOD_ID) && id.getPath().endsWith(".json"));

            final DocsTree.BranchElement[] root = {null};
            Map<Identifier, DocsEntry> docsEntries = new HashMap<>();
            resources.forEach((id, resource) -> {
                try(Reader reader = resource.getReader()) {
                    if(id.getPath().equals("docs/tree.json")) {
                        DocsTree.BranchElement.CODEC.decode(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
                                .ifSuccess(entry -> root[0] = entry.getFirst())
                                .ifError(error -> Minions.LOGGER.warn("Could not parse docs tree {}: {}", id, error.message()));
                    } else {
                        Identifier docId = Identifier.of(id.getNamespace(), id.getPath().substring("docs/".length(), id.getPath().length() - ".json".length()));
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
                return new Pair<>(docsEntries, tree);
            } else {
                return new Pair<>(docsEntries, null);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Pair<Map<Identifier, DocsEntry>, DocsTree> o, ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            docs = o.getLeft();
            tree = o.getRight();
            return null;
        });
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Minions.MOD_ID, "docs");
    }
}
