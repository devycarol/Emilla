package net.emilla.commands;

import androidx.annotation.StringRes;

public interface DataCommand {
@StringRes int dataHint();
/**
 * @param data is provided from the data field. Unlike `instruction`, this shouldn't be
 *             considered trim-safe.
 */
void runWithData(final String data);
/**
 * @param instruction is provided after in the command field after the command's name. It's
 *                    always space-trimmed should remain as such.
 * @param data is provided from the data field. Unlike `instruction`, this shouldn't be
 *             considered trim-safe.
 */
void runWithData(final String instruction, final String data);
}
