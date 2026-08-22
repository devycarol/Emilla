package net.emilla;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static net.emilla.chime.Chime.ACT;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.activity.AssistActivity;
import net.emilla.activity.PassthroughActivity;
import net.emilla.chime.Chime;
import net.emilla.run.DialogRun;
import net.emilla.run.MessageFailure;
import net.emilla.run.TextGift;
import net.emilla.util.Intents;

public interface Feedback {
    void run(AssistActivity act);

    static Feedback succeed() {
        return act -> {
            act.finishAndRemoveTask();
            act.chime(Chime.SUCCEED);
        };
    }

    static Feedback succeed(Intent intent) {
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        return act -> {
            try {
                act.startActivity(intent);
                succeed().run(act);
            } catch (ActivityNotFoundException e) {
                Feedback.fail(R.string.error_no_app).run(act);
            }
        };
    }

    static Feedback fail(@StringRes int message) {
        return act -> fail(act.command().name, message).run(act);
    }

    static Feedback fail(@StringRes int title, @StringRes int message) {
        return act -> {
            new MessageFailure(act, title, message).run(act);
            act.chime(Chime.FAIL);
        };
    }

    static Feedback fail(CharSequence title, @StringRes int message) {
        return act -> {
            new MessageFailure(act, title, message).run(act);
            act.chime(Chime.FAIL);
        };
    }

    static Feedback fail(CharSequence title, CharSequence message) {
        return act -> {
            new MessageFailure(act, title, message).run(act);
            act.chime(Chime.FAIL);
        };
    }

    static Feedback silence() {
        return act -> {};
    }

    static Feedback give() {
        return act -> act.chime(Chime.ACT);
    }

    static Feedback give(Intent email) {
        return act -> {
            if (email.resolveActivity(act.getPackageManager()) != null) {
                act.finishAndRemoveTask();
                // todo: try/catch would be better here, but for some reason we
                //  need to do finishAndRemoveTask() first.
                act.startActivity(Intents.me(act, PassthroughActivity.class)
                    .putExtra(Intent.EXTRA_INTENT, email)
                    .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                );
                act.chime(Chime.ACT);
            } else {
                fail(R.string.error, R.string.error_no_app).run(act);
            }
        };
    }

    static Feedback fail(AlertDialog.Builder dialog) {
        return act -> {
            new DialogRun(dialog).run(act);
            act.chime(Chime.FAIL);
        };
    }

    static Feedback pend() {
        return act -> act.chime(Chime.PEND);
    }

    static Feedback offer(AlertDialog.Builder dialog) {
        return act -> {
            new DialogRun(dialog).run(act);
            act.chime(Chime.PEND);
        };
    }

    static Feedback giveText(@StringRes int text) {
        return act -> {
            var res = act.getResources();
            giveText(act.command().name, res.getString(text)).run(act);
        };
    }

    static Feedback giveText(CharSequence text) {
        return act -> giveText(act.command().name, text).run(act);
    }

    static Feedback giveText(CharSequence title, CharSequence text) {
        return act -> {
            new TextGift(act, title, text).run(act);
            act.chime(Chime.ACT);
        };
    }

    static Feedback selectInstruction() {
        return act -> {
            act.selectInstruction();
            act.chime(ACT);
        };
    }
}
