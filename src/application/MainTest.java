package application;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MainTest {
    private static final String FILENAME = "files/Heuristics.xml";
    public static void main(String[] args) {


        try {

            SAXBuilder sax = new SAXBuilder();
            // XML is a local file
            Document doc = sax.build(new File(FILENAME));

            Element rootNode = doc.getRootElement();
            List<Element> list = rootNode.getChildren("heuristic");

            for (Element target : list) {


                String name = target.getChildText("name");
                /*String acronym = target.getChildText("configuration");


                String [] params = acronym.split(",");*/

                System.out.printf("Name : %s%n", name);

                /*System.out.printf("Acronym : %s%n", acronym);

                System.out.print(params.length);
                for(int i =0; i < params.length; i++){
                    System.out.print(params[i] + " ");
                }
                System.out.println();*/
            }

        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
    }
}
