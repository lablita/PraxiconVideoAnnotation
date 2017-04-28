
package pva;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class SimpleControlsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int SKIP_TIME_MS = 10 * 100;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final EmbeddedMediaPlayer mediaPlayer;

    private JLabel timeLabel;
    private JSlider positionSlider;
    private JLabel chapterLabel;

    private JButton previousChapterButton;
    private JButton rewindButton;
    private JButton stopButton;
    private JButton pauseButton;
    private JButton playButton;
    private JButton nextChapterButton;
    private FormPanel fp;
    private boolean mousePressedPlaying = false;

    public SimpleControlsPanel(EmbeddedMediaPlayer mediaPlayer, FormPanel _fp) {
        this.mediaPlayer = mediaPlayer;
        fp = _fp;
        createUI();

        executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayer), 0L, 400L, TimeUnit.MILLISECONDS);
    }

    private void createUI() {
        createControls();
        layoutControls();
        registerListeners();
    }

    private void createControls() {
        timeLabel = new JLabel("mm:ss:ms");
        positionSlider = new JSlider();
        positionSlider.setMinimum(0);
        positionSlider.setMaximum(1000);
        positionSlider.setValue(0);
        positionSlider.setToolTipText("Position");

        chapterLabel = new JLabel("00/00");

        previousChapterButton = new JButton();
        //previousChapterButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_start_blue.png")));
        previousChapterButton.setText("[");
        previousChapterButton.setToolTipText("Set Start Time");

        rewindButton = new JButton();
        rewindButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_rewind_blue.png")));
        rewindButton.setToolTipText("Skip back");

        stopButton = new JButton();
        stopButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_stop_blue.png")));
        stopButton.setToolTipText("Stop");

        pauseButton = new JButton();
        pauseButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_pause_blue.png")));
        pauseButton.setToolTipText("Play/pause");

        playButton = new JButton();
        playButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_play_blue.png")));
        playButton.setToolTipText("Play");


        nextChapterButton = new JButton();
        //nextChapterButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icons/control_end_blue.png")));
        nextChapterButton.setText("]");
        nextChapterButton.setToolTipText("Set End Time");

    }

    private void layoutControls() {
        setBorder(new EmptyBorder(4, 4, 4, 4));

        setLayout(new BorderLayout());

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new GridLayout(1, 1));
        positionPanel.add(positionSlider);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(10, 0));

        topPanel.add(timeLabel, BorderLayout.WEST);
        topPanel.add(positionPanel, BorderLayout.CENTER);
        topPanel.add(chapterLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();

        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5)); 
        bottomPanel.setSize(500, 80);
        bottomPanel.add(previousChapterButton);
        bottomPanel.add(rewindButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(playButton);
        bottomPanel.add(nextChapterButton);


        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Broken out position setting, handles updating mediaPlayer
     */
    private void setSliderBasedPosition() {
        if(!mediaPlayer.isSeekable()) {
            return;
        }
        float positionValue = positionSlider.getValue() / 1000.0f;
        // Avoid end of file freeze-up
        if(positionValue > 0.99f) {
            positionValue = 0.99f;
        }
        mediaPlayer.setPosition(positionValue);
    }

    private void updateUIState() {
        if(!mediaPlayer.isPlaying()) {
            // Resume play or play a few frames then pause to show current position in video
            mediaPlayer.play();
            if(!mousePressedPlaying) {
                try {
                    // Half a second probably gets an iframe
                    Thread.sleep(100);
                }
                catch(InterruptedException e) {
                    // Don't care if unblocked early
                }
                mediaPlayer.pause();
            }
        }
        long time = mediaPlayer.getTime();
        int position = (int)(mediaPlayer.getPosition() * 1000.0f);
        int chapter = mediaPlayer.getChapter();
        int chapterCount = mediaPlayer.getChapterCount();
        updateTime(time);
        updatePosition(position);
        updateChapter(chapter, chapterCount);
    }

    private void skip(int skipTime) {
        // Only skip time if can handle time setting
        if(mediaPlayer.getLength() > 0) {
            mediaPlayer.skip(skipTime);
            updateUIState();
        }
    }

    private void registerListeners() {
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
//                updateVolume(mediaPlayer.getVolume());
            }
        });

        positionSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(mediaPlayer.isPlaying()) {
                    mousePressedPlaying = true;
                    mediaPlayer.pause();
                }
                else {
                    mousePressedPlaying = false;
                }
                setSliderBasedPosition();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setSliderBasedPosition();
                updateUIState();
            }
        });

        previousChapterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long curTime = mediaPlayer.getTime();
                if (curTime == -1) { return; }
                if (curTime - 100 < 0) { curTime = 0; }
                fp.setStartTime(curTime + "");
                fp.setVideoFragment(true);
                if (fp.getEndTime().length() > 0 && Long.parseLong(fp.getEndTime()) < curTime)
                {
                    fp.setEndTime(Math.max(curTime, mediaPlayer.getMediaMeta().getLength()) + "");
                }
                fp.selectFirstField();
            }
        });

        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                skip(-SKIP_TIME_MS);
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.stop();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.pause();
                fp.selectFirstField();
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.play();
                fp.selectFirstField();
            }
        });


        nextChapterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long curTime = mediaPlayer.getTime();
                if (curTime == -1) { return; }
                if (mediaPlayer.getLength() > -1 && curTime + 100 > mediaPlayer.getLength()) { curTime = mediaPlayer.getLength(); }
                fp.setEndTime(curTime + "");
                fp.setVideoFragment(true);
                if (fp.getStartTime().length() > 0 && Long.parseLong(fp.getStartTime()) > curTime)
                {
                    fp.setStartTime("0");
                }
                fp.selectFirstField();
            }
        });
    }

    private final class UpdateRunnable implements Runnable {

        private final MediaPlayer mediaPlayer;

        private UpdateRunnable(MediaPlayer mediaPlayer) {
            this.mediaPlayer = mediaPlayer;
        }

        @Override
        public void run() {
            final long time = mediaPlayer.getTime();
            final int position = (int)(mediaPlayer.getPosition() * 1000.0f);
            final int chapter = mediaPlayer.getChapter();
            final int chapterCount = mediaPlayer.getChapterCount();

            // Updates to user interface components must be executed on the Event
            // Dispatch Thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer.isPlaying()) {
                        updateTime(time);
                        updatePosition(position);
                        updateChapter(chapter, chapterCount);
                    }
                }
            });
        }
    }

    private void updateTime(long millis) {
        String s = String.format("%02d:%02d:%02d", 
                TimeUnit.MILLISECONDS.toMinutes(millis), 
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));
        timeLabel.setText(s);
    }

    private void updatePosition(int value) {
        // positionProgressBar.setValue(value);
        positionSlider.setValue(value);
    }

    private void updateChapter(int chapter, int chapterCount) {
        String s = chapterCount != -1 ? (chapter + 1) + "/" + chapterCount : "-";
        chapterLabel.setText(s);
        chapterLabel.invalidate();
        validate();
    }

}
