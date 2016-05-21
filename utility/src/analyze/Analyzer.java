package analyze;


import log.Log;
import log.Reader;
import message.Content;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by admin on 2016/5/21.
 */
public class Analyzer
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        Reader ra=new Reader(new FileInputStream("RPQ_client.log"));
        Reader rb=new Reader(new FileInputStream("RPQ_server.log"));

        ArrayList<Log> la=ra.getLog(),lb=rb.getLog();
        int size=Math.min(la.size(),lb.size());
        int max=0;
        for(int i=0;i<size;i++)
        {
            System.out.println("size:"+size);
            int res=0,rsize=Math.min(la.get(i).array.size(),lb.get(i).array.size());
            System.out.println("rsize:"+rsize);
            ArrayList<Content<String,Integer>> a= (ArrayList<Content<String,Integer>>) la.get(i).array,b= (ArrayList<Content<String,Integer>>) lb.get(i).array;
            for(int j=0;j<rsize;j++)
            {
                if(j==0)
                {
                    res+=rsize*(Math.abs(a.get(j).value-b.get(j).value));
                }
                else res+=Math.abs(a.get(j).value-b.get(j).value);
            }
            if(res>max)max=res;
        }
        System.out.println(max);
    }
}
