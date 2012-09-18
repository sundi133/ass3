import java.awt.dnd.Autoscroll;
import java.io.*;

import java.util.*;

public class DTree

{

	int numParams;

	String[] ParamNames;

	Vector[] ParamDomains;


	ArrayList paramwithNames= new ArrayList(7);

	
	
	class DTreeNode {

		public double entropy;

		public Vector data;

		public int decompositionAttribute;

		public int decompositionValue;

		public DTreeNode[] children;

		public DTreeNode parent;

		public DTreeNode() {

			data = new Vector();

		}

	};

	class PointsinData {

		public int[] attributes;

		public PointsinData(int numattributes) {

			attributes = new int[numattributes];

		}

	};

	DTreeNode root = new DTreeNode();

	public int getSymbolValue(int attribute, String symbol) {

		int index = ParamDomains[attribute].indexOf(symbol);

		if (index < 0) {

			//System.out.println("4 : " + attribute + "symbol : " + symbol );
			ParamDomains[attribute].addElement(symbol);

			return ParamDomains[attribute].size() - 1;

		}

		return index;

	}

	public int[] getAllValues(Vector data, int attribute) {

		Vector values = new Vector();

		int num = data.size();

		for (int i = 0; i < num; i++) {

			PointsinData point = (PointsinData) data.elementAt(i);

			String symbol =

				(String) ParamDomains[attribute].elementAt(point.attributes[attribute]);

			int index = values.indexOf(symbol);

			if (index < 0) {

				values.addElement(symbol);

			}

		}

		int[] array = new int[values.size()];

		for (int i = 0; i < array.length; i++) {

			String symbol = (String) values.elementAt(i);

			array[i] = ParamDomains[attribute].indexOf(symbol);

		}

		values = null;

		return array;

	}

	public Vector getSubset(Vector data, int attribute, int value) {

		Vector subset = new Vector();

		int num = data.size();

		for (int i = 0; i < num; i++) {

			PointsinData point = (PointsinData) data.elementAt(i);

			if (point.attributes[attribute] == value)
				subset.addElement(point);

		}

		return subset;

	}

	public double calculateEntropy(Vector data) {

		int numdata = data.size();

		if (numdata == 0)
			return 0;

		int attribute = numParams - 1; 

		int numvalues = ParamDomains[attribute].size();

		double sum = 0;

		for (int i = 0; i < numvalues; i++) {

			int count = 0;

			for (int j = 0; j < numdata; j++) {

				PointsinData point = (PointsinData) data.elementAt(j);

				if (point.attributes[attribute] == i)
					count++;

			}

			double probability = 1. * count / numdata;

			if (count > 0)
				sum += -probability * Math.log(probability);

		}

		return sum;

	}

	public boolean alreadyUsedToDecompose(DTreeNode node, int attribute) {

		if (node.children != null) {

			if (node.decompositionAttribute == attribute)

				return true;

		}

		if (node.parent == null)
			return false;

		return alreadyUsedToDecompose(node.parent, attribute);

	}

	public void decomposeNode(DTreeNode node) {
		
		for(int k=0;k<ParamDomains.length;k++)
			System.out.println( "1 : " +paramwithNames.get(k)+ ParamDomains[k]);

		for(int k=0;k<ParamNames.length;k++)
			System.out.println( "2 : " +paramwithNames.get(k)+ ParamNames[k]);

		double bestEntropy=0;

		boolean selected=false;

		int selectedAttribute=0;

		int numdata = node.data.size();

		int numinputattributes = numParams-1;

		node.entropy = calculateEntropy(node.data);

		if (node.entropy == 0) return;



		for (int i=0; i< numinputattributes; i++) {

			int numvalues = ParamDomains.length;

			if ( alreadyUsedToDecompose(node, i) ) continue;

			double averageentropy = 0;

			for (int j=0; j< numvalues; j++) {

				Vector subset = getSubset(node.data, i, j);

				if (subset.size() == 0) continue;

				double subentropy = calculateEntropy(subset);

				averageentropy += subentropy * subset.size();

			}

			averageentropy = averageentropy / numdata; 
			
			//Taking the weighted average

			if (selected == false) {

				selected = true;

				bestEntropy = averageentropy;

				selectedAttribute = i;

			} else {

				if (averageentropy < bestEntropy) {

					selected = true;

					bestEntropy = averageentropy;

					selectedAttribute = i;

				}

			}

		}

		if (selected == false) return;

		int numvalues = ParamDomains[selectedAttribute].size();

		node.decompositionAttribute = selectedAttribute;

		node.children = new DTreeNode [numvalues];

		for (int j=0; j< numvalues; j++) {

			node.children[j] = new DTreeNode();

			node.children[j].parent = node;

			node.children[j].data = getSubset(node.data,selectedAttribute, j);

			node.children[j].decompositionValue = j;

		}



		for (int j=0; j< numvalues; j++) {

			decomposeNode(node.children[j]);

		}



		node.data = null;

	}

	public int readFileData(String filename) throws Exception {

		FileInputStream in = null;

		try {

			File inputFile = new File(filename);

			in = new FileInputStream(inputFile);

		} catch ( Exception e) {

			return -1;

		}

		BufferedReader bin = new BufferedReader(new InputStreamReader(in) );

		String input;

		while(true) {

			input = bin.readLine();

			if (input == null) {

				// no data found in file
				return 0;

			}

			if (input.startsWith("//")) continue;

			if (input.equals("")) continue;

			break;

		}



		// parse each input
		StringTokenizer tokenizer = new StringTokenizer(input);

		numParams = tokenizer.countTokens();

		if (numParams <= 1) {

			System.err.println( "Read line: " + input);

			System.err.println( "Could not obtain the names of attributes in the line");

					System.err.println( "Expecting at least one input attribute and one output attribute");

							return 0;

		}

		ParamDomains = new Vector[numParams];

		for (int i=0; i < numParams; i++){
			
			ParamDomains[i] = new Vector();
		}

		ParamNames = new String[numParams];

		for (int i=0; i < numParams; i++) {

			//iiii
			//ParamNames[i] = tokenizer.nextToken();
			ParamNames[i] = String.valueOf(i);
			//System.out.println( "3 : " +i+ ", " + ParamNames[i]);

		}



		while(true) {

			input = bin.readLine();

			if (input == null) break;

			if (input.startsWith("//")) continue;

			if (input.equals("")) continue;

			tokenizer = new StringTokenizer(input);

			int numtokens = tokenizer.countTokens();

			if (numtokens != numParams) {

				
				return 0;

			}

			PointsinData point = new PointsinData(numParams);

			String autoval=tokenizer.nextToken();
			int autonindex=0;
			for (int i=0; i < numParams-1; i++) {

				System.out.println("sun 5: " + i );
				
				point.attributes[i] = getSymbolValue(i, tokenizer.nextToken());
				autonindex=i;

			}
			System.out.println("sun 56 : " + (autonindex+1)  + "," + autoval);
			point.attributes[6] = getSymbolValue(6, "1");
			point.attributes[6] = getSymbolValue(6, "2");

			root.data.addElement(point);

		}

		bin.close();

		return 1;

	}

	public void printTree(DTreeNode node, String tab)
	{
		int outputattr = numParams-1;

		if (node.children == null) {

			int []values = getAllValues(node.data, outputattr );

			if (values.length == 1) {

				System.out.println(tab + "\t" + ParamNames[outputattr] + " = \"" +

						ParamDomains[outputattr].elementAt(values[0]) + "\";");

				return;

			}

			System.out.print(tab + "\t" + ParamNames[outputattr] + " = {");

			for (int i=0; i < values.length; i++) {

				System.out.print("\"" + ParamDomains[outputattr].elementAt(i) + "\"");

						if ( i != values.length-1 ) System.out.print( " , " );

			}

			System.out.println( " };");

			return;

		}

		int numvalues = node.children.length;

		for (int i=0; i < numvalues; i++) {

			System.out.println(tab + "if( " +

					ParamNames[node.decompositionAttribute] + " == \"" +

					ParamDomains[node.decompositionAttribute].elementAt(i)

					+ "\") {" );

			printTree(node.children[i], tab + "\t");

			if (i != numvalues-1) System.out.print(tab + "} else ");

			else System.out.println(tab + "}");

		}

	}
	
	/*public void printTree(DTreeNode node, String tab) {

		int outputattr = numParams-1;

		if (node.children == null) {

			int []values = getAllValues(node.data, outputattr );

			if (values.length == 1) {

				System.out.println(tab + "\t" + ParamNames[outputattr] + " = \"" +

						ParamDomains[outputattr].elementAt(values[0]) + "\";");

				return;

			}

			System.out.print(tab + "\t" + ParamNames[outputattr] + " = {");

			for (int i=0; i < values.length; i++) {

				System.out.print("\"" + ParamDomains[outputattr].elementAt(i) + "\"");

						if ( i != values.length-1 ) System.out.print( " , " );

			}

			System.out.println( " };");

			return;

		}

		int numvalues = node.children.length;

		for (int i=0; i < numvalues; i++) {

			int indexofVal=Integer.parseInt(ParamDomains[node.decompositionAttribute].toString().substring(1, 2));
			//System.out.println("sund 6 " + indexofVal);
			System.out.println(tab + "if( " +

					paramwithNames.get(Integer.parseInt(ParamNames[node.decompositionAttribute])+1).toString().split(",")[0] + " == \"" +

					//ParamDomains[node.decompositionAttribute].elementAt(i)
					paramwithNames.get(Integer.parseInt(ParamNames[node.decompositionAttribute])+1).toString().split(",")[indexofVal]

					+ "\") {" );

			printTree(node.children[i], tab + "\t");

			if (i != numvalues-1) System.out.print(tab + "} else ");

			else System.out.println(tab + "}");

		}



	}
*/
	public void decisionTree() {


		
		paramwithNames.add("STABILITY,stab,xstab");
 		paramwithNames.add("ERROR,XL,LX,MM,SS");
		paramwithNames.add("SIGN,pp,nn");
 		paramwithNames.add("WIND,head,tail");
		paramwithNames.add("MAGNITUDE,Low,Medium,Strong,OutOfRange");
		paramwithNames.add("VISIBILITY,yes,no");
		paramwithNames.add("AUTO,noauto,auto");

		decomposeNode(root);

		printTree(root, "");

	}

	

	public static void main(String[] args) throws Exception {

		DTree me = new DTree();

	
		try{
		int status = me.readFileData("C:\\Users\\sundi133\\Downloads\\fall2011\\ML\\ass3\\shuttle_ext_unique.dat");
		me.decisionTree();
		}catch (Exception e) {
		
			System.out.println("Error in reading file");
			e.printStackTrace();
		}

	}

}