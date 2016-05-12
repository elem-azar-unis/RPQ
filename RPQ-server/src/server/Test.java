package server;


import log.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test
{
    private PriorityQueue rpq;
    private FileOutputStream f_out;
    private Logger log;

    private static final int port=9000;
    private static final int log_count=2000;
    private static final int interval=500;
    private static final String logFile="RPQ_server.log";
    private static final boolean RPQ=true;

    private Test() throws FileNotFoundException
    {
        f_out =new FileOutputStream(logFile);
        if(RPQ)
            log=new Logger(f_out);
        rpq=new PriorityQueue(port);
    }
    private void test_RPQ() throws InterruptedException
    {
        for(int i=0;i<log_count;i++)
        {
            Thread.sleep(interval);
            log.add(rpq.getK());
        }
        log.flush_close();
    }
    private void test_strong_RPQ() throws InterruptedException, IOException
    {
        Thread.sleep(interval*log_count);
        f_out.write(rpq.getCount());
        f_out.flush();
        f_out.close();
    }
    public static void main(String[] args) throws IOException, InterruptedException
    {
        Test m=new Test();
        Thread.sleep(5000);
        if (RPQ)
            m.test_RPQ();
        else
            m.test_strong_RPQ();
    }
}
