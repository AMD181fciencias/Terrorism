import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public static void main(String[] args){
	//las columnas del csv que vamos a ocupar
	int[] columnas = {1, 8, 9, 35, 84, 100, 103};
	//el número de registros a ocupar
	int registros = 1000000;
	//obtenemos los datos
	ArrayList<String[]> datos = dameDatos(columnas,registros,-1);
	ArrayList<String[]> datosPrueba = dameDatos(columnas,100000,1);
	
	//Renombramos las columnas para mejor uso
	
	datos.get(0)[0] = "year";
	datos.get(0)[1] = "country";
	datos.get(0)[2] = "region";
	datos.get(0)[3] = "target";
	datos.get(0)[4] = "weapon";
	datos.get(0)[5] = "fatalities";
	datos.get(0)[6] = "injuries";
	
	//Los datos Nan los hacemos 0, en las columnas de heridos y muertos
	// y Unknown en weapon
	for(String[] sa: datos){
	    if(sa[4].length() == 0)
		sa[4] = "Unknown";
	    if(sa[5].length() == 0)
		sa[5] = "0";
	    if(sa[6].length() == 0)
		sa[6] = "0";

	    //Clasificamos los años en etapas(discretización)

	    int anio = 0;
	    if(!sa[0].equals("year"))
		anio = Integer.parseInt(sa[0]);
	    
	    if(anio >= 1970 && anio < 1975)
		sa[0] = "1970-1974";
	    if(anio >= 1975 && anio < 1980)
		sa[0] = "1975-1979";
	    if(anio >= 1980 && anio < 1985)
		sa[0] = "1980-1984";
	    if(anio >= 1985 && anio < 1990)
		sa[0] = "1985-1989";
	    if(anio >= 1990 && anio < 1995)
		sa[0] = "1990-1994";
	    if(anio >= 1995 && anio < 2000)
		sa[0] = "1995-1999";
	    if(anio >= 2000 && anio < 2005)
		sa[0] = "2000-2004";
	    if(anio >= 2005 && anio < 2010)
		sa[0] = "2005-2009";
	    if(anio >= 2010 && anio < 2015)
		sa[0] = "2010-2014";
	    if(anio >= 2015)
		sa[0] = "2015-2017";	    
	}
	
	for(String[] sa: datosPrueba){
	    if(sa[4].length() == 0)
		sa[4] = "Unknown";
	    if(sa[5].length() == 0)
		sa[5] = "0";
	    if(sa[6].length() == 0)
		sa[6] = "0";

	    //Clasificamos los años en etapas(discretización)

	    int anio = 0;
	    if(!sa[0].equals("year"))
		anio = Integer.parseInt(sa[0]);
	    
	    if(anio >= 1970 && anio < 1975)
		sa[0] = "1970-1974";
	    if(anio >= 1975 && anio < 1980)
		sa[0] = "1975-1979";
	    if(anio >= 1980 && anio < 1985)
		sa[0] = "1980-1984";
	    if(anio >= 1985 && anio < 1990)
		sa[0] = "1985-1989";
	    if(anio >= 1990 && anio < 1995)
		sa[0] = "1990-1994";
	    if(anio >= 1995 && anio < 2000)
		sa[0] = "1995-1999";
	    if(anio >= 2000 && anio < 2005)
		sa[0] = "2000-2004";
	    if(anio >= 2005 && anio < 2010)
		sa[0] = "2005-2009";
	    if(anio >= 2010 && anio < 2015)
		sa[0] = "2010-2014";
	    if(anio >= 2015)
		sa[0] = "2015-2017";	    
	}

	//Primero obtenemos los valores posibles de cada atributo(columna)
	LinkedList<LinkedList<String>> valoresPosibles = new LinkedList<>();
	for(int i = 0;i < datos.get(0).length - 1;i++){
	    LinkedList<String> valores = new LinkedList<>();
		    
	    for(ListIterator<String[]> iterator = datos.listIterator(1); iterator.hasNext() ;){
		String[] currentArray = iterator.next();
		if(!valores.contains(currentArray[i]))
		    valores.add(currentArray[i]);
	    }
	    valoresPosibles.add(valores);
	}
	
	//Obtenemos las probabilidades de todos los atributos en la tabla para el caso negativo
	ArrayList<double[]> probasNeg = new ArrayList<>();
	for(int i = 0;i < datos.get(0).length - 1 ;i++){
	   
	    double[] proba = obtenProbas("0", 5, i, valoresPosibles.get(i), datos);

	    probasNeg.add(proba);
	    
	}
	
	//Lo mismo para el caso positivo
	
	ArrayList<double[]> probasPos = new ArrayList<>();	
	for(int i = 0;i < datos.get(0).length - 1 ;i++){
	    

	    double[] proba = obtenProbas("1", 5, i, valoresPosibles.get(i), datos);
	    
	    probasPos.add(proba);
	    
	}

	double [][] matConf = {{0.0,0.0},{0.0,0.0}};	
	
	//Aquí se clasifica un ejemplar:
	for(String[] ejemplar : datosPrueba){
	    
	    double probPos = 0.5;
	    for(int i = 0;i < ejemplar.length - 1;i++){ 
		int indice = valoresPosibles.get(i).indexOf(ejemplar[i]);
		probPos *= probasPos.get(i)[indice];
	    }
	
	    double probNeg = 0.5;
	    for(int i = 0;i < ejemplar.length - 1;i++){ 
		int indice = valoresPosibles.get(i).indexOf(ejemplar[i]);
		probNeg *= probasNeg.get(i)[indice];
	    }
	    int prediccion = probPos > probNeg ? 1 : 0;
	    int valorReal = Integer.parseInt(ejemplar[5]);
	    if(valorReal == 1){
		if(prediccion == 1)
		    matConf[0][0]++;
		else
		    matConf[0][1]++;
	    }else{
		if(prediccion == 1)
		    matConf[1][0]++;
		else
		    matConf[1][1]++;
	    }
	    
	}
	
	
	
	for(int i = 0;i<2;i++){
	    for(int j = 0;j<2;j++)
		System.out.print(matConf[i][j] + " ");
	    System.out.print("\n");
	}		
	double sen = matConf[0][0] / (matConf[0][0] + matConf[0][1]);
	double esp = matConf[1][1] / (matConf[1][1] + matConf[1][0]);
	System.out.println("Sensibilidad: " + sen);
	System.out.println("Especificación: " + esp);
	
    }

    /**
     *  Método que construye las probabilidades por clase de los
     *  valores posibles de un atributo dado, en un conjunto de 
     * datos objetivo
     * @param clase - La clase objetivo
     * @param claseIndex - El indice de la columna en la cual está la clase
     * @param atributoIndex - El indice de la columna en la cual está el atributo
     * @param valores - Los valores posibles del atributo dado 
     * @param data - El conjunto de datos
     */

    public static double[] obtenProbas(String clase, int claseIndex, int atributoIndex, LinkedList<String> valores, ArrayList<String[]> data){
	int listLength = 0;
	for(String s : valores)
	    listLength++;
	double[] ret = new double[listLength];
	for(int i = 0;i < ret.length;i++)
	    ret[i] = 0.0;
	for(String[] sa: data){
	    if(sa[claseIndex].equals(clase))
		for( int i = 0; i < listLength ;i++)
		    if(sa[atributoIndex].equals(valores.get(i)))
			ret[i] += 1;
	}
	
	int suma = 0;
	for(int i = 0;i < ret.length;i++){
	    ret[i] += 1.0;
	    suma += ret[i];
	}	    
	for(int i = 0;i < ret.length;i++)
	    ret[i] /= suma;
	return ret;

	
    }


    /**
     * Método que construye un conjunto de datos a partir de un
     * archivo csv, puede darte los registros pares, impares o todo
     * @param cols - El indice de las columnas a parsear(necesario si solo
     * queremos un número reducido de columnas)
     * @param rows - El número de registros a parsear
     * @param paridad - Si se parsean, los registros pares, impares o todos(0=pares,1=impares,otro=todo)
     */

    public static ArrayList<String[]> dameDatos(int[] cols,int rows, int paridad) {
        String fileLocation = "globalterrorismdb_0617dist.csv";
        BufferedReader br = null;
        String line = "";
        String separadorInicial = ",";
        String separador = "°";
        char capsul = '"';
        ArrayList<String[]> attacks = new ArrayList<String[]>();
	
        try {
	    
            br = new BufferedReader(new FileReader(fileLocation));
	    int i = 0;

            while (((line = br.readLine()) != null) && i < rows ) {
		//revisar criterio de paridad
	        if((i%2 != 0 && paridad == 0) || (i%2 == 0 && paridad == 1)){
		    i++;
		    continue;
		}
                String[] attack = parseLine(line);
		String[] shortAttack = new String[cols.length];
		
		for(int j = 0; j < cols.length ;j++)		    
		    shortAttack[j] = attack[cols[j]];		    
		
                attacks.add(shortAttack);
		i++;
		
	    }
	    return attacks;
        }catch (IOException e) {
            e.printStackTrace();
	    return null;
        }
        
    }

    /**
     * Método que construye un arreglo a partir de una linea csv
     * @param cvsLine - La linea del texto en formato csv
     */
    public static String[] parseLine(String cvsLine) {
        List<String> line  = parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
        String[] result = new String[line.size()];
        for(int i = 0; i < result.length; i++)
            result[i] = line.get(i);
        return result;
    }


    /**
     * Método que construye un arreglo a partir de una linea csv
     * @param cvsLine - La linea del texto en formato csv
     * @param separators - Los separadores del csv
     */

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    /**
     * Método que construye un arreglo a partir de una linea csv
     * @param cvsLine - La linea del texto en formato csv
     * @param separators - Los separadores del csv
     * @param customQuote - El caracter para citar
     */
    
    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }

}
