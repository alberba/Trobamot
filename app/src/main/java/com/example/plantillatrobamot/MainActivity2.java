package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MainActivity2 extends AppCompatActivity {
    private boolean w;
    private String palabra, restricciones, posiblesSoluciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = this.getIntent();
        w = intent.getBooleanExtra("VICTORIA",false);
        palabra = intent.getStringExtra("PALABRA");
        restricciones = intent.getStringExtra("RESTRICCIONES");
        posiblesSoluciones = intent.getStringExtra("POSIBLES_SOLUCIONES");


        // Comprobar si ha ganado
        pantallaFinal();

    }

    private void pantallaFinal() {
        TextView textoFinal = findViewById(R.id.textoFinal);
        TextView textoPalabra = findViewById(R.id.textoPalabra);

        textoPalabra.setText(palabra);
        if (w == true) {
            textoFinal.setText("Enhorabona!");
        } else {
            textoFinal.setText("Oh oh oh oh...");
            modificarStrings();
        }
        Thread thread = new Thread ( new Runnable () {
            @Override
            public void run () {
                try {
                    TextView textodef=findViewById(R.id.textoDefPalabra);
                    textodef.setText(Html.fromHtml(AgafaHTML().toString()));

                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    private void modificarStrings() {
        // Dar formato al texto (negrita)
        SpannableString ss = new SpannableString(posiblesSoluciones);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(boldSpan, 0, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Poner texto
        TextView posible = findViewById(R.id.textoPalPos);
        posible.setText(ss);

        SpannableString ss2 = new SpannableString(restricciones);
        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        ss2.setSpan(boldSpan2, 0, restricciones.indexOf(":"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Poner texto
        TextView restric = findViewById(R.id.textoRestricciones);
        restric.setText(ss2);

    }

    private String AgafaHTML() {
        String direccion = "https://www.vilaweb.cat/paraulogic/?diec=" + palabra;

        try {
            // Enlace a internet
            URL definicion = new URL(direccion);
            BufferedReader in = new BufferedReader(new InputStreamReader(definicion.openStream()));
            StringBuffer texto = new StringBuffer();
            String line = in.readLine();
            while(line != null) {
                texto.append(line);
                line = in.readLine();
            }
            if (texto.toString().equals("[]")) {
                //in.close();
                return "Without definition";
            } else {
                JSONObject jObject = new JSONObject(texto.toString());
                String def = jObject.getString("d");
                //in.close();
                return def;
            }
        } catch (Exception e) {
            return "Link not found";
        }
    }


}