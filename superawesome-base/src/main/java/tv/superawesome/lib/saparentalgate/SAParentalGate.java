package tv.superawesome.lib.saparentalgate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import java.util.Random;

import tv.superawesome.sdk.publisher.base.R;

/**
 * Class that defines a parental gate - basically a pop-up that, when enables, forces users to
 * respond to a mini-math quiz in order to proceed forward
 */
public class SAParentalGate {

    // the alert dialog
    private static AlertDialog dialog = null;

    // the pg listener
    private static SAParentalGate.Interface listener = new SAParentalGate.Interface() {
        @Override public void parentalGateOpen () {}
        @Override public void parentalGateSuccess() {}
        @Override public void parentalGateFailure() {}
        @Override public void parentalGateCancel() {}
    };

    /**
     * Method that shows the parental gate popup and fires the necessary events
     */
    public static void show(final Context c) {

        listener.parentalGateOpen();

        final int startNum = randomNumberBetween(c.getResources().getInteger(R.integer.videoads_rand_min), c.getResources().getInteger(R.integer.videoads_rand_max));
        final int endNum = randomNumberBetween(c.getResources().getInteger(R.integer.videoads_rand_min), c.getResources().getInteger(R.integer.videoads_rand_max));

        // we have an alert dialog builder
        final AlertDialog.Builder alert = new AlertDialog.Builder(c);
        // set title and message
        alert.setTitle(R.string.videoads_challange_alertview_title);
        alert.setCancelable(false);
        alert.setMessage(c.getResources().getString(R.string.videoads_challange_alertview_message) + startNum + " + " + endNum + " = ? ");

        // Set an EditText view to get user input
        final EditText input = new EditText(c);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        // create positive dialog
        alert.setPositiveButton(R.string.videoads_challange_alertview_continuebutton_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                int userValue = -1;

                // try parsing the result and check for mathematical correctness
                try {
                    userValue = Integer.parseInt(input.getText().toString());

                    if (userValue == (startNum + endNum)) {

                        listener.parentalGateSuccess();

                    } else {

                        // go on error way
                        AlertDialog.Builder erroralert = new android.app.AlertDialog.Builder(c);
                        erroralert.setTitle(R.string.videoads_error_alertview_title);
                        erroralert.setMessage(R.string.videoads_error_alertview_message);

                        // set button action
                        erroralert.setPositiveButton(R.string.videoads_error_alertview_cancelbutton_title, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                listener.parentalGateFailure();

                                // dismiss this
                                dialog.dismiss();

                            }
                        });
                        erroralert.show();
                    }

                }
                // catch the number format error and calce the parental gate
                catch (Exception e) {

                    listener.parentalGateCancel();

                }

                // dismiss
                dialog.dismiss();
            }
        });

        // create negative dialog
        alert.setNegativeButton(R.string.videoads_challange_alertview_cancelbutton_title, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                // dismiss
                dialog.dismiss();

                listener.parentalGateCancel();

            }
        });

        dialog = alert.create();
        dialog.show();
    }

    /**
     * Close method for the dialog
     */
    public static void close () {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    /**
     * Set the parental gate listener
     *
     * @param lis the listener instance
     */
    public static void setListener (SAParentalGate.Interface lis) {
        listener = lis != null ? lis : listener;
    }

    private static int randomNumberBetween(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    public interface Interface {
        /**
         * Method part of SAParentalGateInterface called when the gate is opened
         */
        void parentalGateOpen ();

        /**
         * Method part of SAParentalGateInterface called when the gate is successful
         */
        void parentalGateSuccess ();

        /**
         * Method part of SAParentalGateInterface called when the gate is failed
         */
        void parentalGateFailure ();

        /**
         * Method part of SAParentalGateInterface called when the gate is closed
         */
        void parentalGateCancel ();
    }
}
