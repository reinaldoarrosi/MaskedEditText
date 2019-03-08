package com.github.reinaldoarrosi.maskededittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.*;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MaskedEditText extends EditText {
    private static final char NUMBER_MASK = '9';
    private static final char ALPHA_MASK = 'A';
    private static final char ALPHANUMERIC_MASK = '*';
    private static final char CHARACTER_MASK = '?';
    private static final char ESCAPE_CHAR = '\\';

    private String mask;
    private String placeholder;
    private List<TextWatcher> textWatchers = new ArrayList<TextWatcher>();
    private SelectionSpan selectionSpan;

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
        this(context, attr, mask, ' ');
    }

    public MaskedEditText(Context context, AttributeSet attr, String mask, char placeholder) {
        super(context, attr);

        TypedArray a = context.obtainStyledAttributes(attr, R.styleable.MaskedEditText);
        final int N = a.getIndexCount();

        for (int i = 0; i < N; ++i) {
            int at = a.getIndex(i);

            if (at == R.styleable.MaskedEditText_mask) {
                mask = (mask.length() > 0 ? mask : a.getString(at));
            } else if (at == R.styleable.MaskedEditText_placeholder) {
                placeholder = (a.getString(at).length() > 0 && placeholder == ' ' ? a.getString(at).charAt(0) : placeholder);
            }
        }

        a.recycle();

        // disable text suggestions since they can influence in the mask

        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | getInputType());



        this.mask = mask;
        this.placeholder = String.valueOf(placeholder);
        super.addTextChangedListener(new MaskTextWatcher());

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

    public Editable getTextWithoutPlaceholders() {
        SpannableStringBuilder value = new SpannableStringBuilder(getText());
        stripPlaceholders(value);

        return value;
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        this.textWatchers.add(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        this.textWatchers.remove(watcher);
    }

    private void formatMask(Editable value) {
        InputFilter[] inputFilters = value.getFilters();
        value.setFilters(new InputFilter[0]);

        int i = 0;
        int j = 0;
        int maskLength = 0;
        boolean treatNextCharAsLiteral = false;

        SelectionSpan selection = new SelectionSpan();
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
        selectionSpan = selection;

        value.setFilters(inputFilters);
    }

    private void stripMaskChars(Editable value) {
        PlaceholderSpan[] pSpans = value.getSpans(0, value.length(), PlaceholderSpan.class);
        LiteralSpan[] lSpans = value.getSpans(0, value.length(), LiteralSpan.class);

        for (PlaceholderSpan pSpan : pSpans) {
            value.delete(value.getSpanStart(pSpan), value.getSpanEnd(pSpan));
        }

        for (LiteralSpan lSpan : lSpans) {
            value.delete(value.getSpanStart(lSpan), value.getSpanEnd(lSpan));
        }
    }

    private void stripPlaceholders(Editable value) {
        PlaceholderSpan[] pSpans = value.getSpans(0, value.length(), PlaceholderSpan.class);

        for (PlaceholderSpan pSpan : pSpans) {
            value.delete(value.getSpanStart(pSpan), value.getSpanEnd(pSpan));
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

    private boolean hasHint() {
        CharSequence hint = getHint();
        return hint != null && hint.length() > 0;
    }

    private void invokeBeforeTextChanged(CharSequence s, int start, int count, int after) {
        for (TextWatcher t : this.textWatchers) {
            t.beforeTextChanged(s, start, count, after);
        }
    }

    private void invokeOnTextChanged(CharSequence s, int start, int before, int count) {
        for (TextWatcher t : this.textWatchers) {
            t.onTextChanged(s, start, before, count);
        }
    }

    private void invokeAfterTextChanged(Editable s) {
        for (TextWatcher t : this.textWatchers) {
            t.afterTextChanged(s);
        }
    }

    public void resetSelection() {
        if(selectionSpan != null) {
            SpannableStringBuilder value = new SpannableStringBuilder(getText());
            Selection.setSelection(value, value.getSpanStart(selectionSpan), value.getSpanEnd(selectionSpan));
        }
    }

    private class MaskTextWatcher implements TextWatcher {
        private boolean updating = false;
        private String originalValue;

        @Override
        public void afterTextChanged(Editable s) {
            if (updating || mask.length() == 0)
                return;

            if (!updating) {
                updating = true;

                stripMaskChars(s);

                if (s.length() <= 0 && hasHint()) {
                    setText("");
                } else {
                    formatMask(s);
                }

                updating = false;

                if(!originalValue.equals(getText(true).toString())) {
                    invokeAfterTextChanged(s);
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(updating) return;

            originalValue = getText(true).toString();
            invokeBeforeTextChanged(s, start, count, after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(updating) return;
            invokeOnTextChanged(s, start, before, count);
        }
    }

    private class PlaceholderSpan {
        // this class is used just to keep track of placeholders in the text
    }

    private class LiteralSpan {
        // this class is used just to keep track of literal chars in the text
    }

    private class SelectionSpan {
        // hold on to the section to reset cursor after text changes
    }
}
