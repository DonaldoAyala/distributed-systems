package WebShop;

import com.google.gson.*;

public class Mensaje
{
	String message;

	Mensaje(String message)
	{
		this.message = message;
	}
	public static Mensaje valueOf(String s) throws Exception {
		Gson j = new GsonBuilder().registerTypeAdapter(byte[].class,new GsonBase64Adapter()).create();
		return (Mensaje)j.fromJson(s,Mensaje.class);
  	}
}
