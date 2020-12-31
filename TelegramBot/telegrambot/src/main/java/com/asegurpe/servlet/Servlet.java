package com.asegurpe.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asegurpe.telegrambot.TelegramBot;

public class Servlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public Servlet() {
		TelegramBot.getInstance();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter pw = res.getWriter();

		pw.println("<html><body>");
		pw.println("Welcome to servlet");
		pw.println("</body></html>");

		pw.close();// closing the stream
	}
	
}