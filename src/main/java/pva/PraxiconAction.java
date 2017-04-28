package pva;

import gr.csri.poeticon.praxicon.db.dao.ConceptDao;
import gr.csri.poeticon.praxicon.db.dao.implSQL.ConceptDaoImpl;
import gr.csri.poeticon.praxicon.db.entities.*;
import gr.csri.poeticon.praxicon.db.entities.Concept.ConceptType;
import gr.csri.poeticon.praxicon.db.entities.LanguageRepresentation.Language;
import gr.csri.poeticon.praxicon.db.entities.LanguageRepresentation.PartOfSpeech;
import gr.csri.poeticon.praxicon.db.entities.RelationType.RelationNameForward;
import gr.csri.poeticon.praxicon.db.entities.VisualRepresentation.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PraxiconAction
{
    Concept goal;
    Concept tool;
    Concept object;
    Concept super_goal;
    Concept full_action;
    LanguageRepresentation lang_repr;
    VisualRepresentation visual_repr;
    ConceptDao cd;
    List<Concept> concepts;
    List<Relation> relations;
    List<RelationSet> relationSets;
    String actionId;
    String outDir;
    List<Concept> relConcepts;
    List<RelationNameForward> relFw;
    List<VisualRepresentation> multiVr;
    long startTime, endTime;
    boolean continuous = false;
    Concept continuousConcept;
    String wn3continuousKey = "continuous%3:00:01::";
    boolean videoFragment = false;
    
    public PraxiconAction()
    {
        this("0");
    }
    
    public PraxiconAction(String id)
    {
        cd = new ConceptDaoImpl();
        concepts = new ArrayList<>();
        relations = new ArrayList<>();
        relationSets = new ArrayList<>();
        actionId = id;
    }
    
    public void unpackData()
    {
        for (Relation r : relations)
        {
            if (r.getLeftArgument().isConcept() && r.getRelationType().getForwardName() == RelationType.RelationNameForward.ACTION_GOAL)
            {
                setGoal(r.getRightArgument().getConcept().getName());
            }
            else if (r.getLeftArgument().isConcept() && r.getRelationType().getForwardName() == RelationType.RelationNameForward.ACTION_TOOL)
            {
                setTool(r.getRightArgument().getConcept().getName());
            }
            else if (r.getRightArgument().isConcept() && r.getLeftArgument().isConcept() && r.getRelationType().getForwardName() == RelationType.RelationNameForward.ACTION_OBJECT)
            {
                setObject(r.getRightArgument().getConcept().getName());
            }
            else if (r.getLeftArgument().isConcept() && r.getRelationType().getForwardName() == RelationType.RelationNameForward.HAS_FREQUENCY && r.getRightArgument().getConcept().getName().equals("wn3continuousKey"))
            {
                setContinuous(true);
            }
            else if (r.getLeftArgument().isConcept() && r.getRightArgument().isConcept() && r.getLeftArgument().isConcept())
            {
                setOtherRel(r.getRelationType().getForwardName(), r.getRightArgument().getConcept().getName(), r.getRightArgument().getConcept().getConceptType());
            }
            else if (r.getLeftArgument().isRelationSet() && r.getRightArgument().isConcept() && r.getRelationType().getForwardName() == RelationType.RelationNameForward.ACTION_GOAL)
            {
                setSuperGoal(r.getRightArgument().getConcept().getName());
            }
        }
        multiVr = new ArrayList<>();
        for (Concept c : concepts)
        {
            List<VisualRepresentation> vrs = c.getVisualRepresentations();
            if (vrs != null && ! vrs.isEmpty())
            {
                for (VisualRepresentation vr : vrs)
                {
                    if (vr.getMediaType() == MediaType.VIDEO && vr.getUri() != null && vr.getName() != null)
                    {
                        //String src = "unknown";
                        //if (vr.getSource() != null && !vr.getSource().isEmpty()) { src = vr.getSource(); }
                        //setVideo(vr.getUri().getPath(),vr.getName(),src);
                        multiVr.add(vr);
                    }
                }
            }
        }
    }
    
    public void setStartEndTime(long st, long et)
    {
        videoFragment = true;
        startTime = st;
        endTime = et;
    }
    
    public void setOutputDir(String dir)
    {
        outDir = dir;
    }
    
    public void setActionId(String id)
    {
        actionId = id;
    }
    
    public void saveAction()
    {
        if (goal == null || tool == null)
        {
            System.err.println("ERROR! Can't save the action: Tool and Goal can not be NULL");
            return;
        }
        if (outDir == null || !new File(outDir).isDirectory())
        {
            System.err.println("ERROR! Can't save the action: Set a valid output directory");
            return;
        }
        concepts.add(goal);
        concepts.add(tool);
        if (object != null)
        {
            concepts.add(object);
        }
        String actionName = formatActionConceptName(goal,tool,object);
        full_action = cd.getConceptByNameExact(actionName);
        if (full_action == null)
        {
            full_action = new Concept();
            full_action.setConceptType(Concept.ConceptType.MOVEMENT);
            full_action.setName(actionName);
            full_action.setSpecificityLevel(Concept.SpecificityLevel.UNKNOWN);
            full_action.setStatus(Concept.Status.CONSTANT);
        }
        if (visual_repr != null)
        {
            List<VisualRepresentation> vrepr = full_action.getVisualRepresentations();
            boolean addVr = true;
            for (VisualRepresentation vr : vrepr)
            {
                if (vr.getUri().equals(visual_repr.getUri()))
                {
                    addVr = false; break;
                }
            }
            if (addVr)
            {
                full_action.addVisualRepresentation(visual_repr);
            }
        }
        if (lang_repr != null)
        {
            List<LanguageRepresentation> lrepr = full_action.getLanguageRepresentations();
            boolean addLr = true;
            for (LanguageRepresentation lr : lrepr)
            {
                if (lr.getText().equals(lang_repr.getText()))
                {
                    addLr = false; break;
                }
            }
            if (addLr)
            {
                full_action.addLanguageRepresentation(lang_repr,true);
            }
        }
        concepts.add(full_action);
        addRelation(full_action,goal,RelationType.RelationNameForward.ACTION_GOAL);
        addRelation(full_action,tool,RelationType.RelationNameForward.ACTION_TOOL);
        addRelation(full_action,object,RelationType.RelationNameForward.ACTION_OBJECT);
        if (continuous)
        {
            addRelation(full_action,continuousConcept,RelationType.RelationNameForward.HAS_FREQUENCY);
        }
        if (relConcepts != null)
        {
            for (int i = 0; i < relConcepts.size(); i++)
            {
                Concept tempC = relConcepts.get(i);
                concepts.add(tempC);
                RelationNameForward tempR = relFw.get(i);
                addRelation(full_action,tempC,tempR);
            }
        }
        if (super_goal != null)
        {
            concepts.add(super_goal);
            RelationSet rs = new RelationSet();
            for (Relation r : relations)
            {
                rs.addRelation(r);
            }
            rs.setName("cane");
            rs.setId(3345L);
            relationSets.add(rs);
            addRelation(rs,super_goal,RelationType.RelationNameForward.ACTION_GOAL);
        }
        
        Utils.exportAllObjectsToXML_noDb(relationSets, concepts, relations, outDir + File.separator + "action_" + actionId + ".xml");
    }
    
    private void addRelation(RelationSet r1, Concept c2, RelationType.RelationNameForward relFw)
    {
        addRelation(r1, c2, relFw, Utils.getBackwardRelation(relFw));
    }
    
    private void addRelation(RelationSet r1, Concept c2, RelationType.RelationNameForward relFw, RelationType.RelationNameBackward relBw)
    {
        Relation rel = new Relation();
        RelationType relType = new RelationType();
        relType.setForwardName(relFw);
        relType.setBackwardName(relBw);
        rel.setRelationType(relType);
        RelationArgument relationArgument1 = new RelationArgument(r1);
        RelationArgument relationArgument2 = new RelationArgument(c2);
        rel.setLeftArgument(relationArgument1);
        rel.setRightArgument(relationArgument2);
        rel.setLinguisticSupport(Relation.LinguisticallySupported.UNKNOWN);
        relations.add(rel);
    }
    
    private void addRelation(Concept c1, Concept c2, RelationType.RelationNameForward relFw)
    {
        addRelation(c1, c2, relFw, Utils.getBackwardRelation(relFw));
    }
    
    private void addRelation(Concept c1, Concept c2, RelationType.RelationNameForward relFw, RelationType.RelationNameBackward relBw)
    {
        Relation rel = new Relation();
        RelationType relType = new RelationType();
        relType.setForwardName(relFw);
        relType.setBackwardName(relBw);
        rel.setRelationType(relType);
        RelationArgument relationArgument1 = new RelationArgument(c1);
        RelationArgument relationArgument2 = new RelationArgument(c2);
        rel.setLeftArgument(relationArgument1);
        rel.setRightArgument(relationArgument2);
        rel.setLinguisticSupport(Relation.LinguisticallySupported.UNKNOWN);
        relations.add(rel);
    }
    
    public void setVideo(String videoUri, String videoId, String videoSource)
    {
        visual_repr = new VisualRepresentation();
        visual_repr.setMediaType(MediaType.VIDEO);
        visual_repr.setName(videoId);
        visual_repr.setSource(videoSource);
        try
        {
            if (videoFragment)
            {
                visual_repr.setUri(videoUri + "#t=" + Utils.millisToSeconds(startTime) + "," + Utils.millisToSeconds(endTime));
            }
            else
            {
                visual_repr.setUri(videoUri);
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(PraxiconAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setContinuous(boolean cont)
    {
        continuous = cont;
        if (continuous)
        {
            continuousConcept = cd.getConceptByNameExact(wn3continuousKey);
            if (continuousConcept == null)
            {
                continuousConcept = new Concept();
                continuousConcept.setConceptType(Concept.ConceptType.FEATURE);
                continuousConcept.setName(wn3continuousKey);
                continuousConcept.setSpecificityLevel(Concept.SpecificityLevel.UNKNOWN);
                continuousConcept.setStatus(Concept.Status.CONSTANT);
            }
        }
    }
    
    public void setLanguageRepr(String text, Language lang, PartOfSpeech pos)
    {
        lang_repr = new LanguageRepresentation();
        lang_repr.setText(text);
        lang_repr.setPartOfSpeech(pos);
        lang_repr.setLanguage(lang);
    }
    
    public void setTool(String _tool)
    {
        tool = cd.getConceptByNameExact(_tool);
        if (tool == null)
        {
            tool = new Concept();
            tool.setConceptType(Concept.ConceptType.ENTITY);
            tool.setName(_tool);
            tool.setSpecificityLevel(Concept.SpecificityLevel.UNKNOWN);
            tool.setStatus(Concept.Status.CONSTANT);
        }
    }
    public void setGoal(String _goal)
    {
        goal = cd.getConceptByNameExact(_goal);
        if (goal == null)
        {
            goal = new Concept();
            goal.setConceptType(Concept.ConceptType.MOVEMENT);
            goal.setName(_goal);
            goal.setSpecificityLevel(Concept.SpecificityLevel.UNKNOWN);
            goal.setStatus(Concept.Status.CONSTANT);
        }
    }
    public void setObject(String _obj)
    {
        object = cd.getConceptByNameExact(_obj);
        if (object == null)
        {
            object = new Concept();
            object.setConceptType(Concept.ConceptType.ENTITY);
            object.setName(_obj);
            object.setSpecificityLevel(Concept.SpecificityLevel.UNKNOWN);
            object.setStatus(Concept.Status.CONSTANT);
        }
    }
    public void setSuperGoal(String _sgoal)
    {
        super_goal = cd.getConceptByNameExact(_sgoal);
        if (super_goal == null)
        {
            super_goal = new Concept();
            super_goal.setConceptType(Concept.ConceptType.MOVEMENT);
            super_goal.setName(_sgoal);
            super_goal.setSpecificityLevel(Concept.SpecificityLevel.UNKNOWN);
            super_goal.setStatus(Concept.Status.CONSTANT);
        }
    }
    public void setOtherRel(RelationType.RelationNameForward relName, String _obj, ConceptType cType)
    {
        if (relConcepts == null)
        {
            relConcepts = new ArrayList<>();
            relFw = new ArrayList<>();
        }
        Concept c = cd.getConceptByNameExact(_obj);
        if (c == null)
        {
            c = new Concept();
            c.setConceptType(cType);
            c.setName(_obj);
            c.setSpecificityLevel(Concept.SpecificityLevel.UNKNOWN);
        }
        relConcepts.add(c);
        relFw.add(relName);
    }
    
    public String getActionName()
    {
        return formatActionConceptName(goal,tool,object);
    }
    
    public String formatActionConceptName(String goal, String tool, String object)
    {
        if (object == null)
        {
            return goal + "#with#" + tool;
        }
        return goal + "#with#" + tool + "#the#" + object;
    }
    
    public String formatActionConceptName(Concept goal, Concept tool, Concept object)
    {
        if (object == null)
        {
            return goal.getName() + "#with#" + tool.getName();
        }
        return goal.getName() + "#with#" + tool.getName() + "#the#" + object.getName();
    }
}
