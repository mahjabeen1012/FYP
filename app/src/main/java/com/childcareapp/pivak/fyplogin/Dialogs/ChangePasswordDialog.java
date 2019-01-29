package com.childcareapp.pivak.fyplogin.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.childcareapp.pivak.fyplogin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Console;
import java.net.PasswordAuthentication;

import static android.content.ContentValues.TAG;

public class ChangePasswordDialog extends AppCompatDialogFragment {

    TextView oldPassLable, newPassLabel, confirmPassLabel, oldPassError, newPassError, confirmPassError;
    EditText oldPass,newPass, confirmPass;
    Button cancelBtn, changePassBtn;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_password_dialog, null);
        bindXmlAttributes(view);
        textChangeLitener();
        /// cancel dialog box
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        /// change password
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!oldPass.getText().toString().equals("")) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(getArguments().getString("user"), oldPass.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        if (!oldPass.getText().toString().equals("") && !newPass.getText().toString().equals("") &&
                                                !confirmPass.getText().toString().equals("") && newPass.getText().toString()
                                                .equals(confirmPass.getText().toString()) && newPass.getText().toString().length() > 5) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            user.updatePassword(newPass.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "User password updated.");
                                                            }
                                                        }
                                                    });
                                            Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            dismiss();
                                        } else {
                                            displayError();
                                        }
                                    } else {
                                       // Toast.makeText(getActivity(), "Old password is incorrect", Toast.LENGTH_SHORT).show();
                                        oldPassError.setText("  wrong password");
                                        oldPassError.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
                else
                {
                    displayError();
                }
            }
        });
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        dialog.show();
        return dialog;
    }

    public void bindXmlAttributes(View view)
    {
        oldPass=view.findViewById(R.id.oldPassword);
        newPass=view.findViewById(R.id.newPassword);
        confirmPass=view.findViewById(R.id.newPaswordTwo);
        oldPassError=view.findViewById(R.id.oldPassErrorLable);
        newPassError=view.findViewById(R.id.newPassErrorLable);
        confirmPassError=view.findViewById(R.id.matchPassErrorLable);
        oldPassLable=view.findViewById(R.id.oldPassLabel);
        newPassLabel=view.findViewById(R.id.newPassLabel);
        confirmPassLabel=view.findViewById(R.id.matchPassLabel);
        cancelBtn=view.findViewById(R.id.CancelChangePassword);
        changePassBtn=view.findViewById(R.id.changePassword);
    }

    public void displayError()
    {
        if(oldPass.getText().toString().equals("")&& newPass.getText().toString().equals("") && confirmPass.getText().toString().equals(""))
        {
            //Toast.makeText(getActivity(), "Enter all the required fields", Toast.LENGTH_SHORT).show();
            oldPassError.setText("  required");
            oldPassError.setVisibility(View.VISIBLE);
            newPassError.setText("  required");
            newPassError.setVisibility(View.VISIBLE);
            confirmPassError.setText("  required");
            confirmPassError.setVisibility(View.VISIBLE);
            return;
        }
        else if(oldPass.getText().toString().equals("")&& !newPass.getText().toString().equals("") && !confirmPass.getText().toString().equals(""))
        {
            //Toast.makeText(getActivity(), "Enter old password", Toast.LENGTH_SHORT).show();
            oldPassError.setText("  required");
            oldPassError.setVisibility(View.VISIBLE);
            confirmPassError.setVisibility(View.INVISIBLE);
            return;
        }
        else if(!oldPass.getText().toString().equals("")&& newPass.getText().toString().equals("") && confirmPass.getText().toString().equals(""))
        {
            //Toast.makeText(getActivity(), "Enter and confirm new password", Toast.LENGTH_SHORT).show();
            oldPassError.setVisibility(View.INVISIBLE);
            newPassError.setText("  required");
            newPassError.setVisibility(View.VISIBLE);
            confirmPassError.setText("  required");
            confirmPassError.setVisibility(View.VISIBLE);
            return;
        }
        else if(!oldPass.getText().toString().equals("")&& newPass.getText().toString().equals("") && !confirmPass.getText().toString().equals(""))
        {
            //Toast.makeText(getActivity(), "Enter new password", Toast.LENGTH_SHORT).show();
            oldPassError.setVisibility(View.INVISIBLE);
            newPassError.setText("  required");
            newPassError.setVisibility(View.VISIBLE);
            confirmPassError.setVisibility(View.INVISIBLE);
            return;
        }
        else if(!oldPass.getText().toString().equals("")&& !newPass.getText().toString().equals("") && confirmPass.getText().toString().equals(""))
        {
            //Toast.makeText(getActivity(), "Re enter new password", Toast.LENGTH_SHORT).show();
            oldPassError.setVisibility(View.INVISIBLE);
            confirmPassError.setText("  required");
            confirmPassError.setVisibility(View.VISIBLE);
            return;
        }
        else if(oldPass.getText().toString().equals("")&& !newPass.getText().toString().equals("") && confirmPass.getText().toString().equals(""))
        {
            //Toast.makeText(getActivity(), "Enter old and confirm new password", Toast.LENGTH_SHORT).show();
            oldPassError.setText("  required");
            oldPassError.setVisibility(View.VISIBLE);
            confirmPassError.setText("  required");
            confirmPassError.setVisibility(View.VISIBLE);
            return;
        }
        else if(oldPass.getText().toString().equals("")&& newPass.getText().toString().equals("") && !confirmPass.getText().toString().equals(""))
        {
            //Toast.makeText(getActivity(), "Enter old and new password", Toast.LENGTH_SHORT).show();
            oldPassError.setText("  required");
            oldPassError.setVisibility(View.VISIBLE);
            newPassError.setText("  required");
            newPassError.setVisibility(View.VISIBLE);
            confirmPassError.setVisibility(View.INVISIBLE);
            return;
        }
//        else if(newPass.getText().toString().length() <6) {
//            Toast.makeText(getActivity(), "Enter atleast 6 characters for new password", Toast.LENGTH_LONG).show();
//            newPassLabel.setTextColor(getResources().getColor(R.color.red));
//            return;
//        }
        else if(!newPass.getText().toString().equals(confirmPass.getText().toString())) {
            //Toast.makeText(getActivity(), "Password don't match", Toast.LENGTH_LONG).show();
            oldPassError.setVisibility(View.INVISIBLE);
            confirmPassError.setText("  password don't match");
            confirmPassError.setVisibility(View.VISIBLE);
            return;
        }

    }

    public  void textChangeLitener() {

        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0)
                {
                    newPassError.setVisibility(View.VISIBLE);
                    newPassError.setText("  required");
                    newPassError.setTextColor(getResources().getColor(R.color.colorError));
                }
                else if (s.length() > 0 && s.length() < 6) {
                    newPassError.setVisibility(View.VISIBLE);
                    newPassError.setText("  weak");
                    newPassError.setTextColor(getResources().getColor(R.color.colorWarning));
                }
                else if (s.length() < 8) {
                    newPassError.setVisibility(View.VISIBLE);
                    newPassError.setText("  medium");
                    newPassError.setTextColor(getResources().getColor(R.color.otherBlur));
                }
                else {
                    newPassError.setText("  strong");
                    newPassError.setTextColor(getResources().getColor(R.color.colorSuccess));
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            newPassError.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        oldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() ==0 ) {
                    oldPassError.setText("  required");
                    oldPassError.setVisibility(View.VISIBLE);
                }
                else if(s.length()>0)
                {
                    oldPassError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassError.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() ==0) {
                    confirmPassError.setText("  required");
                    confirmPassError.setVisibility(View.VISIBLE);
                }
                else if(s.length()>0)
                {
                    confirmPassError.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
