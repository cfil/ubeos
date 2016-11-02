package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import play.data.binding.Global;
import play.data.binding.TypeBinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Global
public class JsonObjectBinder implements TypeBinder<JsonObject>{
	@Override
	public Object bind(String name, Annotation[] annotations, String value,
			Class actualClass, Type genericType) throws Exception {
		return new JsonParser().parse(value);
	}
	
	public static String exposedJson(Object object) {
		Gson builder =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		return builder.toJson(object);
	}
}
