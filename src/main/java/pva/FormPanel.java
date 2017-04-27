package pva;

import edu.mit.jwi.item.POS;
import gr.csri.poeticon.praxicon.db.entities.Concept;
import gr.csri.poeticon.praxicon.db.entities.RelationType.RelationNameForward;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class FormPanel extends JPanel implements ActionListener
{
    public FormPanel(WordnetPanel _wnp)
    {
        super(new MigLayout());
        wnp = _wnp;
        labSgoal= new JLabel("SUPER_GOAL");
        labGoal = new JLabel("ACTION_GOAL");
        labTool = new JLabel("ACTION_TOOL");
        labObj = new JLabel("ACTION_OBJECT");
        labRepeat = new JLabel("Action Repeat");
        labNumAgents = new JLabel("Num. Agents");
        labStime = new JLabel("startTime");
        labEtime = new JLabel("endTime");
        txtSgoal= new JTextField(15);
        txtGoal = new JTextField(15);
        txtTool = new JTextField(15);
        txtObj = new JTextField(15);
        txtStime = new JTextField(8); txtStime.setFocusable(false);
        txtEtime = new JTextField(8); txtEtime.setFocusable(false);
        txtNumAgents = new JTextField(4);
        loadRelationList();
        comboRel = new JComboBox(otherRel);
        comboRepeat = new JComboBox(new String[]{"No","Yes","continous"});
        txtNumAgents.setText("1");
        labOtherRelList = new ArrayList<>();
        txtOtherRelList = new ArrayList<>();
        
        txtSgoal.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                wnp.table.requestFocus();
                wnp.table.clearSelection();
                wnp.searchWn(txtSgoal.getText(), POS.VERB);
                wnp.setOutputField((JTextField) e.getSource());
                wnp.updateData();
                wnp.table.changeSelection(0, 0, false, false);
            }
        });
        
        txtGoal.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                wnp.table.requestFocus();
                wnp.table.clearSelection();
                wnp.searchWn(txtGoal.getText(), POS.VERB);
                wnp.setOutputField((JTextField) e.getSource());
                wnp.updateData();
                wnp.table.changeSelection(0, 0, false, false);
            }
        });
        
        txtTool.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                wnp.table.requestFocus();
                wnp.table.clearSelection();
                wnp.searchWn(txtTool.getText(), POS.NOUN);
                wnp.setOutputField((JTextField) e.getSource());
                wnp.updateData();
                wnp.table.changeSelection(0, 0, false, false);
            }
        });
        
        txtObj.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                wnp.table.requestFocus();
                wnp.table.clearSelection();
                wnp.searchWn(txtObj.getText(), POS.NOUN); 
                wnp.setOutputField((JTextField) e.getSource());
                wnp.updateData();
                wnp.table.changeSelection(0, 0, false, false);
            }
        });
        
        comboRel.addItemListener(new RelComboItemListener());
        comboRel.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent event) 
            {
                if (event.getKeyChar() == KeyEvent.VK_ENTER && labTempRel != null)
                {
                    labOtherRelList.add(new JLabel(labTempRel.getText()));
                    txtOtherRelList.add(new JTextField(15));
                    
                    txtOtherRelList.get(txtOtherRelList.size()-1).addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            wnp.table.requestFocus();
                            wnp.table.clearSelection();
                            wnp.searchWn(txtOtherRelList.get(txtOtherRelList.size()-1).getText()); 
                            wnp.setOutputField((JTextField) e.getSource());
                            wnp.updateData();
                            wnp.table.changeSelection(0, 0, false, false);
                        }
                    });
                    mainPanel.remove(labTempRel); mainPanel.revalidate();
                    mainPanel.remove(txtTempRel); mainPanel.revalidate();
                    mainPanel.remove(comboRel); mainPanel.revalidate();
                    mainPanel.repaint();
                    labTempRel = null;
                    txtTempRel = null;
                    mainPanel.add(labOtherRelList.get(labOtherRelList.size()-1));
                    mainPanel.add(txtOtherRelList.get(txtOtherRelList.size()-1),"wrap");
                    //comboRel.removeItemAt(comboRel.getSelectedIndex()); 
                    comboRel.setSelectedIndex(0);
                    mainPanel.add(comboRel,"wrap,span");
                    txtOtherRelList.get(txtOtherRelList.size()-1).requestFocus();
                    mainPanel.revalidate();
                }
            }
        });
        comboRel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (e.getModifiers() != 0) {
                    Utils.pressEnter();
                } 
            }
        });
        
        mainPanel = new JPanel(new MigLayout());
        mainPanel.add(labStime); mainPanel.add(txtStime, "wrap");
        mainPanel.add(labEtime); mainPanel.add(txtEtime, "wrap");
        mainPanel.add(Box.createVerticalStrut(20), "wrap, span");
        mainPanel.add(labSgoal); mainPanel.add(txtSgoal, "wrap, span");
        mainPanel.add(Box.createVerticalStrut(20), "wrap, span");
        mainPanel.add(labGoal); mainPanel.add(txtGoal, "wrap, span");// this.add(linkWnButtonGoal,"wrap");
        mainPanel.add(labTool); mainPanel.add(txtTool, "wrap, span"); //this.add(linkWnButtonTool,"wrap");
        mainPanel.add(labObj); mainPanel.add(txtObj, "wrap, span"); //this.add(linkWnButtonObj,"wrap");
        mainPanel.add(labRepeat); mainPanel.add(comboRepeat, "wrap, span");
        mainPanel.add(Box.createVerticalStrut(15), "wrap, span");
        mainPanel.add(comboRel,"wrap, span"); 
        mainPane = new JScrollPane(mainPanel,
                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPane.setPreferredSize(new Dimension(350,400));
        this.add(mainPane);
    }
    
    private JLabel labGoal, labSgoal, labTool, labObj, labRepeat, labNumAgents, labTempRel, labStime, labEtime;
    private JTextField txtGoal, txtTool, txtObj, txtSgoal, txtNumAgents, txtTempRel, txtStime, txtEtime;
    private JComboBox comboRel, comboRepeat;
    private JScrollPane mainPane;
    private JPanel mainPanel;
    private ArrayList<JLabel> labOtherRelList;
    private ArrayList<JTextField> txtOtherRelList;
    private String[] otherRel;
    private String[] otherRel_bw;
    WordnetPanel wnp;
    
    public PraxiconAction getPraxiconAction()
    {
        if (txtGoal.getText() == null || txtGoal.getText().length() == 0 ||
                txtTool.getText() == null || txtTool.getText().length() == 0)
        {
            return null;
        }
        PraxiconAction pa = new PraxiconAction();
        pa.setTool(txtTool.getText());
        pa.setGoal(txtGoal.getText());
        if (txtObj.getText() != null && txtObj.getText().length() > 0) { pa.setObject(txtObj.getText()); }
        if (txtStime.getText() != null && txtStime.getText().length() > 0 && 
                txtEtime.getText() != null && txtEtime.getText().length() > 0)
        { 
            pa.setStartEndTime(Long.parseLong(txtStime.getText()), Long.parseLong(txtEtime.getText()));
        }
        for (int i = 0; i < txtOtherRelList.size(); i++)
        {
            JTextField othC = txtOtherRelList.get(i);
            String othR = labOtherRelList.get(i).getText();
            if (othC.getText() != null && othC.getText().length() > 0) { pa.setOtherRel(RelationNameForward.valueOf(othR), othC.getText(), Concept.ConceptType.UNKNOWN); }
        }
        if (txtSgoal.getText() != null && txtSgoal.getText().length() > 0) { pa.setSuperGoal(txtSgoal.getText()); }
        return pa;
    }
    
    public void clearForm(boolean deleteSuperGoal)
    {
        txtGoal.setText("");
        txtTool.setText("");
        txtObj.setText("");
        if (deleteSuperGoal) { txtSgoal.setText(""); }
        txtStime.setText("0");
        txtEtime.setText("");
        comboRepeat.setSelectedIndex(0);
        comboRel.setSelectedIndex(0);
        txtNumAgents.setText("1");
        for (JLabel temp : labOtherRelList)
        {
            mainPanel.remove(temp);
            mainPanel.revalidate();
        }
        for (JTextField temp : txtOtherRelList)
        {
            mainPanel.remove(temp);
            mainPanel.revalidate();
        }
        labOtherRelList = new ArrayList<>();
        txtOtherRelList = new ArrayList<>();
    }
    
    public void loadAction(PraxiconAction pa)
    {
        if (pa == null) {return;}
        if (pa.goal != null) { txtGoal.setText(pa.goal.getName()); }
        if (pa.tool != null) { txtTool.setText(pa.tool.getName()); }
        if (pa.object != null) { txtObj.setText(pa.object.getName()); }
        if (pa.super_goal != null) { txtSgoal.setText(pa.super_goal.getName()); }
        if (pa.relConcepts != null)
        {
            for (int i = 0; i < pa.relConcepts.size(); i++)
            {
                String rel = pa.relFw.get(i).toString();
                String conc = pa.relConcepts.get(i).getName();
                labTempRel = new JLabel(rel);
                txtTempRel = new JTextField(15);
                txtTempRel.setText(conc);
                mainPanel.add(labTempRel);
                validate();
                mainPanel.add(txtTempRel, "wrap, span");
                validate();
                labOtherRelList.add(new JLabel(labTempRel.getText()));
                txtOtherRelList.add(txtTempRel);
                txtOtherRelList.get(txtOtherRelList.size()-1).addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        wnp.table.requestFocus();
                        wnp.table.clearSelection();
                        wnp.searchWn(txtOtherRelList.get(txtOtherRelList.size()-1).getText()); 
                        wnp.setOutputField((JTextField) e.getSource());
                        wnp.updateData();
                        wnp.table.changeSelection(0, 0, false, false);
                    }
                });
                mainPanel.remove(labTempRel); mainPanel.revalidate();
                mainPanel.remove(txtTempRel); mainPanel.revalidate();
                mainPanel.remove(comboRel); mainPanel.revalidate();
                mainPanel.repaint();
                labTempRel = null;
                txtTempRel = null;
                mainPanel.add(labOtherRelList.get(labOtherRelList.size()-1));
                mainPanel.add(txtOtherRelList.get(txtOtherRelList.size()-1),"wrap");
                //comboRel.removeItemAt(comboRel.getSelectedIndex()); 
                comboRel.setSelectedIndex(0);
                mainPanel.add(comboRel,"wrap,span");
                txtOtherRelList.get(txtOtherRelList.size()-1).requestFocus();
                mainPanel.revalidate();
            }
        }
    }
    
    public void setStartTime(String time)
    {
        this.txtStime.setText(time);
    }
    
    public void setEndTime(String time)
    {
        this.txtEtime.setText(time);
    }
    
    public String getStartTime()
    {
        return this.txtStime.getText();
    }
    
    public String getEndTime()
    {
        return this.txtEtime.getText();
    }
    
    public void selectFirstField()
    {
        this.txtGoal.requestFocus();
    }
    
    private void loadRelationList()
    {
        ArrayList<String> tempRel = Utils.getActionRelationList();
        otherRel = new String[tempRel.size()+1];
        otherRel[0] = "Other Relations";
        otherRel_bw = new String[tempRel.size()+1];
        for (int i = 1; i < tempRel.size()+1; i++)
        {
            otherRel[i] = tempRel.get(i-1);
        }
        /*RelationNameForward[] rf = RelationType.RelationNameForward.values();
        otherRel = new String[rf.length+1];
        otherRel[0] = "Other Relations";
        otherRel_bw = new String[rf.length+1];
        for (int i = 1; i < rf.length+1; i++)
        {
            otherRel[i] = rf[i-1].name();
        }*/
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Utils.pressEnter();
    }
    
    class RelComboItemListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent evt)
        {
            JComboBox cb = (JComboBox) evt.getSource();

            Object item = evt.getItem(); 
            //Utils.pressEnter(); 
            if (evt.getStateChange() == ItemEvent.SELECTED)
            {
                String sel = (String)cb.getSelectedItem();
                if (cb.getSelectedIndex() > 0)
                {
                    labTempRel = new JLabel(sel);
                    txtTempRel = new JTextField(15);
                    mainPanel.add(labTempRel);
                    validate();
                    mainPanel.add(txtTempRel, "wrap, span");
                    validate();
                }
            }
            else if (evt.getStateChange() == ItemEvent.DESELECTED)
            {
                if (labTempRel != null)
                {
                    mainPanel.remove(labTempRel); 
                    mainPanel.revalidate();
                    mainPanel.remove(txtTempRel);
                    mainPanel.revalidate();
                    mainPanel.repaint();
                    labTempRel = null;
                    txtTempRel = null;
                }
            }
        }
    }
}
