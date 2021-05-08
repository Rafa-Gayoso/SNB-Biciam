package utils;

import controller.CalendarController;
import definition.TTPDefinition;
import definition.state.statecode.Date;
import execute.Executer;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
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
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import problem.definition.State;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;


import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataFiles {

    private static final String MUTATIONS = "config_files" + File.separator + "Mutations.xml";
    private static final String HEURISTICS = "config_files" + File.separator + "Heuristics.xml";
    private static final String TEAMS = "config_files" + File.separator + "Teams.xml";
    private static final String DATA = "config_files" + File.separator + "Data.xlsx";
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

            int posLocal = -1;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();

                //Recorremos las celdas de la fila
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell cell = cellIterator.next();
                //teams.add(cell.getStringCellValue());
                posLocal++;
                int posVisitor = -1;
                while (cellIterator.hasNext()) {
                    posVisitor++;
                    cell = cellIterator.next();
                    //System.out.print(cell.toString() + ";");
                    teamsPairDistances.add(new TeamsPairDistance(posLocal, posVisitor, cell.getNumericCellValue()));
                }
            }


        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void readMutations() {

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

    public void readHeuristics() {

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

    public void readTeams() {
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


    /*public  void exportItineraryInExcelFormat(State state){
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
    }*/

    public void exportItinerary(boolean all) {
        DirectoryChooser fc = new DirectoryChooser();
        int pos = CalendarController.selectedCalendar;

        //Set extension filter for text files
        /*FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fc.getExtensionFilters().add(extFilter);*/


        //dc = new DirectoryChooser();
        File f = fc.showDialog(new Stage());
        File dir = new File(f.getAbsoluteFile() + "/Calendarios");
        if (!dir.exists()) {
            dir.mkdir();
        }

        ArrayList<State> calendarToExport = new ArrayList<>();
        if (all) {
            calendarToExport.addAll(Executer.getInstance().getResultStates());
        } else {
            calendarToExport.add(Executer.getInstance().getResultStates().get(pos));
        }

        for (int i = 0; i < calendarToExport.size(); i++) {
            exportSingleCalendar(calendarToExport.get(i), dir.getAbsolutePath(), i);
        }

    }

    private void exportSingleCalendar(State state, String route, int calendar) {

        File file = new File(route + "/Calendario " + calendar + ".xlsx");
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

        int j = 1;
        for (; j < teamDate.size() - 1; j++) {
            ArrayList<Integer> date = teamDate.get(j);
            row = spreadsheet.createRow(j);
            for (int k = 0; k < date.size(); k++) {
                int posTeam = teamDate.get(j).get(k);
                String team = getAcronyms().get(posTeam);
                Cell cell = row.createCell(k);
                cell.setCellStyle(style);
                cell.setCellValue(team);
            }
        }
        j++;

        row = spreadsheet.createRow(j);
        Cell cell1 = row.createCell(0);
        cell1.setCellStyle(style);
        cell1.setCellValue("Distancia total(km): ");

        Cell cell2 = row.createCell(1);
        cell2.setCellStyle(style);
        cell2.setCellValue(Distance.getInstance().calculateCalendarDistance(state));


        //autosize each column of the excel document
        for (int i = 0; i < row.getLastCellNum(); i++) {
            spreadsheet.autoSizeColumn(i);
        }

        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file.getAbsolutePath());
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

    public void addModifyTeamToData(String teamName, String acronym, String location, Double[] distances, int pos) throws IOException {


        FileInputStream fis = new FileInputStream(DATA);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet xssfSheet = workbook.getSheetAt(0);


        XSSFFont headerCellFont = workbook.createFont();
        headerCellFont.setBold(true);
        headerCellFont.setColor(IndexedColors.WHITE.getIndex());
        headerCellFont.setFontHeightInPoints((short) 11);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(headerCellFont);

        //int addPos = Controller.getSingletonController().getTeams().size() + 1;
        Cell cell = xssfSheet.getRow(0).createCell(pos);
        cell.setCellStyle(style);
        cell.setCellValue(acronym);

        style = workbook.createCellStyle();
        headerCellFont = workbook.createFont();
        headerCellFont.setBold(false);
        headerCellFont.setFontHeightInPoints((short) 12);
        style.setFont(headerCellFont);


        for (int i = 1; i < distances.length + 1; i++) {
            cell = xssfSheet.getRow(i).createCell(pos);
            cell.setCellStyle(style);
            cell.setCellValue(distances[i - 1]);
        }

        Row row = xssfSheet.createRow(pos);
        cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue(teamName);

        for (int i = 1; i < distances.length + 1; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(distances[i - 1]);
        }

        if (pos == (getTeams().size() + 1)) {
            cell = row.createCell(distances.length + 1);
            cell.setCellStyle(style);
            cell.setCellValue(0);
        }

        XSSFSheet locationSheet = workbook.getSheetAt(1);
        cell = locationSheet.getRow(0).createCell(pos - 1);
        cell.setCellValue(location);

        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(DATA);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeTeamFromData(int pos) throws IOException {

        removeTeamFXML(pos);
        FileInputStream fis = new FileInputStream(DATA);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet xssfSheet = workbook.getSheetAt(0);


        for (int i = 0; i < getTeams().size() + 1; i++) {
            Cell cell = xssfSheet.getRow(i).getCell(pos);
            xssfSheet.getRow(i).removeCell(cell);
        }

        if (pos < xssfSheet.getRow(0).getLastCellNum()) {
            xssfSheet.shiftColumns(pos + 1, xssfSheet.getRow(0).getLastCellNum(), -1);
        }

        Row row = xssfSheet.getRow(pos);
        xssfSheet.removeRow(row);

        if (pos < xssfSheet.getLastRowNum()) {
            xssfSheet.shiftRows(pos + 1, xssfSheet.getLastRowNum(), -1);
        }

        XSSFSheet locationsSheet = workbook.getSheetAt(1);
        Cell cell = locationsSheet.getRow(0).getCell(pos - 1);
        locationsSheet.getRow(0).removeCell(cell);

        if (pos - 1 < locationsSheet.getRow(0).getLastCellNum() - 1) {
            locationsSheet.shiftColumns(pos, locationsSheet.getRow(0).getLastCellNum() - 1, -1);
        }

        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(DATA);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTeamToFXML(String teamName, String acronym, String location) {
        try {

            //////TERMINAR METODOS DE INSERTAR ELIMINAR Y MODIFICAR

            SAXBuilder sax = new SAXBuilder();
            // XML is a local file
            Document doc = sax.build(new File(TEAMS));

            Element rootNode = doc.getRootElement();
            List<Element> list = rootNode.getChildren("team");

            Element team = new Element("team");

            team.addContent(new Element("name").setText(teamName));
            team.addContent(new Element("acronym").setText(acronym));
            team.addContent(new Element("region").setText(location));
            rootNode.addContent(team);


            XMLOutputter xmlOutput = new XMLOutputter();

            // display nice nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(TEAMS));

            // xmlOutput.output(doc, System.out);

            System.out.println("File updated!");
        } catch (IOException io) {
            io.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

    public void modifyTeamFXML(String teamName, String acronym, String location, int pos) {
        try {

            SAXBuilder sax = new SAXBuilder();
            // XML is a local file
            Document doc = sax.build(new File(TEAMS));

            Element rootNode = doc.getRootElement();
            List<Element> list = rootNode.getChildren("team");


            XMLOutputter xmlOutput = new XMLOutputter();

            // display nice nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(TEAMS));

            // xmlOutput.output(doc, System.out);

            System.out.println("File updated!");
        } catch (IOException io) {
            io.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

    public void removeTeamFXML(int pos) {
        try {

            SAXBuilder sax = new SAXBuilder();
            // XML is a local file
            Document doc = sax.build(new File(TEAMS));

            Element rootNode = doc.getRootElement();
            List<Element> list = rootNode.getChildren("team");

            for (Element teamElement : list) {

                if (list.indexOf(teamElement) == pos) {
                    rootNode.removeContent(teamElement);
                    break;
                }
            }

            XMLOutputter xmlOutput = new XMLOutputter();

            // display nice nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(TEAMS));

            // xmlOutput.output(doc, System.out);

            System.out.println("File updated!");
        } catch (IOException io) {
            io.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }

}
