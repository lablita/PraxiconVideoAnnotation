package pva;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import gr.csri.poeticon.praxicon.CreateNeo4JDB;
import gr.csri.poeticon.praxicon.XmlUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;


public class PraxiVideoAnnotation {

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run()
            {
              PraxiVideoAnnotation a = new PraxiVideoAnnotation();
              a.loadConfig();
              a.createMainPanel();
            }
          });
    }
    
    Properties configProp;
    EmbeddedMediaPlayerComponent playerComp;
    String wnhome;
    String neo4jPath;

    private void loadConfig()
    {
        InputStream input = null;
        try
        {
            configProp = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            input = loader.getResourceAsStream("config.properties");
            configProp.load(input);
            wnhome = configProp.getProperty("WN_HOME");
            neo4jPath = configProp.getProperty("NEO4J_DB_PATH");
	}
        catch (IOException ex)
        {
            System.err.println("Error! Can't load configuration file.");
	}
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void createMainPanel() 
    {
        JFrame frame = new JFrame();
        frame.setLayout(new MigLayout());
        frame.setTitle("Praxicon Video Annotation");
        
        WordnetPanel wnp = new WordnetPanel(wnhome);
        FormPanel formPanel = new FormPanel(wnp);
        formPanel.setStartTime("0");
        VideoPanel videoPanel = new VideoPanel(formPanel);
        videoPanel.load();
        ButtonsPanel butPanel = new ButtonsPanel(neo4jPath);
        ActionListPanel actPanel = new ActionListPanel();
        StatusBarPanel statBarPanel = new StatusBarPanel();
        PlaylistPanel playlist = new PlaylistPanel();
        
        frame.add(videoPanel, Panels.VIDEO_PLAYER);
        frame.add(formPanel, Panels.FORM);
        frame.add(wnp, "wrap", Panels.WORDNET); 
        frame.add(actPanel, Panels.ACTION_LIST);
        frame.add(butPanel, "span", Panels.BUTTONS);
        frame.add(statBarPanel,"south", Panels.STATUS_BAR);
        frame.add(playlist,"west", Panels.VIDEO_PLAYLIST);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }
    
}
