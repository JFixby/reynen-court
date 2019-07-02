
package com.jfixby.reynencourt.sns.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfixby.reynencourt.demo.api.DataSampleStorage;
import com.jfixby.reynencourt.sns.demo.Deployment;
import com.jfixby.scarabei.api.err.Err;

/** @author JCode */
public class EntryPoint1 extends HttpServlet {

	private static DataSampleStorage storage;

	static {
		try {
			storage = Deployment.deploy();
		} catch (final IOException e) {
			Err.reportError(e);
		}
	}

	/** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs */
	protected void processRequest (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet EntryPoint1</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet EntryPoint1 at " + request.getContextPath() + "</h1>");
			out.println("<h1>" + new Date() + "</h1>");
			out.println("</body>");
			out.println("</html>");
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/** Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs */
	@Override
	protected void doGet (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		this.processRequest(request, response);
	}

	/** Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs */
	@Override
	protected void doPost (final HttpServletRequest request, final HttpServletResponse response)
		throws ServletException, IOException {
		this.processRequest(request, response);
	}

	/** Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description */
	@Override
	public String getServletInfo () {
		return "Short description";
	}// </editor-fold>

}
