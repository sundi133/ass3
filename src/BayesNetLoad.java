import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

class BayesNetData
{
	class Node
	{
		final public Map<Map<String,Boolean>, Float> probs = new HashMap<Map<String,Boolean>, Float>();
		final public Set<String> neighbours = new TreeSet<String>();
	}

	final Map<String, Node> _graph = new HashMap<String, Node>();
	ArrayList parent = new ArrayList();
	ArrayList child = new ArrayList();
	String childs="";
	String parents="";


	private static String NameNormalize(Object o)
	{
		return o.toString().trim().toLowerCase();
	}

	private void _loadGraph(InputStream is_graph) throws InvalidPropertiesFormatException, IOException
	{
		final Properties graph = new Properties();
		graph.loadFromXML(is_graph);

		for(Map.Entry<Object, Object> item:graph.entrySet())
		{
			final String key = NameNormalize(item.getKey());

			final Set<String> nodes;

			if(_graph.containsKey(key))
				nodes = _graph.get(key).neighbours;
			else
			{
				final Node node = new Node();
				nodes = node.neighbours;
				_graph.put(key, node);
			}

			final String[] values = item.getValue().toString().split(",");
			for(String node:values)
				nodes.add(NameNormalize(node));
		}		
	}

	private void _loadProbs(InputStream is_probs) throws InvalidPropertiesFormatException, IOException
	{
		final Properties probs = new Properties();
		probs.loadFromXML(is_probs);
		for(Map.Entry<Object, Object> item:probs.entrySet())
		{
			final String key = item.getKey().toString();
			final String[] parts = key.split("\\|");
			final Node node = _graph.get(NameNormalize(parts[0]));
			if(node!=null)	// just skip absent nodes
				node.probs.put(createProbKey(parts.length>1?parts[1]:""), Float.valueOf(item.getValue().toString()));
		}
	}

	public BayesNetData(InputStream is_graph, InputStream is_probs) throws InvalidPropertiesFormatException, IOException
	{
		_loadGraph(is_graph);
		_loadProbs(is_probs);
	}

	public Set<String> getNodes() 
	{	
		return Collections.unmodifiableSet(new TreeSet<String>(_graph.keySet()));
	}

	public Set<String> getNeighbours(String snode)
	{
		final Node node = _graph.get(NameNormalize(snode));
		return node==null?null:Collections.unmodifiableSet(node.neighbours);
	}

	public Map<String, Boolean> createProbKey(String s)
	{
		return createProbKey(s.split(","));
	}

	public Map<String, Boolean> createProbKey(String... skeys)
	{
		final Map<String, Boolean> pkey = new HashMap<String, Boolean>();

		for(String key: skeys)
		{
			boolean value = true;
			String nkey = NameNormalize(key);
			if(nkey.startsWith("~"))
			{
				value = false;
				nkey = NameNormalize(nkey.substring(1));
			}
			if(_graph.containsKey(nkey))
				pkey.put(nkey, value);
		}

		//System.out.println("mapkey="+pkey);

		return pkey;
	}

	public Map<Map<String,Boolean>, Float> getProbabilityTable(String snode)
	{
		final Node node = _graph.get(NameNormalize(snode));
		return Collections.unmodifiableMap(node.probs);	
	}

	public float getProbabilityVal(String node, String cond)
	{
		final Map<Map<String,Boolean>, Float> map = getProbabilityTable(node);
		if(map==null)
			return 0;
		final Float v = map.get(createProbKey(cond));
		return v==null?0:v;
	}
	public float getProbability(String node, String cond)
	{
		int neg=0;
		try{
		final Map<Map<String,Boolean>, Float> map = getProbabilityTable(node);
		}catch (Exception e) {
			// TODO: handle exception
			node=node.split("~")[1];
			neg=1;
			final Map<Map<String,Boolean>, Float> map = getProbabilityTable(node);
		}
		ArrayList x= new ArrayList();//query vars
		x.add(node);
		ArrayList e= new ArrayList();
		String[] esplits=cond.split(",");//condn vars
		for(int k=0;k<esplits.length;k++){
			if(esplits.length>0 && esplits[k].trim()!=""){
				e.add(esplits[k]);
				//System.out.println(" test " +esplits[k]);
			}
				
		}
		ArrayList h=new ArrayList();
		//sundi
		ArrayList all=new ArrayList();
		Set<String> all1 =  getNodes();

		Iterator itr = all1.iterator();
		while (itr.hasNext()) {
			String element = itr.next().toString();
			all.add(element);
			h.add(element);
		}

		for(int k=0;k<all.size();k++){

			//System.out.println("else remove test 1");
			if(((String) all.get(k)).indexOf(node.toString())!=-1 ){
				////System.out.println("1 i" + all.get(k) + node.toString());
				//System.out.println("else remove test2");
				h.remove(all.get(k));
			}else{
				//System.out.println("else remove test3");
				if(((String) ("~"+all.get(k))).indexOf(node.toString())!=-1 ){
					//System.out.println("else remove test4");
					h.remove(all.get(k));
				}
			
			}

			for(int k1=0;k1<e.size();k1++){
				//System.out.println("else remove test5");
				if(((String) all.get(k)).indexOf(e.get(k1).toString())!=-1){
					//System.out.println("else remove test6");
					////System.out.println("2 i" + all.get(k) + e.get(k1));
					h.remove(all.get(k));
				}	else{
					//System.out.println("else remove test");
					if(((String) ("~"+all.get(k))).indexOf(e.get(k1).toString())!=-1){
						h.remove(all.get(k));
					}
						
					////System.out.println("2 e" + all.get(k) + e.get(k1));
					//h.add(all.get(k));
				}
			}

		}

		for(int k=0;k<x.size();k++){
			//System.out.println("sundi x" + x.get(k));
			//x.add(esplits)
		}
		for(int k=0;k<e.size();k++){
			//System.out.println("sundi e" + e.get(k));
		}
		for(int k=0;k<h.size();k++){
			//System.out.println("sundi h" + h.get(k));
		}

		ArrayList<Float> distribtion = new ArrayList<Float>();
		distribtion = Probvalues(x,e,h,node,cond);
		
		if(neg==1){
			return (float) distribtion.get(1);
		}else{
			return (float) distribtion.get(0);
		}
		

	}

	private ArrayList<Float> Probvalues(ArrayList x, ArrayList e, ArrayList h, String node, String cond) {
		String enumerateAll="";
		for(Entry<String, Node> item:_graph.entrySet())
		{
			final String key = NameNormalize(item.getKey());

			final Set<String> nodes;

			////System.out.println("sun1234"+key +  object.toString());
			//final Set<String> nodes;
			if(_graph.containsKey(NameNormalize(key))){
				nodes = _graph.get(NameNormalize(key)).neighbours;


				Iterator itr = nodes.iterator();
				while (itr.hasNext()) {
					String element = itr.next().toString();
					////System.out.println("sun12345" + key +","+element +","+ nodes.size());
					childs+=element+",";
					//childs=getNeighbours(NameNormalize(key));
				}
				//Array[] nodes2 = nodes.;
				//Iterator iter = nodes.iterator();


			}
			child.add(key+"<child>" +childs);
			childs="";
		}

		//fomr parents graph
		for(Entry<String, Node> item:_graph.entrySet())
		{
			final String key = NameNormalize(item.getKey());

			for(int k=0;k<child.size();k++){
				String item1=(String) child.get(k);
				String[] ch=item1.split("<child>");
				if(ch.length > 0){
					try{
						if(ch[1].indexOf(key)!=-1){
							/*if(e.contains(ch[0])){
							parents+=ch[0]+",";
						}else if(e.contains("~"+ch[0])){
							parents+="~"+ch[0]+",";
						}*/
							parents+=ch[0]+",";

						}
					}catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
				}
			}

			parent.add(key+"<parents>" +parents);
			parents="";
		}
		for(int k3=0;k3<child.size();k3++){
			//System.out.println("sun123457 " + child.get(k3));
		}
		for(int k3=0;k3<parent.size();k3++){
			//System.out.println("sun123456 " + parent.get(k3));
		}

		ArrayList<Float> askVals= new ArrayList<Float>();
		ArrayList<Float> probs= new ArrayList<Float>();
		for(int k=0;k<2;k++){
			//enumerateAll+= enumerateAll(x,e,h,parent, node,  cond,k) ;
			askVals.add(enumerateAll(x,e,h,parent, node,  cond,k));
		}
		float total = 0 ;
		for(int k=0;k<askVals.size();k++){
			total += askVals.get(k);
			
			
		}
		
		for(int k=0;k<askVals.size();k++){
		float Prob =  askVals.get(k)/total;

		probs.add(Prob);
		//System.out.println("sundi " + k + ",Probaility : " + Prob + ", total : " + total + " askval :" + askVals.get(k));
		}
		


		return probs;
	}

	private float enumerateAll(ArrayList x, ArrayList e, ArrayList h, ArrayList parent, String node, String cond, int bool) {
		
		//x is the query var, e given cond, h hidden variables 
		/*//System.out.println("sun123");
		for(int k3=0;k3<parent.size();k3++){
			//System.out.println("sun123456 enumerateAll" + parent.get(k3));
		}*/
		String xq= x.get(0).toString();
		if(bool==0){

			String[] xq2= xq.split("<parents>");
			if(xq2.length >1){
				//System.out.println("test bool 1" + bool);
				xq=xq2[0].trim().toString();
				/*if(xq2[0].trim().toString().indexOf("~")!=-1)
				xq=xq2[0].trim().toString();
			else {
				xq=xq2[0].trim().toString().split("~")[1];
			}*/
				if(xq.contains("~~")){
					xq=xq.split("~~")[1];
				}
			}else{
				//System.out.println("test bool 2" + bool);
				xq=xq2[0].trim().toString();;
				if(xq.contains("~~")){
					xq=xq.split("~~")[1];
				}
			}
		}else {

			xq= x.get(0).toString();
			String[] xq2= xq.split("<parents>");
			if(xq2.length >1){
				//System.out.println("test bool 3" + bool);
				xq="~"+xq2[0].trim().toString();
				if(xq.contains("~~")){
					xq=xq.split("~~")[1];
				}
				/*if(xq2[0].trim().toString().indexOf("~")!=-1)
					xq=xq2[0].trim().toString();
				else {
					xq=xq2[0].trim().toString().split("~")[1];
				}*/
			}	else{
				//System.out.println("test bool 4" + bool);
				xq="~"+xq2[0].trim().toString();
				if(xq.contains("~~")){
					xq=xq.split("~~")[1];
				}
			}
		}
		//xq is now query var

		//get e's
		String ecodn="";
		for(int p=0;p<e.size();p++){
			if(p==e.size()-1){
				ecodn+=e.get(p);
			}else{
				ecodn+=e.get(p)+",";
			}


		}

		//System.out.println("sundi 980" + xq +"," + ecodn);
		//hidden var condtions enumerate
		ArrayList hconds= new ArrayList();
		for(int k=0;k<h.size();k++){
			String hcond=h.get(k).toString();
			String tmpcond="";
					
			hconds.add(hcond +  "," +tmpcond );
			//hconds.add("~"+hcond +"," + tmpcond );
			
			
		}
		float v=0;
		Set<String> all2 =  getNodes();

		ArrayList all= new ArrayList();
		Iterator itr = all2.iterator();
		while (itr.hasNext()) {
			String element = itr.next().toString();
			all.add(element);

		}
		if(hconds.size()>0 && hconds.size()==2){
		for(int k=0;k<hconds.size();k++){
			
			/*if(k==1){
				hidden.add("~"+ hconds.get(k));
			}else{
				hidden.add(hconds.get(k));
			}*/
			    
			    //hconds.add("~"+hcond +"," + tmpcond );
			for(int k1=0;k1<hconds.size();k1++){
				ArrayList hidden=new ArrayList();
				/*if(k1!=k){
				if(k1==1){
					hidden.add("~"+ hconds.get(k1));
				}else{
					hidden.add(hconds.get(k1));
				}
				}
				
				for(int k2=0;k2<hconds.size();k2++){
				if(k2!=k){
					hidden.add(hconds.get(k2));
				}
				}*/
				if(k==0 && k1==0){
					try{
						//System.out.println("sund " + k +"," + k1);
					hidden.add(hconds.get(k));
					hidden.add(hconds.get(k1+1));
					}catch (Exception e2) {
						// TODO: handle exception
						System.out.println("sund " + k +"," + k1);
					}
				}
				if(k==0 && k1==1){
					hidden.add(hconds.get(k));
					hidden.add("~"+hconds.get(k1));
				}
				if(k==1 && k1==0){
					hidden.add(hconds.get(k));
					hidden.add("~"+hconds.get(k1));
				}
				if(k==1 && k1==1){
					hidden.add("~"+hconds.get(k));
					hidden.add("~"+hconds.get(k1-1));
				}
				
				
				String hid="";
				for(int p=0;p<hidden.size();p++){
					hid+=hidden.get(p)+",";
				}
				String ecodn1="";
				if(ecodn.trim().equalsIgnoreCase("")){
					ecodn1=hid.substring(0, hid.lastIndexOf(","));
				}
				else{
					ecodn1=ecodn+hid.substring(0, hid.lastIndexOf(","));
				}
				System.out.println("Probaility : + " + xq +"|" + ecodn1);

				String[] pars = null;
				/*for(int k3=0;k3<parent.size();k3++){
					
					//System.out.println("var   " + parent.get(k3) +"," +xquery);
					if(parent.get(k3).toString().split("<parents>").length>1){
					String key= parent.get(k3).toString().split("<parents>")[0].trim();
					if(key.equalsIgnoreCase(xq)){
						//System.out.println("equal");
						if(parent.get(k3).toString().split("<parents>").length>1){
						 pars=parent.get(k3).toString().split("<parents>")[1].trim().split(",");
						for(int p=0;p<pars.length;p++){
							//System.out.println("parents  " + p +"," +pars[p]);
						}
						}
						
					}
					}

				}*/
				
					
				
					float v1= Probvalues(xq,ecodn1,hconds,all,0);
					System.out.println(" " + v1);
					v+=v1;
				
				
				
				   

			}
			
		}
		}
		else if(hconds.size()>0 && hconds.size()==1){
			
				for(int k1=0;k1<hconds.size()+1;k1++){
					ArrayList hidden=new ArrayList();
					/*if(k1!=k){
					if(k1==1){
						hidden.add("~"+ hconds.get(k1));
					}else{
						hidden.add(hconds.get(k1));
					}
					}
					
					for(int k2=0;k2<hconds.size();k2++){
					if(k2!=k){
						hidden.add(hconds.get(k2));
					}
					}*/
					if( k1==0){
						try{
						
						
						hidden.add(hconds.get(k1));
						}catch (Exception e2) {
							// TODO: handle exception
							System.out.println("sund " +  "," + k1);
						}
					}
					if(k1==1){
						
						hidden.add("~"+hconds.get(k1-1));
					}
					
					
					
					String hid="";
					for(int p=0;p<hidden.size();p++){
						hid+=hidden.get(p)+",";
					}
					String ecodn1="";
					if(ecodn.trim().equalsIgnoreCase("")){
						ecodn1=hid.substring(0, hid.lastIndexOf(","));
					}
					else{
						ecodn1=ecodn+","+hid.substring(0, hid.lastIndexOf(","));
					}
					System.out.println("Probaility : + " + xq +"|" + ecodn1);

			
					
						float v1= Probvalues(xq,ecodn1,hconds,all,0);
						System.out.println(" " + v1);
						v+=v1;
					
					
					
					   

				}
			
		}	else{
		
			v = Probvalues(xq,ecodn,hconds,all,0);
			//System.out.println("v val  " + v);
		}

		//v = Probvalues(x,xq,ecodn,hconds);
		//String resp=getProbabilityVal() 



		return v;
	}


	private float Probvalues(String xq, String ecodn,
			ArrayList hconds,ArrayList all, int i) {

		//System.out.println("sun i " + i +"," +all.size());
		if(all.size()==i){
			return 1;
		}
		/*p(rain|grass_wet,sprinkler)
		 * 
		 * 
		 * 
		 */


		//probability of all parents


		float v=1;
		String xquery= all.get(i).toString();


		//get all parents of xquery
		String[] pars = null;
		for(int k3=0;k3<parent.size();k3++){
			
			//System.out.println("var   " + parent.get(k3) +"," +xquery);
			if(parent.get(k3).toString().split("<parents>").length>1){
			String key= parent.get(k3).toString().split("<parents>")[0].trim();
			if(key.equalsIgnoreCase(xquery)){
				//System.out.println("equal");
				if(parent.get(k3).toString().split("<parents>").length>1){
				 pars=parent.get(k3).toString().split("<parents>")[1].trim().split(",");
				for(int p=0;p<pars.length;p++){
					//System.out.println("parents  " + p +"," +pars[p]);
				}
				}
				
			}
			}

		}


		
		String parcond="";
		try{
		String[] cond=(ecodn+","+xq).split(",");
		if(pars!=null){
		for(int j =0;j<pars.length;j++){
			//System.out.println("pars 1234" + pars[j]);
			if(cond.length>0){
				for(int k=0;k<cond.length;k++){
					//System.out.println("pars 1234" + pars[j] + "," + cond[k]);
					//check the values the parents(+/-) based on condition
				    if(pars[j].equalsIgnoreCase(cond[k])){
				    	parcond+=cond[k]+",";
				    }else if(("~"+pars[j]).equalsIgnoreCase(cond[k])){
				    	parcond+=cond[k]+",";
				    }else{
				    	//parcond+=pars[j]+",";
				    }
				}
			}else{
				parcond+=pars[j]+",";
			}
		
		}
		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		//xquery variable
		try{
			String[] cond=(ecodn+","+xq).split(",");
			
			for(int j =0;j<parent.size();j++){
				//System.out.println("pars 1234" );
				if(cond.length>0){
					for(int k=0;k<cond.length;k++){
						//System.out.println("pars 4567" + xquery + "," + cond[k]);
						//check the values the parents(+/-) based on condition
					    if(xquery.equalsIgnoreCase(cond[k])){
					    	xquery=cond[k];
					    }else if(("~"+xquery).equalsIgnoreCase(cond[k])){
					    	xquery=cond[k];
					    }else{
					    	//parcond+=pars[j]+",";
					    }
					}
				}else{
					if(xquery.equalsIgnoreCase(xq)){
						
					}else if(("~"+xquery).equalsIgnoreCase(xq)){
						xquery="~"+xquery;
					}
						
						
				}
				
			}}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		
		//
		

		//take the prob from look up table
		//v=getProbability(xquery, parcond);
		//pass for multiply recyursively
		try{
		parcond=parcond.substring(0, parcond.lastIndexOf(","));
		}catch (Exception e) {
			// TODO: handle exception
			parcond="";
		}
	
		if(xquery.indexOf("~")!=-1){
			v = 1-getProbabilityVal(xquery.split("~")[1].trim(), parcond);
		}else{
			v=getProbabilityVal(xquery, parcond);	
		}
			
		
		//System.out.println("par  " + i  + xquery + ",  " +parcond + ",  " + v);
		int j=i+1;
		float expectedResult=0;
		if(Math.abs(v - expectedResult) < 0.00001)
			return 0;
		v=v*Probvalues(xq,ecodn,hconds,all,j);

		return v;
	}





}



public class BayesNetLoad 
{
	public static void PrintGraph(BayesNetData bn)
	{
		for(String node: bn.getNodes())
			System.out.println(node+" -> "+bn.getNeighbours(node));
	}

	public static void PrintProbs(BayesNetData bn)
	{
		for(String node: bn.getNodes())
		{

			//System.out.println(node+" -> "+bn.getProbabilityTable(node));
			//Map<Map<String,Boolean>, Float> map = bn.getProbabilityTable(node);			
		}
	}

	public static void main(String[] args) throws IOException
	{	
		final BayesNetData bn = new BayesNetData(new FileInputStream("graph.xml"),
				new FileInputStream("probs.xml"));
		PrintGraph(bn);
		String node = null;
		String cond = null;

		System.out.print("Enter query variable : ");
	      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	      try {
	         node = br.readLine();
	      } catch (IOException ioe) {
	         System.out.println("IO error trying to read your name!");
	         System.exit(1);
	      }
	      System.out.print("Enter conditions variable with comma separated values : ");
	      br = new BufferedReader(new InputStreamReader(System.in));
	      try {
	         cond = br.readLine();
	      } catch (IOException ioe) {
	         System.out.println("IO error trying to read your name!");
	         System.exit(1);
	      }

	      //System.out.println("Condtions : " + cond);

		if((cond.equalsIgnoreCase(" ") || cond==null || cond.length()==0) || (cond.equalsIgnoreCase(""))){
			System.out.println("P("+node+")="+bn.getProbability(node, cond));
			//System.out.print("P("+node+")=");
			//System.out.printf("%.2f", bn.getProbability(node, cond));
		}else{
			System.out.println("P("+node+"|"+cond+")="+bn.getProbability(node, cond));
			//System.out.print("P("+node+"|"+cond+")=");
			//System.out.printf("%.2f", bn.getProbability(node, cond));
			
		}
		
		
	}
}
//String cond = "rain,~sprinkler";
//String condequ = "~sprinkler,rain";
//final String node = "rain";
//final String condequ = "~sprinkler,grass_wet";