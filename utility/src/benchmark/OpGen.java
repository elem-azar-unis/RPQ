package benchmark;

import java.util.Random;

/**
 * generate a random operation
 */
public class OpGen
{
    private final String name=getRandomString(5);
    private int count=0;
    private Random random = new Random();

    private static final int maxN=2<<15;
    private static final int maxD=2<<7;

    private static final double alter   =0.25;
    private static final double delete  =0.1    +alter;
    private static final double delta   =0.5    +alter+delete;
    private static final double insert  =0.15   +alter+delete+delta;

    private String getRandomString(int length)
    {
        //noinspection SpellCheckingInspection
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * if alter or delta, you need to choose an existing element by yourself.
     * */
    public Operation genOperation()
    {
        Operation operation=new Operation();
        double d=random.nextDouble();
        if(d<alter)
        {
            //alter
            operation.type=OperationType.ALTER;
            operation.value= (int) (random.nextGaussian()*maxN);
        }
        else if(alter<=d && d<delete)
        {
            //delete
            operation.type=OperationType.DELETE;
        }
        else if(delete<=d && d<delta)
        {
            //delta
            operation.type=OperationType.DELTA;
            operation.value= (int) (random.nextGaussian()*maxD);
            if(random.nextBoolean())
                operation.value=-operation.value;
        }
        else
        {
            //insert
            operation.type=OperationType.INSERT;
            operation.key=name+count;
            count++;
            operation.value= (int) (random.nextGaussian()*maxN);
        }
        return operation;
    }
}
