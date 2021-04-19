package utils;

import definition.TTPDefinition;
import definition.state.statecode.Date;
import execute.Executer;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import problem.definition.State;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;


import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
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


    /*public  void exportItineraryInExcelFormat(){
        FileChooser fc = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fc.getExtensionFilters().add(extFilter);


        //dc = new DirectoryChooser();
        File f = fc.showSaveDialog(new Stage());

        XSSFWorkbook workbook = new XSSFWorkbook();

        List<State> list = Executer.getInstance().getResultStates();

        for (int i = 0; i < list.size(); i++){
            Sheet spreadsheet = workbook.createSheet("Calendario "+ (i+1));

            ArrayList<ArrayList<Integer>> teamDate = TTPDefinition.getInstance().teamsItinerary(list.get(i));
            Row row = spreadsheet.createRow(0);
            //Style of the cell
            XSSFFont headerCellFont = workbook.createFont();
            headerCellFont.setBold(true);
            headerCellFont.setColor(IndexedColors.WHITE.getIndex());
            headerCellFont.setFontHeightInPoints((short) 15);
            XSSFCellStyle style = workbook.createCellStyle();

            // Setting Background color
            style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFont(headerCellFont);

            //Header of the itinerary
            for (int j = 0; j < teamDate.get(0).size(); j++) {
                int posTeam = teamDate.get(0).get(j);
                String team = getTeams().get(posTeam);
                Cell cell = row.createCell(j);
                cell.setCellStyle(style);
                cell.setCellValue(team);
            }

            //Itinerary
            style = workbook.createCellStyle();
            headerCellFont = workbook.createFont();
            headerCellFont.setBold(false);
            headerCellFont.setFontHeightInPoints((short) 12);


            for(int j=1; j < teamDate.size()-1;j++ ){
                ArrayList<Integer> date = teamDate.get(j);
                row = spreadsheet.createRow(j);
                for(int k=0; k < date.size();k++){
                    int posTeam = teamDate.get(j).get(k);
                    String team = getAcronyms().get(posTeam);
                    Cell cell = row.createCell(k);
                    cell.setCellStyle(style);
                    cell.setCellValue(team);
                }
            }
        }


        Sheet spreadsheetData = workbook.createSheet("Data");
        Row rowData = spreadsheetData.createRow(0);
        Cell cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue("Calendario "+ (pos+1));

        rowData = spreadsheetData.createRow(1);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(Distance.getInstance().calculateCalendarDistance(state));


        //autosize each column of the excel document
        for(int i=0; i < row.getLastCellNum(); i++){
            spreadsheet.autoSizeColumn(i);
        }

        for(int i=0; i < rowData.getLastCellNum(); i++){
            spreadsheetData.autoSizeColumn(i);
        }

        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(f.getAbsolutePath());
            workbook.write(fileOut);
            fileOut.close();
            showSuccessfulMessage();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private static void showSuccessfulMessage() {
        TrayNotification notification = new TrayNotification();
        notification.setTitle("Guardar Calendario");
        notification.setMessage("Calendario exportado con �xito");
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(2));
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
