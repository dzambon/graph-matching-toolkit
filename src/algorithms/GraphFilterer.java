package algorithms;

import java.io.FileInputStream;

import java.util.Iterator;
import java.util.Properties;

import util.Graph;
import util.GraphSet;
import xml.XMLParser;

public class GraphFilterer {

	private GraphSet source;

	public GraphFilterer(String prop) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(prop));
		System.out.println("Load the source and target graph sets...");
		XMLParser xmlParser = new XMLParser();
		xmlParser.setGraphPath(properties.getProperty("path"));
		String sourceString = properties.getProperty("source");
		this.source = xmlParser.parseCXL(sourceString);
		
		Iterator<Graph> iter = this.source.iterator();
		int MAX_NUMBER = 150;
		int counter = 0;
		while (iter.hasNext()){
			Graph g = iter.next();
			if (counter % 2 == 0){
				System.out.println("  <print file=\""+g.getFileName()+"\"/>");			
			}
			counter++;
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GraphFilterer gf = new GraphFilterer("/Users/riesen/Documents/GraphMatchingProject/PropertiesFiles/GREC_Exact.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
