package Calc;

public class LinkedList
{
	public Object node;
	private LinkedList next;
	private boolean sortable;
	private boolean sorted;
	
	public LinkedList(Object o)
	{
		node = o;
		if(o instanceof Comparable){
			sortable = true;
			sorted = true;
		} else {
			sortable = false;
			sorted = false;
		}
	}
	
	public LinkedList(Object o, LinkedList n)
	{
		node = o;
		next = n;
	}
	
	public void setNext(LinkedList n)
	{
		next = n;
		sorted = false;
	}
	
	public LinkedList next()
	{
		return next;
	}
	
	public Object find(Object o)
	{
		if(sorted = false){
			if(node.equals(o)){
				return node;
			}
			return next.find(o);
		} else {
			if(((Comparable)node).compareTo((Comparable)o)>0){
				next.find(o);
			} else if(node.equals(o)){
				return node;
			}
		}
		return null;
	}
	
	public void add(Object o)
	{
		if(!sortable){
			if(next == null){
				next = new LinkedList(o);
			} else {
				next.add(o);
			}
			sorted = false;
		} else {
			if(((Comparable)node).compareTo((Comparable)o)>0){
				next.add(o);
			} else /*if(((Comparable)node).compareTo((Comparable)o)<0)*/{
				next = new LinkedList(node,next);
				node = o;
			}
			sorted = true;
		}
	}
	
	public void sort()
	{
		sorted = false;
	}
}
