
package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {
    private boolean w;
    private String palabra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = this.getIntent();
        w = intent.getBooleanExtra("VICTORIA",false);
        palabra = intent.getStringExtra("PALABRA");

    }
    
    private String url(String palabra) {
            String direccion= "https://www.vilaweb.cat/paraulogic/?diec=" + palabra;
            try {
                URL definicion = new URL(direccion);
                BufferedReader lectura = new BufferedReader(new InputStreamReader(definicion.openStream()));
                StringBuffer texto = new StringBuffer();
                String line = lectura.readLine();
                while(line != null){
                    texto.append(line);
                    line = lectura.readLine();
                }
                if(texto.toString().equals("[]")){
                    return "Without definition";
                } else {
                    JSONObject jObject = new JSONObject(texto.toString());
                    String def = jObject.getString("d");
                    return def;
                }
            }catch(Exception e){
                System.out.println("Error de URL");
            }
            return "Link not found";
        }
}