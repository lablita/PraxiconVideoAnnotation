package pva;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class StatusBar extends JLabel
{
    public StatusBar()
    {
        super();
        this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 12));
        Border margin = new EmptyBorder(3,3,3,3);
        this.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.GRAY),margin));
        
        setMessage("Ready");
    }
     
    public void setMessage(String message)
    {
        setText(" "+message);        
    }        
}