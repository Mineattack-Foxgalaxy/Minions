package io.github.skippyall.minions;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record PlayerClipboardAttachment(UUID selectedMinion, String selectedInstruction) {
    public static final AttachmentType<PlayerClipboardAttachment> TYPE = AttachmentRegistry.create(Identifier.of(Minions.MOD_ID, "clipboard"));
}
