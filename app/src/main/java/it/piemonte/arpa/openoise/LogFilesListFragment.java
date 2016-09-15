package it.piemonte.arpa.openoise;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;



import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LogFilesListFragment extends ListFragment implements OnItemClickListener {

    private String path;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_files_list, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        refresh();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int  position,long id) {
//        Toast.makeText(getActivity(), "Loading file", Toast.LENGTH_SHORT).show();



        final AlertDialog adbFilesList = new AlertDialog.Builder(this.getActivity()).create();

        View dialog_files_list = View.inflate(this.getActivity(), R.layout.dialog_files_list, null);
        final View dialog_file_delete = View.inflate(this.getActivity(), R.layout.dialog_file_delete, null);
        final Activity Act = this.getActivity();

        TextView OpenFile;
        TextView OpenWith;
        TextView ShareWith;
        TextView DeleteFile;
        OpenFile = (TextView) dialog_files_list.findViewById(R.id.dialog_files_list_open_file);
        ShareWith = (TextView) dialog_files_list.findViewById(R.id.dialog_files_list_share_with);
        OpenWith = (TextView) dialog_files_list.findViewById(R.id.dialog_files_list_open_with);
        DeleteFile = (TextView) dialog_files_list.findViewById(R.id.dialog_files_list_delete_file);


        OpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = (String) getListAdapter().getItem(position);

                Intent intent = new Intent(getActivity(), LogFilesReadActivity.class);
                intent.putExtra("LOG_FILE_NAME", filename);

                if (adbFilesList.isShowing()) adbFilesList.dismiss();

                startActivity(intent);

            }
        });

        OpenWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filename = (String) getListAdapter().getItem(position);
                path = Environment.getExternalStorageDirectory() + File.separator + "openoise";
                File file = new File(path,filename);

                String title = getResources().getString(R.string.choose_open);

                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                myIntent.setDataAndType(Uri.fromFile(file),"text/plain");
                Intent j = Intent.createChooser(myIntent, title);
                startActivity(j);

                if (adbFilesList.isShowing()) adbFilesList.dismiss();
            }
        });

        ShareWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String filename = (String) getListAdapter().getItem(position);
                path = Environment.getExternalStorageDirectory() + File.separator + "openoise";
                File file = new File(path,filename);

                String title = getResources().getString(R.string.choose_share);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_STREAM,  Uri.fromFile(file));
                startActivity(Intent.createChooser(share, title));

                if (adbFilesList.isShowing()) adbFilesList.dismiss();
            }
        });

        DeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adbFilesList.isShowing()) adbFilesList.dismiss();

                final String filename = (String) getListAdapter().getItem(position);
                TextView FileDelete;
                FileDelete = (TextView) dialog_file_delete.findViewById(R.id.dialog_file_delete);
                FileDelete.setText(filename);

                String cancel = getResources().getString(R.string.cancel);
                new AlertDialog.Builder(Act)

                        .setTitle(getResources().getString(R.string.app_name))
                        //.setMessage(disclaimerText)
                        .setView(dialog_file_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                path = Environment.getExternalStorageDirectory() + File.separator + "openoise";
                                File file = new File(path,filename);
                                file.delete();
                                refresh();

                            }
                        })
                        .setNeutralButton(cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            } })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();


            }
        });
//

        adbFilesList.setTitle(getResources().getString(R.string.app_name));
        adbFilesList.setView(dialog_files_list);
        adbFilesList.setIcon(R.mipmap.ic_launcher);
        adbFilesList.show();

    }

    private void refresh(){
        path = Environment.getExternalStorageDirectory() + File.separator + "openoise";

        // Read all files sorted into the values-array
        List values = new ArrayList();
        File dir = new File(path);
        if (!dir.canRead()) {
//            Toast.makeText(getActivity(), "Cannot read directory" + "\n" + "Maybe you have never recorded log files", Toast.LENGTH_SHORT).show();
        }

        String[] list = dir.list();

        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,android.R.id.text1,values);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }
}
