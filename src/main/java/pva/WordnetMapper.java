package pva;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import gr.csri.poeticon.praxicon.EntityMngFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

public class WordnetMapper
{
    public WordnetMapper(ISynset _synset)
    {
        synset = _synset;
        fl = new FrequencyList();
        extractKeys();
        extractOccurrences();
        fl.sort();
    }
    ISynset synset;
    FrequencyList fl;
    private ArrayList<String> keys;
    
    public String getBestKey()
    {
        return fl.getTokens()[0];
    }
    
    public int getBestKeyOcc()
    {
        return fl.getFrequency(fl.getTokens()[0]);
    }
    
    private void extractOccurrences() 
    {
        EntityManager emg = EntityMngFactory.getEntityManager();
        String q = "";
        for (String k : keys)
        {
            q = "SELECT COUNT(*) FROM RelationArguments as ra, Relations as r, Concepts as c " +
        "WHERE c.name = '" + k.replaceAll("'", "''") + "' " + "AND ra.concept_ConceptId = c.ConceptId AND ( r.leftArgument_RelationArgumentId = ra.RelationArgumentId OR " +
        "r.rightArgument_RelationArgumentId = ra.RelationArgumentId)";
            Query qr = emg.createNativeQuery(q);
            Number n = ((Number) qr.getSingleResult());
            if (n != null)
            {
                fl.add(k, n.intValue());
            }
            else
            {
                fl.add(k, 0);
            }
        }
    }
    
    private void extractKeys()
    {
        keys = new ArrayList<>();
        java.util.List<IWord> wds = synset.getWords();
        for (int i = 0; i < wds.size(); i++)
        {
            IWord w = wds.get(i);
            keys.add(w.getSenseKey().toString());
        }
    }
}
