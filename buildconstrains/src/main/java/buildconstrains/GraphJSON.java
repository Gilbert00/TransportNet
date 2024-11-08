package buildconstrains;

/**
 *
 * @author пользователь
 */

import com.google.gson.Gson;
import java.io.IOException;
//import com.google.gson.reflect.TypeToken;
//import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


class SideJSON {
	ArrayList<String>  node;
	ArrayList<Double>  value;
	
	SideJSON() {}
	
	SideJSON(ArrayList<String> node, ArrayList<Double>  value) {
		this.node = new ArrayList<>();
		this.value = new ArrayList<>();
		
		for(int i=0; i<node.size(); i++) {
			this.node.add(node.get(i));
		}
		
		for(int i=0; i<value.size(); i++) {
			this.value.add(value.get(i));
		}
	}

	ArrayList<String> get_node() {
		return this.node;
	}

	ArrayList<Double> get_value() {
		return this.value;
	}
	
	@Override
	public String toString() {
		ArrayList<String> nodeS = new ArrayList<>();
		for(int i=0; i < node.size(); i++) nodeS.add("\""+node.get(i)+"\"");
		return "{\"node\":" +nodeS.toString()+ ",\"value\":" +value.toString()+ "}";
	}
}

class GraphJSON {
	SideJSON X;
	SideJSON Y;
	
	GraphJSON() {}
	
	GraphJSON(SideJSON X, SideJSON Y) {
		this.X = new SideJSON(X.get_node(), X.get_value());
		this.Y = new SideJSON(Y.get_node(), Y.get_value());
	}
	
    @Override
	public String toString() {
		return "{\"X\":" +X+ ",\"Y\":" +Y+ "}";
	}
}


class GraphManageJSON {
	void main1() throws IOException {
/* 		String resourceJSON = """
            {"X":{"node":["1","2","3"], 
                  "value":[1.0,2.0,3.0]
                 }, 
             "Y":{"node":["1","2","3","4"], 
                  "value":[1,2,3,4]
                 }
            }
        """; */
/* 		String resourceJSON = """
{"X":{"node":["1","2","3"],"value":[1.0,2.0,3.0]},"Y":{"node":["1","2","3","4"],"value":[1.0,2.0,3.0,4.0]}}		
"""; */
		String resourceJSON = """
{"X":{"node":["1", "2", "3"],"value":[1.0, 2.0, 3.0]},"Y":{"node":["1", "2", "3", "4"],"value":[1.0, 2.0, 3.0, 4.0]}}
""";

        ArrayList<String>  node;
        ArrayList<Double>  value;
        String[] sarr1 = {"1","2","3"};
        node = new ArrayList<>( Arrays.asList(sarr1));
        Double[] farr1 = {1.0,2.0,3.0};
        value = new ArrayList<>(Arrays.asList(farr1));
        SideJSON gX = new SideJSON(node, value);
        
        String[] sarr2 = {"1","2","3","4"};
        Double[] farr2 = {1.0,2.0,3.0,4.0};
        node = new ArrayList<>( Arrays.asList(sarr2));
        value = new ArrayList<>(Arrays.asList(farr2));
		SideJSON gY = new SideJSON(node, value);
		GraphJSON g = new GraphJSON (gX,gY);
	
		Gson gson = new Gson();
		String json = gson.toJson(g);
		
		//String json = gson.toJson(resourceJSON);
		System.out.println(json);
//{"X":{"node":["1","2","3"],"value":[1.0,2.0,3.0]},"Y":{"node":["1","2","3","4"],"value":[1.0,2.0,3.0,4.0]}}

		//GraphJSON gFrom = gson.fromJson(resourceJSON, GraphJSON.class);
/* 		String result = new String(
			Files.readAllBytes(Paths.get("rsrc00.json")), 
			StandardCharsets.UTF_8
		); */
		
		String result = Files.readString(Paths.get("rsrc00.json"), StandardCharsets.UTF_8);
		
		GraphJSON gFrom = gson.fromJson(result, GraphJSON.class);
		System.out.println(gFrom);	
//{"X":{"node":["1", "2", "3"],"value":[1.0, 2.0, 3.0]},"Y":{"node":["1", "2", "3", "4"],"value":[1.0, 2.0, 3.0, 4.0]}}		
	}
	
	public static void main(String[] args) throws Exception {
	
        GraphManageJSON cls = new GraphManageJSON();
		cls.main1();
		
	}
}

