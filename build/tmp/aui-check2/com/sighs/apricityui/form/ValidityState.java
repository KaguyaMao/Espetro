/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.form;

public final class ValidityState {
    public final boolean badInput;
    public final boolean customError;
    public final boolean patternMismatch;
    public final boolean rangeOverflow;
    public final boolean rangeUnderflow;
    public final boolean stepMismatch;
    public final boolean tooLong;
    public final boolean tooShort;
    public final boolean typeMismatch;
    public final boolean valueMissing;
    public final boolean valid;

    public ValidityState(boolean badInput, boolean customError, boolean patternMismatch, boolean rangeOverflow, boolean rangeUnderflow, boolean stepMismatch, boolean tooLong, boolean tooShort, boolean typeMismatch, boolean valueMissing, boolean valid) {
        this.badInput = badInput;
        this.customError = customError;
        this.patternMismatch = patternMismatch;
        this.rangeOverflow = rangeOverflow;
        this.rangeUnderflow = rangeUnderflow;
        this.stepMismatch = stepMismatch;
        this.tooLong = tooLong;
        this.tooShort = tooShort;
        this.typeMismatch = typeMismatch;
        this.valueMissing = valueMissing;
        this.valid = valid;
    }

    public boolean isBadInput() {
        return this.badInput;
    }

    public boolean isCustomError() {
        return this.customError;
    }

    public boolean isPatternMismatch() {
        return this.patternMismatch;
    }

    public boolean isRangeOverflow() {
        return this.rangeOverflow;
    }

    public boolean isRangeUnderflow() {
        return this.rangeUnderflow;
    }

    public boolean isStepMismatch() {
        return this.stepMismatch;
    }

    public boolean isTooLong() {
        return this.tooLong;
    }

    public boolean isTooShort() {
        return this.tooShort;
    }

    public boolean isTypeMismatch() {
        return this.typeMismatch;
    }

    public boolean isValueMissing() {
        return this.valueMissing;
    }

    public boolean isValid() {
        return this.valid;
    }
}

