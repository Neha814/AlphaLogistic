package functions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Neha on 12/29/2015.
 */
public class StringUtils {

    // to get edittext text length

    public static int  getLength(EditText et) {
        return et.getText().toString().trim().length();
    }

    // to get editText text
    public static String  getText(EditText et) {
        return et.getText().toString();
    }

    public static void showDialog(String msg, Context context) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    context).create();


            // Setting Dialog Message
            alertDialog.setMessage(msg);

            // Setting Icon to Dialog
            //	alertDialog.setIcon(R.drawable.browse);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Convert date String from one format to another

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e("TAG Date", "ParseException - dateFormat");
        }

        return outputDate;

    }

}
