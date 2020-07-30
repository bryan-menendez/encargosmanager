package com.example.protonmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import modelo.*;
import tools.*;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleEncargoActivity extends AppCompatActivity
{
    AppCompatActivity thisActivitiy = this;
    private String action; //0 - NEW ; 1 - MODIFY
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_ID = "id";
    private static final String KEY_TITULO = "titulo";
    private static final String KEY_DESCRIPCION = "descripcion";
    private static final String KEY_PRECIO = "precio";
    private static final String KEY_COMPLETADO = "completado";
    private static final String BASE_URL = "http://localhost/encargos/";

    private String encargoid;
    private EditText tituloET;
    private EditText descripcionET;
    private EditText precioET;
    private CheckBox completadoCheck;

    private String titulo;
    private String descripcion;
    private int precio;
    private boolean completado;

    private Button deleteButton;
    private Button saveButton;
    private int success;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_encargo);
        Intent intent = getIntent();
        action = intent.getStringExtra("action");

        deleteButton = findViewById(R.id.btnEliminar);
        saveButton = findViewById(R.id.btnGuardar);
        tituloET = findViewById(R.id.tituloText);
        descripcionET = findViewById(R.id.descripcionText);
        precioET = findViewById(R.id.precioText);
        completadoCheck = findViewById(R.id.completeCheck);

        if (action.equals("new")) //NEW ENCARGO
        {
            deleteButton.setVisibility(View.INVISIBLE);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkChecker.isNetworkAvailable(getApplicationContext()))
                        addEncargo();
                    else
                        Toast.makeText(thisActivitiy, "No es posible conectarse", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else //MOD ENCARGO
        {
            encargoid = intent.getStringExtra(KEY_ID);
            new FetchEncargoAsyncTask().execute();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (NetworkChecker.isNetworkAvailable(getApplicationContext()))
                        updateEncargo();
                    else
                        Toast.makeText(thisActivitiy, "No es posible conectarse", Toast.LENGTH_SHORT).show();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    confirmDelete();
                }
            });


        }
    }

    //gets the values from the selected item and sets them on the ui
    private class FetchEncargoAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialog = new ProgressDialog(thisActivitiy);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpJSONParser parser = new HttpJSONParser();
            Map<String, String> params = new HashMap<>();
            params.put(KEY_ID, encargoid);
            JSONObject jObj = parser.makeHTTPRequest(BASE_URL + "getencargo.php", "GET", params);

            try
            {
                int success = jObj.getInt(KEY_SUCCESS);
                JSONObject encargo;

                if (success == 1)
                {
                    encargo = jObj.getJSONObject(KEY_DATA);
                    titulo = encargo.getString(KEY_TITULO);
                    descripcion = encargo.getString(KEY_DESCRIPCION);
                    precio = encargo.getInt(KEY_PRECIO);

                    if (encargo.getString(KEY_COMPLETADO).equals("1"))
                        completado = true;
                    else
                        completado = false;
                }
            }
            catch (Exception ex)
            {
                System.out.println("ERROR PARSEANDO EN BACKGROUND");
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    //Populate the Edit Texts once the network activity is finished executing
                    tituloET.setText(titulo);
                    descripcionET.setText(descripcion);
                    precioET.setText(Integer.toString(precio));
                    completadoCheck.setChecked(completado);
                }
            });
        }
    }

    //responds when the delete button is pressed
    private class DeleteEncargoAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialog = new ProgressDialog(thisActivitiy);
            pDialog.setMessage("Eliminando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpJSONParser parser = new HttpJSONParser();
            Map<String, String> params = new HashMap<>();

            params.put(KEY_ID, encargoid);
            JSONObject obj = parser.makeHTTPRequest(BASE_URL + "delencargo.php", "POST", params);

            try {
                success = obj.getInt(KEY_SUCCESS);
            }
            catch (Exception ex)
            {
                System.out.println("ERROR PARSEANDO EN DELETE BACKGROUND");
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (success == 1)
                    {
                        Toast.makeText(thisActivitiy, "Encargo eliminado", Toast.LENGTH_SHORT);
                        Intent i = getIntent();
                        setResult(20,i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(thisActivitiy, "Error eliminando el encargo", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void confirmDelete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivitiy);
        builder.setMessage("¿Esta seguro que desea eliminar este encargo?");
        builder.setPositiveButton("Borrar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (NetworkChecker.isNetworkAvailable(getApplicationContext()))
                            new DeleteEncargoAsyncTask().execute();
                        else
                            Toast.makeText(thisActivitiy, "No se pudo conectar a internet", Toast.LENGTH_SHORT);
                    }
                }
        );
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateEncargo()
    {
        if (tituloET.getText().toString().equals(""))
            tituloET.setText("Sin titulo");
        if (descripcionET.getText().toString().equals(""))
            descripcionET.setText("Sin descripcion");
        if (precioET.getText().toString().equals(""))
            precioET.setText("0");

        titulo = tituloET.getText().toString();
        descripcion = descripcionET.getText().toString();
        precio = Integer.parseInt(precioET.getText().toString());
        completado = completadoCheck.isChecked();

        new UpdateEncargoAsyncTask().execute();
    }

    private void addEncargo()
    {
        if (tituloET.getText().toString().equals(""))
            tituloET.setText("Sin titulo");
        if (descripcionET.getText().toString().equals(""))
            descripcionET.setText("Sin descripcion");
        if (precioET.getText().toString().equals(""))
            precioET.setText("0");

        titulo = tituloET.getText().toString();
        descripcion = descripcionET.getText().toString();
        precio = Integer.parseInt(precioET.getText().toString());
        completado = completadoCheck.isChecked();

        new AddEncargoAsyncTask().execute();
    }

    private class AddEncargoAsyncTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialog = new ProgressDialog(thisActivitiy);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpJSONParser parser = new HttpJSONParser();
            Map<String,String> params = new HashMap<>();

            params.put(KEY_TITULO, titulo);
            params.put(KEY_DESCRIPCION, descripcion);
            params.put(KEY_PRECIO, Integer.toString(precio));
            if (completado)
                params.put(KEY_COMPLETADO, "1");
            else
                params.put(KEY_COMPLETADO, "0");

            JSONObject obj = parser.makeHTTPRequest(BASE_URL + "addencargo.php", "POST", params);
            try {
                success = obj.getInt(KEY_SUCCESS);
            }
            catch (Exception ex)
            {
                System.out.println("ERROR PARSEANDO EN INSERT BACKGROUND");
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (success == 1)
                    {
                        Toast.makeText(thisActivitiy, "Encargo añadido", Toast.LENGTH_SHORT);
                        Intent i = getIntent();
                        setResult(20,i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(thisActivitiy, "Error añadiendo el encargo", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private class UpdateEncargoAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            pDialog = new ProgressDialog(thisActivitiy);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... param)
        {
            HttpJSONParser parser = new HttpJSONParser();
            Map<String,String> params = new HashMap<>();

            params.put(KEY_ID, encargoid);
            params.put(KEY_TITULO, titulo);
            params.put(KEY_DESCRIPCION, descripcion);
            params.put(KEY_PRECIO, Integer.toString(precio));

            if (completado)
                params.put(KEY_COMPLETADO, "1");
            else
                params.put(KEY_COMPLETADO, "0");

            JSONObject obj = parser.makeHTTPRequest(BASE_URL + "editarencargo.php", "POST", params);

            try {
                success = obj.getInt(KEY_SUCCESS);
            }
            catch (Exception ex)
            {
                System.out.println("ERROR PARSEANDO EN UPDATE BACKGROUND");
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result)
        {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (success == 1)
                    {
                        Toast.makeText(thisActivitiy, "Encargo actualizado", Toast.LENGTH_SHORT);
                        Intent i = getIntent();
                        setResult(20,i);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(thisActivitiy, "Error actualizando el encargo", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    public void cancelar(View view)
    {
        Intent i = getIntent();
        setResult(20,i);
        finish();
    }

}
