package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


@SuppressWarnings({"rawtypes", "unchecked"})
public class MainActivity extends AppCompatActivity {
    // Variables de lògica del joc
    private final int lengthWord = 5;
    private final int maxTry = 6;
    private String palabraSolucion = "pasta";
    private int numIntentos = 0;
    private int numLetra = 0;
    private int contadorPalabrasDiccionario = 0;
    // Si el valor es false, se pasará del árbol 0 al 1. Si el valor es true, del 1 al 0.
    private boolean arbolSwitcher = false;

    // mapping con la información de las letras y su posición en la palabra original
    UnsortedArrayMapping<Character, UnsortedLinkedListSet<Integer>> letras;
    // mapping con las palabras sin acento (keys) y con acento (values) del diccionario
    HashMap<String, String> diccionario = new HashMap<>();
    // mapping similar a diccionario pero solo con las palabras que pueden ser solución
    TreeSet<String> posiblesSoluciones = new TreeSet<>();



    // Variables de construcció de la interfície
    public static String grayColor = "#D9E1E8";
    private int widthDisplay;
    private int heightDisplay;
    private final int ID_TEXT_SOL = 777;
    private final int textViewSize = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object to store display information
        DisplayMetrics metrics = new DisplayMetrics();
        // Get display information
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        crearInterficie();
        crearDiccionario();
        initPosiblesSoluciones();
    }


    @Override
    protected void onStart() {
        super.onStart();
        hideSystemUI();
    }

    private void crearInterficie() {
        crearGraella();
        crearTeclat();

        // crear TextView de Soluciones
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        TextView stringSoluciones = new TextView(this);
        stringSoluciones.setId(ID_TEXT_SOL);
        String textoSoluciones = "Hi ha " + contadorPalabrasDiccionario + " Solucions Possibles.";
        stringSoluciones.setText(textoSoluciones);
        stringSoluciones.setTextColor(Color.BLACK);
        stringSoluciones.setTextSize(20);

        stringSoluciones.setX((float)(widthDisplay - (textViewSize*lengthWord+1))/2);
        stringSoluciones.setY((float)heightDisplay/2 + textViewSize);

        constraintLayout.addView(stringSoluciones);
    }

    // Método que crea el Mapping de las palabras del diccionario que tienen la misma longitud que lenghtWord
    public void crearDiccionario() {
        // abrir fichero
        try {

            InputStream is = getResources().openRawResource(R.raw.paraules);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));

            // leer primera línea de fichero
            String fileWord;
            String [] fileWordArray = new String [2];
            // bucle de lectura del fichero
            while ((fileWord = r.readLine()) != null) {
                // obtener palabra con/sin acentos
                fileWordArray = fileWord.split(";");

                // comprobar si la palabra tiene la longitud adecuada
                if (fileWordArray[0].length() == lengthWord){
                    // añadir palabra al diccionario
                    diccionario.put(fileWordArray[1], fileWordArray[0]);
                    contadorPalabrasDiccionario++;
                }

            }
            TextView stringSoluciones = findViewById(ID_TEXT_SOL);
            String textSoluciones = "Hi ha " + contadorPalabrasDiccionario + " Solucions Possibles.";
            stringSoluciones.setText(textSoluciones);
            // cerrar fichero
            r.close();
            is.close();
        } catch (IOException e) {
            // Control de Excepción
            Context context = getApplicationContext() ;
            CharSequence text = "Error al crear el fichero";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private void crearGraella() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);

        // Definir les característiques del "pinzell"
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));


        TextView [][] textViewGraella = new TextView [maxTry][lengthWord];
        // Crear un TextView per cada posició de la graella
        for(int i = 0; i < maxTry; i++){
            for(int j = 0; j < lengthWord; j++){
                textViewGraella[i][j] = new TextView(this);
                textViewGraella[i][j].setBackground(gd);
                textViewGraella[i][j].setId(i*lengthWord+j);
                textViewGraella[i][j].setWidth(textViewSize);
                textViewGraella[i][j].setHeight(textViewSize);
                textViewGraella[i][j].setX((float) widthDisplay/2 - (float)textViewSize/2 - (float)(lengthWord/2 - j)*textViewSize);
                textViewGraella[i][j].setY((float)heightDisplay/3 - textViewSize/2 - (maxTry/2 - i)*textViewSize);
                textViewGraella[i][j].setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                textViewGraella[i][j].setTextColor(Color.GRAY);
                textViewGraella[i][j].setTextSize(30);
                textViewGraella[i][j].setText("");

                // Afegir el TextView al layout
                constraintLayout.addView(textViewGraella[i][j]);
            }

        }
        // Pintar la primera casilla
        GradientDrawable gdOrange = new GradientDrawable();
        gdOrange.setCornerRadius(5);
        gdOrange.setStroke(3, Color.parseColor("#FF8000"));
        textViewGraella[0][0].setBackground(gdOrange);

    }

    @SuppressLint("SetTextI18n")
    private void crearTeclat() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        // Teclat
        final int ALFABET_SIZE = 27;
        letras = new UnsortedArrayMapping<>(ALFABET_SIZE);
        // crear teclat
        int offsetH = 8 * heightDisplay / 11;
        int offsetW;
        Button botonLetra;

        String substrPalSol = palabraSolucion;
        char c;

        // inicializar letras teclado
        for (int i = 0; i < ALFABET_SIZE; i++) {
            if (i != 26)
                //letras.put((char) ('A' + i), new UnsortedLinkedListSet<Integer>());
                c = (char) ('a' + i);
            else
                //letras.put('Ç', new UnsortedLinkedListSet<Integer>());
                c = 'ç';

            // añadir conjunto con las posiciones de la letra en la palabra
            if (substrPalSol.contains("" + c)) {
                // la palabra contiene al menos una ocurrencia del caracter c
                int j = 0;
                // crear conjunto vacío
                UnsortedLinkedListSet <Integer> posSet = new UnsortedLinkedListSet<Integer>();
                // añadir posiciones en la palabra (primer índice = 1)
                while (substrPalSol.substring(j).contains("" + c)) {
                    j += substrPalSol.substring(j).indexOf(c) + 1;
                    posSet.add(j);
                }
                // resetear pal2 al valor de la palabra a adivinar
                substrPalSol = palabraSolucion;
                // añadir conjunto
                letras.put(c, posSet);
            } else {
                // el conjunto de posiciones es nulo
                letras.put(c, null);
            }
        }

        // bucle con iterator
        int i = 0;
        offsetW = widthDisplay / 25;
        Iterator it = letras.iterator();

        // bucle de generación del teclado
        while (it.hasNext()) {
            UnsortedArrayMapping.Pair p = (UnsortedArrayMapping.Pair) it.next();
            // si es final de fila, reset de offset W y incrementar offset de altura
            if (i % 9 == 0) {
                offsetW = widthDisplay / 25;
                offsetH += heightDisplay / 20 + 10;
            }
            // crear Button
            botonLetra = new Button(this);
            // añadir texto
            String textoBoton = "" + (char) ((Character) p.getKey() + ('A'-'a'));
            botonLetra.setText(textoBoton);

            // personalizar Button
            botonLetra.setBackgroundColor(Color.parseColor(grayColor));
            botonLetra.setTextColor(Color.BLACK);
            botonLetra.setTextSize(13);
            // definir tamaño
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            params.height = 125;
            params.width = 125;
            botonLetra.setLayoutParams(params);
            // Posicionar Button
            botonLetra.setY(offsetH);
            botonLetra.setX(offsetW);
            // Añadir Button al layout
            constraintLayout.addView(botonLetra);
            // añadir funcionalidad
            botonLetra.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onClickTeclado(v);
                }
            });
            // actualizar offset
            offsetW += widthDisplay / 11 + 20;
            // incrementar i
            i++;
        }

        // Crear el botón
        Button buttonEsborrar = new Button(this);
        buttonEsborrar.setText("Esborrar");
        // Posicionar el botón
        int buttonWidth = 400;
        int buttonHeight = 200;

        buttonEsborrar.setY(heightDisplay - 700 - buttonHeight);
        buttonEsborrar.setX(widthDisplay / 4 - buttonWidth / 20 + 30);
        // Añadir el botón al layout
        constraintLayout.addView(buttonEsborrar);
        // Añadir la funcionalidad al botón
        buttonEsborrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // verificar si debemos borrar
                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(5);
                gd.setStroke(3, Color.parseColor(grayColor));
                GradientDrawable gdOrange = new GradientDrawable();
                gdOrange.setCornerRadius(5);
                gdOrange.setStroke(3, Color.parseColor("#FF8000"));


                if (numLetra == 0) {
                    return;
                }
                // Buscamos la casilla siguiente a rellenar
                TextView textViewSeleccionat = findViewById(numIntentos * lengthWord + numLetra - 1);
                TextView textViewSiguiente = findViewById(numIntentos * lengthWord + numLetra);
                textViewSiguiente.setBackground(gd);
                textViewSeleccionat.setBackground(gdOrange);
                textViewSeleccionat.setText("");
                // decrementar numLetra
                numLetra--;
            }
        });

        // Crear el botón
        Button buttonEnvia = new Button(this);
        buttonEnvia.setText("Envia");
        // Posicionar el botón
        // buttonEnvia.setLayoutParams(params);
        buttonEnvia.setY(heightDisplay - 700 - buttonHeight);
        buttonEnvia.setX((widthDisplay / 4 - buttonWidth / 2) + (buttonWidth + 160) + 30);
        // Añadir el botón al layout
        constraintLayout.addView(buttonEnvia);
        buttonEnvia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               EnviarLogic();
            }
        });
    }

    private void initPosiblesSoluciones() {
        Set<Map.Entry<String, String>> setDiccionario = diccionario.entrySet();
        Iterator iterador = setDiccionario.iterator();
        // Se copia el contenido del hash en el árbol RN
        while (iterador.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterador.next();
            String key = entry.getKey();
            posiblesSoluciones.add(key);
        }

        Iterator itSeries = posiblesSoluciones.iterator();
        while (itSeries.hasNext()){
            String key = (String) itSeries.next();
            Log.d("hola", "key: " + key);

        }


    }

    // Función para actualizar el árbol de posibles soluciones haciendo uso del
    // árbol binario de las restricciones
    private void updatePosiblesSoluciones() {
        // Iterador que recorrerá el árbol con las restricciones del intento anterior
        Iterator itSeries = posiblesSoluciones.iterator();
        // Árbol auxiliar para almacenar las posibles soluciones teniendo en cuenta las nuevas
        // restricciones
        TreeSet<String> posiblesSolucionesAux = new TreeSet<>();

        // Se va recorriendo posiblesSoluciones
        while (itSeries.hasNext()) {
            String key = (String) itSeries.next();
            for (int i = 0; i < key.length(); i++) {
                boolean correcta = false;

                /////////////////////////////////////////////////////////////////////7
                ////////                    CORREGIR ESTO                       //////
                /////////////////////////////////////////////////////////////////////7

                // La letra no existe en la solución
                if (letras.get(key.charAt(i)).isEmpty()) {
                    letras.get(key.charAt(i)).add(-1);
                // La letra esta en la solucion
                } else {
                    Iterator it = letras.get(key.charAt(i)).iterator();
                    while (it.hasNext() && !correcta) {
                        Integer num = (Integer) it.next();
                        if (num != i) {
                            correcta = true;
                        }
                    }
                    if (!correcta){

                    }
                }
            }
        }

        // Se asigna el puntero del árbol auxiliar al árbol de posibles soluciones
        posiblesSoluciones = posiblesSolucionesAux;
    }

    public void onClickTeclado(View v) {
        String text = ((Button) v).getText().toString();
        // Miramos si se pueden escribir mas palabras
        if (numLetra == lengthWord) {
            return;
        }
        GradientDrawable gdOrange = new GradientDrawable();
        gdOrange.setCornerRadius(5);
        gdOrange.setStroke(3, Color.parseColor("#FF8000"));

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));
        // Buscamos la graella siguiente a rellenar
        int indiceSeleccionado = numIntentos * lengthWord + numLetra;

        TextView texViewSeleccionat = findViewById (indiceSeleccionado);
        if (numIntentos * lengthWord < indiceSeleccionado + 1 && (numIntentos+1)*lengthWord > indiceSeleccionado + 1) {
            TextView textViewSiguiente = findViewById(numIntentos * lengthWord + numLetra + 1);
            // Escribimos la letra
            textViewSiguiente.setBackground(gdOrange);
        }

        texViewSeleccionat.setBackground(gd);
        texViewSeleccionat.setText(text);
        numLetra++;
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // no posar amb notch
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Método lógico al apretar el boton enviar
    @SuppressWarnings("StringConcatenationInLoop")
    public void EnviarLogic() {
        if ((numLetra) != lengthWord) { // Comprobar longitud correcta (no se envia la palabra)
            // Mostrar mensaje palabra incompleta
            Context context = getApplicationContext() ;
            CharSequence text = "TU PALABRA NO TIENE LA LONGITUD ADECUADA";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else { // longitud correcta
            // Recoger la palabra en un string
            String palabra = "";
            for (int i = 0; i < lengthWord; i++) { // recorrer la palabra
                TextView textViewSeleccionat = findViewById(numIntentos * lengthWord + i);
                char letra = textViewSeleccionat.getText().toString().charAt(0);
                letra += 32;
                palabra += letra;
            }
            // Comprobar si la palabra existe
            int indice = palabraExiste(palabra);
            if (indice == -1) { // La palabra no existe (no se envia la palabra)
                // Mostrar mensaje palabra incompleta
                Context context = getApplicationContext() ;
                CharSequence text = "TU PALABRA NO EXISTE";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else { // La palabra sí existe (a partir de este punto SÍ se envia la palabra)
                /*boolean acertada = EsCorrecta();
                if  (!acertada) { // La palabra no es la correcta
                    if (numIntentos == maxTry) { // Se verifica el número de intentos
                        // Mostrar pantalla game over
                        Context context = getApplicationContext() ;
                        CharSequence text = "TU PALABRA NO EXISTE";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else { // quedan intentos
                        // siguienteIntento();
                        numIntentos++;
                        siguienteLinea();
                    }
                } else {
                    // Mostrar pantalla de victoria
                    Context context = getApplicationContext() ;
                    CharSequence text = "ENHORABUENA";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    // fin();
                }*/
            }
        }

    }

    // verifica si la palabra está en el fichero, si lo está, devuelve su posición en él, sino, -1.
    public int palabraExiste (String word) {
        String fileWord = "";
        int wordPos = 0;

        try {
            // abrir fichero
            InputStream is = getResources().openRawResource(R.raw.paraules);
            BufferedReader r = new BufferedReader (new InputStreamReader(is)) ;

            // leer primera línea de fichero
            fileWord = r.readLine();
            // bucle de lectura del fichero
            while (fileWord != null) {
                // obtener palabra sin acentos (está después del ';')
                fileWord = fileWord.substring(fileWord.indexOf(';')+1);
                // comparar palabras
                if (fileWord.equals(word))
                    // devolver wordPos
                    return wordPos;

                // leer la siguiente línea de fichero
                fileWord = r.readLine();
                // incrementar wordPos
                wordPos++;
            }
            return -1;
        } catch (IOException err) {
            // Mostrar pantalla de victoria
            Context context = getApplicationContext() ;
            CharSequence text = "No se pudo verificar si la palabra existe.";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        return -1;
    }
    private boolean EsCorrecta(String word){
        char c;
        UnsortedLinkedListSet j;
        for(int i=0;i<word.length();i++){
            c=word.charAt(i);
            j=letras.get(c);
            if(j==null||!j.contains((i+1))){
                return false;
            }
        }
        return true;
    }

    public void getPistas (String word) {
        // hay que mirar, para cada uno de los caracteres de la palabra, si está en la palabra solucion
        char c;
        UnsortedLinkedListSet j;
        // bucle de recorrido de los caracteres de word
        for (int i=0; i < word.length(); i++) {
            c = word.charAt(i);
            // obtener conjunto de posiciones
            j = letras.get(c);

            if (j != null) {
                // check posiciones unsando index of y substring (quiza necesito String auxiliar);
                if(j.contains(i+1)){
                    //poner letra verde
                }
                else{
                    //poner letra amarillo
                }

            } else {
                // esta letra no se encuentra en la palabra
                //poner letra roja
            }

        }
    }

    public void siguienteLinea() {
        numLetra = 0 ;
        GradientDrawable gdOrange = new GradientDrawable();
        gdOrange.setCornerRadius(5);
        gdOrange.setStroke(3, Color.parseColor("#FF8000"));

        TextView textViewSeleccionat = findViewById (numIntentos * lengthWord + numLetra);
        textViewSeleccionat.setBackground(gdOrange);
    }

}