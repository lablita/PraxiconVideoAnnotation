package pva;

import javax.swing.table.DefaultTableModel;

public class NonEditableTableModel extends DefaultTableModel
{

    public NonEditableTableModel(String[][] tableData, String[] columnNames)
    {
        super(tableData, columnNames);
    }
    public NonEditableTableModel(String[] string, int i)
    {
        super(string,i);
    }
    
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
}
