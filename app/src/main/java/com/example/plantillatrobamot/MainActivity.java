package com.example.plantillatrobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
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
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


@SuppressWarnings({"rawtypes", "unchecked"})
public class MainActivity extends AppCompatActivity {
    // Variables de lògica del joc
    private final int lengthWord = 5;
    private final int maxTry = 6;
    private int numIntentos = 0;
    private int numLetra = 0;
    private int contadorPalabrasDiccionario = 0;
    private String palabraSolucion;

    // mapping con la información de las letras y su posición en la palabra original
    // implementación de array
    UnsortedArrayMapping<Character, UnsortedLinkedListSet<Integer>> letras;
    // mapping con las pistas generadas a partir de las palabras insertadas por el usuario
    TreeMap <Character, UnsortedLinkedListSet<Integer>> restricciones;
    // mapping con las palabras sin acento (keys) y con acento (values) del diccionario
    // implementación hash
    HashMap<String, String> diccionario = new HashMap<>();
    // Conjunto que contiene las palabras que pueden ser solución (sin acento)
    // implementación de arbol binario
    TreeSet<String> posiblesSoluciones = new TreeSet<>();

    // Variables de construcció de la interfície
    public static String grayColor = "#D9E1E8";
    private int widthDisplay;
    private int heightDisplay;
    private final int ID_TEXT_SOL = 777;
    private final int textViewSize = 200 - (lengthWord-5) * 40;;

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

        // inicializar estructuras de datos
        crearDiccionario();
        initPosiblesSoluciones();
        // generar palabra solucion
        generarPalabra();
        // crear interfaz
        crearInterficie();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSystemUI();
    }

    // método que crea los elementos de la interfaz de forma dinámica
    private void crearInterficie() {
        // crear casillas
        crearCasilla();
        // crear teclado, inicializa el mapping con la información de la palabra solución
        crearTeclat();

        // crear TextView de Soluciones
        ConstraintLayout constraintLayout = findViewById(R.id.layout);
        TextView stringSoluciones = new TextView(this);
        stringSoluciones.setId(ID_TEXT_SOL);
        String textoSoluciones = "Hi ha " + contadorPalabrasDiccionario + " Solucions Possibles.";
        stringSoluciones.setText(textoSoluciones);
        stringSoluciones.setTextColor(Color.BLACK);
        stringSoluciones.setTextSize(20);

        stringSoluciones.setX((float)(widthDisplay - (200*5+1))/2);
        stringSoluciones.setY((float)heightDisplay/2 + 200);

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

    private void crearCasilla() {
        ConstraintLayout constraintLayout = findViewById(R.id.layout);

        // Definir les característiques del "pinzell"
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));
        int offsetX;
        int offsetY = 0;



        TextView [][] textViewCasilla = new TextView [maxTry][lengthWord];
        // Crear un TextView per cada posició de la casilla
        for(int i = 0; i < maxTry; i++){
            if (lengthWord == 6){
                offsetX = 15;
            } else {
                offsetX = ((lengthWord-1)/2) * (-20);
            }
            for(int j = 0; j < lengthWord; j++){
                textViewCasilla[i][j] = new TextView(this);
                textViewCasilla[i][j].setBackground(gd);
                textViewCasilla[i][j].setId(i*lengthWord+j);
                textViewCasilla[i][j].setWidth(textViewSize);
                textViewCasilla[i][j].setHeight(textViewSize);
                textViewCasilla[i][j].setX((float) widthDisplay/2 - (float)textViewSize/2 - (float)(lengthWord/2 - j)*textViewSize + offsetX);
                textViewCasilla[i][j].setY((float)heightDisplay/3 - textViewSize/2 - (maxTry/2 - i)*textViewSize + offsetY);
                textViewCasilla[i][j].setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                textViewCasilla[i][j].setTextColor(Color.GRAY);
                textViewCasilla[i][j].setTextSize(30);
                textViewCasilla[i][j].setText("");
                offsetX += 20;

                // Afegir el TextView al layout
                constraintLayout.addView(textViewCasilla[i][j]);
            }
            offsetY += 20;

        }
        // Pintar la primera casilla
        GradientDrawable gdOrange = new GradientDrawable();
        gdOrange.setCornerRadius(5);
        gdOrange.setStroke(3, Color.parseColor("#FF8000"));
        textViewCasilla[0][0].setBackground(gdOrange);

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
            botonLetra.setId(300+i);

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
                // Verificar si debemos borrar
                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(5);
                gd.setStroke(3, Color.parseColor(grayColor));
                GradientDrawable gdOrange = new GradientDrawable();
                gdOrange.setCornerRadius(5);
                gdOrange.setStroke(3, Color.parseColor("#FF8000"));

                // Si no hay nada que borrar, salir
                if (numLetra == 0) {
                    return;
                }
                // Buscamos la casilla siguiente a rellenar
                TextView textViewSeleccionat = findViewById(numIntentos * lengthWord + numLetra - 1);
                TextView textViewSiguiente = findViewById(numIntentos * lengthWord + numLetra);
                textViewSiguiente.setBackground(gd);
                textViewSeleccionat.setBackground(gdOrange);
                textViewSeleccionat.setText("");
                // Decrementar numLetra
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
        // Se obtiene un iterador sobre el mapping del diccionario
        Set<Map.Entry<String, String>> setDiccionario = diccionario.entrySet();
        Iterator iterador = setDiccionario.iterator();
        // Se copia el contenido del hash (las claves) en el árbol RN posiblesSoluciones
        while (iterador.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterador.next();
            // Obtener clave
            String key = entry.getKey();
            posiblesSoluciones.add(key);
        }

        restricciones = new TreeMap<>();
    }

    // Función para actualizar el árbol de posibles soluciones haciendo uso del
    // árbol binario de las restricciones
    private void updatePosiblesSoluciones() {
        // Iterador que recorrerá el árbol teniendo en cuenta las restricciones del intento anterior
        Iterator itSeries = posiblesSoluciones.iterator();
        // Árbol auxiliar para almacenar las posibles soluciones teniendo en cuenta las nuevas
        // restricciones
        TreeSet<String> posiblesSolucionesAux = new TreeSet<>();
        int contadorPalabrasDiccionarioAux = 0;
        // Se va recorriendo posiblesSoluciones
        while (itSeries.hasNext()) {
            // Obtener palabra de posiblesSoluciones
            String palabra = (String) itSeries.next();

            // Iterador de las restricciones
            Set<Map.Entry<Character, UnsortedLinkedListSet<Integer>>> setRestricciones = restricciones.entrySet();
            Iterator itRestricciones = setRestricciones.iterator();

            // Variable que indica si la palabra cumple las restricciones
            boolean cumpleRestricciones = true;

            // Bucle que recorre las restricciones
            while (itRestricciones.hasNext() && cumpleRestricciones) {
                // Obtener pareja del mapping de restricciones
                Map.Entry <Character, UnsortedLinkedListSet <Integer>> entry = (Map.Entry <Character, UnsortedLinkedListSet <Integer>>) itRestricciones.next();
                // Obtener valor de la pareja obtenida
                UnsortedLinkedListSet <Integer> posRest = entry.getValue();

                Iterator posRestriccionIt = posRest.iterator();
                // Número de la posición de una restricción
                int posRestriccion;
                // Carácter de la restricción
                char c = entry.getKey();
                String cStr = ""+c;

                // Bucle que recorre las posiciones de una restricción
                while (posRestriccionIt.hasNext() && cumpleRestricciones) {
                    // Obtener posición de la restricción
                    posRestriccion = (int) posRestriccionIt.next();

                    // Comprobar si la letra de la restricción esta en la solución
                    if (posRestriccion == 0) {
                        // La letra no está en la palabra solucion
                        if (palabra.contains(cStr)) {
                            cumpleRestricciones = false;
                        }
                    } else {
                        // La letra está en la palabra solución
                        if (posRestriccion > 0) {
                            // La letra está en la palabra solución y en la posición correcta
                            if (palabra.charAt(posRestriccion - 1) != c) {
                                // palabra incorrecta
                                cumpleRestricciones = false;
                            }
                        } else {
                            // La letra está en la palabra, pero no en la posición abs(posUserI)
                            if ((palabra.charAt(Math.abs(posRestriccion) - 1) == c) && (palabra.contains(cStr))) {
                                // Palabra incorrecta
                                cumpleRestricciones = false;
                            }
                        }
                    }

                }
            }
            // Se añade la posible palabra al árbol auxiliar si cumple las restricciones
            if (cumpleRestricciones) {
                posiblesSolucionesAux.add(palabra);
                contadorPalabrasDiccionarioAux++;
            }
        }

        // Se asigna el puntero del árbol auxiliar al árbol de posibles soluciones
        posiblesSoluciones = posiblesSolucionesAux;
        contadorPalabrasDiccionario = contadorPalabrasDiccionarioAux;
        TextView textView = findViewById(ID_TEXT_SOL);
        textView.setText("Hi ha " + contadorPalabrasDiccionario + " Solucions Possibles.");
    }

    public void onClickTeclado(View v) {
        String text = ((Button) v).getText().toString();
        // Miramos si se pueden escribir más palabras
        if (numLetra == lengthWord) {
            return;
        }
        GradientDrawable gdOrange = new GradientDrawable();
        gdOrange.setCornerRadius(5);
        gdOrange.setStroke(3, Color.parseColor("#FF8000"));

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));
        // Buscamos la casilla siguiente a rellenar
        int indiceSeleccionado = numIntentos * lengthWord + numLetra;

        TextView textViewSeleccionat = findViewById (indiceSeleccionado);

        if (numIntentos * lengthWord < indiceSeleccionado + 1 && (numIntentos + 1)*lengthWord > indiceSeleccionado + 1) {
            TextView textViewSiguiente = findViewById(numIntentos * lengthWord + numLetra + 1);
            // Escribimos la letra
            textViewSiguiente.setBackground(gdOrange);
        }
        // Escribimos la letra
        textViewSeleccionat.setBackground(gd);
        textViewSeleccionat.setText(text);
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
            // Recoger la palabra del usuario en un string
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
               boolean acertada = EsCorrecta(palabra);
                if  (!acertada) { // La palabra no es la correcta
                    if (numIntentos+1 == maxTry) { // Se verifica el número de intentos
                        // Mostrar pantalla game over
                        Context context = getApplicationContext() ;
                        CharSequence text = "SE ACABARON LOS INTENTOS";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        fin(false);
                    } else { // Quedan intentos
                        numIntentos++;
                        updatePistas(palabra);
                        updatePosiblesSoluciones();
                        siguienteLinea();
                    }
                } else {
                    // Mostrar pantalla de victoria
                    Context context = getApplicationContext() ;
                    CharSequence text = "ENHORABUENA";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    fin(true);
                }
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

    private boolean EsCorrecta(String word) {
        char c;
        UnsortedLinkedListSet j;
        for(int i=0; i < word.length(); i++) {
            c=word.charAt(i);
            j=letras.get(c);
            if (j==null || !j.contains((i + 1))) {
                return false;
            }
        }
        return true;
    }

    // actualiza las restricciones conocidas en función de la palabra enviada por el usuario
    // para cada letra, se evalua si está en la posicion correcta, en la palabra pero en otra
    // posición o directamente no está en la palabra
    // en función de esto, se actualizan las letras del teclado y las casillas de la graella
    public void updatePistas (String word) {
        // hay que mirar, para cada uno de los caracteres de la palabra, si está en la palabra solucion
        char c;
        UnsortedLinkedListSet j;

        // bucle de recorrido de los caracteres de word
        for (int i = 0; i < word.length(); i++) {
            c = word.charAt(i);
            // obtener conjunto de posiciones
            j = letras.get(c);

            // verificar si la letra está en la palabra (su conjunto de posiciones es no nulo)
            if (j != null) {
                // verificar si el conjunto de posiciones contiene el índice de la letra
                if (j.contains(i + 1)) {
                    // La letra está en la palabra y en la posición correcta (VERDE)
                    // comprobar si existe o no la restricción de esta letra
                    if (!restricciones.containsKey(c)) {
                        // crear restricción con la posición i+1
                        restricciones.put(c, new UnsortedLinkedListSet<>(i + 1));
                    } else {
                        // añadir a la restricción la posición i+1
                        restricciones.get(c).add(i + 1);
                    }
                    pintarCasilla((numIntentos-1)*lengthWord + i, Color.GREEN);
                    if(c == 'ç'){
                        pintarCasilla(326, Color.GREEN);
                    }else{
                        pintarCasilla((300+(c-'a')), Color.GREEN);
                    }

                }
                else {
                    // La letra está en la palabra pero no esta en la posición correcta (AMARILLO)
                    restricciones.put(c, new UnsortedLinkedListSet<>(-i-1));
                    pintarCasilla((numIntentos-1)*lengthWord + i, Color.YELLOW);
                    if(c == 'ç'){
                        pintarCasilla(326, Color.YELLOW);
                    }else{
                        pintarCasilla((300+(c-'a')), Color.YELLOW);
                    }
                }

            } else {
                // Esta letra no se encuentra en la palabra (ROJO)
                restricciones.put(c, new UnsortedLinkedListSet<>(0));
                pintarCasilla((numIntentos-1)*lengthWord + i, Color.RED);
                if(c == 'ç'){
                    pintarCasilla(326, Color.RED);
                }else{
                    pintarCasilla((300+(c-'a')), Color.RED);
                }
            }
        }
    }

    // método que pinta el fondo de un componente de id i del color pasado por parámetro
    private void pintarCasilla(int i, int color) {
        TextView textViewSeleccionat = findViewById(i);
        textViewSeleccionat.setBackgroundColor(color);
        textViewSeleccionat.setTextColor(Color.BLACK);
    }

    // método que salta a la siguiente letra de la graella de casillas,
    // modificando las variables necesarias
    public void siguienteLinea() {
        numLetra = 0 ;
        GradientDrawable gdOrange = new GradientDrawable();
        gdOrange.setCornerRadius(5);
        gdOrange.setStroke(3, Color.parseColor("#FF8000"));

        TextView textViewSeleccionat = findViewById (numIntentos * lengthWord + numLetra);
        textViewSeleccionat.setBackground(gdOrange);
    }

    // método que llama a la actividad relacionada con la pantalla de fin de juego
    public void fin(boolean w) {
        Intent intent=new Intent(this, MainActivity2.class);
        // paso de parámetros a la nueva actividad
        intent.putExtra("PALABRA", diccionario.get(palabraSolucion));
        intent.putExtra("VICTORIA", w);
        intent.putExtra("RESTRICCIONES", setTextRest());
        intent.putExtra("POSIBLES_SOLUCIONES", palabrasPosiblesToString());
        // iniciar actividad de pantalla final
        startActivity(intent);
    }

    // método que obtiene una palabra aleatoria de todas aquellas que tengan longitud lenghtWord
    private void generarPalabra(){
        // generar índice aleatorio de la palabra solución
        int n =  new Random().nextInt(contadorPalabrasDiccionario);
        // iterador sobre todas las palabras de la longitud requerida
        Iterator it = posiblesSoluciones.iterator();
        String s = "";
        // recorrido
        for(int i = 0; i < n && it.hasNext(); i++){
            s = (String)it.next();
        }
        palabraSolucion = s;
    }


    private String setTextRest() {
        TextView textoRest = findViewById(R.id.textoRestricciones);
        StringBuilder texto = new StringBuilder("Restriccions: ");
        // Iterador de las restricciones
        Set<Map.Entry<Character, UnsortedLinkedListSet<Integer>>> setRestricciones = restricciones.entrySet();
        Iterator itRestricciones = setRestricciones.iterator();
        while (itRestricciones.hasNext()) {
            Map.Entry<Character, UnsortedLinkedListSet<Integer>> restriccion = (Map.Entry<Character, UnsortedLinkedListSet<Integer>>) itRestricciones.next();
            UnsortedLinkedListSet<Integer> posiciones = restriccion.getValue();
            Iterator itPosiciones = posiciones.iterator();
            String aux = ""+restriccion.getKey();
            while (itPosiciones.hasNext()) {
                // Obtener posición de la restricción
                int posRestriccion = (int) itPosiciones.next();

                // Comprobar si la letra de la restricción esta en la solución
                if (posRestriccion == 0) {
                    texto.append("no ha de contenir la ").append(aux.toUpperCase()).append(", ");
                } else {
                    // La letra está en la palabra solución
                    if (posRestriccion > 0) {
                        texto.append("ha de contenir la ").append(aux.toUpperCase()).append(" a la posició ").append(posRestriccion).append(", ");
                    } else {
                        // La letra está en la palabra, pero no en la posición
                        texto.append("no ha de contenir la ").append(aux.toUpperCase()).append(" a la posició ").append(-posRestriccion).append(", ");
                    }
                }
            }
        }
        // Eliminamos la última coma y ponemos un punto
        texto.setCharAt(texto.length()-2, '.');

        return texto.toString();


    }

        // método que devuelve un String con las 5 primeras posibles
    // palabras solución (por orden alfabético)
    private String palabrasPosiblesToString() {
        final int MAX_SHOWN_WORDS = 5;
        Iterator it = posiblesSoluciones.iterator();
        StringBuilder st = new StringBuilder("Paraules possibles: " + it.next());

        // Concatenación de posibles palabras solución (por orden alfabético)
        for (int contador = 1; it.hasNext() && contador < MAX_SHOWN_WORDS; contador++) {
             st.append(", " + it.next());
        }

        // Comprobamos si hhay más soluciones posibles
        if(it.hasNext())
            st.append("...");   // Si hay más palabras, ponemos puntos suspensivos
        else
            st.append(".");     // Si no hay más palabras, ponemos un punto

        return st.toString();
    }
}