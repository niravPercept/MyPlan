package percept.myplan.customviews;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;

public class TextWatcherAdapter implements TextWatcher {

	public interface TextWatcherListener {

		void onTextChanged(AutoCompleteTextView view, String text);

	}

	private final AutoCompleteTextView view;
	private final TextWatcherListener listener;

	public TextWatcherAdapter(AutoCompleteTextView editText, TextWatcherListener listener) {
		this.view = editText;
		this.listener = listener;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		listener.onTextChanged(view, s.toString());
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// pass
	}

	@Override
	public void afterTextChanged(Editable s) {
		// pass
	}

}