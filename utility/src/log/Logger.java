package log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Log the RPQ.
 * Need to flush in the end.
 */
public class Logger
{
    private ObjectOutputStream out=null;
    private ArrayList<Log> log=new ArrayList<>();
    public Logger(FileOutputStream fo)
    {
        try
        {
            out=new ObjectOutputStream(fo);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void add(ArrayList<?> array)
    {
        log.add(new Log(System.currentTimeMillis(),array));
    }
    public void flush_close()
    {
        try
        {
            out.writeObject(log);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
