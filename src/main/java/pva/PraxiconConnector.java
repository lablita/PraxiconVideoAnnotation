package pva;

import gr.csri.poeticon.praxicon.EntityMngFactory;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringEscapeUtils;

public class PraxiconConnector
{
    
    // search the concept and return the number of relation this concept is linked to (in praxiconDB)
    public int getNumConceptRelations(String conceptKey)
    {
        EntityManager emg = EntityMngFactory.getEntityManager();
        String q = "SELECT COUNT(*) FROM RelationArguments as ra, Relations as r, Concepts as c WHERE c.name = '" + conceptKey.replace("'", "''") + "' " 
                + "AND ra.concept_ConceptId = c.ConceptId AND ( r.leftArgument_RelationArgumentId = ra.RelationArgumentId OR " +
                "r.rightArgument_RelationArgumentId = ra.RelationArgumentId)";
        Query qr = emg.createNativeQuery(q);
        Number n = ((Number) qr.getSingleResult());
        if (n != null)
        {
            return n.intValue();
        }
        return 0;
    }
    
    public String formatActionConceptName(String goal, String tool, String object)
    {
        if (object == null)
        {
            return goal + "#with#" + tool;
        }
        return goal + "#with#" + tool + "#the#" + object;
    }
}
