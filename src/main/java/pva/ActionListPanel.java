package pva;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ActionListPanel extends JPanel
{
    public ActionListPanel()
    {
        super(new GridLayout(1,1));
        
        table = new JTable();
        tabModel = new NonEditableTableModel(new String[] { "Action List" },0);
        //table.setPreferredSize(new Dimension(520,75));
        table.setModel(tabModel);
        centerAligner = new DefaultTableCellRenderer(); centerAligner.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAligner);
        
        
        tablePane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tablePane.setPreferredSize(new Dimension(535,100));
        this.add(tablePane);
        actions = new ArrayList<>();
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, editAction);
        table.getActionMap().put(editAction, new ActionListPanel.EnterAction());
        table.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked (MouseEvent me) {
                if (me.getClickCount() == 2) { Utils.pressEnter(); }
        }});
    }
    
    JScrollPane tablePane;
    JTable table;
    DefaultTableModel tabModel;
    DefaultTableCellRenderer centerAligner;
    List<PraxiconAction> actions;
    String editAction = "Edit Action";
    
    public void addAction(PraxiconAction act)
    {
        actions.add(act);
        tabModel.addRow(new String[]{act.getActionName()});
        table.revalidate();
        table.setRowSelectionInterval(actions.size()-1, actions.size()-1);
    }
    public void removeAction(int ind)
    {
        actions.remove(ind);
        tabModel.removeRow(ind);
        table.revalidate();
        //table.setRowSelectionInterval(actions.size()-1, actions.size()-1);
    }
    public void removeAnyAction()
    {
        int n = actions.size();
        for (int i = 0; i < n; i++)
        {
            removeAction(0);
        }
    }
    public boolean isEmpty()
    {
        if (actions.size() == 0)
        {
            return true;
        }
        return false;
    }
    
    public void writeToFile()
    {
        String videoId = "xxx";
        String wDir = System.getProperty("user.home");
        PlaylistPanel playlist = (PlaylistPanel) this.getParent().getComponent(Panels.VIDEO_PLAYLIST);
        //FormPanel formPanel = (FormPanel) this.getParent().getComponent(Panels.FORM);
        if (playlist.currentIdScene != null)
        {
            videoId = playlist.currentIdScene;
        }
        AnnotatedVideo av = null;
        if (playlist.avl != null)
        {
            wDir = playlist.avl.getWorkDir();
            av = playlist.avl.getVideoByIndex(playlist.currentIndex);
        }
        int i = 0;
        for (PraxiconAction pa : actions)
        {
            pa.setActionId(videoId + "_" + i);
            pa.setOutputDir(wDir);
            pa.setVideo(av.video_url, av.video_id, av.source);
            i++;
            pa.saveAction();
        }
        
    }
    
    public void updateAction(PraxiconAction act)
    {
        int ind = 0;
        for (int i = 0; i < actions.size(); i++)
        {
            PraxiconAction pi = actions.get(i);
            if (pi.getActionName().equals(act.getActionName()))
            {
                actions.set(i, act);
                ind = i;
                break;
            }
        }
        table.setRowSelectionInterval(ind, ind);
    }
    
    public PraxiconAction findActionByName(String actName)
    {
        for (PraxiconAction act : actions)
        {
            if (act.getActionName().equals(actName))
            {
                return act;
            }
        }
        return null;
    }
    
    private class EnterAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JTable t = (JTable) e.getSource();
            int ind = t.getSelectedRow();
            String name = (String) t.getModel().getValueAt(ind, 0);
            JPanel root = (JPanel) t.getParent().getParent().getParent().getParent();
            FormPanel formPanel = (FormPanel) root.getComponent(Panels.FORM);
            formPanel.clearForm(true);
            PraxiconAction pa = actions.get(ind);
            formPanel.loadAction(pa);
            removeAction(ind);
        }
    }
}
