package com.SCM.pra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class AccessEmployee {

	public static void main(String[] args) {
		
		
		
//		ArrayList<Employee> employees = new ArrayList<>();
//		employees.add(new Employee(1,"John",23000));
//		employees.add(new Employee(2,"Doug",190000));
//		employees.add(new Employee(3,"Jack",200000));
//		employees.add(new Employee(4,"Daisy",240000));
//		employees.add(new Employee(5,"William",220000));
//		}
		
		List<Employee> employess = Arrays.asList(
				(new Employee(1,"John",23000)),
				(new Employee(2,"Doug",190000)),
				(new Employee(3,"Jack",200000)),
				(new Employee(4,"Daisy",240000)),
				(new Employee(5,"William",220000))
				
		);
		
		List<Employee> startWithJ = new ArrayList<>();
		List<Employee> maxSalary = new ArrayList<>();
		
		for(Employee emp : employess) {
			if(emp.getEmpName().startsWith("J")) {
				startWithJ.add(emp);
			}
		}
		
		System.out.println(startWithJ);
		
		Employee maxSal = employess.get(0);
		
		int max = (int) maxSal.getEmpSalary();
		
		for(Employee emp : employess) {
			if(emp.getEmpSalary() > max) {
				maxSalary.add(emp);
			}
		}
		
		
		
		System.out.println(">>>>>>>>>> : " + maxSalary);
	}
	
	
	
}
	
