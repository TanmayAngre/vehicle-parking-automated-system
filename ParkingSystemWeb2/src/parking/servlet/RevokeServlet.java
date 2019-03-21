package parking.servlet;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import parking.lot.*;
import parking.wheelers.*;
import parking.db.*;
import parking.fileio.*;
import parking.controller.*;
import parking.tickets.*;

/**
 * Servlet implementation class RevokeServlet
 */
@WebServlet("/RevokeServlet")
public class RevokeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RevokeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int flag = 0;
		double newCost = 0;
		try{
			JDBCCon jdbc = JDBCCon.getInstanceJDBC();
			Ticket ticket = jdbc.retrieveTicket(request.getParameter("ticketno"));
			if(ticket == null)
				System.out.println("tickt");
			else
				System.out.println("nymber:"+ticket.getTicketNo() + "cost" + ticket.getServiceCost());
			getServletContext().setAttribute("diff", ticket.getServiceCost());
			Slot sslot = jdbc.retrieveSlot(request.getParameter("ticketno"));   
			if(sslot == null)
				System.out.println("slot");
			
			/* ----------------------Generate new Cost ---------------------- */
			newCost = CostGenerator.generateCost(ticket, sslot);
			System.out.println("New Cost is:"+newCost);
			getServletContext().setAttribute("diff", newCost - (Double)getServletContext().getAttribute("diff"));
			jdbc.insertTicketHistory(ticket);
			System.out.println("1");
			//TicketHistoryFileWriter.writeFile(ticket, "C:\\Users\\TANMAY\\git\\glowing-journey1\\ParkingSystemWeb\\src\\tickethistory.csv");
			TicketHistoryFileWriter.writeFile(ticket, "E:\\fuzzy-doodle\\ParkingSystemWeb2\\src\\tickethistory.csv");
			System.out.println("1");
			/* -----------------Revoking the filled slot----------- */
			Slot filled = RevokeController.revokeFilledSlot((ParkingLot)getServletContext().getAttribute("pl"), sslot);
			System.out.println("DEBUG:" + filled.isAvailable());
			jdbc.updateSlot(sslot);
			System.out.println("1");
			/* Delete ticket entry after exit... Ticket Entry persists in TicketHistory */
			jdbc.deleteTicket(ticket);
			System.out.println("1");
		}
		catch(Exception e){
			System.out.println(e);
			System.out.println("flag:"+flag);
			flag = 1;
		}
		//response.setContentType("text/html");
		//PrintWriter out = response.getWriter();
		//out.print("<h3>Vehicle removed!!!</h3>");
		if(flag == 0){
			if((Double)getServletContext().getAttribute("diff")<0)
				getServletContext().setAttribute("message", "Vehicle removed successfully!!" + " New generated cost: Rs." + newCost + ", Refunded amount : Rs." + (Double)getServletContext().getAttribute("diff"));
			else if((Double)getServletContext().getAttribute("diff")>0)
				getServletContext().setAttribute("message", "Vehicle removed successfully!!" + " New generated cost: Rs." + newCost + ",Additional amount to be paid : Rs." + (Double)getServletContext().getAttribute("diff"));
		}
		else
			getServletContext().setAttribute("message", "Ticket number entered is invalid!");
		request.getRequestDispatcher("index.jsp").forward(request, response);
		//response.sendRedirect("index.jsp");
	}

}
