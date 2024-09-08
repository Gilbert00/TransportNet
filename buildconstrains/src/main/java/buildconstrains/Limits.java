package buildconstrains;

/**
 *
 * @author пользователь
 */
 //import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.Date;
//import java.text.SimpleDateFormat;
//import java.util.BitSet;
//import java.util.ArrayDeque;
//import java.util.Hashtable;
//import java.util.Map;
//import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.Comparator;
//import java.lang.Math;
//import java.lang.Comparable;
//import java.lang.invoke.MethodHandles;
//import java.lang.StringTemplate;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.math.BigInteger;
        
/* import os
import sys, getopt
import datetime #as dt
import queue
import csv
#import networkx as nx
#G = nx.Graph()
import numpy as np */

/* #fileName="graph0.csv"
#fileName="graph1.csv"
#fileName="graph11.csv"
#fileName="graph2.csv"
#fileName="graph3.csv"

#System.out.println(fileName) */
//--------

class Constants{
    //static int EMPTY_EL=-1;
    static int TST=0; //!!! [1,2,4,5]
	
	static boolean check_TST(int[] inds) {
		for(int i=0; i<inds.length; i++) {
			if (TST==inds[i]) return true;
		}
		return false;
	}
	
	static void set_TST(int val){
		TST = val;
	}
}


//--------
//--------
class Side extends ArrayList<String> {
    Side() {
        super();
    }

    //# def append(self, v):
    //    # self.super.append(v)
        
    public int len(){
         return super.size();
    }
                
    String get_vertex(int k){
        return super.get(k);
	}
}

class SideSortRow{
	String vertex;
	Integer ind;
		
	SideSortRow(String vs, Integer ind) {
	//BinMGRow row = new BinMGRow();
			this.vertex = vs;
			this.ind = ind;
	}
	
	String get_vertex() {
		return this.vertex;
	}
	
	int get_ind() {
		return this.ind;
	}
	
        @Override
	public String toString(){
		return "[" + this.vertex + "," + this.ind + "]";
	}
}

class SideSort extends ArrayList<SideSortRow> {
	ArrayList<SideSortRow> sideSort;

    SideSort() {
        this.sideSort = new ArrayList<SideSortRow>();
    }
	
/*     BinMG(int ln) {
		binMG = new ArrayList<>();
	//	BinMGRow.set_vertex("");
		for (int i=0; i<ln; i++) {
			BinMGRow row = new BinMGRow(new VertexSet(), -1);
//			row.vertex = new VertexSet();
//			row.ind = -1;
			binMG.add(row);
		}
	} */
        
    int get_kxOld(int kx){
        return this.get_row(kx).get_ind();
	}
        
    String get_v(int kx){
        return this.sideSort.get(kx).get_vertex();
    }
    
    void set_row(int kx, SideSortRow val){
        //#System.out.println('val:',val)
        this.sideSort.set(kx, val);
    }
        
    SideSortRow get_row(int kx){
        return this.sideSort.get(kx);
    }
        
    int len(){
        return this.sideSort.size();
    }
        
    ArrayList<SideSortRow> get_list(){
        return this.sideSort;
	}

    static SideSort sort_list_by_val(ArrayList<String> lst) {

		if (Constants.check_TST(new int[]{5})) System.out.println(" sort_list_by_val");
		if (Constants.check_TST(new int[]{5})) System.out.printf("lst: %s%n",lst.toString());
		
        //lst_out: [][lst_el, old_indx]
        SideSort lst_out = new SideSort();
        for(int i=0; i<lst.size(); i++)
            lst_out.sideSort.add(new SideSortRow(lst.get(i),i));
        
        if (Constants.check_TST(new int[]{5})) System.out.printf("lst_out-s: %s%n",lst_out.sideSort.toString());
        //lst_out.sort();
		Collections.sort(lst_out.sideSort, (s1,s2)->{return s1.vertex.compareTo(s2.vertex);} );
        
        if (Constants.check_TST(new int[]{5})) System.out.printf("lst_out-f: %s%n",lst_out.sideSort.toString());
        return lst_out;
	}
	
	String side_to_limit_side(String prefix, String bitStr) {
	/*         """
		prefix str
		bitStr
		sideSorted[[vertex, vertexIndxInLimit]]
		""" */
		
		if (Constants.check_TST(new int[]{5})) System.out.println(" side_to_limit_side");
		if (Constants.check_TST(new int[]{5})) System.out.printf("bitStr: %s%n", bitStr);
		if (Constants.check_TST(new int[]{5})) System.out.printf("len:%d%n",this.len());
   
        int n = bitStr.length();
		char[] listBit = bitStr.toCharArray();
                //ArrayList lc = new ArrayList<Char>.asList(listBit);
		//listBit.reverse();
        char[] lr = new char[n];
        for(int i=0; i<n; i++)
            lr[n-1-i] = listBit[i];
	//Tst3    System.out.println('listBit:',listBit)
	//Tst3    System.out.println('edge:',self.matr)
		//System.out.printf("n lr: %d %s%n", n,lr.toString());
		
		ArrayList<String> listS = new ArrayList<String>();
		int j;
		for(int i=0; i<n; i++)
			if (lr[i] == '1') {
				if (Constants.check_TST(new int[]{5})) System.out.printf("i: %d%n",i);
				j = this.get_lineIndx_by_oldInd(i);
				if (Constants.check_TST(new int[]{5})) System.out.printf("j: %d%n",j);
				listS.add(this.get_v(j));
            }
			
                //Arrays(listS.toArray()).sort();
                Collections.sort(listS, (s1,s2)->{return s1.compareTo(s2);} );
		//listS.sort();
	 //Tst3   System.out.println('listS:',listS) 
		
		String s = new String();
		int cnt = 0;
		for(int k=0; k<listS.size(); k++) {
			if (cnt > 0) s += " + ";   
			s += prefix + listS.get(k);
			cnt = cnt + 1;
                }
				
	 //Tst3   System.out.printf('s:%s%n',s);
		return s;
	}
	
	int get_lineIndx_by_oldInd(int e) {
		if (Constants.check_TST(new int[]{5})) System.out.println(" get_lineIndx_by_oldInd");
		if (Constants.check_TST(new int[]{5})) System.out.printf("len:%d%n",this.len());
		for(int k=0; k<this.len(); k++){
			if (Constants.check_TST(new int[]{5})) System.out.printf("e,k,ind: %d %d %d%n", e,k,this.get_kxOld(k));
			if (this.get_kxOld(k) == e) return k;
		}
	
		return -1;
    }	
}	
//--------
//--------

//--------
//--------
class Connection {
	VertexSet x;
	VertexSet y;
	
	Connection() {
		this.x = new VertexSet();
		this.y = new VertexSet();
	}
	
	Connection(VertexSet x, VertexSet y) {
		this.x = x;
		this.y = y;
	}
        
        VertexSet get_x() {
            return this.x;
        }
        
        VertexSet get_y() {
            return this.y;
        }
		
	public static Connection EMPTY_TURTLE(){
        return new Connection(VertexSet.EMPTY_SET(), VertexSet.EMPTY_SET());
    }
}
//#--------
//#--------
class VertexPairLong extends ArrayList<Long>{
	VertexPairLong() {
		super();
	}
	
	VertexPairLong(Long x, Long y) {
		new VertexPairLong();
		this.add(x);
		this.add(y);
	}
}	
    
class ListXY extends ArrayList<ArrayList<Long>> {
	ListXY() {
		super();
	}
	
}

class VertexPairStr extends ArrayList<String>{
	VertexPairStr() {
		super();
	}
	
	VertexPairStr(String x, String y) {
		new VertexPairStr();
		this.add(x);
		this.add(y);
	}
}	

class ListXYStr extends ArrayList<VertexPairStr> {
	ListXYStr() {
		super();
	}
}

//#--------
//#--------       
class Limits {
	ArrayList<Connection> limits;
	
    Limits() {
        this.limits = new ArrayList<Connection>();
	}
        
    boolean append(Connection limit) {
        return this.limits.add(limit);
	}
        
    boolean extend(Limits limits) {
        return this.limits.addAll(limits.get_list());
	}

    Connection delete(int krow){
        return this.limits.remove(krow);
	}
        
    Connection set_row(int krow, Connection val){
        return this.limits.set(krow, val);
    }
        
    Connection get_row(int krow){
        return this.limits.get(krow);
	}
     
    VertexSet get_x(int krow) {
        return this.limits.get(krow).x;
	}
        
    VertexSet get_y(int krow){  
        return this.limits.get(krow).y;
	}

    int len(){
        return this.limits.size();
	}
        
    ArrayList<Connection> get_list(){
        return this.limits;
	}
        
    ListXY out_list(){    
        ListXY list_xy = new ListXY();
        for(int i=0; i< this.len(); i++) {
			VertexPairLong pair = new VertexPairLong(this.get_x(i).to_int(), this.get_y(i).to_int());
            list_xy.add(pair);
		}
        return list_xy;
	}
        
    void clear_limits(){
        int lenLimits = this.len();
        for(int k=(lenLimits-1); k>-1; k--) {
         //   Connection row = this.get_row(k);
            VertexSet x = this.get_x(k);
            VertexSet y = this.get_x(k);
            if( x.eq(VertexSet.EMPTY_SET()) && y.eq(VertexSet.EMPTY_SET())) this.delete(k);
		}
	}
            
//#----------  
//# cls
     static Limits create_limits(QX npQX, BinMG binMG){
/*         """
        npQX[kx][kx]
        binMG[](bitY,kx)
        -
        listLimits[(xbit, ybit)]
        xPrevConnections[(xbit, ybit)]
        connection (xbit, ybit)
        xPrevLimitList[(xbit, ybit)]
        """ */
    //Tst     System.out.println(' create_limits')
		Connection connection;
		Limits xPrevConnections;
		Limits xPrevLimitList;
        Limits listLimits = new Limits();
        for(int indx=0; indx<npQX.len(); indx++){
            if (Constants.check_TST(new int[]{4})) System.out.printf("indQX: %d%n",indx);
			
            if (indx == 0) {
                connection = binMG.get_x_connection(indx);
                listLimits.append(connection);
                if (Constants.check_TST(new int[]{4})) listLimits.print_list_xy("after append listLimits");
				continue;
			}
            else{
                xPrevConnections = binMG.get_sorted_prev_connections(indx,npQX);
                if (xPrevConnections.len() > 0) 
                    xPrevLimitList = listLimits.get_prev_connected_limits(xPrevConnections);
                else //# First vertex in connected component
                    xPrevLimitList = new Limits();
                
                connection = binMG.get_x_connection(indx);
                listLimits.append(connection);
                if (Constants.check_TST(new int[]{4})) listLimits.print_list_xy("after append listLimits");
                
                if (xPrevLimitList.len() > 0) {
                    xPrevLimitList.add_xy_bits_to_prev(indx,binMG);
                    xPrevLimitList.check_dupl_y_in_prev();
                    listLimits.check_prev_y_in_limits(xPrevLimitList);
                    listLimits.extend(xPrevLimitList);
                    if (Constants.check_TST(new int[]{4})) listLimits.print_list_xy("after extend listLimits");
				}
			}
        }
	
        return listLimits;
	}
//#----------  
//# Limits
    Limits get_prev_connected_limits(Limits xPrevConnections){
/*         """
        for each prev connected vertex we get limits with it
        listLimits[](xbit, ybit)
        xPrevConnections[](xbit, ybit)
        -
        prev_limits[](xbit, ybit)
        """ */
        if (Constants.check_TST(new int[]{4})) System.out.println(" get_prev_connected_limits");//Tst
    //Tst     xPrevConnections.print_list_xy('xPrevConnections')
    //Tst     listLimits.print_list_xy('listLimits')
        
        Limits prev_limits = new Limits();
        for(int i=0; i<xPrevConnections.len(); i++){
            VertexSet xPrev = xPrevConnections.get_x(i);
    //Tst         System.out.println('i,xPrev:',i,xPrev)
            for(int k=0; k<this.len(); k++) {
                if (this.get_x(k).vand(xPrev).to_int() != 0) 
                    prev_limits.append(this.get_row(k));
			}
        }      
        if (Constants.check_TST(new int[]{4})) prev_limits.print_list_xy("prev_limits");//Tst
        return prev_limits;
    }
//#----------
//# Limits
    void add_xy_bits_to_prev(int indx,BinMG binMG){
/*        """
        adding current x & y to each previous limit
        binMG[(bitY,kx)]
        xPrevLimitList[(xbit, ybit)]
        """ */
        if (Constants.check_TST(new int[]{4})) System.out.println(" add_xy_bits_to_prev");//Tst
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("xPrevLimitList-s");//Tst
        Connection xy = binMG.get_x_connection(indx);
        VertexSet x = xy.get_x();
        VertexSet y = xy.get_y();    
        if (Constants.check_TST(new int[]{4})) System.out.printf("indx,x,y:",indx,x.set2BinStr(),y.set2BinStr());//Tst 
        
        for(int i=0; i<this.len(); i++) {
            Connection pair = new Connection(this.get_x(i).vor(x), this.get_y(i).vor(y));
            this.set_row(i, pair);
		}
        
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("xPrevLimitList-f");//Tst
    }
//#----------
//# Limits
    void check_dupl_y_in_prev(){
/*         """
        check and clear previous limits for duplicated y
        xPrevLimitList[(xbit, ybit)]
        """ */
        if (Constants.check_TST(new int[]{4})) System.out.println(" check_dupl_y_in_prev");//Tst
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("xPrevLimitList-s");//Tst
        int lenP = this.len();
        
        for(int kp=(lenP-1); kp>-1; kp--) {
            VertexSet yp = this.get_y(kp);
            if (yp.eq(VertexSet.EMPTY_SET())) continue;
            for(int kl=(kp-1); kl>-1; kl--){
                if (this.get_y(kl).eq(yp)) {
                    this.set_row(kp, new Connection(this.get_x(kp).vor(this.get_x(kl)), yp) ) ;
                    this.set_row(kl, Connection.EMPTY_TURTLE()) ;
				}
			}
		}
		
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("xPrevLimitList-e");//Tst
        
    //#   clear previous limits
    //#   clear list 
        this.clear_limits();
                
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("xPrevLimitList-f");//Tst
	}
//#----------
//# Limits
    void check_prev_y_in_limits(Limits xPrevLimitList) {
/*         """
        check and clear common limits for duplicated y by previous limits
        listLimits[(xbit, ybit)]
        xPrevLimitList[(xbit, ybit)]
        """ */
        if (Constants.check_TST(new int[]{4})) System.out.println(" check_prev_y_in_limits");//Tst
        if (Constants.check_TST(new int[]{4})) xPrevLimitList.print_list_xy("xPrevLimitList");//Tst
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("listLimits-s");//Tst+
        int lenP = xPrevLimitList.len();
        int lenL = this.len();
        
        for(int kp=0; kp<lenP; kp++){
            VertexSet yp = xPrevLimitList.get_y(kp);
            for(int kl=0; kl<lenL; kl++)
                if (this.get_y(kl).eq(yp)) {
                    xPrevLimitList.set_row(kp, new Connection(xPrevLimitList.get_x(kp).vor(this.get_x(kl)), yp) ); 
                    this.set_row(kl, Connection.EMPTY_TURTLE());
				}
		}
                    
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("listLimits-e");//Tst            

    //#   clear limits 
    //#   clear list  
        this.clear_limits();
            
        if (Constants.check_TST(new int[]{4})) this.print_list_xy("listLimits-f");  //Tst  
    }
//#----------
//# Limits 
   static Limits build_net_limits(Graph graph) {
    //    """
    //    graph: 
    //    """
    //Tst    System.out.println('dual:',self.dual)

        graph.gen_sides();
        int nX = graph.lX.size();
        int nY = graph.lY.size();
        if(Constants.check_TST(new int[]{1})) System.out.println("lX: "+nX+" "+graph.lX);
        if(Constants.check_TST(new int[]{1})) System.out.println("lY: "+nY+" "+graph.lY);

        MatrG matrG = graph.set_matrG();

        if (Constants.check_TST(new int[]{1})) System.out.printf("matrG: %s%n",matrG.get_matr());
        QX npQX = new QX(matrG);
        //#System.out.println('npQX:',npQX.npQX)   //Tst
        if (Constants.check_TST(new int[]{1,4})) System.out.printf("npQX: %s%n",npQX.get_npQX());
        ParamsSortMatrG params = matrG.sort_matrG_by_x(graph.lX, graph.lY, npQX);
        matrG = params.matrG;
        graph.lX = params.lX;
        BinMG binMG = params.binMG;
        if (Constants.check_TST(new int[]{1,4})) System.out.println(" after sort_matrG_by_x");//Tst1
        if (Constants.check_TST(new int[]{1,4})) System.out.printf("matrG: %s%n",matrG.get_matr());//Tst1
        if (Constants.check_TST(new int[]{1,4})) System.out.printf("lX: %d %s%n",nX,graph.lX);//Tst1
        if (Constants.check_TST(new int[]{1,4})) System.out.printf("binMG:  %s%n",binMG.get_list().toString());//Tst1

        npQX = new QX(matrG);
        if (Constants.check_TST(new int[]{1,4})) System.out.printf("npQX: %s%n",npQX.get_npQX());//Tst

        Limits listR = Limits.create_limits(npQX, binMG);
        listR.print_list_xy("listR");
		
		return listR;
        //listR.print_limits(graph);
}   
//#----------
//# Limits    
    void print_limits(Graph graph){
/*         """
        print limit in symbolic view
        listR[(xbit, ybit)]
        lX[x]
        lY[y]
        dual: logical
        -
        lXsort[[val, oldInd]]
        lYsort[[val, oldInd]]
        """ */
            
        SideSort lXsort = SideSort.sort_list_by_val(graph.lX);    
        SideSort lYsort = SideSort.sort_list_by_val(graph.lY);
		if (Constants.check_TST(new int[]{5})) System.out.printf("lXsort: %s%n", lXsort.get_list().toString());
		if (Constants.check_TST(new int[]{5})) System.out.printf("lYsort: %s%n", lYsort.get_list().toString());
        
        for(int iR=0; iR<this.len(); iR++){
            String sl = new String();
			String sr = new String();

            String xa = this.get_x(iR).set2BinStr();     //#
            String yb = this.get_y(iR).set2BinStr();     //#
 //#!!!           xa,yb = set2BinStr(self.get_row(iR))
    		if (Constants.check_TST(new int[]{5})) System.out.printf("xa,yb: %s %s%n", xa,yb);
	 
            if (graph.get_dual()) {
                sl = lXsort.side_to_limit_side("b",xa);
                sr = lYsort.side_to_limit_side("a",yb);
			}
            else {
                sl = lXsort.side_to_limit_side("a",xa);
                sr = lYsort.side_to_limit_side("b",yb);
			}
            
            System.out.printf("%s%s%s%n", sl," <= ",sr);
		}
	}

//#----------
//# Limits  
    void print_list_xy(String prefix){
/*         """
        prefix: str
        list_xy[](xbin, ybin)
        """ */
        ListXY list_xy = new ListXY();
        ListXYStr lp = new ListXYStr();
        for(int i=0; i<this.len(); i++) {
            list_xy.add( new VertexPairLong( this.get_x(i).to_int(), this.get_y(i).to_int() )  );
            lp.add( new VertexPairStr( this.get_x(i).set2BinStr(), this.get_y(i).set2BinStr() ) );
        }
		
        System.out.printf(prefix+": %s%n", list_xy);
        System.out.printf("%s %s%n", " ".repeat(prefix.length()), lp);
    }
}        
//-------------------
//-------------------
