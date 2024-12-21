import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    BufferedReader reader;
    String delimiter;
    boolean hasHeader;
    List<String> columnLabels = new ArrayList<>();
    Map<String,Integer> columnLabelsToInt = new HashMap<>();
    String[]current;

    /**
     *
     * @param filename - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */

    public CSVReader(String filename,String delimiter,boolean hasHeader) {
        try{
            this.reader = new BufferedReader(new FileReader(filename));
        }
        catch(Exception e){
            System.out.printf("File not found\n");
        }
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();
    }
    CSVReader(String filename,String delimiter) {
        this(filename, delimiter, true);
    }
    CSVReader(String filename) {
        this(filename, ",", true);
    }
    public CSVReader(Reader reader, String delimiter, boolean hasHeader){
        reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();
    }

    void parseHeader() {
        String line=null;
        try {
            line = reader.readLine();
        }
        catch(Exception e){
            System.out.printf("Can't read the line - exception \"%s\"\n", e.toString());
        }
        if (java.util.Objects.equals(line,null)) {
            return;
        }
        StringBuilder splt = new StringBuilder();
        splt.append(delimiter);
        splt.append("(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        String[] header = line.split(splt.toString());    // podziel na pola

        for (int i = 0; i < header.length; i++) {   // przetwarzaj dane w wierszu
            this.columnLabels.add(header[i]);
            this.columnLabelsToInt.put(header[i], i);
        }
    }

    boolean next() {
        String line=null;
        try {
            line = reader.readLine();
        }
        catch(Exception e){
            System.out.printf("Can't read the line\n");
        }
        if (line == null) {
            return false;
        }
        StringBuilder splt = new StringBuilder();
        splt.append(delimiter);
        splt.append("(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        String[] header = line.split(splt.toString());

        this.current = new String[header.length];

        for (int i = 0; i < header.length; i++) {
            this.current[i] = header[i];
        }
        return true;
    }

    List<String> getColumnLabels(){
        return this.columnLabels;
    }

    int getRecordLength(){
        return this.current.length;
    }

    boolean isMissing(int columnIndex){
        if(this.get(columnIndex)!="")
            return false;
        else
            return true;
    }

    boolean isMissing(String columnLabel){
        return this.get(columnLabel).equals("");
    }

    String get(int columnIndex){
        if(columnIndex > this.current.length)
            return "";  //pewnie lepiej by było exception?
        else
        if(java.util.Objects.equals(this.current[columnIndex], null))
            return "";
        else
            return this.current[columnIndex];
    }

    String get(String columnLabel){
        return get(columnLabelsToInt.get(columnLabel));
    }

    int getInt(int columnIndex){
        if(get(columnIndex) != "")
            return Integer.parseInt(get(columnIndex));
        else return 0;
    }

    int getInt(String columnLabel){
        if(get(columnLabel) != "")
            return Integer.parseInt(get(columnLabel));
        else return 0;
    }

    long getLong(int columnIndex){
        if(get(columnIndex) != "")
            return Long.parseLong(get(columnIndex).replaceAll(",","."));
        else return 0;
    }

    long getLong(String columnLabel){
        if(get(columnLabel) != "")
            return Long.parseLong(get(columnLabel).replaceAll(",","."));
        else return 0;
    }

    double getDouble(int columnIndex){
        if(get(columnIndex) != "")
            return Double.parseDouble(get(columnIndex).replaceAll(",","."));
        else return 0;
    }

    double getDouble(String columnLabel){
        if(get(columnLabel) != "")
            return Double.parseDouble(get(columnLabel).replaceAll(",","."));
        else return 0;
    }

    LocalTime getTime(int columnIndex, String format){
        return LocalTime.parse(current[columnIndex], DateTimeFormatter.ofPattern(format));
    }

    LocalTime getTime(int columnIndex){
        return this.getTime(columnIndex, "HH:mm:ss");
    }

    LocalDate getDate(int columnIndex, String format){
        return LocalDate.parse(current[columnIndex], DateTimeFormatter.ofPattern(format));
    }

    LocalDate getDate(int columnIndex){
        return this.getDate(columnIndex, "yyyy-MM-dd");
    }

}