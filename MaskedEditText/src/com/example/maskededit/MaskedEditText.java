package com.example.maskededit;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class MaskedEditText extends EditText {
	private static final char NUMBER_MASK = '9';
	private static final char ALPHA_MASK = 'A';
	private static final char ALPHANUMERIC_MASK = '*';
	private static final char CHARACTER_MASK = '?';
	private static final char ESCAPE_CHAR = '\\';

	private String mask;
	private String placeholder;

	public MaskedEditText(Context context) {
		this(context, "");
	}

	public MaskedEditText(Context context, String mask) {
		this(context, mask, ' ');
	}

	public MaskedEditText(Context context, String mask, char placeholder) {
		this(context, null, mask, placeholder);
	}

	public MaskedEditText(Context context, AttributeSet attr) {
		this(context, attr, "");
	}

	public MaskedEditText(Context context, AttributeSet attr, String mask) {
		this(context, attr, "", ' ');
	}

	public MaskedEditText(Context context, AttributeSet attr, String mask, char placeholder) {
		super(context, attr);
		
		TypedArray a = context.obtainStyledAttributes(attr, R.styleable.MaskedEditText);
		final int N = a.getIndexCount();
		
		for (int i = 0; i < N; ++i)
		{
		    int at = a.getIndex(i);
		    switch (at)
		    {
		        case R.styleable.MaskedEditText_mask:
		            mask = (mask.length() > 0 ? mask : a.getString(at));
		            break;
		        case R.styleable.MaskedEditText_placeholder:
		            placeholder = (a.getString(at).length() > 0 && placeholder == ' ' ? a.getString(at).charAt(0) : placeholder);
		            break;
		    }
		}
		
		a.recycle();

		this.mask = mask;
		this.placeholder = String.valueOf(placeholder);
		addTextChangedListener(new MaskTextWatcher());

		if (mask.length() > 0)
			setText(getText()); // sets the text to create the mask
	}
	
	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
		setText(getText());
	}
	
	public char getPlaceholder() {
		return placeholder.charAt(0);
	}

	public void setPlaceholder(char placeholder) {
		this.placeholder = String.valueOf(placeholder);
		setText(getText());
	}

	public Editable getText(boolean removeMask) {
		if (!removeMask) {
			return getText();
		} else {
			SpannableStringBuilder value = new SpannableStringBuilder(getText());
			stripMaskChars(value);

			return value;
		}
	}

	private void formatMask(Editable value) {
		int i = 0;
		int j = 0;
		int maskLength = 0;
		boolean treatNextCharAsLiteral = false;

		Object selection = new Object();
		value.setSpan(selection, Selection.getSelectionStart(value), Selection.getSelectionEnd(value), Spanned.SPAN_MARK_MARK);

		while (i < mask.length()) {
			if (!treatNextCharAsLiteral && isMaskChar(mask.charAt(i))) {
				if (j >= value.length()) {
					value.insert(j, placeholder);
					value.setSpan(new PlaceholderSpan(), j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					j++;
				} else if (!matchMask(mask.charAt(i), value.charAt(j))) {
					value.delete(j, j + 1);
					i--;
					maskLength--;
				} else {
					j++;
				}

				maskLength++;
			} else if (!treatNextCharAsLiteral && mask.charAt(i) == ESCAPE_CHAR) {
				treatNextCharAsLiteral = true;
			} else {
				value.insert(j, String.valueOf(mask.charAt(i)));
				value.setSpan(new LiteralSpan(), j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				treatNextCharAsLiteral = false;

				j++;
				maskLength++;
			}

			i++;
		}

		while (value.length() > maskLength) {
			int pos = value.length() - 1;
			value.delete(pos, pos + 1);
		}

		Selection.setSelection(value, value.getSpanStart(selection), value.getSpanEnd(selection));
		value.removeSpan(selection);
	}

	private void stripMaskChars(Editable value) {
		PlaceholderSpan[] pspans = value.getSpans(0, value.length(), PlaceholderSpan.class);
		LiteralSpan[] lspans = value.getSpans(0, value.length(), LiteralSpan.class);

		for (int k = 0; k < pspans.length; k++) {
			value.delete(value.getSpanStart(pspans[k]), value.getSpanEnd(pspans[k]));
		}

		for (int k = 0; k < lspans.length; k++) {
			value.delete(value.getSpanStart(lspans[k]), value.getSpanEnd(lspans[k]));
		}
	}

	private boolean matchMask(char mask, char value) {
		boolean ret = (mask == NUMBER_MASK && Character.isDigit(value));
		ret = ret || (mask == ALPHA_MASK && Character.isLetter(value));
		ret = ret || (mask == ALPHANUMERIC_MASK && (Character.isDigit(value) || Character.isLetter(value)));
		ret = ret || mask == CHARACTER_MASK;

		return ret;
	}

	private boolean isMaskChar(char mask) {
		switch (mask) {
		case NUMBER_MASK:
		case ALPHA_MASK:
		case ALPHANUMERIC_MASK:
		case CHARACTER_MASK:
			return true;
		}

		return false;
	}

	private class MaskTextWatcher implements TextWatcher {
		private boolean updating = false;

		@Override
		public void afterTextChanged(Editable s) {
			if (updating || mask.length() == 0)
				return;

			if (!updating) {
				updating = true;

				stripMaskChars(s);
				formatMask(s);

				updating = false;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}

	private class PlaceholderSpan {
		// this class is used just to keep track of placeholders in the text 
	}

	private class LiteralSpan {
		// this class is used just to keep track of literal chars in the text
	}
}
