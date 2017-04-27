package pva;

public class AnnotatedVideo
{
    enum AnnotationStatus
    {
        TODO, DONE, SKIP, CANC;
    }
    String video_url;
    String video_id;
    AnnotationStatus status;
    String annotationFile;
    String source;
}
