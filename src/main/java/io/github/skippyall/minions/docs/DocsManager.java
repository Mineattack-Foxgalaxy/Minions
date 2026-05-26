package io.github.skippyall.minions.docs;

import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.skippyall.minions.Minions;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Tuple;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DocsManager extends SimpleReloadListener<Tuple<Map<Identifier, DocsEntry>, DocsTree>> {
    private static @Nullable Map<Identifier, DocsEntry> docs;
    private static @Nullable DocsTree tree;

    public static @Nullable DocsTree getTree() {
        return tree;
    }

    public static void showDocsEntry(ServerPlayer player, Identifier id) {
        DocsEntry entry = getDocsEntry(id);
        if(entry == null) {
            return;
        }

        List<ActionButton> buttons = new ArrayList<>();
        if(tree != null) {
            DocsTree.DocElement element = tree.getElement(id);
            if (element.previous() != null) {
                Identifier previousId = element.previous().getId();
                buttons.add(getDialogButton(Component.literal("<- ").append(Component.translatable(getDocsEntry(previousId).getMetadata().titleKey())), previousId.toString()));
            }
            if (element.next() != null) {
                Identifier nextId = element.next().getId();
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

    public static @Nullable DocsEntry getDocsEntry(Identifier id) {
        if(docs != null) {
            return docs.get(id);
        } else {
            return null;
        }
    }

    public static @Nullable Collection<Identifier> getDocsEntryIds() {
        if (docs != null) {
            return docs.keySet();
        } else {
            return null;
        }
    }

    @Override
    public Tuple<Map<Identifier, DocsEntry>, DocsTree> prepare(SharedState state) {
        Map<Identifier, Resource> resources = state.resourceManager().listResources("docs", id -> id.getNamespace().equals(Minions.MOD_ID) && id.getPath().endsWith(".json"));

        final DocsTree. @Nullable BranchElement[] root = {null};
        Map<Identifier, DocsEntry> docsEntries = new HashMap<>();
        resources.forEach((id, resource) -> {
            try(Reader reader = resource.openAsReader()) {
                if(id.getPath().equals("docs/tree.json")) {
                    DocsTree.BranchElement.CODEC.decode(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
                            .ifSuccess(entry -> root[0] = entry.getFirst())
                            .ifError(error -> Minions.LOGGER.warn("Could not parse docs tree {}: {}", id, error.message()));
                } else {
                    Identifier docId = Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().substring("docs/".length(), id.getPath().length() - ".json".length()));
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
    }

    @Override
    public void apply(Tuple<Map<Identifier, DocsEntry>, DocsTree> o, SharedState state) {
        docs = o.getA();
        tree = o.getB();
    }
}
