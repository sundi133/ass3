import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;



public class KNN {

	public static ArrayList training = new ArrayList();
	public static ArrayList testdata = new ArrayList();

	public static ArrayList printing = new ArrayList();
	public static HashMap<Integer, String> printMap = new HashMap<Integer, String>();
	private static int rowsize;
	static Writer output = null;


	public static void main(String[] args) {




		try {
			/* creates the link to read the data file */
			//with 64 as rowsize
			//rowsize=64;
			//String datafile = "C:\\Users\\sundi133\\Downloads\\fall2011\\ML\\ass3\\optdigits_tra_trans.dat";
			//with 1024 as rowsize
			rowsize=1024;
			String datafile = "C:\\Users\\sundi133\\Downloads\\fall2011\\ML\\ass3\\optdigits_tra.dat";

			KNN me = new KNN();
			int status = me.readData(datafile,0);

			//datafile = "C:\\Users\\sundi133\\Downloads\\fall2011\\ML\\ass3\\optdigits_trial_trans.dat";
			datafile = "C:\\Users\\sundi133\\Downloads\\fall2011\\ML\\ass3\\optdigits_trial.dat";
			status = me.readData(datafile,1);

			findnearest3(training,testdata);

		}catch (Exception e) {
			// TODO: handle exception
		}
	}


	private static void findnearest3(ArrayList training, ArrayList testdata) {

		ArrayList nearest = new ArrayList();

		try{
			for(int i=0;i<testdata.size();i++){


				ArrayList nearestRorRowI= new ArrayList();
				String resp=caldist(i);
				nearestRorRowI.add(resp);

				//int majprity=MajorityElement(resp);
				//System.out.println("nearest 3 neighbours  "+ resp + " - "+ " Query Point Digits in Test Data : "+ testdata.get(i).toString().split(",")[rowsize]);
				printing.add(testdata.get(i).toString().split(",")[rowsize] + "," + resp.substring(0,resp.length()-1));


			}
		

		process();
		/*for(int k=0;k<printing.size();k++){

			System.out.println("sundi printing " + printing.get(k));


		}*/

		File file = new File("writeknn.txt");
		
		output = new BufferedWriter(new FileWriter(file));
		//output.write(text);
		  

		for(int i=0;i<printing.size();i++){
			printData(i);
		}
		
		output.close();
		
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


	}

	private static void printData(int i) throws IOException {


		//System.out.println("                                               ");
		//System.out.println("                                               ");
		//System.out.println("                                                                ");
		int bitsize=32;
		int noofdigits=10;
		int col=4;
		String dat = printing.get(i).toString();
		String[] print= dat.split(",");
		
		//System.out.println("Data printed  " + print[0] + print[1] + print[2] + print[3]);
		
			String[] col1 =  printMap.get(Integer.parseInt(print[0])).toString().split(",");
			String[] col2 =  printMap.get(Integer.parseInt(print[1])).toString().split(",");
			String[] col3 =  printMap.get(Integer.parseInt(print[2])).toString().split(",");
			String[] col4 =  printMap.get(Integer.parseInt(print[3])).toString().split(",");
		
		for(int k=0;k<32;k++){
			//output.write(str)
			
			
			
			for(int k1=32*k;k1<(32*k)+32;k1++){
				output.write(col1[k1] + " ");
			}
			//System.out.print("\t");
			for(int k2=32*k;k2<(32*k)+32;k2++){
				output.write(col2[k2]+ " ");
			}
			//System.out.print("\t");
			for(int k3=32*k;k3<(32*k)+32;k3++){
				output.write(col3[k3]+ " ");
			}
			//output.write("\t");
			for(int k4=32*k;k4<(32*k)+32;k4++){
				output.write(col4[k4]+ " ");
			}
			//output.write(";");
			output.write("\n");
			
		}
		

		//System.out.println("                                               ");
		//System.out.println("                                               ");
		//System.out.println("                                               ");
		
	}


	private static void process() {
		for(int i=0;i<testdata.size();i++){
			printMap.put(Integer.parseInt(testdata.get(i).toString().split(",")[rowsize]),testdata.get(i).toString().substring(0,testdata.get(i).toString().length()-2));
			//System.out.println("key  " +Integer.parseInt(testdata.get(i).toString().split(",")[rowsize]) + " val " + testdata.get(i).toString().substring(0,testdata.get(i).toString().length()-2));



		}

	}


	static String caldist(int query) {
		int sum = 0;

		HashMap<Integer, Integer> near3Ints = new HashMap<Integer, Integer>();
		ArrayList<Integer> nearest3= new ArrayList();
		String quer=testdata.get(query).toString();
		int len = quer.split(",").length;
		try{
			//System.out.println("sundi "  +", "  + testdata.size() + ", " + training.size() );
			String train="";
			for(int j=0;j<training.size();j++){
				//Collections.sort(nearest3);
				//Collections.sort(near3Ints);
				train=training.get(j).toString();

				int dat =  Integer.parseInt(train.split(",")[rowsize]);
				int distance= eucliddist(train,quer);
				//System.out.println("query "+ query + "j "+ j+ " dist " + distance + "index : " + Integer.parseInt(train.split(",")[rowsize]));
				nearest3.add(distance);
				near3Ints.put(distance,dat);


				/*if(nearest3.size()<=3){//3 nearest neighbour
	    		nearest3.add(distance);
	    		near3Ints.add(train.split(",")[rowsize]);

	    	}else{

	    		for(int m=nearest3.size()-1;m>0;m--){
	    			if(distance < nearest3.get(m)){
	    				nearest3.remove(nearest3.get(m));
	    				nearest3.add(distance);
	    				near3Ints.remove(m);
	    				near3Ints.add(train.split(",")[rowsize]);
	    			}
	    		}
	    		//Collections.sort(nearest3);
	    	}*/

			}
			Collections.sort(nearest3);

			//for(int k=0;k<nearest3.size();k++){
			//System.out.println("sundi caldist "+ nearest3.get(k) + near3Ints.get(nearest3.get(k)) );
			//}

			String near3="";
			for(int k=0;k<3;k++){
				near3+=near3Ints.get(nearest3.get(k))+",";
			}
			return near3;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}
	}

	static int eucliddist(String train, String quer){
		int  dist=0;
		String[] tr=train.split(",");
		String[] qu=quer.split(",");
		try{
			for(int k=0;k<rowsize;k++){
				int difference = Integer.parseInt(tr[k]) - Integer.parseInt(qu[k]);
				dist += difference * difference;

			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return dist;
	}

	public int readData(String filename, int j) throws Exception {
		File file = null;
		FileReader freader = null;
		LineNumberReader lnreader = null;
		try{
			file = new File(filename);
			freader = new FileReader(file);
			lnreader = new LineNumberReader(freader);
			String line = "";
			if(j==0){
				while ((line = lnreader.readLine()) != null){
					StringTokenizer tokenizer = new StringTokenizer(line);
					int numtokens = tokenizer.countTokens();

					String row = "";
					for (int i=0; i < numtokens; i++) {
						String val = tokenizer.nextToken();
						row+=val+",";
						//System.out.println("sundi " + val);
					}

					training.add(row);
				}
				for(int k=0;k<training.size();k++){
					//System.out.println("sundi train " +training.get(k));
				}
			}else{

				while ((line = lnreader.readLine()) != null){
					StringTokenizer tokenizer = new StringTokenizer(line);
					int numtokens = tokenizer.countTokens();

					String row = "";
					for (int i=0; i < numtokens; i++) {
						String val = tokenizer.nextToken();
						row+=val+",";
						//System.out.println("sundi " + val);
					}

					testdata.add(row);
				}
				for(int k=0;k<testdata.size();k++){
					//System.out.println("sundi test " +testdata.get(k));
				}
			}


			freader.close();
			lnreader.close();

		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return 1;

	}



}