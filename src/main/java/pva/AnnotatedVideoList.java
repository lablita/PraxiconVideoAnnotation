package pva;

import pva.AnnotatedVideo.AnnotationStatus;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedVideoList
{
    public AnnotatedVideoList()
    {
        vlist = new ArrayList<>();
    }
    public AnnotatedVideoList(String _csvFile)
    {
        vlist = new ArrayList<>();
        setListFile(_csvFile);
    }
    
    protected String csvFile;
    protected String source;
    protected List<AnnotatedVideo> vlist;
    int nTotal = -1, nTodo, nSkip, nDone, nDel;
    
    public String getAnnotationStats()
    {
        updateStats();
        return nTotal + " Total Videos, " + nTodo + " to do, " + nDel + " deleted, " + nSkip + " skipped, " + nDone + " done.";
    }
    
    private void updateStats()
    {
        nTotal = vlist.size();
        nTodo = 0; nSkip = 0; nDone = 0; nDel = 0;
        for (AnnotatedVideo v : vlist)
        {
            if (v.status == AnnotationStatus.CANC)
                nDel++;
            else if (v.status == AnnotationStatus.SKIP)
                nSkip++;
            else if (v.status == AnnotationStatus.DONE)
                nDone++;
            else
                nTodo++;
        }
    }
    
    public void sortList()
    {
        List<AnnotatedVideo> vlist2 = new ArrayList<>();
        for (AnnotatedVideo v : vlist) { if (v.status == null) { v.status = AnnotationStatus.TODO; } }
        for (AnnotatedVideo v : vlist) { if (v.status == AnnotationStatus.TODO) { vlist2.add(v); } }
        for (AnnotatedVideo v : vlist) { if (v.status == AnnotationStatus.SKIP) { vlist2.add(v); } }
        for (AnnotatedVideo v : vlist) { if (v.status == AnnotationStatus.DONE) { vlist2.add(v); } }
        for (AnnotatedVideo v : vlist) { if (v.status == AnnotationStatus.CANC) { vlist2.add(v); } }
        vlist = vlist2;
    }
    
    public String getWorkDir()
    {
        if (csvFile == null) { return null; }
        File f = new File(csvFile);
        if (f.exists())
        {
            return f.getParentFile().getAbsolutePath();
        }
        return null;
    }
    
    public void setListFile(String _csvFile)
    {
        csvFile = _csvFile;
    }
    
    public void setGlobalSource(String videoSource)
    {
        source = videoSource;
        for (AnnotatedVideo v : vlist)
        {
            v.source = source;
        }
    }
    
    public void loadFromFile()
    {
        if (csvFile == null)
        {
            System.err.println("Error: CSV file not specified");
            return;
        }
        File f = new File(csvFile);
        if (f.exists() && f.isFile())
        {
            vlist = Utils.importVideoAnnotationFromCsv(csvFile);
        }
        else
            System.err.println("Error: invalid file " + csvFile);
    }
    
    public void saveToFile()
    {
        if (csvFile == null)
        {
            System.err.println("Error: CSV file not specified");
            return;
        }
        Utils.exportVideoAnnotationToCsv(vlist, csvFile);
    }
    
    public void setAnnotationDone(String video_id, String annotFile, boolean autosave)
    {
        for (AnnotatedVideo v : vlist)
        {
            if (v.video_id.equals(video_id))
            {
                v.status = v.status.DONE;
                v.annotationFile = annotFile;
                break;
            }
        }
        if (autosave)
            saveToFile();
    }
    public void setSceneToSkip(String video_id, boolean autosave)
    {
        for (AnnotatedVideo v : vlist)
        {
            if (v.video_id.equals(video_id))
            {
                v.status = v.status.SKIP;
                break;
            }
        }
        if (autosave)
            saveToFile();
    }
    
    public void setSceneToDelete(String video_id, boolean autosave)
    {
        for (AnnotatedVideo v : vlist)
        {
            if (v.video_id.equals(video_id))
            {
                v.status = v.status.CANC;
                break;
            }
        }
        if (autosave)
            saveToFile();
    }
    
    public List<String> getVideoIds()
    {
        List<String> ids = new ArrayList<>();
        for (AnnotatedVideo v : vlist)
        {
            ids.add(v.video_id);
        }
        return ids;
    }
    
    public String getVideoUrl(String video_id)
    {
        for (AnnotatedVideo v : vlist)
        {
            if (v.video_id.equals(video_id))
            {
                return v.video_url;
            }
        }
        return null;
    }
    
    public AnnotatedVideo getVideoByIndex(int index)
    {
        return vlist.get(index);
    }
}
