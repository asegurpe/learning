package com.asegurpe.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.asegurpe.websocket.ChatClient;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/")
public class RestService {
		
	@GET
	@Path("/service1")
	@Produces("application/json")
	public Response service1() {
 
		String jsonString = new JSONObject()
                .put("JSON1", "Hello World!")
                .put("JSON2", "Hello my World!")
                .put("JSON3", new JSONObject().put("key1", "value1"))
                .toString();
		
		ChatClient.broadcast("M'han cridat a Service 1");
		return Response.status(200).entity(jsonString).build();
	}
	
	@POST
	@Path("/service2")
	@Produces("application/json")
	public Response service2() {
 
		String jsonString = new JSONObject()
                .put("JSON4", "Hello World!")
                .put("JSON5", "Hello my World!")
                .put("JSON6x", new JSONObject().put("key1", "value1"))
                .toString();
		
		ChatClient.broadcast("M'han cridat a Service 2");
		return Response.status(200).entity(jsonString).build();
	}
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
	        @FormDataParam("file") InputStream uploadedInputStream,
	        @FormDataParam("file") FormDataContentDisposition fileDetail,
	        @FormDataParam("path") String path) {


	    // Path format //10.217.14.97/Installables/uploaded/
	    System.out.println("path::"+path);
	    String uploadedFileLocation = path
	            + fileDetail.getFileName();

	    // save it
	    writeToFile(uploadedInputStream, uploadedFileLocation);

	    String output = "File uploaded to : " + uploadedFileLocation;

	    return Response.status(200).entity(output).build();

	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

	    try (OutputStream out = new FileOutputStream(new File(uploadedFileLocation))) {
	        int read = 0;
	        byte[] bytes = new byte[1024];

	        while ((read = uploadedInputStream.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        out.flush();
	        out.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
