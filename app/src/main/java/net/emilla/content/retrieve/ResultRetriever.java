package net.emilla.content.retrieve;

import static net.emilla.BuildConfig.DEBUG;

import android.content.ActivityNotFoundException;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.ResultReceiver;
import net.emilla.run.MessageFailure;

/*internal*/ abstract class ResultRetriever<I, O, C extends ResultReceiver> {

    private static final String TAG = ResultRetriever.class.getSimpleName();

    protected final AssistActivity activity;
    private final ActivityResultLauncher<I> mLauncher;
    @Nullable
    @Deprecated // Todo: incorporate these in the launchers directly if possible.
    private C mReceiver;

    protected ResultRetriever(AssistActivity act, ActivityResultContract<I, O> contract) {
        this.activity = act;
        mLauncher = act.registerForActivityResult(contract, makeCallback());
    }

    protected abstract ResultCallback makeCallback();

    @Deprecated
    protected final boolean alreadyHas(C receiver) {
        if (mReceiver != null) {
            if (DEBUG) Log.d(TAG, "retrieve: result launcher already engaged. Not launching again.");
            return true;
        }
        mReceiver = receiver;
        return false;
    }

    protected final void launch(@Nullable I input) { try {
        mLauncher.launch(input);
    } catch (ActivityNotFoundException e) {
        activity.fail(new MessageFailure(activity, R.string.error, R.string.error_no_app));
    }}

    @Deprecated @Nullable
    protected /*open*/ C receiver() {
        return mReceiver;
    }

    @Deprecated
    protected /*open*/ void deleteReceiver() {
        mReceiver = null;
    }

    protected /*inner*/ abstract class ResultCallback implements ActivityResultCallback<O> {

        @Override
        public final void onActivityResult(O output) {
            C receiver = mReceiver;
            mReceiver = null;
            onActivityResult(output, receiver);
        }

        protected abstract void onActivityResult(O output, @Nullable C receiver);
    }
}
