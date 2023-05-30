package com.example.diplomclear;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogCalender extends DialogFragment {

    public interface OnInputListener {
        void sendInput(CalendarInfo input,String UID);
    }

    public OnInputListener mOnInputListener;

    String dateBirth;

    java.util.Calendar dateAndTime = java.util.Calendar.getInstance();

    public void setDate() {
        new DatePickerDialog(getActivity(), d,
                dateAndTime.get(java.util.Calendar.YEAR),
                dateAndTime.get(java.util.Calendar.MONTH),
                dateAndTime.get(java.util.Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(java.util.Calendar.YEAR, year);
            dateAndTime.set(java.util.Calendar.MONTH, monthOfYear);
            dateAndTime.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };

    private void setInitialDate() {
//        IDTextDate.setText("Дата: " + DateUtils.formatDateTime(getActivity(),
//                dateAndTime.getTimeInMillis(),
//                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
//        ));

        dateBirth = DateUtils.formatDateTime(getActivity(), dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        IDTextDate.setText("Дата: " + dateBirth);

//        Toast toast = Toast.makeText(getActivity(), dateBirth+"",Toast.LENGTH_LONG);
//        toast.show();

    }

    public void setTime() {
        new TimePickerDialog(getActivity(), t,
                dateAndTime.get(java.util.Calendar.HOUR_OF_DAY),
                dateAndTime.get(java.util.Calendar.MINUTE), true)

                .show();
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(java.util.Calendar.MINUTE, minute);
            setInitialTime();
        }
    };

    private void setInitialTime() {

        IDTextTime.setText("Время: " + DateUtils.formatDateTime(getActivity(),
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME));

    }

    TextView IDTextDate;
    TextView IDTextTime;
    EditText IDRecordCalender;

    @SuppressLint("MissingInflatedId")
    @NonNull
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        String timeInfo = (getArguments().getString("time"));
        String dateInfo = (getArguments().getString("date"));
        String recordInfo = (getArguments().getString("record"));
        String UIDInfo = (getArguments().getString("UID"));

        inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.info_save, null, false);

        Button IDDeleteCalendar=myLayout.findViewById(R.id.IDDeleteCalendar);
        IDDeleteCalendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String time = "Delete";

                String date = dateBirth;
                date = date.replace("Дата: ", "");

                String record = IDRecordCalender.getText().toString();
                CalendarInfo input = new CalendarInfo(time, date, record);

                mOnInputListener.sendInput(input,UIDInfo);
                getDialog().dismiss();

            }});
        IDDeleteCalendar.setVisibility(View.GONE);

        if (UIDInfo.trim() == "New!") {

            LinearLayout IDDateSave = myLayout.findViewById(R.id.IDDateSave);
            IDTextDate = myLayout.findViewById(R.id.IDTextDate);
            IDDateSave.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  setDate();
                                              }
                                          }
            );

            LinearLayout IDTimeSave = myLayout.findViewById(R.id.IDTimeSave);
            IDTextTime = myLayout.findViewById(R.id.IDTextTime);
            IDTimeSave.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  setTime();
                                              }
                                          }
            );
            ImageView IDImageView = myLayout.findViewById(R.id.IDImageView);
            IDImageView.setOnClickListener(new View.OnClickListener() {
                                               public void onClick(View v) {
                                                   dismiss();
                                               }
                                           }
            );

            IDRecordCalender = myLayout.findViewById(R.id.IDRecordCalender);

            Button IDChangeSave = myLayout.findViewById(R.id.IDChangeSave);
            IDChangeSave.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    String time = IDTextTime.getText().toString();
                                                    time = time.replace("Время: ", "");

                                                    String date = dateBirth;
                                                    date = date.replace("Дата: ", "");

                                                    String record = IDRecordCalender.getText().toString();
                                                    CalendarInfo input = new CalendarInfo(time, date, record);

                                                    mOnInputListener.sendInput(input,UIDInfo);
                                                    getDialog().dismiss();
                                                }
                                            }
            );
//        String text = getArguments().getString("text");

            return myLayout;
        }
        else {
            IDDeleteCalendar.setVisibility(View.VISIBLE);

            dateBirth=dateInfo;

            LinearLayout IDDateSave = myLayout.findViewById(R.id.IDDateSave);
            IDTextDate = myLayout.findViewById(R.id.IDTextDate);
            IDDateSave.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  setDate();
                                              }
                                          }
            );

            LinearLayout IDTimeSave = myLayout.findViewById(R.id.IDTimeSave);
            IDTextTime = myLayout.findViewById(R.id.IDTextTime);
            IDTimeSave.setOnClickListener(new View.OnClickListener() {
                                              public void onClick(View v) {
                                                  setTime();
                                              }
                                          }
            );

            ImageView IDImageView = myLayout.findViewById(R.id.IDImageView);
            IDImageView.setOnClickListener(new View.OnClickListener() {
                                               public void onClick(View v) {
                                                   dismiss();
                                               }
                                           }
            );

            IDRecordCalender = myLayout.findViewById(R.id.IDRecordCalender);

            Button IDChangeSave = myLayout.findViewById(R.id.IDChangeSave);
            IDChangeSave.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    String time = IDTextTime.getText().toString();
                                                    time = time.replace("Время: ", "");

                                                    String date = dateBirth;
                                                    date = date.replace("Дата: ", "");

                                                    String record = IDRecordCalender.getText().toString();
                                                    CalendarInfo input = new CalendarInfo(time, date, record);

                                                    mOnInputListener.sendInput(input,UIDInfo);
                                                    getDialog().dismiss();
                                                }
                                            }
            );

            IDTextDate.setText(dateInfo);
            IDTextTime.setText(timeInfo);
            IDRecordCalender.setText(recordInfo);

//        String text = getArguments().getString("text");

            return myLayout;
        }
//        return builder
//                .setView(myLayout)
//                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener
                    = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: "
                    + e.getMessage());
        }
    }

}