package benchmark;


public class Operation
{
    public OperationType type;
    public String key=null;
    public int value;
    public Operation(OperationType t,String k,int v)
    {
        type=t;
        key=k;
        value=v;
    }
    public Operation(){}
}
