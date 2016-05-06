package log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Read the log.
 */
public class Reader
{
    private ArrayList<Log> log =new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Reader(FileInputStream fin)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(fin);
            log = (ArrayList<Log>) in.readObject();
            in.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<Log> getLog()
    {
        return log;
    }
}
