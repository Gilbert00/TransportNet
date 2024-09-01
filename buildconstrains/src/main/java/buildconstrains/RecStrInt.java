package buildconstrains;

import java.util.ArrayList;
import java.lang.Comparable;

/**
 *
 * @author пользователь
 */
public class RecStrInt implements Comparable<RecStrInt> {
	String vertex;
	int oldInd;
	
	RecStrInt(String vertex){
		this.vertex = vertex;
		this.oldInd =0;
	}
	
    @Override 
	public int compareTo(RecStrInt other)
    {
        return this.vertex.compareTo(other.vertex);
    }	
	
}

class SideSorted{
	ArrayList<RecStrInt> side;
	
	SideSorted() {
		this.side = new ArrayList<>();
	}
	
}	
//--------
//--------