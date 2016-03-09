import java.util.ArrayList;

public class Node {
	public ArrayList<Node> Child;//节点的孩子
	public ArrayList<Double> value;//节点孩子对应的属性值
	public int test_cond; //节点对应哪个属性值进行划分
	public int label; //节点标签
	public double test_value; //对应连续性属性的划分点的值
	public Node()
	{
		this.Child = new ArrayList<Node>();
		this.value = new ArrayList<Double>();
		this.test_cond = -1;
		this.label = -1;//-1表示无类别
		this.test_value = -1;//-1表示离散型
	}
}
