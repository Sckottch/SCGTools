package com.sckottch.scgtools.utility.enums;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public enum UpgradeValidationResult {
    OK(null),
    INVALID("invalid"),
    WRONG_TOOL("wrong_tool"),
    WRONG_TIER("wrong_tier"),
    ALREADY_APPLIED("already_applied"),
    NO_SLOTS("no_slots"),
    // a receita não vale para esta ferramenta, mas não há nada a avisar ao jogador
    NOT_APPLICABLE(null);

    private final @Nullable Component message;

    UpgradeValidationResult(@Nullable String key) {
        this.message = key == null ? null : Component.translatable("gui.scgtools.forge." + key);
    }

    public @Nullable Component getMessage() {
        return message;
    }

    public boolean isOk() {
        return this == OK;
    }

    public boolean isReported() {
        return message != null;
    }
}
