package net.emilla.content;

import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;

import net.emilla.AssistActivity;

abstract class ResultContract<I, O, R extends Receiver> {

    private static final String TAG = FileContract.class.getSimpleName();

    protected final AssistActivity activity;
    protected final ActivityResultLauncher<I> launcher;
    @Deprecated // Todo: incorporate these in the launchers directly if possible.
    private R receiver;

    protected ResultContract(AssistActivity act, ActivityResultContract<I, O> contract) {
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
            R receiver = ResultContract.this.receiver;
            ResultContract.this.receiver = null;
            onActivityResult(output, receiver);
        }

        protected abstract void onActivityResult(O output, R receiver);
    }
}
