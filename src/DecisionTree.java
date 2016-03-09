import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DecisionTree {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public static Node TreeGroth(ArrayList<double[]> E, int[] F) //E是训练集， F是属性集
	{
		int lenE = E.size();
		if(E.size()>0  && stropping_cond(E, F) == true )
		{
			Node leaf = createNode();
			leaf.label = Classify(E);
			return leaf;
		}
		else
		{
			Node root = createNode();
			root.test_cond = find_beast_split(E, F, root);
			if(F[root.test_cond] == 1)
			{
				Set<Double> set = new TreeSet<Double>();
				for(int i=0; i<lenE; i++)
				{
					if( !set.contains((Double)E.get(i)[root.test_cond]) )
					{
						set.add((Double)E.get(i)[root.test_cond]);
					}
				}
				Iterator<Double> iterator = set.iterator();
				while(iterator.hasNext())
				{
					ArrayList<double[]> E_v = new ArrayList<double[]>();
					double value = (double) iterator.next();
					for(int i=0; i<lenE; i++)
					{
						if( E.get(i)[root.test_cond] == value )
						{
							E_v.add(E.get(i));
						}
					}
					if(E_v.size() != 0)
					{
						root.Child.add(TreeGroth(E_v, F));
						root.value.add(value);
					}
				}
			}
			else if(F[root.test_cond] == 0)
			{
				ArrayList<double[]> E_v_0 = new ArrayList<double[]>();
				ArrayList<double[]> E_v_1 = new ArrayList<double[]>();
				double value = root.test_value;
				for(int i=0; i<lenE; i++)
				{
					if( E.get(i)[root.test_cond] <= value )
					{
						E_v_0.add(E.get(i));
					}
					else
					{
						E_v_1.add(E.get(i));
					}
				}
				if(E_v_0.size() != 0)
					root.Child.add(TreeGroth(E_v_0, F));
				if(E_v_1.size() != 0)
					root.Child.add(TreeGroth(E_v_1, F));
			}
			
			
			return root;
		}
	}
	
	private static Node createNode() {
		// TODO Auto-generated method stub
		Node node = new Node();
		return node;
	}

	//确定应当选择哪个属性作为划分训练记录的测试条件
	private static int find_beast_split(ArrayList<double[]> E, int[] F, Node node) {
		// TODO Auto-generated method stub
		int lenF = F.length;
		int lenE = E.size();
		double Entropy = 1000;
		int temp = 0;
		for(int i=0; i<lenF; i++)
		{
			if(F[i] == 1)
			{
				Set<Double> set = new TreeSet<Double>();
				for(int j=0; j<lenE; j++)
				{
					if( !set.contains((Double)E.get(j)[i]) )
					{
						set.add((Double)E.get(j)[i]);
					}
				}
				if(set.size() == 1)
				{
					continue;
				}
				double tempEntropy = 0;
				Iterator<Double> iterator = set.iterator();
				while(iterator.hasNext())
				{
					double value = (double) iterator.next();
					int S_i = 0;
					for(int l=0; l<lenE; l++)
					{
						if(E.get(l)[i] == value)
							S_i++;
					}
					tempEntropy = tempEntropy + ((double)S_i/(double)lenE)*Entropy_Split_Cate(E,value,i);//i表示按着属性i计算熵
				}
				if(Entropy > tempEntropy)
				{
					Entropy = tempEntropy;
					temp = i;
				}
			}
			else
			{
				Set<Double> set = new TreeSet<Double>();
				for(int j=0; j<lenE; j++)
				{
					if( !set.contains((Double)E.get(j)[i]) )
					{
						set.add((Double)E.get(j)[i]);
					}
				}
				double tempEntropy = 1000;
				Iterator<Double> iterator = set.iterator();
				double temp_value = 0;
				while(iterator.hasNext())
				{
					double value = (double) iterator.next();
					int S_0 = 0;
					for(int l=0; l<lenE; l++)
					{
						if(E.get(l)[i] <= value)
							S_0++;
					}
					double Entropy1 = ((double)S_0/(double)lenE)*Entropy_Split_Num(E,value,i,0) + ((lenE-(double)S_0)/(double)lenE)*Entropy_Split_Num(E,value,i,1);
					if(tempEntropy > Entropy1)
					{
						tempEntropy = Entropy1;
						temp_value = value;
					}
				}
				if(Entropy > tempEntropy)
				{
					Entropy = tempEntropy;
					temp = i;
					node.test_value = temp_value;
				}
			}
		}
		return temp;
	}

	//连续型熵的计算
	private static double Entropy_Split_Num(ArrayList<double[]> E, double value, int n, int t) {
		// TODO Auto-generated method stub
		double Entropy = 0;
		int lenE = E.size();
		int len = E.get(0).length;
		int sum = 0;
		int count_1 =0;
		int count_0 =0;
		if(t==0)
		{
			for(int i=0; i<lenE; i++)
			{
				if(E.get(i)[n] <= value)
				{
					sum++;
					if(E.get(i)[len-1]==1)
						count_1++;
					else
						count_0++;
				}
			}
			double p_0 = (double)count_0/(double)sum;
			double p_1 = (double)count_1/(double)sum;
			if(p_0 == 0 || p_1 == 0)
				Entropy = 0;
			else
				Entropy = -p_0*(Math.log(p_0)/Math.log(2)) - p_1*(Math.log(p_1)/Math.log(2));
		}
		else if(t==1)
		{
			for(int i=0; i<lenE; i++)
			{
				if(E.get(i)[n] > value)
				{
					sum++;
					if(E.get(i)[len-1]==1)
						count_1++;
					else
						count_0++;
				}
			}
			double p_0 = (double)count_0/(double)sum;
			double p_1 = (double)count_1/(double)sum;
			if(p_0 == 0 || p_1 == 0)
				Entropy = 0;
			else
				Entropy = -p_0*(Math.log(p_0)/Math.log(2)) - p_1*(Math.log(p_1)/Math.log(2));
		}
		
		return Entropy;
	}

	//离散型熵的计算
	private static double Entropy_Split_Cate(ArrayList<double[]> E, double value, int n) {
		// TODO Auto-generated method stub
		double Entropy = 0;
		int lenE = E.size();
		int len = E.get(0).length;
		int sum =0;
		int count_1 =0;
		int count_0 =0;
		for(int i=0; i<lenE; i++)
		{
			if(E.get(i)[n] == value)
			{
				sum++;
				if(E.get(i)[len-1]==1)
					count_1++;
				else
					count_0++;
			}
		}
		double p_0 = (double)count_0/(double)sum;
		double p_1 = (double)count_1/(double)sum;
		if(p_0 == 0 || p_1 == 0)
			Entropy = 0;
		else
			Entropy = -p_0*(Math.log(p_0)/Math.log(2)) - p_1*(Math.log(p_1)/Math.log(2));
		return Entropy;
	}


	//将节点指派到具有多数记录的类中
	private static int Classify(ArrayList<double[]> E) {
		// TODO Auto-generated method stub
		int lenE = E.size();
		int len = E.get(0).length;
		int count = 0;
		for(int i=0; i<lenE; i++)
		{
			if(E.get(i)[len-1] == 1.0)
				count++;
		}
		if(count > lenE-count)
			return 1;
		else
			return 0;
	}

	//是否所有的记录都具有相同的属性或都属于同一类
	private static boolean stropping_cond(ArrayList<double[]> E, int[] F) {
		// TODO Auto-generated method stub
		boolean right = true;
		int lenE = E.size();
		int len = E.get(0).length;
		for(int i=0; i<lenE-1; i++)
		{
			if(E.get(i)[len-1] == E.get(i+1)[len-1])
				continue;
			else
			{
				right = false;
				break;
			}
				
		}
		if(right == true)
			return true;
		else
		{
			for(int i=0; i<lenE-1; i++)
			{
				for(int j=0; j<len-1; j++)
					if(E.get(i)[j] == E.get(i+1)[j])
						continue;
					else
						return false;
			}
		}
		return true;
	}
	
	//读取文件函数
	public static double[][] readerFileTxt(File file,int[] feature, int n) throws IOException
	{
		double [][]dataSet = null;
		if(n == 1)
			dataSet = new double[277][10];
		else if(n == 2)
			dataSet = new double[1000][25];
		if( file.isFile()&&file.exists())
		{
			InputStreamReader read = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferReader = new BufferedReader(read);
			String lineTxt = null;
			if((lineTxt = bufferReader.readLine()) != null)
			{
				String[] temp = lineTxt.split(",");
            	for(int j=0; j<temp.length; j++)
            		feature[j] = Integer.parseInt(temp[j]);
			}
			int i=0;
            while ((lineTxt = bufferReader.readLine()) != null) 
            {   
            	String[] temp = lineTxt.split(",");
            	for(int j=0; j<temp.length; j++)
            		dataSet[i][j] = Double.parseDouble(temp[j]);
            	i++;
            } 
			read.close();
		}
		else
			System.out.println("读取文件失败！");
		return dataSet;
	}
}
