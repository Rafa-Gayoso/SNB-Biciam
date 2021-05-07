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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
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

    private static final String MUTATIONS = "config_files"+File.separator+"Mutations.xml";
    private static final String HEURISTICS = "config_files"+File.separator+"Heuristics.xml";
    private static final String TEAMS = "config_files"+File.separator+"Teams.xml";
    private static final String DATA = "config_files"+File.separator+"Data.xlsx";
    private static DataFiles singletonFiles;//Singleton Pattern
    private ArrayList<String> teams;//List of resources.teams
    private ArrayList<String> acronyms;
    private ArrayList<String> locations;
    private ArrayList<String> mutations;
    private ArrayList<String> heuristics;
    private ArrayList<TeamsPairDistance> teamsPairDistances;//List of LocalVisitorDistance

    private DataFiles() {
        this.teams = new ArrayList<>();
        this.acronyms = new ArrayList<>();
        this.teamsPairDistances = new ArrayList<>();
        this.mutations = new ArrayList<>();
        this.heuristics = new ArrayList<>();
        readMutations();
        readHeuristics();
        readTeams();
        createTeamsPairDistances();
    }

    public static DataFiles getSingletonDataFiles() {
        if (singletonFiles == null) {
            singletonFiles = new DataFiles();
        }
        return singletonFiles;
    }

    public void createTeamsPairDistances() {

        try {

            FileInputStream fis = new FileInputStream(DATA);
            //Creamos el objeto XSSF  para el archivo excel
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0);


            teamsPairDistances = new ArrayList<>();
            //llenar los acrónimos
            Row row = sheet.getRow(0);
            Iterator<Cell> cellAcro = row.cellIterator();


            Iterator<Row> rowIterator = sheet.iterator();

            //Nos saltamos la primera fila del encabezado
            rowIterator.next();

            int posLocal =-1;
            while (rowIterator.hasNext()){
                row = rowIterator.next();

                //Recorremos las celdas de la fila
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell = cellIterator.next();
                //teams.add(cell.getStringCellValue());
                posLocal++;
                int posVisitor = -1;
                while (cellIterator.hasNext()){
                    posVisitor++;
                    cell  = cellIterator.next();
                    //System.out.print(cell.toString() + ";");
                    teamsPairDistances.add(new TeamsPairDistance(posLocal,posVisitor,cell.getNumericCellValue()));
                }
            }



        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void readMutations(){

        try {

            SAXBuilder sax = new SAXBuilder();
            // XML is a local file
            Document doc = sax.build(new File(MUTATIONS));

            Element rootNode = doc.getRootElement();
            List<Element> list = rootNode.getChildren("mutation");

            for (Element target : list) {



                String name = target.getChildText("name");
                String configuration = target.getChildText("configuration");
                mutations.add(name);
                //mutation.add(configuration);

                //mutations.add(mutation);
                /*
                PAra leer los parametros luego
                String [] params = acronym.split(",");

                System.out.print(params.length);
                for(int i =0; i < params.length; i++){
                    System.out.print(params[i] + " ");
                }
                System.out.println();*/
            }

        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

        //return mutations;
    }

    public void readHeuristics(){

        try {

            SAXBuilder sax = new SAXBuilder();
            // XML is a local file
            Document doc = sax.build(new File(HEURISTICS));

            Element rootNode = doc.getRootElement();
            List<Element> list = rootNode.getChildren("heuristic");

            for (Element target : list) {
                String name = target.getChildText("name");
                heuristics.add(name);
            }

        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }


    }

    public void readTeams(){
        List<String> heuristics = new ArrayList<>();
        try {

            SAXBuilder sax = new SAXBuilder();
            // XML is a local file
            Document doc = sax.build(new File(TEAMS));

            Element rootNode = doc.getRootElement();
            List<Element> list = rootNode.getChildren("team");

            acronyms = new ArrayList<>();
            teams = new ArrayList<>();
            locations = new ArrayList<>();

            for (Element target : list) {
                String name = target.getChildText("name");
                String acronym = target.getChildText("acronym");
                String region = target.getChildText("region");
                teams.add(name);
                acronyms.add(acronym);
                locations.add(region);
            }

        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }

    }


    public  void exportItineraryInExcelFormat(State state){
        FileChooser fc = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fc.getExtensionFilters().add(extFilter);


        //dc = new DirectoryChooser();
        File f = fc.showSaveDialog(new Stage());

        XSSFWorkbook workbook = new XSSFWorkbook();

            Sheet spreadsheet = workbook.createSheet();

            ArrayList<ArrayList<Integer>> teamDate = TTPDefinition.getInstance().teamsItinerary(state);
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

        int j=1;
            for(; j < teamDate.size()-1;j++ ){
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
            j++;

        row = spreadsheet.createRow(j);
        Cell cell1 =  row.createCell(0);
        cell1.setCellStyle(style);
        cell1.setCellValue("Distancia total(km): ");

        Cell cell2 =  row.createCell(1);
        cell2.setCellStyle(style);
        cell2.setCellValue(Distance.getInstance().calculateCalendarDistance(state));


        //autosize each column of the excel document
        for(int i=0; i < row.getLastCellNum(); i++){
            spreadsheet.autoSizeColumn(i);
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
    }


    private static void showSuccessfulMessage() {
        TrayNotification notification = new TrayNotification();
        notification.setTitle("Guardar Calendario");
        notification.setMessage("Calendario exportado con éxito");
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

    public ArrayList<String> getMutations() {
        return mutations;
    }

    public void setMutations(ArrayList<String> mutations) {
        this.mutations = mutations;
    }

    public ArrayList<String> getHeuristics() {
        return heuristics;
    }

    public void setHeuristics(ArrayList<String> heuristics) {
        this.heuristics = heuristics;
    }

}
