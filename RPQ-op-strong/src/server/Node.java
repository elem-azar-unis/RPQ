package server;

import connector.Sender;

class Node
{
	Sender to;
	Node(Sender t)
	{
		to=t;
	}
}
