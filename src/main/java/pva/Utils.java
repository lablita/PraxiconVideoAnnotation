package pva;

import pva.AnnotatedVideo.AnnotationStatus;
import gr.csri.poeticon.praxicon.XmlUtils;
import gr.csri.poeticon.praxicon.db.entities.CollectionOfObjects;
import gr.csri.poeticon.praxicon.db.entities.Concept;
import gr.csri.poeticon.praxicon.db.entities.Concepts;
import gr.csri.poeticon.praxicon.db.entities.Relation;
import gr.csri.poeticon.praxicon.db.entities.RelationSet;
import gr.csri.poeticon.praxicon.db.entities.RelationSets;
import gr.csri.poeticon.praxicon.db.entities.RelationType.RelationNameBackward;
import gr.csri.poeticon.praxicon.db.entities.RelationType.RelationNameForward;
import gr.csri.poeticon.praxicon.db.entities.Relations;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;

public class Utils
{
    public static String[][] twoDimensionalArrayClone(String[][] a)
    {
        String[][] b = new String[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            b[i] = (String[]) a[i].clone();
        }
        return b;
    }
    
    public static String millisToSeconds(long ms)
    {
        long sec = ms / 1000;
        long mms = ms % 1000;
        return sec + "." + mms;
    }
    
    public static PraxiconAction importActionFromXml_noDb(String xmlFilePath)
    {
        PraxiconAction pa = new PraxiconAction();
        try
        {
            JAXBContext jaxbContext = JAXBContext.
                    newInstance(CollectionOfObjects.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            File xmlFile = new File(xmlFilePath);
            CollectionOfObjects importedCollectionOfObjects =
                    (CollectionOfObjects)jaxbUnmarshaller.unmarshal(xmlFile);
            List<Concepts> listOfConcepts = importedCollectionOfObjects.
                    getConcepts();
            List<RelationSets> listOfRelationSets =
                    importedCollectionOfObjects.
                    getRelationSets();
            List<Relations> listOfRelations = importedCollectionOfObjects.
                    getRelations();
            for (Concepts c : listOfConcepts)
            {
                pa.concepts.addAll(c.getConcepts());
            }
            for (Relations r : listOfRelations)
            {
                pa.relations.addAll(r.getRelations());
            }
            for (RelationSets rs : listOfRelationSets)
            {
                pa.relationSets.addAll(rs.getRelationSets());
            }
            pa.unpackData();
        } catch (JAXBException ex) {
            Logger.getLogger(XmlUtils.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
        return pa;
    }
    
    public static void importIntoDb(String annotationDirectory, boolean moveToDoneDir)
    {
        File f = new File(annotationDirectory);
        if (f.exists() && f.isDirectory())
        {
            FilenameFilter xmlFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    String lowercaseName = name.toLowerCase();
                    if (lowercaseName.endsWith(".xml")) {
                            return true;
                    } else {
                            return false;
                    }
                }
            };
            File[] files = f.listFiles(xmlFilter);
            File outDir = new File(f.getAbsolutePath() + File.separator + "imported");
            for (int i = 0; i < files.length; i++)
            {
                XmlUtils.importObjectsFromXml(files[i].getAbsolutePath());
                if (moveToDoneDir)
                {
                    try {
                        FileUtils.moveFileToDirectory(files[i], outDir, true);
                    } catch (IOException ex) {
                        System.err.println("Error: can't move annotation files to '/imported' directory");
                    }
                }
            }
        }
        else
        {
            System.err.println("Error: invalid annotation directory!");
        }
    }
    
    public static ArrayList<String> getActionRelationList()
    {
        ArrayList<String> rels = new ArrayList<>();
        rels.add(RelationNameForward.HAS_LOCATION.toString());
        rels.add(RelationNameForward.HAS_DIRECTION.toString());
        rels.add(RelationNameForward.HAS_FREQUENCY.toString());
        rels.add(RelationNameForward.ACTION_RESULT.toString());
        rels.add(RelationNameForward.ACTION_AGENT.toString());
        rels.add(RelationNameForward.ACTION_GOAL.toString());
        rels.add(RelationNameForward.ACTION_TOOL.toString());
        rels.add(RelationNameForward.ACTION_OBJECT.toString());
        rels.add(RelationNameForward.EVENT_STEP.toString());
        rels.add(RelationNameForward.HAS_CONDITION.toString());
        rels.add(RelationNameForward.HAS_FORCE.toString());
        rels.add(RelationNameForward.HAS_MOTOR_PROGRAM.toString());
        rels.add(RelationNameForward.HAS_INSTANCE.toString());
        rels.add(RelationNameForward.HAS_PARTIAL_INSTANCE.toString());
        rels.add(RelationNameForward.HAS_SPEED_RATE.toString());
        rels.add(RelationNameForward.HAS_TIME.toString());
        rels.add(RelationNameForward.HAS_VALUE.toString());
        rels.add(RelationNameForward.MORE.toString());
        rels.add(RelationNameForward.LESS.toString());
        rels.add(RelationNameForward.TYPE_TOKEN.toString());
        return rels;
    }
    
    public static RelationNameBackward getBackwardRelation(RelationNameForward fwRel)
    {
        switch (fwRel)
        {
            case ACTION_AGENT: return RelationNameBackward.AGENT_ACTION;
            case ACTION_GOAL: return RelationNameBackward.GOAL_ACTION;
            case ACTION_OBJECT: return RelationNameBackward.OBJECT_ACTION;
            case ACTION_RESULT: return RelationNameBackward.RESULT_ACTION;
            case ACTION_TOOL: return RelationNameBackward.TOOL_ACTION;
            case EVENT_STEP: return RelationNameBackward.STEP_EVENT;
            case HAS_CONDITION: return RelationNameBackward.CONDITION_OF;
            case HAS_DIRECTION: return RelationNameBackward.DIRECTION_OF;
            case HAS_FORCE: return RelationNameBackward.FORCE_OF;
            case HAS_FREQUENCY: return RelationNameBackward.FREQUENCY_OF;
            case HAS_MOTOR_PROGRAM: return RelationNameBackward.MOTOR_PROGRAM_OF;
            case HAS_INSTANCE: return RelationNameBackward.INSTANCE_OF;
            case HAS_LOCATION: return RelationNameBackward.LOCATION_OF;
            case HAS_PARTIAL_INSTANCE: return RelationNameBackward.PARTIAL_INSTANCE_OF;
            case HAS_SPEED_RATE: return RelationNameBackward.SPEED_RATE_OF;
            case HAS_TIME: return RelationNameBackward.TIME_OF;
            case HAS_VALUE: return RelationNameBackward.VALUE_OF;
            case MORE: return RelationNameBackward.NO;
            case LESS: return RelationNameBackward.NO;
            case TYPE_TOKEN: return RelationNameBackward.TOKEN_TYPE;
        }
        return RelationNameBackward.NO;
    }
    
    public static void pressEnter()
    {
        try
        {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_ENTER);r.keyRelease(KeyEvent.VK_ENTER);
        }
        catch (AWTException ex)  { }
    }
    
    public static void exportVideoAnnotationToCsv(List<AnnotatedVideo> vlist, String csvFile)
    {
        File file = new File(csvFile);
        try
        {
            PrintWriter out = new PrintWriter(file);
            for (AnnotatedVideo v : vlist)
            {
                if (v.status == null) { v.status = AnnotationStatus.TODO; }
                if (v.source == null) { v.source = "UNKNOWN"; }
                if (v.annotationFile == null) { v.annotationFile = ""; }
                out.println(v.video_id + "," + v.video_url + "," + v.source + "," + v.status.name() + "," + v.annotationFile);
            }
            out.close();
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("Error: can't load video list from file " + csvFile);
        }
        
    }
    
    public static List<AnnotatedVideo> importVideoAnnotationFromCsv(String csvFile)
    {
        List<AnnotatedVideo> vlist = new ArrayList<>();
        try
        {
            Scanner scanner = new Scanner(new File(csvFile));
            while (scanner.hasNext())
            {
                String line = scanner.nextLine();
                String[] larr = line.split(",");
                if (larr.length < 2) { continue; }
                AnnotatedVideo vid = new AnnotatedVideo();
                vid.video_id = larr[0];
                vid.video_url = larr[1];
                if (larr.length > 2 && larr[2].length() > 0)
                {
                    vid.source = larr[2];
                }
                if (larr.length > 3 && larr[3].length() == 4)
                {
                    vid.status = AnnotationStatus.valueOf(larr[3]);
                }
                if (larr.length > 4 && larr[4].length() > 0)
                {
                    vid.annotationFile = larr[4];
                }
                vlist.add(vid);
            }
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("Error: can't load video list from file " + csvFile);
            return vlist;
        }
        return vlist;
    }
    
    public static void exportConceptsToXML_noDb(List<Concept> conceptsList,
            String xmlFileName) {
        Concepts concepts = new Concepts();
        concepts.setConcepts(new ArrayList<>());
        List<Concept> newConceptsList = new ArrayList<>();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Concepts.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            for (Concept item : conceptsList) {
                if (!newConceptsList.contains(item)) {
                    newConceptsList.add(item);
                }
            }
            // Export concepts to the xml file
            concepts.setConcepts(newConceptsList);
            marshaller.marshal(concepts, new File(xmlFileName));
        } catch (JAXBException ex) {
            Logger.getLogger(XmlUtils.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }
    }
    
    public static void exportRelationsToXML_noDb(List<Relation> relationsList,
            String xmlFileName) {

        Relations relations = new Relations();
        relations.setRelations(new ArrayList<>());
        List<Relation> newRelationsList = new ArrayList<>();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Relations.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            for (Relation item : relationsList) {
                if (!newRelationsList.contains(item)) {
                    newRelationsList.add(item);
                }
            }
            relations.setRelations(newRelationsList);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Export concepts to the xml file
            marshaller.marshal(relations, new File(xmlFileName));
        } catch (JAXBException ex) {
            Logger.getLogger(XmlUtils.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public static void exportRelationSetsToXML_noDb(
            List<RelationSet> relationSetsList, String xmlFileName) {

        RelationSets relationSets = new RelationSets();
        relationSets.setRelationSets(new ArrayList<>());
        List<RelationSet> newRelationSetsList = new ArrayList<>();

        try {
            JAXBContext jaxbContext = JAXBContext.
                    newInstance(RelationSets.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            for (RelationSet item : relationSetsList) {
                if (!newRelationSetsList.contains(item)) {
                    newRelationSetsList.add(item);
                }
            }
            relationSets.setRelationSets(newRelationSetsList);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Export relation sets to the xml file
            marshaller.marshal(relationSets, new File(xmlFileName));
        } catch (JAXBException ex) {
            Logger.getLogger(XmlUtils.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Exports an xml file given a list of relation sets, a list of Concepts
     * and a list of relations. If the file exists, it is overwritten.
     *
     * @param relationSetsList
     * @param conceptsList
     * @param relationsList
     * @param xmlFileName
     */
    public static void exportAllObjectsToXML_noDb(
            List<RelationSet> relationSetsList,
            List<Concept> conceptsList, List<Relation> relationsList,
            String xmlFileName) {
        CollectionOfObjects collectionOfObjects = new CollectionOfObjects();
        RelationSets relationSets = new RelationSets();
        List<RelationSet> newRelationSetsList = new ArrayList<>();
        Concepts concepts = new Concepts();
        List<Concept> newConceptsList = new ArrayList<>();
        Relations relations = new Relations();
        List<Relation> newRelationsList = new ArrayList<>();
        relationSets.setRelationSets(new ArrayList<>());
        concepts.setConcepts(new ArrayList<>());
        relations.setRelations(new ArrayList<>());

        try {
            JAXBContext jaxbContext = JAXBContext.
                    newInstance(CollectionOfObjects.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            for (RelationSet item : relationSetsList) {
                if (!newRelationSetsList.contains(item)) {
                    newRelationSetsList.add(item);
                }
            }

            for (Concept item : conceptsList) {
                if (!newConceptsList.contains(item)) {
                    newConceptsList.add(item);
                }
            }

            for (Relation item : relationsList) {
                if (!newRelationsList.contains(item)) {
                    newRelationsList.add(item);
                }
            }

            concepts.setConcepts(newConceptsList);
            relations.setRelations(newRelationsList);
            relationSets.setRelationSets(newRelationSetsList);

            collectionOfObjects.getConcepts().add(concepts);
            collectionOfObjects.getRelations().add(relations);
            collectionOfObjects.getRelationSets().add(relationSets);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Export all objects to the xml file
            marshaller.marshal(collectionOfObjects, new File(xmlFileName));

        } catch (JAXBException ex) {
            Logger.getLogger(XmlUtils.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
}
