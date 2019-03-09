

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TicketServlet
 */
@WebServlet("/TicketServlet")
public class TicketServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketServlet() {
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
		/* ----------------Generate Ticket and Cost--------------------- */
		ParkingLot pl = (ParkingLot)getServletContext().getAttribute("pl");
		Slot slot = (Slot)getServletContext().getAttribute("slot");
		Vehicle v = (Vehicle)getServletContext().getAttribute("v");
		pl.getSlotAvailableList().remove(slot);
		pl.getSlotFilledList().add(slot);
		slot.setIsAvailable(false);
		//generate ticket
		Ticket tic = null;
		tic = (Ticket)getServletContext().getAttribute("ticket");
		System.out.print((Integer)getServletContext().getAttribute("back"));
		if((Integer)getServletContext().getAttribute("back")==0 || getServletContext().getAttribute("back") == null){
			tic = TicketGenerator.generateTicket(Integer.parseInt(request.getParameter("service")));
			getServletContext().setAttribute("back",1);
			getServletContext().setAttribute("ticket",tic);
			
		}
		else if((Integer)getServletContext().getAttribute("back")==1){
			tic.setEntryDate(LocalDate.now());
			tic.setEntryTime(LocalTime.now());
			int entryH = tic.getEntryTime().getHour();
			//System.out.println("Enter the service time (in hours):");
			//totalParkTime = getparkTime();
			int serviceDays = Integer.parseInt(request.getParameter("service"))/24;
			int plusServiceTime = Integer.parseInt(request.getParameter("service"))%24;
			tic.setExpiryDate(tic.getEntryDate().plusDays(serviceDays));
			tic.setExpiryTime(tic.getEntryTime().plusHours(plusServiceTime));
			int expiryH = tic.getExpiryTime().getHour();
			if(expiryH < entryH)
				tic.setExpiryDate(tic.getExpiryDate().plusDays(1));
		}
			
		
		//generate cost
		double cost = CostGenerator.generateCost(tic, slot);
		getServletContext().setAttribute("diff", tic.getServiceCost());
		
		getServletContext().setAttribute("cost",cost);
		System.out.println(cost);
		int flag = 0;
		// --------------------------- TODO add jdbc to servletcontext ------------------------
		//JDBCCon jdbc = (JDBCCon)getServletContext().getAttribute("jdbc");
		try{
			JDBCCon jdbc = JDBCCon.getInstanceJDBC();
			/* --------------------Database inserts--------------------- */
			/* --------------------File writes-------------------- */
			int u = jdbc.updateSlot(slot);
			System.out.println("DEBUG: " + u + " results updated");
			int x = jdbc.updateTicket(tic);
			TicketFileWriter.writeFile(tic, "E:\\EclipseWorkspace\\ParkingSystemWeb2\\src\\ticket.csv");
			System.out.println("DEBUG: " + x + " results updated in tickets");
			int y = jdbc.insertSlotHistory(tic, v, slot);
			SlotHistoryFileWriter.writeFile(tic, slot, v);
			System.out.println("DEBUG: " + y + " results inserted in slothistory");
			int z = jdbc.insertTicketHistory(tic);
			TicketHistoryFileWriter.writeFile(tic, "E:\\EclipseWorkspace\\ParkingSystemWeb2\\src\\tickethistory.csv");
			System.out.println("DEBUG: " + z + " results inserted in tickethistory");
		}
		catch(Exception e){
			System.out.println("Exception caught");
			e.printStackTrace();
			flag = 1;
		}
		//response.setContentType("text/html");
		//PrintWriter out = response.getWriter();
		//out.print("<h3>Vehicle inserted!!!</h3>");
		if(flag == 0)
			getServletContext().setAttribute("message", "Vehicle parked successfully!!   Alloted Ticket Number : "+tic.getTicketNo() + ",  Cost generated : "+tic.getServiceCost());
		else if(flag == 1)
			getServletContext().setAttribute("message", "An internal error occured. Please try again.");
		request.getRequestDispatcher("index.jsp").forward(request, response);
		//response.sendRedirect("index.jsp");
	}

}
