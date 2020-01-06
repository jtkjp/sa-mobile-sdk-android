package tv.superawesome.lib.saparentalgate;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

import tv.superawesome.sdk.publisher.base.R;

import java.util.Random;

/**
 * Class that defines a parental gate - basically a pop-up that, when enables, forces users to
 * respond to a mini-math quiz in order to proceed forward
 */
public class SAParentalGate {

    // the alert dialog
    private static AlertDialog dialog = null;

    // the pg listener
    private static SAParentalGate.Interface listener = new SAParentalGate.Interface() {
        @Override
        public void parentalGateOpen() {
        }

        @Override
        public void parentalGateSuccess() {
        }

        @Override
        public void parentalGateFailure() {
        }

        @Override
        public void parentalGateCancel() {
        }
    };

    /**
     * Method that shows the parental gate popup and fires the necessary events
     */
    public static void show(final Context c) {
        //九九の問題を作成
        int rnd1, rnd2 = 0;
        do {
            rnd1 = randomNumberBetween(c.getResources().getInteger(R.integer.videoads_rand_min), c.getResources().getInteger(R.integer.videoads_rand_max));
            rnd2 = randomNumberBetween(c.getResources().getInteger(R.integer.videoads_rand_min), c.getResources().getInteger(R.integer.videoads_rand_max));
        } while (rnd1 * rnd2 <= c.getResources().getInteger(R.integer.videoads_answer_min));

        final int num1 = rnd1;
        final int num2 = rnd2;

        listener.parentalGateOpen();

        // we have an alert dialog builder
        final Builder alert = new Builder(c, R.style.VideoAdsParentalGateDialogStyle);
        // set title and message
        alert.setTitle(R.string.videoads_challange_alertview_title);
        alert.setCancelable(false);
        alert.setMessage(c.getResources().getString(R.string.videoads_challange_alertview_message) + "\n" + num1 + " x " + num2 + " = ? ");

        // Set an EditText view to get user input
        final EditText input = new EditText(c);
        input.setInputType(2);
        input.setHint(R.string.videoads_challange_edittext_hint);
        alert.setView(input);

        // create positive dialog
        alert.setPositiveButton(R.string.videoads_challange_alertview_continuebutton_title, new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int userValue = -1;

                // try parsing the result and check for mathematical correctness
                try {
                    userValue = Integer.parseInt(input.getText().toString());
                    if (userValue == num1 * num2) {
                        SAParentalGate.listener.parentalGateSuccess();
                    } else {

                        // go on error way
                        Builder erroralert = new Builder(c, R.style.VideoAdsParentalGateDialogStyle);
                        erroralert.setTitle(R.string.videoads_error_alertview_title);
                        erroralert.setMessage(R.string.videoads_error_alertview_message);

                        // set button action
                        erroralert.setPositiveButton(R.string.videoads_error_alertview_cancelbutton_title, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SAParentalGate.listener.parentalGateFailure();

                                // dismiss this
                                dialog.dismiss();

                            }
                        });
                        erroralert.show();
                    }

                    // catch the number format error and calce the parental gate
                } catch (Exception e) {
                    SAParentalGate.listener.parentalGateCancel();
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
    public static void close() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    /**
     * Set the parental gate listener
     *
     * @param lis the listener instance
     */
    public static void setListener(SAParentalGate.Interface lis) {
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
        void parentalGateOpen();

        /**
         * Method part of SAParentalGateInterface called when the gate is successful
         */
        void parentalGateSuccess();

        /**
         * Method part of SAParentalGateInterface called when the gate is failed
         */
        void parentalGateFailure();

        /**
         * Method part of SAParentalGateInterface called when the gate is closed
         */
        void parentalGateCancel();
    }
}
