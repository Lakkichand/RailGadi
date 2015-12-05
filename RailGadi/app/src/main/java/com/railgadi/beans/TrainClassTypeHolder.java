package com.railgadi.beans;

import android.widget.TextView;

public class TrainClassTypeHolder {

    private TextView button;
    private boolean activation;

    public void setButton(TextView button) {
        this.button = button;
    }

    public void setActivation(boolean activation) {
        this.activation = activation;
    }

    public TextView getButton() {
        return this.button;
    }

    public boolean getActivation() {
        return this.activation;
    }
}
