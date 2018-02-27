package com.ko.efarming.util;

import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private static final String REGX_HASHTAG = "[`@!\\&×\\÷~#\\-\\+=\\[\\]{}\\^()<>/;:,.?'|\"\\*%$\\s+\\\\"
            + "•??£¢€°™®©¶¥??????????¿¡??¤??]"; // #$%^*()+=\-\[\]\';,.\/{}|":<>?~\\\\
    public static Pattern PATTERN_HASHTAG;

    static {
        PATTERN_HASHTAG = Pattern.compile(REGX_HASHTAG);
    }


    public static final String encodeToBase64(CharSequence content) {
        if (content == null) {
            return null;
        }
        byte[] bytes = Base64.encode(content.toString().getBytes(), Base64.DEFAULT);
        return new String(bytes).trim();
    }

    public static final String encodeToBase64(byte[] data) {
        byte[] bytes = Base64.encode(data, Base64.DEFAULT);
        return new String(bytes).trim();
    }

    public static final String decodeBase64(String base64String) {
        if (base64String == null) {
            return base64String;
        }

        try {
            return new String(Base64.decode(base64String, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return base64String;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().equals("") || value.trim().equals("null");
    }

    public static boolean isEmpty(CharSequence value) {
        return value == null || value.toString().equals("") || value.toString().equals("null");
    }

    public static boolean isEmpty(TextInputLayout inputLayout) {
        if (inputLayout != null) {
            return isEmpty(inputLayout.getEditText());
        }
        return true;
    }

    public static boolean isEmpty(EditText editText) {
        if (editText != null) {
            return isEmpty(editText.getText().toString());
        }

        return true;
    }

    public static boolean isEmpty(TextView textView) {
        if (textView != null) {
            return isEmpty(textView.getText());
        }

        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidWebUrl(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.WEB_URL.matcher(target).matches();
        }
    }

    public static boolean isValidHost(CharSequence target) {
        try {
            URI uri = new URI((String) target);
            if (uri.getHost() != null) {
                return true;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return false;

       /* if (target == null) {
            return false;
        } else {
            return Patterns.DOMAIN_NAME.matcher(target).;
        }*/
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
            value += "...";
        }
        return value;
    }

    public static boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isJsonData(String content) {
        return content != null && content.startsWith("{") && content.endsWith("}");
    }

    public final static String asUpperCaseFirstChar(final String target) {

        if ((target == null) || (target.length() == 0)) {
            return target; // You could omit this check and simply live with an
            // exception if you like
        }
        return Character.toUpperCase(target.charAt(0))
                + (target.length() > 1 ? target.substring(1) : "");
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    public static boolean isNullOrEmpty(CharSequence value) {
        return value == null || value.toString().equals("");
    }

    public static String arrayToString(ArrayList<String> array, String delimiter) {
        StringBuilder builder = new StringBuilder();
        if (array.size() > 0) {
            builder.append(array.get(0));
            for (int i = 1; i < array.size(); i++) {
                builder.append(delimiter);
                builder.append(array.get(i));
            }
        }
        return builder.toString();
    }

    public static ArrayList<String> stringToArray(String string) {
        return new ArrayList<>(Arrays.asList(string.split(",")));
    }

    public static String integerArrayToString(ArrayList<Integer> array, String delimiter) {
        StringBuilder builder = new StringBuilder();
        if (array.size() > 0) {
            builder.append(array.get(0));
            for (int i = 1; i < array.size(); i++) {
                builder.append(delimiter);
                builder.append(array.get(i));
            }
        }
        return builder.toString();
    }

    public static String capitalizeFirstLetter(String original) {
        if (original.length() == 0)
            return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String fromHtml(String string) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(string);
        }

        return String.valueOf(result);
    }

    public static Spannable doSizeSpanForFirstString(String firstString,
                                                     String lastString, int color) {
        String changeString = (firstString != null ? firstString : "");

        String totalString = changeString + lastString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new ForegroundColorSpan(color), 0, changeString.length(), 0);
        spanText.setSpan(new RelativeSizeSpan(1.5f), 0, changeString.length(), 0);
        return spanText;
    }

    public static Spannable doSizeSpanForSecondString(String firstString,
                                                      String lastString, int firstColor, int secondColor) {
        String changeString = (firstString != null ? firstString : "");
        String totalString = changeString + lastString;

        Spannable spanText = new SpannableString(totalString);

        spanText.setSpan(new ForegroundColorSpan(firstColor), 0, changeString.length(), 0);
        spanText.setSpan(new RelativeSizeSpan(1.5f), 0, changeString.length(), 0);

        spanText.setSpan(new ForegroundColorSpan(secondColor), String.valueOf(firstString)
                .length(), totalString.length(), 0);

        spanText.setSpan(new RelativeSizeSpan(1.0f), String.valueOf(firstString)
                .length(), totalString.length(), 0);

        return spanText;
    }

    public static void reduceMarginsInTabs(TabLayout tabLayout, int marginOffset) {
        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            for (int i = 0; i < ((ViewGroup) tabStrip).getChildCount(); i++) {
                View tabView = tabStripGroup.getChildAt(i);
                if (tabView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) tabView.getLayoutParams()).leftMargin = marginOffset;
                    ((ViewGroup.MarginLayoutParams) tabView.getLayoutParams()).rightMargin = marginOffset;
                }
            }
            tabLayout.requestLayout();
        }
    }

    public static final InputFilter[] filter = {new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            StringBuilder builder = new StringBuilder(dest);
            builder.replace(dstart, dend, source
                    .subSequence(start, end).toString());
            if (!builder.toString().matches(
                    "(([1-9]{1})([0-9]{0,4})?(\\.)?)?([0-9]{0,2})?"

            )) {
                if (source.length() == 0)
                    return dest.subSequence(dstart, dend);
                return "";
            }
            return null;
        }
    }
    };
}

