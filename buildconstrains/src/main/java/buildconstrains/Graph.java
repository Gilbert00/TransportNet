package buildconstrains;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
//import java.util.Hashtable;
import java.util.LinkedHashMap;
//import java.util.Map;

/**
 *
 * @author пользователь
 */

//--------
//--------
class VertexSet {
    BigInteger vset;
	int len;
	
	VertexSet(){
            this.vset = new BigInteger("0");
            this.len = 0;
	}
	
/*     VertexSet(int len){
		vset = new super(len);
    } */
	VertexSet(int ind){
//          if (ind < 0) throw new ValueError("Value of the " + ind + " is negative ! ");
	    this.vset = new BigInteger("0");
            this.vset = this.vset.setBit(ind);
            this.len = ind + 1;
	}
		
/* 	VertexSet(int len, int ind){
//          if (ind < 0) throw new ValueError("Value of the " + ind + " is negative ! ");
	    vset = new super(len);
        super.set(1<<ind, true);	
	} */
	
    //@Override
    public int length(){
         return this.vset.bitLength();
    } 
    
    public long to_int(){
        return this.vset.longValueExact();
	}
    
//    @Override
    public VertexSet vor(VertexSet other){
		VertexSet out = new VertexSet();
		out.vset = this.vset.or(other.vset);
		out.len = Math.max(this.len,other.len);		
        return out;
    }

    public VertexSet vand(VertexSet other){
		VertexSet out = new VertexSet();
		out.vset = this.vset.and(other.vset);
		out.len = this.len;
        return out;
    }

    public VertexSet invert(){
		VertexSet out = new VertexSet();
		out.vset = this.vset.not();
		out.len = this.len;
        return out;
    }
    
    public Boolean eq(VertexSet other){
        return this.vset.compareTo(other.vset) == 0;
    }

    public Boolean gt(VertexSet other){
		int ct = this.vset.compareTo(other.vset);
		return (ct == 1);
    }
    
/*     # def __ior__(self, other):
        # if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        # self.set |= other.set        

    def __iand__(self, other):
        if not isinstance(other, Set): raise TypeError(f"Type of the {other} isn't Set !")
        self.set &= other.set */

    @Override
    public String toString(){  
        //long n = this.to_int();
        return this.vset.toString(2);
    }
    
	String set2BinStr() {
		return this.toString();
	}
	
    public static VertexSet int2set(long k){
		VertexSet out = new VertexSet();
		String s = ((Long) k).toString();
		out.vset = new BigInteger(s);
		out.len = s.length();
		return out;
		
/*         VertexSet out = new VertexSet(len);
        String str = Long.toBinaryString(k);
		char[] array = str.toCharArray();
		int lstr1 = array.length-1;
		//if (lstr1+1 > len) Error!!!
		for (int i = lstr1; i >= 0; i--) 
			if (array[i]=='1') out.set(lstr1-i, true);
        return out; */
    }
        
    public static VertexSet EMPTY_SET(){
        return new VertexSet();
    }
        
/*     public static VertexSet[] EMPTY_TURTLE(){
        VertexSet es = EMPTY_SET();
        VertexSet[] out = {es,es};
        return out;
    } */
}                     
//--------
//--------
class Graph{
	boolean dual;
	ArrayList<String> lX;
	ArrayList<String> lY;
	LinkedHashMap<String, ArrayList<String>> graph;
	
    Graph() {
        dual = false;
        lX = new ArrayList<String>();
        lY = new ArrayList<String>();
        graph = new LinkedHashMap<>();
    }
    
    public boolean get_dual(){
        return this.dual;
    }
    
    public LinkedHashMap<String, ArrayList<String>> get_graph(){
        return graph;
	}
        
//-------- 
// Graph
    private static final String COMMA_DELIMITER = ",";

    public void input(String fileName){
		if (Constants.check_TST(new int[]{2})) System.out.println(" graph.input");
        try {
		//List<List<String>> records = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
			String row;
			while ((row = br.readLine()) != null) {
				//Tst System.out.printf("row: |%s|%n",row);
				if (row.trim().length() == 0) continue;
				String[] values = row.split(COMMA_DELIMITER);
			//	records.add(Arrays.asList(values));
				String key = values[0];
				//if (Constants.check_TST(new int[]{2})) System.out.printf("key: %s%n",key);
				ArrayList<String> vals = new ArrayList<String>();
				for(int i=1; i<values.length; i++){
					if (Constants.check_TST(new int[]{2})) System.out.printf("key, value: %s %s%n",key,values[i]);
					vals.add(values[i]);
				}
				this.graph.put(key, vals);
			}	
        }
        catch(IOException e){
            e.printStackTrace();
        }
                    //return graph;
     //   System.out.println()
     //   System.out.println(self.graph)
    }
//--------
// Graph
    boolean check_list(ArrayList<String> lst, String e){
//       System.out.println('list',l)
//       System.out.println('e',e)
        int n=lst.size();
        if (n==0) return false;
        for(String k: lst)
            if (k.equals(e)) return true;
        
        return false;
    }

    public void gen_sides() {
        
        if (Constants.check_TST(new int[]{2})) System.out.println(" gen_sides");
        for (Map.Entry<String, ArrayList<String>> e: this.graph.entrySet()) {
            String keyX = e.getKey();
    		if (Constants.check_TST(new int[]{2})) System.out.printf("keyX: %s%n",keyX);
            if (! check_list(lX, keyX))
                lX.add(keyX);
            
            for (String y: e.getValue()) {
                if (Constants.check_TST(new int[]{2})) System.out.printf("y: %s%n",y);
                if (! check_list(lY, y))
                    lY.add(y);
			}
        }
		if (Constants.check_TST(new int[]{2})) System.out.printf("lX: %s%n", lX.toString());
		if (Constants.check_TST(new int[]{2})) System.out.printf("lY: %s%n", lY.toString());
    }
//--------
// Graph
    public Graph create_dual(){
    //    String[] oldVal;
    //    String[] newVal;
        Graph graph_dual = new Graph();
     
    //    short NON=-1;
        
        for (Map.Entry<String, ArrayList<String>> e: this.graph.entrySet()) { // new y
            String keyX = e.getKey();
	//#Tst        System.out.println('keyX',keyX)
	
            for (String y: e.getValue()) { // new x
     //#Tst            System.out.println('y',y)
                if (! graph_dual.graph.containsKey(y)) {
     //#Tst                System.out.println('NON')
                    ArrayList<String> vkey = new ArrayList<String>();
                    vkey.add(keyX);
                    graph_dual.graph.put(y,vkey);
                }
                else if (! Matr.check_list(graph_dual.graph.get(y), keyX)) {
     //#Tst                    System.out.println('append')
					//!!! Set newVal by oldVal + keyX
/*                         ArrayList<String> oldVal = graph_dual.graph.get(y);
                        ArrayList<String> newVal = new ArrayList<String>();
                        for(int i=0; i<oldVal.size(); i++) {
                                newVal.add(oldVal.get(i));
                        } */
					ArrayList<String> newVal = new ArrayList<String>(graph_dual.graph.get(y));
                    newVal.add(keyX);
                    graph_dual.graph.replace(y,newVal);                        
				}
                //    else
                //        pass #Error! Dupl edje
            }
	}

     //#Tst2   System.out.println('graph_dual:',graph_dual.graph)
        for (Map.Entry<String, ArrayList<String>> e: graph_dual.graph.entrySet()) {
			String   key = e.getKey();
            ArrayList<String> arr = e.getValue();
            Arrays.sort(arr.toArray());
            graph_dual.graph.replace(key,arr);
	}
            
        graph_dual.dual = true;

//    System.out.println('graph_dual:',graph_dual.graph)
        return graph_dual;
    }

//--------
// Graph
    MatrG set_matrG(){ // #, nX, nY):
        MatrG matr = new MatrG(this.lX.size(), this.lY.size()); //#[ [0]*nY for i in range(nX) ]
    //   matrG: [indX][indY] -> 1 if x connect y
        for (String keyX: this.graph.keySet()) {
            int indX = this.lX.indexOf(keyX);
			for(String y: this.graph.get(keyX)) {
                int indY=this.lY.indexOf(y);
                matr.set_el(indX, indY, 1); //#matr.matrG[indX][indY] = 1
			}
		}
                
        return matr;
    }
}        
//--------        
//--------
class MatrG extends Matr<Integer>{
//    MatrG matrG;
	
	MatrG() {
            super();
	}	
    
	MatrG(int nX, int nY) {
            super(nX,nY);
	}
        
    ArrayList<Vect<Integer>> get_matr(){
        return matr;
    }

    VertexSet set_binY(int iv){
        VertexSet binY = new VertexSet();
//        System.out.println('binY',type(binY))
        int n = this.get_row(iv).len();
        for(int ky=0; ky<n; ky++) {
            if (this.get_el(iv,ky) == 1) {
//                System.out.println(f'Set({ky})',type(Set(ky)))
                binY = binY.vor(new VertexSet(ky));
//                System.out.println('ky,binY:',ky,binY.to_int())
            }
        }
	return binY;
    }
	
    void set_row(int kl, Vect<Integer> low) {
        super.set_row(kl,low);
    }
        
    void set_el(int ix, int iy, Integer val){
        super.set_el(ix,iy,val);
    }
      
	Integer get_el(int ix, int iy){
        return super.get_el(ix,iy);
    }
	  
    ParamsSortMatrG sort_matrG_by_x(ArrayList<String> lX, ArrayList<String> lY, QX npQX) {
/*         """
        matrG[x][y]
        lX[x]
        lY[y]
        -
        inbinMG[(binY,kx)],inlX[],inmatrMG[][]: binMG, lX, matrG
        """ */
        int nX = lX.size();
        int nY = lY.size();
        if (Constants.check_TST(new int[]{1})) System.out.println(" sort_matrG_by_x");
    //    inbinMG=[]
        if (Constants.check_TST(new int[]{1})) System.out.printf("matrG: %s%n",this.get_matr());
        if (Constants.check_TST(new int[]{1})) System.out.printf("lX: %d %s%n",nX,lX);
        if (Constants.check_TST(new int[]{1})) System.out.printf("lY: %d %s%n",nY,lY);
        if (Constants.check_TST(new int[]{1})) System.out.printf("npQX: %s%n",npQX.get_npQX());
		ParamsSortMatrG params = new ParamsSortMatrG();
        params.binMG = npQX.sort_BSF_x(this);
        if (Constants.check_TST(new int[]{1})) System.out.printf("inbinMG: %s%n",params.binMG.get_list().toString());
        

    // sort lX, matrG  
        //params.lX = ["" for i in range(nX) ];
	for(int i=0; i<nX; i++) params.lX.add("");
        params.matrG = new MatrG(nX,nY); // #[ [0]*nY for i in range(nX) ] 
        for(int kx=0; kx < params.binMG.len(); kx++){
           int kxOld = params.binMG.get_kxOld(kx);
           params.lX.set(kx, lX.get(kxOld)); 
           params.matrG.set_row(kx, this.get_row(kxOld));
		}
        if (Constants.check_TST(new int[]{1})) System.out.printf("inlX: %s%n",params.lX);
        if (Constants.check_TST(new int[]{1})) System.out.printf("inmatrMG: %s%n",params.matrG.get_matr());
        return params;
    }
}

class ParamsSortMatrG {
	MatrG matrG;
	ArrayList<String> lX;
	BinMG binMG;
	
	ParamsSortMatrG() {
		this.matrG = null;
		this.lX = new ArrayList<String>();
		this.binMG = new BinMG();
	}
}

//--------
//--------
class QX extends MatrG {
//    QX npQX;

    QX(MatrG matrG){
//		int nX = matrG.len();
		super(matrG.len(), matrG.len());
//        matr = matrG.get_matr();
        set_npQX(matrG);
    }

    void set_npQX(MatrG matrG){
//        npMG = np.array(matr)
        //Tst    System.out.println('npMG:',npMG)
//        npMX = np.dot(npMG, npMG.transpose())
        //Tst System.out.println(' after np.dot')
        //Tst System.out.println('npMX:',npMX)
		int nX = matrG.len();
		int nY = matrG.len2();
            //QX npQX = (QX) new MatrG(nX, nX);
		for(int i=0; i<nX; i++) {
			for(int k=0; k<nX; k++) {
				int s =0;
				for(int j=0; j<nY; j++) {
					s += matrG.get_el(i,j) * matrG.get_el(k,j);
				}
				s = Math.min(s,1);
				this.set_el(i,k,s);
			}
		}
//		np.minimum(npMX, 1)
       // return npQX;
    }

    ArrayList<Vect<Integer>> get_npQX(){  
        return this.matr; 
    }

    @Override    
    Integer get_el(int i, int j){  
        return (Integer) super.get_el(i,j);
    }
        
//    int len(){
//        return this.size();
//    }
    
    BinMG sort_BSF_x(MatrG matrG) {
/* # BFS
# Вход: граф G = (V, E), представленный в виде списков смежности,
# и вершина s из V.
# Постусловие: вершина достижима из s тогда и только тогда,
# когда она помечена как «разведанная».
# 1) пометить s как разведанную вершину, все остальные как не-
# разведанные
# 2) Q := очередь, инициализированная вершиной s
# 3) while Q не является пустой do
# 4) удалить вершину из начала Q, назвать ее v
# 5) for каждое ребро (v, w) в списке смежности v do
# 6) if w не разведана then
# 7) пометить w как разведанную
# 8) добавить w в конец Q  
        """
        inbinMG[(binY,kx)]
        """ */
        //System.out.println(' sort_BSF_x')//Tst
		
		ArrayDeque q = new ArrayDeque();
        
        ///q = queue.Queue()
        //System.out.println('self.npQX:',self.get_npQX()) //Tst
        //System.out.println('matrG:',matrG.get_matr()) //Tst
        int lenQX = this.len();
        ArrayList<Boolean> explored = new ArrayList<>(); //[ False for i in range(lenQX) ]
		for(int i=0; i<lenQX; i++) explored.add(i, false);
        BinMG inbinMG = new BinMG(lenQX); //#[ (-1,-1) for i in range(lenQX) ]
        //System.out.println('inbinMG:',inbinMG.get_list())  //Tst
        
        int ibin = 0;
        while (ibin < lenQX) {
            int istrt = ibin;
            explored.set(istrt,true);
            q.add(istrt);
            while (! q.isEmpty()){
                int iv = (Integer) q.poll();
                VertexSet y = matrG.set_binY(iv); //Tst
                //#System.out.println('y:',y.to_int(),y,type(y)) //Tst
                //inbinMG.set_row(ibin, ((matrG.set_binY(iv),iv)))
                inbinMG.set_row(ibin, new BinMGRow(y,iv)); //Tst
                //inbinMG.set_row(ibin, [y,iv]) //Tst
                ibin += 1;
				for(int iw=0; iw < (this.get_row(iv)).len(); iw++) {
                    if ((this.get_el(iv,iw) == 1) & (! explored.get(iw))) {
                        explored.set(iw,true);
                        q.add(iw);
					}
				}
			}
		}
        //#System.out.println('inbinMG:',inbinMG.get_list())  //Tst
        return inbinMG;
    }
}       
//--------
//--------
class BinMGRow{
	VertexSet vertex;
	Integer ind;
		
/* 	BinMGRow() {
		vertex = new String();
		ind = -1;
	} */
	BinMGRow(VertexSet vs, Integer ind) {
	//BinMGRow row = new BinMGRow();
			this.vertex = vs;
			this.ind = ind;
	}
	
	VertexSet get_vertex() {
		return this.vertex;
	}
	
	int get_ind() {
		return this.ind;
	}
	
	@Override
	public String toString(){
		return "[" + this.vertex.toString() + "," + this.ind + "]";
	}
}	
	
class BinMG extends ArrayList<BinMGRow> {
	ArrayList<BinMGRow> binMG;

    BinMG() {
        binMG = new ArrayList<>();
    }
	
    BinMG(int ln) {
		binMG = new ArrayList<>();
	//	BinMGRow.set_vertex("");
		for (int i=0; i<ln; i++) {
			BinMGRow row = new BinMGRow(new VertexSet(), -1);
//			row.vertex = new VertexSet();
//			row.ind = -1;
			binMG.add(row);
		}
	}
        
    int get_kxOld(int kx){
        return binMG.get(kx).get_ind();
	}
        
    VertexSet get_y(int kx){
        return binMG.get(kx).get_vertex();
    }
    
    void set_row(int kx, BinMGRow val){
        //#System.out.println('val:',val)
        this.binMG.set(kx, val);
    }
        
    BinMGRow get_row(int kx){
        return this.binMG.get(kx);
    }
        
    int len(){
        return binMG.size();
    }
        
    ArrayList<BinMGRow> get_list(){
        return this.binMG;
	}
      
//#---------- 
//# BinMG 
    Connection get_x_connection(int indx) {
/*         """
        binMG[(bitY,kx)]
        return (xbit, ybit)
        """ */
        VertexSet x = new VertexSet(indx);
        VertexSet y = this.get_y(indx);
        if (Constants.check_TST(new int[]{4})) System.out.printf(" indx,x,y current: %d %s %s%n", indx, x.set2BinStr(), y.set2BinStr());//#Constants.TST4; 
        return new Connection(x, y);
	}
//#---------- 
//# BinMG
    Limits get_sorted_prev_connections(int indx, QX npQX) {
/*         """
        It gets previous connected vertexes for current vertex 
        indx: int
        npQX[kx][kx]
        binMG[(binY,kx)]
        -
        prev_connections[](xbit, ybit)
        """ */
        if (Constants.check_TST(new int[]{4})) System.out.println(" get_sorted_prev_connections");//Tst
    //Tst     System.out.println(f'npQX[{indx}]',npQX[indx])
        Limits prev_connections = new Limits();
        
        for(int i=0; i<indx; i++){
            if (npQX.get_el(indx,i) == 1)
                prev_connections.append(this.get_x_connection(i));
		}
        
        if (Constants.check_TST(new int[]{4})) System.out.printf("indx,prev_connections %d %s%n",indx,prev_connections.out_list());//Tst
		
        return prev_connections;
	}
} 
//}
//--------
//--------

