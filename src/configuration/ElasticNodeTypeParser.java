package configuration;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import experiment.elasticity.ElasticNodeType;

public class ElasticNodeTypeParser implements INodeTypeParser<ElasticNodeType> {

	public ElasticNodeType readNodeType(String filename) {
		ObjectMapper mapper = new ObjectMapper();
		ElasticNodeType nodeType = null;
		try {
			nodeType = mapper.readValue(new File(filename),
					ElasticNodeType.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nodeType;
	}

	@Override
	public boolean writeNodeType(ElasticNodeType nodeType, String fileName) {
		throw new RuntimeException("Not implemented");
	}

}
