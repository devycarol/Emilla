package net.emilla.speech;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.box.ActionBox;
import net.emilla.action.box.TriResult;
import net.emilla.databinding.TtsFragmentBinding;
import net.emilla.util.Toasts;

import java.util.Locale;

public final class TtsFragment extends ActionBox {
    private TtsFragmentBinding mBinding;
    private TextToSpeech mTts;
    private String mQueuedPhrase = null;
    private TriResult mReady = TriResult.WAITING;

    public TtsFragment() {
        super(R.layout.tts_fragment);
    }

    public static TtsFragment newInstance() {
        return new TtsFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = TtsFragmentBinding.bind(view);
        mTts = new TextToSpeech(
            requireContext().getApplicationContext(),
            this::onInit
        );
        mBinding.getRoot().setOnClickListener((View __) -> {
            if (mTts != null) {
                mTts.stop();
            }
        });
    }

    private void onInit(int status) {
        if (status != TextToSpeech.SUCCESS
            || mTts.setLanguage(Locale.getDefault())
                < TextToSpeech.LANG_AVAILABLE
            //
        ) {
            mReady = TriResult.FAILURE;
            mBinding.getRoot().setText(R.string.tts_failed);
        } else {
            mReady = TriResult.SUCCESS;
            mBinding.getRoot().setText(R.string.tts_ready);
        }
        if (mQueuedPhrase != null) {
            say(mQueuedPhrase);
            mQueuedPhrase = null;
        }
    }

    public void say(String phrase) {
        switch (mReady) {
            case SUCCESS -> mTts.speak(
                phrase,
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            );
            case WAITING -> mQueuedPhrase = phrase;
            case FAILURE -> Toasts.show(requireContext(), phrase);
        }
    }

    @Override
    public void onDestroyView() {
        mQueuedPhrase = null;
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            mTts = null;
        }
        mReady = TriResult.WAITING;
        super.onDestroyView();
    }

    @Override
    public void instruct(@Nullable String instruction) {
        // do nothing
    }
}
