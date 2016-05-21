package com.example.admin.rpq_client;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

import benchmark.OpGen;
import benchmark.Operation;
import client.PriorityQueue;
import kernel.Element;
import log.Logger;

/**
 * 作者：张宇奇
 * 时间：2016/5/8.
 * 邮箱：cs_yqzhang@qq.com
 */
public class Test
{
	private static final int log_count=2000;
	private static final int interval=500;
	private static final int op_interval=50;
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
				File f=new File(Environment.getExternalStoragePublicDirectory(Environment
						.DIRECTORY_DOCUMENTS),logFile);
				log=new Logger(new FileOutputStream(f));
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
}
