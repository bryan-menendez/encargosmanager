package com.example.protonmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import tools.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import modelo.*;

public class MainActivity extends AppCompatActivity {
    AppCompatActivity thisActivity = this;
    ArrayList<String> titulos;
    EncargoDAO encargoDAO = new EncargoDAO();

    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_ID = "id";
    private static final String KEY_TITULO = "titulo";
    //private static final String KEY_DESCRIPCION = "descripcion";
    //private static final String KEY_PRECIO = "precio";
    //private static final String KEY_COMPLETADO = "completado";
    private static final String BASE_URL = "http://localhost/encargos/";
    private ArrayList<HashMap<String, String>> listaEncargos;
    private ListView encargosListView;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        encargosListView = findViewById(R.id.lista_encargos);
        new FetchEncargosAsyncTask().execute();

        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnBorrarCompletados = findViewById(R.id.btnBorrarCompletados);

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchEncargosAsyncTask().execute();
            }
        });

        btnBorrarCompletados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteEncargosCompletadosAsyncTask().execute();
            }
        });

    }

    private class DeleteEncargosCompletadosAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //loading bar
            pDialog = new ProgressDialog(thisActivity);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            HttpJSONParser parser = new HttpJSONParser();
            JSONObject obj = parser.makeHTTPRequest(BASE_URL + "delcompletados.php", "GET", null);

            try
            {
                int success = obj.getInt(KEY_SUCCESS);
            }
            catch (Exception ex)
            {
                System.out.println("ERROR BACKGROUND DELETECOMPLETADOS TASK");
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute (String result)
        {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new FetchEncargosAsyncTask().execute();
                }
            });
        }
    }

    private class FetchEncargosAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //loading bar
            pDialog = new ProgressDialog(thisActivity);
            pDialog.setMessage("Cargando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            HttpJSONParser parser = new HttpJSONParser();
            JSONObject jObj = parser.makeHTTPRequest(BASE_URL + "getencargos.php", "GET", null);

            try
            {
                int success = jObj.getInt(KEY_SUCCESS);
                JSONArray encargos;

                if (success == 1)
                {
                    listaEncargos = new ArrayList<>();
                    encargos = jObj.getJSONArray(KEY_DATA);

                    for (int i = 0; i < encargos.length(); i++)
                    {
                        JSONObject encargo = encargos.getJSONObject(i);

                        Integer id = encargo.getInt(KEY_ID);
                        String titulo = encargo.getString(KEY_TITULO);
                        HashMap<String, String> map = new HashMap<>();
                        map.put(KEY_ID, id.toString());
                        map.put(KEY_TITULO, titulo);
                        listaEncargos.add(map);
                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println("ERROR BACKGROUND TASK");
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute (String result)
        {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateList();
                }
            });
        }
    }

    private void populateList()
    {
        ListAdapter adapter = new SimpleAdapter(
            thisActivity, listaEncargos, R.layout.list_item,
            new String[]{KEY_ID, KEY_TITULO}, new int[]{R.id.encargoId, R.id.encargoTitulo}
        );

        encargosListView.setAdapter(adapter);
        encargosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NetworkChecker.isNetworkAvailable(getApplicationContext()))
                {
                    String encargoid = ((TextView) view.findViewById(R.id.encargoId)).getText().toString();
                    Intent intent = new Intent(getApplicationContext(), DetalleEncargoActivity.class);
                    intent.putExtra(KEY_ID, encargoid);
                    intent.putExtra("action", "mod");
                    startActivityForResult(intent, 20);
                }
                else
                {
                    Toast.makeText(thisActivity, "Error al conectar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20) {
            new FetchEncargosAsyncTask().execute();
        }
    }

    public void nuevo(View view)
    {
        Intent intent = new Intent(thisActivity, DetalleEncargoActivity.class);
        intent.putExtra("action", "new");
        startActivityForResult(intent, 20);
    }
}
