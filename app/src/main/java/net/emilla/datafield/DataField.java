package net.emilla.datafield;

import androidx.annotation.StringRes;

public record DataField(@StringRes int hint) implements DataDirective {
}
