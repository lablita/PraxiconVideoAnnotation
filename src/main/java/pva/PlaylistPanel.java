package pva;

import pva.AnnotatedVideo.AnnotationStatus;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.codehaus.plexus.util.FileUtils;


public class PlaylistPanel extends JPanel
{
    public PlaylistPanel()
    {
        super(new GridLayout(1,1));
        
        table = new JTable();
        tabModel = new NonEditableTableModel(new String[] { "Video List" },0);
        table.setEnabled(true);
        table.setRequestFocusEnabled(true);
        table.setFocusable(true);
        table.setSurrendersFocusOnKeystroke(true);
        table.setModel(tabModel);
        centerAligner = new DefaultTableCellRenderer(); centerAligner.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerAligner);
        
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, playVideo);
        table.getActionMap().put(playVideo, new PlaylistPanel.EnterAction());
        
        table.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked (MouseEvent me) {
                if (me.getClickCount() == 2) { Utils.pressEnter(); }
        }});

        
        tablePane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tablePane.setPreferredSize(new Dimension(150,500));
        this.add(tablePane);
    }
    
    JScrollPane tablePane;
    JTable table;
    DefaultTableModel tabModel;
    DefaultTableCellRenderer centerAligner;
    AnnotatedVideoList avl;
    List<String> video_ids;
    String playVideo = "Play Video";
    String currentIdScene;
    int currentIndex;
    
    public void loadList(AnnotatedVideoList _avl)
    {
        avl = _avl;
        updateList();
    }
    
    public void updateList()
    {
        avl.sortList();
        video_ids = avl.getVideoIds();
        
        table.validate();
        //table.setPreferredSize(preferredSize);
        while (tabModel.getRowCount() > 0)
        {
            tabModel.removeRow(0);
            table.revalidate();
        }
        for (int i = 0; i < video_ids.size(); i++)
        {
            tabModel.addRow(new String[]{video_ids.get(i)});
            table.revalidate();
        }
        table.getColumnModel().getColumn(0).setCellRenderer(new ColouredCellRenderer(avl));
        table.revalidate();
        table.repaint();
        table.requestFocus();
        table.getSelectionModel().setSelectionInterval(0,0);
    }
    
    
    private class EnterAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JTable t = (JTable) e.getSource();
            int ind = t.getSelectedRow();
            currentIndex = ind;
            currentIdScene = (String) t.getModel().getValueAt(ind, 0);
            String url = avl.getVideoUrl(currentIdScene);
            JComponent a = (JComponent) e.getSource();
            JPanel root = (JPanel) a.getParent().getParent().getParent().getParent();
            VideoPanel vplayer = (VideoPanel) root.getComponent(Panels.VIDEO_PLAYER);
            File temp = new File(System.getProperty("java.io.tmpdir") + File.separator + "annotVideo.mp4");
            vplayer.setVideo(getClass().getClassLoader().getResource("img/downloading.png").getFile());
            vplayer.play();
            try
            {
                FileUtils.copyURLToFile(new URL(url), temp);
                vplayer.setVideo(temp.getAbsolutePath());
                vplayer.play();
            }
            catch (Exception ex)
            {
                vplayer.setVideo(getClass().getClassLoader().getResource("img/videoError.png").getFile());
                vplayer.play();
            }
        }
    }
    
    private class ColouredCellRenderer extends DefaultTableCellRenderer
    {
        ColouredCellRenderer(AnnotatedVideoList _avl)
        {
            super();
            avl = _avl;
            this.setHorizontalAlignment(SwingConstants.CENTER);
        }
        AnnotatedVideoList avl;
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            AnnotatedVideo vid = avl.getVideoByIndex(row);
            Component rendererComp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (vid.status == AnnotationStatus.SKIP)
            {
                rendererComp.setBackground(new Color(255, 239, 105));
            }
            else if (vid.status == AnnotationStatus.DONE)
            {
                rendererComp.setBackground(new Color(114, 198, 108));
            }
            else if (vid.status == AnnotationStatus.CANC)
            {
                rendererComp.setBackground(new Color(216, 99, 99));
            }
            else if (vid.status == AnnotationStatus.TODO)
            {
                rendererComp.setBackground(Color.WHITE);
            }
            if (isSelected)
            {
                rendererComp.setBackground(new Color(154, 182, 237));
            }
            return rendererComp ;
        }
    }
}