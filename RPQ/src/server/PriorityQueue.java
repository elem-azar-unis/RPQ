package server;

import java.io.ObjectOutputStream;

import kernel.Element;
import kernel.ServerPriorityQueue;
import kernel.TaskQueue;

public class PriorityQueue
{
	ServerPriorityQueue<String,Integer, Element<String,Integer>> queue=new ServerPriorityQueue<>();
	TaskQueue tasks=new TaskQueue();
	int version=0;
	//TODO threads:接受连接，apply+更新者。顶层操作堆的函数，维护版本号，要有notify apply者。
}

class Node
{
	ObjectOutputStream to;
}