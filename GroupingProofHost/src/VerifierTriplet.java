import org.json.JSONObject;

public class VerifierTriplet{
		public byte m;
		public byte m2;
		public byte c;
		public byte rand;
		public void parseJSON(String json) {
			try {
				JSONObject jObject = new JSONObject(json);

				JSONObject productObject = jObject.getJSONObject("group");

				m = (byte) Integer.parseInt(productObject.getString("m"));
				m2 = (byte) Integer.parseInt(productObject.getString("m2"));
				c = (byte) Integer.parseInt(productObject.getString("c"));
				rand = (byte) Integer.parseInt(productObject.getString("rand"));
			} catch (Exception e) {

			}
		} 
		public VerifierTriplet()
		{
			
		}
	}
