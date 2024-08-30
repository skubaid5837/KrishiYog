package com.example.krishiyog;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class SimpleTextWatcher implements TextWatcher {
    private final EditText nextEditText;

    protected SimpleTextWatcher(EditText nextEditText) {
        this.nextEditText = nextEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //no action needed
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 1 && nextEditText != null) {
            nextEditText.requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // No action needed here
    }

}
