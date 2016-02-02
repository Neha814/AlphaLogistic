package functions;

import android.widget.EditText;

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


}
