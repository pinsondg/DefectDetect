package com.example.dpgra.defectdetect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import model.Pothole;
import model.PotholeList;

public class MoreMenuHandler implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, ChooserDialog.Result{

    private View mainView;
    private Fragment fragment;
    private File loadedFile;
    private List<OnFileLoadedListener> onFileLoadedListeners;

    public MoreMenuHandler(Fragment fragment, View mainView ) {
        this.fragment = fragment;
        this.mainView = mainView;
        loadedFile = null;
        onFileLoadedListeners = new ArrayList<OnFileLoadedListener>(0);
    }

    @Override
    public void onClick(View view) {
        showPopup(view);
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(mainView.getContext(), v);
        popup.inflate(R.menu.more_menu);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }


    public void addOnItemsLoadedListener( OnFileLoadedListener listener ) {
        onFileLoadedListeners.add(listener);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.clear:
                clearList();
                break;
            case R.id.export:
                if ( !exportList() ) {
                    Toast.makeText(fragment.getActivity(), "Error File not exported!", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.load:
                showFileChooser();
                break;
        }
        return false;
    }

    private void notifyListeners() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for ( OnFileLoadedListener listener : onFileLoadedListeners ) {
                    listener.onFileLoaded(loadedFile);
                }
            }
        };
        runnable.run();
    }
    private boolean exportList() {
        boolean flag = true;
        File file = null;
        OutputStreamWriter writer = null;
        FileOutputStream stream = null;
        try {
            file = getPublicAlbumStorageDir( "potholes.csv");
            stream = new FileOutputStream(file);
            writer = new OutputStreamWriter(stream);
            writer.write("ID, Lat, Lon, Size\n");
            for (Pothole pothole : PotholeList.getInstance() ) {
                String str = pothole.getId() + ", " + pothole.getLat() + ", " + pothole.getLon() + ", " + pothole.getSize() + "\n";
                writer.write(str);
            }
            writer.close();
        } catch ( IOException | NullPointerException e ) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File getPublicAlbumStorageDir(String name) throws IOException {
        // Get the directory for the user's public pictures directory.
        File file = null;
        File retFile = null;
        if (isExternalStorageWritable()) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
            if (!file.mkdirs()) {
                System.out.print("Not created");
            }
            retFile = new File(file, name);
        }
        return retFile;
    }

    private void clearList() {
        new AlertDialog.Builder(mainView.getContext())
                .setTitle("Alert")
                .setMessage("Do you want to clear the list?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //PotholeList.getInstance().clear();
                                ListView listView = mainView.findViewById(R.id.list_view);
                                PotholeListAdapter adapter = (PotholeListAdapter) listView.getAdapter();
                                //Clears all potholes displayed in list
                                clearResults(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", null).show();
    }

    private void clearResults(PotholeListAdapter adapter) {
        if ( fragment instanceof  PotholeListFragment ) {
            if(((PotholeListFragment) fragment).getNew_list().isEmpty()) {
                PotholeList.getInstance().clear();
                adapter.clear();
            } else {
                int n = 0;
                while(!((PotholeListFragment) fragment).getNew_list().isEmpty()) {
                    for (int j = 0; j < PotholeList.getInstance().size(); j++) {
                        if (((PotholeListFragment) fragment).getNew_list().get(n).getId().matches(PotholeList.getInstance().get(j).getId())) {
                            //adapter.remove(PotholeList.getInstance().get(j));
                            adapter.remove(((PotholeListFragment) fragment).getNew_list().get(n));
                            PotholeList.getInstance().remove(j);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void showFileChooser() {
        loadedFile = null;
        new ChooserDialog().with(mainView.getContext())
                .withStartFile(null)
                .withChosenListener(this)
                .build()
                .show();
    }

    @Override
    public void onChoosePath(String s, File file) {
        if ( s.endsWith(".csv") ) {
            loadedFile = file;
            if ( !loadFile() && loadedFile != null ) {
                Toast.makeText(fragment.getActivity(), "Formatting error: Could not load specified file!", Toast.LENGTH_SHORT).show();
            } else if ( loadedFile != null ){
                Toast.makeText(fragment.getActivity(), "File Loaded Successfully!", Toast.LENGTH_SHORT).show();
                notifyListeners();
            }
        }
    }

    private boolean loadFile() {
        boolean flag = true;
        BufferedReader reader;
        if ( loadedFile != null ) {
            try {
                reader = new BufferedReader( new FileReader(loadedFile) );
            } catch (FileNotFoundException e) {
                return false;
            }
            try {
                reader.readLine();
                String line = reader.readLine();
                while (line != null) {
                    if ( !createNewPothole(line) ) {
                        flag = false;
                        break;
                    }
                    line = reader.readLine();
                }
            } catch (IOException e ) {
                return false;
            }

        } else {
            flag = false;
        }
        return flag;
    }

    private boolean createNewPothole( String line ) {
        boolean flag = true;
        Pothole pothole = null;
        Double lat = null;
        Double lon = null;
        Integer size = null;
        String id = "";
        String[] splitLine = line.split(",");
        if ( splitLine.length == 4 ) {
            try {
                id = CameraFragment.getInstance().createPotholeId();
                lat = new Double(splitLine[1]);
                lon = new Double(splitLine[2]);
                size = new Integer(splitLine[3].trim());
                pothole = new Pothole( lat, lon, id, size );
                PotholeList.getInstance().add(pothole);
            } catch (NumberFormatException e) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }
}