package ws.splash.projetcandidature.model;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    //This method filter the input value
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend);

            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart);

            //This line allow Negative values
            if (newVal.equalsIgnoreCase("-") && min < 0)
                return null;

            int input = Integer.parseInt(newVal);
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return "";
    }

    //Method to check if a double is between two integer
    private boolean isInRange(int a, int b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}