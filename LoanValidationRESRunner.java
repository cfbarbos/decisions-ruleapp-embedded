package com.ibm.decisions.loanvalidation;


import java.util.Map;

import ilog.rules.res.model.IlrPath;
import ilog.rules.res.session.IlrJ2SESessionFactory;
import ilog.rules.res.session.IlrPOJOSessionFactory;
import ilog.rules.res.session.IlrSessionFactory;
import ilog.rules.res.session.IlrSessionRequest;
import ilog.rules.res.session.IlrSessionResponse;
import ilog.rules.res.session.IlrStatelessSession;
import ilog.rules.res.session.config.IlrDatasourcePersistenceConfig;
import ilog.rules.res.session.config.IlrJDBCPersistenceConfig;
import ilog.rules.res.session.config.IlrPersistenceConfig;
import ilog.rules.res.session.config.IlrPersistenceType;
import ilog.rules.res.session.config.IlrSessionFactoryConfig;
import ilog.rules.res.session.config.IlrXUConfig;
import ilog.rules.res.session.impl.IlrSessionRequestImpl;
import miniloan.Borrower;
import miniloan.Loan;

public class LoanValidationRESRunner {
	
	

	/**
	 * @return
	 */
	private static IlrJ2SESessionFactory GetRuleSessionFactory() {
		
		IlrSessionFactoryConfig factoryConfig = IlrJ2SESessionFactory.createDefaultConfig();
	
		IlrXUConfig xuConfig = factoryConfig.getXUConfig();
		xuConfig.setLogAutoFlushEnabled(false);
		xuConfig.getPersistenceConfig().setPersistenceType(IlrPersistenceType.MEMORY);
		xuConfig.getManagedXOMPersistenceConfig().setPersistenceType(IlrPersistenceType.MEMORY);
		return GetRuleSessionFactoryJDBC2();
		//return new IlrJ2SESessionFactory(factoryConfig);
	}
	
	private static IlrJ2SESessionFactory GetRuleSessionFactoryJDBC2() {

		IlrSessionFactoryConfig factoryConfig = IlrJ2SESessionFactory.createDefaultConfig();
		//IlrPersistenceConfig persistenceConfig = factoryConfig.getXUConfig().getPersistenceConfig();
		IlrXUConfig xuConfig = factoryConfig.getXUConfig();
		xuConfig.setLogAutoFlushEnabled(false);
		xuConfig.getPersistenceConfig().setPersistenceType(IlrPersistenceType.JDBC);
		xuConfig.getManagedXOMPersistenceConfig().setPersistenceType(IlrPersistenceType.JDBC);
		
		IlrJDBCPersistenceConfig jdbcPersistenceConfig = xuConfig.getPersistenceConfig().getJDBCPersistenceConfig();
		//jdbcPersistenceConfig.setDriverClassName("com.ibm.db2.jcc.DB2Driver")
		jdbcPersistenceConfig.setDriverClassName("org.h2.Driver");
		jdbcPersistenceConfig.setPassword("res");
		//jdbcPersistenceConfig.setURL( "jdbc:db2://HOST[:PORT]/DB");
		//jdbcPersistenceConfig.setURL( "jdbc:h2:C:\\IBM\\wlp-21.0.0.9\\usr\\servers\\odm81101\\data\\h2\\resdb");
		jdbcPersistenceConfig.setURL( "jdbc:h2:C:\\Users\\Administrator\\Documents\\dbdata\\dbdata\\resdb");
		jdbcPersistenceConfig.setUser("res");
		return new IlrJ2SESessionFactory(factoryConfig);

		}

	private static IlrJ2SESessionFactory GetRuleSessionFactoryJDBC() {

		IlrSessionFactoryConfig factoryConfig = IlrJ2SESessionFactory.createDefaultConfig();
		IlrPersistenceConfig persistenceConfig = factoryConfig.getXUConfig().getPersistenceConfig();
		IlrPersistenceType persistenceType = IlrPersistenceType.JDBC;
		persistenceConfig.setPersistenceType(persistenceType);
		IlrJDBCPersistenceConfig jdbcPersistenceConfig = factoryConfig.getXUConfig().getPersistenceConfig().getJDBCPersistenceConfig();
		//jdbcPersistenceConfig.setDriverClassName("com.ibm.db2.jcc.DB2Driver")
		jdbcPersistenceConfig.setDriverClassName("org.h2.Driver");
		jdbcPersistenceConfig.setPassword("res");
		//jdbcPersistenceConfig.setURL( "jdbc:db2://HOST[:PORT]/DB");
		//jdbcPersistenceConfig.setURL( "jdbc:h2:C:\\IBM\\wlp-21.0.0.9\\usr\\servers\\odm81101\\data\\h2\\resdb");
		jdbcPersistenceConfig.setURL( "jdbc:h2:C:\\Users\\Administrator\\Documents\\dbdata\\dbdata\\resdb");
		jdbcPersistenceConfig.setUser("res");
		return new IlrJ2SESessionFactory(factoryConfig);

		}

	
	
	
	public static void main(String[] args) {
		LoanValidationRESRunner runner = new LoanValidationRESRunner();
		
		
		Borrower borrower = new Borrower("Smith", 10, 2);
		
		Loan loan = new Loan( 48, 20000, 0.05);
		
		IlrSessionResponse response = runner.execute(borrower, loan);
		Map<String, Object> outParameters = response.getOutputParameters();
		Loan loan2 = (Loan) outParameters.get("loan");
		
	    System.out.println(loan2.getApprovalStatus());
	    
	}
	
	
	/*
	 * public LoanValidationDecision execute(LoanValidationRequest request) {
	 * 
	 * LoanValidationResponse response = null;
	 * 
	 * try {
	 * 
	 * IlrSessionResponse sessionResponse = execute(request.borrower,
	 * request.loanRequest); //long t3 = System.currentTimeMillis();
	 * LoanValidationResponse loanValidationResponse = new
	 * LoanValidationResponse(sessionResponse); response = loanValidationResponse;
	 * 
	 * } catch (Exception exception) { exception.printStackTrace(System.err); }
	 * 
	 * double yearlyRepayment = response.getReport().getMonthlyRepayment();
	 * 
	 * System.out.print("Loan approved=" + response.report.isApproved() +
	 * " with a yearly repayment=" + yearlyRepayment + " insurance required:" +
	 * response.getReport().isInsuranceRequired() + " messages= " +
	 * response.getReport().getMessages());
	 * System.out.println(" executed in thread " +
	 * Thread.currentThread().getName());
	 * 
	 * return new LoanValidationDecision(request, response); }
	 */
	
	
	public IlrSessionResponse execute(Borrower borrower, Loan loan) {
		try {

			IlrJ2SESessionFactory sessionFactory =  GetRuleSessionFactoryJDBC2();

			// Creating the decision request
			IlrSessionRequest sessionRequest = sessionFactory.createRequest();
			String rulesetPath = "/tutorial_ruleapp/miniloan_ruleset";
			sessionRequest.setRulesetPath(IlrPath.parsePath(rulesetPath));
			
			sessionRequest.setTraceEnabled(true);
			sessionRequest.getTraceFilter().setInfoAllFilters(true);
			sessionRequest.getTraceFilter().setInfoRules(true);
			sessionRequest.getTraceFilter().setInfoRulesNotFired(true);
			sessionRequest.getTraceFilter().setInfoTasks(true);
			sessionRequest.getTraceFilter().setInfoTotalTasksNotExecuted(true);
			sessionRequest.getTraceFilter().setInfoExecutionEvents(true);

			Map<String, Object> inputParameters = sessionRequest
					.getInputParameters();
			inputParameters.put("loan", loan);
			inputParameters.put("borrower", borrower);

			// Creating the rule session
			IlrStatelessSession session = sessionFactory
					.createStatelessSession();
		
			IlrSessionResponse response = session.execute(sessionRequest);
			return response;

		} catch (Exception exception) {
			exception.printStackTrace(System.err);
		}
		return null;
	}

}
