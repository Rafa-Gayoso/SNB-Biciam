package utils;

import controller.CalendarController;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import execute.Executer;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import operators.initialSolution.InitialSolutionType;
import operators.interfaces.ICreateInitialSolution;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFRow;
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
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class DataFiles implements ICreateInitialSolution {

    private static final String MUTATIONS = "config_files" + File.separator + "Mutations.xml";
    private static final String HEURISTICS = "config_files" + File.separator + "Heuristics.xml";
    private static final String TEAMS = "config_files" + File.separator + "Teams.xml";
    private static final String DATA = "config_files" + File.separator + "Data.xlsx";
    private static DataFiles singletonFiles;//Singleton Pattern
    private ArrayList<String> teams;//List of resources.teams
    private ArrayList<String> acronyms;
    private ArrayList<String> locations;
    private ArrayList<String> mutations;
    private ArrayList<ArrayList<String>> mutationsConfiguration;
    private ArrayList<String> heuristics;
    private ArrayList<TeamsPairDistance> teamsPairDistances;//List of LocalVisitorDistance

    private DataFiles() {
        this.teams = new ArrayList<>();
        this.acronyms = new ArrayList<>();
        this.teamsPairDistances = new ArrayList<>();
        this.mutations = new ArrayList<>();
        this.mutationsConfiguration = new ArrayList<>();
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

                ArrayList<String> config = new ArrayList<>();
                String name = target.getChildText("name");
                String configuration = target.getChildText("configuration");
                mutations.add(name);
                config.add(configuration);
                mutationsConfiguration.add(config);


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
            CalendarState state = (CalendarState) calendarToExport.get(i);
            exportSingleCalendar(state, dir.getAbsolutePath());
        }

    }

    private void exportSingleCalendar(CalendarState state, String route) {

        File file = new File(route + "/Calendario " + state.getConfiguration().getCalendarId() + ".xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet spreadsheet = workbook.createSheet("Itinerario");
        CalendarConfiguration configuration = state.getConfiguration();
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

        for(int i=0; i < Days.values().length; i++){
            String day = Days.values()[i].toString();
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(day);
        }

        //Header of the itinerary
        for (int j = 0; j < teamDate.get(0).size(); j++) {
            int posTeam = teamDate.get(0).get(j);
            String team = getTeams().get(posTeam);
            Cell cell = row.createCell(j+7);
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
                Cell cell = row.createCell(k+7);
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


        Sheet spreadsheetData = workbook.createSheet("Data");
        Row rowData = spreadsheetData.createRow(0);
        Cell cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.getCalendarId());

        rowData = spreadsheetData.createRow(1);

        for (int i = 0; i < configuration.getTeamsIndexes().size(); i++){
            cellData = rowData.createCell(i);
            cellData.setCellStyle(style);
            cellData.setCellValue(configuration.getTeamsIndexes().get(i));
        }

        rowData = spreadsheetData.createRow(2);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.isInauguralGame());

        rowData = spreadsheetData.createRow(3);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.isChampionVsSecondPlace());

        rowData = spreadsheetData.createRow(4);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.getChampion());

        rowData = spreadsheetData.createRow(5);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.getSecondPlace());

        rowData = spreadsheetData.createRow(6);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.isSecondRoundCalendar());

        rowData = spreadsheetData.createRow(7);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.isSymmetricSecondRound());

        rowData = spreadsheetData.createRow(8);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.isOccidenteVsOriente());

        rowData = spreadsheetData.createRow(9);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.getMaxLocalGamesInARow());

        rowData = spreadsheetData.createRow(10);
        cellData =  rowData.createCell(0);
        cellData.setCellStyle(style);
        cellData.setCellValue(configuration.getMaxVisitorGamesInARow());
        int currentRow = 10;

        int [][] duelMatrix = configuration.getDuelMatrix();
        for (int i = 0; i < duelMatrix.length; i++) {
            for (int k = 0; k < duelMatrix.length ; k++) {
                System.out.print(duelMatrix[i][k] + " ");
            }
            System.out.println();
        }

        System.out.println("------");
        for(int i =0; i < duelMatrix.length; i++){
            currentRow++;
            rowData = spreadsheetData.createRow(11+i);

            for (int k = 0; k < duelMatrix.length; k++) {
                cellData =  rowData.createCell(k);
                cellData.setCellStyle(style);
                cellData.setCellValue(duelMatrix[i][k]);
                System.out.print(duelMatrix[i][k] + " ");
            }
            System.out.println();
        }
        currentRow++;
        rowData = spreadsheetData.createRow(currentRow);
        for(int k = 0; k < configuration.getRestDates().size(); k++){
            cellData =  rowData.createCell(k);
            cellData.setCellStyle(style);
            cellData.setCellValue(configuration.getRestDates().get(k));
        }

        XSSFFont headerCellDateFont = workbook.createFont();
        headerCellDateFont.setBold(true);
        headerCellDateFont.setColor(IndexedColors.WHITE.getIndex());
        headerCellDateFont.setFontHeightInPoints((short) 15);

        XSSFCellStyle newStyle = workbook.createCellStyle();
        newStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        newStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        newStyle.setFont(headerCellDateFont);

        for (int i = 0; i < state.getCode().size(); i++) {
            Sheet spreadsheetDate = workbook.createSheet("Fecha "+(i+1));
            Row rowLocalVisitor = spreadsheetDate.createRow(0);
            Cell cellLocal=  rowLocalVisitor.createCell(0);
            cellLocal.setCellStyle(newStyle);
            cellLocal.setCellValue("Local");

            Cell cellVisitor=  rowLocalVisitor.createCell(1);
            cellVisitor.setCellStyle(newStyle);
            cellVisitor.setCellValue("Visitante");

            Date date = (Date) state.getCode().get(i);

            for (int n =0; n < date.getGames().size(); n++){
                Row row1 = spreadsheetDate.createRow(n+1);
                cellLocal=  row1.createCell(0);
                cellLocal.setCellValue(DataFiles.getSingletonDataFiles().getTeams().get(date.getGames().get(n).get(0)));

                cellVisitor=  row1.createCell(1);
                cellVisitor.setCellValue(DataFiles.getSingletonDataFiles().getTeams().get(date.getGames().get(n).get(1)));
            }

            for(int k=0; k < rowLocalVisitor.getLastCellNum(); k++){
                spreadsheetDate.autoSizeColumn(k);
            }
        }

        //autosize each column of the excel document
        //for(int i=0; i < row.getLastCellNum(); i++){
            spreadsheet.autoSizeColumn(0);
        //}

        for(int i=0; i < rowData.getLastCellNum(); i++){
            spreadsheetData.autoSizeColumn(i);
        }


        workbook.setSheetHidden(1, true);
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
        notification.setMessage("Calendario exportado con \u00e9xito");
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(2));
    }

    private static void showSuccessfulMessageStatistics() {
        TrayNotification notification = new TrayNotification();
        notification.setTitle("Guardar Estad\u00edsticas");
        notification.setMessage("Estad\u00edsticas exportadas con \u00e9xito");
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(2));
    }

    private static void showSuccessfulMessageConfiguration() {
        TrayNotification notification = new TrayNotification();
        notification.setTitle("Guardar Configuraci\u00f3n");
        notification.setMessage("Configuraci\u00f3n exportada con \u00e9xito");
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(2));
    }


    public ArrayList<ArrayList<String>> getMutationsConfiguration() {
        return mutationsConfiguration;
    }

    public void setMutationsConfiguration(ArrayList<ArrayList<String>> mutationsConfiguration) {
        this.mutationsConfiguration = mutationsConfiguration;
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

    public CalendarState readExcelItineraryToCalendar(String route) throws IOException {
        CalendarState cal = null;
        try {
            CalendarConfiguration configuration  = new CalendarConfiguration();

            ArrayList<Date> dates = new ArrayList<>();
            //ArrayList<Integer>teamsIndexes = new ArrayList<>();

            FileInputStream fis = new FileInputStream(route);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            XSSFSheet xssfSheetData = workbook.getSheetAt(1);
            Iterator<Row> rowIteratorData = xssfSheetData.iterator();

            configuration.setCalendarId(rowIteratorData.next().getCell(0).getStringCellValue());

            Row rowTeamIndexes = rowIteratorData.next();

            for (Cell cellData : rowTeamIndexes) {
                configuration.getTeamsIndexes().add((int) cellData.getNumericCellValue());
            }
            int [][] duelMatrix = new int [configuration.getTeamsIndexes().size()][configuration.getTeamsIndexes().size()];
            configuration.setInauguralGame(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setChampionVsSecondPlace(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setChampion((int) rowIteratorData.next().getCell(0).getNumericCellValue());
            configuration.setSecondPlace((int) rowIteratorData.next().getCell(0).getNumericCellValue());
            configuration.setSecondRoundCalendar(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setSymmetricSecondRound(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setOccidenteVsOriente(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setMaxLocalGamesInARow((int) rowIteratorData.next().getCell(0).getNumericCellValue());
            configuration.setMaxVisitorGamesInARow((int) rowIteratorData.next().getCell(0).getNumericCellValue());

            for (int i = 0; i < configuration.getTeamsIndexes().size(); i++) {
                Row row = rowIteratorData.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                int cellNumber =0;
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    duelMatrix[i][cellNumber] = (int)cell.getNumericCellValue();
                    cellNumber++;
                }
            }

            configuration.setDuelMatrix(duelMatrix);

            ArrayList<Integer> rest = new ArrayList<>();
            try {
                Row row1 = rowIteratorData.next();
                Iterator<Cell> cellIterator = row1.cellIterator();

                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    if(cell !=null){
                        rest.add((int)cell.getNumericCellValue());
                    }
                }
            }catch (Exception e){

            }

            configuration.setRestDates(rest);

            TTPDefinition.getInstance().setTeamIndexes(configuration.getTeamsIndexes());
            TTPDefinition.getInstance().setSymmetricSecondRound(configuration.isSymmetricSecondRound());
            TTPDefinition.getInstance().setSecondRound(configuration.isSecondRoundCalendar());
            TTPDefinition.getInstance().setCantVecesLocal(configuration.getMaxLocalGamesInARow());
            TTPDefinition.getInstance().setCantVecesVisitante(configuration.getMaxVisitorGamesInARow());
            TTPDefinition.getInstance().setChampionVsSub(configuration.isChampionVsSecondPlace());
            TTPDefinition.getInstance().setFirstPlace(configuration.getChampion());
            TTPDefinition.getInstance().setSecondPlace(configuration.getSecondPlace());
            TTPDefinition.getInstance().setInauguralGame(configuration.isInauguralGame());
            TTPDefinition.getInstance().setOccidentVsOrient(configuration.isOccidenteVsOriente());
            TTPDefinition.getInstance().setCalendarId(configuration.getCalendarId());
            TTPDefinition.getInstance().setDuelMatrix(configuration.getDuelMatrix());
            TTPDefinition.getInstance().setRestIndexes(configuration.getRestDates());


            XSSFSheet xssfSheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = xssfSheet.iterator();
            rowIterator.next();

            if (configuration.isInauguralGame()) {
                rowIterator.next();
                Date date = new Date();
                ArrayList<Integer> pair = new ArrayList<>();
                pair.add(configuration.getChampion());
                pair.add(configuration.getSecondPlace());
                date.getGames().add(pair);
                dates.add(date);
            }


            boolean secondRound = configuration.isSecondRoundCalendar();
            int cantRealRowAdded = 0;
            int restMoment = configuration.getTeamsIndexes().size() - 1;


            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (!secondRound || cantRealRowAdded != restMoment) {

                    Date date = new Date();

                    int pos = 0;
                    for(int i =7; i < row.getLastCellNum(); i++){
                        Cell cell = row.getCell(i);
                        int local = getAcronyms().indexOf(cell.toString());
                        int visitor = configuration.getTeamsIndexes().get(pos);

                        if(local != -1 && visitor != -1){
                            ArrayList<Integer> pair = new ArrayList<>();
                            pair.add(local);
                            pair.add(visitor);


                            if (local != visitor) {
                                date.getGames().add(pair);
                            }

                        }

                        pos++;
                    }

                    if(date.getGames().size() > 0){

                        dates.add(date);
                    }
                }
                cantRealRowAdded++;
            }

            cal = new CalendarState();
            InitialSolutionType initialSolutionType = createSolutionType();
            int type = initialSolutionType.ordinal();
            cal.getCode().addAll(dates);
            cal.setCalendarType(type);
            cal.setConfiguration(configuration);
            workbook.close();
            fis.close();
        }catch (Exception e) {
            TrayNotification notification = new TrayNotification();
            notification.setTitle("Importaci\u00f3n de Calendario");
            notification.setMessage("Archivo con formato incorrecto.");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(2));
        }

        return cal;
    }

    public void exportsStatistics(TableView<TableStatisticsData> table) {
        DirectoryChooser fc = new DirectoryChooser();
        //int pos = CalendarController.selectedCalendar;

        //Set extension filter for text files
        /*FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fc.getExtensionFilters().add(extFilter);*/


        //dc = new DirectoryChooser();
        File f = fc.showDialog(new Stage());
        File dir = new File(f.getAbsoluteFile() + "/Estad\u00edsticas");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir.getAbsolutePath() + "/Estad\u00edsticas.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet spreadsheet = workbook.createSheet();


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

        ObservableList<TableColumn<TableStatisticsData, ?>> columns = table.getColumns();

        for (int i = 0; i < columns.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(columns.get(i).getText());
        }


        //Itinerary
        style = workbook.createCellStyle();
        headerCellFont = workbook.createFont();
        headerCellFont.setBold(false);
        headerCellFont.setFontHeightInPoints((short) 12);


        for (int i =1; i <=table.getItems().size(); i++) {
            row = spreadsheet.createRow(i);

            TableStatisticsData data = table.getItems().get(i-1);
            //
            Cell cellCalendar = row.createCell(0);
            cellCalendar.setCellStyle(style);
            cellCalendar.setCellValue(data.getCalendarId());

            //
            Cell cellCalendarDistance = row.createCell(1);
            cellCalendarDistance.setCellStyle(style);
            cellCalendarDistance.setCellValue(data.getCalendarDistance());

            //
            Cell cellLessTeam = row.createCell(2);
            cellLessTeam.setCellStyle(style);
            cellLessTeam.setCellValue(data.getLessTeam());

            //
            Cell cellLessTeamDistance = row.createCell(3);
            cellLessTeamDistance.setCellStyle(style);
            cellLessTeamDistance.setCellValue(data.getLessTeamDistance());

            //
            Cell cellMoreTeam = row.createCell(4);
            cellMoreTeam.setCellStyle(style);
            cellMoreTeam.setCellValue(data.getMoreTeam());

            //
            Cell cellMoreTeamDistance = row.createCell(5);
            cellMoreTeamDistance.setCellStyle(style);
            cellMoreTeamDistance.setCellValue(data.getMoreTeamDistance());

            Cell cellLocalRestriction = row.createCell(6);
            cellLocalRestriction.setCellStyle(style);
            cellLocalRestriction.setCellValue(data.getLocalRestrictionsVioleted());



            Cell cellvisitorlRestriction = row.createCell(7);
            cellvisitorlRestriction.setCellStyle(style);
            cellvisitorlRestriction.setCellValue(data.getVisitorRestrictionsVioleted());


        }




        //autosize each column of the excel document
        for (int i = 0; i < row.getLastCellNum(); i++) {
            spreadsheet.autoSizeColumn(i);
        }

        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file.getAbsolutePath());
            workbook.write(fileOut);
            fileOut.close();
            showSuccessfulMessageStatistics();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void exportConfiguration(CalendarConfiguration configuration){
        DirectoryChooser fc = new DirectoryChooser();


        //Set extension filter for text files
        /*FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fc.getExtensionFilters().add(extFilter);*/


        //dc = new DirectoryChooser();
        File f = fc.showDialog(new Stage());
        File dir = new File(f.getAbsoluteFile() + "/Configuraci\u00f3n");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir.getAbsolutePath() + "/Configuraci\u00f3n " + configuration.getCalendarId() + ".xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet spreadsheetData = workbook.createSheet("Data");
        Row rowData = spreadsheetData.createRow(0);
        Cell cellData =  rowData.createCell(0);
        cellData.setCellValue(configuration.getCalendarId());

        rowData = spreadsheetData.createRow(1);

        for (int i = 0; i < configuration.getTeamsIndexes().size(); i++){
            cellData = rowData.createCell(i);
            cellData.setCellValue(configuration.getTeamsIndexes().get(i));
        }

        rowData = spreadsheetData.createRow(2);
        cellData =  rowData.createCell(0);
        cellData.setCellValue(configuration.isInauguralGame());

        rowData = spreadsheetData.createRow(3);
        cellData =  rowData.createCell(0);
        cellData.setCellValue(configuration.isChampionVsSecondPlace());

        rowData = spreadsheetData.createRow(4);
        cellData =  rowData.createCell(0);
        cellData.setCellValue(configuration.getChampion());

        rowData = spreadsheetData.createRow(5);
        cellData =  rowData.createCell(0);

        cellData.setCellValue(configuration.getSecondPlace());

        rowData = spreadsheetData.createRow(6);
        cellData =  rowData.createCell(0);

        cellData.setCellValue(configuration.isSecondRoundCalendar());

        rowData = spreadsheetData.createRow(7);
        cellData =  rowData.createCell(0);

        cellData.setCellValue(configuration.isSymmetricSecondRound());

        rowData = spreadsheetData.createRow(8);
        cellData =  rowData.createCell(0);

        cellData.setCellValue(configuration.isOccidenteVsOriente());

        rowData = spreadsheetData.createRow(9);
        cellData =  rowData.createCell(0);

        cellData.setCellValue(configuration.getMaxLocalGamesInARow());

        rowData = spreadsheetData.createRow(10);
        cellData =  rowData.createCell(0);

        cellData.setCellValue(configuration.getMaxVisitorGamesInARow());


        rowData = spreadsheetData.createRow(11);
        for(int k = 0; k < configuration.getRestDates().size(); k++){
            cellData =  rowData.createCell(k);

            cellData.setCellValue(configuration.getRestDates().get(k));
        }

        for (int i = 0; i < rowData.getLastCellNum(); i++) {
            spreadsheetData.autoSizeColumn(i);
        }

        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file.getAbsolutePath());
            workbook.write(fileOut);
            fileOut.close();
            showSuccessfulMessageStatistics();

        } catch (Exception e) {
            e.printStackTrace();
            TrayNotification notification = new TrayNotification();
            notification.setTitle("Exportaci\u00f3n de Calendario");
            notification.setMessage("Ha ocurrido un Error.");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(2));
        }
    }

    public CalendarConfiguration importConfiguration(String route) throws IOException {
        CalendarConfiguration configuration = null;
        try {
            configuration  = new CalendarConfiguration();

            ArrayList<Date> dates = new ArrayList<>();
            //ArrayList<Integer>teamsIndexes = new ArrayList<>();

            FileInputStream fis = new FileInputStream(route);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            XSSFSheet xssfSheetData = workbook.getSheetAt(1);
            Iterator<Row> rowIteratorData = xssfSheetData.iterator();

            configuration.setCalendarId(rowIteratorData.next().getCell(0).getStringCellValue());

            Row rowTeamIndexes = rowIteratorData.next();

            for (Cell cellData : rowTeamIndexes) {
                configuration.getTeamsIndexes().add((int) cellData.getNumericCellValue());
            }
            int [][] duelMatrix = new int [configuration.getTeamsIndexes().size()][configuration.getTeamsIndexes().size()];
            configuration.setInauguralGame(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setChampionVsSecondPlace(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setChampion((int) rowIteratorData.next().getCell(0).getNumericCellValue());
            configuration.setSecondPlace((int) rowIteratorData.next().getCell(0).getNumericCellValue());
            configuration.setSecondRoundCalendar(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setSymmetricSecondRound(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setOccidenteVsOriente(rowIteratorData.next().getCell(0).getBooleanCellValue());
            configuration.setMaxLocalGamesInARow((int) rowIteratorData.next().getCell(0).getNumericCellValue());
            configuration.setMaxVisitorGamesInARow((int) rowIteratorData.next().getCell(0).getNumericCellValue());


            ArrayList<Integer> rest = new ArrayList<>();
            try {
                Row row1 = rowIteratorData.next();
                Iterator<Cell> cellIterator = row1.cellIterator();

                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    if(cell !=null){
                        rest.add((int)cell.getNumericCellValue());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            configuration.setRestDates(rest);

            TTPDefinition.getInstance().setTeamIndexes(configuration.getTeamsIndexes());
            TTPDefinition.getInstance().setSymmetricSecondRound(configuration.isSymmetricSecondRound());
            TTPDefinition.getInstance().setSecondRound(configuration.isSecondRoundCalendar());
            TTPDefinition.getInstance().setCantVecesLocal(configuration.getMaxLocalGamesInARow());
            TTPDefinition.getInstance().setCantVecesVisitante(configuration.getMaxVisitorGamesInARow());
            TTPDefinition.getInstance().setChampionVsSub(configuration.isChampionVsSecondPlace());
            TTPDefinition.getInstance().setFirstPlace(configuration.getChampion());
            TTPDefinition.getInstance().setSecondPlace(configuration.getSecondPlace());
            TTPDefinition.getInstance().setInauguralGame(configuration.isInauguralGame());
            TTPDefinition.getInstance().setOccidentVsOrient(configuration.isOccidenteVsOriente());
            TTPDefinition.getInstance().setCalendarId(configuration.getCalendarId());
            TTPDefinition.getInstance().setDuelMatrix(configuration.getDuelMatrix());
            TTPDefinition.getInstance().setRestIndexes(configuration.getRestDates());

            workbook.close();
            fis.close();
        }catch (Exception e) {
            TrayNotification notification = new TrayNotification();
            notification.setTitle("Importaci\u00f3n de la Configuraci\u00f3n");
            notification.setMessage("Archivo con formato incorrecto.");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(2));
        }

        return configuration;
    }
}
