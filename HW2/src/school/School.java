package school;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Path("/school")
public class School {
	
	private Connection conn;
	private Statement stmt;
	
	public School(){
		conn = null;
		stmt = null;
	}

	public void log(HttpServletRequest request) {
		String fileName = "rest.log";
		String str = "";
		str += "time " + new Date() + "\n";
		str += "getRemoteAddr() = " + request.getRemoteAddr() + "\n";
		str += "getRemoteHost() = " + request.getRemoteHost() + "\n";
		str += "getRemoteUser() = " + request.getRemoteUser() + "\n";
		str += "getProtocol() = " + request.getProtocol() + "\n";
		str += "getPathInfo() = " + request.getPathInfo() + "\n";
		str += "getMethod() = " + request.getMethod() + "\n";
		str += "getContentType()= " + request.getContentType() + "\n";
		str += "getContextPath()= " + request.getContextPath() + "\n";
		str += "getLocalAddr() = " + request.getLocalAddr() + "\n";
		str += "getLocalName() = " + request.getLocalName() + "\n";
		str += "getRequestURI() = " + request.getRequestURI() + "\n";
		str += "\n";
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(fileName, true)));
			out.println(str);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(str);
	}

	private void connect(String server, String db, String id, String pwd)
			throws Exception {
		// get db connection
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setServerName(server);
		dataSource.setDatabaseName(db);
		dataSource.setUser(id);
		dataSource.setPassword(pwd);
		conn = dataSource.getConnection();
		stmt = conn.createStatement();
	}

	private void close() throws Exception {
		stmt.close();
		conn.close();
	}
	
	
	@GET
	@Path("/signup")
	@Produces(MediaType.TEXT_PLAIN)
	public String signUp(@QueryParam("username") String username, @QueryParam("password") String password){
		String query = "INSERT INTO `ebbmf`.`user` (`id`, `name`, `password`, `total_followers`, `total_following`) VALUES (NULL, '" + username + "', '" + password + "', '0', '0');";
		String temp = "";
		try {
			connect("kc-sce-netrx9.umkc.edu", "ebbmf", "ebbmf", "ebbmf");
			signUpProcess(query);
			close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return temp;
	}
	
	
	private void signUpProcess(String query) throws Exception {
		stmt.executeUpdate(query);
	}
	
	// return one long table
	// http://localhost:8080/HW2/rest/school
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String selectAll(@Context HttpServletRequest request) {
		log(request);
		String query = "SELECT stu.id, stu.first_name, stu.last_name, stu.gpa, dept.id, dept.dept_name FROM `student` stu INNER JOIN `student_department` stu_dept ON stu_dept.Student_ID = stu.id INNER JOIN `department` dept ON dept.id = stu_dept.Department_ID ORDER BY `stu`.`ID` ASC";
		String temp = "";
		try {
			connect("kc-sce-netrx9.umkc.edu", "ebbmf", "ebbmf", "ebbmf");
			selectAllProcess(query);
			close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return temp;
	}

	
	private String selectAllProcess(String query) throws Exception {
		String temp = null;
		ResultSet rset = stmt.executeQuery(query);
		while (rset.next()) {
			temp += rset.getInt("id") + "\t" + rset.getString("first_name") + "\t\t" +
					rset.getString("last_name") + "\t\t" + rset.getDouble("gpa") + "\t" +
					rset.getInt("id") + "\t" + rset.getString("dept_name") + "\n";
			
		}
		return temp;
	}
	
	//return all students in a department who have greater than or equal a certain GPA
	//http://localhost:8080/HW2/rest/school/1/2
	@GET
	@Path("/{dept_id}/{gpa}")
	@Produces(MediaType.TEXT_PLAIN)
	public String selectDept(@PathParam("dept_id") int dept_id, @PathParam("gpa") double gpa) {
		String query = "SELECT dept.dept_name, stu.GPA, stu.first_name, stu.last_name FROM `department` dept INNER JOIN `student_department` stu_dept ON stu_dept.Department_ID = dept.id AND department_id = " + dept_id + " INNER JOIN `student` stu ON stu.id = stu_dept.Student_ID AND stu.GPA >=" + gpa + " ORDER BY stu.GPA DESC";
		String temp = "";
		try {
			connect("kc-sce-netrx9.umkc.edu", "ebbmf", "ebbmf", "ebbmf");
			temp = selectDeptProcess(query);
			close();
		} catch (Exception e) {
			System.out.println(e);
		}

		return temp;
	}
	
	// return all students in a department who have greater than or equal a certain GPA
	// http://localhost:8080/HW2/rest/school/dept_gpa?dept_id=1&gpa=2
	@GET
	@Path("/dept_gpa")
	@Produces(MediaType.TEXT_PLAIN)
	public String selectDept2(@QueryParam("dept_id") String dept_id, @QueryParam("gpa") double gpa){
		String query = "SELECT dept.dept_name, stu.GPA, stu.first_name, stu.last_name FROM `department` dept INNER JOIN `student_department` stu_dept ON stu_dept.Department_ID = dept.id AND department_id = " + dept_id + " INNER JOIN `student` stu ON stu.id = stu_dept.Student_ID AND stu.GPA >=" + gpa + " ORDER BY stu.GPA DESC";
		String temp = "";
		try {
			connect("kc-sce-netrx9.umkc.edu", "ebbmf", "ebbmf", "ebbmf");
			temp = selectDeptProcess(query);
			close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return temp;
	}
	
	private String selectDeptProcess(String query) throws Exception {
		String temp = null;
		ResultSet rset = stmt.executeQuery(query);
		while (rset.next()) {
			temp += rset.getString("dept_name") + " " + rset.getDouble("GPA") + " " + 
					rset.getString("first_name") + " " + rset.getString("last_name") + "\n";
		}
		return temp;
	}
}
