package com.tkisor.uwtweaker.upd8r.compat.fancymenu;

import com.tkisor.uwtweaker.UwTweaker;
import com.tkisor.uwtweaker.upd8r.CurrentVersionObj;
import com.tkisor.uwtweaker.upd8r.LatestVersionObj;
import de.keksuccino.fancymenu.menu.placeholder.v2.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.menu.placeholder.v2.Placeholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Upd8rPlace {
    public static class CurrentVersion extends Placeholder {

        public CurrentVersion() {
            super("current_version");
        }

        @Override
        public String getReplacementFor(DeserializedPlaceholderString deserializedPlaceholderString) {
            return CurrentVersionObj.INSTANCE.getVersionFormat();
        }

        @Override
        public @Nullable List<String> getValueNames() {
            return null;
        }

        @Override
        public @NotNull String getDisplayName() {
            return "Current Version";
        }

        @Override
        public @Nullable List<String> getDescription() {
            return null;
        }

        @Override
        public String getCategory() {
            return UwTweaker.MOD_ID;
        }

        @Override
        public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
            DeserializedPlaceholderString dps = new DeserializedPlaceholderString();
            dps.placeholder = this.getIdentifier();
            return dps;
        }
    }

    public static class LatestVersion extends Placeholder {

        public LatestVersion() {
            super("latest_version");
        }

        @Override
        public String getReplacementFor(DeserializedPlaceholderString deserializedPlaceholderString) {
            return LatestVersionObj.INSTANCE.getVersionFormat();
        }

        @Override
        public @Nullable List<String> getValueNames() {
            return null;
        }

        @Override
        public @NotNull String getDisplayName() {
            return "Latest Version";
        }

        @Override
        public @Nullable List<String> getDescription() {
            return null;
        }

        @Override
        public String getCategory() {
            return UwTweaker.MOD_ID;
        }

        @Override
        public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
            DeserializedPlaceholderString dps = new DeserializedPlaceholderString();
            dps.placeholder = this.getIdentifier();
            return dps;
        }
    }
}
