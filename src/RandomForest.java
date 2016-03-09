import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RandomForest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File filepath1 = new File("Dataset1.txt");
		File filepath2 = new File("Dataset2.txt");
		int[] feature1 = new int[9];
		int[] feature2 = new int[24];
		double [][]DataSet1 = readerFileTxt(filepath1, feature1, 1);
		double [][]DataSet2 = readerFileTxt(filepath2, feature2, 2);
		double []accuracy_1 = crossValid(DataSet1, feature1, 51);
		double []accuracy_2 = crossValid(DataSet2, feature2, 31);
		double means_1 = Means(accuracy_1);
		double means_2 = Means(accuracy_2);
		double standard_1 = standardDev(accuracy_1, means_1);
		double standard_2 = standardDev(accuracy_2, means_2);
		System.out.println("RandomForest算法在Data_1中的均值为：" + means_1);
		System.out.println("RandomForest算法在Data_2中的均值为：" + means_2);
		System.out.println("RandomForest算法在Data_1中的标准差为：" + standard_1);
		System.out.println("RandomForest算法在Data_2中的标准差为：" + standard_2);
	}
	
	private static double standardDev(double[] accuracy, double means) {
		// TODO Auto-generated method stub
		double standard = 0;
		int len = accuracy.length;
		for(int i=0; i<len; i++)
		{
			standard = standard + (accuracy[i]-means)*(accuracy[i]-means)/(double)len;
		}
		
		return Math.sqrt(standard);
	}

	private static double Means(double[] accuracy) {
		// TODO Auto-generated method stub
		int len = accuracy.length;
		double sum = 0;
		for(int i=0; i<len; i++)
		{
			sum = sum + accuracy[i];
		}
		return sum/(double)len;
	}
	
	private static double[] crossValid(double[][] DataSet, int[] feature1, int t) {
		// TODO Auto-generated method stub
		double [] error= new double[10];
		int lenData = DataSet.length;
		int len = DataSet[0].length;
		int lenTest = lenData/10;
		for(int i=0; i<10; i++)
		{
			double [][]TrainDataSet = new double[lenData-lenTest][len];
			double [][]TestDataSet = new double[lenTest][len];
			int t_1 = 0, t_2 = 0; 
			for(int j=0; j<lenData; j++)
			{
				if(j>=i*lenTest && j<(i+1)*lenTest)
				{
					TestDataSet[t_1] = DataSet[j];
					t_1++;
				}
				else
				{
					TrainDataSet[t_2] = DataSet[j];
					t_2++;
				}
			}
			int []label1 = randomForestAlgorithm(TrainDataSet, TestDataSet, feature1, t);
			int count = 0;
			for(int j=0; j<lenTest; j++)
			{
				if(label1[j] == 1 && TestDataSet[j][len-1] == 1.0 || label1[j] != 1 && TestDataSet[j][len-1] != 1.0)
				{
					count++;
				}
			}
			error[i] = (double)count/(double)lenTest;
		}
		return error;
	}
	
	private static int[] randomForestAlgorithm(double[][] DataSet, double [][]TestDataSet, int []F, int t) {
		// TODO Auto-generated method stub
		
		int lenData = DataSet.length;
		int len = DataSet[0].length;
		Node []node = new Node[t];
		for(int i=0; i<t; i++)
		{
			ArrayList<double[]> E = new ArrayList<double[]>();
				for(int j=0; j<lenData/4; j++)
				{
					int Rand = (int) (Math.random()*(lenData));
					E.add(DataSet[Rand]);
				}
			node[i] = DecisionTree.TreeGroth(E, F);
		}
		int []label = new int[lenData];
		int lenTest = TestDataSet.length;
		for(int i=0; i<lenTest; i++)
		{
			double labelsum_0 = 0;
			double labelSum_1 = 0;
			for(int j=0; j<t; j++)
			{
				if(TestData(node[j], TestDataSet[i]) == 1)
				{
					labelSum_1++;
				}
				else
				{
					labelsum_0++;
				}
			}
			if(labelSum_1 >= labelsum_0)
				label[i] = 1;
		}
		return label;
	}

	private static int TestData(Node node, double[] Data) {
		// TODO Auto-generated method stub
		if(node.label != -1)
			return node.label;
		else{
			int label = 0;
			int feature = node.test_cond;
			double number = Data[feature];
			if(node.test_value == -1)//对应离散型值
			{
				int index = node.value.indexOf(number);
				if(index != -1)
					label = TestData(node.Child.get(index), Data);
			}
			else//对应连续型值
			{
				int temp = node.Child.size();
				if(number <= node.test_value)
					label = TestData(node.Child.get(0), Data);
				else if(temp > 1)
					label = TestData(node.Child.get(1), Data);
				else
					label = TestData(node.Child.get(0), Data);
				
			}
			return label;
		}
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
