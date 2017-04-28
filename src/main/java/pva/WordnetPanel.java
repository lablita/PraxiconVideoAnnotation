package pva;

import edu.mit.jwi.*;
import edu.mit.jwi.item.*;
import edu.mit.jwi.item.POS;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
 
public class WordnetPanel extends JPanel
{
    JTextPane output;
    JList list; 
    JTable table;
    DefaultTableCellRenderer centerAligner;
    JScrollPane tablePane;
    JPanel controlPane;
    JScrollPane outputPane;
    JSplitPane splitPane;
    JPanel topHalf;
    JPanel listContainer;
    JPanel tableContainer;
    DefaultTableModel tabModel;
    JPanel bottomHalf;
    String newline = "\n";
    ListSelectionModel listSelectionModel, listSelectionModel_cols;
    IDictionary wnDict;
    String wnPath;
    String[][] tableData;
    String[] columnNames = { "WN Synset", "DB Occurr."};
    private ArrayList<ISynset> listedSynsets;
    JTextField outputField;
    String insWnSynset = "InsertSynset";

    public WordnetPanel(String _wnPath) {
        super(new BorderLayout());
        wnPath = _wnPath;
        initWordnet(wnPath);
        tableData = new String[0][0];
        initData();
    }
    
    public void setOutputField(JTextField txt)
    {
        outputField = txt;
    }
    
    public void updateData()
    {
        table.validate();
        while (tabModel.getRowCount() > 0)
        {
            tabModel.removeRow(0);
            table.revalidate();
        }
        for (int i = 0; i < tableData.length; i++)
        {
            tabModel.addRow(tableData[i]);
            table.revalidate();
        }
        table.revalidate();
        table.repaint();
        table.requestFocus();
        listSelectionModel.setSelectionInterval(0,0);
    }
    
    public void initData()
    {
        this.setFocusable(true);
        tabModel = new NonEditableTableModel(tableData, columnNames);
        
        table = new JTable(tabModel);
        table.setEnabled(true);
        table.setRequestFocusEnabled(true);
        table.setFocusable(true);
        table.setSurrendersFocusOnKeystroke(true);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        centerAligner = new DefaultTableCellRenderer(); centerAligner.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerAligner);
        table.getColumnModel().getColumn(0).setMinWidth(360);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, insWnSynset);
        table.getActionMap().put(insWnSynset, new EnterAction());
        
        listSelectionModel = table.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
        table.setSelectionModel(listSelectionModel);
        table.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked (MouseEvent me) {
                if (me.getClickCount() == 2) { Utils.pressEnter(); }
        }});
        tablePane = new JScrollPane(table);
     
        controlPane = new JPanel();

        output = new JTextPane();
        output.setEditable(false);
        output.setContentType("text/html");

        output.setMargin(new Insets(5,5,5,5));
        outputPane = new JScrollPane(output,
                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 
        //Do the layout.
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
 
        topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
        listContainer = new JPanel(new GridLayout(1,1));
        tableContainer = new JPanel(new GridLayout(1,1));
        tableContainer.setBorder(BorderFactory.createTitledBorder("Wordnet Synsets"));
        tableContainer.add(tablePane);
        tablePane.setPreferredSize(new Dimension(500, 400));
        
        topHalf.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        topHalf.add(listContainer);
        topHalf.add(tableContainer);
 
        topHalf.setPreferredSize(new Dimension(500, 200));
        splitPane.add(topHalf);
 
        bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.add(controlPane, BorderLayout.PAGE_START);
        bottomHalf.add(outputPane, BorderLayout.CENTER);
        bottomHalf.setPreferredSize(new Dimension(500, 200));
        splitPane.add(bottomHalf);
    }
    
    
    public void searchWn(String x)
    {
        listedSynsets = new ArrayList<>();
        String[][] dataNoun = searchWnByStringPos(x, POS.NOUN);
        String[][] dataVerb = searchWnByStringPos(x, POS.VERB);
        String[][] dataAdj = searchWnByStringPos(x, POS.ADJECTIVE);
        String[][] dataAdv = searchWnByStringPos(x, POS.ADVERB);
        tableData = new String[dataNoun.length+dataVerb.length+dataAdj.length+dataAdv.length][2];
        int i = 0;
        int j = 0;
        while (i < dataNoun.length)
        {
            tableData[j][0] = dataNoun[i][0];
            tableData[j][1] = dataNoun[i][1];
            i++; j++;
        }
        i = 0;
        while (i < dataVerb.length)
        {
            tableData[j][0] = dataVerb[i][0];
            tableData[j][1] = dataVerb[i][1];
            i++; j++;
        }
        j = 0;
        while (i < dataAdj.length)
        {
            tableData[j][0] = dataAdj[i][0];
            tableData[j][1] = dataAdj[i][1];
            i++; j++;
        }
        j = 0;
        while (i < dataAdv.length)
        {
            tableData[j][0] = dataAdv[i][0];
            tableData[j][1] = dataAdv[i][1];
            i++; j++;
        }
    }
    
    public void searchWn(String x, POS pos)
    {
        listedSynsets = new ArrayList<>();
        tableData = searchWnByStringPos(x, pos);
    }
    
    private String[][] searchWnByStringPos(String x, POS pos)
    {
        if (x == null || x.length() == 0)
        {
            return new String[0][0];
        }
        IIndexWord idxWord = wnDict.getIndexWord(x,pos);
        if (idxWord == null)
        {
            return new String[0][0];
        }
        int n = idxWord.getWordIDs().size();
        
        FrequencyList dataFl = new FrequencyList();
        for (int i = 0; i < n; i++)
        {
            IWordID wordID = idxWord.getWordIDs().get(i) ;
            IWord word = wnDict.getWord(wordID) ;
            String line = word.getSynset().getID().toString().replaceAll("^.*-([0-9]+)-.*$", "$1") + " - ";
            ISynset synset = word.getSynset();
            WordnetMapper wm = new WordnetMapper(synset);
            listedSynsets.add(synset);
            for (IWord w : synset.getWords())
            {
                line += w.getLemma() + ", ";
            }
            line = "(" + synset.getPOS().getTag() + ") " + line.substring(0, line.length()-2);
            dataFl.add(line, wm.getBestKeyOcc());
        }
        dataFl.sort();
        String[] lines = dataFl.getTokens();
        String[][] data = new String[lines.length][2];
        for (int i = 0; i < dataFl.size(); i++)
        {
            data[i][0] = lines[i];
            data[i][1] = "" + dataFl.getFrequency(lines[i]);
        }
        return data;
    }
    
    public void initWordnet(String wnhome)
    {
        try
        {
            String path = wnhome + File.separator + "dict" ;
            URL url = new File(path).toURI().toURL();
            wnDict = new edu.mit.jwi.Dictionary ( url ) ;
            wnDict.open() ;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

 
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            int ind = -1;
            if (lsm.isSelectionEmpty()) {
                output.setText("");
            } else {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        ind = i;
                    }
                }
            }
            if (ind > -1)
            {
                String outText = "<html>";
                if (!listedSynsets.isEmpty())
                {
                    outText += listedSynsets.get(ind).getID().toString().replaceAll("^.*-([0-9]+)-.*$", "$1") + " - <b>";
                    for (IWord w : listedSynsets.get(ind).getWords())
                    {
                        outText += w.getLemma() + ", ";
                    }
                    outText = outText.substring(0, outText.length()-2);
                    outText += "</b><br /><br />";
                    outText += listedSynsets.get(ind).getGloss();
                    outText += "<br /><br /><b>Hypernyms</b><br />";
                    java.util.List<ISynsetID> hypernyms = listedSynsets.get(ind).getRelatedSynsets(Pointer.HYPERNYM);
                    for (ISynsetID h : hypernyms)
                    {
                        outText += h.toString().replaceAll("^.*-([0-9]+)-.*$", "$1") + " - ";
                        for (IWord w : wnDict.getSynset(h).getWords())
                        {
                            outText += w.getLemma() + ", ";
                        }
                        outText = outText.substring(0, outText.length()-2);
                        outText += "<br />";
                    }
                    outText += "<br /><b>Hyponyms</b><br />";
                    java.util.List<ISynsetID> hyponyms = listedSynsets.get(ind).getRelatedSynsets(Pointer.HYPONYM);
                    for (ISynsetID h : hyponyms)
                    {
                        outText += h.toString().replaceAll("^.*-([0-9]+)-.*$", "$1") + " - ";
                        for (IWord w : wnDict.getSynset(h).getWords())
                        {
                            outText += w.getLemma() + ", ";
                        }
                        outText = outText.substring(0, outText.length()-2);
                        outText += "<br />";
                    }
                }
                outText += "</html>";
                output.setText(outText);
                //output.setCaretPosition(output.getDocument().getLength());
                output.setCaretPosition(0);
            }
        }
    }
    
    private class EnterAction extends AbstractAction
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JTable t = (JTable) e.getSource();
            int ind = t.getSelectedRow();
            if (listedSynsets != null && !listedSynsets.isEmpty())
            {
                ISynset syn = listedSynsets.get(ind);
                WordnetMapper wm = new WordnetMapper(syn);
                java.util.List<IWord> wds = syn.getWords();
                if (wds.size() > 0 && outputField != null)
                {
                    outputField.setText(wm.getBestKey());
                    outputField.requestFocus();
                    outputField.transferFocus();
                }
            }
        }
    }
    
    
}
