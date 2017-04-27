package pva;

import gr.csri.poeticon.praxicon.CreateNeo4JDB;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class ButtonsPanel extends JPanel
{
    public ButtonsPanel(String neo4j_db_path)
    {
        this();
        neo4jDbPath = neo4j_db_path;
    }
    public ButtonsPanel()
    {
        super(new MigLayout("","[center]20[center]20[center]20[center]160[center]",""));
        
        fc = new JFileChooser();
        butSave = this.createIconButton("add.png", 20, 20, "Add action");
        butClear = this.createIconButton("clear.png", 20, 20, "Clear form");
        butDel = this.createIconButton("discard.png", 20, 20, "Discard current scene");
        butSkip = this.createIconButton("next.png", 20, 20, "Skip current scene");
        butLoad = this.createIconButton("open.png", 20, 20, "Load video list");
        butReset = this.createIconButton("reset.png", 20, 20, "Delete annotated actions for this scene");
        butSaveFile = this.createIconButton("save.png", 20, 20, "Save action(s) to file");
        butImportDb = this.createIconButton("db.png", 20, 20, "Import all the annotation to Praxicon DB");
        butGraphDb = this.createIconButton("neo4j.png", 20, 20, "Export PraxiconDB to Neo4J Graph Database");
        
        toggleButtons(false);
        
        butLoad.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                int returnVal = fc.showOpenDialog(butPanel);

                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile();
                    PlaylistPanel pp = (PlaylistPanel) butPanel.getParent().getComponent(Panels.VIDEO_PLAYLIST);
                    ActionListPanel ap = (ActionListPanel) butPanel.getParent().getComponent(Panels.ACTION_LIST);
                    StatusBarPanel sbp = (StatusBarPanel) butPanel.getParent().getComponent(Panels.STATUS_BAR);
                    ap.removeAnyAction();
                    AnnotatedVideoList avl = new AnnotatedVideoList(file.getAbsolutePath());
                    avl.loadFromFile();
                    String repoSrc = JOptionPane.showInputDialog(null, "Set the source of the loaded video (repository name)", "Video Source", JOptionPane.PLAIN_MESSAGE);
                    if (repoSrc != null && repoSrc.trim().length() > 0)
                    {
                        avl.setGlobalSource(repoSrc);
                        sbp.setSource(repoSrc);
                    }
                    sbp.setMessage(avl.getAnnotationStats());
                    pp.loadList(avl);
                    toggleButtons(true);
                }
            }
        });
        butSkip.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                PlaylistPanel pp = (PlaylistPanel) butPanel.getParent().getComponent(Panels.VIDEO_PLAYLIST);
                ActionListPanel ap = (ActionListPanel) butPanel.getParent().getComponent(Panels.ACTION_LIST);
                ap.removeAnyAction();
                if (pp.avl == null) { return; }
                pp.avl.setSceneToSkip(pp.currentIdScene, true);
                pp.repaint();
                pp.table.requestFocus();
                pp.table.setRowSelectionInterval(pp.currentIndex+1, pp.currentIndex+1);
                StatusBarPanel sbp = (StatusBarPanel) butPanel.getParent().getComponent(Panels.STATUS_BAR);
                sbp.setMessage(pp.avl.getAnnotationStats());
                Utils.pressEnter();
            }
        });
        butDel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                PlaylistPanel pp = (PlaylistPanel) butPanel.getParent().getComponent(Panels.VIDEO_PLAYLIST);
                if (pp.avl == null) { return; }
                pp.avl.setSceneToDelete(pp.currentIdScene, true);
                pp.repaint();
                pp.table.requestFocus();
                pp.table.setRowSelectionInterval(pp.currentIndex+1, pp.currentIndex+1);
                StatusBarPanel sbp = (StatusBarPanel) butPanel.getParent().getComponent(Panels.STATUS_BAR);
                sbp.setMessage(pp.avl.getAnnotationStats());
                Utils.pressEnter();
            }
        });
        butSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                ActionListPanel ap = (ActionListPanel) butPanel.getParent().getComponent(Panels.ACTION_LIST);
                FormPanel fp = (FormPanel) butPanel.getParent().getComponent(Panels.FORM);
                PraxiconAction pa = fp.getPraxiconAction();
                if (pa != null)
                {
                    PraxiconAction tempPa = ap.findActionByName(pa.getActionName());
                    if (tempPa == null)
                    {
                        ap.addAction(pa);
                        fp.clearForm(false);
                        fp.requestFocus();
                        fp.selectFirstField();
                    }
                    else
                    {
                        ap.updateAction(pa);
                        fp.clearForm(false);
                        fp.requestFocus();
                        fp.selectFirstField();
                    }
                }
            }
        });
        butClear.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                FormPanel fp = (FormPanel) butPanel.getParent().getComponent(Panels.FORM);
                fp.clearForm(true);
            }
        });
        butReset.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                ActionListPanel ap = (ActionListPanel) butPanel.getParent().getComponent(Panels.ACTION_LIST);
                FormPanel fp = (FormPanel) butPanel.getParent().getComponent(Panels.FORM);
                fp.clearForm(true);
                ap.removeAnyAction();
            }
        });
        butSaveFile.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                ActionListPanel ap = (ActionListPanel) butPanel.getParent().getComponent(Panels.ACTION_LIST);
                if (ap.isEmpty()) { return; }
                FormPanel fp = (FormPanel) butPanel.getParent().getComponent(Panels.FORM);
                ap.writeToFile();
                fp.clearForm(true);
                PlaylistPanel pp = (PlaylistPanel) butPanel.getParent().getComponent(Panels.VIDEO_PLAYLIST);
                if (pp.avl == null) { return; }
                pp.avl.setAnnotationDone(pp.currentIdScene, "action_" + pp.currentIdScene, true);
                ap.removeAnyAction();
                pp.repaint();
                pp.table.requestFocus();
                pp.table.setRowSelectionInterval(pp.currentIndex+1, pp.currentIndex+1);
                StatusBarPanel sbp = (StatusBarPanel) butPanel.getParent().getComponent(Panels.STATUS_BAR);
                sbp.setMessage(pp.avl.getAnnotationStats());
                //Utils.pressEnter();
            }
        });
        
        butImportDb.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JPanel butPanel = (JPanel) ((JButton) e.getSource()).getParent();
                PlaylistPanel pp = (PlaylistPanel) butPanel.getParent().getComponent(Panels.VIDEO_PLAYLIST);
                String dir = pp.avl.getWorkDir();
                if (dir != null && dir.length() > 0)
                {
                    Utils.importIntoDb(dir,true);
                }
            }
        });
        
        butGraphDb.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                boolean showDial = true;
                if (neo4jDbPath != null && neo4jDbPath.length() > 0)
                {
                    File f = new File(neo4jDbPath);
                    if (f.exists() && f.isDirectory())
                    {
                        showDial = false;
                    }
                }
                if (showDial)
                {
                    neo4jDbPath = JOptionPane.showInputDialog(null, "Set the Neo4J db data directory: default is \"data/databases/graph.db\" in the neo4J installation folder", "Neo4J DB Path", JOptionPane.PLAIN_MESSAGE);
                    if (neo4jDbPath == null) { return; }
                    File f = new File(neo4jDbPath);
                    if (!f.exists() || !f.isDirectory()) {return;}
                }
                CreateNeo4JDB.main(new String[]{neo4jDbPath});
            }
        });
        
        this.add(new JLabel("Add / Save"));
        this.add(new JLabel("Skip / Discard Scene"));
        this.add(new JLabel("Clear Annotation"));
        this.add(new JLabel("Load Video List"));
        this.add(new JLabel("DB Import"), "wrap");
        this.add(butSave);
        this.add(butSkip);
        this.add(butClear);
        this.add(butLoad);
        this.add(butImportDb,"wrap");
        this.add(butSaveFile);
        this.add(butDel);
        this.add(butReset);
        this.add(Box.createHorizontalGlue());
        this.add(butGraphDb);
        
    }
    
    private JButton butSave, butClear, butDel, butSkip, butLoad, butReset, butSaveFile, butImportDb, butGraphDb;
    private JFileChooser fc;
    String neo4jDbPath;
    
    
    
    private void toggleButtons(boolean enable)
    {
        butSave.setEnabled(enable);
        butClear.setEnabled(enable);
        butDel.setEnabled(enable);
        butSkip.setEnabled(enable);
        butReset.setEnabled(enable);
        butSaveFile.setEnabled(enable);
        butImportDb.setEnabled(enable);
        butGraphDb.setEnabled(enable);
    }
    
    private JButton createIconButton(String imgName, int width, int height, String text)
    {
        JButton but = new JButton();
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icons/" + imgName));
        Image img = icon.getImage() ;  
        Image newimg = img.getScaledInstance( width, height, java.awt.Image.SCALE_SMOOTH ) ;  
        icon = new ImageIcon( newimg );
        but.setIcon(icon);
        but.setToolTipText(text);
        return but;
    }
}
