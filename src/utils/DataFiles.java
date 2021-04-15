package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataFiles {


    private static DataFiles singletonFiles;//Singleton Pattern
    private ArrayList<String> teams;//List of resources.teams
    private ArrayList<String> acronyms;
    private ArrayList<String> locations;
    private ArrayList<TeamsPairDistance> teamsPairDistances;//List of LocalVisitorDistance

    private DataFiles() {
        this.teams = new ArrayList<>();
        this.acronyms = new ArrayList<>();
        this.teamsPairDistances = new ArrayList<>();
        createTeams();
    }

    public static DataFiles getSingletonDataFiles() {
        if (singletonFiles == null) {
            singletonFiles = new DataFiles();
        }
        return singletonFiles;
    }

    public void createTeams() {

        try {

            FileInputStream fis = new FileInputStream("src/files/Data.xlsx");
            //Creamos el objeto XSSF  para el archivo excel
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0);

            acronyms = new ArrayList<>();
            teams = new ArrayList<>();
            teamsPairDistances = new ArrayList<>();

            //llenar los acrónimos
            Row row = sheet.getRow(0);
            Iterator<Cell> cellAcro = row.cellIterator();
            while (cellAcro.hasNext()){
                acronyms.add(cellAcro.next().getStringCellValue());
            }

            Iterator<Row> rowIterator = sheet.iterator();

            //Nos saltamos la primera fila del encabezado
            rowIterator.next();

            int posLocal =-1;
            while (rowIterator.hasNext()){
                row = rowIterator.next();

                //Recorremos las celdas de la fila
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell = cellIterator.next();
                teams.add(cell.getStringCellValue());
                posLocal++;
                int posVisitor = -1;
                while (cellIterator.hasNext()){
                    posVisitor++;
                    cell  = cellIterator.next();
                    //System.out.print(cell.toString() + ";");
                    teamsPairDistances.add(new TeamsPairDistance(posLocal,posVisitor,cell.getNumericCellValue()));
                }
            }


            Sheet sheetLocations = workbook.getSheetAt(1);
            locations = new ArrayList<>();
            Row rowLocations = sheetLocations.getRow(0);
            Iterator<Cell> cellLoc = rowLocations.cellIterator();

            while(cellLoc.hasNext()){
                locations.add(cellLoc.next().getStringCellValue());
            }

        } catch (IOException e) {
            e.getMessage();
        }
    }

    public List<String> readExcelFiles(String direction) {
        List<String>data = new ArrayList<>();
        try{
            FileInputStream fis = new FileInputStream(direction);

            //Creamos el objeto XSSF  para el archivo eexcel
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            //Nos quedamos con la primera hoja de calculo

            //XSSFSheet xssfSheet = workbook.getSheetAt(0);

            XSSFSheet xssfSheet = workbook.getSheetAt(0);

            //System.out.println(xssfSheet.getSheetName());
            //Recorremos las filas

            //Nos saltamos la primera fila del encabezado

            for (Row row : xssfSheet) {
                //Recorremos las celdas de la fila
                Iterator<Cell> cellIterator = row.cellIterator();

                StringBuilder sb = new StringBuilder();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    sb.append(cell.toString());
                }
                data.add(new String(sb));
            }

            workbook.close();
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }


    public ArrayList<String> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<String> teams) {
        this.teams = teams;
    }

    public ArrayList<String> getAcronyms() {
        return acronyms;
    }

    public void setAcronyms(ArrayList<String> acronyms) {
        this.acronyms = acronyms;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    public ArrayList<TeamsPairDistance> getTeamsPairDistances() {
        return teamsPairDistances;
    }

    public void setTeamsPairDistances(ArrayList<TeamsPairDistance> teamsPairDistances) {
        this.teamsPairDistances = teamsPairDistances;
    }
}
