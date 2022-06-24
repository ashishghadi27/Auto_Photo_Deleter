package com.root.autophotodeleter.fragments;

import static com.root.autophotodeleter.utils.Constants.CUSTOM;
import static com.root.autophotodeleter.utils.Constants.MONTH;
import static com.root.autophotodeleter.utils.Constants.SEVEN_DAYS;
import static com.root.autophotodeleter.utils.Constants.TODAY;
import static com.root.autophotodeleter.utils.Constants.WEEK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.root.autophotodeleter.R;
import com.root.autophotodeleter.adapters.ImageAdapter;
import com.root.autophotodeleter.operationHandlers.CameraPicsReader;
import com.root.autophotodeleter.utils.Constants;
import com.root.autophotodeleter.utils.GridSpacingItemDecoration;
import com.root.autophotodeleter.utils.PermissionManager;
import com.root.autophotodeleter.utils.TimePickerFragment;
import com.root.autophotodeleter.vo.FileInfoVO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class ImageLoadingFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<FileInfoVO> fileInfoList;
    private ImageAdapter adapter;
    private ImageView delete, filter;
    private TextView timePeriodLabel;
    private TextView noDataFoundText;
    private LocalDateTime startDateTime, endDateTime;
    private AlertDialog alertDialog, deleteAlertDialog;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int selection = 0;
    private ProgressDialog dataFetchDialog;
    private ProgressDialog deleteDialog;

    public ImageLoadingFragment() {
        // Required empty public constructor
    }

    public static ImageLoadingFragment newInstance() {
        return new ImageLoadingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_loading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selection = TODAY;
        timePeriodLabel = view.findViewById(R.id.timePeriodLabel);
        noDataFoundText = view.findViewById(R.id.noDataText);
        recyclerView = view.findViewById(R.id.recyclerView);
        delete = view.findViewById(R.id.delete);
        filter = view.findViewById(R.id.filter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 10,
                true));
        dataFetchDialog = getProgressDialog("Photos", "Fetching data...", getContext());
        deleteDialog = getProgressDialog("Deleting Data", "Please Do not close the app while deletion in progress", getContext());
        fetchDataAndLoadInRecycler(null, null);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(getActivity());
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet(getContext());
            }
        });
    }

    private void delete(View v) {
        deleteDialog.show();
        for (FileInfoVO fileInfo : fileInfoList) {
            boolean deleted = fileInfo.getFile().delete();
            Log.i("DELETED", deleted + "");
        }
        deleteDialog.dismiss();
        fileInfoList.clear();
        fetchDataAndLoadInRecycler(null, null);
    }

    private void fetchDataAndLoadInRecycler(LocalDateTime startDate,
                                            LocalDateTime endDate){
        dataFetchDialog.show();
        try {
            switch (selection){
                case SEVEN_DAYS:
                    fileInfoList = CameraPicsReader.getCameraFilesCapturedPastSevenDays();
                    timePeriodLabel.setText("Last Seven Days");
                    break;
                case WEEK:
                    fileInfoList = CameraPicsReader.getCameraFilesCapturedLastWeek();
                    timePeriodLabel.setText("Last Week");
                    break;
                case MONTH:
                    fileInfoList = CameraPicsReader.getCameraFilesCapturedLastMonth();
                    timePeriodLabel.setText("Last Month");
                    break;
                case CUSTOM:
                    if(startDate != null && endDate != null){
                        fileInfoList = CameraPicsReader
                                .getCameraFilesCapturedInSelectedTimeSpan(startDate, endDate);
                        String label = endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
                        label = label + "\n" + startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
                        timePeriodLabel.setText(label);
                    }
                    else {
                        fileInfoList = CameraPicsReader.getCameraFilesCapturedToday();
                        timePeriodLabel.setText("Today");
                    }
                    break;
                case TODAY:
                default:
                    fileInfoList = CameraPicsReader.getCameraFilesCapturedToday();
                    timePeriodLabel.setText("Today");
                    break;

            }
            dataFetchDialog.dismiss();
            if(fileInfoList != null && !fileInfoList.isEmpty()){
                delete.setVisibility(View.VISIBLE);
                noDataFoundText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                fileInfoList.sort((o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
                refreshRecyclerView();
            }
            else {
                delete.setVisibility(View.GONE);
                noDataFoundText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            Log.e("IOException", e.getCause() + "");
        }
    }

    private void refreshRecyclerView(){
        adapter = new ImageAdapter(fileInfoList, getContext());
        recyclerView.setAdapter(adapter);
    }

    public ProgressDialog getProgressDialog(String title, String message, Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    private void showBottomSheet(Context context){
        final BottomSheetDialog bottom_sheet_dialog = new BottomSheetDialog(context);
        View dialog = View.inflate(getContext(), R.layout.bottom_sheet, null);
        bottom_sheet_dialog.setContentView(dialog);

        TextView today, sevenDays, week, month, custom;

        today = dialog.findViewById(R.id.today);
        sevenDays = dialog.findViewById(R.id.sevenDays);
        week = dialog.findViewById(R.id.week);
        month = dialog.findViewById(R.id.month);
        custom = dialog.findViewById(R.id.custom);

        today.setOnClickListener(v -> {
            selection = TODAY;
            bottom_sheet_dialog.dismiss();
            fetchDataAndLoadInRecycler(null, null);
        });

        sevenDays.setOnClickListener(v -> {
            selection = SEVEN_DAYS;
            bottom_sheet_dialog.dismiss();
            fetchDataAndLoadInRecycler(null, null);
        });

        week.setOnClickListener(v -> {
            selection = WEEK;
            bottom_sheet_dialog.dismiss();
            fetchDataAndLoadInRecycler(null, null);
        });

        month.setOnClickListener(v -> {
            selection = MONTH;
            bottom_sheet_dialog.dismiss();
            fetchDataAndLoadInRecycler(null, null);
        });

        custom.setOnClickListener(v -> {
            selection = CUSTOM;
            bottom_sheet_dialog.dismiss();
            showDateTimeDialog(getActivity(), true);
            //fetchDataAndLoadInRecycler(null, null);
        });
        bottom_sheet_dialog.show();
    }

    private void showDatePicker(boolean isStart){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.i("MONTH", month + "");
                showTimePicker(isStart, year, month + 1,dayOfMonth);
                datePickerDialog.dismiss();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isStart, int year, int month, int dayOfMonth){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(isStart){
                    startDateTime = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute);
                    showDateTimeDialog(getActivity(), false);
                }
                else {
                    endDateTime = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute);
                    Log.i("Start Time: ", startDateTime.toString());
                    Log.i("End Time: ", endDateTime.toString());
                    processLocalDateTimeAndFetchData();
                }
                timePickerDialog.dismiss();
            }
        }, hour, minute, DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();

    }

    private void processLocalDateTimeAndFetchData(){
        if(startDateTime != null && endDateTime != null){
            if(startDateTime.isAfter(endDateTime)){
                fetchDataAndLoadInRecycler(startDateTime, endDateTime);
            }
            else {
                fetchDataAndLoadInRecycler(endDateTime, startDateTime);
            }
        }
    }

    public void showDateTimeDialog(Activity activity, boolean isStart){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialog = View.inflate(activity, R.layout.info_dialogue, null);
        TextView titleText = dialog.findViewById(R.id.title);
        TextView messageText = dialog.findViewById(R.id.message);
        Button button = dialog.findViewById(R.id.ok);
        if(isStart){
            messageText.setText("Choose From Date");
        }
        else {
            messageText.setText("Choose To Date");
        }
        button.setOnClickListener(v -> {
            alertDialog.dismiss();
            showDatePicker(isStart);
        });

        titleText.setText("Choose Date and Time");

        builder.setView(dialog);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();

    }

    public void showDeleteDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialog = View.inflate(activity, R.layout.delete_dialogue, null);
        Button cancel = dialog.findViewById(R.id.cancel);
        Button delete = dialog.findViewById(R.id.delete);

        cancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        delete.setOnClickListener(v -> {
            alertDialog.dismiss();
            delete(v);
        });

        builder.setView(dialog);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();

    }


}