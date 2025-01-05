package net.emilla.content.retrieve;

import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;

import net.emilla.AssistActivity;
import net.emilla.content.receive.ResultReceiver;

abstract class ResultRetriever<I, O, R extends ResultReceiver> {

    private static final String TAG = FileRetriever.class.getSimpleName();

    protected final AssistActivity activity;
    protected final ActivityResultLauncher<I> launcher;
    @Deprecated // Todo: incorporate these in the launchers directly if possible.
    private R receiver;

    protected ResultRetriever(AssistActivity act, ActivityResultContract<I, O> contract) {
        this.activity = act;
        this.launcher = act.registerForActivityResult(contract, makeCallback());
    }

    protected abstract ResultCallback makeCallback();

    @Deprecated
    protected final boolean alreadyHas(R receiver) {
        if (this.receiver != null) {
            Log.d(TAG, "retrieve: result launcher already engaged. Not launching again.");
            return true;
        }
        this.receiver = receiver;
        return false;
    }

    @Deprecated
    protected R receiver() {
        return receiver;
    }

    @Deprecated
    protected void deleteReceiver() {
        receiver = null;
    }

    protected abstract class ResultCallback implements ActivityResultCallback<O> {

        @Override
        public final void onActivityResult(O output) {
            R receiver = ResultRetriever.this.receiver;
            ResultRetriever.this.receiver = null;
            onActivityResult(output, receiver);
        }

        protected abstract void onActivityResult(O output, R receiver);
    }
}
