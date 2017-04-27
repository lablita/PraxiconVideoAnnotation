package pva;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class StatusBarPanel extends JPanel
{
    public StatusBarPanel()
    {
        super(new MigLayout("fill, insets 0", "[]0[grow,fill]", ""));
        srcBar = new StatusBar();
        this.setSource("unknown");
        msgBar = new StatusBar();
        this.add(srcBar, "w 10%");
        this.add(msgBar);
    }
    
    private StatusBar srcBar, msgBar;
    
    public void setSource(String src)
    {
        srcBar.setText("Source: " + src);
    }
    public void setMessage(String msg)
    {
        msgBar.setText(msg);
    }
}
