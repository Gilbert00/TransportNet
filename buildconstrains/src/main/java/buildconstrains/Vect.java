package buildconstrains;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author пользователь
 */
public class Vect<T extends Integer> extends ArrayList<T> implements Comparable<Vect<T>> {
//	ArrayList<T> vector;
	
/* 	Vect<T>() {
		 
	//    this.vector = new ArrayList<>();
        //   this = new ArrayList<T>();
            new ArrayList<>();
                
	} */
	
	int len(){
		return this.size();
	}
	
	@Override
	public int compareTo(Vect<T> vect2) {
		return this.get(0).compareTo(vect2.get(0));
	}

}
//--------
//--------
class Matr<T extends Integer> extends ArrayList<Vect<T>> {
    ArrayList<Vect<T>> matr;

    Matr() {
        matr = new ArrayList<Vect<T>>();
    }

	Matr(int nX, int nY) {
		matr = new Matr();
//		Integer c = (Integer) 0;
		for(int i=0; i<nX; i++) {
			Vect<T> vect = new Vect<T>();
			for(int j=0; j<nY; j++) {
				vect.add(j, (T) (Integer) 0);
			}
			matr.add(i, vect); 
		}
	}

    public int len() {
        return matr.size();
    }
	
	int len2() {
		Vect<T> vect = get_row(0);
		return vect.size();
	}

    public void append(Vect<T> v) {
        matr.add(v);
    }
    
	Vect<T> get_row(int ind) {
		return matr.get(ind);
	
	}

	T get_el(int ind0, int ind1) {
		return matr.get(ind0).get(ind1);
	}	
	
	void set_row(int ind, Vect<T> row) {
		matr.set(ind, row);
	
	}	
	
	void set_el(int ind0, int ind1, T el) {
		Vect<T> row = matr.get(ind0);
		row.set(ind1, el);
		matr.set(ind0, row);
	}	

    public void sort() {
		Collections.sort(this.matr);
        //this.matr.sort();
    }

    public int get_lineIndx_by_val_in_col(int col, T e) {
        for(int k=0; k<this.len(); k++)
            if (this.matr.get(k).get(col)==e) return k;
        
        return -1;
    }
 
    public boolean check_list(ArrayList<T> l, T e){
//       System.out.println('list',l)
//       System.out.println('e',e)
        int n=l.size();
        if (n==0) return false;
        for(T k: l)
            if (k==e) return true;
        
        return false;
    }

    public static boolean check_list(ArrayList<String> l, String e){
//       System.out.println('list',l)
//       System.out.println('e',e)
        int n=l.size();
        if (n==0) return false;
        for(String k: l)
            if (k==e) return true;
        
        return false;
    }
}      