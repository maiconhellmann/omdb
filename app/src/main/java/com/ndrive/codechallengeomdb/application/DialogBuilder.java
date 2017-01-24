package com.ndrive.codechallengeomdb.application;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.ndrive.codechallengeomdb.R;

/**
 * Dialog helper to easily show error, warning, advice or information dialog to the user.
 */
public class DialogBuilder {

    /**
     * Show error dialog
     */
    public static void e(Context context, Throwable t){
        Log.e(DialogBuilder.class.getSimpleName(), context.getString(R.string.erro),t);
        String message = t.getMessage() != null ? t.getMessage() : t.getClass().getName();
        showDialog(context, context.getString(R.string.erro), message);
    }

    /**
     * Show error dialog
     */
    public static void e(Context context,String title, Throwable t){
        Log.e(DialogBuilder.class.getSimpleName(), context.getString(R.string.erro),t);
        String message = t.getMessage() != null ? t.getMessage() : t.getClass().getName();
        showDialog(context, title, message);
    }

    /**
     * Show message/information dialog
     */
    public static void m(Context context,String title, String message){
        showDialog(context,title, message);
    }
    /**
     * Show message/information dialog
     */
    public static void m(Context context, String message){
        showDialog(context, context.getString(R.string.mensagem), message);
    }

    /**
     * Show message/information dialog
     */
    public static void m(Context context, int titleResourceString, int messageResourceString){
        m(context, context.getString(titleResourceString), context.getString(messageResourceString));
    }

    /**
     * Show warning/advice dialog
     */
    public static void w(Context context,String title, String message){
        showDialog(context,title, message);
    }

    /**
     * Show warning/advice dialog
     */
    public static void w(Context context,String message){
        showDialog(context,context.getString(R.string.aviso), message);
    }

    /**
     * Show warning/advice dialog
     */
    public static void w(Context context, int titleResourceString, int messageResourceString){
        w(context, context.getString(titleResourceString), context.getString(messageResourceString));
    }

    /**
     * Show a dialog with specific title and message
     */
    private static void showDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(title != null && !title.trim().equals(""))
            builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}

