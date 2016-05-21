package client;

import benchmark.OpGen;
import benchmark.Operation;
import kernel.Element;
import log.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * 作者：张宇奇
 * 时间：2016/5/8.
 * 邮箱：cs_yqzhang@qq.com
 */
public class Test
{
    private static final int log_count=200;//200
    private static final int interval=50;//50
    private static final int op_interval=50;//50
    private static final String logFile="RPQ_client.log";
    private static final boolean RPQ=true;

    private PriorityQueue rpq;
    private OpGen gen=new OpGen();
    private Logger log;
    private Random r=new Random();

    private class logger implements Runnable
    {
        public void run()
        {
            try
            {
                for(int i=0; i<log_count; i++)
                {
                    Thread.sleep(interval);
                    log.add(rpq.getK());
                }
                log.flush_close();
                System.out.println("Log Finished");
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Test(String ip, int port)
    {
        rpq=new PriorityQueue(ip,port);
        if(RPQ)
        {
            try
            {
                FileOutputStream f_out =new FileOutputStream(logFile);
                log=new Logger(f_out);
                new Thread(new logger()).start();
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void ran_op()
    {
        Operation op=gen.genOperation();
        switch(op.type)
        {
            case ALTER:
            {
                if(rpq.getSize()==0)return;
                rpq.alter(rpq.getOne(r.nextInt()),op.value);
                break;
            }
            case DELETE:
            {
                rpq.delete();
                break;
            }
            case DELTA:
            {
                if(rpq.getSize()==0)return;
                rpq.delta(rpq.getOne(r.nextInt()),op.value);
                break;
            }
            case INSERT:
            {
                rpq.insert(new Element<>(op.key, op.value));
                break;
            }
        }
    }

    public void benchmark()
    {
        for(int i=0;i<log_count*interval/op_interval;i++)
            ran_op();
    }

    public static void main(String[] args)
    {
        Test m=new Test("192.168.1.117", 9000);
        m.benchmark();
    }
}

