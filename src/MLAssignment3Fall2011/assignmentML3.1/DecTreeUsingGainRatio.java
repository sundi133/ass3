

	import java.io.*;

import java.util.*;

	public class DecTreeUsingGainRatio

	{

		int paramAttributes;

		String[] ParamNames;

		Vector[] ParamDomains;
		
		ArrayList paramwithNames= new ArrayList(7);

		/*
		 * The class to represent a data point consisting of paramAttributes values
		 * 
		 * of attributes
		 */

		class DataPoint {

			public int[] attributes;

			public DataPoint(int numattributes) {

				attributes = new int[numattributes];

			}

		};

		/*
		 * The class to represent a node in the decomposition tree.
		 */

		class TreeNode {

			public double entropy;

			public Vector data;

			public int decompositionAttribute;

			public int decompositionValue;

			public TreeNode[] children;

			public TreeNode parent;

			public TreeNode() {

				data = new Vector();

			}

		};

		TreeNode root = new TreeNode();
		TreeNode rootgr = new TreeNode();

		public int getSymbolValue(int attribute, String symbol) {

			int index = ParamDomains[attribute].indexOf(symbol);

			if (index < 0) {

				ParamDomains[attribute].addElement(symbol);

				return ParamDomains[attribute].size() - 1;

			}

			return index;

		}

		public int[] getAllValues(Vector data, int attribute) {

			Vector values = new Vector();

			int num = data.size();

			for (int i = 0; i < num; i++) {

				DataPoint point = (DataPoint) data.elementAt(i);

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

				DataPoint point = (DataPoint) data.elementAt(i);

				if (point.attributes[attribute] == value){
					subset.addElement(point);
					//System.out.println("sundi  : " + point.toString() + ", "+attribute + ", " + value);
					
				}

			}

			return subset;

		}

		public double calculateEntropy(Vector data) {

			int numdata = data.size();

			if (numdata == 0)
				return 0;

			int attribute = paramAttributes - 1; 

			int numvalues = ParamDomains[attribute].size();

			double sum = 0;

			for (int i = 0; i < numvalues; i++) {

				int count = 0;

				for (int j = 0; j < numdata; j++) {

					DataPoint point = (DataPoint) data.elementAt(j);

					if (point.attributes[attribute] == i)
						count++;

				}

				double probability = 1. * count / numdata;

				
				if (count > 0)
					sum += -probability * Math.log(probability);
				
				

			}

			//System.out.println("sundi log " + sum);
			return sum;

		}

		public boolean alreadyUsedToDecompose(TreeNode node, int attribute) {

			if (node.children != null) {

				if (node.decompositionAttribute == attribute)

					return true;

			}

			if (node.parent == null)
				return false;

			return alreadyUsedToDecompose(node.parent, attribute);

		}

		public void decomposeNodeUsingGRatio(TreeNode node) {
			
			//System.out.println(ParamDomains.length + "test " );
			/*for(int k=0;k<ParamDomains.length;k++)
				System.out.println( "1 : " +paramwithNames.get(k)+ ParamDomains[k]);

			for(int k=0;k<ParamNames.length;k++)
				System.out.println( "2 : " +paramwithNames.get(k)+ ParamNames[k]);
				*/


			double bestGainRatio=0;

			boolean selected=false;

			int selectedAttribute=0;

			int numdata = node.data.size();

			int numinputattributes = paramAttributes-1;

			node.entropy = calculateEntropy(node.data);
			//System.out.println(" sundi 678 :  " + node.entropy +  node.data);

			if (node.entropy == 0) return;



			for (int i=0; i< numinputattributes; i++) {

				int numvalues = ParamDomains.length;

				if ( alreadyUsedToDecompose(node, i) ) continue;

				double averageentropy = 0;
				double InfoGain=0;

				for (int j=0; j< numvalues; j++) {

					Vector subset = getSubset(node.data, i, j);

					if (subset.size() == 0) continue;

					double subentropy = calculateEntropy(subset);
					//System.out.println(" sundi 910 :  "  + i + ", "+j + ", "+ subentropy + ", "+averageentropy + subset.size());


					averageentropy += subentropy *	subset.size();
					//System.out.println(" sundi 911 :  "  + i + ", "+j + ", "+ subentropy + ", "+averageentropy + subset.size());


				}

				averageentropy = averageentropy / numdata; 
			
				InfoGain= (calculateEntropy(node.data) - averageentropy);
				
				//System.out.println( " sundi " + i +", " + InfoGain);

				double gainratio=0;
				float averagesplitInfo=0;
				
				for (int j=0; j< numvalues; j++) {

					Vector subset = getSubset(node.data, i, j);

					if (subset.size() == 0) continue;

					double subspltinfo = calculateSplit(subset,numdata);
					//System.out.println(" sundi 910 :  "  + i + ", "+j + ", "+ subentropy + ", "+averageentropy + subset.size());


					
					//averagesplitInfo += (float) (subspltinfo *	(1. *subset.size()/numdata));
					averagesplitInfo += (float) (subspltinfo );
					//System.out.println(" sundi 911 :  "  + i + ", "+j + ", "+ averagesplitInfo + ","+ subset.size() + "," + numdata + ", " + subspltinfo);


				}
				
				//averagesplitInfo=averagesplitInfo/numdata;
				
				gainratio=1. *InfoGain/averagesplitInfo;
				//System.out.println(" sundi 912 :  "  + i + ", "+ gainratio + " ," + InfoGain +", " + averagesplitInfo);
				//splitInfo()
				
				//System.out.println(" sundi 912 :  "  + i +", "+averageentropy);
				//Taking the weighted average

				// to take the max of ingo gain , tka the least of the the (|Sv|/S)*( entropy (Sv)), summed over Sv
				if (selected == false) {

					selected = true;

					bestGainRatio = gainratio;

					selectedAttribute = i;

				} else {

					if (gainratio >= bestGainRatio) {

						selected = true;

						bestGainRatio = gainratio;

						selectedAttribute = i;

					}

				}

			}

			if (selected == false) return;

			int numvalues = ParamDomains[selectedAttribute].size();

			node.decompositionAttribute = selectedAttribute;

			node.children = new TreeNode [numvalues];

			for (int j=0; j< numvalues; j++) {

				node.children[j] = new TreeNode();

				node.children[j].parent = node;

				node.children[j].data = getSubset(node.data,selectedAttribute, j);

				node.children[j].decompositionValue = j;

			}



			for (int j=0; j< numvalues; j++) {

				decomposeNodeUsingGRatio(node.children[j]);

			}



			node.data = null;

		}


		private double calculateSplit(Vector data, int numdata2) {
			
			int numdata = data.size();
			if (numdata == 0)
				return 0;
			float sum=0;
			//System.out.println("sundi log 219 " + data.size()/numdata2);
			double split=1. *data.size()/numdata2;
			sum += -split * Math.log(split);
			//System.out.println("sundi 932 " + data.size() + "," + numdata2+", " +(data.size()/numdata2) +","+ sum);
			return sum;
			
		}

		public int readData(String filename) throws Exception {

			FileInputStream in = null;

			try {

				File inputFile = new File(filename);

				in = new FileInputStream(inputFile);

			} catch ( Exception e) {

				System.err.println( "Unable to open data file: " + filename + "\n" + e);

				return 0;

			}

			BufferedReader bin = new BufferedReader(new InputStreamReader(in) );

			String input;

			while(true) {

				input = bin.readLine();

				if (input == null) {

					System.err.println( "No data found in the data file: " + filename +

					"\n");

					return 0;

				}

				if (input.startsWith("//")) continue;

				if (input.equals("")) continue;

				break;

			}



			StringTokenizer tokenizer = new StringTokenizer(input);

			paramAttributes = tokenizer.countTokens();

			if (paramAttributes <= 1) {

				System.err.println( "Read line: " + input);

				System.err.println( "Could not obtain the names of attributes in the line");

						System.err.println( "Expecting at least one input attribute and one output attribute");

								return 0;

			}

			ParamDomains = new Vector[paramAttributes];

			for (int i=0; i < paramAttributes; i++) ParamDomains[i] = new Vector();

			ParamNames = new String[paramAttributes];

			for (int i=0; i < paramAttributes; i++) {

				//ParamNames[i] = tokenizer.nextToken(); sundi
				ParamNames[i] = String.valueOf(i);
				

			}



			while(true) {

				input = bin.readLine();

				if (input == null) break;

				if (input.startsWith("//")) continue;

				if (input.equals("")) continue;

				tokenizer = new StringTokenizer(input);

				int numtokens = tokenizer.countTokens();

				if (numtokens != paramAttributes) {

					System.err.println( "Read " + root.data.size() + " data");

					System.err.println( "Last line read: " + input);

					System.err.println( "Expecting " + paramAttributes + " attributes");

					return 0;

				}

				DataPoint point = new DataPoint(paramAttributes);
				String autoval = tokenizer.nextToken();

				for (int i=0; i < paramAttributes-1; i++) {

					point.attributes[i] = getSymbolValue(i, tokenizer.nextToken());

				}

				point.attributes[paramAttributes-1] = getSymbolValue(paramAttributes-1, autoval);
				root.data.addElement(point);

			}

			bin.close();

			return 1;

		}

		public void printTree(TreeNode node, String tab) {

			int outputattr = paramAttributes-1;

			if (node.children == null) {

				int []values = getAllValues(node.data, outputattr );
				
				

				if (values.length == 1) {

					//System.out.println(tab + "\t" + ParamNames[outputattr] + " = \"" +
					System.out.println(tab + "\t" + paramwithNames.get(outputattr).toString().split(",")[0] + " = \"" +
							ParamDomains[outputattr].elementAt(values[0]) + "\";");

					return;

				}

				//System.out.print(tab + "\t" + ParamNames[outputattr] + " = {");
				System.out.print(tab + "\t" + paramwithNames.get(outputattr).toString().split(",")[0] + " = {");
				

				for (int i=0; i < values.length; i++) {

					System.out.print("\"" + ParamDomains[outputattr].elementAt(i) + "\"");

							if ( i != values.length-1 ) System.out.print( " , " );

				}

				System.out.println( " };");

				return;

			}

			int numvalues = node.children.length;

			for (int i=0; i < numvalues; i++) {

				 Object elem = ParamDomains[node.decompositionAttribute].elementAt(i);
				 //System.out.println("sundi " + elem.toString());
				int indexofVal=Integer.parseInt(elem.toString().substring(0, 1));
				//System.out.println("sundi " + indexofVal);
				System.out.println(tab + "if( " +

						//ParamNames[node.decompositionAttribute] + " == \"" +
						paramwithNames.get(Integer.parseInt(ParamNames[node.decompositionAttribute])).toString().split(",")[0] + " == \"" + 

						//ParamDomains[node.decompositionAttribute].elementAt(i)
						paramwithNames.get(Integer.parseInt(ParamNames[node.decompositionAttribute])).toString().split(",")[indexofVal]

						+ "\") {" );

				printTree(node.children[i], tab + "\t");

				if (i != numvalues-1) System.out.print(tab + "} else ");

				else System.out.println(tab + "}");

			}



		}

		public void createDecisionTreeUsingGRatio() {

			
			paramwithNames.add("STABILItY,stab,xstab");
	 		paramwithNames.add("ERROR,XL,LX,MM,SS");
			paramwithNames.add("SIGN,pp,nn");
	 		paramwithNames.add("WIND,head,tail");
			paramwithNames.add("MAGNITUDE,Low,Medium,Strong,OutOfRange");
			paramwithNames.add("VISIBILITY,yes,no");
			paramwithNames.add("AUTO,noauto,auto");
			System.out.println("Tree using Gain Ratio");
			decomposeNodeUsingGRatio(root);
			printTree(root, "");

		}



		/* main function */

		public static void main(String[] args) throws Exception {

			DecTreeUsingGainRatio me = new DecTreeUsingGainRatio();

			int status = me.readData("C:\\Users\\sundi133\\Downloads\\fall2011\\ML\\ass3\\shuttle_ext_unique.dat");


			//System.out.println( "test " );
			me.createDecisionTreeUsingGRatio();
			;

		}

	}
