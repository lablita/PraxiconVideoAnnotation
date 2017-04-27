package pva;

import java.awt.Canvas;
import java.awt.Color;
import static java.lang.Thread.sleep;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

public class VideoPanel extends JPanel
{
    public VideoPanel(FormPanel _fp)
    {
        super();
        this.setBackground(Color.GRAY);
        this.setLayout(new MigLayout());
        fp = _fp;
    }
    
    String videoPath;
    int width = 520;
    int height = 300;
    EmbeddedMediaPlayer player;
    long videoLength = -1;
    FormPanel fp;
    
    public void setVideo(String path)
    {
        videoPath = path;
    }
    
   
    public void load()
    {
        Canvas canvas = new Canvas();
        canvas.setSize(width, height);
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        player = mediaPlayerFactory.newEmbeddedMediaPlayer();
        player.setVideoSurface(videoSurface);
        canvas.setVisible(true);
        
        SimpleControlsPanel controls = new SimpleControlsPanel(player,fp);
        this.add(canvas, "wrap,align center");
        this.add(controls,"wrap,align center");
        
        this.revalidate();
        this.repaint();
    }
    
    public void play()
    {
        player.playMedia(videoPath);
        videoLength = -1;
        if (!videoPath.endsWith(".png"))
        {
            while (videoLength == -1)
            {
                try
                {
                    sleep(500);
                    long t = player.getMediaMeta().getLength();
                    if (t > -1)
                    {
                        videoLength = t;
                        fp.setEndTime("" + videoLength);
                    }
                }
                catch (InterruptedException ex)  {  }
            }
        }
    }
    public void stop()
    {
        player.stop();
    }
    public void pause()
    {
        player.pause();
    }
}
