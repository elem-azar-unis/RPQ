package log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * struct log
 */
public class Log implements Serializable
{
    public long time;
    public ArrayList<?> array;
    public Log(long t,ArrayList<?> a)
    {
        time=t;
        array=a;
    }
}
